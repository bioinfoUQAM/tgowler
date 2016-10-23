package ca.uqam.gdac.framework.xml;

enum Tag 
{
	//---------------------------------------------------------------- XML Tags
	INDIVIDUAL	( "INDIVIDUAL" ),
	NAMESPACE	( "NAMESPACE" ),
	SEQUENCES 	( "SEQUENCES" ),
	SEQUENCE 		( "SEQUENCE" );
	
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
