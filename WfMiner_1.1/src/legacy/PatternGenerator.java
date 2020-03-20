package legacy;

import java.util.ArrayList;
import ontologyrep20.Concept;
import ontologyrep20.OntoRepresentation;
import ontologyrep20.Relation;
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
         * By DCC ?
         * @param motif
         * @param hierarchyRepresentation
         * @return 
         */
        // Append in the last transaction
        public static ArrayList<Motif> generateSequencesByDCC( final Motif motif, final OntoRepresentation hierarchyRepresentation, int minLevel, int level ){
                ArrayList<Concept> rootConcepts = hierarchyRepresentation.getRootConcepts(minLevel);//3 avant
                ArrayList<Motif> newWorkflows = new ArrayList<>();
                if(rootConcepts != null){
                    for (Concept rootConcept : rootConcepts ){
                        Motif newWorkflow = new Motif(motif);
                        newWorkflow.appendConceptC(rootConcept.index, level);//CRITIQUE !!!
                        System.out.println("rootConcept.index: " + rootConcept.index);
                        //NOTE 1 : Added an additional test: new pattern MUST be different than its parent
                        //  i.e. If no refinnement is possible, the method returns the SAME pattern
                        //NOTE 2 : For .contains to work, it requires a .equals to be implemented (cf. Motif class)
                        if(!motif.equals(newWorkflow) && !newWorkflows.contains(newWorkflow)) {
                            newWorkflows.add(newWorkflow);
                            System.out.println("newWorkflow: "+newWorkflow + ", oldWorkflow: "+motif);
                        }
                    }
                }
                
		return newWorkflows;
	}
        
        /**
         * By ACC ?
         * @param motif
         * @param hierarchyRepresentation
         * @return 
         */
        // Add to the last transaction
        public static ArrayList<Motif> generateSequencesByACC( final Motif motif, final OntoRepresentation hierarchyRepresentation, int minLevel, int level ){
                ArrayList<Concept> rootConcepts = hierarchyRepresentation.getRootConcepts(minLevel);//3 avant
                ArrayList<Motif> newWorkflows = new ArrayList<>();
                if(rootConcepts != null){
                    for (Concept rootConcept : rootConcepts ){
                        Motif newWorklow = new Motif(motif);
                        newWorklow.addConceptC(rootConcept.index, level);//CRITIQUE !!!
//                        System.out.println("newSequence : " + newSequence);
                        if(!newWorkflows.contains(newWorklow)) newWorkflows.add(newWorklow);
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
        public static ArrayList<Motif> generateSequencesBySCC( final Motif motif, final OntoRepresentation hierarchyRepresentation ){
            //ArrayList<Integer> concepts = motif.concepts;            
            // Get the concept to specialize and its children
//            System.out.println("motif: " + motif);
            ArrayList<Integer> lastTransaction = motif.transactions.get(motif.nbTransactions() - 1);
            Integer lastConcept = lastTransaction.get(lastTransaction.size()-1);
//            System.out.println("generateSequencesBySCC lastConcept: " + lastConcept);
            
            
            ArrayList<Concept> conceptChildren = hierarchyRepresentation.getConceptChildren(lastConcept);
            
            
            // Specialize this concept
            ArrayList<Motif> newWorkflows = new ArrayList<>();
            if (!conceptChildren.isEmpty()) {
//                System.out.println("conceptChildren: " + conceptChildren);
                for(Concept conceptChild : conceptChildren){
                    Motif copySpe = new Motif(motif);
                    copySpe.splConceptC(conceptChild.index);
                    if(!newWorkflows.contains(copySpe)) newWorkflows.add(copySpe);
//                    System.out.println("next newWorkflows: " + newWorkflows);
                }
                
            }
            return newWorkflows;
	}
                
        /**
         * 
         * @param motif
         * @param propertiesAndSubject
         * @return 
         */
        public static ArrayList<Motif> generateSequencesByAPC( final Motif motif, final ArrayList<PropertiesAndSubject> propertiesAndSubject )
	{
            // Set of the generated sequences
            ArrayList<Motif> newSequences = new ArrayList<>( );
            // Creation of a new sequence by adding the applicable properties
            for ( PropertiesAndSubject propertiesAndPosition : propertiesAndSubject ){
                Integer positionSource = propertiesAndPosition.getSecond();	
                ArrayList<Relation> temp = propertiesAndPosition.getFirst();

                for(Relation r : temp){
                    //System.out.println("Triplets:(r)"+t.relation.index+",(d)"+t.domaine.index+",(c)"+t.codomaine.index);
                    Motif newSequence = new Motif( motif );
                    // Addition of the property in the new sequence
                    newSequence.addPropertyC(positionSource, r.index);
                    if(!newSequences.contains(newSequence)) newSequences.add( newSequence );
                }
            }
            return newSequences;
	}
                
        public static ArrayList<Motif> generateSequencesBySPC( final Motif motif, final ArrayList<Relation> applicableProperties )
	{
		// Set of the generated new sequences
		ArrayList<Motif> newSequences = new ArrayList<>( );
		// Get the positions of the source and the target concept
		Integer sourcePosition = motif.sourcePositionInf-1;
		
		for (Relation propertyChild : applicableProperties){
			Motif newSequence = new Motif(motif);
			newSequence.splPropertyC(sourcePosition, propertyChild.index);
                        if(!newSequences.contains(newSequence)) newSequences.add( newSequence );
		}
		return newSequences;
	}
}
