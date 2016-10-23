/**
 *
 * @author Enridestroy
 */
package ontopattern16.db;

import java.util.ArrayList;
import legacy.RawUserSequence;
import ontologyrep2.Instance;
import ontologyrep2.OntoRepresentation;
import ontologyrep2.RadixTree;
import ontopatternmatching.Sequence;


public class VerticalSeqInstDB {
    public static ArrayList<Sequence> construire_vdbi(int[] vertical_db, ArrayList<RawUserSequence> all_sequences, 
            int nbr_concepts, int nbr_seq, int range_of_one_item, int[] all_sequences_length, int[] all_sequences_sum,  
            final OntoRepresentation ontology, final int BLOCK_SIZE, final int BLOCK_SIZE_SHIFT, final RadixTree.RadixNode... localNameNodes){
        
        final RadixTree.RadixNode localNameNode;
        final ArrayList<Sequence> userSequences = new ArrayList<>();
        
        if(localNameNodes!=null && localNameNodes.length != 0 && localNameNodes[0] != null){
            localNameNode = localNameNodes[0];
        }
        else localNameNode = ontology.index_instances_by_name.root;
               
        
        //int counts = 0;
        
        for(int i=0;i!=nbr_seq;i++){
            final Sequence currSeq = new Sequence();
            final RawUserSequence seq = all_sequences.get(i);
            final ArrayList<String> uris = seq.getIndividualsLocalNames();
            final int l_seq = all_sequences_length[i];
                        
            for(int z=0; z < l_seq ; z++){
                //System.out.println("with "+indv);
                final RadixTree.RadixNode indv_n = ontology.index_instances_by_name.getNode(uris.get(z), localNameNode);
                if(indv_n == null || indv_n.getValue() == null){
                    System.out.println("probleme...");
                    System.exit(1);
                }
                
                final Instance individu = (Instance)indv_n.getValue();
                
                //final Concept classe = individu.concept;
                //final int class_id = classe.index;
                
                //ATTENTION : on ajoute l'id de l'instance et pas du concept !
                currSeq.objects.add(individu.index);
                
                /*if(counts == 539){
                    System.out.println(">>>"+individu.getName());
                    System.out.println(""+individu.concept.getName());
                    System.exit(1);
                }
                
                counts+= 1;*/
                
                
                /*
                final int offset_bits = (range_of_one_item * class_id) + all_sequences_sum[i];
                //System.out.println("cid="+class_id+" and all_seq_sum="+all_sequences_sum[i]);
                tw_bitset.set_bit_at_one(offset_bits + z, vertical_db);
                //if(class_id == 2) System.out.println("in seq "+i+" at position "+z+" class "+class_id+" is present => "+tw_bitset.get_bit_at(offset_bits + z, vertical_db));
                */
            }     
            userSequences.add(currSeq);
        }
        return userSequences;
    }
}
