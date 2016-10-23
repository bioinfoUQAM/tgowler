package ca.uqam.gdac.framework.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import legacy.RawUserSequence;
import java.util.ArrayList;


// Handler used to extract sequences into an XML document
//
// The structure of the XML document has to be like this :
// SEQUENCES :: SEQUENCE*
// SEQUENCE :: INSTANCE*
// INSTANCE :: [instance_name]*
//
// See an example in the file 'file-tests/sequencesTest.xml'
class Handler implements ContentHandler
{
	//-------------------------------------------------------------- Attributes
	// Field used to know which is the current tag
	private Tag currentTag;
	
	// The final set of sequences extracted during the parsing
	private ArrayList<RawUserSequence> parseResult;
	
	// Field used to store the instances of the current sequence
	private RawUserSequence currentSequence;
	
	// Filed used to prevent a parsing bug
	// It is used to know if the last individual tag has been closed
	// Because sometimes the parser seems to find an opening tag which is not
	private static boolean firstTagProccessing;
	private static String lastLocalName = "";
	
	//------------------------------------------------------------- Constructor
	public Handler( )
	{
		super( );
		parseResult = new ArrayList<RawUserSequence>( );
	}
	
	//------------------------------------------------------- Getters / Setters
	public ArrayList<RawUserSequence> getParseResult( )
	{
		return parseResult;
	}
	
	//---------------------------------------------------------- Public methods
	@Override
	// Method called to read a tag content
	public void characters( char[ ] ch, int start, int length )
	{
		switch( currentTag )
		{
			case INDIVIDUAL:
				processIndividual( ch, start, length );
				firstTagProccessing = false;
				break;
			
			case SEQUENCES:
			case SEQUENCE:
			default:
				// Ignore these characters
				break;
		}
	}
	
	@Override
	// Method called when the parser has found a closing tag
	public void endElement( String uri, String localName, String qName )
	{
		switch( currentTag )
		{
			case SEQUENCES:
				// End of the XML file
				break;
				
			case SEQUENCE:
				// End of the sequence tag
				parseResult.add( currentSequence );
				// Return to the parent tag
				currentTag = Tag.SEQUENCES;
				break;
				
			case INDIVIDUAL:
				// End of the instance tag
				// Return to the parent tag
				currentTag = Tag.SEQUENCE;
				break;
				
			default:
				break;
		}
	}
	
	@Override
	// Method called when the parser has found an opening tag
	public void startElement( String uri, String localName, String qName, Attributes atts )
	throws SAXException 
	{
		if( equalsIgnoreCase( qName, Tag.SEQUENCES )  )
		// If this is a 'sequences' tag
		{
			currentTag = Tag.SEQUENCES;
		}
		else if( equalsIgnoreCase( qName, Tag.SEQUENCE ) )
		// If this is a 'sequence' tag
		{
			// Addition of a new sequence to the set of sequences
			currentSequence = new RawUserSequence( );
			currentTag = Tag.SEQUENCE;
		}
		else if( equalsIgnoreCase( qName, Tag.INDIVIDUAL ) )
		// If this is a 'instance' tag
		{
			firstTagProccessing = true;
			currentTag = Tag.INDIVIDUAL;
		}
		else
		// If this is an unknown tag
		{
			throw new SAXException( "Not expected opening tag : " + qName );
		}
	}
	
	
	//--------------------------------------------------------- Private methods
	// Extract the instance from the characters and 
	// add it to the current sequence
	private void processIndividual( final char[ ] ch, final int start, final int length  )
	{
		// Extract the instance
		String individualStr = extractString( ch, start, length );
		
		// Following code prevents a parsing bug
		if( firstTagProccessing )
		// If it is a new individual local name
		{
			// Insert a new individual local name
			lastLocalName = individualStr;
			currentSequence.appendIndividualLocalName( lastLocalName );
		}
		else
		// The string is the continuation of the last individual local name
		{
			// Complete the last local name added
			lastLocalName += individualStr;
			currentSequence.replaceIndividualLocalName( lastLocalName );
		}

	}
	
	// Make a string from data in an array
	private static String extractString( final char[ ] ch, final int start, final int length )
	{
		return ( new String( ch, start, length ) );
	}
	

	//-------------------------------------------------- Private static methods
	// Indicate if a string is equals to a tag symbol
	private static boolean equalsIgnoreCase( final String input, final Tag tag )
	{
		return ( input.equalsIgnoreCase( tag.getSymbol( ) ) );
	}
	
	
	//------------------------------------------------- Not implemented methods
	@Override
	public void endDocument( ) throws SAXException 
	{
	}

	@Override
	public void endPrefixMapping( String arg0 ) throws SAXException 
	{
	}

	@Override
	public void ignorableWhitespace( char[] arg0, int arg1, int arg2 )
			throws SAXException 
	{
	}

	@Override
	public void processingInstruction(String arg0, String arg1)
			throws SAXException 
	{
	}

	@Override
	public void skippedEntity( String arg0 ) throws SAXException 
	{
	}

	@Override
	public void startDocument( ) throws SAXException 
	{
	}

	@Override
	public void startPrefixMapping( String arg0, String arg1 )
			throws SAXException 
	{
	}

	@Override
	public void setDocumentLocator(Locator locator) 
	{
	}
	
}
