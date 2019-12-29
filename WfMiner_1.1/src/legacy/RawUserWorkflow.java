package legacy;

import java.util.ArrayList;
import java.util.HashMap;

import legacy.Pair;

/**
 * RawUserWorkflow represents a user sequence composed of local names.<br/>
 * The raw user sequence is composed of individuals local names, and properties local names.
 * 
 * @author Frapin Kevin
 */
public class RawUserWorkflow extends BaseSequence<String>
{

	//------------------------------------------------------------- Constructor
	/**
	 * Creates an empty raw user sequence.
	 */
	public RawUserWorkflow( )
	{
		super( );
	}
	
	/**
	 * Creates a raw user sequence from an existing one.
	 * 
	 * @param rawUserSequence Raw user sequence to copy.
	 */
	public RawUserWorkflow( final RawUserWorkflow rawUserSequence )
	{
		super( rawUserSequence );
	}
	
	//------------------------------------------------------- Getters / Setters
	/**
	 * Gets all the individuals local names present in this raw user sequence.
	 * 
	 * @return All the individuals local names.
	 */
	public ArrayList<String> getIndividualsLocalNames( )
	{
		return ( super.getElements( ) );
	}
        
        //------------------------------------------------------- Getters / Setters
	/**
	 * Gets all the individuals local names present in this raw user sequence.
	 * 
	 * @return All the individuals local names.
	 */
	public HashMap< Pair<Integer,Integer>, ArrayList<String> > getLinksLocalNames( )
	{
		return ( super.getLinks( ) );
	}
        
        /**
	 * Gets all the individuals local names present in this raw user sequence.
	 * 
	 * @return All the individuals local names.
	 */
	public ArrayList<String> getIndividualslastLocalNames( )
	{
		return ( super.getLastElements( ) );
	}
        
	
	/**
	 * Gets all properties local names indexed by subject and object positions present in this raw user sequence.
	 * 
	 * @return  All properties local names indexed by subject and object positions.
	 */
	public HashMap< Pair<Integer,Integer>, ArrayList<String> > getPropertiesLocalNames( )
	{
		return ( super.getLinks( ) );
	}
	
	//---------------------------------------------------------- Public methods
	/**
	 * Add an individual local name at the end of this raw user transaction.
	 * 
	 * @param individualLocalName Individual local name to append.
	 */
	public void appendIndividualLocalName( final String individualLocalName  )
	{
		super.appendElement( individualLocalName );
	}
        
        //---------------------------------------------------------- Public methods
	/**
	 * Add an individual local name at the end of this raw user transaction.
	 * 
	 * @param individualLocalName Individual local name to append.
	 */
	public void addIndividualLocalName( final String individualLocalName  )
	{
		super.addElement( individualLocalName );
	}
	
	/**
	 * Add a property local name between the subject and object positions.
	 * 
	 * @param subjectPosition Position of the subject.
	 * @param objectPosition Position of the object.
	 * @param propertyLocalName Property local name to add.
	 */
	public void addPropertyLocalName( final Integer subjectPosition, final Integer objectPosition, final String propertyLocalName )
	{
		super.addLink( subjectPosition, objectPosition, propertyLocalName );
	}
	
	/**
	 * Replaces the last local name appended to the sequence.
	 * @param newIndividualLocalName The local name used to replace the last one of the sequence.
	 */
	public void replaceIndividualLocalName( final String newIndividualLocalName )
	{
		super.replaceLastElement( newIndividualLocalName );
	}
        
        /**
	 * Replaces the last local name appended to the sequence.
	 * @param newIndividualLocalName The local name used to replace the last one of the sequence.
	 */
	public void replaceLastAddedLink( final Integer subjectPosition, final Integer objectPosition, final String newLink )
	{
		super.replaceLastAddedLink( subjectPosition, objectPosition, newLink );
	}
}
