# TGROWLeR
TGROWLeR system abstracts general patterns from workflow sequences previously extracted from texts. 
It comprises two modules –a workflow extractor and a pattern miner– both relying on a specific domain ontology. 

## Prerequisites
* JAVA 1.8: <span style="color: #0000ff;"><a href="http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html" download="http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html">http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html</a></span>
* PYHTON 2.7 <span style="color: #0000ff;"><a href="https://www.python.org/download/releases/2.7/" download="https://www.python.org/download/releases/2.7/">https://www.python.org/download/releases/2.7</a></span>
* TOMCAT 8.0 <span style="color: #0000ff;"><a href="https://tomcat.apache.org/download-80.cgi" download="https://tomcat.apache.org/download-80.cgi">https://tomcat.apache.org/download-80.cgi</a></span>
* SESAME OpenRDF 2.7.16 <span style="color: #0000ff;"><a href="https://sourceforge.net/projects/sesame/files/Sesame%202/2.7.16/" download="https://sourceforge.net/projects/sesame/files/Sesame%202/2.7.16/">https://sourceforge.net/projects/sesame/files/Sesame%202/2.7.16</a></span>
* GATE 8.1 <span style="color: #0000ff;"><a href="https://gate.ac.uk/download/" download="https://gate.ac.uk/download/">https://gate.ac.uk/download/</a></span></H5>

<H2>Input/output data</H2>

* The phylogenetic ontology PHAGE is available in: http://bioportal.bioontology.org/ontologies/PHAGE
* Texts, extracted workflows and generated patterns are available in: http://labo.bioinfo.uqam.ca/index.php?action=tgrowler

<H2>Tools</H2>

<H3>WfExtractor_1.0: Worklflow Extractor on Gate 8.1</H3>

<p>
The <b>WfExtractor_1.0</b> tool annotates a text corpus with its phylgoenetic analyses workflows.
Some of the features of <b>WfExtractor_1.0</b> are:
<ul>
<li>Extract workflow components (programs, parameters, data and metadata) from texts</li>
<li>Extract data flows (relations) from texts</li>
<li>Create a WSD (Word Sense Disambiguation) models for both components and relations</li>
<li>Export Gate Inline XML corpus.</li>
</ul>
</p>

<H4>Installation:</H4>
1. Unzip the <b>WfExtractor_1.0.zip</b> file.
2. Import all files from $WfExtractor_HOME/plugins to the $GATE_HOME/plugins directory 
3. Load the PHAGE ontology via tomcat (see <span style="color: #0000ff;"><a href="http://www.jenitennison.com/2011/01/25/getting-started-with-rdf-and-sparql-using-sesame-and-python.html" download="http://www.jenitennison.com/2011/01/25/getting-started-with-rdf-and-sparql-using-sesame-and-python.html">Sesame deployment guide</a></span>)
4. If JAVA reports an error please configure the $TOMCAT_HOME/bin/catalina.sh file to prevent Entity Expansion Attacks: ```
JAVA_OPTS="$JAVA_OPTS -Djdk.xml.entityExpansionLimit=100000000 -Djdk.xml.FEATURE_SECURE_PROCESSING=false -Xmx6G"```
5. Configure the <b>Gazetteer_LKB</b> dictionary configuration file $WfExtractor_HOME/application-resources/Dictionary_from_remote_repository/config.ttl</li> with changing the ontology information:```
hr:repositoryURL \< YOUR_HTTP_REPOSITORY \>"
rep:repositoryID "[YOUR_REPOSITORY_ID]"
rdfs:label "[YOUR_REPOSITORY_LABEL]"```
6. Open Gate and import the application file <b>WfExtractor1.0.xgapp</b> from $WfExtractor_HOME
7. Run the application (see <span style="color: #0000ff;"><a href="https://gate.ac.uk/releases/gate-8.1-build5169-ALL/doc/tao/splitch3.html" download="https://gate.ac.uk/releases/gate-8.1-build5169-ALL/doc/tao/splitch3.html">Gate 8.1 Developer Guide</a></span>).


<H3>WfMiner_1.1: Worklflow Pattern Miner and Rule Recommender</H3>

<p>
<b>WfMiner_1.0</b> mines abstract closed patterns and generate associations from XML worklfow sequence files and a specific domain ontology.
</p>

<H4>Installation:</H4>

1. Launch the <b>bowlUtil_0.5</b> tool and transform the OWL ontology into a binary one (see the README file in $WFMINER_HOME/bowlUtil/). Bowl tranformation is used to speed up the mining process and load a lighter version of the ontology. <b>Note:</b> <i>please use the bowl version of the ontology from the input data (above) to skip this step and don't forget to download the Gene Ontology (owl version)</i>
2. Unzip the <b>WfMiner_1.0.zip</b> file Launch the WfMiner miner using the following code on your shell (see the README file in WFMINER_HOME/):```
java -jar java -jar[PATH_TO]/OntoPattern16.jar "[minSupp]" "[PATH_TO]/[bowl_file]" "[PATH_TO]/[train_set]" "[namespace]" "[PATH_TO]/[test_set]" "[topkItems]" "[topnRules]" "[min)ontology_level]"```

<H3>Other T-GROWLer tools</H3>

<H4>WfTransformer_1.0: </H4>
<p>
This tool transforms the Gate inline XML into sequences of events (encoded in a simple XML tree).
</p>

<H4>WfSimulator_1.0: </H4>

<p>
This tool simulates phylogenetic workflows using instances encoded in the ontolog PHAGE. Using apriori abstract patterns provided by an expert to guide workflow reconstruction. The simulator is based on a Montre Carlo simulation fixing a number of parameters each run to generate event sequences.
</p>


<H2>Contact</H2>
<p>
For any technical issues, please e-mail admin: <a href="mailto:halioui.ahmed@uqam.ca?Subject=Technical issue" target="_top">halioui.ahmed@uqam.ca</a>
</p>
