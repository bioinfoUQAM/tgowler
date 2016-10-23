IMPORTANT : ceci ne fonctionne pas avec JDK>7 !!!!!!!

java -jar bowlUtil.jar OntologyFileName BaseURI OutputFile Compression TotalFileSize BufferSize

ex : java -jar /Users/ahmedhalioui/NetBeansProjects/bowlUtil/bowlUtil.jar "/Users/ahmedhalioui/NetBeansProjects/bowlUtil/phylOntology_v51_small_final.rdf" "http://www.co-ode.org/ontologies/ont.owl#" "testargskompress" "yes" "500000" "1000"

NOTA : Les deux derniers parametres sont la au cas ou l'ontologie ne passe pas avec les parametres par defaut. Si ca crashe relancer avec des valeurs plus grandes (en octets)
NOTB : si le parametre de compression est a yes, la taille importe peu, tous les byte a zero seront enleves