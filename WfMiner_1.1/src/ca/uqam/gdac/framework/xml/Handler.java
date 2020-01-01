package ca.uqam.gdac.framework.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import legacy.RawUserSequence;
import java.util.ArrayList;
import java.util.Arrays; 
import java.util.List;


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
	private RawUserSequence currentWorkflow;
	
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
//            System.out.println("currentTag " + currentTag);
//            System.out.println("parseResult " + parseResult);
//            System.out.println("currentWorkflow " + currentWorkflow);
//            System.out.println("firstTagProccessing " + firstTagProccessing);
//            System.out.println("lastLocalName " + lastLocalName);
//            if 
//            for (int i = 0; i < currentWorkflow.getIndividualsLocalNames().size(); i++) {
//                System.out.println(.get(i));
//            }
//            if (currentWorkflow != null){
//                System.out.println(currentWorkflow.getIndividualsLocalNames());
//                System.out.println(currentWorkflow.getPropertiesLocalNames());
//            }
//            System.out.println();
		switch( currentTag )
		{
                    case TRANSACTION:
                        processIndividual( ch, start, length );
                        firstTagProccessing = false;
                        break;
                    case TRIPLET:
                        processLink( ch, start, length );
                        firstTagProccessing = false;
                        break;
                    case WORKFLOWS:
                    case WORKFLOW:
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
			case WORKFLOWS:
				// End of the XML file
				break;
                                
                        case TRANSACTION:
                                 // End of the instance tag
				// Return to the parent tag
				currentTag = Tag.WORKFLOW;
				break;
				
			case WORKFLOW:
				// End of the sequence tag
				parseResult.add(currentWorkflow );
				// Return to the parent tag
				currentTag = Tag.WORKFLOWS;
				break;
                        case TRIPLET:
				// End of the instance tag
				// Return to the parent tag
				currentTag = Tag.WORKFLOW;
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
		if( equalsIgnoreCase( qName, Tag.WORKFLOWS )  )
		// If this is a 'workflows' tag
		{
			currentTag = Tag.WORKFLOWS;
		}
                else if( equalsIgnoreCase( qName, Tag.TRANSACTION )  )
		// If this is a 'transaction' tag
		{
                        firstTagProccessing = true;
			currentTag = Tag.TRANSACTION;
		}
		else if( equalsIgnoreCase( qName, Tag.WORKFLOW ) )
		// If this is a 'workflow' tag
		{
			// Addition of a new sequence to the set of sequences
			currentWorkflow = new RawUserSequence( );
			currentTag = Tag.WORKFLOW;
		}
                else if( equalsIgnoreCase( qName, Tag.TRIPLET ) )
		// If this is a 'instance' tag
		{
			firstTagProccessing = true;
			currentTag = Tag.TRIPLET;
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
                String individualStrings = extractString( ch, start, length );
//                System.out.println("individualStrings " + individualStrings);
                List<String> items = Arrays.asList(individualStrings.split("\\s*,\\s*"));
//                System.out.println("items " + items);
                
                // Add a transaction with the first element
                if( firstTagProccessing )
                    // If it is a new individual local name
                    {
                        // Insert a new individual local name
                        lastLocalName = items.get(0);
                        currentWorkflow.appendIndividualLocalName(lastLocalName );
//                        System.out.println("currentTransaction: " + currentWorkflow.getIndividualslastLocalNames( ));
                    }
                    else
                    // The string is the continuation of the last individual local name
                    {
                        // Complete the last local name added
                        lastLocalName = items.get(0);
                        currentWorkflow.replaceIndividualLocalName( lastLocalName );
//                        System.out.println("currentTransaction: " + currentWorkflow.getIndividualslastLocalNames( ));
                    }
                
                
                // Append the transactions with the rest of the elements
                int length_transaction = items.size();
                if (length_transaction > 1){
                    for (String individualStr : items.subList(1, items.size()-1)) {
    			if( firstTagProccessing )
                            // If it is a new individual local name
                            {
                                // Insert a new individual local name
                                lastLocalName = individualStr;
//                                System.out.println(lastLocalName);
                                currentWorkflow.appendIndividualLocalName( lastLocalName );
                            }
                            else
                            // The string is the continuation of the last individual local name
                            {
                                // Complete the last local name added
                                lastLocalName += individualStr;
                                currentWorkflow.replaceIndividualLocalName(lastLocalName );
                            }
                    }
                }
	}
        
        private void processLink( final char[ ] ch, final int start, final int length  )
	{   
            // Get Subject, Libnk, Object
            String tripletStr = extractString( ch, start, length );
            List<String> triplet = Arrays.asList(tripletStr.split("\\s*,\\s*"));
//            System.out.println("triplet: " + triplet);
//            System.out.println("subjectPosition: " + Integer.parseInt(triplet.get(0)));
//            System.out.println("objectPosition: " + Integer.parseInt(triplet.get(2)));
//            System.out.println("propertyLocalName: " + triplet.get(1));
            
            // Following code prevents a parsing bug
            if (triplet.size() == 3){
                if( firstTagProccessing )
                    // If it is a new individual local name
                    {
                        if (triplet.size() > 2){
                            try {
                                Integer subjectPosition = Integer.parseInt(triplet.get(0));
                                String propertyLocalName = triplet.get(1);
                                Integer objectPosition = Integer.parseInt(triplet.get(2));

                                // Add a property to the workflow
                                currentWorkflow.addPropertyLocalName(subjectPosition, objectPosition, propertyLocalName );
                                currentWorkflow.getPropertiesLocalNames();

                              } catch (NumberFormatException e) {
        //                        System.out.println(e);
                              }

                        }
                    }
                    else
                    // The string is the continuation of the last individual local name
                    {
                        if (triplet.size() > 2){
                            try {
                                Integer subjectPosition = Integer.parseInt(triplet.get(0));
                                String propertyLocalName = triplet.get(1);
                                Integer objectPosition = Integer.parseInt(triplet.get(2));
                                System.out.println(triplet);

                                // Complete the last local name added
                                currentWorkflow.replaceLastAddedLink(subjectPosition, objectPosition, propertyLocalName );

                              } catch (NumberFormatException e) {
                                System.out.println(e);
                              }

                        }
                    }
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
