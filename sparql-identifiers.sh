#!/bin/bash
bedfile=$1
sparql=$2
java -jar target/sparql-bed-1.0-SNAPSHOT-jar-with-dependencies.jar $bedfile "$sparql"
