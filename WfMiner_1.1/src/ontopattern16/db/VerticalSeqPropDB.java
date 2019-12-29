/**
 *
 * @author Enridestroy
 */
package ontopattern16.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import legacy.RawUserWorkflow;
import ontologyrep2.Concept;
import ontologyrep2.Instance;
import ontologyrep2.OntoRepresentation;
import ontologyrep2.RadixTree;
import ontologyrep2.Relation;
import ontopatternmatching.Workflow;

/**
 * de quelle facon representer les props ?
 * il nous faut un vecteur de bits props/seqid qui dit que telle prop apparait a telle sequence => fast pruning...
 * il nous faut aussi ca pour les concepts ?
 * mais ensuite pour le contenu reel, il y a probablement peu de difference entre un voire deux bitchecks et un vecteur de bits.
 * 
 * on utilise un vecteur de bits pour en une operation valider ou invalider un ajout au motif. avec les props
 *  -> on peut tout check un par un => dependre du nombre d'occurences dans chaque sequence
 *  -> utiliser un vbits pour appliquer les deux bits au motif mais on ne peut pas tous les melanger -> depend aussi du nombre d'occurnces
 *  -> utiliser un vbits global melange pour valider quels sont les choses qui peuvent peut etre marcher puis utiliser les couples de positions pour verifier tout ca ?
 * 
 * @author Enridestroy
 */
public class VerticalSeqPropDB {
    public static void construire_vpdb(int[] vertical_db, ArrayList<Workflow> all_sequences, int nbr_concepts, 
            int nbr_seq, int range_of_one_item, final OntoRepresentation ontology, int BLOCK_SIZE, int BLOCK_SIZE_SHIFT, 
            final RadixTree.RadixNode... localNameNodes){        
        
        int totalProps = 0;
        float avg_props_per_sequence = 0;
        float avg_props_per_position = 0;
        float avg_position_avec_props = 0;
        float avg_ecart_prop = 0;
        int nbr_props_in_sequence = 0;
        int nbr_props_in_position = 0;
        int nbr_positions_with_p = 0;
        int totalNbrPositions = 0;
        int totalNbrPosAvecProp = 0;
        int totalEcart = 0;
        
        //il faut creer un tableau de la taille de tous les itemsets....
        //il faut aussi creer un index/header de seqid => debut de positions
        
        
        //int[][] seqid_to_pos = new int[range_of_one_item][Relation.ID];
        
        int[] itemset2prop = new int[range_of_one_item];
        int[] prop2fatbitmap = new int[range_of_one_item * (Relation.ID*3)];//au maxmum, afficher le % de remplissage
        
        System.out.println("itemset2prop size="+range_of_one_item);
        System.out.println("prop2fatbitmap="+(range_of_one_item * (Relation.ID*3)));
        
        //et eventuellement raboter
        
        //celui ci est extensible...
        int[] fatbitmap = tw_bitset.int_nouveau_bitset(range_of_one_item * Relation.ID, 0);//ca devrait aller si toute prop est codee sur un seul bloc.
        //afficher aussi le % de replissage
                
        //chaque property possede un bitset pour chaque sequence ?
        //chaque property possede un bitset par position fin dans sequence ?
        
        //le bitset contient des portions de sequences qui correspondent a des regroupements en fonction de la fin
        //
        
        //un bitset contient le nombre de groupes de proprietes (Px, Sy)
        
        //travailler avec les itemsets est quand meme pas evident ? sauf si o boucle sur l'index des seqid => id itemsets.
        //la c facile ????
        
        
        /**
         * l'autre chose ca va etre de generaliser...
         * avant on lisait la matrice de descendance et on balancait les fils dans les parents
         * 
         * ici si on fait la meme chose ???
         * on va pas pouvoir le faire tel quel parce que l'on ne connait pas les proprietes commes un array
         * on a les itemsets -> props. on pourrait pour chaque itemset faire le meme traitement en faisant monter ses enfants vers les parents
         * ca devrait peut etre meme aller plus vite car on tient compte uniquement des sequences dans lesquelles les props apparaissent ???
         * 
         * en tout ca c'est une autre approche interessante...
         */
        
        int next_pos_in_prop2fatbitmap = 1;
        int next_free_block_in_bitmap = 1;
        
        //int nbr_positions_with_prop = 0;
        //int newTotalProps = 0;
        for(int p=0;p!=nbr_seq;p++){
            final Workflow currSeq = all_sequences.get(p);
//            ArrayList<Integer> flat_workflow = new ArrayList<>();
//            currSeq.objects.forEach(flat_workflow::addAll);
            final int ssz = currSeq.objects.size();
            
            System.out.println("--------- changement de sequence -------------");
            
            //on recupere la prochaine position pour le prochain bloc
            
            //seqid_to_pos[0] = begin_pos;//si on maintient la taille cumulee on s'en sort...
            
            
            int nbrDistinctProps = (Relation.ID * 3);//on considere qu'on va avoir autant de props qu'on peut en avoir...
            //parce qu'on a trois parties : debut, fin et offset...
            
            for(int i=ssz;i != 0; i--){
                Integer fin = currSeq.objects.get(i-1);
                final int[][] list_of_compat = ontology.matrix_instances_props[fin];
                if(list_of_compat==null){
                    continue;
                }
                System.out.println("--------- changement de position de fin "+i+"-------------");
                
                //ici on recupere les props pour la position de fin
                
                itemset2prop[totalNbrPositions + i] = next_pos_in_prop2fatbitmap;
                int debut_prop_compatibles = itemset2prop[totalNbrPositions + i];
                

                boolean hasOneP = false;
                int max_ecart_pour_pos = 0;
                
                for(int j=i; j !=0 ; j--){
                    Integer debut = currSeq.objects.get(j-1);
                    final int[] real_props = list_of_compat[debut];
                    if(real_props == null || /*real_props[0] == null ||*/ real_props[0] == 0){
                        continue;
                    }
                    
                    
                    //int CURRENT_BLOCK = 0;
                    
                    final int rpsz = real_props[0]+1;
                    for(int z=1;z < rpsz;z++){
                        final Integer[] _relation = new Integer[3];
                        _relation[0] = real_props[z];
                        _relation[1] = j;
                        _relation[2] = i;
                        currSeq.relations.add(_relation);
                        System.out.println("on a une propriete "+(ontology.getRelation(real_props[z]).getName())+" aux positions "+j+" et "+i);
                        final Instance codomaine = ontology.getInstance(fin);
                        final Instance domaine = ontology.getInstance(debut);
                        System.out.println("codomaine="+codomaine.getName()+" instance of "+codomaine.concept.getName());
                        System.out.println("domaine="+domaine.getName()+" instance of "+domaine.concept.getName());
                        totalProps += 1;
                        nbr_props_in_sequence += 1;
                        nbr_props_in_position += 1;
                        hasOneP = true;
                        
                        int ecart = i - j;
                        totalEcart += ecart;
                        
                        if(max_ecart_pour_pos < ecart) max_ecart_pour_pos = ecart;
                        //balancer le no seq => position de fin (i) => pid prop => alter bitset
                        //noseq * positionfin
                        
                        //int begin_pos = next_block_pos;
                        //seqid_to_pos[0][real_props[z]] = begin_pos;//indique le a l'itemset x, et pour la propriete realprops[z]
                        //le debut de block est a l'offset...
                        //on fait +32 pour prendre le bloc suivant comme extremite
                        //dans ce cas, on va devoir faire des bitshift et des masquage...
                        
                        //on peut le traiter facilement a l'envers et le retourner a la fin ???
                        //de toute facon on travaille sur des int. pour depasser ca, il faudrait qu'une prop pour une position ait un ecart > 32 ???
                        //semble peu probable mais pourrait arriver...
                        
                        
                        //on doit placer ca a un endroit en particulier dans le bitvector...
                        
                        //comment facilement intersecter ca avec un vecteur de concepts ?
                        
                        //ca veut dire que l'on considere chaque prop comme une case du tableau => full matrix...
                        int fin_bitmap_itemsetprop = 0;
                        int debut_bitmap_itemsetprop = prop2fatbitmap[debut_prop_compatibles + real_props[z]];//on recupere le first bloc pour une prop a une position donnee
                        System.out.println("debut prop is "+debut_bitmap_itemsetprop);
                        if(debut_bitmap_itemsetprop == 0){
                            debut_bitmap_itemsetprop = next_free_block_in_bitmap;
                            System.out.println("premiere fois!!!! => "+debut_bitmap_itemsetprop);
                        }
                        else{
                            fin_bitmap_itemsetprop = prop2fatbitmap[debut_prop_compatibles + (Relation.ID + real_props[z])];//on recupere la fin...
                            System.out.println("pas la premiere fois !!!!");
                        }
                        System.out.println("fin prop is "+fin_bitmap_itemsetprop);
                        //comment on connait le nombre de blocs necessaires ???
                        
                                                
                        
                        //on va calculer la position de depart dans le bloc
                        final int no_block = i >> 5;//diviser par 32 = 2^5
                        
                        if(no_block != 0){
                            System.out.println("on veut le block +"+no_block);
                            System.out.println("dans la sequence no "+p);
                            
                            final int pos_base = i & 31;//modulo 32
                            
                            final int pos_prop = j & 31;
                            final int no_block_prop = j >> 5;
                            //la propriete va etre a cheval sur deux blocs...
                            if(no_block_prop != no_block){
                                if(no_block - no_block_prop > 2){
                                    System.out.println("on a un ecart plus grand que deux blocs => +2int");
                                    System.out.println("seul de cout tolerable depasse...Il faut trouver une solution...");
                                    System.exit(1);
                                }
                                //ici c'est le cas difficile, il va falloir ajouter un nouveau bloc et tout decaler vers la droite
                                //toute la bd... ou alors rajouter un niveau supplementaire d'abstraction... mais ca allourdit l'exploitation...
                                final int block_for_end = debut_bitmap_itemsetprop + no_block;
                                fatbitmap[block_for_end] |= (1 << pos_base);//on positionne le bit a 1 de fin
                                
                                final int block_for_begin = debut_bitmap_itemsetprop + no_block_prop;
                                fatbitmap[block_for_begin] |= (1 << pos_prop);//on positionne le bit 1 de depart
                                {
                                    StringBuilder sb= new StringBuilder();
                                    for(int m=0;m < 32;m++){
                                        sb.append(((fatbitmap[block_for_end] & (1 << m))!=0) ? 1 : 0);
                                    }
                                    System.out.println("end>>>"+sb.toString());
                                }
                                {
                                    StringBuilder sb= new StringBuilder();
                                    for(int m=0;m < 32;m++){
                                        sb.append(((fatbitmap[block_for_begin] & (1 << m))!=0) ? 1 : 0);
                                    }
                                    System.out.println("begin>>>"+sb.toString());
                                }
                                /**
                                 * pour l'instant on gere pas ce cas ^^
                                 */
                                System.out.println("ecart entre position de fin et depart a cheval sur deux blocs...");
                                //System.out.println("on ne gere pas ce cas ci pour l'instant");
                                System.out.println("cad base_pos="+pos_base);
                                System.out.println("prop_pos="+pos_prop);
                                System.out.println("block base="+no_block);
                                System.out.println("block prop="+no_block_prop);
                                //System.exit(1);
                            }
                            else{
                                //ici non ca rentre dans meme dans un seul block...
                                
                                final int current_block = debut_bitmap_itemsetprop + no_block;
                                fatbitmap[current_block] |= (1 << pos_prop);
                                fatbitmap[current_block] |= (1 << pos_base);
                                {
                                    StringBuilder sb= new StringBuilder();
                                    for(int m=0;m < 32;m++){
                                        sb.append(((fatbitmap[current_block] & (1 << m))!=0) ? 1 : 0);
                                    }
                                    System.out.println(">>>"+sb.toString());
                                }
                            }
                            
                            if(fin_bitmap_itemsetprop == 0){
                                System.out.println("c'est la premiere fois que l'on utilise la prop"+real_props[z]+" a l'itemset "+(totalNbrPositions + i)+" dans seq "+p);
                                //c first time qu'on modifie cette prop a cette position
                                prop2fatbitmap[debut_prop_compatibles + real_props[z]] = debut_bitmap_itemsetprop;
                                prop2fatbitmap[debut_prop_compatibles + Relation.ID + +real_props[z]] = debut_bitmap_itemsetprop + no_block + 1;
                                prop2fatbitmap[debut_prop_compatibles + Relation.ID + Relation.ID + real_props[z]] = no_block_prop;//first offset ??? eviter les operations inutiles d'intersection vide
                                next_free_block_in_bitmap += (no_block_prop+1);
                                System.out.println("debut is "+debut_bitmap_itemsetprop);
                                System.out.println("fin is at "+(debut_bitmap_itemsetprop+no_block));
                                System.out.println("first block is at "+no_block_prop);
                                System.out.println("now next free block is at "+next_free_block_in_bitmap);
                            }
                            else{
                                System.out.println("c'est PAS la premiere fois, le vecteur de bits est suppose deja exister...");
                                System.out.println(">>>"+fatbitmap[debut_bitmap_itemsetprop + no_block]);
                                {
                                    StringBuilder sb= new StringBuilder();
                                    for(int m=0;m < 32;m++){
                                        sb.append(((fatbitmap[debut_bitmap_itemsetprop + no_block] & (1 << m))!=0) ? 1 : 0);
                                    }
                                    System.out.println(">>>"+sb.toString());
                                }
                                System.out.println("now next free block is at "+next_free_block_in_bitmap);
                            }
                            
                        }
                        else{
                            fatbitmap[debut_bitmap_itemsetprop] |= (1 << j);
                            
                            //dans ce cas, on ne peut pas avoir un ecart > 32, donc tout va fitter dans un seul int
                             //on doit juste placer le bit qui correspond au debut de prop a 1
                            
                            if(fin_bitmap_itemsetprop == 0){
                                //c first time qu'on modifie cette prop a cette position
                                
                                fatbitmap[debut_bitmap_itemsetprop] |= (1 << i);
                                
                                prop2fatbitmap[debut_prop_compatibles + real_props[z]] = debut_bitmap_itemsetprop;
                                prop2fatbitmap[debut_prop_compatibles + Relation.ID + real_props[z]] = debut_bitmap_itemsetprop+1;
                                prop2fatbitmap[debut_prop_compatibles + Relation.ID + Relation.ID + real_props[z]] = 0;//offset
                                next_free_block_in_bitmap += 1;
                                
                                System.out.println("c'est la premiere fois que l'on utilise la prop"+real_props[z]+" a l'itemset "+(totalNbrPositions + i)+" dans seq "+p);
                                //c first time qu'on modifie cette prop a cette position
                                System.out.println("debut is "+debut_bitmap_itemsetprop);
                                System.out.println("fin is at "+(debut_bitmap_itemsetprop+no_block));
                                System.out.println("first block is at "+0);
                            }
                            else{
                                System.out.println("c'est PAS la premiere fois, le vecteur de bits est suppose deja exister...");
                                //System.out.println(">>>"+fatbitmap[debut_bitmap_itemsetprop]);
                            }
                            
                            {
                                StringBuilder sb= new StringBuilder();
                                for(int m=0;m < 32;m++){
                                    sb.append(((fatbitmap[debut_bitmap_itemsetprop] & (1 << m))!=0) ? 1 : 0);
                                }
                                System.out.println(">>>"+sb.toString());
                            }
                            System.out.println("now next free block is at "+next_free_block_in_bitmap);
                        }
                        
                        //final int bEcart = BLOCK_SIZE - ecart;//on calcule l'ecart quand meme pour les statistiques...                        
                    }
                    //CURRENT_BLOCK = ~CURRENT_BLOCK;//on le retourne
                    
                }
                
                //next_block_pos += max_ecart_pour_pos;//ecart entre les position et la prop la plus loin
                //ne pas oublier le /32 ???? mais dans ce cas la, les blocs vont etre tout petits????
                
                if(hasOneP) nbr_positions_with_p += 1;
                
                next_pos_in_prop2fatbitmap += nbrDistinctProps;
            }
            avg_props_per_position += (nbr_props_in_position/(float)nbr_positions_with_p);//nombre de proprietes par positions dans la sequence
            //System.out.println(">>"+(nbr_props_in_position / (float)ssz));
            avg_position_avec_props += (nbr_positions_with_p / (float)ssz);

            System.out.println("% positions avec props sur total pos = "+(nbr_positions_with_p / (float)ssz)+" => ("+nbr_positions_with_p+"/"+ssz+")");
            System.out.println("avg nbr props par pos ="+(nbr_props_in_position/(float)nbr_positions_with_p)+" => ("+nbr_props_in_position+"/"+nbr_positions_with_p+")");
            nbr_positions_with_p = 0;
            totalNbrPosAvecProp += nbr_positions_with_p;
            nbr_props_in_position = 0;
            
            totalNbrPositions += ssz;
            
        }
        System.out.println("##############################");
        System.out.println("avg props per seq="+(nbr_props_in_sequence / (float)nbr_seq));
        System.out.println("avg props per position="+(avg_props_per_position / (float)nbr_seq));
        System.out.println("avg pos avec props ="+(avg_position_avec_props / (float)nbr_seq));
        System.out.println("avg ecart entre depart-fin="+(totalEcart / (float)totalProps));
        
        int notUsedItemSets = 0;int notUsedPropsInAllPositions = 0;int notUsedBlocks = 0;
        for(int i=0;i != itemset2prop.length; i++){
            System.out.println("############# "+i+" #####################");
            final int cur = itemset2prop[i];
            if(cur == 0){
                notUsedItemSets+=1;continue;
            }    
            //final int debut_props = prop2fatbitmap[cur];//on recupere le debut des props pour cet itemset
            //on boucle sur toutes les props
            for(int j=0;j < Relation.ID; j++){
                if(prop2fatbitmap[cur + j] == 0){
                    notUsedPropsInAllPositions+=1;continue;
                }
                System.out.println("-------- "+j+" -------------");
                final int begin_block = prop2fatbitmap[cur + j];
                final int end_block = prop2fatbitmap[cur + Relation.ID + j];
                final int offset_blocks = prop2fatbitmap[cur + Relation.ID + Relation.ID + j];
                
                notUsedBlocks+=offset_blocks;
                //on boucle sur les blocs consommes
                for(int z=begin_block+offset_blocks;z != end_block; z++){
                    final int bitset = fatbitmap[z];
                    {
                        StringBuilder sb= new StringBuilder();
                        for(int m=0;m < 32;m++){
                            sb.append(((bitset & (1 << m))!=0) ? 1 : 0);
                        }
                        int cb = (z-(begin_block+offset_blocks));
                        System.out.println("["+(cb*32)+" - "+(((cb+1)*32)-1)+"]>>>"+sb.toString());
                    }
                    if(offset_blocks!=0 && Integer.bitCount(bitset)<2){
                        System.out.println("too few bits...");
                        System.out.println("offset="+offset_blocks);
                        System.out.println("begin="+begin_block);
                        System.out.println("end="+end_block);
                        System.out.println("z="+z);
                        System.exit(1);
                    }
                }
            }
            
        }
        construire_vpdb_generalisations(ontology, Relation.ID, prop2fatbitmap, itemset2prop, fatbitmap, next_free_block_in_bitmap);
    }
    
    /**
     * 
     * @param ontology
     * @param nbr_props
     * @param prop2fatbitmap
     * @param itemset2prop
     * @param fatbitmap 
     * @param next_free_block_in_bitmap 
     */
    public static void construire_vpdb_generalisations(final OntoRepresentation ontology, final int nbr_props, 
            final int[] prop2fatbitmap, final int[] itemset2prop, final int[] fatbitmap, int next_free_block_in_bitmap){
        
        final Set<Character> feuilles = new HashSet<>();
        feuilles.addAll(ontology.ancestor_matrix_r.matrix.keySet());
        final Character[] sons_keys = feuilles.toArray(new Character[feuilles.size()]);
        final int ll = sons_keys.length;
        for(int i = 0;i != ll; ++i){
                        
            final HashSet<Character> parents = ontology.ancestor_matrix_r.matrix.get(sons_keys[i]);//.toArray(new Character[]);
            final Character[] arr_parents = parents.toArray(new Character[parents.size()]);
            //on parcout tous les parents
            final int lll = arr_parents.length;
            for(int j=0; j != lll;++j){
                //on enleve ceux qui sont parents a leur tour
                feuilles.remove(arr_parents[j]);
            }
        }
        
        //normalement ces concepts la sont relativement vides (il se peut qu'une instance soit directement instance d'eux 
        //memes mais plus on monte plus ca devrait etre rare).
        
        //ici on a tous les concepts qui sont fils et jamais parents...
        System.out.println("feuilles="+feuilles.toString());
        System.out.println("depart="+Arrays.toString(sons_keys));
        System.out.println("on a "+feuilles.size()+" / "+nbr_props+" relations feuilles");
        //pour chacun des fils, on va or son vecteur avec son parent
        
        final HashSet<Character> next_level = new HashSet<>();
        next_level.addAll(feuilles);
        
        int niveau = 0;
        while(!next_level.isEmpty()){
            //System.out.println(""+next_level.toString());
            niveau += 1;
            //tant qu'il y a des parents...
            Character[] feuilles_arr = next_level.toArray(new Character[next_level.size()]);
            //System.out.println("il reste "+feuilles_arr.length+" a traiter");
            next_level.clear();
            final int lll = feuilles_arr.length;
            for(int i=0;i!=lll;++i){

                //


                final HashSet<Character> parents = ontology.ancestor_matrix_r.matrix.get(feuilles_arr[i]);//.toArray(new Character[]);
                if(parents == null) {
                    System.out.println("parents is null for "+ontology.getRelation((int)feuilles_arr[i]).getName());
                    continue;
                }
                final Character[] arr_parents = parents.toArray(new Character[parents.size()]);
                //on parcout tous les parents
                for(int j=0; j < arr_parents.length;j++){
                    //on enleve ceux qui sont parents a leur tour
                    //feuilles.remove(arr_parents[j]);
                    char p = arr_parents[j];
                    
                    
                    
                    for(int h=0;h != itemset2prop.length; h++){
                        //System.out.println("############# "+i+" #####################");
                        final int cur = itemset2prop[h];
                        if(cur == 0){
                            //System.out.println("nothing for that itemset...");
                            continue;
                        }    
                        //final int debut_props = prop2fatbitmap[cur];//on recupere le debut des props pour cet itemset
                        //on boucle sur toutes les props
                        
                        //ici il faut boucler sur les 
                        final int fils_range;
                        final int begin_block_fils;
                        final int end_block_fils;
                        final int offset_blocks_fils;
                        boolean propWasInItemset = false;
                        {
                            
                            int actual_pos = cur + feuilles_arr[i];
                            if(prop2fatbitmap[actual_pos] == 0){
                                continue;
                            }
                            propWasInItemset = true;
                            //System.out.println("-------- "+g+" -------------");
                            begin_block_fils = prop2fatbitmap[actual_pos];
                            end_block_fils = prop2fatbitmap[actual_pos + Relation.ID];
                            offset_blocks_fils = prop2fatbitmap[actual_pos + Relation.ID + Relation.ID];
                            
                            fils_range = end_block_fils - begin_block_fils;
                            
                            //on va devoir balancer chaque bloc vers celui du parent !!!! (qui est celui ci)
                            /*for(int z=begin_block_fils+offset_blocks_fils;z != end_block_fils; z++){
                                final int bitset = fatbitmap[z];
                                {
                                    StringBuilder sb= new StringBuilder();
                                    for(int m=0;m < 32;m++){
                                        sb.append(((bitset & (1 << m))!=0) ? 1 : 0);
                                    }
                                    int cb = (z-(begin_block_fils+offset_blocks_fils));
                                    System.out.println("on va devoir balancer le "+(cb)+"e block de "+feuilles_arr[i]+" dans "+(int)p);
                                    System.out.println(ontology.getRelation((int)feuilles_arr[i]).getName()+" --> "+ontology.getRelation((int)p).getName());
                                    System.out.println("["+(cb*32)+" - "+(((cb+1)*32)-1)+"]>>>"+sb.toString());
                                }
                            }*/
                        }
                        
                        //comment faire quand on a un element plus a droite que le plus a droite du parent ??
                        //doit on decaler ? 
                        if(propWasInItemset){
                            {
                                int actual_pos = cur + (int)p;
                                int end_block = 0;
                                int begin_block = prop2fatbitmap[actual_pos];//on recupere le first bloc pour une prop a une position donnee
                                //System.out.println("debut prop is "+begin_block);
                                if(begin_block == 0){
                                    begin_block = next_free_block_in_bitmap;
                                    System.out.println(h + " premiere fois!!!! => "+begin_block);
                                    System.out.println(ontology.getRelation((int)feuilles_arr[i]).getName()+" --> "+ontology.getRelation((int)p).getName());
                                    end_block = begin_block + (end_block_fils - begin_block_fils);
                                    
                                    //replace par system.arraycopy()
                                    for(int z=begin_block;z != end_block; z++){
                                        //System.out.println("fils="+(begin_block_fils + (z-begin_block)));
                                        //System.out.println("parent="+z);
                                        if(fatbitmap[z]!=0){
                                            System.out.println("il y a deja des choses ici...");
                                            {
                                                StringBuilder sb= new StringBuilder();
                                                for(int m=0;m < 32;m++){
                                                    sb.append(((fatbitmap[z] & (1 << m))!=0) ? 1 : 0);
                                                }
                                                int cb = (z-(begin_block_fils+offset_blocks_fils));
                                                System.out.println("["+(cb*32)+" - "+(((cb+1)*32)-1)+"]>>>"+sb.toString());
                                            }
                                            System.exit(1);
                                        }
                                        fatbitmap[z] = fatbitmap[begin_block_fils + (z-begin_block)];
                                        {
                                            StringBuilder sb= new StringBuilder();
                                            for(int m=0;m < 32;m++){
                                                sb.append(((fatbitmap[z] & (1 << m))!=0) ? 1 : 0);
                                            }
                                            int cb = (z-(begin_block));
                                            System.out.println(fatbitmap[z] + " Parent ("+begin_block+")before ["+(cb*32)+" - "+(((cb+1)*32)-1)+"]>>>"+sb.toString());
                                        }
                                    }
                                    
                                    
                                    //dans ce cas on va devoir le copier (le fils...)
                                    prop2fatbitmap[actual_pos] = begin_block;
                                    prop2fatbitmap[actual_pos + Relation.ID] = begin_block+fils_range;//ceci semble faire l'affaire pour l'instant car a cause des sequences courtes...
                                    //jamais plus de 2 blocks donc ca va... mais il va falloir trouver une solution.
                                    prop2fatbitmap[actual_pos + Relation.ID + Relation.ID] = 0;//offset
                                    next_free_block_in_bitmap += (end_block - begin_block);
                                }
                                else{
                                    System.out.println(h + " deja rencontre " +ontology.getRelation((int)p).getName());
                                    System.out.println("fils is "+ontology.getRelation((int)feuilles_arr[i]).getName());
                                    end_block = prop2fatbitmap[actual_pos + Relation.ID];//on recupere la fin...
                                    //System.out.println("pas la premiere fois !!!!");
                                    
                                    final int offset_blocks = prop2fatbitmap[actual_pos + Relation.ID + Relation.ID];
                                
                                    //final int range = end_block - begin_block;

                                    /*if(range < fils_range){
                                        System.out.println("range="+range+", fils range="+fils_range);
                                        //ici il faut etendre le parent...
                                        System.exit(1);
                                    }*/

                                    
                                    for(int z=begin_block+offset_blocks;z != end_block; z++){
                                        //System.out.println("fils="+(begin_block_fils + (z-begin_block)));
                                        //System.out.println("parent="+z);
                                        /*if(fatbitmap[z]!=0){
                                            System.out.println("il y a deja des choses ici...");
                                            {
                                                StringBuilder sb= new StringBuilder();
                                                for(int m=0;m < 32;m++){
                                                    sb.append(((fatbitmap[z] & (1 << m))!=0) ? 1 : 0);
                                                }
                                                int cb = (z-(begin_block_fils+offset_blocks_fils));
                                                System.out.println("["+(cb*32)+" - "+(((cb+1)*32)-1)+"]>>>"+sb.toString());
                                            }
                                            System.exit(1);
                                        }*/
                                        
                                        {
                                            StringBuilder sb= new StringBuilder();
                                            for(int m=0;m < 32;m++){
                                                sb.append(((fatbitmap[z] & (1 << m))!=0) ? 1 : 0);
                                            }
                                            int cb = (z-(begin_block+offset_blocks));
                                            System.out.println(fatbitmap[z] + " Parent ("+begin_block+")before ["+(cb*32)+" - "+(((cb+1)*32)-1)+"]>>>"+sb.toString());
                                        }
                                        {
                                            StringBuilder sb= new StringBuilder();
                                            for(int m=0;m < 32;m++){
                                                sb.append(((fatbitmap[begin_block_fils + (z-begin_block)] & (1 << m))!=0) ? 1 : 0);
                                            }
                                            int cb = (z-(begin_block+offset_blocks));
           
                                            System.out.println(fatbitmap[begin_block_fils + (z-begin_block)]+" Fils ("+begin_block_fils+")  ["+(cb*32)+" - "+(((cb+1)*32)-1)+"]>>>"+sb.toString());
                                        }
                                        
                                        fatbitmap[z] |= fatbitmap[begin_block_fils + (z-begin_block)];
                                        
                                        System.out.println("on va devoir faire un OR:");
                                        {
                                            StringBuilder sb= new StringBuilder();
                                            for(int m=0;m < 32;m++){
                                                sb.append((( fatbitmap[z] & (1 << m))!=0) ? 1 : 0);
                                            }
                                            int cb = (z-(begin_block+offset_blocks));
                                            System.out.println(fatbitmap[z] + " Apres OR ["+(cb*32)+" - "+(((cb+1)*32)-1)+"]>>>"+sb.toString());
                                        }
                                    }
                                    
                                    
                                    //on va devoir balancer chaque bloc vers celui du parent !!!! (qui est celui ci)
                                    
//                                    for(int z=begin_block+offset_blocks;z != end_block; z++){
//                                        final int bitset = fatbitmap[z];
//                                        /*{
//                                            StringBuilder sb= new StringBuilder();
//                                            for(int m=0;m < 32;m++){
//                                                sb.append(((bitset & (1 << m))!=0) ? 1 : 0);
//                                            }
//                                            int cb = (z-(begin_block+offset_blocks));
//                                            ///System.out.println("on va devoir balancer le "+(cb)+"e block de "+feuilles_arr[i]+" dans "+(int)p);
//                                            //System.out.println(ontology.getRelation((int)feuilles_arr[i]).getName()+" --> "+ontology.getRelation((int)p).getName());
//                                            System.out.println("dans ["+(cb*32)+" - "+(((cb+1)*32)-1)+"]>>>"+sb.toString());
//                                        }*/
//                                        System.out.println("on va devoir faire un OR");
//                                    }
                                    //System.exit(1);
                                }
                                
                                
                                //if(prop2fatbitmap[actual_pos] == 0){
                                    //System.out.println("no appearance of "+ontology.getRelation((int)feuilles_arr[i]).getName()+" --> "+ontology.getRelation((int)p).getName());
                                    //System.out.println("rien pour le parent dans l'itemset courrant. on va devoir le creer.");
                                    //continue;
                                //}
                                //System.out.println("le parent est bien la.");
                                //System.exit(1);//ceci n'arrive pas non plus
                                
                                //System.out.println("-------- "+g+" -------------");
                                //final int begin_block = prop2fatbitmap[actual_pos];
                                //final int end_block = prop2fatbitmap[actual_pos + Relation.ID];
                                
                            }
                        }
                    }
                    
                    
                    //System.out.println("n="+niveau+" on va balancer "+ontology.getConcept((int)feuilles_arr[i]).getName()+" dans "+ontology.getConcept((int)p).getName());

                    //System.out.println(((int)feuilles_arr[i]) +" => " + print_db_for_item(feuilles_arr[i], sum_sizes, all_sequences_sum, vertical_db));
                    //System.out.println("before was "+((int)p) +" => " + print_db_for_item(p, sum_sizes, all_sequences_sum, vertical_db));
                    
                    
//                    tw_bitset.or_same_length_and_apply_to_left_full(vertical_db, vertical_db, sum_sizes * (int)p, //on doit envoyer une position vers le debut d'un block
//                            sum_sizes, sum_sizes * (int)feuilles_arr[i]);//meme chose ici

                    //System.out.println("and now "+((int)p) +" => " + print_db_for_item(p, sum_sizes, all_sequences_sum, vertical_db));
                    
                    next_level.add(arr_parents[j]);//on ajoute le parent du niveau courrant...
                }
            }
            //break;//pour l'instant !!!
        }        
    
        /*for(Relation p : ontology.root_relations){
            System.out.println(""+p.getName());
        }*/
    
    }
    
    
    
    /*
    public static void construire_vpdb(int[] vertical_db, ArrayList<Workflow> all_sequences, int nbr_concepts, 
            int nbr_seq, int range_of_one_item, final OntoRepresentation ontology, int BLOCK_SIZE, int BLOCK_SIZE_SHIFT, 
            final RadixTree.RadixNode... localNameNodes){  
    */
}
