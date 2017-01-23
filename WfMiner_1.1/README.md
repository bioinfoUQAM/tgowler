For software installation guide and more input/output data please visit the root project in: https://github.com/bioinfoUQAM/tgowler

# BUILD OUTPUT DESCRIPTION

When you build an Java application project that has a main class, the IDE
automatically copies all of the JAR
files on the projects classpath to your projects dist/lib folder. The IDE
also adds each of the JAR files to the Class-Path element in the application
JAR files manifest file (MANIFEST.MF).

To run the project from the command line, go to the dist folder and
type the following (replace[PATH_TO] with appropriate directory data):

java -jar java -jar[PATH_TO]/OntoPattern16.jar "[minSupp]" "[PATH_TO]/[bowl_file]" "[PATH_TO]/[train_set]" "[namespace]" "[PATH_TO]/[test_set]" "[topkItems]" "[topnRules]" "[min)ontology_level]"


For example:
java -jar java -jar./OntoPattern16.jar "0.1" "./phylOntology_v51_small_final.bowl" "./WD-Phy-extracted-1/WD-Phy-extracted-1_2783_0.xml" "http://www.co-ode.org/ontologies/ont.owl#" "./WD-Phy-gold-1.xml" "10" "50" "2"



To distribute this project, zip up the dist folder (including the lib folder)
and distribute the ZIP file.

Notes:

* If two JAR files on the project classpath have the same name, only the first
JAR file is copied to the lib folder.
* Only JAR files are copied to the lib folder.
If the classpath contains other types of files or folders, these files (folders)
are not copied.
* If a library on the projects classpath also has a Class-Path element
specified in the manifest,the content of the Class-Path element has to be on
the projects runtime path.
* To set a main class in a standard Java project, right-click the project node
in the Projects window and choose Properties. Then click Run and enter the
class name in the Main Class field. Alternatively, you can manually type the
class name in the manifest Main-Class element.
