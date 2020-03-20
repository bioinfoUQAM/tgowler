package ca.uqam.gdac.framework.matcher;

import java.util.ArrayList;
import ontologyrep20.OntoRepresentation;
import ontopatternmatching.Motif;
import ontopatternmatching.Sequence;




/**
 * ConceptMatcher represents the match of an input sequence against a pattern.
 * This match is based on the concepts sequence.
 * 
 * @author Frapin Kevin
 *
 */
public class ConceptMatcher extends BaseMatcher
{
	//-------------------------------------------------------------- Attributes
	// Current match of the sequence against the pattern
	protected ArrayList<Integer> match;
        public static Integer lastMatch;
	
	// Indicate if the matcher has already been launched in order to find a match
	private boolean launched;
	
	//------------------------------------------------------------- Constructor
	
	/**
	 * Creates a new ConceptMatcher using the hierarchyRepresentation to find a match between the concepts of the input and the pattern.
	 * 
	 * @param pattern Pattern to test.
	 * @param input User sequence to confront against the pattern.
	 * @param hierarchyRepresentation Hierarchy representation of the ontology.
	 */
	/*public ConceptMatcher( final Pattern pattern, final UserSequence input, final HierarchyRepresentation<Integer> hierarchyRepresentation )
	{
		super( pattern, input, hierarchyRepresentation );
		this.match = new ArrayList<Integer>( );
		this.launched = false;
		this.hierarchyRepresentation = hierarchyRepresentation;
	}*/
        
        public ConceptMatcher( Motif pattern, final Sequence input, final OntoRepresentation hierarchyRepresentation )
	{
		super( pattern, input, hierarchyRepresentation );
		this.match = new ArrayList<>( );
		this.launched = false;
		this.hierarchyRepresentation = hierarchyRepresentation;
	}
        
        public void reset(Sequence input){
            this.launched = false;
            this.match.clear();
            this.input = input;
        }
	
	//---------------------------------------------------------- Public methods
	
	/**
	 * Indicate if a match between the concepts sequence of the pattern and the concepts sequence of the input sequence has been found.
	 * The method starts at the beginning of the input sequence.
	 * If a match has been found before, the method will try to find an other match in the input sequence.
	 * Update the match attribute in order to keep in memory the current match.
	 * 
	 * @return true if, and only if, a match has been found between the concepts sequences.
	 */
	public boolean find( )
	{
		if( areSizesValids( ) )
		// If it might be possible to find a match regarding to the sizes
		{
                    //System.out.println("sizes are valid");
                    //System.out.println("sizes are valid");
                    if( !launched )
                    // If it is the first call of the method
                    {
                        //System.out.println("Not launched.");
                        // Try to find a match starting at the beginning of the input sequence
                        findFirstMatch( );
                        launched = true;
                    }
                    else if( ! match.isEmpty( ) )
                    // If it is not the first call of the method and the current match is not empty
                    {
                        //System.out.println("not the first call and not empty.");
                        // Try to find the next match regarding to the current match
                        findNextMatch( );
                    }
		}
		//System.out.println("sizes are not valid");
		return ( ! match.isEmpty( ) );
	}
	
	/** Return an array list containing the positions of the concepts (in the input sequence) which have matched the pattern during the last call to the 'find' method.
	 * 
	 * @return Positions of the concepts, in the input concept, that matched against the pattern.
	 */
	public ArrayList<Integer> group( )
	{
		return match;
	}
	
	/**
	 * Creates a PropertyMatcher based on the current concept match.
	 * 
	 * @return A new PropertyMatcher based on the current concept match.
	 */
	public PropertyMatcher propertyMatcher(  )
	{
		return ( new PropertyMatcher( pattern, input, match, hierarchyRepresentation ) );
	}
	
	//--------------------------------------------------------- Private methods
	// Check if the the number of concepts and properties into the pattern is <=
	// than the number of concepts and properties into the user sequence
	private boolean areSizesValids( )
	{
		// Test the number of concepts
		// NOTE : Make an other test on the number of properties ?
		if( nbConceptsValid( ) )
		{
			return true;
		}
		return false;
	}
	
	// Try to find a match starting at the beginning of the input sequence
	private void findFirstMatch( )
	{
		match = ConceptMatcher.findMatch( pattern.getAllConcepts( ), input.getConcepts( ), 0, match, hierarchyRepresentation );
	}
	
	// Try to find a match regarding to the current match
	private void findNextMatch( )
	{
	
		Integer patternSize = pattern.nbConcepts( );
		
		for( int i = patternSize - 1; i >= 0; i-- )
		// Try to move the latest positions of the match
		{
                    // Slice the pattern to match (keep only the last concepts)
                    //ArrayList<Integer> subPattern = subArrayList( pattern.getTransactions( ), i, patternSize );

                    // Slice the input sequence (keep only the concepts presents after the start position of the match)
                    int matchStart = match.get( i );
                    //ArrayList<Integer> subInput = ConceptMatcher.subArrayList( input.getTransactions( ), matchStart + 1, input.nbConcepts( ) );

                    // Try to find a new match
                    match.remove( match.size( ) - 1 );
                    ArrayList<Integer> newMatch = ConceptMatcher.findMatch( pattern.getAllConcepts( ), input.getConcepts(), matchStart + 1, match, hierarchyRepresentation, i, matchStart+1);
                    if( ! newMatch.isEmpty( ) )
                    {
                            match = newMatch;
                            return;
                    }
		}
		
		// If no match has been found
		match = new ArrayList<>( );
	}

	
	// Check if the number of concepts into the pattern is <=  
	// than the number of patterns into the user sequence
	private boolean nbConceptsValid(  )
	{
		return  ( pattern.nbConcepts( ) <= input.nbConcepts( ) );
	}

	//-------------------------------------------------- Private static methods

	// Recursive function used to find a match of an input sequence against a pattern
	/*private static ArrayList<Integer> findMatch( final ArrayList<Integer> patternConcepts, final ArrayList<Integer> inputConcepts, final int start, final ArrayList<Integer> currentMatch, final HierarchyRepresentation<Integer> hierarchyRepresentation )
	{
		//  Stopping conditions :
		//  - The pattern is empty
		//  - The input is empty
		if( ConceptMatcher.noMoreSearch( patternConcepts, inputConcepts ) )
		// If a match has been found (even if it is the empty one)
		{
			return ConceptMatcher.matchFound( patternConcepts, inputConcepts, currentMatch );
		}
		
		// Search a match between the input sequence and the pattern
		return ConceptMatcher.searchMatch( patternConcepts, inputConcepts, start, currentMatch, hierarchyRepresentation  );
	}*/
        
        private static ArrayList<Integer> findMatch( final ArrayList<Integer> patternConcepts, final ArrayList<Integer> inputConcepts, final int start, final ArrayList<Integer> currentMatch, final OntoRepresentation hierarchyRepresentation)
	{
		//  Stopping conditions :
		//  - The pattern is empty
		//  - The input is empty
		if( ConceptMatcher.noMoreSearch( patternConcepts, inputConcepts ) )
		// If a match has been found (even if it is the empty one)
		{
                    //System.out.println("A match has been found?");
                    return ConceptMatcher.matchFound( patternConcepts, inputConcepts, currentMatch );
		}
                
		
		// Search a match between the input sequence and the pattern
		return ConceptMatcher.searchMatch( patternConcepts, inputConcepts, start, currentMatch, hierarchyRepresentation);
	}
        
        private static ArrayList<Integer> findMatch( final ArrayList<Integer> patternConcepts, final ArrayList<Integer> inputConcepts, final int start, final ArrayList<Integer> currentMatch, final OntoRepresentation hierarchyRepresentation, int index_pattern, int index_input)
	{
            //  Stopping conditions :
            //  - The pattern is empty
            //  - The input is empty
            if( ConceptMatcher.noMoreSearch( patternConcepts, inputConcepts, index_pattern, index_input ) )
            // If a match has been found (even if it is the empty one)
            {
                //System.out.println("A match has been found?");
                return ConceptMatcher.matchFound( patternConcepts, inputConcepts, currentMatch, index_pattern );
            }

            // Search a match between the input sequence and the pattern
            return ConceptMatcher.searchMatch( patternConcepts, inputConcepts, start, currentMatch, hierarchyRepresentation  , index_pattern, index_input);
	}

	// Check if the match search has to be stopped
	private static boolean noMoreSearch( final ArrayList<Integer> patternConcepts, final ArrayList<Integer> inputConcepts )
	{
            //System.out.println("pcEmpty:"+patternConcepts.isEmpty( ));
            //System.out.println("inputCe:"+inputConcepts.isEmpty( ));
            return ( patternConcepts.isEmpty( ) || inputConcepts.isEmpty( ));
	}
        
        private static boolean noMoreSearch( final ArrayList<Integer> patternConcepts, final ArrayList<Integer> inputConcepts, int index_pattern, int index_input )
	{
            //System.out.println("pcEmpty:"+patternConcepts.isEmpty( ));
            //System.out.println("inputCe:"+inputConcepts.isEmpty( ));
            return ( patternConcepts.isEmpty( ) || inputConcepts.isEmpty( ) || index_pattern>=patternConcepts.size() || index_input>=inputConcepts.size());
	}


	// Return the final result of the match search depending 
	// of the condition which stopped the search
	private static ArrayList<Integer> matchFound( final ArrayList<Integer> patternConcepts, final ArrayList<Integer> inputConcepts, final ArrayList<Integer> currentMatch )
	{
		if( patternConcepts.isEmpty( ) )
		// If the pattern is empty, a match has been found
		{
			return currentMatch;
		}
		// - else -
		// The input is empty, no match has been found
		
		return ( new ArrayList<>( ) );
	}
        
        private static ArrayList<Integer> matchFound( final ArrayList<Integer> patternConcepts, final ArrayList<Integer> inputConcepts, final ArrayList<Integer> currentMatch, int index_pattern )
	{
		if( patternConcepts.isEmpty( ) || index_pattern>=patternConcepts.size())
		// If the pattern is empty, a match has been found
		{
			return currentMatch;
		}
		// - else -
		// The input is empty, no match has been found
		
		return ( new ArrayList<>( ) );
	}
	
	// Method used to search a match of an input sequence against a pattern
	/*private static ArrayList<Integer> searchMatch( final ArrayList<Integer> patternConcepts, final ArrayList<Integer> inputConcepts, final int start, final ArrayList<Integer> currentMatch, final HierarchyRepresentation<Integer> hierarchyRepresentation )
	{
		ArrayList<Integer> match = new ArrayList<Integer>( currentMatch );
		
		for ( Integer conceptToMatch : patternConcepts ) 
		{
			Integer conceptToConfront = inputConcepts.get( 0 );
			ArrayList<Integer> subPattern;
			ArrayList<Integer> subInput;
			
			if( hierarchyRepresentation.isConceptEqualOrDescendant( conceptToMatch, conceptToConfront ) )
			// If the concepts match
			{
				// Find matches of the next pattern concepts in the next input concepts
				subPattern = BaseMatcher.subArrayList( patternConcepts, 1,  patternConcepts.size( ) );
				subInput = BaseMatcher.subArrayList( inputConcepts, 1,  inputConcepts.size( ) );
				
				// Indicate the position of the input concept that have matched
				match.add( start );
			}
			else
			{
				// Find matches of the same pattern concepts in the next input concepts
				subPattern = new ArrayList<Integer>( patternConcepts );
				subInput = BaseMatcher.subArrayList( inputConcepts, 1,  inputConcepts.size( ) );
			}
			return ConceptMatcher.findMatch( subPattern, subInput, start + 1, match, hierarchyRepresentation  );
		}
		// Not reachable code
		return new ArrayList<Integer>( );
	}*/
        
        /**
         * 
         * @param patternConcepts
         * @param inputConcepts
         * @param start
         * @param currentMatch
         * @param hierarchyRepresentation
         * @return 
         */
        private static ArrayList<Integer> searchMatch( final ArrayList<Integer> patternConcepts, final ArrayList<Integer> inputConcepts, final int start, final ArrayList<Integer> currentMatch, final OntoRepresentation hierarchyRepresentation){
            return ConceptMatcher.searchMatch(patternConcepts, inputConcepts, start, currentMatch, hierarchyRepresentation, 0, 0);
        }
        
        /**
         * 
         * @param patternConcepts
         * @param inputConcepts
         * @param start
         * @param currentMatch
         * @param hierarchyRepresentation
         * @param subp_t
         * @param subi_t
         * @return 
         */
        private static ArrayList<Integer> searchMatch( final ArrayList<Integer> patternConcepts, final ArrayList<Integer> inputConcepts, final int start, final ArrayList<Integer> currentMatch, final OntoRepresentation hierarchyRepresentation , int index_pattern, int index_input)
	{
		ArrayList<Integer> match = new ArrayList<>( currentMatch );
		
                //int start_index;
                
                for(int i=index_pattern;i<patternConcepts.size();i++){
                    Integer conceptToMatch = patternConcepts.get(i);
                    Integer conceptToConfront = inputConcepts.get( index_input );
                    //ArrayList<Integer> subPattern;
                    //ArrayList<Integer> subInput;
                    
                    
                    if( hierarchyRepresentation.isConceptEqualOrDescendant( conceptToMatch, conceptToConfront ) )
                    // If the concepts match
                    {
                        //System.out.println("FOUND CONCEPT!!!!!:"+conceptToMatch+"="+conceptToConfront);
                        // Find matches of the next pattern concepts in the next input concepts
                        /**
                         * Enleve le premier item de la liste en fait.
                         */
                        //subPattern = BaseMatcher.subArrayList( patternConcepts, 1,  patternConcepts.size( ) );
                        //subInput = BaseMatcher.subArrayList( inputConcepts, 1,  inputConcepts.size( ) );
                        
                        index_input++;
                        index_pattern++;
                        
                        // Indicate the position of the input concept that have matched
                        match.add( start );
                        
                    }
                    else
                    {
                        //System.out.println("CONCEPT NOT MATCHED!!!");   
                        // Find matches of the same pattern concepts in the next input concepts
                        
                        //subPattern = new ArrayList<>( patternConcepts );
                        //subInput = BaseMatcher.subArrayList( inputConcepts, 1,  inputConcepts.size( ) );
                        
                        index_input++;
                    }
                    return ConceptMatcher.findMatch( patternConcepts, inputConcepts, start + 1, match, hierarchyRepresentation, index_pattern, index_input);                    
                }
//                lastMatch=match.get(match.)
		// Not reachable code
		return new ArrayList<>( );
	}
	
}
