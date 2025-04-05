# EOSC-MLDCAT-AP-Pilot

This repository is created in the context EOSC-MLDCAT-AP Pilot to support EOSC to reach semantic interoperability by adoping MLDCAT-AP.

While technical interoperability is reached via [EOSC REST API](https://api1.dev.ai4eosc.eu/docs#/) semantic interoperabiltiy could be reached by:
- enriching the JSON output via a json-ld context to generate an RDF output in the form of JSON structure (JSON-LD);
- generating an RDF in Turtle syntax.

The EOSC REST API has been [modified](https://api1.dev.ai4eosc.eu/docs#/Modules%20catalog/get_metadata_v1_catalog_modules__item_name__metadata_get) to:
- specify the accept header, allowing to request "application/ld+json" and "text/turtle" formats
- specify the type of profile such as "mldcatap"

The EOSC REST API is tested first with an API portal online to generate the output depending on the parameters (accept header and query parameter) like https://reqbin.com/

For JSON-LD, the output is first tested in [JSON-LD Playground](https://json-ld.org/playground/), if it is able to parse it.
For Turtle, the output is tested in [Turle validator](http://ttl.summerofcode.be/). 

There are 4 types of clients:
1) [Python client](./python), via the [rdflib](https://rdflib.readthedocs.io/en/stable/) library, stored in the python folder ;
2) Java client, via the [RDF4J](https://rdf4j.org/) library, stored in the java folder ;
3) Java client, via [Titanium-JSONLD](https://github.com/filip26/titanium-json-ld) to parse JSON-LD and [Jena](https://jena.apache.org/) library, stored in the java folder.
4) Javascript (node) client, via [jsonld.js](https://www.npmjs.com/package/jsonld) library to parse the JSON-LD and [N3](https://www.npmjs.com/package/n3) to convert to Turtle.

For instructions see the README.me in the respective folders.

# Code list

The code_list folder contains the work done for to generate code lists
