package ca.uqam.gdac.framework.xml;

import java.io.IOException;

import org.xml.sax.SAXException;

import legacy.RawUserWorkflow;
import java.util.ArrayList;

/**
 * WorkflowFactory represents a factory used to create raw user sequences from an XML file.
 * 
 * @author Frapin Kevin
 */
public abstract class WorkflowFactory
{
	//--------------------------------------------------- Public static methods
	/**
	 * Creates raw user sequences from an XML file representing.
	 * 
	 * @param fileURI The URI of the XML file containing raw user sequences.
	 * 
	 * @return The raw users sequences loaded from the XML file.
	 * 
	 * @throws SAXException Any SAX exception, possibly wrapping another exception.
	 * @throws IOException An IO exception from the parser, possibly from a byte stream or character stream supplied by the application.
	 */
	public static ArrayList<RawUserWorkflow> createRawUserWorkflows( final String fileURI ) 
	throws SAXException, IOException
	{
		// Get a parser to extract the sequences
		Parser parser = new Parser( );
		return parser.extractRawUserWorkflows( fileURI );
	}
	
	
}
