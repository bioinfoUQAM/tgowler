/**
 *
 * @author Enridestroy
 */

package preprocessing;

import bowl.Converter2Bowl;
import legacy.RawUserWorkflow;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import ontologyrep20.Instance;
import ontologyrep20.OntoRepresentation;
import ontologyrep20.RadixTree;
import ontopatternmatching.Sequence;


public class bowlLoader {
    //charge et transforme les sequences brutes en sequences d'instances ou classes
    public static void loadRawSequences(final ArrayList<RawUserWorkflow> rawUserSequences, final ArrayList<Sequence> userSequences,
                                        final OntoRepresentation ontology, final String rawSequences, final RadixTree.RadixNode localNameNode,
                                        final String namespace){
        if(localNameNode != null){
//            System.out.println("rawUserSequences: " + rawUserSequences);
            for(RawUserWorkflow seq : rawUserSequences){
//                System.out.println(""+seq.toString());
                Sequence currSeq = new Sequence();

                // Get Concepts Indexes!
                for(String indv : seq.getIndividualsLocalNames()){
                    Object get = ontology.index_instances_by_name.get(indv, localNameNode);
                    if(get != null){
                        currSeq.objects.add(((Instance)get).index);
                    }
                    else{
                        System.exit(1);
                    }
                }
                if(currSeq.objects.size() != seq.getIndividualsLocalNames().size()){
//                    System.out.println("au moins un element n'a pas ete trouve....");
                    System.exit(1);
                }
                //System.out.println("maintenant la sequence est : "+currSeq.toString());

                // Get Relation Indexes!
                Iterator iterator = seq.getPropertiesLocalNames().entrySet().iterator();
                while (iterator.hasNext()) {
                    // Get relation
                    Map.Entry rel = (Map.Entry) iterator.next();
                    legacy.Pair subj_obj = (legacy.Pair)rel.getKey();
                    String link_string = rel.getValue().toString().replace("[", "").replace("]", "");
                    Integer link_index = ontology.getRelationByName(namespace+link_string, "").index;//

                    // Add relation to the current sequence
                    final Integer[] _relation = new Integer[3];
                    _relation[0] = (Integer)link_index;
                    _relation[1] = (Integer)subj_obj.getFirst();
                    _relation[2] = (Integer)subj_obj.getSecond();
                    currSeq.relations.add(_relation);
                }


                for(int k=0;k < currSeq.objects.size();k++){
                    currSeq.objects.set(k, ontology.getInstance(currSeq.objects.get(k)).concept.index);
                }

                userSequences.add(currSeq);
            }
        }
        else{
            System.out.println("no local node...");
        }
    }
}
