# Choose the java client

Change, in the pom.xml, the value of <mainClass> for the maven-jar-plugin and maven-assembly-plugin.

The value could be:
- com.example.AppRDF4J to use RDF4J or 
- com.example.AppJena  to use Titanium-JSON-LD and Jena

# Clean (to remove the target folder)

Command:
 & "C:\Users\estani002\Downloads\apache-maven-3.9.6-bin\apache-maven-3.9.6\bin\mvn.cmd" clean -f "pom.xml"

# Build (to generate the target folder containing the jar application)

Command:
 & "C:\Users\estani002\Downloads\apache-maven-3.9.6-bin\apache-maven-3.9.6\bin\mvn.cmd" package assembly:single  -f "pom.xml" -s settings.xml

# Execute (to launch the Java API client)

Commands:
cd .\target\
java -jar .\demo-1.0-SNAPSHOT-jar-with-dependencies.jar 

# Output

Both clients generate, in the target folder, the output.ttl file, as the result of the parsing the JSON-LD from the API and convertion to Turtle/Trig syntax.

The Jena client prints on the terminal the number of triples extracted from the Titanium JSON-LD Dataset and the number of triples inserted in the Jena Dataset saved in the output.ttl file.
