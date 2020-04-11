package cleanrepresentation;

import cleanrepresentation.steps.CleanRepresentation;
import ontologyrep20.Concept;
import ontologyrep20.LinkedList;
import ontologyrep20.OntoRepresentation;
import ontologyrep20.Relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static cleanrepresentation.steps.OntologyMapping.createOntologyMapping;

public class OntologyMiningPreprocessor {

  public static Mapping performAll(final byte _TRAVERSAL, final OntoRepresentation _ontology, final OntoRepresentation _cleanHierarchyOutput){
    // Final ontology representation
    //final OntoRepresentation cleanHierarchy = _ontology.clone();
    // Remove the inheritance in the hierarchy
    CleanRepresentation.createCleanHierarchyRepresentation(_ontology, _cleanHierarchyOutput);//ok
    //HTMLViz.dumpCleanedHierarchy("../GraphLinearisation/test-viz7.html", _cleanHierarchyOutput, _cleanHierarchyOutput);

    // Create the links between properties and concepts
    //MappedHierarchyRepresentation.linkHierarchies(_ontology, cleanHierarchy );
    //final OntoRepresentation cleanHierarchy = createCleanHierarchyRepresentation(_ontology);
    //System.out.println("all concepts clean => "+cleanHierarchy.concepts.size);
    final Mapping mapping = createOntologyMapping(_TRAVERSAL, _ontology, _cleanHierarchyOutput);
    //final OntoRepresentation mappedHierarchy = createMappedHierarchyRepresentation(cleanHierarchy, mapping);
    return mapping;
  }

  public static void updateOntology(final Mapping _mapping, final OntoRepresentation _cleanRepresentation,
                                    final OntoRepresentation _outputRepresentation){
    // il faut prendre cleanRep, copier tout le contenu en utilisant les id dans mapping
    Map<Integer, Integer> oldConceptToNew = new HashMap<>();
    Map<Integer, Integer> oldRelationToNew = new HashMap<>();
    {
      final LinkedList<Concept> concepts = _cleanRepresentation.getAllConcepts();
      concepts.reset();
      do {
        final Concept oldConcept = concepts.curr().value;
        final String label = oldConcept.getName();
        //System.out.println("updating "+oldConcept.toString()+" "+_mapping.idsByClasses.containsKey(oldConcept));
        final Concept cid = _outputRepresentation.createConcept(label, _mapping.idsByClasses.get(oldConcept));
        _outputRepresentation.addConcept(label, cid);
        //System.out.println("created "+cid.toString());
        oldConceptToNew.put(oldConcept.index, cid.index);
      }
      while (concepts.hasNext());
    }

    //
    {
      final LinkedList<Relation> relations = _cleanRepresentation.getAllProperties();
      relations.reset();
      do {
        final Relation oldProperties = relations.curr().value;
        final String label = oldProperties.getName();
        final Relation rid = _outputRepresentation.createRelation(label, _mapping.idsByProperties.get(oldProperties));
        _outputRepresentation.addRelation(label, rid);
        oldRelationToNew.put(oldProperties.index, rid.index);
      }
      while (relations.hasNext());
    }


    //hierarchie concept
    {
      final LinkedList<Concept> concepts = _cleanRepresentation.getAllConcepts();
      concepts.reset();
      do {
        final Concept oldConcept = concepts.curr().value;
        final ArrayList<Concept> conceptParents = new ArrayList<>();
        if(oldConcept.parent != null)
          conceptParents.add(oldConcept.parent);

        if(conceptParents != null && !conceptParents.isEmpty()){
          final Concept newConcept = (Concept)_outputRepresentation.index_concepts_by_name.get(oldConcept.getName());
          //final Concept newConcept = _outputRepresentation.getConcept(oldConceptToNew.get(oldConcept.index));
          for(final Concept parent : conceptParents){
            final Concept newParent = (Concept)_outputRepresentation.index_concepts_by_name.get(parent.getName());
            //final Concept newChild = _outputRepresentation.getConcept(oldConceptToNew.get(child.index));
            _outputRepresentation.addConceptChild(newConcept, newParent);
            System.out.println("added "+newConcept.toString()+" as child of "+newParent.toString());
          }
        }
      }
      while (concepts.hasNext());
    }

    //hierarchie relations
    {
      final LinkedList<Relation> relations = _cleanRepresentation.getAllProperties();
      relations.reset();
      do {
        final Relation oldRelation = relations.curr().value;
        final ArrayList<Relation> relationParents = new ArrayList<>();
        if(oldRelation.parent != null)
          relationParents.add(oldRelation.parent);
        //final ArrayList<Relation> relationChildren = _cleanRepresentation.getPropertyChildren(oldRelation.index);
        if(relationParents != null && !relationParents.isEmpty()){
          for(final Relation parent : relationParents){
            _outputRepresentation.addPropertyChild(_outputRepresentation.getRelation(oldRelationToNew.get(oldRelation.index)),
                    _outputRepresentation.getRelation(oldRelationToNew.get(parent.index)));
          }
        }
      }
      while (relations.hasNext());
    }

    //
  }
}
