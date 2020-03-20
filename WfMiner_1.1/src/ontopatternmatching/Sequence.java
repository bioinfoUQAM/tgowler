/**
 *
 * @author Enridestroy
 */

package ontopatternmatching;

import ca.uqam.gdac.framework.miner.Miner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import legacy.Pair;
import ontologyrep20.OntoRepresentation;

//public class Sequence  extends IntermediateSequence{
public class Sequence  {
    public ArrayList<Integer> objects = new ArrayList<>();
    public ArrayList<Integer[]> relations = new ArrayList<>();
    
    public ArrayList<Integer> getConcepts() {
        return objects;
    }

    public ArrayList<Integer[]> getProperties() {
        return relations;
    }
    
    public Integer nbConcepts() {
        return objects.size();
    }
    
    public Integer nbProperties( Integer subjectPosition,  Integer objectPosition) {
        return getPropertiesByConceptPositions(subjectPosition,objectPosition).size();
    }
    
    /**
    * Gets the existing links between the subject and object positions in the sequence.
    * 
    * @param subjectPosition Position of the subject in the sequence.
    * @param objectPosition Position of the object in the sequence.
    * 
    * @return The list of properties between the positions.
    */
    public ArrayList<Integer> getPropertiesByConceptPositions( Integer subjectPosition, Integer objectPosition ) 
    {
        ArrayList<Integer> relationsByPositions = new ArrayList();
        for (Integer[] r : relations){
            if (r[1] == subjectPosition && r[2] == objectPosition)
                relationsByPositions.add(r[0]);
        }

        return relationsByPositions;
    }
    
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        OntoRepresentation ontology = Miner.hierarchyRepresentation;
        
        ArrayList<String> cs = new ArrayList ();
        
        for(Integer o : objects){
            String con=ontology.getConcept(o).getName().split("#")[1];
            cs.add(con);
            
        }
        s.append(cs.toString());
        
        s.append(", ");
        for(Integer[] r : relations){
           s.append("{(");
           s.append(r[1]+","+r[2]);
           s.append(")=");
           String uri = ontology.getRelation(r[0]).getName();
           s.append(uri.substring(uri.indexOf("#")+1));
           s.append("}");
        }
        return s.toString();
    }
    
}
