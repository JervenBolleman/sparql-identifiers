#!/bin/bash
sparql=$1
java -jar target/sparql-identifiers-1.0-SNAPSHOT-jar-with-dependencies.jar "$sparql"
