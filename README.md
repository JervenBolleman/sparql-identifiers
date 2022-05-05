sparql-identifiers
==================

sparql for identifiers.org to convert possible URI patterns simply by having a virtual triple store.

```
git clone https://github.com/JervenBolleman/sparql-identifiers
cd sparql-identifiers
mvn assembly:assembly
./sparql-identifiers.sh "PREFIX owl:<http://www.w3.org/2002/07/owl#> SELECT ?target WHERE {<https://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915> owl:sameAs ?target}"
```

Or as a server

```
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8080
```

Or a docker image

```
mvn spring-boot:build-image
docker run sparql-identifiers:1.3-SNAPSHOT
```

Then test with a curl command 
```
curl -v -H 'Accept:application/sparql-results+xml' 'http://localhost:8080/sparql/?query=PREFIX%20owl:%3Chttp://www.w3.org/2002/07/owl%23%3E%20SELECT%20?target%20WHERE%20%7B%3Chttps://www.ebi.ac.uk/QuickGO/GTerm%3Fid=GO:0006915%3E%20owl:sameAs%20%3Ftarget%7D'

```

See the issue lists for future tasks..
The only requirements are a maven3 and java11+ installation.
