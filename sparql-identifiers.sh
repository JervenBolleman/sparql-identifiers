#!/bin/bash
sparql=$1
java -jar target/sparql-identifiers-1.3-SNAPSHOT-jar-with-dependencies.jar "$sparql"
