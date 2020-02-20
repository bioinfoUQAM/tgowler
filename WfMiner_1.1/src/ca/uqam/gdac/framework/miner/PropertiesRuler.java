package ca.uqam.gdac.framework.miner;

import java.util.ArrayList;
import java.util.Objects;
import ontologyrep2.OntoRepresentation;
import ontologyrep2.Relation;
import ontopatternmatching.Motif;

/**
 * PropertiesRuler is a class used to filter the elements (properties, concept position) in order
 *  to keep ONLY those that can be applied on a pattern in order to make new patterns.
 *  
 *  This ruler exists to make sure that each pattern is generated once, and only once. 
 * 
 * @author Frapin Kevin
 */
import legacy.PropertiesAndSubject;
public class PropertiesRuler 
{
	//--------------------------------------------------- Public static methods
	/**
	 * Keep the properties which have an id < to the last property applied id.
	 * 
	 * @param pattern Pattern used to filter.
	 * @param propertiesAndSubject Properties (by subject position) to filter.
	 */        
        public static void apcFilterPropertiesById(final Motif pattern, final ArrayList<PropertiesAndSubject> propertiesAndSubject ){
		// Get the ID of the last property added/specialized
		Integer propertySup = pattern.propertySup;
		// Get the position of the source concept used by the last AP
		Integer sourcePositionInf = pattern.sourcePositionInf-1;
		for( PropertiesAndSubject propertiesAndSrcPosition : propertiesAndSubject ){
                    if(Objects.equals(propertiesAndSrcPosition.getSecond(), sourcePositionInf) ){
                        // If some properties have already been applied between this source position and the last position
                        propertiesAndSrcPosition.setFirst(PropertiesRuler.filterPropertiesByPropertySup( propertiesAndSrcPosition.getFirst(), propertySup));
                    }
		}
	}
		
	/**
	 *  Keep properties which have a subject position greater than (or equal to) the last subject position used by the last AP operation.
	 *  
	 * @param pattern The pattern used to filter.
	 * @param propertiesAndSubject The properties (by subject position) to filter.
	 */        
        public static void apcFilterPropertiesBySrcPosition( final Motif pattern, ArrayList<PropertiesAndSubject> propertiesAndSubject ){
            // Get the position of the source concept of the last AP operation
            Integer sourcePositionInf = pattern.sourcePositionInf-1;
            // Remove the properties if their source position are < sourcePositionInf
            PropertiesRuler.removePropertiesBySrcPosition(propertiesAndSubject,sourcePositionInf);
	}
	
	// 
	/**
	 * Keep the properties which have an id < propertySup.
	 * 
	 * @param properties Properties to filter.
	 * @param propertySup Maximum id allowed.
     * @return 
	 */
	public static ArrayList<Relation> filterPropertiesByPropertySup( ArrayList<Relation> properties, final Integer propertySup )
	{
            ArrayList<Relation> filteredProperties = new ArrayList<>();            
            for( Relation property : properties ){
                if(property.index < propertySup ){
                    filteredProperties.add(property);
                }
            }
            return filteredProperties;
	}
        	
	/**
	 * Keeps the properties that could specialize the last applied property on the pattern.
	 * 
	 * @param pattern Pattern to extend by specialization.
	 * @param properties Properties to filter.
	 * @param hierarchyRepresentation Hierarchy representation of the ontology.
     * @return 
	 */        	
        public static ArrayList<Relation> spcFilterPropertiesByConcepts(final Motif pattern, ArrayList<Relation> properties, final OntoRepresentation hierarchyRepresentation){            
            ArrayList<Integer> lastTransaction = pattern.getConcepts(pattern.nbTransactions() - 1);
            // Get the source and target concepts
            // Get the non-root properties applicable between the source and the target concept
            // Remove the properties that are not applicable between the source and the target concept            
            return PropertiesRuler.removePropertiesByExclusion(properties, 
                    hierarchyRepresentation.getNonRootProperties_OnlyProp(hierarchyRepresentation.getConcept(lastTransaction.get(pattern.sourcePositionInf-1)), 
                    hierarchyRepresentation.getConcept(lastTransaction.get(pattern.concepts.size()-1))));
	}
        
        
	//-------------------------------------------------- Private static methods
	// Remove the properties that are not present in the non-root properties        
        private static ArrayList<Relation> removePropertiesByExclusion( ArrayList<Relation> properties, ArrayList<Relation> nonRootProperties )
	{
            ArrayList<Relation> filteredProperties = new ArrayList<>( );
            for ( Relation property : properties )
            {                
                for(Relation r : nonRootProperties){
                    if(r.index==property.index){
                        filteredProperties.add(property);
                        break;
                    }
                }
            }
            return filteredProperties;
	}
	
	// Remove the properties from the list if they have a position source lesser than the minimum
	private static void removePropertiesBySrcPosition( ArrayList<PropertiesAndSubject> propertiesAndSubject, Integer positionMinimum ){
            for ( int i = 0; i < propertiesAndSubject.size( ); i++ ){
                if( propertiesAndSubject.get(i).getSecond( ) < positionMinimum ){
                    propertiesAndSubject.remove( propertiesAndSubject.get( i ) );
                }
            }
	}

}
