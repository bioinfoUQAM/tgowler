/**
 *
 * @author Enridestroy
 */
package ontologyrep20;


public class TripletInst {
    public Instance domaine;
    public Instance codomaine;//range
    public Relation relation;
    public boolean sens;
    
    @Override
    public String toString(){
        return "Triplet:(d)"+this.domaine.index+"(c)"+this.codomaine.index+"(r)"+this.relation.index;
    }
}
