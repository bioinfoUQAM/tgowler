/**
 *
 * @author Enridestroy
 */

package ontopatternmatching;

import java.util.ArrayList;
import ontologyrep2.OntoRepresentation;

public final class TrouveNextConcept implements Job{
    /**
     * Permet de trouver un nouveau concept qui instancie la classe du motif
     * @param block
     * @param sequence
     * @param appariement
     * @param modifications
     * @param ontology
     * @param sm
     * @return 
     */
    @Override
    public final int doJob(JobBlock block, final Sequence sequence, int[] appariement, boolean[] modifications, final OntoRepresentation ontology, final Motif m) {
        int j=0;int s;int o;
        
        int solution = appariement[block.position];
        int prev_solution = 0;
        if(block.prev!=null) {
            prev_solution = appariement[block.prev.position];
        }

        boolean position_domaine_modifiee=false;
        if(block.firstChild!=null){
            if(block.last_range_added!=null){
                if(SequenceMatcher.debug) System.out.println("Codomaine spoted donc on regarde si des modifs faites");
                //si ce concept est codomaine, c'est a dire que des concepts a gauche ont peut etre ete modifies
                position_domaine_modifiee = modifications[0];//on va laisser ca pour l'instant
            }
        }
        
        /**
         * Si on a deja une solution
         */
        if(solution>0){
            /**
            * Si le bloc n'a pas encore de solution et que l'on ne monte pas
            * => c'est la premiere fois que l'on arrive sur ce bloc (nouveau bloc)
            */
            if(SequenceMatcher.mode == 0){
                if(SequenceMatcher.debug) System.out.println("Already a solution but not ascending");
                j = solution-1;//on va verifier ce bloc (specialisation par exemple)
                //if(SequenceMatcher.debug) System.out.println("["+j+"]trying to match concept "+o+" with "+block.item);
                if(ontology.isConceptEqualOrDescendant(block.item, sequence.objects.get(j))){
                    return solution;
                }
                else if(prev_solution>0){
                    j = prev_solution;
                }
                else{
                    j = 0;
                }
            }
            else if(SequenceMatcher.mode==1){
                //on repart de la derniere solution valide
                if(SequenceMatcher.debug) System.out.println("Starting from last valid position");
                j = solution;
            }
            else if(SequenceMatcher.mode==2){
                //on remet en question le bloc d'avant
                //a partir du moment ou on fait une remise ne question on doit tout refaire !
                if(SequenceMatcher.debug) System.out.println("Ascending and with a solution!:"+solution);
                if(block.prev!=null) {
                    if(SequenceMatcher.debug) System.out.println("prev not null");
                    if(solution > prev_solution && !position_domaine_modifiee){
                        if(SequenceMatcher.debug) System.out.println("Ok a deja un resultat et pas de modif("+position_domaine_modifiee+"):"+solution +">"+ prev_solution);
                        return solution;
                    }else{
                        if(SequenceMatcher.debug) System.out.println("On reprend a partir du dernier concept:"+ prev_solution);
                        j = prev_solution;
                    }
                }else{
                    //ne cas la ne devrait jamais arriver : on monte et on est a la premiere case !
                    if(SequenceMatcher.debug) System.out.println("On a deja un resultat!"+solution);
                    return solution;
                }
            }
        }
        /**
         * Si on en a pas et qu'on est pas sur le first block
         */
        else if(block.prev!=null) {
            if(SequenceMatcher.debug) System.out.println("Just prev is not null:"+prev_solution);
            j = prev_solution;//notre element doit au moins le suivant du concept precedent
        }
        /**
         * On a pas de solution et on est sur le first block
         */
        else {
            if(SequenceMatcher.debug) System.out.println("Else, j=0");
            j = 0;
        }

        if(SequenceMatcher.debug) System.out.println("Concept => Starting at:"+j+" ("+solution+")");
        
        s=sequence.objects.size()-(m.concepts.size()-(block.sibling_position+1));//enlever le nombre de blocks restants
        //tant qu'on n'est pas a la fin de la sequence
        while(j<s){
            o = sequence.objects.get(j);//on recupere l'item
            //if(SequenceMatcher.debug) System.out.println("["+j+"]trying to match concept "+o+" with "+block.item);
            if(ontology.isConceptEqualOrDescendant(block.item, o)){//si l'element correspond a notre classe
                //if(block.firstChild!=null) 
                if(SequenceMatcher.debug) System.out.println("The item "+block.item+" is matched by "+o+" at "+j);
                //if(o==block.item) {//si l'element correspond a notre classe
                //block.solution = j;//il s'agit de la solution
                return j+1;//on sort de la boucle
            }
            j++;//sinon, on incremente le compteur
        }
        if(SequenceMatcher.debug) System.out.println("["+block.position+"]No more solutions for Concept "+block.item+"....GOING BACK");
        //block.solution = 0;
        return 0;//ou j
    }

    @Override
    public int doJob_with_output(JobBlock block, final Sequence sequence, int[] appariement, boolean[] modifications, final OntoRepresentation ontology, final Motif m, String[] info) {
        int j=0;int s;int o;
        
        StringBuilder _info = new StringBuilder();
        
        
        int solution = appariement[block.position];
        int prev_solution = 0;
        if(block.prev!=null) {
            prev_solution = appariement[block.prev.position];
        }

        boolean position_domaine_modifiee=false;
        if(block.firstChild!=null){
            if(block.last_range_added!=null){
                
                _info.append("Le bloc possede des relations et est codomaine d'au moins une relation.");
                
                if(SequenceMatcher.debug) System.out.println("Codomaine spoted donc on regarde si des modifs faites.");
                //si ce concept est codomaine, c'est a dire que des concepts a gauche ont peut etre ete modifies
                position_domaine_modifiee = modifications[0];//on va laisser ca pour l'instant
                
                _info.append("On modifie la position de depart?");
                
            }
        }
        
        /**
         * Si on a deja une solution
         */
        if(solution>0){
            
            _info.append("Le bloc possede deja une solution.");
            
            /**
            * Si le bloc n'a pas encore de solution et que l'on ne monte pas
            * => c'est la premiere fois que l'on arrive sur ce bloc (nouveau bloc)
            */
            if(SequenceMatcher.mode == 0){
                
                _info.append("Mais on n'est PAS en train d'aller vers la droite.");
                
                if(SequenceMatcher.debug) System.out.println("Already a solution but not ascending");
                j = solution-1;//on va verifier ce bloc (specialisation par exemple)
                //if(SequenceMatcher.debug) System.out.println("["+j+"]trying to match concept "+o+" with "+block.item);
                if(ontology.isConceptEqualOrDescendant(block.item, sequence.objects.get(j))){
                    _info.append("La solution actuelle est valide.");
                    info[0] = _info.toString();
                    return solution;
                }
                else if(prev_solution>0){
                    _info.append("On va demarrer la rechercher a partir de la solution du bloc precedent (c.a.d à droite).");
                    j = prev_solution;
                }
                else{
                    _info.append("On commence la recherche au debut.");
                    j = 0;
                }
            }
            else if(SequenceMatcher.mode==1){
                _info.append("On est en train de descendre (vers gauche), on va commencer à partir de la derniere position valide.");
                //on repart de la derniere solution valide
                if(SequenceMatcher.debug) System.out.println("Starting from last valid position");
                j = solution;
                _info.append("j = solution.");
            }
            else if(SequenceMatcher.mode==2){
                
                _info.append("Ici, on est en train de monter (vers la droite)");
                
                //on remet en question le bloc d'avant
                //a partir du moment ou on fait une remise ne question on doit tout refaire !
                if(SequenceMatcher.debug) System.out.println("Ascending and with a solution!:"+solution);
                if(block.prev!=null) {
                    
                    _info.append("On est pas dans le premier bloc.");
                    
                    if(SequenceMatcher.debug) System.out.println("prev not null");
                    if(solution > prev_solution && !position_domaine_modifiee){
                        
                        _info.append("Notre solution est bien plus a droite que celle du bloc d'avant et pos_domaine pas modifiee. Donc on est bons.");
                        
                        if(SequenceMatcher.debug) 
                            System.out.println("Ok a deja un resultat et pas de modif("+position_domaine_modifiee+"):"+solution +">"+ prev_solution);
                        info[0] = _info.toString();
                        return solution;
                    }else{
                        if(SequenceMatcher.debug) System.out.println("On reprend a partir du dernier concept:"+ prev_solution);
                        j = prev_solution;
                        
                        _info.append("On reprend a partir du dernier concept.");
                        
                    }
                }else{
                    _info.append("On a deja un resultat. On est bons.");
                    
                    //ne cas la ne devrait jamais arriver : on monte et on est a la premiere case !
                    if(SequenceMatcher.debug) System.out.println("On a deja un resultat!"+solution);
                    info[0] = _info.toString();
                    return solution;
                }
            }
        }
        /**
         * Si on en a pas et qu'on est pas sur le first block
         */
        else if(block.prev!=null) {
            if(SequenceMatcher.debug) System.out.println("Just prev is not null:"+prev_solution);
            j = prev_solution;//notre element doit au moins le suivant du concept precedent
            
            _info.append("On a aucune solution et on est pas le premier bloc. On repart de la solution du bloc d'avant.");
            
        }
        /**
         * On a pas de solution et on est sur le first block
         */
        else {
            if(SequenceMatcher.debug) System.out.println("Else, j=0");
            j = 0;
            _info.append("On a pas de solution et on est le premier bloc. Donc on commence a zero.");
        }

        if(SequenceMatcher.debug) System.out.println("Concept => Starting at:"+j+" ("+solution+")");
        
        
        _info.append("On va parcourir la sequence et trouver une correspondance si possible.");
        
        s=sequence.objects.size()-(m.concepts.size()-(block.sibling_position+1));//enlever le nombre de blocks restants
        //tant qu'on n'est pas a la fin de la sequence
        while(j<s){
            o = sequence.objects.get(j);//on recupere l'item
            //if(SequenceMatcher.debug) System.out.println("["+j+"]trying to match concept "+o+" with "+block.item);
            if(ontology.isConceptEqualOrDescendant(block.item, o)){//si l'element correspond a notre classe
                //if(block.firstChild!=null) 
                if(SequenceMatcher.debug) System.out.println("The item "+block.item+" is matched by "+o+" at "+j);
                //if(o==block.item) {//si l'element correspond a notre classe
                //block.solution = j;//il s'agit de la solution
                
                _info.append("On a trouve une correspondance!");
                info[0] = _info.toString();
                return j+1;//on sort de la boucle
            }
            j++;//sinon, on incremente le compteur
        }
        if(SequenceMatcher.debug) System.out.println("["+block.position+"]No more solutions for Concept "+block.item+"....GOING BACK");
        //block.solution = 0;
        _info.append("Pas de correspondance...");
        info[0] = _info.toString();
        return 0;//ou j
    }
}
