/**
 *
 * @author Enridestroy
 */
package ontopattern16.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import legacy.RawUserSequence;
import ontologyrep2.Concept;
import ontologyrep2.Instance;
import ontologyrep2.OntoRepresentation;
import ontologyrep2.RadixTree.RadixNode;
import ontopatternmatching.Sequence;


public class VerticalSeqDB {
    
    //il faut un index/header des debuts dans le bitset... sinon on va pas s'en sortir...
    
    public static void construire_vdb(int[] vertical_db, ArrayList<Sequence> all_sequences, int nbr_concepts, 
            int nbr_seq, int range_of_one_item, int[] all_sequences_length, int[] all_sequences_sum,  
            final OntoRepresentation ontology, int BLOCK_SIZE, int BLOCK_SIZE_SHIFT, final RadixNode... localNameNodes){
        
        /*final RadixNode localNameNode;
        //final ArrayList<Sequence> userSequences = new ArrayList<>();
        
        if(localNameNodes!=null && localNameNodes.length != 0 && localNameNodes[0] != null){
            localNameNode = localNameNodes[0];
        }
        else localNameNode = ontology.index_instances_by_name.root;*/   
        
        for(int i=0;i!=nbr_seq;i++){
            //final Sequence currSeq = new Sequence();
            final Sequence seq = all_sequences.get(i);
            //final ArrayList<String> uris = seq.getIndividualsLocalNames();
            final int l_seq = all_sequences_length[i];
                        
            for(int z=0; z < l_seq ; z++){
                //System.out.println("with "+indv);
                /*final RadixNode indv_n = ontology.index_instances_by_name.getNode(uris.get(z), localNameNode);
                if(indv_n == null || indv_n.getValue() == null){
                    System.out.println("probleme...");
                    System.exit(1);
                }*/
                
                final Instance individu = ontology.getInstance(seq.objects.get(z));
                final Concept classe = individu.concept;
                final int class_id = classe.index;
                
                //ATTENTION : on ajoute l'id de l'instance et pas du concept !
                //currSeq.objects.add(individu.index);
                
                final int offset_bits = (range_of_one_item * class_id) + all_sequences_sum[i];
                //System.out.println("cid="+class_id+" and all_seq_sum="+all_sequences_sum[i]);
                tw_bitset.set_bit_at_one(offset_bits + z, vertical_db);
                //if(class_id == 2) System.out.println("in seq "+i+" at position "+z+" class "+class_id+" is present => "+tw_bitset.get_bit_at(offset_bits + z, vertical_db));
            }            
        }
        
        
        //tw_bitset.set_bit_at_one((range_of_one_item * 2) + all_sequences_sum[0] + 1, vertical_db);
        tw_bitset.set_bit_at_one((range_of_one_item * 0) + all_sequences_sum[0] + 0, vertical_db);
        tw_bitset.set_bit_at_one((range_of_one_item * 0) + all_sequences_sum[0] + 18, vertical_db);
        //System.out.println(">>>"+tw_bitset.print_bitset(vertical_db));
        
        /*for(int i=0; i < max_id_concept; i++){
            System.out.println(i +" => " + print_db_for_item(i, range_of_one_item, all_sequences_sum, vertical_db));
        }*/
        
        //System.out.println("max="+max_index);
        System.out.println(""+vertical_db.length);
        //System.out.println(""+tw_bitset.get_bit_at(vertical_db.length*30, vertical_db));
        
        //ceci devrait etre dans le main plutot => plus de clarte et on pourrait facilement l'activer ou desactiver...
        
        ///construit_vdb_generalisations(ontology, nbr_concepts, vertical_db, range_of_one_item, all_sequences_sum);
        
        //return userSequences;
    }
    
    public static void construit_vdb_generalisations(final OntoRepresentation ontology, final int nbr_classes, final int[] vertical_db, 
            final int sum_sizes, final int[] all_sequences_sum){
        //lire la matrice d'ancetres ?
        //les clefs de la matrice = fils => liste les parents...
        //comment ne pas faire deux fois le meme travail ???
        //on prend le vecteur de fils. on le place dans vecteur de parent
        //on fait ca pour tous les fils ??????
        //on fait ca uniquement pour les concepts qui ne sont pas parents (feuilles, cad pas de clef)
        //donc on construit l'avant dernier niveau
        //ensuite, tous ceux de l'avant dernier niveau, on recommence... jsuqu'a top
        final Set<Character> feuilles = new HashSet<>();
        feuilles.addAll(ontology.ancestor_matrix_c.matrix.keySet());
        final Character[] sons_keys = feuilles.toArray(new Character[feuilles.size()]);
        
        for(int i = 0;i < sons_keys.length; i++){
                        
            final HashSet<Character> parents = ontology.ancestor_matrix_c.matrix.get(sons_keys[i]);//.toArray(new Character[]);
            final Character[] arr_parents = parents.toArray(new Character[parents.size()]);
            //on parcout tous les parents
            for(int j=0; j < arr_parents.length;j++){
                //on enleve ceux qui sont parents a leur tour
                feuilles.remove(arr_parents[j]);
            }
        }
        
        //normalement ces concepts la sont relativement vides (il se peut qu'une instance soit directement instance d'eux 
        //memes mais plus on monte plus ca devrait etre rare).
        
        //ici on a tous les concepts qui sont fils et jamais parents...
        System.out.println("feuilles="+feuilles.toString());
        System.out.println("depart="+Arrays.toString(sons_keys));
        System.out.println("on a "+feuilles.size()+" / "+nbr_classes+" concepts feuilles");
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
            for(int i=0;i<feuilles_arr.length;i++){

                //


                final HashSet<Character> parents = ontology.ancestor_matrix_c.matrix.get(feuilles_arr[i]);//.toArray(new Character[]);
                if(parents == null) {
                    //System.out.println("parents is null for "+ontology.getConcept((int)feuilles_arr[i]).getName());
                    continue;
                }
                final Character[] arr_parents = parents.toArray(new Character[parents.size()]);
                //on parcout tous les parents
                for(int j=0; j < arr_parents.length;j++){
                    //on enleve ceux qui sont parents a leur tour
                    //feuilles.remove(arr_parents[j]);
                    char p = arr_parents[j];
                    //System.out.println("n="+niveau+" on va balancer "+ontology.getConcept((int)feuilles_arr[i]).getName()+" dans "+ontology.getConcept((int)p).getName());

                    //System.out.println(((int)feuilles_arr[i]) +" => " + print_db_for_item(feuilles_arr[i], sum_sizes, all_sequences_sum, vertical_db));
                    //System.out.println("before was "+((int)p) +" => " + print_db_for_item(p, sum_sizes, all_sequences_sum, vertical_db));
                    
                    
                    tw_bitset.or_same_length_and_apply_to_left_full(vertical_db, vertical_db, (
                            (sum_sizes * (int)p)), //on doit envoyer une position vers le debut d'un block
                            sum_sizes, 
                            (sum_sizes * (int)feuilles_arr[i]));//meme chose ici

                    //System.out.println("and now "+((int)p) +" => " + print_db_for_item(p, sum_sizes, all_sequences_sum, vertical_db));
                    
                    next_level.add(arr_parents[j]);//on ajoute le parent du niveau courrant...
                    //System.exit(1);
                }
            }

        }
        
        /*for(int i=0; i < 157; i++){
            Concept concept = ontology.getConcept(i);
            if(concept != null) System.out.println(concept.getName() +" => " + print_db_for_item(i, sum_sizes, all_sequences_sum, vertical_db));
        }*/
        
        
    }
    
    
    //public static int max_index = 0;
    public static String print_db_for_item(final int class_id, final int sum_sizes, final int[] all_sequences_sum, final int[] vertical_db){
       
        int nbr_sequences = 57;
        StringBuilder sb = new StringBuilder();
        for(int i=0;i < nbr_sequences; i++){
            sb.append("[ ");
            final int offset_bits = (sum_sizes * class_id) + all_sequences_sum[i];
            final int seq_len = (all_sequences_sum[i+1] - all_sequences_sum[i]);
            for(int z=0; z < seq_len;z++){
                
                /*if(max_index < offset_bits + z) {
                    max_index = offset_bits + z;
                    System.out.println("new max offset "+max_index);
                    System.out.println("=>"+(max_index/32)+" et "+(max_index % 32));
                }*/
                sb.append(tw_bitset.get_bit_at(offset_bits + z, vertical_db)).append(", ");
            }
            sb.append("], "+"\\\n");
            //tw_bitset.set_bit_at_one(offset_bits + z, vertical_db);
            //System.out.println("in seq "+i+" at position "+z+" class "+class_id+" is present => "+tw_bitset.get_bit_at(offset_bits + z, vertical_db));
        }
        return sb.toString();
    }
    
    public static int[] compute_and(){
        //remplacer par l'appel au bon bitset
        return new int[0];
    }
}
