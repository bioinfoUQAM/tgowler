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


public final class AppendConceptTask extends AppariementExtensionTask{

        @Override
        public AppariementStructure extend(AppariementStructure structure) {
            //System.out.println("Append concept:"+this.item[0]);
            Job[] jobs = new Job[1];
            //on ajoute un par un les jobs
            jobs[0] = new TrouveNextConcept();
            //utiliser concept_to_blocks plutot ?
            
            JobBlock last=null;
            if(structure.lastAddedConcept!=null) last = structure.pile_de_taches.get(structure.lastAddedConcept.position);      
            
            //on cree notre nouveau bloc avec l'item, les jobs et le pointeur optionel
            JobBlock b = new JobBlock(this.item[0], jobs, last, null, structure.pile_de_taches.size(), structure.concepts_to_blocks.size());
            
            structure.pile_de_taches.add(b);
            structure.concepts_to_blocks.add(structure.pile_de_taches.get(structure.pile_de_taches.size()-1));
            
            //le concept au dessus correspond au domaine du premier bloc
            if(last!=null) {
                
                JobBlock j = structure.pile_de_taches.get(structure.lastAddedConcept.position);
                
                j.next = structure.pile_de_taches.get(structure.pile_de_taches.size()-1);//lastAddedConcept
                
                structure.pile_de_taches.set(structure.lastAddedConcept.position, j);
                
                
                if(j.firstChild!=null && j.lastChild!=null){
                    //System.out.println("update last son");
                    j.lastChild.next = j.next;
                }
            }
            else{
                structure.firstConceptBlock = structure.pile_de_taches.get(0);
            }
            
            structure.lastAddedConcept = structure.pile_de_taches.get(structure.pile_de_taches.size()-1);
            structure.last_touched_position = structure.pile_de_taches.size()-1;
            return structure;
        }        
    }
