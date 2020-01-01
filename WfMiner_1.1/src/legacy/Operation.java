package legacy;

/**
 * Operation represents the sequence operations :
 * <ul>
 * 		<li>AC : Addition of a Concept</li>
 * 		<li>SC : Specialization of a Concept</li>
 * 		<li>AP : Addition of a Property</li>
 * 		<li>SP : Specialization of a Property</li>
 * </ul>
 * 
 * @author Frapin Kevin
 */
public enum Operation 
{
	//-------------------------------------------------------------- Operations
	AC ("AC"),
	SC ("SC"),
	AP ("AP"),
	SP ("SP");
	
	private String symbol;
	
	//---------------------------------------------------------- Public methods
	/**
	 * Gets the symbol associated to an operation.
	 * 
	 * @return Symbol associated to an operation.
	 */
	public String getSymbol( )
	{
		return symbol;
	}
	
	//--------------------------------------------------------- Private methods
	private Operation( String symbol )
	{
		this.symbol = symbol;
	}
}
