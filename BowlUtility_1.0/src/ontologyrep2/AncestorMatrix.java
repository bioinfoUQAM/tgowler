package ontologyrep2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Permet de maintenir une matrice qui contient tous les items et ses ancetres.
 * Il ne s'agit pas de la forme la plus optimisée mais on en est proche niveau CPU.
 * Niveau memeoire, en revanche, on pourrait surremnt faire mieux.
 * @author Enridestroy
 */
public final class AncestorMatrix {
    public Map<Character, HashSet<Character>> matrix = new HashMap<>();
    
    /**
     * 
     * @param index
     * @return 
     */
    public HashSet<Character> getAncestors(final char index){
        return this.matrix.get(index);
    }
    
    /**
     * 
     * @param descendant
     * @param ancestor
     * @return 
     */
    public boolean isAncestor(final char descendant, final char ancestor){
        HashSet<Character> ancestors = this.getAncestors(descendant);
        return ancestors!=null&&ancestors.contains(ancestor);
    }
    
    /**
     * 
     * @param descendant
     * @param ancestor
     * @return 
     */
    public boolean isEqualOrDescendant(final char descendant, final char ancestor){
        return (descendant==ancestor||this.isAncestor(descendant, ancestor));
    }
    
    /**
     * 
     * @param item 
     */
    public void addItem(final char item){
        this.matrix.put(item, new HashSet<Character>());
    }
    
    /**
     * 
     * @param item 
     */
    public void removeItem(final char item){
        this.matrix.remove(item);
    }
    
    /**
     * 
     * @param descendant
     * @param ancestor
     * @throws Exception 
     */
    public void addAncestor(final char descendant, final char ancestor) throws Exception{
        HashSet<Character> ancestors = this.matrix.get(descendant);
        if(ancestors!=null){
            ancestors.add(ancestor);
        }
        else{
            throw new Exception();
        }
    }
    
    /**
     * 
     * @param descendant
     * @param ancestor
     * @throws Exception 
     */
    public void removeAncestor(final char descendant, final char ancestor) throws Exception{
        HashSet<Character> ancestors = this.matrix.get(descendant);
        if(ancestors!=null){
            boolean can_remove = ancestors.remove(ancestor);
            if(!can_remove){
                throw new Exception();
            }
        }
        else{
            throw new Exception();
        }
    }
}
