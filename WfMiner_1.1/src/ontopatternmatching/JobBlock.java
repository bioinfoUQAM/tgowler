/**
 *
 * @author Enridestroy
 */

package ontopatternmatching;

import ontologyrep20.OntoRepresentation;

public class JobBlock {
    public int solution = 0;
    public int item;
    //public JobBlock pointeur;
    public Job[] jobs = null;//liste de jobs dans le block
    public JobBlock next = null;//pointeur vers l'element suivant
    public JobBlock prev = null;//pointeur vers l'element precedent
    public JobBlock firstChild = null;//pointeur vers les contraintes filles
    public JobBlock lastChild = null;//pointeur vers le dernier fils
    public String label = "";
    public int position = 0;
    public short type = 0;
    
    public JobBlock last_domain_added = null;
    public JobBlock last_range_added = null;
    public JobBlock last_second_range_added = null;
    public int sibling_position = 0;
    
    public JobBlock(final JobBlock block){
        this.jobs = block.jobs;
        this.item = block.item;
        //this.next = block.next;
        //this.prev = block.prev;
        //this.firstChild = block.firstChild;
        this.position = block.position;
        this.sibling_position = block.sibling_position;
        //this.label = block.label;
        //this.lastChild = block.lastChild;
        this.type = block.type;
        //this.solution = block.solution;
    }
    
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder("["+this.position+"]["+this.type+"]"+this.item+" (");
        if(this.prev!=null){
            s.append("prev:").append(this.prev.item);
        }
        if(this.next!=null){
            s.append(", next:").append(this.next.item);
        }
        s.append(")").append(this.solution);
        return s.toString();
    }
    
    
    public JobBlock(int item, Job[] jobs, JobBlock prev, JobBlock next, int position, int sibling_position){
        this.jobs = jobs;
        this.item = item;
        this.next = next;
        this.prev = prev;
        //this.prev = null;
        this.firstChild = null;
        this.lastChild = this.firstChild;
        this.position = position;
        this.sibling_position = sibling_position;
        this.label = "";//["+position+"]"+this.jobs[0].toString()+"-["+this.item+"]"+this;        
    }
    
    
    /**
     * Effectue toutes les operations associees a une case
     * @param appariement
     * @param sequence
     * @param modifications
     * @param ontology
     * @param m
     * @return 
     */
    public int doJobs2(Sequence sequence, int[] appariement, boolean[] modifications, final OntoRepresentation ontology, final Motif m){
        //int res = -1;
        return this.jobs[0].doJob(this, sequence, appariement, modifications, ontology, m);
    }
    
    /**
     * Permet de tenir compte des explications fournies par le Job
     * @param sequence
     * @param appariement
     * @param modifications
     * @param ontology
     * @param m
     * @param info
     * @return 
     */
    public int doJobs_with_output(Sequence sequence, int[] appariement, boolean[] modifications, final OntoRepresentation ontology, final Motif m, String[] info){
        return this.jobs[0].doJob_with_output(this, sequence, appariement, modifications, ontology, m, info);
    }
    
    public int doJobs(JobBlock j, int i){
        int res = -1;
        for (Job job : this.jobs) {
            //on recupere et execute le job
            //res = job.doJob(this, sequence);
            if(res<0) return res;
        }
        return res;
    }
    
    /**
     * Permet d'ajouter un fils au bloc
     * @param block 
     */
    public void addChild(JobBlock block){
        if(this.firstChild==null && this.lastChild==null){
            this.firstChild = block;
            this.lastChild = this.firstChild;
        }
        else{
            this.lastChild.next = block;//passage du dernier bloc au nouveau bloc
            //block.prev = this;//passage du nouveau bloc au concept courrant
            this.lastChild = block;//mise a jour du dernier concept
        }
        this.lastChild.label = "[Child]"+this.lastChild.label;
        
        
        
        //si il s'agit d'un domaine
        if(block.type == 1){
            this.last_domain_added = block;
        }
        //si il s'agit d'un codomaine
        else if(block.type == 2){
            //si la position du domaine dans le motif est superieure a la position du codomaine
            //alors la relation est inversee et se trovuera a la fin de la liste
            if(((RelationJobBlock)block).domaine.prev.position > block.prev.position){
                this.last_second_range_added = block;
            }
            //sinon, la relation est dans le bon sens et elle sera au debut
            else{
               this.last_range_added = block; 
            }
        }
    }
}
