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
        private static ArrayList<Sequence> userWorkflows;
	public static ArrayList<Motif> frequentPatterns;
	//------------------------------------------------------------- Constructor
	
	//------------------------------------------------------- Setters / Getters
        
        public static ArrayList<Motif> getFrequentPatterns2( )
	{
		return frequentPatterns;
	}
        
	
	/**
	 * Indicates the number of user sequences loaded by the Miner.
	 * 
	 * @return The number of user sequences.
	 */
	public static Integer getNbUserSequences( )
	{
		return userWorkflows.size( );
	}
		
	/**
	 * Gets all the user sequences loaded by the Miner.
	 * 
	 * @return User sequences used for the mining.
	 */
        public static ArrayList<Sequence> getUserSequences2( )
	{
		return Library.userWorkflows;
	}
	
        
	/**
	 * Sets the user sequences used by the Miner to realize the mining.
	 * 
	 * @param userSequences User sequences used by the Miner.
	 */
        public static void setUserWorkflows( final ArrayList<Sequence> userSequences )
	{
		Library.userWorkflows = userSequences;
	}
	
	//---------------------------------------------------------- Public methods
	/**
	 * Adds a frequent pattern to the set of frequent pattern.
	 * 
	 * @param frequentPattern Frequent pattern found.
	 */
	public static void addFrequentPattern( final Motif frequentPattern ){
            frequentPatterns.add(frequentPattern);
	}
	
	/**
	 * Initializes the Library in order to use it for a mining phase.
	 */
	public static void initialize( )
	{
                frequentPatterns = new ArrayList<>();
                userWorkflows = new ArrayList<>();
	}
	
}
