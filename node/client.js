const axios = require('axios');
const jsonld = require('jsonld');
const { Writer, Parser } = require('n3');
const https = require('https');
const fs = require('fs');
const path = require('path');
const yaml = require('js-yaml');

// === Load config.yaml from one directory up ===
const configPath = path.resolve(__dirname, '..', 'config.yaml');
const config = yaml.load(fs.readFileSync(configPath, 'utf8'));

// === Build model URLs from config ===
const { endpoint, method, metadata } = config.api;
const models = config.models;
const urls = models.map(name => `${endpoint}${method}${name}${metadata}`);

const httpsAgent = new https.Agent({
  rejectUnauthorized: false  // Disable SSL certificate validation
});

const fetchJsonLd = async (url) => {
  try {
    const response = await axios.get(url, {
      headers: {
        'Accept': 'application/ld+json'
      },
      httpsAgent
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching JSON-LD from ${url}:', error.message);
    return null;
  }
};

const fetchContext = async (contextUrl) => {
  try {
    const response = await axios.get(contextUrl, {
      headers: {
        'Accept': 'application/ld+json'
      },
      httpsAgent
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching context from ${contextUrl}:', error.message);
    return null;
  }
};

const processAndConvert = async (jsonLdData, modelName, remoteContexts = {}) => {
  try {
    const expanded = await jsonld.expand(jsonLdData, {
      documentLoader: async (url) => {
        if (remoteContexts[url]) {
          return {
            contextUrl: null,
            document: remoteContexts[url],
            documentUrl: url
          };
        }
        const contextData = await fetchContext(url);
        return {
          contextUrl: null,
          document: contextData,
          documentUrl: url
        };
      }
    });

    const nquadsString = await jsonld.toRDF(expanded, { format: 'application/n-quads' });
    //console.log('N-Quads String:', nquadsString);

    const parser = new Parser({ format: 'application/n-quads' });
    const writer = new Writer({ format: 'text/turtle' });

    var counter = 0 ;
    parser.parse(nquadsString, (error, quad, prefixes) => {
      if (quad) {
        //console.log('Parsed Quad:', quad);
        writer.addQuad(quad);
        counter += 1 ;
      } else if (error) {
        console.error('Error parsing N-Quads:', error);
      } else {
        // End the writer once all quads are processed
        writer.end((error, result) => {
          if (error) {
            console.error('Error converting to Turtle:', error);
          } else {
            //console.log('Turtle format:\n', result);
            const outputFile = `output-${modelName}.ttl`;
            fs.writeFile(outputFile, result, (err) => {
              if (err) {
                console.error('Error writing to file:', err);
              } else {
                //console.log(`Turtle format for ${modelName} written to ${outputFile}`);
                console.log('Found ' + counter + " triples in " + outputFile) ;
              }
            });
          }
        });
      }
    });
  } catch (error) {
    console.error('Error processing JSON-LD:', error.message);
  }
};

(async () => {
  for (let i = 0; i < urls.length; i++) {
    const url = urls[i];
    const modelName = models[i];
    //console.log(`\nFetching and processing model: ${modelName}`);
    const jsonLdData = await fetchJsonLd(url);
    if (jsonLdData) {
      const remoteContexts = {
      // Add pre-fetched remote contexts here
    };
    await processAndConvert(jsonLdData, modelName, remoteContexts);
    }
  }
})();
