const axios = require('axios');
const jsonld = require('jsonld');
const { Writer, Parser } = require('n3');
const https = require('https');
const fs = require('fs');

const url = 'https://api1.dev.ai4eosc.eu/v1/catalog/modules/thermal-bridges-rooftops-detector/metadata?profile=mldcatap';

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
    console.error('Error fetching JSON-LD:', error.message);
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
    console.error('Error fetching context:', error.message);
    return null;
  }
};

const processAndConvert = async (jsonLdData, remoteContexts = {}) => {
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
    console.log('N-Quads String:', nquadsString);

    const parser = new Parser({ format: 'application/n-quads' });
    const writer = new Writer({ format: 'text/turtle' });

    parser.parse(nquadsString, (error, quad, prefixes) => {
      if (quad) {
        console.log('Parsed Quad:', quad);
        writer.addQuad(quad);
      } else if (error) {
        console.error('Error parsing N-Quads:', error);
      } else {
        // End the writer once all quads are processed
        writer.end((error, result) => {
          if (error) {
            console.error('Error converting to Turtle:', error);
          } else {
            console.log('Turtle format:\n', result);
            fs.writeFile('output.ttl', result, (err) => {
              if (err) {
                console.error('Error writing to file:', err);
              } else {
                console.log('Turtle format successfully written to output.ttl');
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
  const jsonLdData = await fetchJsonLd(url);
  if (jsonLdData) {
    const remoteContexts = {
      // Add pre-fetched remote contexts here
    };

    await processAndConvert(jsonLdData, remoteContexts);
  }
})();
