/**
 *
 * @author Enridestroy
 */

package ontopatternmatching;

import ontologyrep2.OntoRepresentation;


public final class SequenceMatcher {
    public static boolean debug = false;
    public static boolean html_output = false;
    public static short mode = 0;
    
    public SequenceMatcher(){
        
    }
            
    /**
     * Methode qui englobe le matching d'une sequence avec une "pile de taches"
     * @param firstBlock
     * @param appariement
     * @param modifications
     * @param sequence
     * @param ontology
     * @return 
     */
    public final boolean tryToMatch(JobBlock firstBlock, int[] appariement, boolean[] modifications, final Sequence sequence, final OntoRepresentation ontology, final Motif m){      
        
        appariement = this.__match2(firstBlock, appariement, modifications, sequence, ontology, m);        
        return (appariement[0]!=0);
    }
    
    /**
     * Meme chose qu'au dessus mais experiemental
     * @param firstBlock
     * @param appariement
     * @param modifications
     * @param sequence
     * @param ontology
     * @param m
     * @return 
     */
    public final boolean tryToMatch_BETA(JobBlock firstBlock, int[] appariement, boolean[] modifications, final Sequence sequence, final OntoRepresentation ontology, final Motif m){      
        appariement = this.__match3_beta(firstBlock, appariement, modifications, sequence, ontology, m);        
        return (appariement[0]!=0);
    }
    
    
    public final boolean tryToMatch_with_Output(JobBlock firstBlock, int[] appariement, boolean[] modifications, final Sequence sequence, final OntoRepresentation ontology, final Motif m, StringBuilder html, int[] nbr_steps){
        appariement = this.__match_with_output(firstBlock, appariement, modifications, sequence, ontology, m, html, nbr_steps);
        return (appariement[0]!=0);
    }
    
    /**
     * Methode qui effectue le matching d'une sequence de blocs avec une sequence
     * @param block
     * @param appariement
     * @param modifications
     * @param sequence
     * @param ontology
     * @return 
     */
    public final int[] __match2(JobBlock block, int[] appariement, boolean[] modifications, final Sequence sequence, final OntoRepresentation ontology, final Motif m){
        int res;//resultat des jobs
        JobBlock child;//contraintes liees aux relations

        //System.out.println("###############################################");
        // System.out.println("######## MATCHING STARTED #####");
        //System.out.println("###############################################");     
        SequenceMatcher.mode = 0;
        
        while(block!=null){
  
            //last_block = block;
            //on recupere le premier element
            res = block.doJobs2(sequence, appariement, modifications, ontology, m);//la case effectue les traitements associes
            //block.solution = res;//on met a jour la solution
            if(appariement[block.position] != res) {
                appariement[block.position] = res;
                //if(use_modifs) {
                if(SequenceMatcher.debug) System.out.println("Need to set modifs to true");
                modifications[0] = true;
                //}
            }           
            
            //si on a un resultat positif
            if(res>0){
                //System.out.println("match");
                //SequenceMatcher.ascending = true;
                SequenceMatcher.mode = 2;
                //if(this.pile_de_taches2.size() < 1){
                //System.out.println("Result is nice:"+block.item+" => "+block.solution);
                child = block.firstChild;//on recupere la premiere contrainte (relation)
                if(child!=null){
                    //System.out.println("Has child donc relations a tester");
                    block = child;
                }
                else{
                    //si tout matche alors on va au suivant
                    block = block.next;
                }
            }
            else{
                SequenceMatcher.mode = 1;
                
                block = block.prev;
               
            }
        }
        //System.out.println("###############################################");
        //System.out.println("######## MATCHING ENDED #####");
        //System.out.println("###############################################");
        return appariement;
    }
    
    public final int[] __match3_beta(JobBlock block, int[] appariement, boolean[] modifications, final Sequence sequence, final OntoRepresentation ontology, final Motif m){
        int res;//resultat des jobs
        JobBlock child;//contraintes liees aux relations
        System.out.println("#######################");
        SequenceMatcher.mode = 0;
        
        while(block!=null){
            //on recupere le premier element
            res = block.doJobs2(sequence, appariement, modifications, ontology, m);//la case effectue les traitements associes
            //block.solution = res;//on met a jour la solution
            if(appariement[block.position] != res) {
                appariement[block.position] = res;
                if(SequenceMatcher.debug) System.out.println("Need to set modifs to true");
                modifications[0] = true;
            }           
            System.out.println("RES:"+res+"@"+block.position);
            //si on a un resultat positif
            if(res>0){
                System.out.println("UP!");
                SequenceMatcher.mode = 2;
                child = block.firstChild;//on recupere la premiere contrainte (relation)
                if(child!=null){
                    block = child;
                }
                else{
                    //si tout matche alors on va au suivant
                    block = block.next;
                }
            }
            else{
                SequenceMatcher.mode = 1;
                System.out.println("DOWN!");
                
                /**
                 * EXPERIEMENTAL
                 */
                if(m.empty_structure.relations_to_blocks.isEmpty()){
                    System.out.println("Fail1 @"+block.position);
                    appariement = new int[appariement.length];
                    return appariement; 
                }
                else{
                    //block = block.prev;
                    
                    //si ca marche, il faudra precalculer le concept le plus a droite dependant dune relation
                    int most_right_pos = m.empty_structure.relations_to_blocks.get(0).prev.sibling_position;
                    //si il n'y a pas de relations a droite on sort
                    for(RelationJobBlock rel : m.empty_structure.relations_to_blocks){
                        //on recupere la position du concept
                        if(rel.prev.sibling_position>most_right_pos) {
                            most_right_pos = rel.prev.sibling_position;
                            System.out.println("updating mrp:"+most_right_pos);
                        }                        
                    }
                    System.out.println("most right pos:"+most_right_pos+" vs"+block.sibling_position);
                    //ensuite on va regarder si le bloc courrant possede des relations a droite
                    if(block.type==0 && block.firstChild==null && block.sibling_position > most_right_pos){
                        System.out.println("Fail2 @"+block.sibling_position);
                        appariement = new int[appariement.length];
                        return appariement;
                    }
                    //on fait comme d'hab
                    block = block.prev;
                }
                /**
                 * FIN EXPERIMENTAL
                 */
               
            }
        }
        return appariement;
    }
    
    public final int[] __match_with_output(JobBlock block, int[] appariement, boolean[] modifications, final Sequence sequence, final OntoRepresentation ontology, final Motif m, StringBuilder html, int[] nbr_steps){
        int res;//resultat des jobs
        JobBlock child;//contraintes liees aux relations
        boolean use_modifs=false;
        if(appariement[0]!=0){
            use_modifs=true;
            if(SequenceMatcher.debug) System.out.println("using modifications...");
        }
        
        //System.out.println("###############################################");
        //System.out.println("######## MATCHING STARTED #####");
        //System.out.println("###############################################");     
        SequenceMatcher.mode = 0;
        
        
        html.append("\"steps\": [");
        
        //System.out.println("constraints:"+this.pile_de_taches.size());
        while(block!=null){
            nbr_steps[0]++;
            /**
             * Ne pas tenir compte de ca si on est pas en mode HTML
             */
            if(SequenceMatcher.html_output){
                
                if(SequenceMatcher.mode!=0){
                    html.append(",");
                }
            
                String jj = "TrouveNextConcept";
                if(block.type==1){
                    jj = "Domaine";
                }
                else if(block.type==2){
                    jj = "Codomaine";
                }
                
                
                html.append("{  \"case\": \"");
                if(block.type>0){
                    html.append(block.prev.sibling_position);
                }
                else{
                    html.append(block.sibling_position);//numero de case
                }
                html.append("\",  \"rel\" : \"");
                if(block.type>0){
                    html.append(block.sibling_position+1);//numero de la sous case
                }
                else{
                    html.append("0");//numero de la sous case
                }
                html.append("\",\n" +
                "\"job\" : \"");
                html.append(jj);//nom du job
                html.append("\", \"out\" : \"");
            }
            String[] info = new String[1];
            info[0] = "";
            //last_block = block;
            //on recupere le premier element
            //System.out.println("Block("+block.type+"):"+block.item);
            res = block.doJobs_with_output(sequence, appariement, modifications, ontology, m, info);//la case effectue les traitements associes
            //block.solution = res;//on met a jour la solution
            if(appariement[block.position] != res) {
                appariement[block.position] = res;
                if(use_modifs) {
                    if(SequenceMatcher.debug) System.out.println("Need to set modifs to true");
                    modifications[0] = true;
                }
            }
            
            /**
             * Ne pas tenir compte de ca si on est pas en mode HTML
             */
            if(SequenceMatcher.html_output){
                html.append(res);//solution
                html.append("\", " + "\"info\": \"").append(info[0]).append("\" }");
            }
            
            
            
            /*for(int i : appariement){
                System.out.println("blo:"+i);
            }*/
            //si on a un resultat positif
            if(res>0){
                //System.out.println("match");
                SequenceMatcher.mode = 2;
                //if(this.pile_de_taches2.size() < 1){
                //System.out.println("Result is nice:"+block.item+" => "+block.solution);
                child = block.firstChild;//on recupere la premiere contrainte (relation)
                if(child!=null){
                    //System.out.println("Has child donc relations a tester");
                    block = child;
                    //this.pile_de_taches2.pop();
                    //this.pile_de_taches2.addLast(child);
                }
                else{
                    //si tout matche alors on va au suivant
                    block = block.next;
                    //this.pile_de_taches2.addLast(block.next);
                }
                //}
                //block = this.pile_de_taches2.pop();
            }
            else{
                //System.out.println("not match");
                SequenceMatcher.mode = 1;
                //System.out.println("Result is bad:"+block.item);
                //block = block.prev;//on revient au block precedent si le resultat est negatif
                //this.pile_de_taches2.clear();
                block = block.prev;
                //this.pile_de_taches2.addFirst(block.prev);
            }
            /*if(block == last_block){
                System.out.println("The two blocks are the same!!!");
                System.exit(1);
            }*/
        }
        
        html.append("]");
        //System.out.println("###############################################");
        //System.out.println("######## MATCHING ENDED #####");
        //System.out.println("###############################################");
        return appariement;
    }
}
