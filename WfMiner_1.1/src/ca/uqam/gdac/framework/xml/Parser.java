package ca.uqam.gdac.framework.xml;

import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import legacy.RawUserWorkflow;
import java.util.ArrayList;

class Parser 
{
	//-------------------------------------------------------------- Attributes
	// XML reader used to parse the XML
	private XMLReader xmlReader;
	
	// Handler used to extract the sequences
	private Handler sequencesHandler;
	
	//------------------------------------------------------------- Constructor
	public Parser( )
	throws SAXException
	{
                final String property = "http://apache.org/xml/properties/input-buffer-size";

                
		// Get an XML reader able to parse an XML containing sequences
		xmlReader = XMLReaderFactory.createXMLReader( "org.apache.xerces.parsers.SAXParser" );
		sequencesHandler = new Handler( );
		xmlReader.setContentHandler( sequencesHandler );
                xmlReader.setProperty(property, new Integer(16000000));
                System.out.println("xmlReader input-buffer-size: " + xmlReader.getProperty(property));
                
	}
	
	//---------------------------------------------------------- Public methods
	// Extract the sequences described in the XML file
	public ArrayList<RawUserWorkflow> extractRawUserSequences( final String fileURI ) 
	throws SAXException, IOException
	{
		// NOTE : Test the document against a DTD/XML Schema in order to see
		// if it is a valid XML ?
		
		// Parse the XML and make calls to the handler
//                System.out.println("DEBUG: Get parsing results ...");
                parse( fileURI );
		
		// Get the work done by the handler during the parsing
		return sequencesHandler.getParseResult( );
	}
	
	//---------------------------------------------------------- Private methods
	private void parse( final String uri )
	throws SAXException, IOException, NumberFormatException
	{   
//                System.out.println("DEBUG: xmlReader ...");
		xmlReader.parse( uri );
//                System.out.println("DEBUG: xmlReader.parse SUCESS.");
	}

}
