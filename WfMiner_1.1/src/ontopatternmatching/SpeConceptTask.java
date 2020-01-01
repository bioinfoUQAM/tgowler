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


public final class SpeConceptTask extends AppariementExtensionTask{

        @Override
        public AppariementStructure extend(AppariementStructure structure) {
            //System.out.println("Specialising concept:"+structure.lastAddedConcept.item+" to "+this.item[0]);
            structure.concepts_to_blocks.get(structure.concepts_to_blocks.size()-1).item = this.item[0];
            //structure.lastAddedConcept.solution = 0;
            structure.last_touched_position = structure.pile_de_taches.size()-1;
            return structure;
            
        }
        
        /*//specialise un concept du motif
        public final JobBlock speConcept(int nouveau_concept){
            return this.speConcept(structure.lastAddedConcept, nouveau_concept);
        }*/

        //specialise un concept du motif
        /*public final JobBlock speConcept(int position, int nouveau_concept){
            return this.speConcept(structure.concepts_to_blocks.get(position), nouveau_concept);
            //peut etre ajouter un -1 a position ? si on travaille aussi a partir de 1
        }*/
    }
