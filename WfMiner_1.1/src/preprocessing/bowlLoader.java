/**
 *
 * @author Enridestroy
 */

package preprocessing;

import legacy.RawUserWorkflow;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import ontologyrep2.Concept;
import ontologyrep2.Instance;
import ontologyrep2.LinkedList;
import ontologyrep2.OntoRepresentation;
import ontologyrep2.RadixTree;
import ontologyrep2.RadixTree.RadixNode;
import ontologyrep2.Relation;
import ontologyrep2.Triplet;
import ontopatternmatching.Sequence;


public class bowlLoader {
    //charge et transforme les sequences brutes en sequences d'instances ou classes
    public static void loadRawSequences(final ArrayList<RawUserWorkflow> rawUserSequences, final ArrayList<Sequence> userSequences, 
            final OntoRepresentation ontology, final String rawSequences, final RadixNode localNameNode){
        if(localNameNode != null){
            for(RawUserWorkflow seq : rawUserSequences){
                //System.out.println(""+seq.toString());
                Sequence currSeq = new Sequence();
                //final ArrayList<Integer> classSequence = new ArrayList<>();
                for(String indv : seq.getIndividualsLocalNames()){
                    //System.out.println("with "+indv);
                    Object get = ontology.index_instances_by_name.get(indv, localNameNode);
                    if(get != null){
                        //System.out.println("which is "+((Instance)get).index);
                        //System.out.println("and is of type "+((Instance)get).concept.index);
                        
                        //classSequence.add(((Instance)get).concept.index);
                        currSeq.objects.add(((Instance)get).index);
                    }
                    else{
                        System.out.println(">>>"+indv);
                        System.out.println("is not in the ontology...");
                        /*Object get1 = ontology.index_instances_by_name.get("http://www.semanticweb.org/ahmedhalioui/ontologies/2015/7/untitled-ontology-8#DNA", 
                                ontology.index_concepts_by_name.root);
//                        System.out.println(""+get1);*/
                        System.exit(1);
                    }
                }
                if(currSeq.objects.size() != seq.getIndividualsLocalNames().size()){
//                    System.out.println("au moins un element n'a pas ete trouve....");
                    System.exit(1);
                }
                //System.out.println("maintenant la sequence est : "+currSeq.toString());
                int totalProps = 0;
                int newTotalProps = 0;
                //ArrayList<Integer> propSequence = new ArrayList<>();
                for(int i=0;i < currSeq.objects.size();i++){
                    for( int j = i; j < currSeq.objects.size(); j++ ){
                        
                        int[][] list_of_compat = ontology.matrix_instances_props[currSeq.objects.get(j)];
                        if(list_of_compat==null){
                            continue;
                        }
                        int[] real_props =  list_of_compat[currSeq.objects.get(i)];
                        if(real_props == null || /*real_props[0] == null ||*/ real_props[0] == 0){
                            continue;
                        }
                        for(int z=1;z < real_props[0]+1;z++){
                            //System.out.println("compatible props "+real_props[z]);
                            //Relation relation = ontology.getRelation(real_props[z]);
                            //System.out.println(""+relation.index+" and "+relation.getName());
                            newTotalProps += 1;
                            
                            final Integer[] _relation = new Integer[3];
                            _relation[0] = real_props[z];
                            _relation[1] = i + 1;
                            _relation[2] = j + 1;
                            currSeq.relations.add(_relation);
                        }
                        /*ArrayList<Triplet> nonRootProperties = ontology.getNonRootProperties(ontology.getConcept(currSeq.objects.get(i)), ontology.getConcept(currSeq.objects.get(j)));
                        totalProps += nonRootProperties.size();
                        for(Triplet t : nonRootProperties){
                            //propSequence.add(t.relation.index);
                            
                            final Integer[] relation = new Integer[3];
                            relation[0] = t.relation.index;
                            relation[1] = i + 1;//on commence a pos 1
                            relation[2] = j + 1;
                            //currSeq.relations.add(relation);
                        }
                        //System.out.println("NR on a "+nonRootProperties.size()+" props a ajouter...");
                        ArrayList<Triplet> rootProperties = ontology.getRootProperties(ontology.getConcept(currSeq.objects.get(i)), ontology.getConcept(currSeq.objects.get(j)));
                        totalProps += rootProperties.size();
                        for(Triplet t : rootProperties){
                            //propSequence.add(t.relation.index);
                            
                            final Integer[] relation = new Integer[3];
                            relation[0] = t.relation.index;
                            relation[1] = i + 1;
                            relation[2] = j + 1;
                            //currSeq.relations.add(relation);
                        }
                        //System.out.println("R on a "+rootProperties.size()+" props a ajouter...");*/
                    }
                        
                }
                //System.out.println("avant c=>"+currSeq.objects);
                for(int k=0;k < currSeq.objects.size();k++){
                    currSeq.objects.set(k, ontology.getInstance(currSeq.objects.get(k)).concept.index);
                }
                //System.out.println("apres c=>"+currSeq.objects);
                //System.out.println("avec "+totalProps+" proprietes a ajouter.");
                //System.out.println("avec en vrai "+newTotalProps+" proprietes a ajouter.");
                //System.exit(1);
                /*for(Integer c : currSeq.objects){
                    System.out.println("c=("+c+")"+ontology.getConcept(c).getName());
                }*/
                //System.out.println("c=>"+currSeq.objects);
                //for(Integer[] p : currSeq.relations){
                    //System.out.println("p=>"+Arrays.toString(p));
                    //System.out.println("p=("+p+")"+ontology.getRelation(p).getName());
                //}
                //System.out.println("p=>"+currSeq.relations);
                
                userSequences.add(currSeq);
            }
        }
        else{
            System.out.println("no local node...");
        }
        
//        System.out.println("root concepts..."+ontology.conceptsByLevel.get(0).size());
//        System.out.println("root props..."+ontology.root_relations.size());
//        System.out.println("concepts "+ontology.concepts.size);
//        System.out.println("relations "+ontology.relations.size);
//        System.out.println("triplets "+ontology.triplets.size());
//        System.out.println("instances triples:"+ontology.instance_triples.size());
    }

    public static OntoRepresentation deserialize(final String filename, boolean compression){
        final OntoRepresentation ontology = new OntoRepresentation();

        final byte LITTLEE = 1;
        final byte BIGE = 2;
        final byte endianness = 2;
        
        final int offser_header_sz = 1;
        final int offset_nbr_c = 6;
        final int offset_nbr_p = 10;
        final int offset_nbr_t = 14;
        final int offset_nbr_i = 78;
        final int offset_hash_all = 50;
        final int offset_ontoname = 82;
        
        final int offset_h_c = 54;
        final int offset_h_p = 58;
        final int offset_h_t = 62;
        final int offset_h_i = 66;
        final int offset_h_mxc = 70;
        final int offset_h_mxp = 74;
        
        final int INT_SZ = 4;
        final int BYTE_SZ = 1;
        final int FLOAT_SZ = 4;
        
        //on va lire le contenu du header sur disque
        Path path = Paths.get(filename);        
        try {
            byte[] byteFile = Files.readAllBytes(path);
            if(compression){
                final Inflater inflater = new Inflater();    
                inflater.setInput(byteFile); 
                try (final ByteArrayOutputStream out = new ByteArrayOutputStream(byteFile.length)) {
                    final byte[] buffer = new byte[1024];
                    while (!inflater.finished()) {
                        out.write(buffer, 0, inflater.inflate(buffer));        
                    }
                    byteFile =  out.toByteArray();
                } catch (final IOException | DataFormatException e) {
                    System.err.println("Decompression failed! Returning the compressed data...");
                }
            }
            
            if(endianness  == BIGE){
//                System.out.println("big endian");
            }
            else if(endianness == LITTLEE){
//                System.out.println("little endian, reversing endianness to big endian");
                //reverse_endianess(byteFile);
            }
            else{
//                System.out.println("endianness unknown...");
                System.exit(1);
            }
            
            //lire le contenu du header =>
            final int nbr_classes;// = 0;
            final int nbr_properties;// = 0;
            final int nbr_triples;// = 0;
            final int nbr_instances;// = 0;
            
            //comment placer les string???
            //chaque concept/prop a un header ? dit longueur du string uri
            int header_sz = 0;
            //on recupere la taille du header
            {
                byte[] b_header_sz = new byte[INT_SZ];
                System.arraycopy(byteFile, offser_header_sz, b_header_sz, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_header_sz); // big-endian by default
                header_sz = wrapped.getInt();
//                System.out.println("headersz="+header_sz);
            }
            //on recupere le nombre de concepts
            {
                byte[] b_nbr_c = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_nbr_c, b_nbr_c, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_nbr_c); // big-endian by default
                int nbr_c = wrapped.getInt();
//                System.out.println("nbr concept="+nbr_c);
                nbr_classes = nbr_c;
            }
            
            //on recupere le hash du fichier
            {
                byte[] b_hash_all = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_hash_all, b_hash_all, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_hash_all); // big-endian by default
                int hash_all = wrapped.getInt();
//                System.out.println("hashall="+hash_all);
            }
            
            //on recupere le nom de l'ontologie
            {
                byte[] b_ontoname = new byte[BYTE_SZ * (header_sz - offset_ontoname)];
                System.arraycopy(byteFile, offset_ontoname, b_ontoname, 0, BYTE_SZ * (header_sz - offset_ontoname));
                //ByteBuffer wrapped = ByteBuffer.wrap(b_ontoname); // big-endian by default
                char[] ontoname_arr = new char[b_ontoname.length];
                for(int i=0;i<ontoname_arr.length;i++)
                    ontoname_arr[i] = (char)b_ontoname[i];
                
                String ontoname = String.copyValueOf(ontoname_arr);
//                System.out.println("ontoname="+ontoname);
            }
            int offset_c_h = 0;
            //on recupere offset du header de concepts
            {
                byte[] b_offset_c_h = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_c, b_offset_c_h, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_offset_c_h); // big-endian by default
                offset_c_h = wrapped.getInt();
//                System.out.println("concept_header_offset="+offset_c_h);
            }
            int header_c_sz = 0;
            //
            {
                byte[] b_c_header_sz = new byte[INT_SZ];
                System.arraycopy(byteFile, offser_header_sz, b_c_header_sz, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_c_header_sz); // big-endian by default
                header_c_sz = wrapped.getInt();
//                System.out.println("c_headersz="+header_c_sz);
            }
            //on va recuperer les positions et noms des concepts...
            {
                final int base_header = header_c_sz + (4 * INT_SZ);
                for(int id_concept=1;id_concept<(nbr_classes+1);id_concept++){
                    byte[] b_concept_uri_offset = new byte[INT_SZ];
                    byte[] b_concept_uri_prev_offset = new byte[INT_SZ];
                    
                    //lecture de la longueur de l'uri
                    System.arraycopy(byteFile, base_header + ((id_concept-1) * INT_SZ), b_concept_uri_offset, 0, INT_SZ);
                    ByteBuffer wrapped = ByteBuffer.wrap(b_concept_uri_offset); // big-endian by default
                    int concept_uri_offset = wrapped.getInt();
                    
                    if(concept_uri_offset != 0){
                        //System.out.println("on essaie de lire pour "+(id_concept-1));
                        int concept_uri_prev_offset = 0;int cursor = 2;
                        while(concept_uri_prev_offset == 0 && (cursor < (id_concept+1))){
                            //lecture de la longueur de l'uri du concept precedent
                            System.arraycopy(byteFile, base_header + ((id_concept-cursor) * INT_SZ), b_concept_uri_prev_offset, 0, INT_SZ);
                            ByteBuffer wrapped_ = ByteBuffer.wrap(b_concept_uri_prev_offset); // big-endian by default
                            concept_uri_prev_offset = wrapped_.getInt();
                            cursor += 1;
                            //System.out.println("necessaire de remonter de "+(cursor-2)+" cases...");
                        }
                        if(concept_uri_prev_offset != 0 || id_concept == 1){
                            //calcul de la longueur de l'uri, permet de determiner son emplacement dans le fichier
                            final int real_size = concept_uri_offset - concept_uri_prev_offset;
                            //System.out.println("real size="+real_size+" ("+concept_uri_offset+" "+concept_uri_prev_offset+")");

                            //lecture de l'uri (string)
                            byte[] b_concept_uri = new byte[real_size];
                            System.arraycopy(byteFile, base_header + (nbr_classes * INT_SZ) + concept_uri_offset - real_size, b_concept_uri, 0, BYTE_SZ * real_size);
                            char[] concept_uri = new char[real_size];
                            for(int i=0;i<real_size;i++)
                                concept_uri[i] = (char)b_concept_uri[i];
                            //System.out.println("concept uri "+Arrays.toString(b_concept_uri));
                            String concept_uri_s = String.copyValueOf(concept_uri);
                            //System.out.println("concept id="+(id_concept-1)+" => "+concept_uri_s);
                            
                            
                            //ici on envoie le concept dans l'ontologie
                            Concept cc = ontology.createConcept(concept_uri_s, id_concept-1);
                            //System.out.println(">>>>"+cc.getName());
                            //on ajoute le nouveau concept dans l'ontologie
                            ontology.addConcept(concept_uri_s, cc);
                            
                        }
                        else{
                            System.out.println("cannot read uri....");
                            System.exit(1);
                        }
                    }
                    else{
                        System.out.println("il n'y a rien pour le concept no "+(id_concept-1));
                    }
                }
            }
            //System.exit(1);
            
            int offset_p_h = 0;
            //on recupere offset du header de properties
            {
                byte[] b_offset_p_h = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_p, b_offset_p_h, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_offset_p_h); // big-endian by default
                offset_p_h = wrapped.getInt();
//                System.out.println("properties_header_offset="+offset_p_h);
            }
            //on recupere le nombre de concepts
            {
                byte[] b_nbr_p = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_nbr_p, b_nbr_p, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_nbr_p); // big-endian by default
                int nbr_p = wrapped.getInt();
//                System.out.println("nbr props="+nbr_p);
                nbr_properties = nbr_p;
            }
            
            int header_p_sz = 0;
            //
            {
                byte[] b_p_header_sz = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_p, b_p_header_sz, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_p_header_sz); // big-endian by default
                header_p_sz = wrapped.getInt();
//                System.out.println("p_headersz="+header_c_sz);
            }
            
            //on va recuperer les positions et noms des concepts...
            {
                final int base_header = offset_p_h + (4 * INT_SZ);
                for(int id_property=1;id_property<(nbr_properties+1);id_property++){
                    byte[] b_prop_uri_offset = new byte[INT_SZ];
                    byte[] b_prop_uri_prev_offset = new byte[INT_SZ];
                    
                    //lecture de la longueur de l'uri
                    System.arraycopy(byteFile, base_header + ((id_property-1) * INT_SZ), b_prop_uri_offset, 0, INT_SZ);
                    ByteBuffer wrapped = ByteBuffer.wrap(b_prop_uri_offset); // big-endian by default
                    int prop_uri_offset = wrapped.getInt();
                    
                    if(prop_uri_offset != 0){
                        //System.out.println("on essaie de lire pour "+(id_property-1));
                        int prop_uri_prev_offset = 0;int cursor = 2;
                        while(prop_uri_prev_offset == 0 && (cursor < (id_property+1))){
                            //lecture de la longueur de l'uri du concept precedent
                            System.arraycopy(byteFile, base_header + ((id_property-cursor) * INT_SZ), b_prop_uri_prev_offset, 0, INT_SZ);
                            ByteBuffer wrapped_ = ByteBuffer.wrap(b_prop_uri_prev_offset); // big-endian by default
                            prop_uri_prev_offset = wrapped_.getInt();
                            cursor += 1;
                            //System.out.println("necessaire de remonter de "+(cursor-2)+" cases...");
                        }
                        if(prop_uri_prev_offset != 0 || id_property == 1){
                            //calcul de la longueur de l'uri, permet de determiner son emplacement dans le fichier
                            final int real_size = prop_uri_offset - prop_uri_prev_offset;
                            //System.out.println("real size="+real_size+" ("+prop_uri_offset+" "+prop_uri_prev_offset+")");

                            //lecture de l'uri (string)
                            byte[] b_prop_uri = new byte[real_size];
                            System.arraycopy(byteFile, base_header + (nbr_properties * INT_SZ) + prop_uri_offset - real_size, b_prop_uri, 0, BYTE_SZ * real_size);
                            char[] prop_uri = new char[real_size];
                            for(int i=0;i<real_size;i++)
                                prop_uri[i] = (char)b_prop_uri[i];
                            //System.out.println("prop uri "+Arrays.toString(b_prop_uri));
                            String prop_uri_s = String.copyValueOf(prop_uri);
                            //System.out.println("prop id="+(id_property-1)+" => "+prop_uri_s);
                            
                            
                            //ici on envoie le concept dans l'ontologie
                            Relation pp = ontology.createRelation(prop_uri_s, (id_property-1));
                            //System.out.println(">>>>"+cc.getName());
                            //on ajoute le nouveau concept dans l'ontologie
                            ontology.addRelation(prop_uri_s, pp);                            
                        }
                        else{
                            System.out.println("cannot read uri....");
                            System.exit(1);
                        }
                    }
                    else{
                        System.out.println("il n'y a rien pour la prop no "+(id_property-1));
                    }
                }
            }
            
            
            //on recupere le nombre d'instances
            {
                byte[] b_nbr_i = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_nbr_i, b_nbr_i, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_nbr_i); // big-endian by default
                int nbr_i = wrapped.getInt();
//                System.out.println("nbr instances="+nbr_i);
                nbr_instances = nbr_i;
            }
            
            
            int offset_i_h = 0;
            //on recupere offset du header d'instances
            {
                byte[] b_offset_i_h = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_i, b_offset_i_h, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_offset_i_h); // big-endian by default
                offset_i_h = wrapped.getInt();
//                System.out.println("instances_header_offset="+offset_i_h);
            }
            int header_i_sz = 0;
            {
                byte[] b_i_header_sz = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_i_h, b_i_header_sz, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_i_header_sz); // big-endian by default
                header_i_sz = wrapped.getInt();
//                System.out.println("i_headersz="+header_i_sz);
            }
            
            {
                final int header_inst_types = offset_i_h + (2*INT_SZ);
                final int debut_h_inst;
                {
                    byte[] b_instance_types_offset = new byte[INT_SZ];
                    System.arraycopy(byteFile, header_inst_types, b_instance_types_offset, 0, INT_SZ);
                    ByteBuffer wrapped = ByteBuffer.wrap(b_instance_types_offset); // big-endian by default
                    debut_h_inst = wrapped.getInt();
//                    System.out.println("le debut du header des types d'instances est a la pos "+debut_h_inst);
                }
                if(debut_h_inst!=0){
                    //parcourir le header en meme temps que les donnees
                    for(int id_instance=0;id_instance<nbr_instances;id_instance++){
                        byte[] b_instance_types_offset = new byte[INT_SZ];
                        System.arraycopy(byteFile, debut_h_inst + (id_instance * INT_SZ), b_instance_types_offset, 0, INT_SZ);
                        ByteBuffer wrapped = ByteBuffer.wrap(b_instance_types_offset); // big-endian by default
                        int instance_type_offset = wrapped.getInt();
                        //System.out.println("instance no "+id_instance+" start at "+instance_type_offset);
                        
                        byte[] b_instance_types_length = new byte[INT_SZ];
                        System.arraycopy(byteFile, instance_type_offset, b_instance_types_length, 0, INT_SZ);
                        ByteBuffer _wrapped = ByteBuffer.wrap(b_instance_types_length); // big-endian by default
                        int instance_type_l = _wrapped.getInt();
                        //System.out.println("and has "+instance_type_l+" types");
                        
                        Instance addInstance = ontology.createInstance(id_instance);
                        
                        for(int i=0;i < instance_type_l;i++){
                            byte[] b_instance_types_value = new byte[INT_SZ];
                            System.arraycopy(byteFile, instance_type_offset + ((i+1)*INT_SZ), b_instance_types_value, 0, INT_SZ);
                            ByteBuffer _wrapped_ = ByteBuffer.wrap(b_instance_types_value); // big-endian by default
                            //System.out.println(">>"+Arrays.toString(b_instance_types_value));
                            int instance_type_value = _wrapped_.getInt();
                            //System.out.println("type="+instance_type_value);
                            ontology.addInstance(addInstance, ontology.getConcept(instance_type_value));
                        }
                    }
                }else{
                    System.out.println("cannot read instance types...");
                }
            }
            //recuperation des instances
            //les instances ne sont pas utilisees telles quelles dans le programme
            //uniquement a des fins de traduction
            {
                //header_i_sz = (4*INT_SZ);//simulation...
                final int base_header = offset_i_h + header_i_sz;
                for(int id_instance=1;id_instance<(nbr_instances+1);id_instance++){
                    byte[] b_instance_uri_offset = new byte[INT_SZ];
                    byte[] b_instance_uri_prev_offset = new byte[INT_SZ];
                    
                    //lecture de la longueur de l'uri
                    System.arraycopy(byteFile, base_header + ((id_instance-1) * INT_SZ), b_instance_uri_offset, 0, INT_SZ);
                    ByteBuffer wrapped = ByteBuffer.wrap(b_instance_uri_offset); // big-endian by default
                    int instance_uri_offset = wrapped.getInt();
                    
                    //lecture de la longueur de l'uri de l'instance precedente
                    System.arraycopy(byteFile, base_header + ((id_instance-2) * INT_SZ), b_instance_uri_prev_offset, 0, INT_SZ);
                    ByteBuffer wrapped_ = ByteBuffer.wrap(b_instance_uri_prev_offset); // big-endian by default
                    int instance_uri_prev_offset = wrapped_.getInt();
                    
                    //calcul de la longueur de l'uri, permet de determiner son emplacement dans le fichier
                    final int real_size = instance_uri_offset - instance_uri_prev_offset;
                    //System.out.println("real size="+real_size+" ("+instance_uri_offset+" "+instance_uri_prev_offset+")");
                    
                    //lecture de l'uri (string)
                    byte[] b_instance_uri = new byte[real_size];
                    System.arraycopy(byteFile, base_header + (nbr_instances * INT_SZ) + instance_uri_offset - real_size, b_instance_uri, 0, BYTE_SZ * real_size);
                    char[] instance_uri = new char[real_size];
                    for(int i=0;i<real_size;i++)
                        instance_uri[i] = (char)b_instance_uri[i];

                    String instance_uri_s = String.copyValueOf(instance_uri);
                    //System.out.println("instance id="+(id_instance-1)+" => "+instance_uri_s+".");
                    
                    
                    if(ontology.index_instances_by_name.get(instance_uri_s, ontology.index_instances_by_name.root) != null){
                        System.out.println("la valeur existe deja !!!!!");
                        System.exit(1);
                    }
                    
                    final Instance instance = ontology.getInstance(id_instance-1);
                    final RadixTree.RadixNode p_name = ontology.index_instances_by_name.addString(instance_uri_s, instance);
                    instance.p_name = p_name;
                    //System.out.println(""+instance.getName());
                    if(p_name.getKey().length()!=instance_uri_s.length()){
                        System.out.println("not same string (key)....");
                        System.out.println(""+instance_uri_s+"");
                        System.out.println(""+p_name.getKey());
                        System.exit(1);
                    }
                    if(instance.getName().length()!=instance_uri_s.length()){
                        System.out.println("not same string....");
                        System.exit(1);
                    }
                    
                    if(ontology.index_instances_by_name.get(instance_uri_s, ontology.index_instances_by_name.root) == null){
                        System.out.println("could not load "+instance_uri_s);
                        System.exit(1);
                    }
                    
                    /*if(instance_uri_s.contains("#DNA") && instance_uri_s.endsWith("DNA")){
                        System.out.println("we have dna ..."+instance_uri_s);
                        System.out.println(""+instance.getName());
                        System.exit(1);
                    }*/
                    
                }
            }
            
            
            
            int offset_t_h = 0;
            //on recupere offset du header de triplets
            {
                byte[] b_offset_t_h = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_t, b_offset_t_h, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_offset_t_h); // big-endian by default
                offset_t_h = wrapped.getInt();
//                System.out.println("triples_header_offset="+offset_t_h);
            }
            
            //on recupere le nombre de concepts
            {
                byte[] b_nbr_t = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_nbr_t, b_nbr_t, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_nbr_t); // big-endian by default
                int nbr_t = wrapped.getInt();
//                System.out/.println("nbr triples="+nbr_t);
                nbr_triples = nbr_t;
            }
            
            //on va recuperer les triplets
            {
                final int cost_of_triple = (INT_SZ * 3)+1;                
                final int base_header = offset_t_h + (4 * INT_SZ);
                for(int id_triplet=0;id_triplet<nbr_triples;id_triplet++){
                    byte infos = 0;
                    int sujet = 0;int pred = 0; int obj = 0;
                    {
                        byte[] b_triple_infos = new byte[BYTE_SZ];
                        System.arraycopy(byteFile, base_header + (id_triplet * cost_of_triple), b_triple_infos, 0, BYTE_SZ);
                        ByteBuffer wrapped = ByteBuffer.wrap(b_triple_infos); // big-endian by default
                        infos = wrapped.get();
                    }
                    if(infos == 0){
                        {
                            byte[] b_triple_suj = new byte[INT_SZ];
                            System.arraycopy(byteFile, base_header + (id_triplet * cost_of_triple) + BYTE_SZ, b_triple_suj, 0, INT_SZ);
                            ByteBuffer wrapped = ByteBuffer.wrap(b_triple_suj); // big-endian by default
                            sujet = wrapped.getInt();
                        }
                        {
                            byte[] b_triple_pred = new byte[INT_SZ];
                            System.arraycopy(byteFile, base_header + (id_triplet * cost_of_triple) + BYTE_SZ + INT_SZ, b_triple_pred, 0, INT_SZ);
                            ByteBuffer wrapped = ByteBuffer.wrap(b_triple_pred); // big-endian by default
                            pred = wrapped.getInt();
                        }
                        {
                            byte[] b_triple_obj = new byte[INT_SZ];
                            System.arraycopy(byteFile, base_header + (id_triplet * cost_of_triple) + BYTE_SZ + (2*INT_SZ), b_triple_obj, 0, INT_SZ);
                            ByteBuffer wrapped = ByteBuffer.wrap(b_triple_obj); // big-endian by default
                            obj = wrapped.getInt();
                        }
//                        System.out.println("t="+sujet+", "+pred+", "+obj+", "+infos);

                        final Relation p = ontology.getRelation(pred);
                        final Concept s = ontology.getConcept(sujet);
                        final Concept o = ontology.getConcept(obj);
//                        System.out.println("which is "+s.getName()+", "+p.getName()+", "+o.getName());
                        ontology.createTriplet(p, s, o);
                    }
                    else{
                        //ici on gere les triplets d'instances
                        
                        //comment gerer ca ????
                        //utile uniquement au debut pour construire les usersequence en fait, 
                        
                        //donc on a besoin uniquement d'une structuer qui gere les root et non root properties des instances
                        //comme les concepts...
                        
                        //
                        {
                            byte[] b_triple_suj = new byte[INT_SZ];
                            System.arraycopy(byteFile, base_header + (id_triplet * cost_of_triple) + BYTE_SZ, b_triple_suj, 0, INT_SZ);
                            ByteBuffer wrapped = ByteBuffer.wrap(b_triple_suj); // big-endian by default
                            sujet = wrapped.getInt();
                        }
                        {
                            byte[] b_triple_pred = new byte[INT_SZ];
                            System.arraycopy(byteFile, base_header + (id_triplet * cost_of_triple) + BYTE_SZ + INT_SZ, b_triple_pred, 0, INT_SZ);
                            ByteBuffer wrapped = ByteBuffer.wrap(b_triple_pred); // big-endian by default
                            pred = wrapped.getInt();
                        }
                        {
                            byte[] b_triple_obj = new byte[INT_SZ];
                            System.arraycopy(byteFile, base_header + (id_triplet * cost_of_triple) + BYTE_SZ + (2*INT_SZ), b_triple_obj, 0, INT_SZ);
                            ByteBuffer wrapped = ByteBuffer.wrap(b_triple_obj); // big-endian by default
                            obj = wrapped.getInt();
                        }
                        //System.out.println("inst t="+sujet+", "+pred+", "+obj+", "+infos);
                        Instance suji = ontology.getInstance(sujet);
                        Instance obji = ontology.getInstance(obj);
                        Relation rel = ontology.getRelation(pred);
                        //System.out.println("which is "+suji.getName()+", "+rel.getName()+", "+obji.getName());
                        ontology.createTripletInst(rel, suji, obji);
                    }
                }
                //System.exit(1);
            }
            
            
            
            
            int offset_mxc_h = 0;
            //on recupere offset du header d'instances
            {
                byte[] b_offset_mxc_h = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_mxc, b_offset_mxc_h, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_offset_mxc_h); // big-endian by default
                offset_mxc_h = wrapped.getInt();
//                System.out.println("mxc_header_offset="+offset_mxc_h);
            }
            //recuperation de la matrice de descendance
            {
                final int debut_mtx = offset_mxc_h;
                int mtx_h = 0;
                byte[] b_h_mtx_sz = new byte[INT_SZ];
                System.arraycopy(byteFile, debut_mtx, b_h_mtx_sz, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_h_mtx_sz); // big-endian by default
                mtx_h = wrapped.getInt();
//                System.out.println("header m size"+mtx_h);
                //on a max, ca veut dire qu'on lire header correctement
                
                int[] is_nonroot_c = new int[nbr_classes];

                //int debut_arr = 0;
                for(int i=0;i<(mtx_h+1);i++){
                    int debut_arr = 0;
                    {
                        byte[] b_offset_mtx = new byte[INT_SZ];
                        System.arraycopy(byteFile, debut_mtx+(INT_SZ*(1+i)), b_offset_mtx, 0, INT_SZ);
                        ByteBuffer _wrapped = ByteBuffer.wrap(b_offset_mtx); // big-endian by default
                        debut_arr = _wrapped.getInt();
                        //System.out.println("offset pour "+i+" est "+debut_arr);
                        
                    }
                    //si l'item est present (ca devrait etre la cas normalement)
                    if(debut_arr!=0){
                        int fin_arr = 0;
                        //alors on recherche la longueur du tableau
                        {
                            byte[] b_arr_sz = new byte[INT_SZ];
                            System.arraycopy(byteFile, debut_arr, b_arr_sz, 0, INT_SZ);
                            ByteBuffer _wrapped = ByteBuffer.wrap(b_arr_sz); // big-endian by default
                            fin_arr = _wrapped.getInt();
                            //System.out.println("("+debut_arr+")pendant "+fin_arr);
                            fin_arr *= INT_SZ;//parce que c'est des int et pas des byte
                            fin_arr += 1 + debut_arr;
                        }
                        debut_arr += INT_SZ;//on va au premier
                        //et on lit toutes les cases...
                        int last_link = mtx_h + 1;
                        while(debut_arr < fin_arr){
                            byte[] b_arr_val = new byte[INT_SZ];
                            System.arraycopy(byteFile, debut_arr, b_arr_val, 0, INT_SZ);
                            ByteBuffer _wrapped = ByteBuffer.wrap(b_arr_val); // big-endian by default
                            int parent = _wrapped.getInt();
                            //System.out.println(i+" is son of "+parent);
                            
                            if(!ontology.ancestor_matrix_c.matrix.containsKey((char)i)){
                                ontology.ancestor_matrix_c.matrix.put((char)i, new HashSet<>());
                            }
                            HashSet<Character> ancetres = ontology.ancestor_matrix_c.matrix.get((char)i);
                            ancetres.add((char)parent);
                            
                            
                            
                            debut_arr += INT_SZ;
                            last_link = parent;
                        }
                        if(last_link != (mtx_h + 1)){
                            //System.out.println("real link is between "+i+" and "+last_link);
                            ontology.extendConcept(ontology.getConcept(i), ontology.getConcept(last_link), null);
                            if(is_nonroot_c[i] == 0) {
                                is_nonroot_c[i] = is_nonroot_c[last_link] + 1;
                                //System.out.println("-----------------------");
                                //System.out.println(">"+is_nonroot_c[i] + " "+ ontology.getConcept(i).getName());
                                //System.out.println(">"+(is_nonroot_c[last_link]+1) + " "+ ontology.getConcept(last_link).getName());
                            }
                        }
                        
                    }
                }
                
                //seuls les conceps qui n'ont jamais ete fils sont root
                for(int i=0;i < is_nonroot_c.length; i++){
                    //if(is_nonroot_c[i] == 0){
                        final Concept concept = ontology.getConcept(i);
                        //ontology.root_concepts.add(concept);
                        ontology.addConceptByLevel(concept, is_nonroot_c[i]);
//                        System.out.println(is_nonroot_c[i]+" c"+concept.index+" , "+concept.getName());
                    //}
                }
                //System.exit(1);
            }
            
            
            int offset_mxp_h = 0;
            //on recupere offset du header d'instances
            {
                byte[] b_offset_mxp_h = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_mxp, b_offset_mxp_h, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_offset_mxp_h); // big-endian by default
                offset_mxp_h = wrapped.getInt();
//                System.out.println("mxp_header_offset="+offset_mxp_h);
            }
            if(offset_mxp_h !=0){
                //recuperation de la matrice de descendance
                {
                    final int debut_mtx = offset_mxp_h;
                    int mtx_h = 0;
                    byte[] b_h_mtx_sz = new byte[INT_SZ];
                    System.arraycopy(byteFile, debut_mtx, b_h_mtx_sz, 0, INT_SZ);
                    ByteBuffer wrapped = ByteBuffer.wrap(b_h_mtx_sz); // big-endian by default
                    mtx_h = wrapped.getInt();
//                    System.out.println("header m size"+mtx_h);
                    //on a max, ca veut dire qu'on lire header correctement
                    boolean[] is_nonroot_p = new boolean[nbr_properties];

                    //int debut_arr = 0;
                    for(int i=0;i<(mtx_h+1);i++){
                        int debut_arr = 0;
                        {
                            byte[] b_offset_mtx = new byte[INT_SZ];
                            System.arraycopy(byteFile, debut_mtx+(INT_SZ*(1+i)), b_offset_mtx, 0, INT_SZ);
                            ByteBuffer _wrapped = ByteBuffer.wrap(b_offset_mtx); // big-endian by default
                            debut_arr = _wrapped.getInt();
                            //System.out.println("offset pour "+i+" est "+debut_arr);

                        }
                        //si l'item est present (ca devrait etre la cas normalement)
                        if(debut_arr!=0){
                            int fin_arr = 0;
                            //alors on recherche la longueur du tableau
                            {
                                byte[] b_arr_sz = new byte[INT_SZ];
                                System.arraycopy(byteFile, debut_arr, b_arr_sz, 0, INT_SZ);
                                ByteBuffer _wrapped = ByteBuffer.wrap(b_arr_sz); // big-endian by default
                                fin_arr = _wrapped.getInt();
                                //System.out.println("("+debut_arr+")pendant "+fin_arr);
                                fin_arr *= INT_SZ;//parce que c'est des int et pas des byte
                                fin_arr += 1 + debut_arr;
                            }
                            debut_arr += INT_SZ;//on va au premier
                            //et on lit toutes les cases...
                            int last_link = (mtx_h + 1);
                            while(debut_arr < fin_arr){
                                byte[] b_arr_val = new byte[INT_SZ];
                                System.arraycopy(byteFile, debut_arr, b_arr_val, 0, INT_SZ);
                                ByteBuffer _wrapped = ByteBuffer.wrap(b_arr_val); // big-endian by default
                                int parent = _wrapped.getInt();
                                //System.out.println(i+" is son of "+parent);
                                
                                if(!ontology.ancestor_matrix_r.matrix.containsKey((char)i)){
                                    ontology.ancestor_matrix_r.matrix.put((char)i, new HashSet<Character>());
                                }
                                HashSet<Character> get = ontology.ancestor_matrix_r.matrix.get((char)i);
                                get.add((char)parent);
                                
                                //if(!is_nonroot_p[son]) is_nonroot_p[son] = true;
                                
                                debut_arr += INT_SZ;
                                last_link = parent;
                            }
                            if(last_link != (mtx_h + 1)){
                                //System.out.println("real link is between "+i+" and "+last_link);
                                //ontology.extendConcept(ontology.getConcept(i), ontology.getConcept(last_link), r);
                                ontology.addPropertyChild(ontology.getRelation(i), ontology.getRelation(last_link));
                                if(!is_nonroot_p[i]) is_nonroot_p[i] = true;
                            }
                        }
                        /*else{
                            System.out.println("on peut pas lire pour "+i);
                        }*/
                    }
                    //seuls les conceps qui n'ont jamais ete fils sont root
                    for(int i=0;i < is_nonroot_p.length; i++){
                        if(!is_nonroot_p[i]){
                            final Relation relation = ontology.getRelation(i);
                            ontology.root_relations.add(relation);
//                            System.out.println("root p"+relation.index+" , "+relation.getName());
                        }
                    }
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(bowlLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ontology;
    }
    
    /**
     * 
     * @param ontology 
     */
    public static void doSomeChecks(OntoRepresentation ontology){
        //verification des concepts
        {
            final LinkedList<Concept> llll = ontology.getAllConcepts();
            if(llll != null && llll.curr() != null){
                llll.reset();
                do{
                    Concept rrr = llll.curr().value;
//                    System.out.println("Next:"+rrr.index);
                    //System.err.println(""+Arrays.toString(rrr.char_index));
//                    System.out.println("cname=>"+rrr.getName());
                }
                while(llll.hasNext());
            }
        }
        //verification des props
        {
            final LinkedList<Relation> llll = ontology.getAllProperties();
            if(llll != null && llll.curr() != null){
                llll.reset();
                do{
                    Relation rrr = llll.curr().value;
//                    System.out.println("Next prop:"+rrr.index);
                    //System.err.println(""+Arrays.toString(rrr.char_index));
//                    System.out.println("pname=>"+rrr.getName());
                }
                while(llll.hasNext());
            }
        }
        
        //verification des triplets
        {
            
            ArrayList<Triplet> nonRootProperties = ontology.getNonRootProperties(ontology.getConcept(33), ontology.getConcept(127));
//            System.out.println("on a "+nonRootProperties.size()+" non root props");
            for(Triplet rrr : nonRootProperties){
//                System.out.println("rrr=>"+rrr.toString());
            }
        
        }
        
        //verification des proprietes entre deux concepts
        //verification des specialisations de concept
        
        //verifications de specialisations proprietes
        {
            ArrayList<Relation> propertyChildren = ontology.getPropertyChildren(30);//23 ou 30
            for(Relation r : propertyChildren){
//                System.out.println("r="+r.index);
            }
        }
        //verification de la matrice de hierarchie concepts
        {
//            System.out.println("c=>"+ontology.ancestor_matrix_c.matrix.toString());
        
        }
        
        //verification de la matrice de hierarchie props
        {
//            System.out.println("p=>"+ontology.ancestor_matrix_r.matrix.toString());
        }
        
        //verification des instances
        {
            for(Instance inst : ontology.instances){
//                System.out.println(""+inst.index+" and name <"+inst.getName()+">");
//                System.out.println("and type is {"+inst.getType().index+"}");
            }
        }
    }
}
