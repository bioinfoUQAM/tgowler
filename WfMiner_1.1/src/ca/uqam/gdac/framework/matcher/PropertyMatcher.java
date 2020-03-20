package ca.uqam.gdac.framework.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import legacy.Pair;
import ontologyrep20.OntoRepresentation;
import ontopatternmatching.Motif;
import ontopatternmatching.Sequence;


/**
 * PropertyMatcher represents the match of an input sequence against a pattern.
 * This match is based on the properties, regarding to the original ConceptMatcher.
 * 
 * @author Frapin Kevin
 *
 */
public class PropertyMatcher extends BaseMatcher
{
	//-------------------------------------------------------------- Attributes
	// A match of the concepts input against the pattern
	private ArrayList<Integer> conceptMatch;
	
	//------------------------------------------------------------- Constructor
	/**
	 * Creates a new PropertyMatcher using the conceptMatch and the hierarchyRepresentation to find a match between the properties of the  input and the pattern.
	 * 
	 * @param pattern Pattern to test.
	 * @param input User sequence to confront against the pattern.
	 * @param conceptMatch Match between the concepts of the input and the pattern.
	 * @param hierarchyRepresentation Hierarchy representation of the ontology.
	 */
	/*public PropertyMatcher( final Pattern pattern, final UserSequence input, final ArrayList<Integer> conceptMatch, final HierarchyRepresentation<Integer> hierarchyRepresentation )
	{
		super( pattern, input, hierarchyRepresentation );
		this.conceptMatch = conceptMatch;
	}*/
        
        public PropertyMatcher( final Motif pattern, final Sequence input, final ArrayList<Integer> conceptMatch, final OntoRepresentation hierarchyRepresentation )
	{
            super( pattern, input, hierarchyRepresentation );
            this.conceptMatch = conceptMatch;
	}
	
	//---------------------------------------------------------- Public methods
	
	/**
	 *  Indicate if a match between the properties has been found, regarding to the match of the concepts.
	 *  
	 * @return true if, and only if, a match has been found between the properties.
	 */
	public boolean find( )
	{
		if( nbPropertiesValid( ) )
		{
			return allPatternPropertiesMatched( );
		}
		
		return false;
	}
	
	//--------------------------------------------------------- Private methods
	// Indicate if all the input properties can be matched against the pattern properties
	private boolean allPatternPropertiesMatched( )
	{
		// Get the properties, presents in the input sequence, between the concepts which have been matched
		// This hash-map is made in order to have the same key values as those of the pattern
		HashMap< Pair<Integer, Integer>, ArrayList<Integer> > inputPropertiesByPositions = getInputProperties( );
		
		// Get the properties in the pattern
		HashMap< Pair<Integer, Integer>, ArrayList<Integer> > patternPropertiesByPositions = pattern.getProperties( );
		
		// Search if the set of properties in the pattern is matched in the set of properties of the input 
		// having the same positions as key
		Set< Pair<Integer, Integer> > positionsSet = patternPropertiesByPositions.keySet( );
		for ( Pair<Integer, Integer> positions : positionsSet )
		{
			// Get the set of properties of the pattern and the input for these positions
			ArrayList<Integer> patternProperties = patternPropertiesByPositions.get( positions );
			ArrayList<Integer> inputProperties =inputPropertiesByPositions.get( positions );
			
			if( ! PropertyMatcher.matchExists( patternProperties, inputProperties, hierarchyRepresentation ) )
			// At least one property has not been matched
			{
				return false;
			}
		}
		
		// All the sets of properties have been matched
		return true;
	}
	
	// Return the properties present between the concept positions that have matched the pattern
	//
	// Warning : the key put in the hash map don't represent the concept positions in the input sequence,
	// but the concept positions in the concept match
	private HashMap< Pair<Integer, Integer>, ArrayList<Integer> > getInputProperties( )
	{
		// Final result
		HashMap< Pair<Integer, Integer>, ArrayList<Integer> >  propertiesByPositions = new HashMap<Pair<Integer, Integer>, ArrayList<Integer>>( );
		
		int conceptMatchSize = conceptMatch.size( );
		for( int i =0; i < conceptMatchSize - 1; i++ )
		{
			// Get the source position in the input sequence (regarding to the match)
			Integer sourcePosition = conceptMatch.get( i );
			
			for( int j = i + 1; j < conceptMatchSize; j++ )
			{
				// Get the target position in the input sequence (regarding to the match)
				Integer targetPosition = conceptMatch.get( j );
				
				// Get the properties between these positions
				ArrayList<Integer> properties = input.getPropertiesByConceptPositions( sourcePosition, targetPosition );
				
				// Add them to the hash map
				Pair<Integer, Integer> positions = new Pair<Integer, Integer>( i, j );
				propertiesByPositions.put( positions, properties );
			}
		}
		return propertiesByPositions;
	}
	
	// Indicate if the number of properties between a source and a target concept (in the pattern) is <=
	// than the number of properties between the source match and the target match (in the input sequence)
	private boolean nbPropertiesValid( )
	{
		int patternSize = pattern.nbConcepts( );
		
		// Explore the pattern and the input in order to compare their properties
		for ( int srcPositionPattern = 0; srcPositionPattern < patternSize - 1; srcPositionPattern++ )
		{
			// Get the position of the concept source in the input sequence (regarding to the match)
			Integer srcPositionInput = conceptMatch.get( srcPositionPattern );
			
			// Check for all the pairs if the number of properties in the pattern is <=
			// than the number of properties in the input
			for( int targetPositionPattern = srcPositionPattern + 1; targetPositionPattern < patternSize; targetPositionPattern++  )
			{
				// Get the position of the target in the input sequence (regarding to the match)
				Integer targetPositionInput = conceptMatch.get( targetPositionPattern );
				
				// Get the number of properties between these positions
				int nbPatternProperties = pattern.nbProperties( srcPositionPattern, targetPositionPattern );
				int nbInputProperties = input.nbProperties( srcPositionInput, targetPositionInput );
				
				if( ( nbPatternProperties - nbInputProperties ) > 0 )
				// If the pattern have (strictly) more properties than the input
				{
                                    return false;
				}
			}
		}
		return true;
	}
        
        private static boolean matchExists( final ArrayList<Integer> patternProperties, final ArrayList<Integer> inputProperties, final OntoRepresentation hierarchyRepresentation )
	{
		// Stopping conditions
		if( patternProperties.isEmpty( ) )
		{
			return true;
		}
		else if( inputProperties.isEmpty( ) )
		{
			return false;
		}
		
		// Search if a match can be found
		for ( Integer propertyToMatch : patternProperties ) 
		{
			Integer propertyToConfront = inputProperties.get( 0 );
			ArrayList<Integer> subPattern;
			ArrayList<Integer> subInput;
			
			if( hierarchyRepresentation.isPropertyEqualOrDescendant(propertyToMatch, propertyToConfront) )
			// If the property match
			{
				// Find matches of the next pattern properties in the input properties
				subPattern = BaseMatcher.subArrayList( patternProperties, 1,  patternProperties.size( ) );
				subInput =  new ArrayList<>( inputProperties );
			}
			else
			{
				// Find matches of the same pattern concepts in the next input concepts
				subPattern = new ArrayList<>( patternProperties );
				subInput = BaseMatcher.subArrayList( inputProperties, 1,  inputProperties.size( ) );
			}
			return PropertyMatcher.matchExists( subPattern, subInput, hierarchyRepresentation );
		}
		// Not reachable code
		return false;
	}
}
