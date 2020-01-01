/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ontologyrep2;

/**
 *
 * @author Enridestroy
 */
public class Triplet {
    public Concept domaine;
    public Concept codomaine;//range
    public Relation relation;
    public boolean sens;
    
    @Override
    public String toString(){
        return "Triplet:(d)"+this.domaine.index+"(c)"+this.codomaine.index+"(r)"+this.relation.index;
    }
}
