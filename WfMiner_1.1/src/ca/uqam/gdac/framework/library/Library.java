package ca.uqam.gdac.framework.library;

import java.util.ArrayList;
import ontopatternmatching.Motif;
import ontopatternmatching.Sequence;

/**
 * Library contains the user sequences to mine and the frequent patterns extract from the mining.<br/>
 * This class used by the Miner class.
 * 
 * @author Frapin Kevin
 *
 */
public class Library 
{
	//-------------------------------------------------------------- Attributes
	//private static ArrayList<UserSequence> userSequences;
        private static ArrayList<Sequence> userSequences2;
	//private static ArrayList<Pattern> frequentPatterns;
	public static ArrayList<Motif> frequentPatterns2;
	//------------------------------------------------------------- Constructor
	
	//------------------------------------------------------- Setters / Getters
	/**
	 * Gets the frequent patterns extract from by the Miner.
	 * 
	 * @return All the extracted frequent patterns.
	 */
	/*public static ArrayList<Pattern> getFrequentPatterns( )
	{
		return frequentPatterns;
	}*/
        
        public static ArrayList<Motif> getFrequentPatterns2( )
	{
		return frequentPatterns2;
	}
        
	
	/**
	 * Indicates the number of user sequences loaded by the Miner.
	 * 
	 * @return The number of user sequences.
	 */
	public static Integer getNbUserSequences( )
	{
		return userSequences2.size( );
	}
		
	/**
	 * Gets all the user sequences loaded by the Miner.
	 * 
	 * @return User sequences used for the mining.
	 */
	/*public static ArrayList<UserSequence> getUserSequences( )
	{
		return Library.userSequences;
	}*/
	
        public static ArrayList<Sequence> getUserSequences2( )
	{
		return Library.userSequences2;
	}
	
        
	/**
	 * Sets the user sequences used by the Miner to realize the mining.
	 * 
	 * @param userSequences User sequences used by the Miner.
	 */
	/*public static void setUserSequences( final ArrayList<UserSequence> userSequences )
	{
		Library.userSequences = userSequences;
	}*/
        
        public static void setUserSequences2( final ArrayList<Sequence> userSequences )
	{
		Library.userSequences2 = userSequences;
	}
	
	//---------------------------------------------------------- Public methods
	/**
	 * Adds a frequent pattern to the set of frequent pattern.
	 * 
	 * @param frequentPattern Frequent pattern found.
	 */
	public static void addFrequentPattern( final Motif frequentPattern ){
            frequentPatterns2.add(frequentPattern);
	}
        
        /*public static void addFrequentPattern( final Pattern frequentPattern ){
            frequentPatterns.add(frequentPattern);
	}*/
	
	/**
	 * Initializes the Library in order to use it for a mining phase.
	 */
	public static void initialize( )
	{
		//frequentPatterns = new ArrayList<>( );
                frequentPatterns2 = new ArrayList<>();
	}
	
}
