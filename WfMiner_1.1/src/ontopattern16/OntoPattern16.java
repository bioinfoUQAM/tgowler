package ontopattern16;

import ca.uqam.gdac.framework.xml.SequenceFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import legacy.RawUserSequence;
import ontologyrep2.Concept;
import ontologyrep2.OntoRepresentation;
import ontologyrep2.RadixTree;
import ontopattern16.db.VerticalSeqDB;
import ontopattern16.db.VerticalSeqInstDB;
import ontopattern16.db.VerticalSeqPropDB;
import ontopattern16.db.tw_bitset;
import ontopatternmatching.Sequence;
import org.xml.sax.SAXException;
import static preprocessing.bowlLoader.deserialize;
import static preprocessing.bowlLoader.loadRawSequences;

/**
 * Nouvelle version de ontopattern avec notre matching custom mais qui va jusqu'au bout pour chaque sequence
 * comme ca, pas de backtraacking necessaire
 * @author Enridestroy
 */
public class OntoPattern16 {
    public static void main(String[] args) {
        HashMap<String, String[]> parametres = new HashMap<String, String[]>(){{
            //ontologie workflows phylogenetique
            put("phylo", new String[]{
                "ahmedphyloonto.bowl", "Stratified_GenSequences.xml", "http://www.semanticweb.org/ahmedhalioui/ontologies/2015/7/untitled-ontology-8#"
                //"C:\\Users\\Enridestroy\\Dropbox\\Sequential Pattern Mining\\Simulated Workflows\\Workflow Sequences\\Stratified_GenSequences.xml"
            });
            //ontologie tourisme
            put("tour", new String[]{
                "testformining.bowl", "resources/file-tests/sequencesTestPerformance.xml", "http://www.info.uqam.ca/Members/valtchev_p/mbox/ETP-tourism.owl#"
            });
        }}; 
        
        String mode = "tour";
        final String bowlFile = parametres.get(mode)[0];
        OntoRepresentation ontology = deserialize(bowlFile, true);
        
        //doSomeChecks(ontology);
        
        final String rawSequences = parametres.get(mode)[1];
        final String namespace = parametres.get(mode)[2];
        final ArrayList<Sequence> userSequences = new ArrayList<>();
        ArrayList<RawUserSequence> rawUserSequences = null;
        try{
            rawUserSequences = SequenceFactory.createRawUserSequences(rawSequences);
            if(rawSequences==null){
                return;
            }
        }
        catch (SAXException | IOException e ){
            System.out.println("on n'a pas pu charger les sequences brutes");
            return;
        }
        
        RadixTree.RadixNode localNameNode = ontology.index_instances_by_name.getNode(namespace.toCharArray(), ontology.index_instances_by_name.root);
        if(localNameNode == null) return;
        else loadRawSequences(rawUserSequences, userSequences, ontology, rawSequences, localNameNode);
        
        int[] vertical_db = null;
        
        final int nbr_seq = rawUserSequences.size();
        final int[] all_sequences_length = new int[nbr_seq];
        final int[] all_sequences_sum = new int[nbr_seq+1];
        
        /***
         * quelques calculs prelimiiaires
         */
        final int BLOCK_SIZE = 32;
        final int BLOCK_SIZE_SHIFT = 5;//2^5 = 32
        final int max_id_concept = Concept.ID;
        int sum_sizes = 0;
        for(int i=0;i < nbr_seq; i++){
            all_sequences_length[i] = rawUserSequences.get(i).nbElements();
            sum_sizes += all_sequences_length[i];
            all_sequences_sum[i+1] = all_sequences_sum[i] + all_sequences_length[i]; 
        }
        int range_of_one_item = sum_sizes;
        final int c = sum_sizes % BLOCK_SIZE;
        if(c!=0){
            range_of_one_item += (BLOCK_SIZE - c);
        }
        final int nbr_concepts = ontology.getAllConcepts().size;
        /** **/
        
        
        final int bd_sz = (range_of_one_item * nbr_concepts);///32;        
        final int actual_good_size = ((bd_sz + (BLOCK_SIZE-(bd_sz % BLOCK_SIZE))) >> BLOCK_SIZE_SHIFT)+1;//a cause que le division par 32 est pas exacte donc peut etre arrondie
        vertical_db = tw_bitset.int_nouveau_bitset(actual_good_size, 0);
        System.out.println("on a "+nbr_concepts+" concepts differents dans ontologie");
        System.out.println("on a "+nbr_seq+" sequences et sum="+range_of_one_item);
        System.out.println(""+Arrays.toString(all_sequences_length));
        System.out.println(""+Arrays.toString(all_sequences_sum));
        System.out.println("la taille de la vbd (concept) est de "+bd_sz+ " = "+(range_of_one_item * nbr_concepts));
        
        //construit la representation "verticale" des instances (on ne transforme pas encore les instances en mode vertical, 
        //car on ne les utilise pas pour la fouille. ici on ne fait que transformer les sequences de strings (uri)
        //vers des ids grace a l'ontologie (ou kb)
        final ArrayList<Sequence> sequences_avec_instances_ids = VerticalSeqInstDB.construire_vdbi(null, rawUserSequences, 
                nbr_concepts, nbr_seq, range_of_one_item, all_sequences_length, all_sequences_sum, ontology, BLOCK_SIZE, BLOCK_SIZE_SHIFT, localNameNode);
        
        //construit la representation verticale des concepts
        {
            
        
            VerticalSeqDB.construire_vdb(vertical_db, sequences_avec_instances_ids, nbr_concepts, nbr_seq, range_of_one_item, 
                all_sequences_length, all_sequences_sum, ontology, BLOCK_SIZE, BLOCK_SIZE_SHIFT, localNameNode);
            //lance la generalisation des concepts
            
            if(vertical_db!= null) 
                VerticalSeqDB.construit_vdb_generalisations(ontology, nbr_concepts, vertical_db, range_of_one_item, all_sequences_sum);
            else System.out.println("could not start generalisation because VBD is null.");
            
        }
        //construit la representation verticale des proprietes
        {
            /*
            int[] p_verticaldb = null;
        
            VerticalSeqPropDB.construire_vpdb(p_verticaldb, sequences_avec_instances_ids, nbr_concepts, nbr_seq, range_of_one_item, ontology, 
                    BLOCK_SIZE, BLOCK_SIZE_SHIFT, localNameNode);
            */
            //lance la generalisation???
        }
        
        /** ensuite, il faut retransformer les instances en concepts ?
        for(int k=0;k < currSeq.objects.size();k++){
            currSeq.objects.set(k, ontology.getInstance(currSeq.objects.get(k)).concept.index);
        }**/
        
        //construire les sequences avec id instances ?
        //construire representation verticale des instances ?
        //constuire les seq avec props ?
        //construire la representation verticale des props
        //transformer les instances en concepts
        //construire la representation verticale des concepts ?
        
        
        
        //System.out.println(""+(sum_sizes % BLOCK_SIZE));
        //System.out.println(""+(sum_sizes & (BLOCK_SIZE-1)));
        //x - (x / n) * n
        System.out.println("on a "+rawUserSequences.size()+" sequences...");
        
        //une fois qu'on a la bd verticale, 
        
        //on va devoir chercher les rootConcept frequents
        //construire le lexicographic tree de SPAM (S-steps) (construire la stack)
        //ensuite ajouter les proprietes a l'arbre
        
        //ensuite ajouter les specialisations
        
        
        //la stack:
        //on a besoin de conserver tous les resultats intermediaires...
        //a t'on besoin de copier les vecteurs de base ??
        //probablement parce qu'il faut les rafiner tout le temps a cause de props...
        //donc la stack est un array de int qui representent tous les vecteurs confondus
        //on doit alors avoir un header des debuts de chaque vecteur...
        //il faut paginer la stack...
        int[] stack_pg = new int[64];
        //chaque page est composee de blocs (PRISM)
        final byte BLOCK_SZ = 0;
        final byte SUPER_BLOCK_SZ = 0;
        
        //la queue s'agrandit toujours vers la droite, on empile des trucs uniquement...
        
        //on a aussi besoin de structurer le motif...
        //int[] => concepts
        //props ? apres chaque concept, on a les props sous forme de couple (id, pos). puisqu'elles ne peuevent aller que vers la gauche...
        //on devrait pouvoir retrouver la position dans la stack grace a cet ordre ? (pas sur...)
        
        //step one, compter les items frequents (est ce qu'on construit les position lists  ???)
        //est ce qu'on construit les Last position item ? (je pense que oui) => prendre le dernier blokc et demander le rightmost bit
        //on a deja
        
        //step deux, les combiner en mode spam (bettement) en dfs... reprendre la stack taches ?
        //la nuance c'est qu'on va maintenir une queue de transformations
        
        //pour l'instant on precalule rien du tout, on l'activera plus tard...
        
        //la item last position => PAID => cad si un item ne peut plus apparaitre alors en plus de ne pas tester le motif
        //on peut decrementer le support des autres motifs => aucun parcours de la bd..
        
        //spontanement on va pas utiliser les props frequents, toutes les props
        //ontopattern ne tient pas compte d'utiliser uniquement les items frequents (grosse erreur). idem pour les proprietes
        
        
        //il va falloir dev les operateurs de spam
        //transformation de vecteur pour la s-step
        //
        
        
        //il va falloir reflechir a la stack (comment on construit ca... dependances entre stacks...)
        
        //a chaque operation d'ajout d'lement on doit ajouter un vecteur de bits a la stack...
        //de deux choses possibles. soit chaque stack possede les choses, soit chaque stack reference des oeprations sur une pile globale.
        //pile globale pas bon pour du //.
        //vaut miuex pile locales... mais consomme plus de memoire... (a cause des copies)
        
        //quand on etend un motif, doit on copier les anciennes valeurs ??
        //normalement, ce serait bien des les copier au moment ou on doit les modifier, cad au moment ou frequent sur... (lazy)
        
        //regarder comment vbd classes fonctionne et calculer le support...
        //sortir les classes frequentes.
        //faire mm chose avec les props (mais ne pas les utiliser pour l'instant)
        
        //a partir de la combiner les classes => developper les s-step et intersection
        
        //chercher les k-sequences frequentes (avec specialisation)
        
        //ensuite rajouter les proprietes frequentes... (nouvel algo)
        //specialiser les proprietes
        
        //quand ca c fait, construire les ITEM_LAST_position et les PRECALCULS de prism
        //voir comment appliquer PAID
        
        
        //quand c fait, passer a la version spami-fts, hvsm
        
        
        
        final int minsupp = 5;
        
        
        
        int nbr_frequent_items = 0;
        final int[] supports = new int[nbr_concepts];
        int support_max = 0;
        int max_seq_len_blocks = 0;
        //garder les sids / items ?
        
        final int[] sids_by_item = new int[nbr_concepts << 1];
        final int[] actual_frequent_items;
        {
            final int[] frequent_items = new int[nbr_concepts];//borne superuieure
            for(int item_no=0;item_no!=nbr_concepts;++item_no){
                //pour chaque item
                //int block = vertical_db[(range_of_one_item * item_no) + offset_seq];
                int support = 0;
                //int block_supp = 0;
                int last_sid = 0;
                for(int i=0;i != nbr_seq; ++i){
                    final int offset_bits = (sum_sizes * item_no) + all_sequences_sum[i];
                    final int seq_len = (all_sequences_sum[i+1] - all_sequences_sum[i]);
                    /*int count = 0;
                    for(int z=0; z != seq_len;++z){
                        //sb.append().append(", ");
                        count += tw_bitset.get_bit_at(offset_bits + z, vertical_db) ? 1 : 0;
                    }*/
                    int blockcount_sid = tw_bitset.blockcount_partial(vertical_db, offset_bits, seq_len);
                    if(blockcount_sid != 0) {
                        if(support == 0) { sids_by_item[(nbr_frequent_items << 1)] = i; } //leftmost sid
                        support += 1;
                        last_sid = i;
                        if(max_seq_len_blocks < (((seq_len - (seq_len % 32))/ 32)+1)){
                            max_seq_len_blocks = ((seq_len - (seq_len % 32))/ 32) + 1;
                        }
                    }
                    //tw_bitset.popcount(vertical_db, offset_bits, offset_bits + seq_len);
                    //if(count != 0) support += 1;
                }
                sids_by_item[(nbr_frequent_items << 1)+1] = last_sid;//rightmost sid
                System.out.println("concept "+item_no+"="+ontology.getConcept(item_no).getName()+" has a support of "+support);
                //System.out.println("w/ blockcount => "+block_supp);
                if(!(support < minsupp)){
                    supports[nbr_frequent_items] = support;
                    frequent_items[nbr_frequent_items] = item_no;
                    nbr_frequent_items += 1;
                    if(support_max < support) support_max = support;
                }
            }
            System.out.println("on a "+nbr_frequent_items+" /"+nbr_concepts+" => "+((nbr_frequent_items/(float)nbr_concepts)*100)+"%");
            actual_frequent_items = new int[nbr_frequent_items];
            System.arraycopy(frequent_items, 0, actual_frequent_items, 0, nbr_frequent_items);
            System.out.println(Arrays.toString(actual_frequent_items));
        }
        
        for(int i=0;i!=nbr_frequent_items;++i){
            System.out.println("item "+actual_frequent_items[i]+"="+ontology.getConcept(actual_frequent_items[i]).getName()+" is frequent, lsid="+sids_by_item[i << 1]+", rsid="+sids_by_item[(i << 1) + 1]);
            System.out.println("its support is "+supports[i]+" so we have "+(1-( (float)supports[i]/(sids_by_item[(i<<1)+1] - sids_by_item[(i << 1)] + 1)))+"% of lost space...");
        }
        
        //donc ici, il va falloir construire une stack avec chaque item...
        
        //l ideal serait de mettre chaque stack dans un trie ?
        //non, parce que les stacks sont diffrententes apres...
        //je vais reprendre ma liste chainee dans gp ...., la customiser pour des int[]
        
        //simplement comme dans spam on pop et on ajoute...
                
        generate_threads_workspace(SID_WISE, nbr_frequent_items, support_max, minsupp, 64, 1, max_seq_len_blocks);//a verifier
        
        final int DEFAULT_STACK_NBR_OP = 10;
        //des que c fait, on peut ensuite rajouter les generalisations et les props
        //pour chaque item, creer sa stack... (par defaut considerer 10 transformations => sum_all * 10
        
        final int[][][] frequent_stacks = null;//defini a null pour l'instant, on va y mettre les stacks des items frequents....
        
        //construire les stack de tous les items, 
        //ensuite prendre le premier et y apposer les items...
        //storer toutes les stacks, enventuellement purger les stacks des sid useless...
        
        final CustomLinkedList_intArr dfs_stack;
        {
            System.out.println("--------------------------------");
            //final int support_item = supports[0];
            final int left_most_sid = sids_by_item[(0 << 1)];//trouve son sid le plus petit
            final int rightmost_sid = sids_by_item[(0 << 1) + 1];//trouve son plus grand sid
            
            final int range_sid = (rightmost_sid - left_most_sid);//>= support
            if((range_sid+1) < supports[0]) { 
                System.out.println("bad range vs support..."+actual_frequent_items[0]+" -> "+range_sid+" "+supports[0]);System.exit(1); 
            }
            if(rightmost_sid >= nbr_seq){
                System.out.println("bad sid, ...");
                System.exit(1);
            }
            
            final int[][] first_item_stack = create_stack(range_sid+1, left_most_sid, actual_frequent_items, sum_sizes, DEFAULT_STACK_NBR_OP);
            //copier le vecteur de bits de l'item dans la stack... chaque sequence doit etre alignee avec celle des autres items
            //on doit pouvoir supprimer des sequences facilement...
            
            //pour chaque sequence dans laquelle l'item 0 apparait, copier dans sa stack...
            additem_to_stack_at_init(first_item_stack, actual_frequent_items[0], vertical_db, left_most_sid, range_sid, sum_sizes, DEFAULT_STACK_NBR_OP, all_sequences_sum);
            
            System.exit(1);
            
            dfs_stack = new CustomLinkedList_intArr(first_item_stack);
            
            sequence_wise_s_step(dfs_stack, first_item_stack, vertical_db, 2, minsupp, sum_sizes, all_sequences_sum);
            //remove first ???, tour de passe passe qui impacte pas les perfroamnces...
            dfs_stack.removeFirst();
            
            System.out.println("--------------------------------");
            //System.exit(1);
            
            
            //frequent_stacks[0] = first_item_stack;//permet de disposer de toutes les stack au niveau 2...
            
            
            /*while(dfs_stack.size() != 0){
                final int[][] current_stack = dfs_stack.getFirst();
                dfs_stack.removeFirst();
                
                //pour chacun des items dans stack[0][items], 
                //intersecter avec la stack... si frequent, construire sa nouvelle stack avec le resultat.
                //conserver le vecteur...
                //a la fin, oin les les items avec lesquels on reste frequents et les autres sont enleves.
                //donc on construit une stack pour chacun des elements compatibles... et on y ajoute les nouveaux vecteurs...
                //ensuite, on ajoute toutes les nouvelles stacks de facon synchrone...
                //donc chaque stack correspond a un niveau et pas juste un item...
                
                
                sequence_wise_s_step(current_stack, vertical_db, 2, minsupp);
            }*/
        }
        System.out.println(""+two_freq);
        System.exit(1);
        
        //les stack des 1 items frequents vont devenir les bases verticales, plus besoin des autres...
        //va consommer moins de memoire... permet de paritionner...
        
        for(int concept_f=1;concept_f!=nbr_frequent_items;++concept_f){
            final int left_most_sid = sids_by_item[(concept_f << 1)];//trouve son sid le plus petit
            final int rightmost_sid = sids_by_item[(concept_f << 1) + 1];//trouve son plus grand sid
            
            final int range_sid = (rightmost_sid - left_most_sid);//>= support
            if((range_sid+1) < supports[concept_f]) { 
                System.out.println("bad range vs support..."+actual_frequent_items[concept_f]+" -> "+range_sid+" "+supports[concept_f]);System.exit(1); 
            }
            if((left_most_sid + range_sid) >= nbr_seq){
                System.out.println("bad sid, ...");
                System.exit(1);
            }
            final int[][] current_item_stack = create_stack(range_sid+1, left_most_sid, actual_frequent_items, sum_sizes, DEFAULT_STACK_NBR_OP);
            //copier le vecteur de bits de l'item dans la stack... chaque sequence doit etre alignee avec celle des autres items
            //on doit pouvoir supprimer des sequences facilement...
            
            //pour chaque sequence dans laquelle l'item 0 apparait, copier dans sa stack...
            additem_to_stack_at_init(current_item_stack, actual_frequent_items[concept_f], vertical_db, left_most_sid, range_sid, sum_sizes, DEFAULT_STACK_NBR_OP, all_sequences_sum);
            System.out.println("--------------------------------");
            //System.exit(1);
            
            sequence_wise_s_step(dfs_stack, current_item_stack, vertical_db, 2, minsupp, sum_sizes, all_sequences_sum);
            
            
            //frequent_stacks[concept_f] = current_item_stack;//permet de disposer de toutes les stack au niveau 2...
            
            //dfs_stack.add(current_item_stack);//ajoute la stack a la file des choses a faire...
            
            //final CustomLinkedList_intArr dfs_stack;
            //dfs_stack = new CustomLinkedList_intArr(first_item_stack);
            
            //while(dfs_stack.size() != 0){
            //    final int[][] current_stack = dfs_stack.getFirst();
            //    dfs_stack.removeFirst();
                
            //    //pour chacun des items dans stack[0][items], 
            //    //intersecter avec la stack... si frequent, construire sa nouvelle stack avec le resultat.
            //    //conserver le vecteur...
            //    //a la fin, oin les les items avec lesquels on reste frequents et les autres sont enleves.
            //    //donc on construit une stack pour chacun des elements compatibles... et on y ajoute les nouveaux vecteurs...
            //    //ensuite, on ajoute toutes les nouvelles stacks de facon synchrone...
            //    //donc chaque stack correspond a un niveau et pas juste un item...
            //    
            //    
            //    sequence_wise_s_step(current_stack, vertical_db, 2, minsupp);
            //}
        }
        System.out.println("("+nbr_frequent_items+")freq = "+Arrays.toString(actual_frequent_items));
        System.out.println("supports = "+Arrays.toString(supports));
        System.out.println("il reste "+dfs_stack.size()+" etapes dans la stack...");
        
        //on doit prendre le premier item dans la stack, 
        System.out.println("two freq ="+two_freq);
        
    }
    
    static int two_freq = 0;//a supprimer plus tard...
    
    //calculer les supports par sid. tous items vs 1 sid
    public static int sequence_wise_s_step(final CustomLinkedList_intArr global_tasks, final int[][] stack, final int[] vertical_db, final int k, final int minsupp, final int sum_sizes, final int[] all_sequences_sum){
        final int nbr_items = stack[0].length - 5;//5 elements fixes, plus les items...
        final int nbr_sids = stack.length - 1;
        final int[][] sids_by_item = new int[nbr_items][];//extents
        
        final int max_block_length = 2;
        
        final int one_item_length = max_block_length * nbr_sids;
        
        final int first_item = FIRST_ITEM;
        //int offset_mode;

        //pour chaque sequence encore dans la stack
        for(int sid=0;sid!=nbr_sids;++sid){
            if(stack[sid]!=null){
                System.out.println("on passe a sid "+sid+" "+((sid/(float)nbr_sids)*100)+"%");
                //final int[][] current_frequent_extensions = new int[nbr_items][];//ca va dependre du mode choisi, sid-wise ou iid-wise
                /**
                 * pas certains que ca vaille le coup de tout recalcuelr a chaque sid...
                 */
                //final int seq_len = (all_sequences_sum[sid+1] - all_sequences_sum[sid]);
                //final int block_size = 32;
                //final int blocks_len = ((seq_len - (seq_len % block_size)) / block_size) + 1;//+1 a cause du header
                /**
                
                 * fin du recalcul...
                 */
                int offset_mode = sid * max_block_length;
                final int seq_len = (all_sequences_sum[sid+1] - all_sequences_sum[sid]);
                final int block_size = 32;
                final int blocks_len = ((seq_len - (seq_len % block_size)) / block_size) + 1;
                final int begin_ws = threads_ws_start[0] + blocks_len;//tient compte du premier block...
                
                //c'est le workspace du thread ???
                
                //l'autre mode, puisqu'il travaille par item, il peut traiter tous les sid minimaux avant de constuire la nouvelle stack...
                //peut boucler a l'infini dessus par batch...
                
                //mais celui ci necessite de storer les vecteurs de plusieurs items sur plusieurs sids => minsup
                //donc le workspace doit forcement etre bcp plus grand...
                
                //il vaut mieux le faire comme ca, pour prevoir le //isme
                //c pas suffisna tparce que l'on veut tous les resultats, pas juste les derniers pour un itemxsid...
                //faut rajouter une novuelle dimension...
                
               
                
                apply_transformation_for_sstep(stack[sid], k);//applique operateur "triangle-droite"
                //ca veut dire qu'on doit le storer qq part ????
                //ceci est construit dans le workspace...
                
                //int offset_mode = sid * max_block_length;//placement sur la premiere ligne...
                
                //pour chaque sid, pour chaque item, calculer l'intersection
                for(int z=first_item;z!=nbr_items;++z){
                    //System.out.println("tester avec l'item "+stack[0][z]);
                    //travaille directement sur le workspace
                    //{
                        //on devrait le construire avec des + au fur et a mesure...
                        
                        //offset_mode += one_item_length;//((z - FIRST_ITEM) * one_item_length) + (sid * max_block_length);//offset dans le workspace selon le mode, 
                        //pointe vers le sid-ieme vecteur de bits pour l'item z dans le workspace... 
                         
                        //ecrire les sid de facon contigue pour un item
                        //System.out.println("offset is "+offset_mode+" and workspace is "+sequence_workspaces.length);
                        //on a besoin de le faire qu'une seule fois...
                        apply_bitwise_anding_from_vdb(stack[sid], sid, vertical_db, stack[0][z], sum_sizes, all_sequences_sum, offset_mode);//applique le anding...
                        //ici on intersecte le vbit dans le workspace avec la portion de bd verticale..
                        //ca veut dire qu'a chaque fois on doit nettoyer le sequence workspace... ?
                        //, il faut qu'on sequence workspace unique (ex: [0] qui contient le vbit temporaire pre-sstep.
                        //ensuite chaque fois, on intersecte les deux et on place le resultats dans sequence_workspace[1]
                        //on a pas a nettoyer ? si en fait, on doit nettoyer a chaque fois ?
                        //en fait, non, pas besoin de nettoyer si on conserver l'autre separe...
                        
                        //des qu'on a fini, verifie les bits de sequence_workspace[1]
                        //si ok, on update la liste des sid/item ?
                        
                        //comment on passe le nouveau vbit ? copier le resultat ....
                        //
                        
                    //}
                    //verifier si il reste des bits a 1, si oui, support +1 , on avance dans la stack...
                    
                    //si oui, copier le vecteur et le placer dans current_frequent_extensions[item] = new int[nbr_blocs_qui_va_bien];
                    //System.arrayCopy()...
                    
                    //final int pos_check = begin_ws+offset_mode;//(((begin_ws-blocks_len)+offset_mode) % block_size)+1;
                    System.out.println("offset => "+(offset_mode+begin_ws)+" block for "+blocks_len+" blocks");
                    int nbr_blocks = 0;
                    for(int a=begin_ws+offset_mode;a!=begin_ws+offset_mode+blocks_len;++a){
                        System.out.println("check="+tw_bitset.print_block(sequence_workspaces[a]));
                        nbr_blocks += sequence_workspaces[a]!= 0 ? 1 : 0;//on peut mettre un break si !=0
                        //c est un while en fait ??? ou for pour vectoriser ?
                    }
                    
                    //final int nbr_blocks = tw_bitset.blockcount_partial(sequence_workspaces, begin_ws + offset_mode, blocks_len);
                    //System.out.println("still "+nbr_blocks+" valid blocs");
                    if(nbr_blocks!=0){ //compte le nombre de blocs differents de zero
                        //System.out.println("s-step for sid="+sid+" iid="+z+" still has solutions...");
                        //current_frequent_extensions[z - FIRST_ITEM] = res;//copie le resulat temporaire
                        //met a jour la liste des sid /p items
                        {
                            if(sids_by_item[z - first_item]==null){
                                sids_by_item[z - first_item] = new int[nbr_sids]; //diviser par deux ???
                                //on devrait tout laisser, ca cree un tableau de taille du support du parent
                                //meme si 3000 = 3000 int = 12Mo ? on a 32Go, 
                                //puis ca va rapetisser au fur et a mesure...
                            }
                            sids_by_item[z - first_item][0] += 1;
                            sids_by_item[z - first_item][sids_by_item[z - first_item][0]] = sid;//ajoute sid a la fin...
                        }
                        //System.exit(1);
                    }
                    
                    //ici on va devoir creer les stacks pour les items qui matchent au moins une fois, ou completer leur stack si elle existe deja...
                    //l'avantage c'est que ca reduit a chaque fois, une sorte de projection
                    
                    offset_mode += one_item_length;//va au prochain item...
                }
                //sids_by_item[sid] = current_frequent_extensions;
            }
            else{
                //System.out.println(""+sid+" is not a valid sid");
            }
            //offset_mode += max_block_length;
        }
        //a la fin, on cree les stacks des trucs frequents... de facon explicite...
        
        //ici on a calcule tous les supports...
        for(int z=FIRST_ITEM;z!=nbr_items;++z){
            final int[] current_sids = sids_by_item[z-FIRST_ITEM];
            if(current_sids!=null){
                if(current_sids[0] > minsupp){
                    System.out.println(""+stack[0][z]+"-extension is frequent w/ support "+current_sids[0]);

                    //pour chaque sid vlaide, aller chercher les vecteurs dans workspace, les copier dans la stack, ajouter la tache a la stack...
                    
                    //ok
                    //A DECOMMENTER
                    //final int[][] copied_stack = copy_stack(stack, current_sids);
                    //dfs_stack.add(item_stack);
                    two_freq += 1;
                }
                else{
                    System.out.println(""+stack[0][z]+"-extension is not frequent... "+current_sids[0]);
                }
            }
            else{
                System.out.println(""+stack[0][z]+"-extension has not even one match");
            }
        }
        //System.exit(1);
        return 0;
    }
    
    
    
    public static int[] sequence_workspaces;//chaque sequence a son propre workspace... x minsup => comme ca on peut storer toutes les choses en un seul endroit !
    //copier ensuite des que l'on depasse minsup...
    //autant de trucs que de threads actuels...
    //doivent etre bien espaces dans la memoire... important...
    private static int[] threads_ws_start;
    final static int SID_WISE = 0;
    final static int IID_WISE = 1;
    //NOTA : cache line en ko
    public static void generate_threads_workspace(final int mode, final int nbr_items_frequents, final int support_max, final int minsup, final int cache_line_size, final int nbr_threads, final int maximal_sequence_blocks){  
        threads_ws_start = new int[nbr_threads]; 
        //System.out.println("cache line ="+cache_line_size+" and w/ "+nbr_threads+" threads, workspace="+sequence_workspaces.length);
        System.out.println("max seq len="+maximal_sequence_blocks+" max_support="+support_max);
        switch(mode){
            case SID_WISE:
                //construit un vecteur de la taille de la sequence maximale x items frequents x nbr threads
                sequence_workspaces = new int[((maximal_sequence_blocks*(nbr_items_frequents+1)*support_max) + cache_line_size) * nbr_threads];
                for(int i=0;i!=nbr_threads;++i){
                    threads_ws_start[i] = (((maximal_sequence_blocks*(nbr_items_frequents+1)*support_max) + cache_line_size) * i);
                }
                System.out.println("workspace is "+sequence_workspaces.length+"="+maximal_sequence_blocks+"x"+nbr_items_frequents+"(+1)x"+support_max+"(+"+cache_line_size+") x"+nbr_threads);
                break;
            case IID_WISE:
                //construit un vecteur de la taille de la sequence maximale x minsup x nbr threads
                sequence_workspaces = new int[((maximal_sequence_blocks*minsup) + cache_line_size) * nbr_threads];
                for(int i=0;i!=nbr_threads;++i){
                    threads_ws_start[i] = (((maximal_sequence_blocks*minsup) + cache_line_size) * i);
                }
                System.out.println("workspace is "+sequence_workspaces.length+"="+maximal_sequence_blocks+"x"+minsup+"(+"+cache_line_size+") x"+nbr_threads);
                break;
            default:
                System.out.println("cannot create workspace...");
                System.exit(1);
        }
        
    }
    
    //calculer les supports par item. toutes sid par item
    public static int item_wise_s_step(final CustomLinkedList_intArr global_tasks, final int[][] stack, final int[] vertical_db, final int k, final int minsupp){
        final int nbr_items = stack[0][ACTUAL_ITEM_HEADER_SIZE];//nombre d'items locaux dans la stack
                
        final int nbr_sids = stack.length - 1;
        final int[][] sids_by_item = new int[nbr_items][];//extents
        
        //applique les s-step ops sur chaque sid de la stack...(pas besoin de le faire a toutes les fois) 
        for(int sid=0;sid!=nbr_sids;++sid){
            if(stack[sid]!=null){
                apply_transformation_for_sstep(stack[sid], 2);//applique operateur "triangle-droite"
                //ceci va ajouter un nouvelle case a la stack... (nouvel operateur...)
            }
        }
        
        int left_most_item = 0;
        int right_most_item = ACTUAL_ITEM_HEADER_SIZE;//PAS SUR DE CA...
        int still_frequent_items = nbr_items;
        for(int z=FIRST_ITEM;z!=nbr_items;++z){
            //uniquement si option 1 (pas de nettoyage)
            {
                if(stack[0][z]==0) continue;//suffisant, rien a modifier...
            }
            final int item_courrant = stack[0][z];
            //pour chaque sequence encore dans la stack
            int support_local = 0;
            final int[][] current_frequent_extensions = new int[nbr_sids][];
            
            sids_by_item[z - FIRST_ITEM] = new int[nbr_sids];
            final int[] local_sids = sids_by_item[z-FIRST_ITEM];
            
            for(int sid=0;sid!=nbr_sids;++sid){
                if(stack[sid]!=null){
                    //apply_bitwise_anding(stack[sid], vertical_db, item_courrant);//applique le anding...
                    /*if(tw_bitset.blockcount_partial(res, 0, 1000)!=0){ //compte le nombre de blocs differents de zero
                        System.out.println("s-step for sid="+sid+" still has solutions...");
                        current_frequent_extensions[support_local] = res;//copie le resulat temporaire
                        //met a jour la liste des sid /p items
                        {
                            local_sids[0] += 1;
                            local_sids[local_sids[0]] = sid;//ajoute sid a la fin...
                        }
                    }*/
                }
                else{
                    System.out.println(""+sid+" is not a valid sid");
                }
            }
            if(support_local > minsupp){
                //frequent...
                
                if(left_most_item == 0) left_most_item = z;
                
                //cree la nouvelle stack...
                
                final int[][] copied_stack = copy_stack(stack, local_sids, still_frequent_items, left_most_item, right_most_item, stack[0]);//indiquer leftmost et rightmost
                //supprimer les sid inutiles, 
                //copier les resultats dans current_frequent_extensions pour les sid_valides ?
                int valid_sid_pos = 1;
                while(valid_sid_pos < local_sids[0]){
                    stack[local_sids[valid_sid_pos] + 1] = current_frequent_extensions[valid_sid_pos];//copie le resultat pour le bon sid...
                    //important, ne pas oublier le +1  a cause que 0 = header...
                }
                //ici la stack est finie pour le nouvel item, on peut submit dans la pile...
                
                //je suppose qu'il faut dev une pile locale dont la ref va etre renvoyee, et va etre chainee a celle connue dans main...
                //le tout en synchronised (tester l'impact)...
            }
            else{
                still_frequent_items -= 1;
                sids_by_item[z - FIRST_ITEM] = null;//nettoye memoire
                stack[0][z] = 0;//si zero alors ignorer...
                //left_most_item = 1;
            }
        }
        //il faut aussi nettoyer toutes les stacks avec les infrequents...
        return 0;
    }
    
    public static void apply_transformation_for_sstep(final int[] stack, final int k){
        //update le dernier vecteur de la stack pour la s-step
        int DEFAULT_STACK_NBR_OP = 10;
        int blocksize = 32;
        //
        //System.out.println("----------------------- transformation s-step --------------");
        //ca va rester comme ca pour l'instant...
        int seq_len = stack.length / DEFAULT_STACK_NBR_OP;
        
        int nbr_blocks = ((seq_len - (seq_len % blocksize)) / blocksize) + 1;
        
        final int begin_ws = threads_ws_start[0];//on recupere l'offset du workspace du premier thread...
        //on decale en plus ???
        
        final int first_block = 1;//premier bloc dans la stack qu'on va devoir lire, depend du k
        //pas certain que ce soit zero a cause du twbitset ... (a verifier)
        
        
        //bloc vide, qq ca veut dire ???
        //ca veut dire que ce sid devrait pas etre dans la liste...
        //ca veut dire qu'il y a un probleme dans le calcul des sids
        
        System.arraycopy(stack, first_block, sequence_workspaces, begin_ws, nbr_blocks);//on copie le dernier etat dans le workspace
        //System.out.println("begining at "+begin_ws+" for thread #0, copying from "+first_block+" + "+nbr_blocks);
        //System.out.println("nbr blocks = "+nbr_blocks);
        byte found = 0;
        //pour chaque bloc, on applique la transformation...
        for(int i=0;i!=nbr_blocks;++i){
            if(found==0 && sequence_workspaces[begin_ws+i]!=0){
                //la valeur est ici...
                found = 1;
                //divise le bitset
                //System.out.println("found..");
                //System.out.println(">>"+tw_bitset.print_block(sequence_workspaces[begin_ws+i]));
                int lowestOneBit = Integer.numberOfTrailingZeros(sequence_workspaces[begin_ws+i]);
                //System.out.println("first bit is "+lowestOneBit);
                int nouveau_block = (-1 << ((lowestOneBit+1)));
                if(nouveau_block == -1) nouveau_block = 0;
                //System.out.println(">>"+tw_bitset.print_block(nouveau_block));
                sequence_workspaces[begin_ws+i] = nouveau_block;
            }
            else if(found!=0){
                sequence_workspaces[begin_ws+i] = -1;
                //System.out.println("applying all ones...");
            }
            else{
                //System.out.println("block is empty...");
            }
            //System.out.println("block no "+i+"("+(first_block+i)+"),"+tw_bitset.print_block(sequence_workspaces[begin_ws+i]));
        }
        //trouver la plus petite position avec un bit a 1...
        //peut etre utilise de conserver cette info dans le bloc meta du tw bitset ???
        
        //System.out.println("----------------------- fin transformation s-step --------------");
    }
    
    
    public static void apply_bitwise_anding_from_istacks(final int[] stack, final int[] item_stack, final int item){
        //aller dans stack
        
        //pour chaque bloc, ander...
        
        //et c tout...
        
    }
    
    public static void apply_bitwise_anding_from_vdb(final int[] stack, final int sid, final int[] vertical_db, final int item, final int sum_sizes, final int[] all_sequences_sum, final int offset_mode){
        
        final int offset_bits = (sum_sizes * item) + all_sequences_sum[sid];
        final int seq_len = (all_sequences_sum[sid+1] - all_sequences_sum[sid]);
        //System.out.println("sid is "+sid);

        //System.out.println("offset "+offset_bits);
        //System.out.println("seq len "+seq_len);
        //System.out.println("sid "+(sid)+" is not empty");

        //on doit aller chercher dans la base verticale de item/sid, puis ander chaque bloc avec celui dans la stack...
        

        final int block_size = 32;
        final int block_len = ((seq_len - (seq_len % block_size)) / block_size) + 1;//+1 a cause du header

        final int begin_pos = (offset_bits % block_size);
        final int block_to_alter = ((offset_bits - begin_pos) / block_size) + 1;//le numero du block ou commencer
        final int nbr_other_blocks = (((begin_pos + seq_len) - ((begin_pos + seq_len) % block_size)) / block_size) + block_to_alter + 1;//le nombre de blocks a intersecter...
        final int end_pos = (offset_bits + seq_len) % block_size;//calcule la position de fin dans le dernier block    

        //int seq_len = stack.length / DEFAULT_STACK_NBR_OP;
        
        //int nbr_blocks = (seq_len / block_size) + 1;
        
        final int begin_ws = threads_ws_start[0] + offset_mode + block_len;//debut en comptant l'offset du au mode, iid et sid, + first seq qui sert a storer les s-step-transformations...
        //final int first_block = 0;
        
        
        //si plusieurs blocs
        if((nbr_other_blocks - block_to_alter) != 1){
            //System.out.println("plusieurs blocs a copier..."+begin_pos+" and "+end_pos+" , "+block_to_alter+", "+nbr_other_blocks);
            //System.out.println("new len = "+new_len);
            int retenue = 0;
            //savoir si le premier bloc commence pas a zero...
            if(begin_pos!=0){
                //stack[1] &= (-vertical_db[block_to_alter]) >> begin_pos;//ca devrait ajouter des 1 plutot que des zeros...
                sequence_workspaces[begin_ws] = sequence_workspaces[begin_ws - block_len] & (vertical_db[block_to_alter] >>> begin_pos);
                retenue = begin_pos;
            }
            else{
                sequence_workspaces[begin_ws] =  sequence_workspaces[begin_ws - block_len] & vertical_db[block_to_alter];
            }
            //normalement, on peut enlever le if..
            
            //tous les blocs au milieu
            //System.out.println("retenue is "+retenue);
            int cursor = 1;
            for(int i=(block_to_alter+1);i!=(nbr_other_blocks-1);++i){
                final int tmp = vertical_db[i];
                //stack[cursor] = vertical_db[i];
                if(retenue != 0){
                    sequence_workspaces[begin_ws+(cursor - 1)] = sequence_workspaces[begin_ws + (cursor - 1)] | (tmp << retenue);//cursor - 1 est rempli de 1s a la fin..
                    //tout ce que ca fait, c'est placer les bits dans l'autre vecteur 
                    sequence_workspaces[begin_ws+cursor]  = sequence_workspaces[(begin_ws-block_len) + cursor] & (tmp >> retenue);
                    //System.out.println("vdb="+tw_bitset.print_block(vertical_db[i]));
                    //System.out.println("-1"+tw_bitset.print_block(stack[cursor-1]));
                    //System.out.println("cur="+tw_bitset.print_block(stack[cursor]));
                }
                else sequence_workspaces[begin_ws+cursor] = sequence_workspaces[(begin_ws - block_len) + cursor] & tmp;
                cursor += 1;
            }
            //dernier block
            if(end_pos!=(block_size-1)){
                final int right_mask = ~(-1 << end_pos);
                //stack[new_len] = vertical_db[nbr_other_blocks-1] & right_mask;
                final int tmp = vertical_db[nbr_other_blocks-1] & right_mask;
                if(retenue!=0){
                    //decaler aussi...
                    //System.out.println("*-1("+new_len+")="+tw_bitset.print_block(stack[new_len - 1]));
                    //stack[(sid-lo)+1][new_len] = 51544561;
                    //System.out.println("*2mask="+tw_bitset.print_block((stack[new_len] << retenue)));
                    sequence_workspaces[begin_ws+(block_len - 1)] = sequence_workspaces[begin_ws+(block_len - 1)] & (tmp << retenue);//prend le precedent et applique un OU
                    sequence_workspaces[begin_ws+(block_len)] = sequence_workspaces[begin_ws-1] & (tmp >> retenue);//prend le referend et l'applique au nouveau
                }
                else{
                    sequence_workspaces[begin_ws+(block_len)] = sequence_workspaces[begin_ws-1] & tmp;//juste appliquer la transformation avec masque...
                }
            }
            else{
                //stack[new_len] = vertical_db[nbr_other_blocks-1];//<<retenue
                final int tmp = vertical_db[nbr_other_blocks-1];//<<retenue
                if(retenue != 0){
                    //prendre les x premiers bits, les deplacer dans le vecteur predecent, et decaler tout de x
                    sequence_workspaces[begin_ws+(block_len - 1)] = sequence_workspaces[begin_ws+(block_len - 1)] | (tmp >>> (block_size-retenue));
                    sequence_workspaces[begin_ws+(block_len)] = sequence_workspaces[begin_ws+(block_len)] & (tmp >>> retenue);
                    //System.out.println("*vdb="+tw_bitset.print_block(vertical_db[nbr_other_blocks-1]));
                    //System.out.println("*cur="+tw_bitset.print_block(stack[1]));
                }
                else{
                    sequence_workspaces[begin_ws+block_len] = sequence_workspaces[begin_ws-1] & tmp;//applique la transformation si deja alignes...
                }
            }
        }
        //si un seul block
        else{
            
            final int right_mask = ~(-1 << end_pos);
            sequence_workspaces[begin_ws] = sequence_workspaces[begin_ws-block_len] & ((vertical_db[block_to_alter] & right_mask) >>> begin_pos);//decaler de begin_pos pour l'aligner avec debut... 
            
            if(sequence_workspaces[begin_ws-block_len]!=0){
                System.out.println("un bloc a copier..."+begin_pos+" and "+end_pos);
                System.out.println("right="+tw_bitset.print_block(right_mask));
                System.out.println("vdb="+tw_bitset.print_block(vertical_db[block_to_alter]));
                System.out.println("vdb(&&)="+tw_bitset.print_block(((vertical_db[block_to_alter] & right_mask))));
                System.out.println("vdb(>>>)="+tw_bitset.print_block(((vertical_db[block_to_alter] & right_mask) >>> begin_pos)));
                System.out.println("cur_ori="+tw_bitset.print_block(sequence_workspaces[begin_ws-block_len]));
                System.out.println("cur="+tw_bitset.print_block(sequence_workspaces[begin_ws]));
            }
            //if(sequence_workspaces[begin_ws]!=0)System.exit(1);
            
        }
        //}
    //}
        
    }
    
    public static int[] expandArray(final int[] a){
        return null;
    }
    
    public static void ontopattern_spam(){
        //construire la representation verticale des classes et props
        
        //trouver les classes frequentes
        
        //trouver les props frequentes
        
        //
        
        
    }
    
    final static int ACTUAL_ITEM_HEADER_SIZE = 0;
    final static int LEFT_OFFSET = 1;
    final static int PARENT_ID = 2;
    final static int LAST_OP = 3;
    final static int FIRST_ITEM = 4;
    
    public static int[][] create_stack(final int size, final int from, final int[] frequent_items, int sum_sizes, int DEFAULT_STACK_NBR_OP){
        final int[][] stack = new int[size+1][];
        System.out.println("created a stack of size "+(size)+"+1");
        
        stack[0] = new int[5 + frequent_items.length];//header
        stack[0][LEFT_OFFSET] = from;
        stack[0][ACTUAL_ITEM_HEADER_SIZE] = frequent_items.length;
        
        System.out.println("leftmost is "+from);
        System.out.println("actual item header size "+frequent_items.length);
        
        System.arraycopy(frequent_items, 0, stack[0], FIRST_ITEM, frequent_items.length);//tous les items frequents a la suite...
        //faudra faire la mm chose avec les properties... soit sous forme d'id contigus aux concepts soit autre chose...
        return stack;
    }
    
    public static int[][] copy_stack(final int[][] stack_to_copy, final int[] sids_to_copy, final int nbr_items_still_freq, final int leftmost, final int rightmost, 
            final int[] items_still_frequent){
        
        final int[][] stack = new int[sids_to_copy[0]+1][];
        System.out.println("created a stack of size "+(sids_to_copy[0])+"+1");
        //stack[0] = new int[stack_to_copy[0].length];//header (option 1)
        stack[0] = new int[5 + nbr_items_still_freq];//header (option 2)
        stack[0][LEFT_OFFSET] = leftmost;
        stack[0][ACTUAL_ITEM_HEADER_SIZE] = nbr_items_still_freq;
        System.out.println("leftmost is "+leftmost);
        System.out.println("actual item header size "+nbr_items_still_freq);
        //option 1 - copie tout, meme les zeros
        /*{
            System.arraycopy(items_still_frequent, 0, stack[0], FIRST_ITEM, nbr_items_still_freq);//tous les items frequents a la suite...
        }*/
        //option 2 - nettoye
        {
            int cursor = FIRST_ITEM;
            for(int i=FIRST_ITEM;i!=stack_to_copy[0].length;++i){
                if(stack_to_copy[0][i] !=0){
                    stack[0][cursor] = stack_to_copy[0][i];
                    cursor += 1;
                }
            }
        }
        
        for(int i=1, cursor=1;i!=sids_to_copy[0];++i){
            final int[] t = stack_to_copy[sids_to_copy[i]];
            final int l = t.length;
            stack[cursor] = new int[l];//pour l'instant on etend pas... on garde la taille par defaut
            System.arraycopy(t, 0, stack[cursor], 0, l);
            cursor += 1;
        }
        return stack;
    }
    
    
    public static void additem_to_stack_at_init(final int[][] stack, final int iid, final int[] vbd, final int from_sid, final int len_sid, 
        final int sum_sizes, final int DEFAULT_STACK_NBR_OP, final int[] all_sequences_sum){
        //copier le vbit de l'item indique dans la sequence indique
        //le placer dans la bonne sid
        final int[] header = stack[0];
        final int lo = header[LEFT_OFFSET];
        
        System.out.println(""+Arrays.toString(header));
        System.out.println("lo is "+lo);
        System.out.println("from sid "+from_sid);
        System.out.println("length sid "+len_sid);
        for(int sid=from_sid;sid!=(from_sid+len_sid+1);++sid){
            //Verifier que la place existe...sinon etendre
            System.out.println("################# "+iid+" & "+sid+"("+from_sid+"->"+(from_sid+len_sid)+") ######################");
            final int offset_bits = (sum_sizes * iid) + all_sequences_sum[sid];
            final int seq_len = (all_sequences_sum[sid+1] - all_sequences_sum[sid]);
            System.out.println("sid is "+sid);
            
            System.out.println("offset "+offset_bits);
            System.out.println("seq len "+seq_len);
            //si la pile de l'item est vide..
            final int nbr_blocks = tw_bitset.blockcount_partial(vbd, offset_bits, seq_len);
            System.out.println("on a "+nbr_blocks+" blocs valides pour "+sid);
            if(nbr_blocks==0){
                if(sid == from_sid){
                    System.out.println("cannot be empty since it is first sid....");
                    System.exit(1);
                }
            //if(stack[(sid-lo)+1] == null){
                System.out.println("sid "+(sid-lo)+" is empty...skippiing..."+Arrays.toString(stack[(sid-lo)+1] ));
                //stack[(sid-lo)+1] = tw_bitset.int_nouveau_bitset((seq_len * DEFAULT_STACK_NBR_OP), 0);//on l'intialise a 10 operations sur la sequence par defaut
            }
            else{
                System.out.println("sid "+(sid-lo)+" is not empty");
                stack[(sid-lo)+1] = tw_bitset.int_nouveau_bitset((seq_len * DEFAULT_STACK_NBR_OP), 0);//on l'intialise a 10 operations sur la sequence par defaut
                //copier l'item dans le bon sid
                
                //tw_bitset.get_bit_at(offset_bits + z, vertical_db) ? 1 : 0; 
                //copier de offset_bits jusqu'a seq_len
                //on va perdre maximum 1 int... (ce qui reste de chaque vecteur... necessaire pour aligner les trucs...)

                //la sequence a pour longueur x. donc on va placer les trucs aux multilples de x, selon les debut des ints...
                //les props pareil... par contre on va storer le petit vecteur mais le grand (celui complet apres calcul)
                
                
                final int block_size = 32;
                final int new_len = ((seq_len - (seq_len % block_size)) / block_size) + 1 + 1;//+1 a cause du header du twbitset
                
                final int begin_pos = (offset_bits % block_size);
                final int block_to_alter = ((offset_bits - begin_pos) / block_size) + 1;//le numero du block ou commencer
                final int nbr_other_blocks = (((begin_pos + seq_len) - ((begin_pos + seq_len) % block_size)) / block_size) + block_to_alter + 1;//le nombre de blocks a intersecter...
                final int end_pos = (offset_bits + seq_len) % block_size;//calcule la position de fin dans le dernier block    

                //si plusieurs blocs
                if((nbr_other_blocks - block_to_alter) != 1){
                    System.out.println("plusieurs blocs a copier...bp="+begin_pos+" and ep="+end_pos+" , firstblk="+block_to_alter+", lstblk="+nbr_other_blocks);
                    System.out.println("new len = "+new_len);
                    int retenue = 0;
                    //savoir si le premier bloc commence pas a zero...
                    if(begin_pos!=0){
                        stack[(sid-lo)+1][1] = vbd[block_to_alter] >>> begin_pos;
                        retenue = begin_pos;
                        System.out.println("vdb="+tw_bitset.print_block(vbd[block_to_alter]));
                        System.out.println("cur="+tw_bitset.print_block(stack[(sid-lo)+1][1]));
                    }
                    else{
                        stack[(sid-lo)+1][1] = vbd[block_to_alter];
                    }
                    //tous les blocs au milieu
                    System.out.println("retenue is "+retenue);
                    int cursor = 1;
                    for(int i=(block_to_alter+1);i!=(nbr_other_blocks-1);++i){
                        stack[(sid-lo)+1][cursor] = vbd[i];
                        if(retenue != 0){
                            stack[(sid-lo)+1][cursor - 1] |= (stack[(sid-lo)+1][cursor] << retenue);
                            stack[(sid-lo)+1][cursor]  = stack[(sid-lo)+1][cursor] >> retenue;
                            System.out.println("vdb="+tw_bitset.print_block(vbd[i]));
                            System.out.println("-1"+tw_bitset.print_block(stack[(sid-lo)+1][cursor-1]));
                            System.out.println("cur="+tw_bitset.print_block(stack[(sid-lo)+1][cursor]));
                        }
                        cursor += 1;
                    }
                    //dernier block
                    if(end_pos!=(block_size-1)){
                        final int right_mask = ~(-1 << end_pos);
                        stack[(sid-lo)+1][new_len] = vbd[nbr_other_blocks-1] & right_mask;
                        if(retenue!=0){
                            //decaler aussi...
                            System.out.println("*-1("+new_len+")="+tw_bitset.print_block(stack[(sid-lo)+1][new_len - 1]));
                            
                            //stack[(sid-lo)+1][new_len] = 51544561;
                            
                            System.out.println("*2mask="+tw_bitset.print_block((stack[(sid-lo)+1][new_len] << retenue)));

                            stack[(sid-lo)+1][new_len - 1] |= (stack[(sid-lo)+1][new_len] << retenue);
                            stack[(sid-lo)+1][new_len]  = stack[(sid-lo)+1][new_len] >> retenue;
                            
                            System.out.println("*vdb="+tw_bitset.print_block(vbd[nbr_other_blocks-1]));
                            System.out.println("*-1="+tw_bitset.print_block(stack[(sid-lo)+1][new_len - 1]));
                            System.out.println("*cur="+tw_bitset.print_block(stack[(sid-lo)+1][new_len]));
                        }
                    }
                    else{
                        stack[(sid-lo)+1][new_len] = vbd[nbr_other_blocks-1];//<<retenue
                        if(retenue != 0){
                            //prendre les x premiers bits, les deplacer dans le vecteur predecent, et decaler tout de x
                            stack[(sid-lo)+1][new_len - 1] |= (stack[(sid-lo)+1][new_len] >>> (block_size-retenue));
                            stack[(sid-lo)+1][new_len]  = stack[(sid-lo)+1][new_len] >>> retenue;
                            System.out.println("*vdb="+tw_bitset.print_block(vbd[nbr_other_blocks-1]));
                            System.out.println("*cur="+tw_bitset.print_block(stack[(sid-lo)+1][1]));
                        }
                    }
                }
                //si un seul block
                else{
                    System.out.println("un bloc a copier..."+begin_pos+" and "+end_pos);
                    int right_mask = ~(-1 << end_pos);
                    stack[(sid-lo)+1][1] = (vbd[block_to_alter] & right_mask) >>> begin_pos;//decaler de begin_pos pour l'aligner avec debut...
                    System.out.println("right="+tw_bitset.print_block(right_mask));
                    System.out.println("vdb="+tw_bitset.print_block(vbd[block_to_alter]));
                    System.out.println("cur="+tw_bitset.print_block(stack[(sid-lo)+1][1]));
                }
            }
        }
    }
    
    public static void ontopattern_lapin(){
        
    }
    
    /*public static void ontopattern_prism(){
        
    }*/
    
    public static void ontopattern_paid(){
        
    }
    
    public static void transform_s_tep_vbit(final int[] blocks){
        
    }
    
    public static void intersect_vbit(final int[] a, final int[] b){

    }
    
    public static int[] allocate_new_page(){
        return new int[4];
    }
    
    
    
}
