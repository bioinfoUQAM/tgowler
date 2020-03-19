/**
 *
 * @author Enridestroy
 */

import cleanrepresentation.Mapping;
import ontologyrep2.*;
import ontologyrep2.LinkedList;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.ValueFactoryImpl;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Converter2Bowl {
    static int DEFAULT_FILESIZE = 5000000;
    static int DEFAULT_ARR_SZ = 500000;

    public static void set_resource_to_iri_set(final Set<Resource> _input, final Set<IRI> _output){
        for(Resource r : _input){
            IRI actualIRI = null;
            try{
                actualIRI = (IRI) r;
            }
            catch(Exception e){
                //cannot convert...
            }
            if(null != actualIRI){
                _output.add(actualIRI);
            }
        }
    }

    public static void serialize(final String outputFile, final OntoRepresentation ontology, Model jenaWrapper, Mapping mapping, boolean compression){
        final byte[] header = new byte[50];
        final byte LITTLEE = 1;
        final byte BIGE = 2;

        final int INT_SZ = 4;
        final int BYTE_SZ = 1;
        final int FLOAT_SZ = 4;

        HashMap<Integer, String> map_concepts = new HashMap<>();

        int max_id_c = 0;
        final LinkedList<Concept> llll = ontology.getAllConcepts();
        if(llll != null && llll.curr() != null){
            llll.reset();
            do{
                Concept rrr = llll.curr().value;
                System.out.println("Next:"+rrr.index);
                //Concept classById = mapping.classesByIDs.get(rrr.index);
                final int id = rrr.index;//mapping.idsByClasses.get(rrr);
                map_concepts.put(/*rrr.index*/id, /*classById.getName()*/rrr.getName());
                if(max_id_c < id/*rrr.index*/) max_id_c = id;//rrr.index;
            }
            while(llll.hasNext());
        }
        max_id_c += 1;
        String[] concepts = new String[max_id_c];
        System.out.println("max ="+max_id_c);
        System.out.println("s="+map_concepts.size());
        for(Entry<Integer, String> entry : map_concepts.entrySet()){
            concepts[entry.getKey()] = entry.getValue();
        }

        System.out.println("c="+Arrays.toString(concepts));

        HashMap<Integer, String> map_props = new HashMap<>();
        HashMap<String, Integer> inv_map_props = new HashMap<>();
        int max_id_p = 0;
        final LinkedList<Relation> pppp = ontology.getAllProperties();
        if(pppp != null && pppp.curr() != null){
            pppp.reset();
            do{
                Relation rrr = pppp.curr().value;
                //System.out.println("Next:"+rrr.index);
                final int id = rrr.index;//mapping.idsByProperties.get(rrr);
                //Relation propById = mapping.propertiesByIDs.get(rrr.index);
                System.out.println(">>"+rrr.getName());
                map_props.put(id, rrr.getName());
                //map_props.put(rrr.index, propById.getName());
                inv_map_props.put(rrr.getName(), id);
                //inv_map_props.put(propById.getName(), rrr.index);
                if(max_id_p < id/*rrr.index*/) max_id_p = id;//rrr.index;
            }
            while(pppp.hasNext());
        }

        max_id_p += 1;
        String[] proprietes = new String[max_id_p];
        System.out.println("max ="+max_id_p);
        System.out.println("s="+map_props.size());
        for(Entry<Integer, String> entry : map_props.entrySet()){
            proprietes[entry.getKey()] = entry.getValue();
        }
        System.out.println("p="+Arrays.toString(proprietes));

        //on recupere toutes les instances et leur classe
        final Set<IRI> subjects_iri = new HashSet<>();
        final Set<IRI> classes_iri = new HashSet<>();
        set_resource_to_iri_set(jenaWrapper.filter(null, RDF.TYPE, null).subjects(), subjects_iri);
        set_resource_to_iri_set(jenaWrapper.filter(null, RDF.TYPE, OWL.CLASS).subjects(), classes_iri);
        set_resource_to_iri_set(jenaWrapper.filter(null, RDF.TYPE, RDFS.CLASS).subjects(), classes_iri);
        //
        set_resource_to_iri_set(jenaWrapper.filter(null, RDF.TYPE, OWL.DATATYPEPROPERTY).subjects(), classes_iri);
        set_resource_to_iri_set(jenaWrapper.filter(null, RDF.TYPE, OWL.OBJECTPROPERTY).subjects(), classes_iri);
        set_resource_to_iri_set(jenaWrapper.filter(null, RDF.TYPE, RDF.PROPERTY).subjects(), classes_iri);
        //
        set_resource_to_iri_set(jenaWrapper.filter(null, RDF.TYPE, OWL.ONTOLOGY).subjects(), classes_iri);
        subjects_iri.removeAll(classes_iri);

        final IRI[] instances = subjects_iri.toArray(new IRI[0]);

        for(IRI inst : instances){
            System.out.println(inst.stringValue()+" : " + jenaWrapper.filter(inst, RDF.TYPE, null).objects().toString());
        }

        //Set<Individual> allIndividuals = jenaWrapper.getAllIndividuals();
        //Individual[] instances = allIndividuals.toArray(new Individual[allIndividuals.size()]);

        HashMap<String, Integer> insturi_to_id = new HashMap<>();

        //ceci va etre a revoir...
        for(int i=0;i < instances.length;i++){
            IRI inst = (IRI)instances[i];
            System.out.println(i + " is "+inst.stringValue());
            insturi_to_id.put(inst.stringValue(), i);
            //OntClass concept = jenaWrapper.getOntClassByIndividualURI( inst.getURI() );
            //System.out.println("and has class "+concept.getURI());
        }


        int[][] triplets = new int[ontology.triplets.size()][];
        for(int i=0;i < triplets.length;i++){
            int[] triple = new int[5];
            Triplet get = ontology.triplets.get(i);
            triple[0] = 0;//type sujet
            triple[1] = get.domaine.index;//sujet id
            triple[2] = get.relation.index;//prop id
            triple[3] = 0;//type obj
            triple[4] = get.codomaine.index;//obj id
            //les types sont tjrs 0 car on utilise que les proprietes entre les classes !!!
            triplets[i] = triple;
        }
        System.out.println("on a "+triplets.length+" triplets !!!");


        int totalInstLinks = 0;
        HashMap<Pair<IRI, IRI>, Set<IRI>> propertiesByIndividuals = new HashMap<>();
        for(int i=0;i < instances.length;i++) {
            IRI inst = instances[i];
            if(inst == null) continue;
            for(int j=0;j < instances.length;j++) {
                IRI inst2 = instances[j];
                if(inst2 == null) continue;
                final Set<IRI> linksBetweenInstances = new HashSet<>();
                linksBetweenInstances.addAll(jenaWrapper.filter(inst, null, inst2).predicates());
                linksBetweenInstances.addAll(jenaWrapper.filter(inst2, null, inst).predicates());
                propertiesByIndividuals.put(new Pair<>(inst, inst2), linksBetweenInstances);
                if(linksBetweenInstances.size() != 0){
                    System.out.println("");
                }
                totalInstLinks += linksBetweenInstances.size();
            }
        }

        //System.out.println("on avait oublie "+totalInstLinks+" tripelts...");
        final int[][] instance_triples = new int[totalInstLinks][];
        int curr = 0;
        for(final Map.Entry<Pair<IRI, IRI>, Set<IRI>> entry : propertiesByIndividuals.entrySet()){
            Set<IRI> predicates = entry.getValue();
            Pair<IRI, IRI> link = entry.getKey();
            for(final IRI o : predicates){
                System.out.println("un autre lien : "+link.getFirst().stringValue()+ " -> "+link.getSecond().stringValue() + " w/ "+o.stringValue());
                //jenaWrapper.getIndividualByURI("")
                Integer sujetID = insturi_to_id.get(link.getFirst().stringValue());
                if(sujetID == null){
                    System.out.println("unknown inst " + link.getFirst().stringValue());
                    System.exit(1);
                }
                Integer objetID = insturi_to_id.get(link.getSecond().stringValue());
                if(objetID == null){
                    System.out.println("unknown inst" + link.getSecond().stringValue());
                    System.exit(1);
                }
                int[] triple = new int[5];
                Integer relID = inv_map_props.get(o.stringValue());
                if(relID == null){
                    System.out.println("unknown rel" + o.stringValue());
                    System.exit(1);
                }
                triple[0] = 1;//type sujet
                triple[1] = sujetID;//sujet id
                triple[2] = relID;//prop id
                triple[3] = 1;//type obj
                triple[4] = objetID;//obj id
                instance_triples[curr] = triple;
                curr+=1;
            }
        }

        byte[] aBytes = new byte[DEFAULT_FILESIZE];//header + concepts*int + (concepts_names) + props*int + (propsnames) + (triples * 3 * int)

        int rightmost_byte;
        final int base_header_size = (INT_SZ * 16) + (BYTE_SZ * 2) + (FLOAT_SZ * 4);
        String ontologyname = "someontology.owl";//This should be updated
        char[] ontologyname_to_char_arr = ontologyname.toCharArray();
        final int header_size = base_header_size + ontologyname_to_char_arr.length;
        System.out.println("hz="+header_size+ " "+base_header_size);

        {
            int[] int_values = new int[DEFAULT_ARR_SZ];
            int[] int_values_positions = new int[DEFAULT_ARR_SZ];

            float[] float_values = new float[DEFAULT_ARR_SZ];
            int[] float_values_positions = new int[DEFAULT_ARR_SZ];

            byte[] byte_values = new byte[DEFAULT_ARR_SZ];
            int[] byte_values_positions = new int[DEFAULT_ARR_SZ];

            int_values[0] = header_size;
            int_values[1] = concepts.length;//ontology.getAllConcepts().size;//on vera si c bon..
            int_values[2] = proprietes.length;
            int_values[3] = ontology.triplets.size() + totalInstLinks;//nbr triplets
            int_values[4] = -502151321;
            int_values[5] = header_size;//header concepts
            int_values[6] = 500;//header props
            int_values[7] = 520;//header triples
            int_values[8] = 550;//header instances
            int_values[9] = 000;//header matrice ancetre
            int_values[10] = 1;//header matrice ancetre prop
            int_values[11] = instances.length;//nombre d'instances
            //quid des axiomes
            //int_values[9] = 200;//

            int_values_positions[0] = 1;
            int_values_positions[1] = 6;
            int_values_positions[2] = 10;
            int_values_positions[3] = 14;
            int_values_positions[4] = 50;//hash
            int_values_positions[5] = 54;//c
            int_values_positions[6] = 58;//p
            int_values_positions[7] = 62;//t
            int_values_positions[8] = 66;//instances
            int_values_positions[9] = 70;//matrice desc
            int_values_positions[10] = 74;
            int_values_positions[11] = 78;//nbr instances
            final int nbr_int_values = 12;

            for(int i=0;i < nbr_int_values;i++){
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(int_values[i]).array();
                System.arraycopy(bytes, 0, aBytes, int_values_positions[i], INT_SZ);
            }


            float_values[0] = Integer.MAX_VALUE / 5;
            float_values[1] = Integer.MAX_VALUE / 5;
            float_values_positions[0] = 18;
            float_values_positions[1] = 22;
            final int nbr_float_values = 2;

            for(int i=0;i < nbr_float_values;i++){
                final byte[] bytes = ByteBuffer.allocate(FLOAT_SZ).putFloat(float_values[i]).array();
                System.arraycopy(bytes, 0, aBytes, float_values_positions[i], FLOAT_SZ);
            }


            byte_values[0] = BIGE;//endianess
            byte_values[1] = 9;//version
            byte_values_positions[0] = 0;
            byte_values_positions[1] = 5;
            final int nbr_byte_values = 1;

            for(int i=0;i < nbr_byte_values;i++){
                //final byte[] bytes = ByteBuffer.allocate(BYTE_SZ).putBye(byte_values[i]).array();
                aBytes[byte_values_positions[i]] = byte_values[i];

                //System.arraycopy(bytes, 0, aBytes, byte_values_positions[i], BYTE_SZ);
            }


            final int lll=ontologyname_to_char_arr.length;
            byte[][] char_arr_values = new byte[lll][];
            int[] char_arr_values_positions = new int[1];

            char_arr_values[0] = new byte[lll];
            for(int c=0;c < lll; c++){
                char_arr_values[0][c] = (byte)ontologyname_to_char_arr[c];
            }
            char_arr_values_positions[0] = 82;
            final int nbr_char_arr_values = 1;
            for(int i=0;i < nbr_char_arr_values;i++){
                System.arraycopy(char_arr_values[i], 0, aBytes, char_arr_values_positions[i], lll);
                System.out.println("l="+lll);
            }
            //System.out.println("byte="+Arrays.toString(aBytes));
        }
        /**
         * header des concepts
         * 0 => longueur
         * 1 => hash du contenu des concepts
         * 2 =>
         */
        final int concept_part_end;
        {
            int[] int_values = new int[DEFAULT_ARR_SZ];
            int[] int_values_positions = new int[DEFAULT_ARR_SZ];

            //float[] float_values = new float[50];
            //int[] float_values_positions = new int[50];

            //byte[] byte_values = new byte[50];
            //int[] byte_values_positions = new int[50];

            byte[][] char_arr_values = new byte[DEFAULT_ARR_SZ][];
            int[] char_arr_values_positions = new int[DEFAULT_ARR_SZ];

            int_values[0] = (INT_SZ * 4);
            int_values[1] = -544564654;//hash concepts
            int_values[2] = 0;

            //ensuite tous les positions de debut d'uri de concepts ?
            int_values[3] = 0;
            //int_values[4] = 0;

            int_values_positions[0] = header_size + 0;
            int_values_positions[1] = header_size + 4;
            int_values_positions[2] = header_size + 8;
            int_values_positions[3] = header_size + 12;
            System.out.println("header size is "+int_values_positions[0]);
            //System.out.println(""+aBytes[int_values_positions[0]]);

            char[] toCharArray = concepts[0].toCharArray();
            int ll = toCharArray.length;
            char_arr_values[0] = new byte[ll];
            for(int c=0;c < ll; c++){
                char_arr_values[0][c] = (byte)toCharArray[c];
            }
            char_arr_values_positions[0] = header_size + (4 * INT_SZ) + (concepts.length * INT_SZ);
            int_values[4] = ll;
            int_values_positions[4] = (header_size + (4 * INT_SZ));
            System.out.println(""+concepts[0]+" => "+Arrays.toString(char_arr_values[0]));
            System.out.println("starting at "+int_values[3]);
            System.out.println("uri starting at "+char_arr_values_positions[0]);
            System.out.println("s="+int_values_positions[4]);

            int last_concept = 0;
            for(int i=1;i < concepts.length;i++){
                System.out.println("------------------------");
                if(concepts[i] == null){
                    System.out.println("concept is null, skipping...");
                    int_values[4 + i] = 0;//on copie la depart de la position d'avant ?
                    int_values_positions[4 + i] = (header_size + (4 * INT_SZ)) + (i * INT_SZ);
                    //cursor += 1;
                    continue;
                }
                //System.out.println("cursor is "+cursor);
                toCharArray = concepts[i].toCharArray();
                ll = toCharArray.length;
                char_arr_values[i] = new byte[ll];
                for(int c=0;c < ll; c++){
                    char_arr_values[i][c] = (byte)toCharArray[c];
                }
                //System.out.println(""+i+" refering to "+(i-cursor)+" => "+int_values[4 + ((last_concept))]);
                char_arr_values_positions[i] = header_size + (4 * INT_SZ) + (concepts.length * INT_SZ) + int_values[4 + ((last_concept))];//position du last qui est pas null
                int_values[4 + i] = int_values[4 + (last_concept)] + ll;//meme chose
                int_values_positions[4 + i] = (header_size + (4 * INT_SZ)) + (i * INT_SZ);//ca ca bouge pas
                System.out.println(""+concepts[i]+" => "+Arrays.toString(char_arr_values[i]));
                System.out.println("finishing at "+int_values[4 + i]);
                System.out.println("uri starting at "+char_arr_values_positions[i]);
                System.out.println("s="+int_values_positions[4 + i]);
                System.out.println("and l is "+ll);
                last_concept = i;
                //System.arraycopy(char_arr_values[cursor], 0, aBytes, char_arr_values_positions[cursor], char_arr_values[cursor].length);
                //cursor += 1;
            }

            for(int i=0;i < (4 + concepts.length);i++){
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(int_values[i]).array();
                System.arraycopy(bytes, 0, aBytes, int_values_positions[i], INT_SZ);
            }
            //System.out.println("byte="+Arrays.toString(aBytes));
            for(int i=0;i < (concepts.length);i++){
                if(int_values[4 + i] != 0){
                    //System.out.println("i=>"+int_values[i]);
                    //System.out.println(">>"+char_arr_values_positions[i]);
                    //System.out.println(">>"+Arrays.toString(char_arr_values[i]));
                    System.arraycopy(char_arr_values[i], 0, aBytes, char_arr_values_positions[i], char_arr_values[i].length);
                }
                //ici si la position est zero, ca veut dire que le concept existe pas...
            }
            //System.out.println("byte="+Arrays.toString(aBytes));


            /**
             * ici on a place tout les concepts on va calcuelr la position pour
             * la partie qui suit, les proprietes
             */
            {
                concept_part_end = char_arr_values_positions[last_concept] + char_arr_values[last_concept].length;
                //couper ca semble correct, ca chage rien d'autre
                System.out.println("la partie concepts finit a "+concept_part_end);
                System.out.println(""+aBytes[concept_part_end]+" et "+aBytes[concept_part_end-1]);
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(concept_part_end).array();
                System.arraycopy(bytes, 0, aBytes, 58, INT_SZ);
            }
            //System.out.println("byte="+Arrays.toString(aBytes));

            //un triplet = array de 3 ids => 1 concept, 1 prop et 1 concept

        }


        //header des proprietes
        /**
         * ne pas oublier les domaines et codomaines ?
         * donne dans les triplets ?
         */
        final int prop_part_end;
        {

            int debut_h_prop = concept_part_end;

            int[] int_values = new int[DEFAULT_ARR_SZ];
            int[] int_values_positions = new int[DEFAULT_ARR_SZ];

            byte[][] char_arr_values = new byte[DEFAULT_ARR_SZ][];
            int[] char_arr_values_positions = new int[DEFAULT_ARR_SZ];

            int_values[0] = (INT_SZ * 4);
            int_values[1] = +56456465;//hash concepts
            int_values[2] = 0;

            //ensuite tous les positions de debut d'uri de concepts ?
            //
            int_values[3] = 0;
            //int_values[4] = 0;

            int_values_positions[0] = debut_h_prop + 0;
            int_values_positions[1] = debut_h_prop + 4;
            int_values_positions[2] = debut_h_prop + 8;
            int_values_positions[3] = debut_h_prop + 12;
            System.out.println("prop start is "+int_values_positions[0]);
            //System.out.println(""+aBytes[int_values_positions[0]]);
            /**
             * ceci n'est jamais ecrit ???
             */


            char[] toCharArray = proprietes[0].toCharArray();
            int ll = toCharArray.length;
            char_arr_values[0] = new byte[ll];
            for(int c=0;c < ll; c++){
                char_arr_values[0][c] = (byte)toCharArray[c];
            }
            char_arr_values_positions[0] = debut_h_prop + (4 * INT_SZ) + (proprietes.length * INT_SZ);
            int_values[4] = ll;
            int_values_positions[4] = (debut_h_prop + (4 * INT_SZ));
            System.out.println(""+proprietes[0]+" => "+Arrays.toString(char_arr_values[0]));
            System.out.println("starting at "+int_values[3]);
            System.out.println("uri starting at "+char_arr_values_positions[0]);
            System.out.println("s="+int_values_positions[4]);
            System.out.println("and has l "+ll);

            int last_prop = 0;
            for(int i=1;i < proprietes.length;i++){
                if(proprietes[i] == null){
                    System.out.println("prop is null, skipping...");
                    int_values[4 + i] = 0;//on copie la depart de la position d'avant ?
                    int_values_positions[4 + i] = (debut_h_prop + (4 * INT_SZ)) + (i * INT_SZ);
                    continue;
                }
                toCharArray = proprietes[i].toCharArray();
                ll = toCharArray.length;
                char_arr_values[i] = new byte[ll];
                for(int c=0;c < ll; c++){
                    char_arr_values[i][c] = (byte)toCharArray[c];
                }
                char_arr_values_positions[i] = debut_h_prop + (4 * INT_SZ) + (proprietes.length * INT_SZ) + int_values[4 + last_prop];
                int_values[4 + i] = int_values[4 + last_prop] + ll;
                int_values_positions[4 + i] = (debut_h_prop + (4 * INT_SZ)) + (i * INT_SZ);
                System.out.println(""+proprietes[i]+" => "+Arrays.toString(char_arr_values[i]));
                System.out.println("starting at "+int_values[4 +i]);
                System.out.println("uri starting at "+char_arr_values_positions[i]);
                System.out.println("s="+int_values_positions[4 + i]);
                System.out.println("and has l "+ll);
                last_prop = i;
            }

            for(int i=0;i < (4 + proprietes.length);i++){
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(int_values[i]).array();
                System.arraycopy(bytes, 0, aBytes, int_values_positions[i], INT_SZ);
            }
            //System.out.println("byte="+Arrays.toString(aBytes));
            for(int i=0;i < (proprietes.length);i++){
                System.arraycopy(char_arr_values[i], 0, aBytes, char_arr_values_positions[i], char_arr_values[i].length);
            }
            //System.out.println("byte="+Arrays.toString(aBytes));

            {
                //prop_part_end = char_arr_values_positions[proprietes.length-1] + char_arr_values[proprietes.length-1].length;
                prop_part_end = char_arr_values_positions[last_prop] + char_arr_values[last_prop].length;
                System.out.println("la partie props finit a "+prop_part_end);
                System.out.println(""+aBytes[prop_part_end]+" et "+aBytes[prop_part_end-1]);
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(prop_part_end).array();
                System.arraycopy(bytes, 0, aBytes, 62, INT_SZ);
            }
            //System.out.println("byte="+Arrays.toString(aBytes));
        }


        //header des triplets
        final int triple_fin;
        {

            int debut_h_triples = prop_part_end;

            int[] int_values = new int[DEFAULT_ARR_SZ];
            int[] int_values_positions = new int[DEFAULT_ARR_SZ];

            int_values[0] = (INT_SZ * 4);
            int_values[1] = +787987213;//hash concepts
            int_values[2] = 0;

            //ensuite tous les positions de debut d'uri de concepts ?
            //
            int_values[3] = 0;
            //int_values[4] = 0;

            int_values_positions[0] = debut_h_triples + 0;
            int_values_positions[1] = debut_h_triples + 4;
            int_values_positions[2] = debut_h_triples + 8;
            int_values_positions[3] = debut_h_triples + 12;
            System.out.println("triple start is "+int_values_positions[0]);
            //System.out.println(""+aBytes[int_values_positions[0]]);
            /**
             * ceci n'est jamais ecrit ???
             */


            final int cost_of_triple = (INT_SZ * 3)+1;
            //boucler sur les triplets
            for(int i=0;i < triplets.length;i++){
                final int offset_for_t_i = debut_h_triples + (4 * INT_SZ) + (i * cost_of_triple);
                //un  triplet va etre compose de
                //=> id de sujet (int)
                //=> id de prop (int)
                //=> id de objet (int)
                //=> infos sur les resources (byte)
                final int[] triple = triplets[i];
                //infos
                {
                    //dependament du type de resource
                    byte info = 0;
                    if(triple[0]==0 && triple[3]==0){
                        info = 0;
                    }
                    else if(triple[0]==1 && triple[3]==1){
                        info = 3;
                    }
                    else if(triple[0]==1 && triple[3]==0){
                        info = 1;
                    }
                    else if(triple[0]==0 && triple[3]==1){
                        info = 2;
                    }
                    aBytes[offset_for_t_i] = info;
                    System.out.println("for "+i+" info was "+info);
                }
                //sujet
                {
                    final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(triple[1]).array();
                    System.arraycopy(bytes, 0, aBytes, offset_for_t_i + BYTE_SZ, INT_SZ);
                }
                //predicat
                {
                    final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(triple[2]).array();
                    System.arraycopy(bytes, 0, aBytes, offset_for_t_i + INT_SZ + BYTE_SZ, INT_SZ);
                }
                //objet
                {
                    final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(triple[4]).array();
                    System.arraycopy(bytes, 0, aBytes, offset_for_t_i + (2 * INT_SZ) + BYTE_SZ, INT_SZ);
                }
                //System.out.println("byte="+Arrays.toString(aBytes));
            }

            for(int i=0;i < totalInstLinks;i++){
                final int offset_for_t_i = debut_h_triples + (4 * INT_SZ) + ((i+triplets.length) * cost_of_triple);
                byte info = 1;
                aBytes[offset_for_t_i] = info;
                System.out.println("for "+i+" info was "+info);

                final int[] triple = instance_triples[i];
                //sujet
                {
                    final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(triple[1]).array();
                    System.arraycopy(bytes, 0, aBytes, offset_for_t_i + BYTE_SZ, INT_SZ);
                }
                //predicat
                {
                    final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(triple[2]).array();
                    System.arraycopy(bytes, 0, aBytes, offset_for_t_i + INT_SZ + BYTE_SZ, INT_SZ);
                }
                //objet
                {
                    final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(triple[4]).array();
                    System.arraycopy(bytes, 0, aBytes, offset_for_t_i + (2 * INT_SZ) + BYTE_SZ, INT_SZ);
                }

            }

            {
                triple_fin = (INT_SZ * 4) + debut_h_triples+(cost_of_triple*(triplets.length+totalInstLinks));
                System.out.println("dealt with "+(triplets.length+totalInstLinks)+" triples");
                System.out.println("next field starts at "+triple_fin);
                System.out.println(">>"+aBytes[triple_fin]+" and "+aBytes[triple_fin-1]);
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(triple_fin).array();
                System.arraycopy(bytes, 0, aBytes, 66, INT_SZ);
            }
        }


        /**
         * serialisation des instances
         */
        final int instance_fin;
        {
            int depart_header_instances = triple_fin;

            int[] int_values = new int[DEFAULT_ARR_SZ];
            int[] int_values_positions = new int[DEFAULT_ARR_SZ];

            byte[][] char_arr_values = new byte[DEFAULT_ARR_SZ][];
            int[] char_arr_values_positions = new int[DEFAULT_ARR_SZ];

            int_values[0] = (INT_SZ * 4);
            int_values[1] = -465451;//hash concepts
            int_values[2] = 0;//index du header des types des instances

            //ensuite tous les positions de debut d'uri de concepts ?
            //
            int_values[3] = 0;
            //int_values[4] = 0;

            int_values_positions[0] = depart_header_instances + 0;
            int_values_positions[1] = depart_header_instances + INT_SZ;
            int_values_positions[2] = depart_header_instances + (2*INT_SZ);
            int_values_positions[3] = depart_header_instances + (3*INT_SZ);
            System.out.println("instance header size is "+int_values_positions[0]);
            System.out.println(""+aBytes[int_values_positions[0]]);


            char[] toCharArray = instances[0].stringValue().toCharArray();
            int ll = toCharArray.length;
            char_arr_values[0] = new byte[ll];
            for(int c=0;c < ll; c++){
                char_arr_values[0][c] = (byte)toCharArray[c];
            }
            char_arr_values_positions[0] = depart_header_instances + (4 * INT_SZ) + (instances.length * INT_SZ);
            int_values[4] = ll;
            int_values_positions[4] = (depart_header_instances + (4 * INT_SZ));
            System.out.println(""+instances[0]+" => "+Arrays.toString(char_arr_values[0]));
            System.out.println("starting at "+int_values[3]);
            System.out.println("uri starting at "+char_arr_values_positions[0]);
            System.out.println("s="+int_values_positions[4]);
            System.out.println("and size is "+ll);
            for(int i=1;i < instances.length;i++){
                toCharArray = instances[i].stringValue().toCharArray();
                ll = toCharArray.length;
                char_arr_values[i] = new byte[ll];
                for(int c=0;c < ll; c++){
                    char_arr_values[i][c] = (byte)toCharArray[c];
                }
                char_arr_values_positions[i] = depart_header_instances + (4 * INT_SZ) + (instances.length * INT_SZ) + int_values[4 + (i - 1)];
                int_values[4 + i] = int_values[4 + (i - 1)] + ll;
                int_values_positions[4 + i] = (depart_header_instances + 16) + (i * INT_SZ);
                System.out.println(""+instances[i]+" => "+Arrays.toString(char_arr_values[i]));
                System.out.println("starting at "+int_values[4 +i]);
                System.out.println("uri starting at "+char_arr_values_positions[i]);
                System.out.println("s="+int_values_positions[4 + i]);
                System.out.println("and size is "+ll);
            }

            for(int i=0;i < (4 + instances.length);i++){
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(int_values[i]).array();
                System.arraycopy(bytes, 0, aBytes, int_values_positions[i], INT_SZ);
            }
            //System.out.println("byte="+Arrays.toString(aBytes));
            for(int i=0;i < (instances.length);i++){
                System.arraycopy(char_arr_values[i], 0, aBytes, char_arr_values_positions[i], char_arr_values[i].length);
            }
            //System.out.println("byte="+Arrays.toString(aBytes));


            int debut_instance_position_h = char_arr_values_positions[instances.length-1] + char_arr_values[instances.length-1].length;
            int[] instance_header_values = new int[instances.length];

            int debut_instance_position_v = debut_instance_position_h + (instances.length * INT_SZ);
            System.out.println("debut instanes is "+debut_instance_position_h);
            System.out.println("on a "+instances.length+" instances");
            instance_header_values[0] = debut_instance_position_v;

            //jenaWrapper.getOntClassByIndividualURI( inst.getURI() )

            {
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(1).array();
                //final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(instance_types[0].length).array();
                System.arraycopy(bytes, 0, aBytes, instance_header_values[0], INT_SZ);
            }

            int not_found_classid = 0;

            //et ici on met les valeurs
            for(int j=1;j < (1+1);j++){
                //for(int j=1;j < (instance_types[0].length+1);j++){
                {
                    final Set<Value> objects = jenaWrapper.filter(instances[0], RDF.TYPE, null).objects();
                    objects.remove(OWL.NAMEDINDIVIDUAL);

                    if(objects.size() > 1){
                        System.out.println("We have more than one type for "+instances[0]);
                        System.exit(1);
                    }
                    IRI ontClassByIndividualURI = (IRI)objects.iterator().next();
                    //OntClass ontClassByIndividualURI = jenaWrapper.getOntClassByIndividualURI(instances[0].getURI());
                    //convertir cette uri en id de concept
                    System.out.println("instance "+instances[0].stringValue());
                    System.out.println("class id ="+ontClassByIndividualURI);
                    final Concept c = (Concept)ontology.index_concepts_by_name.get(ontClassByIndividualURI.stringValue());
                    //rajouter un check null pour concept
                    final Integer tmp = c.index;//mapping.idsByClasses.get(/*ontClassByIndividualURI.stringValue()*/c);
                    if(tmp == null){
                        System.out.println("mapping cannot find "+ontClassByIndividualURI.stringValue());
                        not_found_classid += 1;
                        continue;
                    }
                    int idbyClass = tmp;
                    System.out.println("l'instance "+instances[0].stringValue()+" has class "+idbyClass+" ("+ontClassByIndividualURI.stringValue()+")");
                    //final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(instance_types[0][j-1]).array();
                    final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(idbyClass).array();
                    System.out.println("=>"+Arrays.toString(bytes));
                    System.arraycopy(bytes, 0, aBytes, instance_header_values[0]+(j * INT_SZ), INT_SZ);
                }
            }

            //on va lier les instances a leurs types
            for(int i=1;i < instances.length;i++){
                //for(int i=1;i < instance_types.length;i++){
                instance_header_values[i] = instance_header_values[i-1] + ((1+1) * INT_SZ);
                //instance_header_values[i] = instance_header_values[i-1] + ((instance_types[i-1].length+1) * INT_SZ);
                //a j=0, on met la longueur du tableau
                {
                    final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(1).array();
                    //final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(instance_types[i].length).array();
                    System.arraycopy(bytes, 0, aBytes, instance_header_values[i], INT_SZ);
                }
                //et ici on met les valeurs
                for(int j=1;j < (1+1);j++){
                    //for(int j=1;j < (instance_types[i].length+1);j++){
                    {
                        final Set<Value> objects = jenaWrapper.filter(instances[0], RDF.TYPE, null).objects();
                        if(objects.size() > 1){
                            System.out.println("We have more than one type for "+instances[0]);
                            System.exit(1);
                        }
                        IRI ontClassByIndividualURI = (IRI)objects.iterator().next();
                        final Concept c = (Concept)ontology.index_concepts_by_name.get(ontClassByIndividualURI.stringValue());
                        //OntClass ontClassByIndividualURI = jenaWrapper.getOntClassByIndividualURI(instances[i].getURI());//recupere la classe de l'instance
                        //convertir cette uri en id de concept
                        Integer tmp = c.index;//mapping.idsByClasses.get(/*ontClassByIndividualURI*/c);
                        if(tmp == null){
                            System.out.println("mapping cannot find "+ontClassByIndividualURI.stringValue());
                            not_found_classid += 1;
                            continue;
                        }
                        int idbyClass = tmp;
                        System.out.println("l'instance "+instances[i].stringValue()+" has class "+idbyClass+" ("+ontClassByIndividualURI.stringValue()+")");
                        //final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(instance_types[0][j-1]).array();
                        final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(idbyClass).array();

                        //final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(instance_types[i][j-1]).array();
                        System.out.println("=>"+Arrays.toString(bytes));
                        System.arraycopy(bytes, 0, aBytes, instance_header_values[i]+(j * INT_SZ), INT_SZ);
                    }
                }
            }
            System.out.println("not found = "+not_found_classid+" /"+instances.length);
            //System.exit(1);
            //on place le header...
            for(int i=0;i < instance_header_values.length;i++){
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(instance_header_values[i]).array();
                System.arraycopy(bytes, 0, aBytes, debut_instance_position_h + (i * INT_SZ), INT_SZ);
            }
            //on doit aussi placer le debut de ce tableau dans le header des instances

            {
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(debut_instance_position_h).array();
                System.arraycopy(bytes, 0, aBytes, int_values_positions[2], INT_SZ);
            }

            {
                //instance_fin = char_arr_values_positions[instances.length-1] + char_arr_values[instances.length-1].length;
                //instance_fin = instance_header_values[instances.length-1] + ((instance_types[instances.length-1].length+1) * INT_SZ);
                instance_fin = instance_header_values[instances.length-1] + ((1+1) * INT_SZ);
                System.out.println("la partie instances finit a "+instance_fin);
                System.out.println(""+aBytes[instance_fin]+" et "+aBytes[instance_fin-1]);
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(instance_fin).array();
                System.arraycopy(bytes, 0, aBytes, 70, INT_SZ);
            }
            //System.out.println("byte="+Arrays.toString(aBytes));
        }


        final int fin_mxc;
        //serialiser la matrice de descendance
        {
            final int begin_pos = instance_fin;//en vrai prendre la fin du field precedent...

            final Map<Character, HashSet<Character>> c_amx = ontology.ancestor_matrix_c.matrix;//.size();
            //creer un header ?

            //un tableau de taille nbr concepts => position de depart de chacun
            //ensuite a l'offset on liste les id dans l'ordre....
            //a la lecture ya qu'a lire l'index. puis on peut charger chacun d'entre eux...
            Integer[] toArray = new Integer[c_amx.size()];
            Character[] _toArray = c_amx.keySet().toArray(new Character[c_amx.size()]);
            for(int i=0;i< _toArray.length;i++){//Character c : _toArray){
                toArray[i] = (int)_toArray[i];
            }
            Arrays.sort(toArray);
            int max = 0;
            for(int c : toArray){
                if(max < c) max = c;
            }
            byte[] matrix_header = new byte[(max+1)*4];
            //int next_offset = INT_SZ + matrix_header.length + (begin_pos+(((INT_SZ * 3)+1)*triplets.length));
            int next_offset = INT_SZ + matrix_header.length + begin_pos;
            for(int i=0;i < toArray.length;i++){
                final int cle = toArray[i];
                HashSet<Character> _get = c_amx.get((char) cle);
                final Character[] get = _get.toArray(new Character[_get.size()]);
                Arrays.sort(get);//pour etre sur...

                final int l = get.length;
                //on complete le header
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(next_offset).array();
                System.arraycopy(bytes, 0, matrix_header, (cle*INT_SZ), INT_SZ);

                {
                    final byte[] _bytes = ByteBuffer.allocate(INT_SZ).putInt(l).array();
                    System.arraycopy(_bytes, 0, aBytes, next_offset, INT_SZ);
                }
                for(int j=0;j<l;j++){
                    final byte[] _bytes = ByteBuffer.allocate(INT_SZ).putInt((int)get[j]).array();
                    System.arraycopy(_bytes, 0, aBytes, next_offset + ((j+1) * INT_SZ), INT_SZ);
                }
                next_offset += ((l+1)*INT_SZ);
                System.out.println("key="+cle+" et l="+l+" so next offset is "+next_offset);
            }
            //pour l'avoir a l'envers
            /*for(int b=INT_SZ;b<matrix_header.length;b+=INT_SZ){
                System.arraycopy(matrix_header, (matrix_header.length - b), aBytes, INT_SZ+(begin_pos+(((INT_SZ * 3)+1)*triplets.length)) + b, INT_SZ);
            }*/
            //pour l'avoir a l'endroit
            for(int b=0;b<matrix_header.length;b+=INT_SZ){
                //System.arraycopy(matrix_header, b, aBytes, INT_SZ+(begin_pos+(((INT_SZ * 3)+1)*triplets.length)) + b, INT_SZ);
                System.arraycopy(matrix_header, b, aBytes, INT_SZ+begin_pos + b, INT_SZ);
            }
            System.out.println("matrix header ="+Arrays.toString(matrix_header));
            //System.out.println("byte="+Arrays.toString(aBytes));

            final byte[] _bytes = ByteBuffer.allocate(INT_SZ).putInt(max).array();
            //System.arraycopy(_bytes, 0, aBytes, (begin_pos+(((INT_SZ * 3)+1)*triplets.length)), INT_SZ);
            System.arraycopy(_bytes, 0, aBytes, begin_pos, INT_SZ);
            fin_mxc = next_offset;
            {
                System.out.println("la partie mxc finit a "+fin_mxc);
                System.out.println(""+aBytes[fin_mxc]+" et "+aBytes[fin_mxc-1]);
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(fin_mxc).array();
                System.arraycopy(bytes, 0, aBytes, 74, INT_SZ);
                System.out.println(">>>"+aBytes[77]);
            }
        }


        final int fin_mxp;
        //serialiser la matrice de descendance
        {
            final int begin_pos = fin_mxc;//en vrai prendre la fin du field precedent...

            final Map<Character, HashSet<Character>> p_amx = ontology.ancestor_matrix_r.matrix;
            Integer[] toArray = new Integer[p_amx.size()];
            Character[] _toArray = p_amx.keySet().toArray(new Character[p_amx.size()]);
            for(int i=0;i< _toArray.length;i++){//Character c : _toArray){
                toArray[i] = (int)_toArray[i];
            }
            Arrays.sort(toArray);
            int max = 0;
            for(int c : toArray){
                if(max < c) max = c;
            }
            byte[] matrix_header = new byte[(max+1)*4];
            //int next_offset = INT_SZ + matrix_header.length + (begin_pos+(((INT_SZ * 3)+1)*triplets.length));
            int next_offset = INT_SZ + matrix_header.length + begin_pos;
            for(int i=0;i < toArray.length;i++){
                final int cle = toArray[i];
                HashSet<Character> _get = p_amx.get((char) cle);
                final Character[] get = _get.toArray(new Character[_get.size()]);
                Arrays.sort(get);//pour etre sur...

                final int l = get.length;
                //on complete le header
                final byte[] bytes = ByteBuffer.allocate(INT_SZ).putInt(next_offset).array();
                System.arraycopy(bytes, 0, matrix_header, (cle*INT_SZ), INT_SZ);

                {
                    final byte[] _bytes = ByteBuffer.allocate(INT_SZ).putInt(l).array();
                    System.arraycopy(_bytes, 0, aBytes, next_offset, INT_SZ);
                }
                for(int j=0;j<l;j++){
                    final byte[] _bytes = ByteBuffer.allocate(INT_SZ).putInt((int)get[j]).array();
                    System.arraycopy(_bytes, 0, aBytes, next_offset + ((j+1) * INT_SZ), INT_SZ);
                }
                next_offset += ((l+1)*INT_SZ);
                System.out.println("key="+cle+" et l="+l+" so next offset is "+next_offset);
            }



            //pour l'avoir a l'envers
            /*for(int b=INT_SZ;b<matrix_header.length;b+=INT_SZ){
                System.arraycopy(matrix_header, (matrix_header.length - b), aBytes, INT_SZ+(begin_pos+(((INT_SZ * 3)+1)*triplets.length)) + b, INT_SZ);
            }*/
            //pour l'avoir a l'endroit
            for(int b=0;b<matrix_header.length;b+=INT_SZ){
                //System.arraycopy(matrix_header, b, aBytes, INT_SZ+(begin_pos+(((INT_SZ * 3)+1)*triplets.length)) + b, INT_SZ);
                System.arraycopy(matrix_header, b, aBytes, INT_SZ+begin_pos + b, INT_SZ);
            }
            System.out.println("matrix header ="+Arrays.toString(matrix_header));
            //System.out.println("byte="+Arrays.toString(aBytes));

            final byte[] _bytes = ByteBuffer.allocate(INT_SZ).putInt(max).array();
            //System.arraycopy(_bytes, 0, aBytes, (begin_pos+(((INT_SZ * 3)+1)*triplets.length)), INT_SZ);
            System.arraycopy(_bytes, 0, aBytes, begin_pos, INT_SZ);
            fin_mxp = next_offset;
        }

        rightmost_byte = fin_mxp;
        /**
         * pour un concept
         * id (int)
         * longueur uri (int)
         * uri (variable)
         * namespace (variable ?)
         *
         */

        /**
         * tous les ids avec longueur uri et prochain concept offset
         * puis toutes les uri regroupees dans un endroit plus loin
         * comme ca on peut lire un concept sans savoir son uri directement via son id => debut + offset=id)
         */

        write(outputFile, aBytes, compression);

        //final LinkedList<Relation> allProperties =
        //LinkedList.LinkedNode next = allProperties.next();
        //Object o = next.value;
        //Relation r = (Relation)o;
        //System.out.println("relation:"+r);//r.name et r.index ?

        /*final LinkedList<Relation> ll = ontology.getAllProperties();
        if(ll != null && ll.curr() != null){
            do{
                Relation r = ll.curr().value;
                System.out.println("Next:"+r.toString());
            }
            while(ll.hasNext());
        }*/

        System.out.println("used "+((rightmost_byte/(float)aBytes.length)*100)+"% of file contents with "+aBytes.length);

        //comment recuperer les uri ?
        //utiliser jena ? (translator)

        //tester une serialisation de property
        //tester une sertialisation de triplet
        //mettre dans les headers le nombre de root concepts/props
        //puis tester la serialisation de la matrice nxn => descendance...

    }

    public static OntoRepresentation deserialize(boolean compression){
        final OntoRepresentation ontology = new OntoRepresentation();
        final String filename = "test.bowl";

        //final int header_sz = 255;
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
                System.out.println("big endian");
            }
            else if(endianness == LITTLEE){
                System.out.println("little endian, reversing endianness to big endian");
                //reverse_endianess(byteFile);
            }
            else{
                System.out.println("endianness unknown...");
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
                System.out.println("headersz="+header_sz);
            }
            //on recupere le nombre de concepts
            {
                byte[] b_nbr_c = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_nbr_c, b_nbr_c, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_nbr_c); // big-endian by default
                int nbr_c = wrapped.getInt();
                System.out.println("nbr concept="+nbr_c);
                nbr_classes = nbr_c;
            }

            //on recupere le hash du fichier
            {
                byte[] b_hash_all = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_hash_all, b_hash_all, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_hash_all); // big-endian by default
                int hash_all = wrapped.getInt();
                System.out.println("hashall="+hash_all);
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
                System.out.println("ontoname="+ontoname);
            }
            int offset_c_h = 0;
            //on recupere offset du header de concepts
            {
                byte[] b_offset_c_h = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_c, b_offset_c_h, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_offset_c_h); // big-endian by default
                offset_c_h = wrapped.getInt();
                System.out.println("concept_header_offset="+offset_c_h);
            }
            int header_c_sz = 0;
            //
            {
                byte[] b_c_header_sz = new byte[INT_SZ];
                System.arraycopy(byteFile, offser_header_sz, b_c_header_sz, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_c_header_sz); // big-endian by default
                header_c_sz = wrapped.getInt();
                System.out.println("c_headersz="+header_c_sz);
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
                        System.out.println("on essaie de lire pour "+(id_concept-1));
                        int concept_uri_prev_offset = 0;int cursor = 2;
                        while(concept_uri_prev_offset == 0 && (cursor < (id_concept+1))){
                            //lecture de la longueur de l'uri du concept precedent
                            System.arraycopy(byteFile, base_header + ((id_concept-cursor) * INT_SZ), b_concept_uri_prev_offset, 0, INT_SZ);
                            ByteBuffer wrapped_ = ByteBuffer.wrap(b_concept_uri_prev_offset); // big-endian by default
                            concept_uri_prev_offset = wrapped_.getInt();
                            cursor += 1;
                            System.out.println("necessaire de remonter de "+(cursor-2)+" cases...");
                        }
                        if(concept_uri_prev_offset != 0 || id_concept == 1){
                            //calcul de la longueur de l'uri, permet de determiner son emplacement dans le fichier
                            final int real_size = concept_uri_offset - concept_uri_prev_offset;
                            System.out.println("real size="+real_size+" ("+concept_uri_offset+" "+concept_uri_prev_offset+")");

                            //lecture de l'uri (string)
                            byte[] b_concept_uri = new byte[real_size];
                            System.arraycopy(byteFile, base_header + (nbr_classes * INT_SZ) + concept_uri_offset - real_size, b_concept_uri, 0, BYTE_SZ * real_size);
                            char[] concept_uri = new char[real_size];
                            for(int i=0;i<real_size;i++)
                                concept_uri[i] = (char)b_concept_uri[i];
                            System.out.println("concept uri "+Arrays.toString(b_concept_uri));
                            String concept_uri_s = String.copyValueOf(concept_uri);
                            System.out.println("concept id="+(id_concept-1)+" => "+concept_uri_s);
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

            int offset_p_h = 0;
            //on recupere offset du header de properties
            {
                byte[] b_offset_p_h = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_p, b_offset_p_h, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_offset_p_h); // big-endian by default
                offset_p_h = wrapped.getInt();
                System.out.println("properties_header_offset="+offset_p_h);
            }
            //on recupere le nombre de concepts
            {
                byte[] b_nbr_p = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_nbr_p, b_nbr_p, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_nbr_p); // big-endian by default
                int nbr_p = wrapped.getInt();
                System.out.println("nbr props="+nbr_p);
                nbr_properties = nbr_p;
            }

            int header_p_sz = 0;
            //
            {
                byte[] b_p_header_sz = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_p, b_p_header_sz, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_p_header_sz); // big-endian by default
                header_p_sz = wrapped.getInt();
                System.out.println("p_headersz="+header_c_sz);
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
                    int concept_uri_offset = wrapped.getInt();

                    if(concept_uri_offset != 0){
                        System.out.println("on essaie de lire pour "+(id_property-1));
                        int concept_uri_prev_offset = 0;int cursor = 2;
                        while(concept_uri_prev_offset == 0 && (cursor < (id_property+1))){
                            //lecture de la longueur de l'uri du concept precedent
                            System.arraycopy(byteFile, base_header + ((id_property-cursor) * INT_SZ), b_prop_uri_prev_offset, 0, INT_SZ);
                            ByteBuffer wrapped_ = ByteBuffer.wrap(b_prop_uri_prev_offset); // big-endian by default
                            concept_uri_prev_offset = wrapped_.getInt();
                            cursor += 1;
                            System.out.println("necessaire de remonter de "+(cursor-2)+" cases...");
                        }
                        if(concept_uri_prev_offset != 0 || id_property == 1){
                            //calcul de la longueur de l'uri, permet de determiner son emplacement dans le fichier
                            final int real_size = concept_uri_offset - concept_uri_prev_offset;
                            System.out.println("real size="+real_size+" ("+concept_uri_offset+" "+concept_uri_prev_offset+")");

                            //lecture de l'uri (string)
                            byte[] b_concept_uri = new byte[real_size];
                            System.arraycopy(byteFile, base_header + (nbr_classes * INT_SZ) + concept_uri_offset - real_size, b_concept_uri, 0, BYTE_SZ * real_size);
                            char[] concept_uri = new char[real_size];
                            for(int i=0;i<real_size;i++)
                                concept_uri[i] = (char)b_concept_uri[i];
                            System.out.println("prop uri "+Arrays.toString(b_concept_uri));
                            String concept_uri_s = String.copyValueOf(concept_uri);
                            System.out.println("prop id="+(id_property-1)+" => "+concept_uri_s);
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




            int offset_t_h = 0;
            //on recupere offset du header de triplets
            {
                byte[] b_offset_t_h = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_t, b_offset_t_h, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_offset_t_h); // big-endian by default
                offset_t_h = wrapped.getInt();
                System.out.println("triples_header_offset="+offset_t_h);
            }

            //on recupere le nombre de concepts
            {
                byte[] b_nbr_t = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_nbr_t, b_nbr_t, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_nbr_t); // big-endian by default
                int nbr_t = wrapped.getInt();
                System.out.println("nbr triples="+nbr_t);
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
                    System.out.println("t="+sujet+", "+pred+", "+obj+", "+infos);
                }
            }

            //on recupere le nombre d'instances
            {
                byte[] b_nbr_i = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_nbr_i, b_nbr_i, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_nbr_i); // big-endian by default
                int nbr_i = wrapped.getInt();
                System.out.println("nbr instances="+nbr_i);
                nbr_instances = nbr_i;
            }


            int offset_i_h = 0;
            //on recupere offset du header d'instances
            {
                byte[] b_offset_i_h = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_i, b_offset_i_h, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_offset_i_h); // big-endian by default
                offset_i_h = wrapped.getInt();
                System.out.println("instances_header_offset="+offset_i_h);
            }
            int header_i_sz = 0;
            {
                byte[] b_i_header_sz = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_i_h, b_i_header_sz, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_i_header_sz); // big-endian by default
                header_i_sz = wrapped.getInt();
                System.out.println("i_headersz="+header_i_sz);
            }

            {
                final int header_inst_types = offset_i_h + (2*INT_SZ);
                final int debut_h_inst;
                {
                    byte[] b_instance_types_offset = new byte[INT_SZ];
                    System.arraycopy(byteFile, header_inst_types, b_instance_types_offset, 0, INT_SZ);
                    ByteBuffer wrapped = ByteBuffer.wrap(b_instance_types_offset); // big-endian by default
                    debut_h_inst = wrapped.getInt();
                    System.out.println("le debut du header des types d'instances est a la pos "+debut_h_inst);
                }
                if(debut_h_inst!=0){
                    //parcourir le header en meme temps que les donnees
                    for(int id_instance=0;id_instance<nbr_instances;id_instance++){
                        byte[] b_instance_types_offset = new byte[INT_SZ];
                        System.arraycopy(byteFile, debut_h_inst + (id_instance * INT_SZ), b_instance_types_offset, 0, INT_SZ);
                        ByteBuffer wrapped = ByteBuffer.wrap(b_instance_types_offset); // big-endian by default
                        int instance_type_offset = wrapped.getInt();
                        System.out.println("instance no "+id_instance+" start at "+instance_type_offset);

                        byte[] b_instance_types_length = new byte[INT_SZ];
                        System.arraycopy(byteFile, instance_type_offset, b_instance_types_length, 0, INT_SZ);
                        ByteBuffer _wrapped = ByteBuffer.wrap(b_instance_types_length); // big-endian by default
                        int instance_type_l = _wrapped.getInt();
                        System.out.println("and has "+instance_type_l+" types");

                        for(int i=0;i < instance_type_l;i++){
                            byte[] b_instance_types_value = new byte[INT_SZ];
                            System.arraycopy(byteFile, instance_type_offset + ((i+1)*INT_SZ), b_instance_types_value, 0, INT_SZ);
                            ByteBuffer _wrapped_ = ByteBuffer.wrap(b_instance_types_value); // big-endian by default
                            //System.out.println(">>"+Arrays.toString(b_instance_types_value));
                            int instance_type_value = _wrapped_.getInt();
                            System.out.println("type="+instance_type_value);
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
                    System.out.println("real size="+real_size+" ("+instance_uri_offset+" "+instance_uri_prev_offset+")");

                    //lecture de l'uri (string)
                    byte[] b_instance_uri = new byte[real_size];
                    System.arraycopy(byteFile, base_header + (nbr_instances * INT_SZ) + instance_uri_offset - real_size, b_instance_uri, 0, BYTE_SZ * real_size);
                    char[] instance_uri = new char[real_size];
                    for(int i=0;i<real_size;i++)
                        instance_uri[i] = (char)b_instance_uri[i];

                    String instance_uri_s = String.copyValueOf(instance_uri);
                    System.out.println("instance id="+(id_instance-1)+" => "+instance_uri_s);
                }
            }





            int offset_mxc_h = 0;
            //on recupere offset du header d'instances
            {
                byte[] b_offset_mxc_h = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_mxc, b_offset_mxc_h, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_offset_mxc_h); // big-endian by default
                offset_mxc_h = wrapped.getInt();
                System.out.println("mxc_header_offset="+offset_mxc_h);
            }
            //recuperation de la matrice de descendance
            {
                final int debut_mtx = offset_mxc_h;
                int mtx_h = 0;
                byte[] b_h_mtx_sz = new byte[INT_SZ];
                System.arraycopy(byteFile, debut_mtx, b_h_mtx_sz, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_h_mtx_sz); // big-endian by default
                mtx_h = wrapped.getInt();
                System.out.println("header m size"+mtx_h);
                //on a max, ca veut dire qu'on lire header correctement


                //int debut_arr = 0;
                for(int i=0;i<(mtx_h+1);i++){
                    int debut_arr = 0;
                    {
                        byte[] b_offset_mtx = new byte[INT_SZ];
                        System.arraycopy(byteFile, debut_mtx+(INT_SZ*(1+i)), b_offset_mtx, 0, INT_SZ);
                        ByteBuffer _wrapped = ByteBuffer.wrap(b_offset_mtx); // big-endian by default
                        debut_arr = _wrapped.getInt();
                        System.out.println("offset pour "+i+" est "+debut_arr);

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
                            System.out.println("("+debut_arr+")pendant "+fin_arr);
                            fin_arr *= INT_SZ;//parce que c'est des int et pas des byte
                            fin_arr += 1 + debut_arr;
                        }
                        debut_arr += INT_SZ;//on va au premier
                        //et on lit toutes les cases...
                        while(debut_arr < fin_arr){
                            byte[] b_arr_val = new byte[INT_SZ];
                            System.arraycopy(byteFile, debut_arr, b_arr_val, 0, INT_SZ);
                            ByteBuffer _wrapped = ByteBuffer.wrap(b_arr_val); // big-endian by default
                            System.out.println("value is "+_wrapped.getInt());
                            debut_arr += INT_SZ;
                        }
                    }
                }

            }


            int offset_mxp_h = 0;
            //on recupere offset du header d'instances
            {
                byte[] b_offset_mxp_h = new byte[INT_SZ];
                System.arraycopy(byteFile, offset_h_mxp, b_offset_mxp_h, 0, INT_SZ);
                ByteBuffer wrapped = ByteBuffer.wrap(b_offset_mxp_h); // big-endian by default
                offset_mxp_h = wrapped.getInt();
                System.out.println("mxp_header_offset="+offset_mxp_h);
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
                    System.out.println("header m size"+mtx_h);
                    //on a max, ca veut dire qu'on lire header correctement


                    //int debut_arr = 0;
                    for(int i=0;i<(mtx_h+1);i++){
                        int debut_arr = 0;
                        {
                            byte[] b_offset_mtx = new byte[INT_SZ];
                            System.arraycopy(byteFile, debut_mtx+(INT_SZ*(1+i)), b_offset_mtx, 0, INT_SZ);
                            ByteBuffer _wrapped = ByteBuffer.wrap(b_offset_mtx); // big-endian by default
                            debut_arr = _wrapped.getInt();
                            System.out.println("offset pour "+i+" est "+debut_arr);

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
                                System.out.println("("+debut_arr+")pendant "+fin_arr);
                                fin_arr *= INT_SZ;//parce que c'est des int et pas des byte
                                fin_arr += 1 + debut_arr;
                            }
                            debut_arr += INT_SZ;//on va au premier
                            //et on lit toutes les cases...
                            while(debut_arr < fin_arr){
                                byte[] b_arr_val = new byte[INT_SZ];
                                System.arraycopy(byteFile, debut_arr, b_arr_val, 0, INT_SZ);
                                ByteBuffer _wrapped = ByteBuffer.wrap(b_arr_val); // big-endian by default
                                System.out.println("value is "+_wrapped.getInt());
                                debut_arr += INT_SZ;
                            }
                        }
                    }

                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Converter2Bowl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ontology;
    }

    public static void write(final String chemin, final byte[] aBytes, final boolean compression){
        Path path = Paths.get(chemin);
        if(compression){
            Deflater compressor = new Deflater();
            compressor.setLevel(Deflater.BEST_COMPRESSION);

            // Give the compressor the data to compress
            compressor.setInput(aBytes);
            compressor.finish();

            // Create an expandable byte array to hold the compressed data.
            // It is not necessary that the compressed data will be smaller than
            // the uncompressed data.
            ByteArrayOutputStream bos = new ByteArrayOutputStream(aBytes.length);

            // Compress the data
            byte[] buf = new byte[1024];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }
            try {
                bos.close();
            } catch (IOException e) {
            }

            // Get the compressed data
            final byte[] compressedData = bos.toByteArray();
            try {
                Files.write(path, compressedData); //creates, overwrites
            } catch (IOException ex) {
                Logger.getLogger(Converter2Bowl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            try {
                Files.write(path, aBytes); //creates, overwrites
            } catch (IOException ex) {
                Logger.getLogger(Converter2Bowl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void reverse_endianess(byte[] content){
        final int cl = content.length;
        if(cl % 4 != 0) {
            System.out.println("le nombre des octets est pas un multiple de 4...");
            System.exit(1);
        }
        for(int i=0;i< cl;i+=2){
            //inverser les bytes par deux
            byte tmp = content[i];
            content[i] = content[i+1];
            content[i+1] = tmp;
        }
        for(int i=0;i < cl;i += 4){
            byte[] tmp = new byte[2];
            System.arraycopy(content, i, tmp, 0, 2);
            System.arraycopy(content, i+2, content, i, 2);
            System.arraycopy(tmp, 0, content, i+2, 2);
        }
    }

    public static class Pair<A, B>
    {
        //-------------------------------------------------------------- Attributes
        private A first;
        private B second;

        //------------------------------------------------------------- Constructor
        /**
         * Creates a pair of elements containing the specified elements.
         *
         * @param first The first element of this pair.
         * @param second The second element of this pair.
         */
        public Pair( A first, B second )
        {
            super( );
            this.first = first;
            this.second = second;
        }

        //------------------------------------------------------- Getters / Setters
        /**
         * Gets the first element of the pair.
         *
         * @return The first element of this pair.
         */
        public A getFirst( )
        {
            return first;
        }

        /**
         * Gets the second element of this pair.
         *
         * @return The second element of this pair.
         */
        public B getSecond( )
        {
            return second;
        }

        public void setFirst(A first){
            this.first = first;
        }

        public void setSecond(B second){
            this.second = second;
        }

        //-------------------------------------------------- Auto-generated methods
        @Override
        public int hashCode( )
        {
            int hashFirst = first != null ? first.hashCode( ) : 0;
            int hashSecond = second != null ? second.hashCode( ) : 0;

            return ( hashFirst + hashSecond ) * hashSecond + hashFirst;
        }

        @Override
        public boolean equals( Object other )
        {
            if ( other instanceof Pair )
            {
                Pair<A, A> otherPair = ( Pair ) other;
                return ( ( this.first == otherPair.first || ( this.first != null
                        && otherPair.first != null && this.first
                        .equals( otherPair.first ) ) ) && ( this.second == otherPair.second || ( this.second != null
                        && otherPair.second != null && this.second
                        .equals( otherPair.second ) ) ) );
            }
            return false;
        }

        public String toString( )
        {
            return "(" + first + ", " + second + ")";
        }
    }
}


