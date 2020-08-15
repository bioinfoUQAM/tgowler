
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package ontologyrep2;

import java.util.ArrayList;
import ontologyrep2.RadixTree.RadixNode;

/**
 *
 * @author Enridestroy
 */
public class Concept extends OntologyItem implements ObjectWithPointedValues{
    public Concept parent;//parent du concept
    public ArrayList<Concept> children;//contient tous les enfants du concept
    //public String name;//nom du concept
    public int index;//index du concept -- represente sa signature unique --
    //public Character[] char_index;//avant c'etait des char
    public char[] char_index;//avant c'etait des char
    
    //
    public ArrayList<Triplet> triplets;
    
    //lien vers le noeud qui contient son nom
    public RadixNode p_name;
    //lien vers le noeud qui contient son index
    public SuffixTree.Node p_index;
    public static int ID = 0;
    
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
    
    /**
     * Renvoie tous les triplets 
     */
    public Triplet[] getAssociatedTriplets(){
        Triplet[] _triplets = new Triplet[50];
        return _triplets;
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
    
    public void updatePIndex(SuffixTree.Node target){
        this.p_index = target;
    }
    
    /*public void updateIInstance(RadixNode<A> target){
        this.p_instances = target;
    }*/    
}
