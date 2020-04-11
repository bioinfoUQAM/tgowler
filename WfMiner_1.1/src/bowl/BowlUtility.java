package bowl;

import cleanrepresentation.OntologyMiningPreprocessor;
import cleanrepresentation.Mapping;
import ontologyrep20.*;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.ValueFactoryImpl;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.*;
import utility.SesameHelper;

import java.util.ArrayList;
import java.util.Set;

public class BowlUtility {

  public static void main(final String[] _args){
    String filename = "";//args[0];
    String baseURI = "";//args[1];
    String outputFile = "";//lasttest";

    if(_args.length > 0){
      filename = _args[0];
    }

    if(_args.length > 1){
      baseURI = _args[1];
      if(baseURI.trim().length() == 0) baseURI = "";
    }

    if(_args.length > 2){
      outputFile = _args[2];
    }

    String _compress = "";//args[2];
    boolean compress = true;
    if(_args.length > 3){
      _compress = _args[3];
      if(_compress.contains("no")){
        compress = false;
      }
    }

    if(_args.length > 4){
      Converter2Bowl.DEFAULT_FILESIZE = Integer.parseInt(_args[4]);
    }

    if(_args.length > 5){
      Converter2Bowl.DEFAULT_ARR_SZ = Integer.parseInt(_args[5]);
    }

    final String ext = ".bowl";
    outputFile = outputFile + ext;
    System.out.println("filename="+filename);
    System.out.println("baseURI="+baseURI);
    System.out.println("output="+outputFile);
    System.out.println("compression="+_compress);
    System.out.println("filesize="+Converter2Bowl.DEFAULT_FILESIZE);
    System.out.println("tempBufferSZ="+Converter2Bowl.DEFAULT_ARR_SZ);

    // 1 - read onlogy file with rdf4j
    Model model = SesameHelper.readRDF("./resources/ontology-travel.owl", RDFFormat.RDFXML, "http://www.owl-ontologies.com/");
    final OntoRepresentation representation = new OntoRepresentation();
    // 2 - then, build a custom in-memory representation from an rdf4j model
    BowlUtility.loadOntologyFromModel(model, representation);
    final OntoRepresentation cleanRepresentation = representation.clone();//shallow copy, hierarchy links will be removed
    // 3  -  build a clean representation of the ontology and a mapping (i.e. total order) for classes and properties
    final Mapping mapping = OntologyMiningPreprocessor.performAll(Mapping.POSTFIX_ORDER, representation, cleanRepresentation);
    final OntoRepresentation serializedRepresentation = new OntoRepresentation();
    // 4 - create a final copy of the clean ontology with ids for classes and properties as mapped ids
    OntologyMiningPreprocessor.updateOntology(mapping, cleanRepresentation, serializedRepresentation);
    // 5 - serialize the last ontology into a bowl file
    Converter2Bowl.serialize(outputFile, serializedRepresentation, model, mapping, compress);
  }

  public static void loadOntologyFromModel(final Model _rdfModel, OntoRepresentation _ontology){
    //OntoRepresentation ontology = new OntoRepresentation();
    //on recupere tous les concepts racines
    SesameHelper.loadClassesFromModel(_rdfModel, _ontology);
    SesameHelper.loadRelationsFromModel(_rdfModel, _ontology);

    ValueFactory factory = ValueFactoryImpl.getInstance();
    ArrayList<Concept> allConceptsArray = new ArrayList<>();
    {
      final LinkedList<Concept> allConcepts = _ontology.getAllConcepts();
      if (allConcepts != null) {
        allConcepts.reset();
        do {
          LinkedList<Concept>.LinkedNode<Concept> curr2 = allConcepts.curr();
          final Concept candidateRange = curr2.value;
          allConceptsArray.add(candidateRange);
        }
        while (allConcepts.hasNext());
      }
    }

    for(int i=0;i!=allConceptsArray.size();++i){
      final Concept candidateDomain = allConceptsArray.get(i);
      final IRI candidateDomainIRI = factory.createIRI(candidateDomain.getName());
      //System.out.println("searching for properties domain= "+candidateDomain.getName());
      for(int j=0;j!=allConceptsArray.size();++j){
        final Concept candidateRange = allConceptsArray.get(j);
        final IRI candidateRangeIRI = factory.createIRI(candidateRange.getName());
        //System.out.println("searching for properties range= "+candidateRange.getName());
        //chercher dans Sesame
        {
          final Set<Resource> subjects = _rdfModel.filter(null, RDFS.DOMAIN, candidateDomainIRI).subjects();
          for(final Resource r : subjects){
            final Set<Resource> candidateProperties = _rdfModel.filter(r, RDFS.RANGE, candidateRangeIRI).subjects();
            if(!candidateProperties.isEmpty()){
              System.out.println("between "+candidateProperties.toString()+ " for "+candidateDomain.getName()+" "+candidateRange.getName());
              for(Resource prop : candidateProperties){
                final IRI actualProperty = (IRI)prop;
                //RadixTree.RadixNode localNameNode = _ontology.index_relations_by_name.getNode(namespace.toCharArray(), _ontology.index_relations_by_name.root);
                final Relation rel = (Relation) _ontology.index_relations_by_name.get(actualProperty.stringValue(), _ontology.index_relations_by_name.root);
                if(null != rel) {
                  System.out.println("added one more triple to schema ! : "+rel.getName());
                  _ontology.createTriplet(rel, candidateDomain, candidateRange);
                }
              }
            }
          }
        }
      }
    }
  }

}
