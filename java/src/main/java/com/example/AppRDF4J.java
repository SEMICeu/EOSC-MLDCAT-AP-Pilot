package com.example;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import no.hasmac.jsonld.document.Document;
import no.hasmac.jsonld.document.JsonDocument;
import no.hasmac.jsonld.loader.DocumentLoader;
import no.hasmac.jsonld.loader.DocumentLoaderOptions;
import no.hasmac.jsonld.JsonLdError;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.rio.jsonld.JSONLDParserFactory;
import org.eclipse.rdf4j.rio.jsonld.JSONLDSettings;

import org.yaml.snakeyaml.Yaml;
import java.util.Map;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AppRDF4J {

    public static Map<String, Object> loadYamlConfig(String path) throws Exception {
        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(Paths.get(path))) {
            Map<String, Object> config = yaml.load(in);
            return config;
        }
    }

    public static void main(String[] args) {
        try {
            // Load config.yaml from one folder up
            Map<String, Object> config = loadYamlConfig("../../config.yaml");

            // Extract API parts
            Map<String, String> api = (Map<String, String>) config.get("api");
            List<String> models = (List<String>) config.get("models");

            String endpoint = api.get("endpoint");
            String method = api.get("method");
            String metadata = api.get("metadata");

            for (String modelName : models) {
                String jsonLdUrl = endpoint + method + modelName + metadata;
                String outputFilePath = "output-" + modelName + ".ttl";
    
                // System.out.println("\nFetching and processing model: " + modelName);
                // System.out.println("URL: " + jsonLdUrl);
                processJsonLdUrl(jsonLdUrl, modelName, outputFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void processJsonLdUrl(String jsonLdUrl, String modelName, String outputFilePath) {
        try {
            // Create a connection to the URL
            URL url = new URL(jsonLdUrl);

            SSLContext context = SSLContext.getInstance("TLSv1.2");
            TrustManager[] trustManager = new TrustManager[] {
                new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                public void checkClientTrusted(X509Certificate[] certificate, String str) {}
                public void checkServerTrusted(X509Certificate[] certificate, String str) {}
                }
            };
            context.init(null, trustManager, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/ld+json");
            connection.setRequestMethod("GET");

            // Open the input stream from the connection
            try (InputStream inputStream = connection.getInputStream()) {

                 Model model = new org.eclipse.rdf4j.model.impl.LinkedHashModel();

                 DocumentLoader customLoader = new DocumentLoader() {
                    @Override
                    public Document loadDocument(URI url, DocumentLoaderOptions options) throws JsonLdError {
                        // TODO Auto-generated method stub
                        JsonDocument doc = null;
                        try {
                            //HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
                            HttpsURLConnection connection2 = (HttpsURLConnection) url.toURL().openConnection();
                            InputStream inputStream = connection2.getInputStream();
                            doc = JsonDocument.of(inputStream);
                            return doc;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return doc;
                    }
                 };

                // Create a JSON-LD parser
                RDFParser parser = new JSONLDParserFactory().getParser();

                // Set custom settings if needed
                parser.getParserConfig().set(JSONLDSettings.USE_NATIVE_TYPES, true);
                parser.getParserConfig().set(JSONLDSettings.SECURE_MODE, false);
                parser.getParserConfig().set(JSONLDSettings.DOCUMENT_LOADER, customLoader);

                // Set the RDFHandler to collect statements into the model
                StatementCollector counter = new StatementCollector(model);
                parser.setRDFHandler(counter);

                // Parse the input stream
                parser.parse(inputStream, jsonLdUrl);

                int numberOfStatements = counter.getStatements().size();
                System.out.println("Found " + numberOfStatements + " triples in " + modelName);
                // Output the model in Turtle format
                FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath);
                Rio.write(model, fileOutputStream, RDFFormat.TURTLE);
                //Rio.write(model, System.out, RDFFormat.TURTLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
