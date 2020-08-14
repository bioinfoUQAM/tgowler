
package ontologyrep20;

import ontologyrep20.RadixTree.RadixNode;

/**
 *
 * @author Enridestroy
 */
public class Instance extends OntologyItem implements ObjectWithPointedValues{
    public Concept concept;
    //public String name;
    public int index;
    public char[] char_index;
    
    public RadixNode p_name;
    
    public Concept getType(){
        return this.concept;
    }
    
    public String getName(){
        return this.p_name.getKey();
    }
        
    @Override
    public void updateObject(Object target){
        if(target instanceof RadixNode){
            this.updatePName((RadixNode)target);
        }
        else if(target instanceof SuffixTree.Node){
            //a remplir
        }else{
            
        }
    }
    
    public void updatePName(RadixNode target){
        this.p_name = target;
    }
}
