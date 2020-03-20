/**
 *
 * @author Enridestroy
 */

package ontopatternmatching;

import ontologyrep20.OntoRepresentation;

public final class ValideCodomaineRelation implements Job{

    /**
     * 
     * @param block
     * @param sequence
     * @param appariement
     * @param modifications
     * @param ontology
     * @return 
     */
    @Override
    public final int doJob(JobBlock block, final Sequence sequence, int[] appariement, boolean[] modifications, final OntoRepresentation ontology, final Motif m) {
        //chercher si il existe une relation de type dans block dans les relations
        RelationJobBlock rjob = ((RelationJobBlock)block);
        int size=sequence.relations.size();

        //on recupere la solution du concept codomaine
        int position_codomaine_dans_sequence = 0; 
        if(block.prev!=null) position_codomaine_dans_sequence = appariement[block.prev.position];
        
        //on recupere la solution pour le concept domaine
        int position_domaine_dans_sequence = 0;

        if(rjob.domaine!=null && rjob.domaine.prev!=null) {
            position_domaine_dans_sequence = appariement[rjob.domaine.prev.position];
        }
        
        //on recupere la solution pour le codomaine precedent si besoin
        int solution_codomaine = 0;
        if(rjob.codomaine!=null) solution_codomaine = appariement[rjob.codomaine.position];
        
        //on recupere la solution pour le domaine (relation potentielle)
        int rel_domaine = appariement[rjob.domaine.position]-1;
        
        int ii = rel_domaine;
        
        //on va recuperer la relation courrante comme etant la relation trouvee par la case domaine
        Integer[] curr_rel = sequence.relations.get(ii);//relation courrante
        
        if(SequenceMatcher.debug) System.out.println("Searching Range..."+block.prev.position+" from"+ii);
        
        //si on a un codomaine precedent et que la solution de celui ci est plus a droite que la solution potentielle actuelle...
        if(rjob.codomaine!=null && solution_codomaine > rel_domaine){
            //si cette solution est le dernier element de notre sequence de relations
            if(solution_codomaine == size){
                //on ne peut pas trouver de relations suivantes donc on sort
                return 0;
            }
            ///...alors notre curseur se place a droite de la solution trouvee pour la case codomaine precedente
            ii = solution_codomaine;
            //on actualise la relation courrante
            curr_rel = sequence.relations.get(ii);
            if(SequenceMatcher.debug) System.out.println("(R) Starting from "+ii+" because "+rel_domaine+" smaller than "+ii+".");
        }
        //si on a peut etre une case codomaine precedente mais la relations potentielle trouvee par le domaine est plus a droite que celle ci
        //la case precedente n'a donc aucun impact, on n'en tient donc pas compte
        else{
            //on demarre avec la meme relation que dans la partie domaine
            if(curr_rel[2] == position_codomaine_dans_sequence){
                //si la position du codomaine correspond a la position trouvee pour le concept dans le bloc au dessus
                if(SequenceMatcher.debug) System.out.println("NICE MATCHING !!!");
                //on a trouve et on sort
                return ii+1;
            }
            //sinon, on va a la relation suivante..
            ii++;
            //...si il existe une relation suivante
            if(ii < size) curr_rel = sequence.relations.get(ii);
            //si y'en a pas, alors on sort parce que l'on n'a rien trouve
            else return 0;
        }
        
        //tant qu'il reste des relationsp potentielles...
        while(ii<size){
            if(SequenceMatcher.debug) System.out.println("(R)"+(ii-1)+" does not match, trying to match relation "+ii+".");
            //si le domaine ne correspond plus, 
            if(curr_rel[1] != position_domaine_dans_sequence){
                if(SequenceMatcher.debug) System.out.println("The domain as changed : ["+(position_domaine_dans_sequence)+"vs"+curr_rel[1]+"]=> exiting...");
                //il n'y a plus de relations potentielles (a cause de l'ordre)
                break;
            }
            //si tout correspond encore
            else{
                //on verifie le codomaine
                if(curr_rel[2] == position_codomaine_dans_sequence){
                    //si la propriete matche aussi
                    if(ontology.isPropertyEqualOrDescendant(block.item, curr_rel[0])){

                        if(SequenceMatcher.debug) 
                            System.out.println("(R)Found potential relation:"+ii+" => "+block.item+" is "+ "equal or D to "+curr_rel[0]);
                        //alors c'est bon et on renvoie la position de la relation qui matche
                        return ii+1;
                    }
                    //si la propriete ne matche pas
                    else{
                        // on va a la relation suivante
                        ii++;
                        //si elle existe
                        if(ii < size) curr_rel = sequence.relations.get(ii);
                        else break;
                        //curr_rel = sequence.relations.get(ii);
                    }
                }
                //si le codomaine ne matche pas
                else{
                    //on va regarder si on est sur un codomaine plus grand ou pas
                    //si oui, on devrait pouvoir sortir ici normalement, grace a l'ordre
                    if(curr_rel[2] > position_codomaine_dans_sequence){
                        break;
                    }
                    //sinon, on va a la relation suivante
                    else{
                        ii++;
                        if(ii < size) curr_rel = sequence.relations.get(ii);
                        else break; 
                    }
                }   
            }
        }
        if(SequenceMatcher.debug) System.out.println("No more solutions for Range....GOING BACK");
        return 0;
    }

    @Override
    public int doJob_with_output(JobBlock block, final Sequence sequence, int[] appariement, boolean[] modifications, final OntoRepresentation ontology, final Motif m, String[] info) {
        StringBuilder _info = new StringBuilder();
        
        //chercher si il existe une relation de type dans block dans les relations
        RelationJobBlock rjob = ((RelationJobBlock)block);
        int size=sequence.relations.size();

        //on recupere la solution du concept codomaine
        int position_codomaine_dans_sequence = 0; 
        if(block.prev!=null){
            position_codomaine_dans_sequence = appariement[block.prev.position];
            _info.append("On est pas dans le premier bloc, on recupere la position du precedent.");
        }
        
        //on recupere la solution pour le concept domaine
        int position_domaine_dans_sequence = 0;

        if(rjob.domaine!=null && rjob.domaine.prev!=null) {
            position_domaine_dans_sequence = appariement[rjob.domaine.prev.position];
            _info.append("Le domaine lie est pas null, on recupere sa position.");
        }
        
        //on recupere la solution pour le codomaine precedent si besoin
        int solution_codomaine = 0;
        if(rjob.codomaine!=null) {
            solution_codomaine = appariement[rjob.codomaine.position];
            _info.append("Il y a un codomaine precedent, on recupere sa position.");
        }
        
        //on recupere la solution pour le domaine (relation potentielle)
        int rel_domaine = appariement[rjob.domaine.position]-1;
        
        int ii = rel_domaine;
        
        //on va recuperer la relation courrante comme etant la relation trouvee par la case domaine
        Integer[] curr_rel = sequence.relations.get(ii);//relation courrante
        
        if(SequenceMatcher.debug) System.out.println("Searching Range..."+block.prev.position+" from"+ii);
        
        //si on a un codomaine precedent et que la solution de celui ci est plus a droite que la solution potentielle actuelle...
        if(rjob.codomaine!=null && solution_codomaine > rel_domaine){
            _info.append("Il y a un codomaine precedent avec une solution invalide (plus a droite la le point de depart actuel).");
            //si cette solution est le dernier element de notre sequence de relations
            if(solution_codomaine == size){
                //on ne peut pas trouver de relations suivantes donc on sort
                _info.append("Et cette solution est la derniere possible dans la sequence, on peut rien trouver.");
                info[0] = _info.toString();
                return 0;
            }
            ///...alors notre curseur se place a droite de la solution trouvee pour la case codomaine precedente
            ii = solution_codomaine;
            _info.append("Alors on se place a droite de cette solution.");
            //on actualise la relation courrante
            curr_rel = sequence.relations.get(ii);
            if(SequenceMatcher.debug) System.out.println("(R) Starting from "+ii+" because "+rel_domaine+" smaller than "+ii+".");
        }
        //si on a peut etre une case codomaine precedente mais la relations potentielle trouvee par le domaine est plus a droite que celle ci
        //la case precedente n'a donc aucun impact, on n'en tient donc pas compte
        else{
            _info.append("Pas de conflit de positions.");
            //on demarre avec la meme relation que dans la partie domaine
            if(curr_rel[2] == position_codomaine_dans_sequence){
                _info.append("La solution actuelle est possible.");
                //si la position du codomaine correspond a la position trouvee pour le concept dans le bloc au dessus
                if(SequenceMatcher.debug) System.out.println("NICE MATCHING !!!");
                //on a trouve et on sort
                
                info[0] = _info.toString();
                return ii+1;
            }
            _info.append("La position actuelle n est pas bonne, on continue.");
            //sinon, on va a la relation suivante..
            ii++;
            //...si il existe une relation suivante
            if(ii < size) {
                curr_rel = sequence.relations.get(ii);
                _info.append("On avance.");
            }
            //si y'en a pas, alors on sort parce que l'on n'a rien trouve
            else {
                _info.append("Il n'y a pas d autre solutions possibles.");
                info[0] = _info.toString();
                return 0;
            }
        }
        _info.append("On passe a la recherche sequentielle.");
        //tant qu'il reste des relationsp potentielles...
        while(ii<size){
            if(SequenceMatcher.debug) System.out.println("(R)"+(ii-1)+" does not match, trying to match relation "+ii+".");
            //si le domaine ne correspond plus, 
            if(curr_rel[1] != position_domaine_dans_sequence){
                if(SequenceMatcher.debug) System.out.println("The domain as changed : ["+(position_domaine_dans_sequence)+"vs"+curr_rel[1]+"]=> exiting...");
                //il n'y a plus de relations potentielles (a cause de l'ordre)
                _info.append("Il n y a plus de retations potentielle (a cause de l ordre de tri)");
                break;
            }
            //si tout correspond encore
            else{
                //on verifie le codomaine
                if(curr_rel[2] == position_codomaine_dans_sequence){
                    //si la propriete matche aussi
                    if(ontology.isPropertyEqualOrDescendant(block.item, curr_rel[0])){

                        if(SequenceMatcher.debug) 
                            System.out.println("(R)Found potential relation:"+ii+" => "+block.item+" is "+ "equal or D to "+curr_rel[0]);
                        //alors c'est bon et on renvoie la position de la relation qui matche
                        _info.append("On a trouve une correspondance.");
                        info[0] = _info.toString();
                        return ii+1;
                    }
                    //si la propriete ne matche pas
                    else{
                        // on va a la relation suivante
                        ii++;
                        //si elle existe
                        if(ii < size) curr_rel = sequence.relations.get(ii);
                        else break;
                        //curr_rel = sequence.relations.get(ii);
                    }
                }
                //si le codomaine ne matche pas
                else{
                    //on va regarder si on est sur un codomaine plus grand ou pas
                    //si oui, on devrait pouvoir sortir ici normalement, grace a l'ordre
                    if(curr_rel[2] > position_codomaine_dans_sequence){
                        break;
                    }
                    //sinon, on va a la relation suivante
                    else{
                        ii++;
                        if(ii < size) curr_rel = sequence.relations.get(ii);
                        else break; 
                    }
                }   
            }
        }
        if(SequenceMatcher.debug) System.out.println("No more solutions for Range....GOING BACK");
        _info.append("Pas de correspondance trouvee.");
        info[0] = _info.toString();
        return 0;
    }

}
