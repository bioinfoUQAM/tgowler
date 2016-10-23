/**
 *
 * @author Tomas Martin
 */

package ontopatternmatching;

import ontologyrep2.OntoRepresentation;

public final class ValideDomaineRelation implements Job{
    /**
     * 
     * @param block
     * @param sequence
     * @param appariement
     * @param modifications
     * @param ontology
     * @param m
     * @param sm
     * @return 
     */
    @Override
    public final int doJob(JobBlock block, final Sequence sequence, int[] appariement, boolean[] modifications, final OntoRepresentation ontology, final Motif m) {
        int size=sequence.relations.size();
        RelationJobBlock rjob = ((RelationJobBlock)block);
        int solution = appariement[block.position];
        int prev_solution = 0;
        int prev_prev_domaine=0;
        
        //si on a un concept (normalement toujours vrai)
        if(block.prev!=null) {
            //alors on recupere la solution du concept
            prev_solution = appariement[block.prev.position];
            
            //si on a un concept precedent le concept domaine et qu'il est domaine d'au moins une relation
            if(block.prev.prev!=null && block.prev.prev.last_domain_added!=null){
                //alors on recupere sa position dans la sequence
                prev_prev_domaine = appariement[block.prev.prev.last_domain_added.position];
            }
        }
        
        int solution_domaine = 0;
        if(rjob.domaine!=null) 
            solution_domaine = appariement[rjob.domaine.position];
        
        int j =0;
        Integer[] curr_rel = rjob.curr_rel;
        if(SequenceMatcher.debug) System.out.println("Searching domain..."+block.prev.position);
        
        //si notre concept est domaine d'une autre relation precedente
        if(rjob.domaine!=null){
            //on place notre curseur a droite
            j = solution_domaine;
            if(SequenceMatcher.debug) System.out.println("(D) Starting from "+j+".");
        }
         
        //si on a deja matche cette case au moins une fois avec une solution et que notre concept a une solution
        //mais que la solution du concept a ete modifee et ne correpond plus a la position utilisee lors de notre derniere solution
        //pour la case actuelle
        else if(solution!= 0 && curr_rel!=null && prev_solution != curr_rel[1]){
            //si on est pas dans le premier concept de la liste et qu'il possede au moisn un domaine
            if(block.prev.prev!=null && block.prev.prev.last_domain_added!=null){
                //on place le curseur a droite de cette solution
                j = prev_prev_domaine;
                if(SequenceMatcher.debug) System.out.println("(D) Setting j to "+j+".");
            }
            else{
                //sinon, on repart au debut
                j = 0;if(SequenceMatcher.debug) System.out.println("(D) Reseting j to "+j+".");
            }
        }
        //on est dans le premier concept de la case, on n'a jamais matche correctement la case
        else{
            //que doit on faire ici ? normalement rien du tout...
            if(SequenceMatcher.debug) System.out.println("Cas inconnu!!!!");
        }
        
        if(SequenceMatcher.debug) System.out.println("(D) Starting j at "+j+".");
        
        //System.out.println("Searching for relation:("+block.item+","+block.prev.item+",?)");
        for(int i=j;i<size;i++){
            //on recupere la relation courrante
            curr_rel = sequence.relations.get(i);

            //si le nom de la relation correspond
            if(curr_rel[1] == prev_solution){
                //si le codomaine correspond aussi 
                if(ontology.isPropertyEqualOrDescendant(block.item, curr_rel[0])){
                    if(SequenceMatcher.debug) System.out.println("(D)Found potential relation:"+i+" => "+block.item+" is "
                            + "equal or D to "+curr_rel[0]);
                    rjob.curr_rel = curr_rel;
                    
                    return i+1;
                }
            }
            //si > alors on sort.
        }
        if(SequenceMatcher.debug) System.out.println("No more solutions for Domain....GOING BACK");
        return 0;
    }

    @Override
    public int doJob_with_output(JobBlock block, final Sequence sequence, int[] appariement, boolean[] modifications, final OntoRepresentation ontology, final Motif m, String[] info) {
        int size=sequence.relations.size();
        RelationJobBlock rjob = ((RelationJobBlock)block);
        int solution = appariement[block.position];
        int prev_solution = 0;
        int prev_prev_domaine=0;
        
        StringBuilder _info = new StringBuilder();
        
        //si on a un concept (normalement toujours vrai)
        if(block.prev!=null) {
            //alors on recupere la solution du concept
            prev_solution = appariement[block.prev.position];
            _info.append("On est pas dans le premier bloc, donc on repart de la solution precedente.");
            //si on a un concept precedent le concept domaine et qu'il est domaine d'au moins une relation
            if(block.prev.prev!=null && block.prev.prev.last_domain_added!=null){
                //alors on recupere sa position dans la sequence
                prev_prev_domaine = appariement[block.prev.prev.last_domain_added.position];
                _info.append("Concept precedent et est domaine d'au moins une relation.");
            }
        }
        
        int solution_domaine = 0;
        if(rjob.domaine!=null) 
            solution_domaine = appariement[rjob.domaine.position];
        
        int j =0;
        Integer[] curr_rel = rjob.curr_rel;
        if(SequenceMatcher.debug) System.out.println("Searching domain..."+block.prev.position);
        
        //si notre concept est domaine d'une autre relation precedente
        if(rjob.domaine!=null){
            //on place notre curseur a droite
            j = solution_domaine;
            if(SequenceMatcher.debug) System.out.println("(D) Starting from "+j+".");
            _info.append("On commence a chercher a partir de ").append(j).append(".");
        }
         
        //si on a deja matche cette case au moins une fois avec une solution et que notre concept a une solution
        //mais que la solution du concept a ete modifee et ne correpond plus a la position utilisee lors de notre derniere solution
        //pour la case actuelle
        else if(solution!= 0 && curr_rel!=null && prev_solution != curr_rel[1]){
            _info.append("On a une solution mais la solution du concept lie a ete modifiee entre temps, on doit donc la modifier.");
            //si on est pas dans le premier concept de la liste et qu'il possede au moisn un domaine
            if(block.prev.prev!=null && block.prev.prev.last_domain_added!=null){
                //on place le curseur a droite de cette solution
                j = prev_prev_domaine;
                _info.append("On est pas dans le premier concept et il possede au moins un domaine. On commence a partir de ").append(j).append(".");
                if(SequenceMatcher.debug) System.out.println("(D) Setting j to "+j+".");
            }
            else{
                //sinon, on repart au debut
                j = 0;if(SequenceMatcher.debug) System.out.println("(D) Reseting j to "+j+".");
                _info.append("On repart de zero.");
            }
        }
        //on est dans le premier concept de la case, on n'a jamais matche correctement la case
        else{
            _info.append("Cas inconnu. Quoi qu il arrive, on a rien a faire de special.");
            //que doit on faire ici ? normalement rien du tout...
            if(SequenceMatcher.debug) System.out.println("Cas inconnu!!!!");
        }
        
        if(SequenceMatcher.debug) System.out.println("(D) Starting j at "+j+".");
        _info.append("Debut de la recherche.");
        //System.out.println("Searching for relation:("+block.item+","+block.prev.item+",?)");
        for(int i=j;i<size;i++){
            //on recupere la relation courrante
            curr_rel = sequence.relations.get(i);

            //si le nom de la relation correspond
            if(curr_rel[1] == prev_solution){
                //si le codomaine correspond aussi 
                if(ontology.isPropertyEqualOrDescendant(block.item, curr_rel[0])){
                    if(SequenceMatcher.debug) System.out.println("(D)Found potential relation:"+i+" => "+block.item+" is "
                            + "equal or D to "+curr_rel[0]);
                    rjob.curr_rel = curr_rel;
                    _info.append("Une correspondance a ete trouvee.");
                    info[0] = _info.toString();
                    return i+1;
                }
            }
            //si > alors on sort.
        }
        if(SequenceMatcher.debug) System.out.println("No more solutions for Domain....GOING BACK");
        _info.append("Aucune correspondance n'a ete trouvee.");
        info[0] = _info.toString();
        return 0;
    }

}
