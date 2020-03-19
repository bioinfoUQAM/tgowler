package cleanrepresentation;

import ontologyrep2.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class Mapping {
  // Mapping between IDs and ontology classes
  public  Map<Integer, Concept> classesByIDs;
  public Map<Concept, Integer> idsByClasses;

  // Mapping between IDs and ontology properties
  public Map<Integer, Relation> propertiesByIDs;
  public Map<Relation, Integer> idsByProperties;

  public Map<Integer, Instance> individualsByIDs;
  public Map<Instance, Integer> idsByIndividuals;

  protected static <T extends OntologyItem> void mapResourceAndID(final Map<T, Integer> idsByResources, Map<Integer, T> resourcesByIds,
               final T resource, final Integer id ) {
    if(id == null){
      System.out.println("id was null...");
      System.exit(1);
    }
    // Mapping : resource --> ID
    idsByResources.put( resource, id );
    // Mapping : ID --> resource
    resourcesByIds.put( id, resource );
  }

  public final static byte POSTFIX_ORDER = 0x00;
  public final static byte PREFIX_ORDER = 0x01;
  public final static byte INFIX_ODER = 0x02;

  // Map a class with an id and map its descendants
  public static Integer mapClassesAndIDs(final byte _TRAVERSAL_ORDER, final OntoRepresentation cleanOntologyRepresentation,
                                         Mapping mapping, final Concept ontClass, Integer id ) {
    // Make the mapping of the resource and the ID
    //ontologyMapping.mapClassAndId( ontClass, id );
    if(_TRAVERSAL_ORDER == PREFIX_ORDER) {
      mapResourceAndID(mapping.idsByClasses, mapping.classesByIDs, ontClass, id++);
    }
    //mapping.mapClassAndId( ontClass, id++ );
    // Map its descendants with greater IDs
    final ArrayList<Concept> childClasses = cleanOntologyRepresentation.getConceptChildren( ontClass );
    for (final Concept childClass : childClasses){
      id = mapClassesAndIDs(_TRAVERSAL_ORDER, cleanOntologyRepresentation, mapping, childClass, id++ );
    }

    if(_TRAVERSAL_ORDER == POSTFIX_ORDER) {
      mapResourceAndID(mapping.idsByClasses, mapping.classesByIDs, ontClass, id++);
    }
    return id;
  }

  // Map a property with an id and map its descendants
  public static Integer mapPropertiesAndIDs( final OntoRepresentation cleanOntologyRepresentation, Mapping mapping, final Relation ontClass, Integer id ) {
    // Make the mapping of the resource and the ID
    mapResourceAndID( mapping.idsByProperties, mapping.propertiesByIDs, ontClass, id++ );
    //mapping.mapPropertyAndId( ontClass, id++ );
    // Map its descendants with greater IDs
    ArrayList<Relation> childProperties = cleanOntologyRepresentation.getPropertyChildren( ontClass );
    for (final Relation childProperty : childProperties){
      id = mapPropertiesAndIDs(  cleanOntologyRepresentation, mapping, childProperty, id++ );
    }
    return id;
  }

  // Map a class with an id and map its descendants
  public static Integer mapInstancesAndIDs(final OntoRepresentation cleanOntologyRepresentation, Mapping mapping, final Instance ontClass, Integer id ) {
    mapResourceAndID( mapping.idsByIndividuals, mapping.individualsByIDs, ontClass, id );
    return id;
  }
}
