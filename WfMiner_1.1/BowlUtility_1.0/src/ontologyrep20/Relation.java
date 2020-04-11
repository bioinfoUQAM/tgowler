
package ontologyrep20;

/**
 *
 * @author Enridestroy
 */
public class Relation extends OntologyItem implements ObjectWithPointedValues{
    public int index;
    public String name;
    public static int ID = 0;
    public char[] char_index;
    
    //lien vers le noeud qui contient son nom
    public RadixTree.RadixNode p_name;
    //lien vers le noeud qui contient son index
    public SuffixTree.Node p_index;

    public Relation parent;//parent du concept
    
    /**
     * 
     * @return 
     */
    public int getIndex(){
        return this.p_index.index;
    }
    
    /**
     * 
     * @return 
     */
    public String getName(){
        return this.p_name.getKey();
    }
    
    @Override
    public void updateObject(Object target){
        if(target instanceof RadixTree.RadixNode){
            this.updatePName((RadixTree.RadixNode)target);
        }
        else if(target instanceof SuffixTree.Node){
            //a remplir
        }else{
            
        }
    }
    
    public void updatePName(RadixTree.RadixNode target){
        this.p_name = target;
    }
    
    public void updatePIndex(SuffixTree.Node target){
        this.p_index = target;
    }

    @Override
    public String toString(){
        return this.getName();
    }
}
