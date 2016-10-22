IMPORTANT : ceci ne fonctionne pas avec JDK>7 !!!!!!!
java -jar bowlUtil.jar OntologyFileName BaseURI OutputFile Compression TotalFileSize BufferSize
ex : "resources/file-tests/ontology-travel-performance.owl" " " "testargskompress" "yes" "140000" "500"
NOTA : Les deux derniers parametres sont la au cas ou l'ontologie ne passe pas avec les parametres par defaut. Si ca crashe relancer avec des valeurs plus grandes (en octets)
NOTA : si le parametre de compression est a yes, la taille importe peu, tous les byte a zero seront enleves