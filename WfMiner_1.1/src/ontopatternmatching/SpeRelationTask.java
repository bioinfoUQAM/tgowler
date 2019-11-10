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


public final class SpeRelationTask extends AppariementExtensionTask{
        /**
        * Comment va t'on proceder ?
        * -par JobBLock
        * -par position dans le motif ?
        * -ou alors on prend le dernier par defaut
         * @param structure
         * @param block
         * @param nouvelle_relation
         * @return 
        */
        //specialise une relation du motif
        /*public final JobBlock speRelation(){
            return this.speRelation(this.empty_structure.lastAddedRelation, new Integer[]{1, 2, 3});
            //OU
            //this.speRelation(this.relations_to_blocks.get(this.relations_to_blocks.size()-1));
        }

        //specialise une relation du motif
        public final JobBlock speRelation(int position, Integer[] nouvelle_relation){
            return this.speRelation(this.empty_structure.relations_to_blocks.get(position), nouvelle_relation);
            //peut etre ajouter un -1 ? si on travaille aussi a partir de 1
        }*/

        //specialise une relation du motif
        @Override
        public final AppariementStructure extend(AppariementStructure structure){   
            if(structure.lastAddedRelation==null){
                System.out.println("can't specialize a null relation");
                System.exit(1);
            }
            //System.out.println("Specialising relation:("+structure.lastAddedRelation.item+") to :("+this.item[0]+","+this.item[1]+","+this.item[2]+")");
            JobBlock[] blocks = new JobBlock[2];
            //on recupere les blocks de domaine et codomaine
            blocks[0] = structure.lastAddedRelation.domaine;
            blocks[1] = structure.lastAddedRelation;
            //pour chaque block...
            for(JobBlock b : blocks){
                b.item = this.item[0];//...on change le label de la nouvelle relation...
                //b.solution = 0;//...et on detruit la solution trouvee
            }
            //structure.lastTouchedBlock = blocks[0];
            
            structure.last_touched_position = structure.lastAddedRelation.domaine.position;//size()-2;
            
            return structure;
            
            //blocks[0];//on renvoie le block qui valide la relation a partir du domaine comme ca
            //on peut commencer directement par lui
        }

        /*@Override
        public JobBlock extend(AppariementStructure structure) {
            return this.speRelation(structure.lastAddedRelation);
        }*/
    }
