/**
 *
 * @author Enridestroy
 */

package ontopatternmatching;

/**
 * Maintenant, codomaine ne sert plus a rien du tout...
 * @author Enridestroy
 */
public class RelationJobBlock extends JobBlock{
    public JobBlock domaine = null;//dans le cas d'une verification de domaine
    public JobBlock codomaine = null;//dans le cas d'une verification de codomaine
    public Integer[] curr_rel;
    
    public RelationJobBlock(int item, Job[] jobs, JobBlock prev, JobBlock next, JobBlock domaine, JobBlock codomaine, int position, int sib_p, short type) {
        super(item, jobs, prev, next, position, sib_p);
        this.domaine = domaine;
        this.type = type;
        this.codomaine = codomaine;
    }
    
    public RelationJobBlock(RelationJobBlock j){
        super(j);
    }
}
