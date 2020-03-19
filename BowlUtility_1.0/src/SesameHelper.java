import ontologyrep2.Concept;
import ontologyrep2.OntoRepresentation;
import ontologyrep2.Relation;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.ValueFactoryImpl;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SesameHelper {
  public static Model readRDF(final String _fileName, final RDFFormat _format, final String _namespace){
    java.net.URL documentUrl = null;
    InputStream inputStream = null;
    File f = new File(_fileName);
    try {
      documentUrl = new URL(_namespace);
    } catch (MalformedURLException ex) {
      Logger.getLogger(BowlUtility.class.getName()).log(Level.SEVERE, null, ex);
    }
    if(documentUrl==null) return null;
    try {
      //inputStream = documentUrl.openStream();
      inputStream = new FileInputStream(f);
    } catch (IOException ex) {
      Logger.getLogger(BowlUtility.class.getName()).log(Level.SEVERE, null, ex);
    }
    RDFParser rdfParser = Rio.createParser(_format);
    Model myGraph = new LinkedHashModel();
    rdfParser.setRDFHandler(new StatementCollector(myGraph));

    try {
      rdfParser.parse(inputStream, documentUrl.toString());
    } catch (IOException | RDFParseException | RDFHandlerException ex) {
      Logger.getLogger(BowlUtility.class.getName()).log(Level.SEVERE, null, ex);
    }
    return myGraph;
  }

  public static void loadClassesFromModel(final Model _rdfModel, OntoRepresentation _ontology){
    //filter les triplets, uniquement les classes.
    ArrayList<Resource> allClasses = new ArrayList<>();
    ValueFactory factory = ValueFactoryImpl.getInstance();
    for(final Resource r : _rdfModel.filter(null, RDF.TYPE, RDFS.CLASS).subjects()){
      //on cherche celles dont le label correspond
      allClasses.add(r);
    }
    for(final Resource r : _rdfModel.filter(null, RDF.TYPE, OWL.CLASS).subjects()){
      allClasses.add(r);
    }
    System.out.println("we have total of "+allClasses.size()+" classes");
    for(final Resource r : allClasses){
      System.out.println(r.stringValue());
    }

    final ArrayList<IRI> rootClasses = new ArrayList<>();
    final ArrayList<IRI> nonRootClasses = new ArrayList<>();
    for(final Resource r : allClasses){
      IRI actualIRI = null;
      try{
        actualIRI = (IRI) r;
      }
      catch(Exception e){
        //cannot convert...
      }
      if(null != actualIRI){
        Set<Resource> objects = _rdfModel.filter(actualIRI, RDFS.SUBCLASSOF, null).subjects();
        if(objects.isEmpty()){
          rootClasses.add(actualIRI);
        }
        else nonRootClasses.add(actualIRI);
      }
    }
    System.out.println(rootClasses.size()+" vs "+nonRootClasses.size());

    int cursor = 0;
    for(final IRI resource : rootClasses){
      final String stringValue = resource.stringValue();//""+_ontology.concepts.size+1;
      final Concept cc = _ontology.createConcept(stringValue, _ontology.concepts.size+1);
      System.out.println("##############NEW ROOT CONCEPT CREATED : "+cursor+"##################");
      //on ajoute le nouveau concept dans l'ontologie
      _ontology.addRootConcept(stringValue, cc);
      //OldOntoToNew.how_many_root_concepts++;
      convertConceptNode(_rdfModel, _ontology, resource, cc);
    }

    /*Set<Integer> rootConcepts = mappedHierarchy.getRootConcepts();
    it = rootConcepts.iterator();
    while(it.hasNext()){
      //On recupere les id des concepts
      Integer i = (Integer)it.next();
      Concept cc = ontology.createConcept(i.toString(), i);
      System.out.println("##############NEW CONCEPT CREATED : "+i+"##################");
      //on ajoute le nouveau concept dans l'ontologie
      ontology.addRootConcept(i.toString(), cc);
      OldOntoToNew.how_many_root_concepts++;
      OldOntoToNew.convertConceptNode(i, mappedHierarchy, ontology, cc);
    }*/
  }

  public static void convertConceptNode(Model _rdfModel, OntoRepresentation ontology, IRI _cOri, Concept _cDest){
    //on chcerche tous les fils du concept (ATTENTION a la redondance !)
    final Set<Resource> children = _rdfModel.filter(null, RDFS.SUBCLASSOF, _cOri).subjects();
    System.out.println(_cOri +" : This concept has "+children.size());
    for(final Resource ii : children){
      final IRI resource = (IRI) ii;
      //On cree un nouveau concept
      final String stringValue = resource.stringValue();//""+ontology.concepts.size+1;
      System.out.println("Testing for "+stringValue);
      if(ontology.index_concepts_by_name.get(stringValue) == null){
        final Concept cc = ontology.createConcept(stringValue, ontology.concepts.size+1);
        System.out.println("############## ext NEW CONCEPT CREATED:"+resource+"##################");
        //on ajoute le nouveau concept dans l'ontologie
        ontology.addConcept(stringValue, cc);
        //how_many_concepts++;
      /*if(ii> OldOntoToNew.max_index_concept){
        OldOntoToNew.max_index_concept = ii;
      }*/
        System.out.println("############## ext NEW CONCEPT ADDED"+resource+"##################");
        //heritage
        if(_cDest!=null){
          ontology.addConceptChild(cc, _cDest);
          System.out.println("##############ext NEW CONCEPT HERITED:"+resource+" extends "+_cDest.index+"##################");
          //how_many_extends++;
        }
        else{
          //how_many_root_concepts++;
          System.out.println("c is null.");
        }
        //on repete le traitement pour chaque fils
        convertConceptNode(_rdfModel, ontology, resource, cc);
      }
      else{
        System.out.println("Children already present "+stringValue);
      }
    }
  }

  public static void loadRelationsFromModel(final Model _rdfModel, OntoRepresentation _ontology){
    ArrayList<Resource> allProperties = new ArrayList<>();
    ValueFactory factory = ValueFactoryImpl.getInstance();
    for(final Resource r : _rdfModel.filter(null, RDF.TYPE, RDF.PROPERTY).subjects()){
      //on cherche celles dont le label correspond
      allProperties.add(r);
    }
    for(final Resource r : _rdfModel.filter(null, RDF.TYPE, OWL.OBJECTPROPERTY).subjects()){
      allProperties.add(r);
    }
    for(final Resource r : _rdfModel.filter(null, RDF.TYPE, OWL.DATATYPEPROPERTY).subjects()){
      allProperties.add(r);
    }
    System.out.println("we have total of "+allProperties.size()+" properties");

    final ArrayList<Resource> rootProperties = new ArrayList<>();
    final ArrayList<Resource> nonRootProperties = new ArrayList<>();
    for(final Resource r : allProperties){
      Set<Resource> objects = _rdfModel.filter(r, RDFS.SUBPROPERTYOF, null).subjects();
      if(objects.isEmpty()){
        rootProperties.add(r);
      }
      else nonRootProperties.add(r);
    }
    System.out.println(rootProperties.size()+" vs "+nonRootProperties.size());

    int cursor = 0;
    for(final Resource r : rootProperties){
      final IRI resource = (IRI) r;
      final String stringValue = resource.stringValue();//""+_ontology.relations.size+1;
      final Relation cc = _ontology.createRelation(stringValue, _ontology.relations.size+1);
      System.out.println("##############NEW PROPERTY CREATED : "+cursor+"##################");
      //on ajoute le nouveau concept dans l'ontologie
      _ontology.addRootProperty(stringValue, cc);
      //OldOntoToNew.how_many_root_concepts++;
      convertPropertyNode(_rdfModel, _ontology, resource, cc);
    }
  }

  public static void convertPropertyNode(Model _rdfModel, OntoRepresentation ontology, IRI _cOri, Relation _cDest){
    //on chcerche tous les fils du concept (ATTENTION a la redondance !)
    final Set<Resource> children = _rdfModel.filter(null, RDFS.SUBPROPERTYOF, _cOri).subjects();
    System.out.println("This property has "+children.size());
    for(final Resource ii : children){
      //On cree un nouveau concept
      IRI resource = (IRI) ii;
      final String stringValue = resource.stringValue();//""+ontology.concepts.size+1;

      if(ontology.index_relations_by_name.get(stringValue) == null){
        final Relation cc = ontology.createRelation(stringValue, ontology.relations.size+1);
        System.out.println("############## ext NEW PROPERTY CREATED:"+resource+"##################");
        //on ajoute le nouveau concept dans l'ontologie
        ontology.addRelation(stringValue, cc);
        //how_many_concepts++;
      /*if(ii> OldOntoToNew.max_index_concept){
        OldOntoToNew.max_index_concept = ii;
      }*/
        System.out.println("############## ext NEW PROPERTY ADDED"+resource+"##################");
        //heritage
        if(_cDest!=null){
          ontology.addPropertyChild(cc, _cDest);
          System.out.println("##############ext NEW PROPERTY HERITED:"+resource+" extends "+_cDest.index+"##################");
          //how_many_extends++;
        }
        else{
          //how_many_root_concepts++;
          System.out.println("c is null.");
        }
        //on repete le traitement pour chaque fils
        convertPropertyNode(_rdfModel, ontology, resource, cc);
      }
    }
  }
}
