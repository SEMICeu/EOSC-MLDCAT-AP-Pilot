@echo off

cd java
start C:\Users\estani002\Downloads\apache-maven-3.9.6-bin\apache-maven-3.9.6\bin\mvn.cmd clean -f "pom.xml"
start C:\Users\estani002\Downloads\apache-maven-3.9.6-bin\apache-maven-3.9.6\bin\mvn.cmd package assembly:single  -f "pom.xml" -s settings.xml
cd target
start java -jar .\demo-1.0-SNAPSHOT-jar-with-dependencies.jar 

cd ..
cd ..

cd python
python .\client.py  

cd ..
cd node
start C:\Users\estani002\Downloads\node-v22.14.0-win-x64\node-v22.14.0-win-x64\node.exe client.js

pause