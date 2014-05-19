sparql-identifiers
==================

sparql for identifiers.org to convert possible URI patternssimple by having a virtual triple store.

```
git clone https://github.com/JervenBolleman/sparql-identifiers
cd sparql-identifiers
mvn assembly:assembly
./sparql-identifiers.sh "PREFIX owl:<http://www.w3.org/2002/07/owl#> SELECT ?target WHERE {<http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915> owl:sameAs ?target}"
```

See the issue lists for future tasks..
The only requirements are a maven3 and java6+ installation.
