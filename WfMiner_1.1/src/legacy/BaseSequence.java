package legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import legacy.Pair;

/**
 * BaseSequence represents a basic sequence of transactions :
 		<ul>
 * 			<li>A list of concepts</li>
 * 			<li>The properties between the concepts</li>
 * 		</ul>
 * 
 * @param <A> The type of the transactions contained in the sequence.
 * 
 * @author Frapin Kevin
 */
public abstract class BaseSequence<A>
{
	//-------------------------------------------------------------- Attributes
	// List of the transactions
	protected ArrayList<ArrayList<A>> transactions;
	
	//	Map used to retrieve link  via a pair of element positions <source, target>
	// (The link are between transactions present in the list 'transactions')
	protected HashMap< Pair<Integer,Integer>, ArrayList<A> > links;

	
	//------------------------------------------------------------- Constructor
	/**
	 * Creates an empty base sequence.
	 */
	public BaseSequence( )
	{
		this.transactions = new ArrayList<ArrayList<A>>( );
		this.links = new HashMap< Pair<Integer,Integer>, ArrayList<A> >( );
	}
	
	/**
	 * Creates a base sequence by copy.
	 * 
	 * @param baseSequence The base sequence to copy.
	 */
	public BaseSequence( BaseSequence<A> baseSequence )
	{
		this.transactions = new ArrayList<ArrayList<A>>( baseSequence.transactions );
		copyLinks( baseSequence );
	}
	
	//------------------------------------------------------- Getters / Setters
	/**
	 * Gets all transactions contained in the base sequence.
	 * 
	 * @return List of all the transactions.
	 */
	protected ArrayList<ArrayList<A>> getTransctions( ) 
	{
		return transactions;
	}
        
        /**
	 * Gets all elements contained in the base sequence.
	 * 
	 * @return List of all the transactions.
	 */
	protected ArrayList<A> getElements( ) 
	{       
                ArrayList<A> elements = new ArrayList<A>();
                for (ArrayList<A> transaction : transactions) { 	
                    for (A element : transaction) { 
                        elements.add(element); 	
                    }	
                }
		return elements;
	}
        
        
        /**
	 * Gets all elements contained in the one transaction.
	 * 
	 * @return List of all the transactions.
	 */
	protected ArrayList<A> getLastElements( ) 
	{       
                ArrayList<A> last_transaction = transactions.get(transactions.size( ) - 1 );   
		return last_transaction;
	}
	
	/**
	 * Gets the last element of the base sequence.
	 * 
	 * @return Last element.
	 */
	protected A getLastElement( )
	{       
                ArrayList last_transaction = transactions.get(transactions.size( ) - 1 );
		return (A) last_transaction.get( last_transaction.size( ) - 1 );
	}
	
        
	/**
	 * Gets the existing links between the elements. <br/>
	 * The links are indexed by the subject and object positions.
	 * 
	 * @return The lists of links indexed by subject and object positions.
	 */
	protected HashMap< Pair<Integer,Integer>, ArrayList<A> > getLinks( )
	{
		return links;
	}
	
	/**
	 * Gets the existing links between the subject and object positions in the sequence.
	 * 
	 * @param subjectPosition Position of the subject in the sequence.
	 * @param objectPosition Position of the object in the sequence.
	 * 
	 * @return The list of properties between the positions.
	 */
	protected ArrayList<A> getLinksByElementPositions( final Integer subjectPosition, final Integer objectPosition ) 
	{
		Pair<Integer,Integer> conceptPositions = new Pair<Integer,Integer>( subjectPosition, objectPosition );
		ArrayList<A> propertiesByPositions = links.get( conceptPositions );
		return ( propertiesByPositions == null ? new ArrayList<A>( ) : propertiesByPositions ) ;
	}
	
	/**
	 * Sets the links between subject and object positions in the sequence.
	 * 
	 * @param links Links to set.
	 */
	protected void setLinks( final HashMap< Pair<Integer,Integer>, ArrayList<A> > links )
	{
		this.links = links;
	}
	
	//---------------------------------------------------------- Protected methods
	/**
	 * Add a link between the positions of the subject  and the object.
	 * 
	 * @param subjectPosition Position of the subject in the sequence.
	 * @param objectPosition Position of the object in the sequence.
	 * @param link Link to add between the subject and object positions.
	 */
	protected void addLink( final Integer subjectPosition, final Integer objectPosition, final A link )
	{
		// Addition of the property in the hash map
		Pair<Integer,Integer> conceptPositions = new Pair<Integer,Integer>( subjectPosition, objectPosition );
		ArrayList<A> associatedProperties = links.get( conceptPositions );
		if( associatedProperties == null )
		// If no properties has been mapped with these concept positions before
		{
			associatedProperties = new ArrayList<A>( );
			links.put( conceptPositions, associatedProperties );
		}
		associatedProperties.add( link );
	}
	
	/**
	 * Adds an element at the end of the sequence.
	 * 
	 * @param element Element to append.
	 */
	protected void appendElement( final A element )
	{
		// Addition of the concept in the list of concepts
//                System.out.println(transactions);
                ArrayList last_transaction = transactions.get(transactions.size( ) - 1 );
		last_transaction.add( element );
	}
        
        /**
	 * Adds an element at the end of the sequence.
	 * 
	 * @param element Element to append.
	 */
	protected void addElement( final A element )
	{
		// Addition of the concept in the list of concepts
                ArrayList<A> one_transaction = new ArrayList<A>();
		one_transaction.add( element );
                transactions.add(one_transaction);
	}
	
	/**
	 * Gets the number of transactions present in the sequence.
	 * 
	 * @return Size of the sequence.
	 */
	public int nbElements( )
	{
		return transactions.size( );
	}
	
	/**
	 * Gets the number of links present between the subject position and the object position.
	 * 
	 * @param subjectPosition Position of the subject in the sequence. 
	 * @param objectPosition Position of the object in the sequence. 
	 * 
	 * @return The number of links between the subject and object positions.
	 */
	public int nbLinks( final Integer subjectPosition, final Integer objectPosition  )
	{
		return ( getLinksByElementPositions( subjectPosition, objectPosition ).size( ) );
	}

	/**
	 * Replaces the last element of the sequence by the new element.
	 * 
	 * @param newElement Element used to replace the last element.
	 */
	protected void replaceLastElement( final A newElement )
	{
		// Specialization of the concept in the list of concepts
                ArrayList last_transaction = transactions.get(transactions.size( ) - 1 );
		last_transaction.remove( last_transaction.size( ) - 1 );
		last_transaction.add( newElement );
	}
	
	/**
	 * Replace the last added link, between the subject and object positions, by the new link.
	 * 
	 * @param subjectPosition Position of the subject in the sequence.
	 * @param objectPosition Position of the object in the sequence.
	 * @param newLink Link used to replace the last added link. 
	 */
	protected void replaceLastAddedLink( final Integer subjectPosition, final Integer objectPosition, final A newLink )
	{
		// Specialization of the property in the hash map
		ArrayList<A> associatedProperties = getLinksByElementPositions( subjectPosition, objectPosition );
		associatedProperties.remove( associatedProperties.size( ) - 1 );
		associatedProperties.add( newLink );
	}
	
	//--------------------------------------------------------- Private methods
	private void copyLinks( BaseSequence<A> baseSequence )
	{
		this.links = new HashMap<Pair<Integer,Integer>, ArrayList<A>>( );
		Iterator< Pair<Integer,Integer> > it = baseSequence.links.keySet( ).iterator( );
		while( it.hasNext( ) )
		{
			Pair<Integer,Integer> originalKey = it.next( );
			Pair<Integer,Integer> copyKey = new Pair<Integer,Integer>( originalKey.getFirst( ), originalKey.getSecond( ) );
			
			ArrayList<A> originalValue = baseSequence.links.get( originalKey );
			ArrayList<A> copyValue = new ArrayList<A>( originalValue );
			this.links.put( copyKey , copyValue );
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((transactions == null) ? 0 : transactions.hashCode());
		result = prime * result
				+ ((links == null) ? 0 : links.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseSequence<A> other = (BaseSequence) obj;
		if (transactions == null) {
			if (other.transactions != null)
				return false;
		} else if (!transactions.equals(other.transactions))
			return false;
		if (links == null) {
			if (other.links != null)
				return false;
		} else if (!links.equals(other.links))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "BaseSequence [concepts=" + transactions + ", properties="
				+ links + "]";
	}
}
