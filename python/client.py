import rdflib
import requests
from rdflib import Graph, URIRef, DCTERMS
import urllib3
import yaml
from pathlib import Path
urllib3.disable_warnings()

# Function to fetch and parse RDF file from URL
def parse_rdf_from_url(url):
    # Fetch the RDF content from the URL
    response = requests.get(url, verify=False, headers={"Accept":"application/ld+json"})
    
    if response.status_code != 200:
        print(f"Error: Unable to fetch the RDF file from {url}. HTTP Status code: {response.status_code}")
        return None
    
    # Create an RDF graph to store the parsed RDF data
    graph = Graph()
    
    # Parse the RDF content
    graph.parse(data=response.text, format='json-ld')  # You can adjust format (e.g., 'xml', 'ttl', etc.)
    
    return graph

def parse_json_from_url(url):
    response = requests.get(url, verify=False)

# Check if the request was successful (status code 200)
    if response.status_code == 200:
        # Parse the JSON response
        json_data = response.json()

        # Now json_data is a Python dictionary
        #print(json_data)
        return json_data

    # You can access specific data in the JSON like this:
    # For example, if the JSON data contains a key "name":
    else:
        print(f"Request failed with status code {response.status_code}")
        return None

# Load YAML config
# Get the path to config.yaml one directory up
config_path = Path(__file__).resolve().parent.parent / "config.yaml"
with open(config_path, "r") as f:
    config = yaml.safe_load(f)

# Extract parts from config
base_url = config["api"]["endpoint"]
method = config["api"]["method"]
metadata_suffix = config["api"]["metadata"]
models = config["models"]

# Example usage:
for model_name in models:
    url = f"{base_url}{method}{model_name}{metadata_suffix}"
    rdf_graph = parse_rdf_from_url(url)

    if rdf_graph:
        turtle_data = rdf_graph.serialize(format='turtle')

        # Save to a file if needed
        #filename = url.split("/")[-1].replace(".jsonld", ".ttl")
        filename = "output-" + model_name + ".ttl"
        with open(filename, "wb") as f:
            f.write(turtle_data.encode())

        sparqlresult = rdf_graph.query("""
            SELECT (COUNT(*) as ?Triples) 
            WHERE { ?s ?p ?o } 
            """)
        for row in sparqlresult:
            print("Found " + row.Triples + " triples in " + filename)
        #json = parse_json_from_url(url)
        #print("Title from JSON: " + json['title'])
