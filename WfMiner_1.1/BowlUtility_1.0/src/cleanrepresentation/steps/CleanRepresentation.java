package cleanrepresentation.steps;

import ontologyrep20.*;
import ontologyrep20.LinkedList;

import java.util.*;

public class CleanRepresentation {


  public static void createCleanHierarchyRepresentation(final OntoRepresentation _ontologyInput, OntoRepresentation _ontologyOutput){
    // Make a copy of the hierarchies
    //OntoRepresentation hierarchyClasses = new Hierarchy<OntResource>( jenaWrapper.getHierarchyClasses( ) );
    //OntoRepresentation hierarchyProperties = new Hierarchy<OntResource>( jenaWrapper.getHierarchyProperties( ) );
    //System.out.println("clean>"+hierarchyClasses.getAllNodes().size());
    //System.out.println("jena>"+jenaWrapper.getAllClasses().size());
    // Remove the multiple inheritances in the hierarchies

    //tout faire en une seule methode ou alors en faire deux distinctes
    removeMultipleInheritances(_ontologyInput, _ontologyInput.getAllConcepts(), _ontologyOutput);
    removeMultipleInheritances(_ontologyInput, _ontologyInput.getAllProperties(), _ontologyOutput);
    // Set the clean hierarchies to the clean hierarchy representation
    //hierarchyRepresentation.setHierarchyConcepts( hierarchyClasses );
    //hierarchyRepresentation.setHierarchyProperties( hierarchyProperties );
  }

  private static <T extends OntologyItem> void removeMultipleInheritances(final OntoRepresentation _ontology, final LinkedList<T> _resources, final OntoRepresentation hierarchy){
    // Get the resources with more than on parent
    ArrayList<T> classesWithMutlipleInheritance = findResourcesWithMutipleInheritance(_ontology, _resources, hierarchy );
    // Keep the deepest inheritances in the hierarchies
    removeNonDeepestInheritance(_ontology, classesWithMutlipleInheritance, hierarchy );
  }


  // Method used to remove all the multiple inheritance present in the model
  /*private static void removeMultipleInheritances(final OntoRepresentation _ontology, Hierarchy<OntResource> hierarchy ){
    // Get the resources with more than on parent
    final ArrayList<Concept> classesWithMutlipleInheritance = findResourcesWithMutipleInheritance(_ontology, hierarchy );
    // Keep the deepest inheritances in the hierarchies
    removeNonDeepestInheritance(_ontology, classesWithMutlipleInheritance, hierarchy );
  }*/

  private static <T extends OntologyItem> void removeNonDeepestInheritance(final OntoRepresentation _ontology,
      final ArrayList<T> resourcesWithMultipleInheritance, OntoRepresentation hierarchy ) {
    for (final T resource : resourcesWithMultipleInheritance) {
      // Get the parents of the resource
      final ArrayList<T> resourceParents = _ontology.getParents(resource);
      // Break the inheritance link between the resource and the non deepest parents
      removeNonDeepestParents(_ontology, resource, resourceParents, hierarchy );
    }
  }

  // Remove the parents which are not the deepest one in the hierarchy
  private static <T extends OntologyItem> void removeNonDeepestParents(final OntoRepresentation _ontology, final T resource,
      final ArrayList<T> parentResources, OntoRepresentation hierarchy) {
    // Consider the first parent as the deepest one, in order to make comparison with the others
    final Iterator<T> itParents = parentResources.iterator();
    T deepestParent = itParents.next();//copy ?
    // Confront this parent with the others and keep the real deepest one
    while(itParents.hasNext()){
      // Compare the current parent with the deepest
      T currentParent = itParents.next();
      deepestParent = removeNonDeepestParent(_ontology, resource, deepestParent, currentParent,  hierarchy);
    }
  }

  // Compare the the two parent depths, and break the link between the child and the parent
  // which is less deep in the inheritance than the other
  private static <T extends OntologyItem> T removeNonDeepestParent(final OntoRepresentation _ontology, final T resource,
        final T firstParent, final T secondParent, OntoRepresentation hierarchy)
  {
    // Get the depth of the resource parents
    Integer firstDepth = getMaximumDepth( _ontology, firstParent, 1);
    Integer secondDepth = getMaximumDepth(_ontology, secondParent, 1);
    // If the current parent has a greater depth than the deepest
    if(secondDepth > firstDepth){
      // Remove the inheritance between the child and the "deepest" parent
      System.out.println("removing "+resource.toString()+" from "+firstParent.toString()+" rather than "+secondParent.toString());
      hierarchy.removeChildNode( resource, firstParent );
      return secondParent;
    }
    // else
    System.out.println("removing "+resource.toString()+" from "+secondParent.toString()+" rather than "+firstParent.toString());
    // Remove the inheritance between the child and the current parent
    hierarchy.removeChildNode(resource, secondParent);
    return firstParent;
  }

  private static <T extends OntologyItem> Integer getMaximumDepth(final OntoRepresentation _ontology, final T node, final Integer currentDepth ){
    // If there is no more parent to explore
    if(_ontology.isRootNode(node)){
      return (currentDepth);
    }
    // Explore the parents and find their maximum depth
    final ArrayList<T> parentNodes = _ontology.getParents(node);
    Integer maximumDepth = currentDepth;
    for (final T parentNode : parentNodes){
      final Integer depth = getMaximumDepth(_ontology, parentNode, currentDepth + 1);
      // Get the maximum depth among the parents of the current node
      if( depth > maximumDepth ){
        maximumDepth = depth;
      }
    }
    return maximumDepth;
  }

  // Find the classes that have more than one parent
  private static <T extends OntologyItem> ArrayList<T> findResourcesWithMutipleInheritance(final OntoRepresentation _ontology,
         final LinkedList<T> _resources, final OntoRepresentation hierarchy)
  {
    final ArrayList<T> childrenWithMultipleParents = new ArrayList<>();
    final LinkedList<T> allConcepts = _resources;//_ontology.getAllConcepts();
    /*do{
      final LinkedList<T>.LinkedNode<T> curr = allConcepts.curr();
      final ArrayList<T> parents = _ontology.getParents(curr.value);
      if(parents.size() > 1){
        childrenWithMultipleParents.add(curr.value);
      }
    }
    while(allConcepts.hasNext());
    */
    if (allConcepts != null) {
      allConcepts.reset();
      do {
        final LinkedList<T>.LinkedNode<T> curr = allConcepts.curr();
        final T candidateRange = curr.value;
        final ArrayList<T> parents = _ontology.getParents(curr.value);
        if(parents.size() > 1){
          childrenWithMultipleParents.add(curr.value);
        }
      }
      while (allConcepts.hasNext());
    }

    //boucle sur tous les concepts, conserve ceux qui ont plus de 1 parent.

    // Children that have more than one parent
    /*Set<OntResource> children = new HashSet<OntResource>( );

    // Get the classes which have more than one parent
    Set<OntResource> ontClasses = hierarchy.getAllNodes( );
    for ( OntResource ontClass : ontClasses )
    {
      // Get the parents oh the current node
      Set<OntResource> parentNodes = hierarchy.getParentsNodes( ontClass );
      if( parentNodes.size( ) > 1 )
      // If the child has more than one parent
      {
        children.add( ontClass );
      }
    }*/
    return childrenWithMultipleParents;
  }
}
