/**
 *
 * @author Enridestroy
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ontopatternmatching;

import java.util.ArrayList;

/**
 * C'est la classe qui va contenir notre structure chainee des blocs.
 * @author Enridestroy
 */
public class AppariementStructure {
    public ArrayList<JobBlock> pile_de_taches = new ArrayList<>();
    public JobBlock firstConceptBlock = null;//represente un pointeur vers le premier bloc ajoute
    public ArrayList<JobBlock> concepts_to_blocks = new ArrayList<>();//permet de requeter les concepts vers le bloc qui les represente
    public JobBlock lastAddedConcept = null;//pointeur vers le dernier concept ajoute
    //public int deplacements = 0;//ne sert a rien (pour l'instant^^)
    public RelationJobBlock lastAddedRelation = null;//pointeur vers la derniere relation ajoutee
    public ArrayList<RelationJobBlock> relations_to_blocks = new ArrayList<>();//permet de requeter les relations vers le bloc qui les represente
    //public JobBlock lastTouchedBlock = null;
    //public Sequence userSequence = null;
    public Integer[] appariement = null;
    public int last_touched_position = -1;
    
    public AppariementStructure(){
        
    }
    
    public AppariementStructure(AppariementStructure a){
        //on cree les structures
        this.pile_de_taches = new ArrayList<>(a.pile_de_taches.size());
        this.concepts_to_blocks = new ArrayList<>(a.concepts_to_blocks.size());
        this.relations_to_blocks = new ArrayList<>(a.relations_to_blocks.size());
        //on copie la sequence
        //this.userSequence = a.userSequence;
        this.last_touched_position = a.last_touched_position;
        
        /**
         * On copie tous les blocs
         */
        for(JobBlock j : a.pile_de_taches){
            if(j.type==0){
                JobBlock jj = new JobBlock(j);
                this.concepts_to_blocks.add(jj);
                this.pile_de_taches.add(jj);
            }
            else if(j.type==1){
                //this.relations_to_blocks.add(new RelationJobBlock((RelationJobBlock)j));
                RelationJobBlock jj = new RelationJobBlock((RelationJobBlock)j);
                this.pile_de_taches.add(jj);
            }
            else{
                RelationJobBlock jj = new RelationJobBlock((RelationJobBlock)j);
                this.relations_to_blocks.add(jj);
                this.pile_de_taches.add(jj);
            }
        }
        
        /**
         * On copie les pointeurs sur tous les blocks (prev, next...)
         */
        for(JobBlock j : a.pile_de_taches){
            //copie du suivant
            if(j.next!=null){
                this.pile_de_taches.get(j.position).next = this.pile_de_taches.get(j.next.position);
            }
            //copie du precedent
            if(j.prev!=null){
                this.pile_de_taches.get(j.position).prev = this.pile_de_taches.get(j.prev.position);
            }
            //copie du premier fils
            if(j.firstChild!=null){
                this.pile_de_taches.get(j.position).firstChild = this.pile_de_taches.get(j.firstChild.position);
            }
            //copie du dernier fils
            if(j.lastChild!=null){
                this.pile_de_taches.get(j.position).lastChild = this.pile_de_taches.get(j.lastChild.position);
            }          
            //copie du dernier domaine ajoute
            if(j.last_domain_added!=null){
                this.pile_de_taches.get(j.position).last_domain_added = this.pile_de_taches.get(j.last_domain_added.position);
            }
            //copie du dernier codomaine ajoute
            if(j.last_range_added!=null){
                this.pile_de_taches.get(j.position).last_range_added = this.pile_de_taches.get(j.last_range_added.position);
            }
            //copie de la deuxieme partie du codomaine
            if(j.last_second_range_added!=null){
                this.pile_de_taches.get(j.position).last_second_range_added = this.pile_de_taches.get(j.last_second_range_added.position);
            }
        }
        /**
         * On copie les pointeurs sur les relations (domaine et codomaine)
         */
        for(RelationJobBlock j : this.relations_to_blocks){
            if(((RelationJobBlock)a.pile_de_taches.get(j.position)).domaine!=null){
                j.domaine = this.pile_de_taches.get(((RelationJobBlock)a.pile_de_taches.get(j.position)).domaine.position);
            }
            
            if(((RelationJobBlock)a.pile_de_taches.get(j.position)).codomaine!=null){
                j.codomaine = this.pile_de_taches.get(((RelationJobBlock)a.pile_de_taches.get(j.position)).codomaine.position);
            }
            
            if(((RelationJobBlock)a.pile_de_taches.get(j.position)).curr_rel!=null){
                j.curr_rel = new Integer[]{
                    ((RelationJobBlock)a.pile_de_taches.get(j.position)).curr_rel[0], 
                    ((RelationJobBlock)a.pile_de_taches.get(j.position)).curr_rel[1], 
                    ((RelationJobBlock)a.pile_de_taches.get(j.position)).curr_rel[2]
                };
            }
        }
        
        
        //on copie les pointeurs de concepts
        {
            int i = this.concepts_to_blocks.size();
            if(i>0) {
                i -= 1;
                this.lastAddedConcept = this.pile_de_taches.get(this.concepts_to_blocks.get(i).position);
                this.firstConceptBlock = this.pile_de_taches.get(0);
            }
        }
        //on copie les pointeurs de relations
        {
            int i = this.relations_to_blocks.size();
            if(i>0){
                i-=1;
                this.lastAddedRelation = (RelationJobBlock)this.pile_de_taches.get(this.relations_to_blocks.get(i).position);
            }
        }
        
        
        /*for(int i=0;i<this.pile_de_taches.size();i++){
            if(this.pile_de_taches.get(i).item != a.pile_de_taches.get(i).item){
                System.out.println("badddd copy");
                System.exit(1);
            }
        }
        
        JobBlock check = a.firstConceptBlock;
        JobBlock check2 = this.firstConceptBlock;
        while(check!=null){
            System.out.println("1:"+check);
            System.out.println("2:"+check2);
            if(check.item!=check2.item){
                System.out.println("Bad copy:("+check.type+")"+check.item+"!=("+check2.type+")"+check2.item);
                System.out.println("a:"+a.pile_de_taches.size()+"-this:"+this.pile_de_taches.size());
                System.exit(1);
            }
            else{
                System.out.println("cc:("+check.type+")"+check.item+"==("+check2.type+")"+check2.item);
            }
            if(check.firstChild!=null){
                System.out.println("check has sons");
                if(check.firstChild.firstChild!=null){
                    System.out.println("fils de fils !!!!");
                    System.exit(1);
                }
                check = check.firstChild;
                check2 = check2.firstChild;
                continue;
            }
            check = check.next;
            check2 = check2.next;
        }
        System.out.println("ok copy");
        
        
        if(this.lastAddedConcept!=null && a.lastAddedConcept!=null && this.lastAddedConcept.item != a.lastAddedConcept.item){
            System.out.println("not nice copy:"+this.lastAddedConcept.item +"=="+ a.lastAddedConcept.item);
            System.exit(1);
        }
        else if(this.lastAddedConcept!=null && a.lastAddedConcept!=null){
            System.out.println("copy:"+this.lastAddedConcept.item +"=="+ a.lastAddedConcept.item);
        }
        else{
            
        }*/
        
    }
    
    /*
    public AppariementStructure(final Sequence s){
        //this.userSequence = s;
    }*/
    
    
    public AppariementStructure extend(AppariementExtensionTask t){
        /*System.out.println("before:");
        for(JobBlock j : this.pile_de_taches){
            System.out.println(">"+j.item+"("+j.type+")");
        }
        */
        AppariementStructure extend = t.extend(this);
        //System.out.println("ltb:"+this.lastTouchedBlock);
        /*System.out.println("after:");
        
        JobBlock j = this.firstConceptBlock;
        while(j!=null){
            System.out.println(">:("+j.type+")"+j.item);
            if(j.firstChild!=null){
                j = j.firstChild;
                continue;
            }
            j = j.next;
        }*/
        
        /*
        for(JobBlock j : this.pile_de_taches){
            System.out.println(">"+j.item+"("+j.type+")");
        }*/
        //System.out.println("done");
        
        /*
        {
            JobBlock one = this.concepts_to_blocks.get(0);
            while(one!=null){
                System.out.println("[22222]Solution pour ["+one.position+"]"+one.item+": "+(one.solution-1));
                if(one.firstChild!=null){
                    JobBlock two=one.firstChild;
                    while(two!=null){
                        System.out.println("with ["+two.position+"]"+two.item+": "+(two.solution-1));
                        if(two.next != two.prev.next) {
                            two = two.next;
                        }else break;
                    }
                }                
                one = one.next;
            }
        }
        */
        return extend;
    }
    
    // <editor-fold defaultstate="collapsed" desc="extension builders">
    
    public AppariementExtensionTask createAddRelationExtension(){
        return new AddRelationTask();
    }
    
    public AppariementExtensionTask createAddConceptExtension(){
        return new AddConceptTask();
    }
    
    public AppariementExtensionTask createSpeConceptExtension(){
        return new SpeConceptTask();
    }
    
    public AppariementExtensionTask createSpeRelationExtension(){
        return new SpeRelationTask();
    }
    
    // </editor-fold>
    

    
    // <editor-fold desc="extension tasks">
    
    // </editor-fold>
}
