# EOSC-MLDCAT-AP-Pilot

This repository is created in the context EOSC-MLDCAT-AP Pilot to support EOSC to reach semantic interoperability by adoping MLDCAT-AP.

While technical interoperability is reached via EOSC REST API.
To reach semantic interoperabiltiy the REST API:
- could be enriched via a json-ld context to provide an RDF output in the form of JSON structure (JSON-LD);
- could generate an RDF using a Turtle syntax.

For JSON-LD, the output is first tested in JSON-LD Playground, if it is able to parse it.
For Turtle, the output is test in Turle validator. 

There are 3 types of clients:
1) Python client, via the rdflib library, stored in the python folder ;
2) Java client, via the RDF4J library, stored in the java folder ;
3) Java client, via Titanium-JSONLD and Jena libraries, stored in the java folder.

For instructions see the README.me in the respective folders.

# Code list

In addition a task_type.json is provided.