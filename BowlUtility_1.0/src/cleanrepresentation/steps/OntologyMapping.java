package cleanrepresentation.steps;

import cleanrepresentation.Mapping;
import ontologyrep2.Concept;
import ontologyrep2.Instance;
import ontologyrep2.OntoRepresentation;
import ontologyrep2.Relation;

import java.util.ArrayList;
import java.util.HashMap;

import static cleanrepresentation.Mapping.*;

public class OntologyMapping {

  public static Mapping createOntologyMapping(final byte _TRAVERSAL_ORDER, final OntoRepresentation _original, final OntoRepresentation _ontology){
    // Final mapping of the ontology
    Mapping mapping = new Mapping();
    mapping.idsByIndividuals = new HashMap<>();
    mapping.idsByProperties = new HashMap<>();
    mapping.idsByClasses = new HashMap<>();
    mapping.classesByIDs = new HashMap<>();
    mapping.propertiesByIDs = new HashMap<>();
    mapping.individualsByIDs = new HashMap<>();
    // Map the classes / properties to IDs
    //createOntologyMapping( cleanHierarchyRepresentation, mapping );
    // Make a mapping between the classes and IDs
    createClassesMapping(_TRAVERSAL_ORDER, _ontology, mapping);
    // Make a mapping between the properties and IDs
    createPropertiesMapping(_ontology, mapping);
    // Map the individuals
    //createIndividualsMapping( _ontology, mapping);
    return mapping;
  }

  // Map all the individuals with IDs
  private static void createIndividualsMapping(final OntoRepresentation _ontology, final Mapping mapping){
    final ArrayList<Instance> allInstances = _ontology.getAllInstances();
    // Get all the individuals
    Integer id = 0;
    for(final Instance individual : allInstances ){
      mapInstancesAndIDs(_ontology, mapping, individual, id++);
    }
  }

  // Make a mapping between the properties and IDs (integers)
  private static void createPropertiesMapping(final OntoRepresentation cleanOntologyRepresentation, Mapping mapping  )
  {
    // Get the root properties
    final ArrayList<Relation> rootProperties = cleanOntologyRepresentation.getRootProperties(true);
    Integer id = 0;
    for (final Relation rootProperty : rootProperties ) {
      id = mapPropertiesAndIDs( cleanOntologyRepresentation, mapping, rootProperty, id );
    }
  }

  // Make a mapping between the classes and IDs (integers)
  private static void createClassesMapping(final byte _TRAVSERAL_ORDER, final OntoRepresentation cleanOntologyRepresentation, Mapping mapping  )
  {
    // Get the root resources
    final ArrayList<Concept> rootConcepts = cleanOntologyRepresentation.getRootConcepts();
    Integer id = 0;
    for (final Concept rootResource : rootConcepts){
      System.out.println("mapping "+rootConcepts.toString()+ " w/ "+id);
      id = mapClassesAndIDs(_TRAVSERAL_ORDER, cleanOntologyRepresentation, mapping, rootResource, id );
      //System.out.println(">>>"+rootResource.getURI()+", "+id);
    }
  }

}
