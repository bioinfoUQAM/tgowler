package ca.uqam.gdac.framework.xml;

enum Tag 
{
	//---------------------------------------------------------------- XML Tags
	TRANSACTION("TRANSACTION" ),
        TRIPLET( "TRIPLET" ),
        ITEM( "ITEM" ),
	NAMESPACE( "NAMESPACE" ),
	WORKFLOWS( "WORKFLOWS" ),
	WORKFLOW( "WORKFLOW" );
	
	private String symbol;
	
	//----------------------------------------------------------------- Methods
	private Tag( String symbol )
	{
		this.symbol = symbol;
	}
	
	
	public String getSymbol( )
	{
		return symbol;
	}
	
}
