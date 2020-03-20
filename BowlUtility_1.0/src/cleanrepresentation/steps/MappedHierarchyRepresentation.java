package cleanrepresentation.steps;

import cleanrepresentation.Mapping;
import ontologyrep20.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MappedHierarchyRepresentation {

  public static OntoRepresentation createMappedHierarchyRepresentation(final OntoRepresentation _ontology, final Mapping _mapping){
    // Final mapped ontology
    OntoRepresentation mappedOntology = new OntoRepresentation();
    // Create the hierarchies of IDs
    //createMappedHierarchies( cleanHierarchyRepresentation, mapping, mappedOntology );
    // Create the whole hierarchy concepts (represented by their IDs)
    //createMappedHierarchyConcepts( cleanOntologyRepresentation, mapping, agent );
    // Create the roots of the agent hierarchy concepts
    createMappedHierarchyConceptsRoots( _ontology, _mapping, mappedOntology );
    // Create the inheritance in the agent hierarchy
    createMappedHierarchyConceptsInheritance( _ontology, _mapping, mappedOntology );

    // Create the whole hierarchy properties (represented by their IDs)
    //faire la meme chose avec les proprietes
    //createMappedHierarchyProperties( cleanOntologyRepresentation, mapping, agent);
    // Create the roots of the agent hierarchy properties
    createMappedHierarchyPropertiesRoots( _ontology, _mapping, mappedOntology );
    // Create the inheritance in the agent hierarchy properties
    createMappedHierarchyPropertiesInheritance(_ontology, _mapping, mappedOntology );
    // Create the links between concepts (IDs) and properties (IDs)
    //linkMappedHierarchies( _ontology, _mapping, mappedOntology );
    return mappedOntology;
  }

  // Create the links between properties and concepts
  public static void linkHierarchies(final OntoRepresentation _ontology, OntoRepresentation cleanHierarchyRepresentation )
  {
    final LinkedList<Relation> allProperties = cleanHierarchyRepresentation.getAllProperties();
    do{
      LinkedList<Relation>.LinkedNode<Relation> curr = allProperties.curr;
      Relation r = curr.value;
      //linkPropertiesByConcepts(cleanHierarchyRepresentation, null, null, r, 0, 0);
    }
    while(allProperties.hasNext());
    /*for(final Relation r : allProperties){

    }*/
    // Get all properties
    /*Set<OntProperty> properties = jenaWrapper.getAllProperties( );
    for ( OntProperty property : properties )
    {
      // Get the domain and range f the property
      OntResource domain = jenaWrapper.getDomain( property );
      OntResource range = jenaWrapper.getRange( property );
      // Create the link between the property and these concepts
      linkPropertyByConcepts( property, domain, range );
    }*/
  }

  public static void linkPropertiesByConcepts(final OntoRepresentation cleanOntologyRepresentation,
                                              final Concept _domain, final Concept _range,
                                              final Set<Integer> _propertyIds, final int _domainId, final int _rangeId){
    final ArrayList<Concept> domainChildren = cleanOntologyRepresentation.getConceptChildren(_domain);
    final ArrayList<Concept> rangeChildren = cleanOntologyRepresentation.getConceptChildren(_range);
    //Set<A> subjectChildren = getConceptChildren( subjectConcept );
    //Set<A> objectChildren = getConceptChildren( objectConcept );

    //il faut stocker qqch ici anyway. a t'on vraimment besoin de cela ?

    for (final Concept subjectChild : domainChildren ){
      // Link the children of the subject
      linkPropertiesByConcepts(cleanOntologyRepresentation, subjectChild, _range, _propertyIds, 0, 0);
    }
    for(final Concept subjectTarget : rangeChildren ){
      // Link the children of the object
      linkPropertiesByConcepts( cleanOntologyRepresentation, _domain, subjectTarget, _propertyIds, 0, 0 );
    }
  }

  private static void linkMappedHierarchies( final OntoRepresentation cleanOntologyRepresentation, final Mapping mapping, final OntoRepresentation _output) {
    // Create the links between roots properties and concepts
    ArrayList<Concept> rootConcepts = cleanOntologyRepresentation.getRootConcepts();
    for(final Concept domain : rootConcepts){
      for(final Concept range : rootConcepts){
        final ArrayList<Triplet> rootPropertiesByConcepts = cleanOntologyRepresentation.getRootProperties(domain, range);
        {
          // Get the rang and domain IDs
          //OntResource domain = pairConcepts.getFirst( );
          //OntResource range = pairConcepts.getSecond( );
          final Integer domainID = /*mapping.getIdbyClass( domain )*/mapping.idsByClasses.get(domain);
          final Integer rangeID = /*mapping.getIdbyClass( range )*/mapping.idsByClasses.get(range);
          // Get the properties between domain and range
          //Set<OntResource> properties = propertiesByConcepts.get( pairConcepts );
          // Get the properties IDs
          final Set<Integer> propertiesIds = new HashSet<>();
          for(final Triplet t : rootPropertiesByConcepts){
            propertiesIds.add(t.relation.index);
          }
          //Set<Integer> propertiesIDs = mapping.getIdsByProperties( properties );
          // Save the mapped result
          linkPropertiesByConcepts(cleanOntologyRepresentation, domain, range, propertiesIds, domainID, rangeID);
        }
        /*final ArrayList<Triplet> nonRootPropertiesByConcepts = cleanOntologyRepresentation.getNonRootProperties(domain, range);
        {
          final Integer domainID = this.idsByClasses.get(domain);
          final Integer rangeID = this.idsByClasses.get(range);
        }*/
      }
    }

    //linkMappedHierarchies(rootPropertiesByConcepts, mapping, mappedHierarchy);
    // Create the links between non roots properties and concepts
    //PropertiesByConcepts<OntResource> nonRootPropertiesByConcepts = cleanOntologyRepresentation.getNonRootPropertiesByConcepts();
    //linkMappedHierarchies(nonRootPropertiesByConcepts, mapping, mappedHierarchy);
  }


  // Create the inheritance in the hierarchy properties of the agent
  private static void createMappedHierarchyPropertiesInheritance( final OntoRepresentation cleanOntologyRepresentation, final Mapping mapping, OntoRepresentation mappedHierarchy )
  {
    // Get all the properties
    LinkedList<Relation> allProperties = cleanOntologyRepresentation.getAllProperties();
    do{
      final LinkedList<Relation>.LinkedNode<Relation> curr = allProperties.curr;
      final Relation currRel = curr.value;
      Integer idProperty = mapping.idsByProperties.get(currRel);
      final ArrayList<Relation> childProperties = cleanOntologyRepresentation.getPropertyChildren(currRel);
      for (final Relation childProperty : childProperties){
        // Link each parent concept with its children
        Integer idChildProperty = mapping.idsByProperties.get(childProperty);
        //mappedHierarchy.addPropertyChild( idChildProperty, idProperty );
      }
    }
    while(allProperties.hasNext());
  }

  // Create the roots of the concept hierarchy of the agent
  private static void createMappedHierarchyPropertiesRoots( final OntoRepresentation cleanOntologyRepresentation, final Mapping mapping, OntoRepresentation mappedHierarchy )
  {
    // Get the roots
    ArrayList<Relation> rootProperties = cleanOntologyRepresentation.getRootProperties(true);
    for ( Relation rootProperty : rootProperties )
    {
      // Add each root to the hierarchy properties
      Integer idRootProperty = mapping.idsByProperties.get(rootProperty);
      //mappedHierarchy.addRootProperty( idRootProperty );
    }
  }

  private static void createMappedHierarchyConceptsRoots( final OntoRepresentation cleanOntologyRepresentation, final Mapping mapping, OntoRepresentation agent )
  {
    // Get the root classes
    final ArrayList<Concept> rootClasses = cleanOntologyRepresentation.getRootConcepts();
    for (final Concept rootClass : rootClasses){
      // Add each root to the concept hierarchy
      Integer rootConcept = mapping.idsByClasses.get(rootClass);
      //agent.addRootConcept( rootConcept );
    }
  }

  // Create the inheritance in the concept hierarchy of the ontology agent
  private static void createMappedHierarchyConceptsInheritance(	final OntoRepresentation cleanOntologyRepresentation, final Mapping mapping, OntoRepresentation agent )
  {
    // Get all classes
    final LinkedList<Concept> allClasses = cleanOntologyRepresentation.getAllConcepts();
    // Create the inheritance in the concept hierarchy
    do{
      final LinkedList<Concept>.LinkedNode<Concept> curr = allClasses.curr;
      final Concept currClass = curr.value;
      Integer parentConcept = mapping.idsByClasses.get(curr.value);
      ArrayList<Concept> childClasses = cleanOntologyRepresentation.getConceptChildren(currClass);
      for (final Concept childClass : childClasses){
        // Link each parent concept with its children
        Integer childConcept = mapping.idsByClasses.get(childClass);
        //agent.addConceptChild( childConcept, parentConcept );
      }
    }
    while(allClasses.hasNext());
    /*for ( OntResource ontClass : allClasses )
    {
      Integer parentConcept = mapping.getIdbyClass( JenaWrapper.asClass( ontClass ) );
      Set<OntResource> childClasses = cleanOntologyRepresentation.getConceptChildren( ontClass );
      for ( OntResource childClass : childClasses )
      {
        // Link each parent concept with its children
        Integer childConcept = mapping.getIdbyClass(  JenaWrapper.asClass( childClass ) );
        agent.addConceptChild( childConcept, parentConcept );
      }
    }*/
  }
}
