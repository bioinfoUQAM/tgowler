package legacy;

/**
 * Pair represents a pair of elements of (eventually) two different types.<br/>
 * The types can be the same, they do not need to be different.
 * 
 * @param <A> The type of the first element of the pair.
 * @param <B> The type of the second element of the pair.
 * 
 * @author Frapin Kevin
 */
public class Pair<A, B>
{
	//-------------------------------------------------------------- Attributes
    private A first;
    private B second;

	//------------------------------------------------------------- Constructor
    /**
     * Creates a pair of elements containing the specified elements.
     * 
     * @param first The first element of this pair.
     * @param second The second element of this pair.
     */
    public Pair( A first, B second )
    {
        super( );
        this.first = first;
        this.second = second;
    }

	//------------------------------------------------------- Getters / Setters
    /**
     * Gets the first element of the pair.
     * 
     * @return The first element of this pair.
     */
    public A getFirst( )
    {
        return first;
    }

    /**
     * Gets the second element of this pair.
     * 
     * @return The second element of this pair.
     */
    public B getSecond( )
    {
        return second;
    }
    
    public void setFirst(A first){
        this.first = first;
    }
    
    public void setSecond(B second){
        this.second = second;
    }
    
    //-------------------------------------------------- Auto-generated methods
    public int hashCode( )
    {
        int hashFirst = first != null ? first.hashCode( ) : 0;
        int hashSecond = second != null ? second.hashCode( ) : 0;

        return ( hashFirst + hashSecond ) * hashSecond + hashFirst;
    }

    @SuppressWarnings("unchecked")
    public boolean equals( Object other )
    {
        if ( other instanceof Pair )
        {
            Pair<A, A> otherPair = ( Pair ) other;
            return ( ( this.first == otherPair.first || ( this.first != null
                    && otherPair.first != null && this.first
                    .equals( otherPair.first ) ) ) && ( this.second == otherPair.second || ( this.second != null
                    && otherPair.second != null && this.second
                    .equals( otherPair.second ) ) ) );
        }

        return false;
    }

    public String toString( )
    {
        return "(" + first + ", " + second + ")";
    }

    public void setSecond(int realNextObject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}


