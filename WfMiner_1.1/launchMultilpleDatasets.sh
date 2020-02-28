#!/bin/bash

jarF=./dist/OntoPattern16.jar

minSupp="0.2"
bowl="phylOntology_v51_small_final1.bowl"
FILES="./WD-Phy-extracted-3/*.xml"
namespace="http://www.co-ode.org/ontologies/ont.owl#"
gold="./WD-Phy-gold-1.xml"
topnItems="10"
topkRules="50"
minLevel="2"


for f in $FILES
do
  echo "$(basename $f)"
  # take action on each file. $f store current file name
  java -jar $jarF $minSupp $bowl $f $namespace $gold $topnItems $topnItems $minLevel
done