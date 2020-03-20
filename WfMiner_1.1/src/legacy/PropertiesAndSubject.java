package legacy;

import java.util.ArrayList;
import ontologyrep20.Relation;


// Class used in a sequence in order to know the properties linked to the last concept.

/**
 * PropertiesAndSubject represents a pair containing a set of properties and the position of the subject concept (of these properties).<br/>
 *  In fact, this class contains two elements :
 *  <ul>
 *  	<li>First element : The set of properties which have the second element as subject</li>
 *  	<li>Second element : The position of the source concept in the sequence</li>
 *  </ul>
 * 
 * When this class is used, the position of the object concept is already known.
 * 
 * @author Frapin Kevin
 */
//public class PropertiesAndSubject extends Pair<Set<Integer>, Integer>
public class PropertiesAndSubject extends Pair<ArrayList<Relation>, Integer>
{
	
	/**
	 * Creates a pair containing the specified set of properties, and the position of the subject.
	 * 
	 * @param properties (First element) The set of properties of this pair.
	 * @param subjectPosition (Second element) The subject position of this pair.
	 */
	/*public PropertiesAndSubject(Set<Integer> properties, Integer subjectPosition) 
	{
            super(properties, subjectPosition);
	}*/
        
        public PropertiesAndSubject(ArrayList<Relation> properties, Integer subjectPosition) 
	{
            super(properties, subjectPosition);
	}
}
