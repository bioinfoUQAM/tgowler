package ca.uqam.gdac.framework.matcher;

import java.util.ArrayList;
import ontologyrep2.OntoRepresentation;
import ontopatternmatching.Motif;
import ontopatternmatching.Sequence;


/**
 * Matcher represents the match of an input user sequence against a pattern.<br/>
 * This match is based on the concepts sequence and the properties.
 * 
 * @author Frapin Kevin
 *
 */
public class Matcher extends BaseMatcher
{
	//-------------------------------------------------------------- Attributes
	public ConceptMatcher conceptMatcher;
	
	public ArrayList<Integer> match;
        
        public static Integer lastMatch;
	
	//------------------------------------------------------------- Constructor
	/**
	 * Creates a new Matcher using the hierarchyRepresentation to find a match between the input sequence and the pattern.
	 * 
	 * @param pattern Pattern to test.
	 * @param input User sequence to confront against the pattern.
	 * @param hierarchyRepresentation Hierarchy representation of the ontology.
	 */
	/*public Matcher( final Pattern pattern, final UserSequence input, final HierarchyRepresentation<Integer> hierarchyRepresentation )
	{
		super( pattern, input, hierarchyRepresentation );
		this.conceptMatcher = pattern.conceptMatcher( input, hierarchyRepresentation );
		this.match = new ArrayList<Integer>( );
	}*/
        
        public Matcher( final Motif pattern, final Sequence input, final OntoRepresentation hierarchyRepresentation )
	{
		super( pattern, input, hierarchyRepresentation );
		this.conceptMatcher = pattern.conceptMatcher( input, hierarchyRepresentation );
		this.match = new ArrayList<Integer>( );
	}
        
        public void reset(Sequence input){
            this.match.clear();//vide l'arraylist
            this.conceptMatcher.reset(input);
            this.input = input;
        }
	
	//--------------------------------------------------- Public static methods
	
	// 
	// 
	/**
	 * Indicate if a match has been found between the pattern and the input sequence.
	 * The match is between the concepts and the properties.
	 * 
	 * @return true if, and only if, a match has been found between the input sequence and the pattern.
	 */
	public boolean find( )
	{
		while( conceptMatcher.find( ) )
		// While a concept match is found
		{
                    //System.out.println("concept found");
                    //System.out.println("still matching concepts...");
                    PropertyMatcher propertyMatcher = conceptMatcher.propertyMatcher( );
                    if( propertyMatcher.find( ) )
                    // If this concept match works for the properties too
                    {
                        //System.out.println("property found");   
                        // Save this current match
                            lastMatch=conceptMatcher.group().get(conceptMatcher.group().size()-1);
                            match = conceptMatcher.group( );
                            return true;
                    }else{
                        //System.out.println("property not found");
                    }
		}
		
		// No match has been found
		return false;
	}
	
	/**
	 * Retrieves the match between the concepts sequences of the input and the pattern.
	 * 
	 * @return Match between input and pattern concepts.
	 */
	public ArrayList<Integer> group( )
	{
		return match;
	}

    public static Integer getLastMatch() {
        return lastMatch;
    }
        
        
	
}
