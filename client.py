import rdflib
import requests
from rdflib import Graph, URIRef, DCTERMS
import urllib3
urllib3.disable_warnings()

# Function to fetch and parse RDF file from URL
def parse_rdf_from_url(url):
    # Fetch the RDF content from the URL
    response = requests.get(url, verify=False)
    
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

# Example usage:
url1 = "https://semiceu.github.io/EOSC-MLDCAT-AP-Pilot/example/thermal-bridges-rooftops-detector.jsonld"  # Replace with your RDF file URL
url2 = "https://semiceu.github.io/EOSC-MLDCAT-AP-Pilot/example/zooprocess-multiple-classifier.jsonld"
url3 = "https://semiceu.github.io/EOSC-MLDCAT-AP-Pilot/example/phyto-plankton-classification.jsonld"
models = [url1, url2, url3]

for url in models:
    rdf_graph = parse_rdf_from_url(url)

    # If the graph was successfully created, print the RDF data
    if rdf_graph:
        turtle_data = rdf_graph.serialize(format='turtle')

        # Save to a file if needed
        filename = url.split("/")[-1].replace(".jsonld", ".ttl")
        with open(filename, "wb") as f:
            f.write(turtle_data.encode())

    # If you just want to print it
        #print(turtle_data.encode("utf-8"))
        # Print a summary of the RDF data (e.g., first 5 triples)
        #for s, p, o in rdf_graph.triples((None, None, None)):
        #    print(f"Subject: {s}, Predicate: {p}, Object: {o}")
        mlmodel = URIRef("https://api.cloud.ai4eosc.eu/v1/catalog/modules/thermal-bridges-rooftops-detector")
        sparqlresult = rdf_graph.query("""
            SELECT ?title
            WHERE {
                ?s dct:title ?title.
            }
            """)
        for row in sparqlresult:
            print("Title from RDF: " + row.title)
        json = parse_json_from_url(url)
        print("Title from JSON: " + json['title'])
