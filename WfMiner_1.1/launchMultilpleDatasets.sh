#!/bin/bash

jarF=/Users/ahmedhalioui/NetBeansProjects/OntoPattern16/dist/OntoPattern16.jar

minSupp="0.5"
bowl="phylOntology_v51_small_final.bowl"
FILES="./WD-Phy-extracted-3/*.xml"
namespace="http://www.co-ode.org/ontologies/ont.owl#"
gold="./WD-Phy-gold-1.xml"
topnItems="10"
topkRules="50"


for f in $FILES
do
  echo "$(basename $f)"
  # take action on each file. $f store current file name
  java -jar $jarF $minSupp $bowl $f $namespace $gold $topnItems $topnItems
done