package legacy;

import java.util.ArrayList;
import ontologyrep2.Concept;
import ontologyrep2.OntoRepresentation;
import ontologyrep2.Relation;
import ontopatternmatching.Motif;

/**
 * PatternGenerator generates new patterns by canonical operations :
 * <ul>
 * 		<li> Adding a root concept at the end of the sequence </li>
 * 		<li> Adding a property between a concept in the sequence and the last concept </li>
 * 		<li> Specializing the last concept of the sequence </li>
 * 		<li> Specializing the last property added (or specialized) in the sequence </li>
 * </ul>
 * 
 * @author Frapin Kevin
 */
public abstract class PatternGenerator{        
        /**
         * By ACC ?
         * @param motif
         * @param hierarchyRepresentation
         * @return 
         */
        public static ArrayList<Motif> generateWorkflowsByACC( final Motif motif, final OntoRepresentation hierarchyRepresentation, int minLevel ){
		ArrayList<Concept> rootConcepts = hierarchyRepresentation.getRootConcepts(minLevel);//3 avant
                
                /*System.out.println("voici les root concepts:"+hierarchyRepresentation.root_relations.size());
                for(Relation rc : hierarchyRepresentation.root_relations){
                    System.out.println(">>>"+rc.index+", "+rc.getName());
                }
                System.exit(1);
		*/
                ArrayList<Motif> newWorkflows = new ArrayList<>();
                /**
                 * Pour tous les concepts racines, on cree un nouveau pattern avec en plus le concept racine
                 */
                if(rootConcepts != null){
                    for (Concept rootConcept : rootConcepts ){
                        Motif newWorkflow = new Motif(motif);
                        newWorkflow.addConceptC(rootConcept.index);//CRITIQUE !!!
                        if(!newWorkflows.contains(newWorkflow)) newWorkflows.add(newWorkflow);
                    }
                }
		
		return newWorkflows;
	}        
        
        /**
         * 64 , 64
        36 , 36
        52 , 52
        * 
        * 
        * 8 , http://www.info.uqam.ca/Members/valtchev_p/mbox/ETP-tourism.owl#Infrastructure
27 , http://www.info.uqam.ca/Members/valtchev_p/mbox/ETP-tourism.owl#Recreational
30 , http://www.info.uqam.ca/Members/valtchev_p/mbox/ETP-tourism.owl#Historical
31 , http://www.info.uqam.ca/Members/valtchev_p/mbox/ETP-tourism.owl#Natural
        * 
         * @param motif
         * @param hierarchyRepresentation
         * @return 
         */
        public static ArrayList<Motif> generateWorkflowsBySCC( final Motif motif, final OntoRepresentation hierarchyRepresentation ){
            //ArrayList<Integer> concepts = motif.concepts;            
            // Get the concept to specialize and its children
            Integer lastConcept = motif.concepts.get(motif.concepts.size()-1);
            
            
            ArrayList<Concept> conceptChildren = hierarchyRepresentation.getConceptChildren(lastConcept);
            /*System.out.println("on tente de specialiser "+35);
            System.out.println(""+hierarchyRepresentation.getConcept(35).index+ ", " + hierarchyRepresentation.getConcept(35).getName());
            System.out.println("on a "+conceptChildren.size()+" spec");
            for(Concept c : conceptChildren){
                System.out.println(""+c.index+" , " +c.getName());
            }
            System.exit(1);
            */
            
            /*ArrayList<Concept> conceptChildren = hierarchyRepresentation.getConceptChildren(lastConcept);
            System.out.println("on tente de specialiser "+lastConcept);
            System.out.println(""+hierarchyRepresentation.getConcept(lastConcept).index+ ", " + hierarchyRepresentation.getConcept(lastConcept).getName());
            System.out.println("on a "+conceptChildren.size()+" spec");
            for(Concept c : conceptChildren){
                System.out.println(""+c.index+" , " +c.getName());
            }
            System.exit(1);*/
            // Specialize this concept
            ArrayList<Motif> newWorkflows = new ArrayList<>();
            for(Concept conceptChild : conceptChildren){
                //System.out.println("son is : "+conceptChild.index);
                Motif newWorkflow = new Motif(motif);
                newWorkflow.splConceptC(conceptChild.index);//est ce que ca marche
                if(!newWorkflows.contains(newWorkflow)) newWorkflows.add(newWorkflow);
            }
            return newWorkflows;
	}
                
        /**
         * 
         * @param motif
         * @param propertiesAndSubject
         * @return 
         */
        public static ArrayList<Motif> generateWorkflowsByAPC( final Motif motif, final ArrayList<PropertiesAndSubject> propertiesAndSubject )
	{
            // Set of the generated sequences
            ArrayList<Motif> newWorkflows = new ArrayList<>( );
            // Creation of a new sequence by adding the applicable properties
            for ( PropertiesAndSubject propertiesAndPosition : propertiesAndSubject ){
                Integer positionSource = propertiesAndPosition.getSecond();	
                ArrayList<Relation> temp = propertiesAndPosition.getFirst();

                for(Relation r : temp){
                    //System.out.println("Triplets:(r)"+t.relation.index+",(d)"+t.domaine.index+",(c)"+t.codomaine.index);
                    Motif newWorkflow = new Motif( motif );
                    // Addition of the property in the new sequence
                    newWorkflow.addPropertyC(positionSource, r.index);
                    if(!newWorkflows.contains(newWorkflow)) newWorkflows.add( newWorkflow );
                }
            }
            return newWorkflows;
	}
                
        public static ArrayList<Motif> generateWorkflowsBySPC( final Motif motif, final ArrayList<Relation> applicableProperties )
	{
		// Set of the generated new sequences
		ArrayList<Motif> newWorkflows = new ArrayList<>( );
		// Get the positions of the source and the target concept
		Integer sourcePosition = motif.sourcePositionInf-1;
		
		for (Relation propertyChild : applicableProperties){
			Motif newWorkflow = new Motif(motif);
			newWorkflow.splPropertyC(sourcePosition, propertyChild.index);
                        if(!newWorkflows.contains(newWorkflow)) newWorkflows.add( newWorkflow );
		}
		return newWorkflows;
	}
}
