package com.example;

import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.loader.DocumentLoader;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.loader.DocumentLoaderOptions;
import com.apicatalog.jsonld.document.JsonDocument;
import com.github.jsonldjava.core.JsonLdError;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.system.JenaTitanium;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.net.URI;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;
import java.util.Properties;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class AppJena {

    public static Model readFromURL(String URL){
        return RDFDataMgr.loadModel(URL);
   }
    public static void main(String[] args) {
        String jsonLdUrl = "https://semiceu.github.io/EOSC-MLDCAT-AP-Pilot/example2/thermal-bridges-rooftops-detector.jsonld"; // Replace with your JSON-LD URL
        //String jsonLdUrl = "https://api1.dev.ai4eosc.eu/v1/catalog/modules/zooprocess-multiple-classifier/metadata?profile=mldcatap";
        String outputFilePath = "output.ttl";
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

                //JsonLdOptions options = new JsonLdOptions();
                //options.setDocumentLoader(customLoader);

                // Parse the JSON-LD input stream using Apache Jena
                //RDFParser.create()
                //    .source(inputStream)
                //    .lang(org.apache.jena.riot.RDFLanguages.JSONLD)
                //    .parse(model);

                TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
                };
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new SecureRandom());

                final Properties props = System.getProperties(); 
                props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
                HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext).build();

                //Model model = ModelFactory.createDefaultModel();

                RDFParserBuilder builder = RDFParserBuilder.create();
                RDFParser parser = builder
                                        .httpHeader("Accept", "*/*")
                                        .httpClient(httpClient)
                                        .source(inputStream)
                                        .lang(org.apache.jena.riot.RDFLanguages.JSONLD)
                                        .build();
                //Model model = parser.toModel();
                //RdfDataset dataset = (RdfDataset) JsonLdProcessor.toRDF(inputStream,  options);
                // Output the model in Turtle format
                //Reader myreader = Reader.
                //model.write(System.out, RDFFormat.TURTLE.getLang().getName());
                JsonLdOptions options = new JsonLdOptions();

                DocumentLoader customLoader2 = new DocumentLoader() {
                    @Override
                    public Document loadDocument(URI url, DocumentLoaderOptions options) throws JsonLdError {
                        // TODO Auto-generated method stub
                        Document  doc = null;
                        try {
                            //HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
                            HttpsURLConnection connection2 = (HttpsURLConnection) url.toURL().openConnection();
                            connection2.setRequestProperty("Accept", "application/ld+json");
                            InputStream inputStream = connection2.getInputStream();
                            doc = JsonDocument.of(inputStream);
                            return doc;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return doc;
                    }
                };
                options.setDocumentLoader(customLoader2);
                RdfDataset dataset = JsonLd.toRdf(jsonLdUrl).options(options).get();
                System.out.println(dataset.size());

                OutputStream outputStream = new FileOutputStream(outputFilePath);
                    
                DatasetGraph jenadataset2 = JenaTitanium.convert(dataset, RiotLib.dftProfile());
                System.out.println(jenadataset2.getDefaultGraph().sizeLong());
                RDFDataMgr.write(outputStream, jenadataset2, RDFFormat.TRIG);

                //OR
                //DatasetGraph jenadataset = DatasetGraphFactory.create();
                //Titanium2Jena.populateDataset(dataset, jenadataset);
                //System.out.println(jenadataset.getDefaultGraph().sizeLong());
                //RDFDataMgr.write(outputStream, jenadataset, RDFFormat.TRIG);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
