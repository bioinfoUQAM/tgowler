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

import java.util.Arrays;

public final class AddRelationTask extends AppariementExtensionTask{
        @Override
        public final AppariementStructure extend(AppariementStructure structure){
            
            //System.out.println("Adding relation:("+this.item[0]+","+this.item[1]+","+this.item[2]+")");
            int dom_pos = this.item[1]-1;
            int rang_pos = this.item[2]-1;
            
            Job[] jobs;
            JobBlock[] blocks = new JobBlock[2];
            int pos = 0;      
            //////////////////////////////////////
            // BLOC 1 - DOMAIN VALIDATION
            //////////////////////////////////////
            //JobBlock domaine_block = this.concepts_to_blocks.get(relation[1]-1);
            
            //JobBlock domaine_block = structure.concepts_to_blocks.get(dom_pos);//les relations commencent a zero mtn
            JobBlock domaine_block = structure.pile_de_taches.get(structure.concepts_to_blocks.get(dom_pos).position);
            if(domaine_block.type!=0){
                System.out.println("not a concept");
                System.exit(1);
            }
            //Ajout du premier bloc a la pile
            jobs = new Job[1];
            //on ajoute un par un les jobs
            jobs[0] = new ValideDomaineRelation();

            //pos = domaine_block.position;
            pos = structure.pile_de_taches.size();
            
            int sib_p = 0;
            if(domaine_block.lastChild!=null){
                sib_p = domaine_block.lastChild.sibling_position+1;
            }
            //if(domaine_block.lastChild!=null) pos = domaine_block.lastChild.position+1;
            //on cree notre nouveau bloc avec l'item, les jobs et le pointeur optionel
            blocks[0] = new RelationJobBlock(this.item[0], jobs, domaine_block, null, 
                    domaine_block.last_domain_added, null, pos, sib_p, (short)1);

            //////////////////////////////////////
            // BLOC 2 - RANGE VALIDATION
            //////////////////////////////////////
            //on recupere le bloc du concept codomaine
            //JobBlock codomaine_block = this.concepts_to_blocks.get(relation[2]-1);
            //JobBlock codomaine_block = structure.concepts_to_blocks.get(rang_pos);//les relations commencent a zero mtn
            JobBlock codomaine_block = structure.pile_de_taches.get(structure.concepts_to_blocks.get(rang_pos).position);
            if(codomaine_block.type!=0){
                System.out.println("not a concept");
                System.exit(1);
            }
            //Ajout du deuxieme bloc a la pile
            jobs = new Job[1];
            //on ajoute un par un les jobs
            jobs[0] = new ValideCodomaineRelation();
            //jobs[1] = new VerifieSiRelationExiste();
            //pos = codomaine_block.position;
            pos = structure.pile_de_taches.size()+1;
            
            sib_p = 0;
            if(codomaine_block.lastChild!=null){
                sib_p = codomaine_block.lastChild.sibling_position+1;
            }
            //if(codomaine_block.lastChild!=null) pos = codomaine_block.lastChild.position;
            //on cree notre nouveau bloc avec l'item, les jobs et le pointeur optionel
            blocks[1] = new RelationJobBlock(this.item[0], jobs, 
                    codomaine_block, codomaine_block.next, blocks[0], 
                    codomaine_block.last_range_added, pos, 
                    sib_p, (short)2);
            //////////////////////////////////////
            // MISE A JOUR DES BLOCS ET CREATION DES LIENS
            //////////////////////////////////////

            
            //pb est ici, utiliser pil de taches plutoto
            
            
            //indique que le prochain traitement a partir de la verification de la relation potentielle
            //du domaine est le matching du concept qui va jouer le role de codomaine
            
            
            //ajouter les blocks dans pile des taches
            //puis modfier les nexts
            
            //blocks[0].next = structure.concepts_to_blocks.get(dom_pos).next;

            //ajoute le fils au block du domaine (ex: A => R1(1))
            //domaine_block.addChild(blocks[0]);
            
            //ajoute le fils au block du codomaine (ex:B => R1(2))
            //codomaine_block.addChild(blocks[1]);
            
            //ajoute le deuxieme fils au block du codomaine (ex: R1(2) => Rel1)
            //codomaine_block.addChild(blocks[2]);
            //construit le pointeur vers le derniere relation ajoutee
            structure.lastAddedRelation = (RelationJobBlock)blocks[1];
            //ajoute cette relation a la liste des relations
            structure.relations_to_blocks.add(structure.lastAddedRelation);
            //ajoute tous les blocks a la pile des taches mais...
            structure.pile_de_taches.addAll(Arrays.asList(blocks));//...la pile ne sert plus a grand chose
            
            
            //structure.concepts_to_blocks.get(dom_pos).addChild(blocks[0]);
            structure.pile_de_taches.get(structure.concepts_to_blocks.get(dom_pos).position).addChild(structure.pile_de_taches.get(structure.pile_de_taches.size()-2));
            //structure.concepts_to_blocks.get(rang_pos).addChild(blocks[1]);
            structure.pile_de_taches.get(structure.concepts_to_blocks.get(rang_pos).position).addChild(structure.pile_de_taches.get(structure.pile_de_taches.size()-1));
            
            
            
            structure.pile_de_taches.get(structure.pile_de_taches.size()-2).next = 
                    structure.pile_de_taches.get(structure.concepts_to_blocks.get(dom_pos).position).next;
            //indique que le prochain traitement a partir de la verification de la relation potentielle
            //du codomaine est le matching des deux appariements de relation trouves
            //blocks[1].next = blocks[2];
            
            //blocks[1].next = structure.concepts_to_blocks.get(rang_pos).next;
            structure.pile_de_taches.get(structure.pile_de_taches.size()-1).next = 
                    structure.pile_de_taches.get(structure.concepts_to_blocks.get(rang_pos).position).next;
            //return blocks[0];//on renvoie le domaine
            //structure.lastTouchedBlock = blocks[0];
            structure.last_touched_position = structure.pile_de_taches.size()-2;
            return structure;
        }
    }
