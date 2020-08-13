
package ontologyrep20;


import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Enridestroy
 * On va utiliser les arbres radix comme contenants des concepts et des relations et des instances.
 * Cela va nous permettre de reduire la memoire.
 * 
 * Comment iterer sur un arbre radix (depth first, bread first)?
 * Les arbres des suffixes vont contenir la hierarchie 
 * 
 * Pour passer du radix au suffix tree on fait:
 * Concept c = RadixTree.get(name);
 * Concept c = SuffixTree.get(path) avec path -> int[] {17, 15, 13, ...};
 * ou SuffixTree.get(item) avec item = int. Est ce judicieux que la racine soit une hashtable ? accelere la recherche.
 * 
 * //parent:
 * SuffixTree.getParent(Concept.index)
 * //enfants:
 * SuffixTree.getChildren(Concept.index) ou  Concept.getSons()
 * //top
 * SuffixTree.getTopParent(Concept.index) ou Concept.getTopParent()
 * 
 * //relation
 * RadixTree.get(name)
 * SuffixTree.get(Relation.index)
 * //relations d'un concept
 * 
 * //domaine et codomaine d'une relation
 * 
 * 
 * c1 = createConcept();
 * c2 = createConcept();
 * r = createRelation();
 * 
 * t = createTriplet(c1,c2,r);
 * 
 * c3 = createConcept();
 * 
 * extendConcept(c1, c3);
 * 
 * c3.getParent();
 * 
 * c1.getChildren();
 * 
 * c1.getRelatedTriplets();//en tant que domaine ou codomaine, = liste de concepts
 * 
 * c3.getRelations();//liste de relations impliquant c3 en tant que domaine ou codomaine
 * 
 * c1.p_name.equals(c3.p_name)
 * //methode equals des radixtreenode?
 * 
 * //on compare le nom du concept
 * if(radixnode.id == radixnode2.id){
 *  return true //les chaines sont egales!!!
 * }
 * else{
 *  return false; 
 * }
 * 
 * //on compare la hierarchie du concept
 * if(suffixnode.id == suffixnode2.id){
 *  return true;//les index sont egaux
 * }
 * else{
 *  return false;
 * }
 *
 * //on va comparer le nombre de proprietes du concept
 * 
 * 
 * //on va comparer chaque propriete selon les memes techniques, nom, index, hierarchie
 * 
 * 
 * DEPRECATED!!!!
 * if(node.index.length==node2.index.length){
 *  int i = node.index.length-1;
 *  while(index[i]==index2[i]&&i>=0){
 *      --i;
 *  }
 *  if(i inf 0){
 *      //matching
 *      RadixNode p = node.parent;
 *      while(p!=null&&){
 * 
 *          p = p.parent;
 *      }
 *  }
 *  else{
 *      //return pas matching
 *  }
 * }
 * else{
 *  return false;
 * }
 * 
 * 
 * 
 * Finir la representation:
 * -ajouter des elements chaines dans le radix Tree.
 * -creer une nouvelle classe, le LinkedRadixTree ? ou ajouter cette feature directement avec un parametre ?
 * -optimiser les bucket des radix tree (b-tree?)
 * -faire un hat-tree
 * 
 */
public class OntoRepresentation implements Cloneable{
    
    //index sur les elements de l'ontologie par noms
    public RadixTree<Concept> index_concepts_by_name = new RadixTree(RadixTree.SIMPLE_NODES);
    public RadixTree<Relation> index_relations_by_name = new RadixTree(RadixTree.SIMPLE_NODES);
    public RadixTree<Instance> index_instances_by_name = new RadixTree(RadixTree.SIMPLE_NODES);
    
    //index sur les hierarchies
    public SuffixTree index_concepts_by_taxonomy = new SuffixTree();
    public SuffixTree index_relations_by_taxonomy = new SuffixTree();
    
    //Listes qui contiennent tous les elements
    public LinkedList<Concept> concepts = new LinkedList<>();//--> liste chainees
    public LinkedList<Relation> relations = new LinkedList<>();
    //public LinkedList<Instance> instances = null;
    public ArrayList<Instance> instances = new ArrayList<>();//changer
    //public ArrayList<Triplet> triplets = new ArrayList<>();
    
    
    //index
    public Object[] index_concepts_by_id = new Object[17];
    public Object[] index_relations_by_id = new Object[17];
    public Object[] index_triplets = new Object[17];
    
    //contient tous les triplets
    public ArrayList<Triplet> triplets = new ArrayList<>();
    public ArrayList<Triplet> triplets_inv = new ArrayList<>();
    
    public final static boolean RELATION_TRUE = true;
    public final static boolean RELATION_FALSE = false;

    public ArrayList<ArrayList<Concept>> conceptsByLevel = new ArrayList<>();

    //contient les index sur les triplets
    //Liste chainee dans le tableau ? ArrayList ? Hashtable ?
    //La liste chainee permet d'ajouter des liens vers les proprietes heritees facilement.
    //L'arraylist aussi
    //Je pense utiliser des arraylist ? ou des B-tree ?
    //Pourquoi on a besoin de toutes ces proprietes ?
    //Pour iterer dessus ? ou pour recuperer un item en particulier ?
    //A -> linkedlist
    //B -> arraylist
    //public Triplet[][] by_c = new Triplet[50][];//par codomaine
    //public Triplet[][] by_d = new Triplet[50][];//par domaine
    //public Triplet[][] by_r = new Triplet[50][];//par relation
    
    public RadixTree<Triplet> by_c = new RadixTree(RadixTree.LINKED_NODES);//par codomaine
    public RadixTree<Triplet> by_d = new RadixTree(RadixTree.LINKED_NODES);//par domaine
    public RadixTree<Triplet> by_r = new RadixTree(RadixTree.LINKED_NODES);//par relation
    
    public RadixTree<TripletInst> by_d_by_inst = new RadixTree(RadixTree.LINKED_NODES);//par domaine
    
    //liste de references vers les concepts et relations dont le parent est null -- ELEMENTS RACINES --
    public ArrayList<Concept> root_concepts = new ArrayList<>();
    public ArrayList<Relation> root_relations = new ArrayList<>();
    /**
     * On peut mettre le support en byte plutot que double.
     * On fait *10 sur des doubles tronques.
     * ex: 0.7 -> 70.
     * 
     * Tester un tableau segmente ?
     * 
     */
    
    //matrice des ancetres et descendants
    public AncestorMatrix ancestor_matrix_c = new AncestorMatrix();
    public AncestorMatrix ancestor_matrix_r = new AncestorMatrix();
    
    public OntoRepresentation(){
        //constructeur vide...
        //this.by_c[0] = new ArrayList<Triplet>();
    }
    
    /**
     * Cherche un concept dans l'arbre des suffixes
     * @param index
     * @return 
     */
    public Concept getConcept(final Integer index){
        ArrayList<Integer> temp = new ArrayList<>();
        temp.add(index);
        
        SuffixTree.Node o = this.index_concepts_by_taxonomy.get(temp);
        if(o!=null){
          return (Concept)o.value;  
        }
        else{
            return null;
        }
    }
    
    
    /**
     * 
     * @param index
     * @return 
     */
    public Relation getRelation(final Integer index){
        ArrayList<Integer> temp = new ArrayList<>();
        temp.add(index);
        
        SuffixTree.Node o = this.index_relations_by_taxonomy.get(temp);
        if(o!=null){
          return (Relation)o.value;  
        }
        else{
            return null;
        }
    }


    public Relation getRelationByName(final String _a, final String _b){
        return (Relation)this.index_relations_by_name.get(_a);
    }
    
    /**
     * 
     * @param c
     * @return 
     */
    public boolean containsConcept(final Concept c){
        //return this.index_concepts_by_taxonomy.treeContainsKey((char)c.index);
        //ou
        return this.ancestor_matrix_c.matrix.containsKey((char)c.index);
    }
    
    /**
     * 
     * @param r
     * @return 
     */
    public boolean containsRelation(final Relation r){
        return false;
    }
    
    /**
     * Renvoie les concepts racines de l'ontologie
     * @return 
     */
    public ArrayList<Concept> getRootConcepts(){
        return this.root_concepts;
    }

    public void addConceptByLevel(Concept c, int level){
        if(this.conceptsByLevel.size() <= level){
            ArrayList<Concept> new_level = new ArrayList<>();
            new_level.add(c);
            while(this.conceptsByLevel.size() < level){
                this.conceptsByLevel.add(null);
            }
            this.conceptsByLevel.add(level, new_level);
        }
        else{
            ArrayList<Concept> get = this.conceptsByLevel.get(level);
            if(get == null){
                ArrayList<Concept> new_level = new ArrayList<>();
                new_level.add(c);
                this.conceptsByLevel.add(level, new_level);
            }
            else{
                this.conceptsByLevel.get(level).add(c);
            }
        }
    }

    /**
     * Ajoute un nouveeau concept a l'ontologie
     * @param name
     * @param c
     */
    public void addConcept(final String name, final Concept c){
        //System.out.println("on ajoute le concept...");
        //Ajout dans l'index par nom
        //final RadixNode p_name =;
        c.p_name = this.index_concepts_by_name.addString(name, c);
        //Ajout dans l'index de la hierarchie des concepts
        ArrayList<Integer> _indexes = new ArrayList<>();//utiliser des tableaux plutot que des arraylist
        _indexes.add(c.index);
        
        //_indexes.addAll(Arrays.asList(c.char_index));
        
        System.out.println("ADDING CONCEPT:"+c.index);
        System.out.println("name="+c.getName());
        try {
            this.index_concepts_by_taxonomy.addNumericIndexes(_indexes, c);
            System.out.println("concept ajoute");
            c.p_index = this.index_concepts_by_taxonomy.suffix_link;//ceci est un test!!!!
            //System.out.println("AAAAAA:"+c.getIndex());
        } catch (Exception ex) {
            Logger.getLogger(OntoRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Parents d'un concept
     * @param c
     * @return 
     */
    public ArrayList<Concept> getParents(final Concept c){
        final HashSet<Character> ancestors = this.ancestor_matrix_c.getAncestors((char) c.index);
        final ArrayList<Concept> ancestorsRefs = new ArrayList<>();
        if(null != ancestors) {
            for (final Character cc : ancestors) {
                ancestorsRefs.add(this.getConcept(Integer.valueOf(cc)));
            }
        }
        return ancestorsRefs;
        //return this.index_concepts_by_taxonomy.getParentValues(c.p_index);
    }

    public <T extends OntologyItem> ArrayList<T> getParents(final T r){
        if(r.getClass().isAssignableFrom(Concept.class)){
            return (ArrayList<T>) this.getParents((Concept)r);
        }
        else if(r.getClass().isAssignableFrom(Relation.class)){
            return (ArrayList<T>) this.getParents((Relation) r);
        }
        else{
            return null;
        }
    }

    public ArrayList<Relation> getParents(final Relation r){
        final HashSet<Character> ancestors = this.ancestor_matrix_r.getAncestors((char) r.index);
        final ArrayList<Relation> ancestorsRefs = new ArrayList<>();
        if(null != ancestors) {
            for (final Character cc : ancestors) {
                ancestorsRefs.add(this.getRelation(Integer.valueOf(cc)));
            }
        }
        return ancestorsRefs;
        //return this.index_relations_by_taxonomy.getParentValues(r.p_index);
    }

    //ceci doit renvoyer t/f si root prop ou concept
    public boolean isRootNode(final OntologyItem _item){
        final ArrayList<OntologyItem> parents = this.getParents(_item);
        return parents == null || parents.isEmpty();
    }

    public void removeChildNode(final OntologyItem _item, final OntologyItem _parent){
        //delete _item de son _parent
        if(_item.getClass() != _parent.getClass()) return;

        if(_item.getClass().isAssignableFrom(Concept.class)){
            final Concept item = (Concept) _item;
            final Concept parent = (Concept) _parent;
            try {
                this.ancestor_matrix_c.removeAncestor((char) item.index, (char) parent.index);
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        else if(_item.getClass().isAssignableFrom(Relation.class)){
            final Relation item = (Relation) _item;
            final Relation parent = (Relation) _parent;
            try {
                this.ancestor_matrix_r.removeAncestor((char) item.index, (char) parent.index);
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
    }

    /**
     * Enfants d'un concept
     * Trouver un moyen d'accelerer le processus.
     * Remplacer la liste chainee par une liste ou alors regarder ce que l'on fait avec et si on ne fait qu'iterer, on peut se
     * contenter de renvoyer le premier fils puis apres on accede aux autres sequentiellement
     * Autre solution, est d'implementer une methode contains() ou l'on va evaluer les fils directement dans le suffixnode
     * @param c
     * @return 
     */
    public ArrayList<Concept> getSons(final Concept c){
        return this.index_concepts_by_taxonomy.getSonsValues(c.p_index);
    }
    
    /**
     * Renvoie les relations semantiques ou pas d'un concept.
     * Doit aussi renvoyer les relations heritees du concept?
     * @param c
     * @return 
     */
    public AnchoredLinkedList<Triplet> getRelations(final Concept c){
        return (AnchoredLinkedList<Triplet>)this.by_d.get(c.char_index, this.by_c.root);
    }
    
    /**
     * Renvoie les "chemins" hierarchiques d'un concept
     * @return 
     */
    public ArrayList<Concept> getFullPaths(){
        return null;
    }
    
    /**
     * Ajoute une nouvelle relation entre deux cocnepts
     * @param name
     * @param r
     */
    public void addRelation(final String name, Relation r){
        r.p_name = this.index_relations_by_name.addString(name, r);
        
        //Ajout dans l'index de la hierarchie des concepts
        ArrayList<Integer> _indexes = new ArrayList<>();//utiliser des tableaux plutot que des arraylist
        //voire des Vectors
        _indexes.add(r.index);
        //System.out.println("ADDING CONCEPT:"+r.index);
        try {
            this.index_relations_by_taxonomy.addNumericIndexes(_indexes, r);
            r.p_index = this.index_relations_by_taxonomy.suffix_link;//ceci est un test!!!!
        } catch (Exception ex) {
            Logger.getLogger(OntoRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteExtensionConcept(final Concept _fils, final Concept _parent){
        try {
            this.ancestor_matrix_c.removeAncestor((char) _fils.index, (char) _parent.index);
        }
        catch(Exception e){
            //on vera plus tard...
        }
    }

    /**
     * Ajoute un concept fils a un concept
     * @param fils
     * @param parent
     * @param r
     */
    public void extendConcept(Concept fils, Concept parent, final Relation r){
        ArrayList<Integer> _indexes = new ArrayList<>();
        _indexes.add(parent.index);
        _indexes.add(fils.index);
        /*ArrayList<Concept> _concepts = new ArrayList<>();
        _concepts.add(parent);
        _concepts.add(fils);*/
        try {
            //Ajoute a l'arbre des suffixes representant la hierarchie des relations
            this.index_concepts_by_taxonomy.addNumericIndexes(_indexes, fils);

            if(fils.parent == null) {
                fils.parent = parent;
            }
            //fils.p_index = this.index_concepts_by_taxonomy.suffix_link;//ceci est un test!!!!
            /*
            //on recupere les relations du parent
            AnchoredLinkedList get = (AnchoredLinkedList)this.by_d.get(parent.index+"");
            //On recupere les relations du fils
            AnchoredLinkedList get1 = (AnchoredLinkedList)this.by_d.get(fils.index+"");
            */            
            //System.out.println("GETSIZE:"+get1.size);
            //On lie les deux relations
            //this.by_d.set(fils.index+"", AnchoredLinkedList.merge((AnchoredLinkedList)this.by_d.get(fils.index+""), (AnchoredLinkedList)this.by_d.get(parent.index+"")));
            AnchoredLinkedList.merge((AnchoredLinkedList)this.by_d.get((fils.char_index)), (AnchoredLinkedList)this.by_d.get(parent.char_index));
            //Ajoute le fils comme nouvel element de la matrice de parente
            this.ancestor_matrix_c.addItem((char)fils.index);
            //on doit recuperer tous les ancetres du parent d'abord
            HashSet<Character> ancestors = this.ancestor_matrix_c.getAncestors((char)parent.index);
            if(ancestors!=null){
                for(Character c : ancestors){
                    this.ancestor_matrix_c.addAncestor((char)fils.index, c);
                }
            }
            //Ajoute le parent comme nouvel item de la matrice de parente du fils
            this.ancestor_matrix_c.addAncestor((char)fils.index, (char)parent.index);
            
            /**
             * Avec cette technique, on a beacoup de repetitions par contre, cette technique est pas opti niveau ram
             * Le principal pour l'instant est que cela fonctionne.
             */
            
            //System.out.println(get1.size);
            //implique que la liste chainee du fils est finale. Si on ajoute un nouvel element a la liste, 
            //alors, on fait des addFirst quand on ajoute une nouvelle relation et des addLast quand on utilise l'heritage.
            
            //lier la liste chainee des proprietes a celle du parent !
        } catch (Exception ex) {
            Logger.getLogger(OntoRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * CREATION DE NOUVEAUX ELEMENTS
     */
    
    /**
     * 
     * @return 
     */
    public Relation createRelation(final String name){
        Relation r = new Relation();
        r.name = name;
        r.index = Relation.ID++;
        //r.char_index = (r.index+"").toCharArray();
        //char[] cc = 
        r.char_index = (r.index+"").toCharArray();//tour de passe passe, necessaire pour acceler le traitement apres.
        //Character[] nchars 
        /*for(int i=0;i<cc.length;i++){
            r.char_index[i] = (Character)cc[i];
        }*/
        if(this.relations.size>0){
            this.relations.addLast(r);
        }
        else{
            this.relations = new LinkedList<>(r);
        }
        return r;
    }
    
    /**
     * 
     * @return 
     */
    public Relation createRelation(final String name, final int index){
        Relation r = this.createRelation(name);
        r.index = index;
        //r.char_index = (r.index+"").toCharArray();
       //char[] cc = 
        r.char_index = (r.index+"").toCharArray();//tour de passe passe, necessaire pour acceler le traitement apres.
        //Character[] nchars 
        /*for(int i=0;i<cc.length;i++){
            r.char_index[i] = (Character)cc[i];
        }*/
        return r;
    }
    
    /**
     * 
     * @param name
     * @return 
     */
    public Concept createConcept(final String name){
        Concept c = new Concept();
        //c.name = name;
        c.index = Concept.ID++;
        //char[] cc = 
        c.char_index = (c.index+"").toCharArray();//tour de passe passe, necessaire pour acceler le traitement apres.
        //Character[] nchars 
        /*for(int i=0;i<cc.length;i++){
            c.char_index[i] = cc[i];
        }*/
        //est ce que l'index en integer est vraimment necessaire du coup ?
        //il suffit de faire une methode qui compare chaque element du tableau tant que egal. si l'un est plus grand que l'autre alors c'est le plus grand qui est + grand ?
        if(this.concepts.size>0){
            this.concepts.addLast(c);
        }else{
            this.concepts = new LinkedList<>(c);
        }
        return c;
    }
    
    /**
     * 
     * @return 
     */
    public Concept createConcept(final String name, final int index){
        Concept c = this.createConcept(name);
        c.index = index;
        //c.char_index = (c.index+"").toCharArray();
        //char[] cc = 
        c.char_index = (c.index+"").toCharArray();//tour de passe passe, necessaire pour acceler le traitement apres.
        //Character[] nchars 
        /*for(int i=0;i<cc.length;i++){
            c.char_index[i] =(Character)cc[i];
        }*/
        return c;
    }
    
    /**
     * nettoyer ca...
     * @param r
     * @param c1
     * @param c2 
     */
    public void createTriplet(final Relation r, final Concept c1, final Concept c2){
        //On cree la relation originale
        Triplet t = new Triplet();
        t.relation = r;
        t.domaine = c1;
        t.codomaine = c2;
        t.sens = OntoRepresentation.RELATION_TRUE;
        //On cree la relation inverse
        
        /*Triplet t2 = new Triplet();
        t.relation = r;
        t.domaine = c1;
        t.codomaine = c2;
        t.sens = OntoRepresentation.RELATION_FALSE;
        */
        //System.out.println("Triplet cree:"+c1.index+" "+c2.index);
        
        //On ajoute a la liste des "vraies" representations
        this.triplets.add(t);
        //On ajoute a la liste des "fausses" representations
        //this.triplets_inv.add(t2);//inutile maintenant!!!!
        
        //Ajoute aux autres index
        //System.out.println("Adding index "+c1.index+" to ontology.");
        //Creer une methode qui ajoute un item a un index particulier puis qui l'ajoute a la fin de la liste
        //codomaine
        this.by_c.addString(c2.char_index, t, RadixTree.OVERRIDE_VALUES);//remplacer par t
        //domaine
        this.by_d.addString(c1.char_index, t, RadixTree.OVERRIDE_VALUES);//remplacer par t
        //System.out.println("AAAA:"+c1.index);
        //System.out.println("NOFCHILDS:"+((AnchoredLinkedList)this.by_d.get(c1.index+"")).size);
        //relation
        this.by_r.addString(r.char_index, t, RadixTree.OVERRIDE_VALUES);//remplacer par t
    }
    
    public Integer[][][] matrix_instances_props = new Integer[4000][][];//[1000][50];
    //public int[][][] matrix_instances_props = new int[4000][][];
    public ArrayList<TripletInst> instance_triples = new ArrayList<>();

    public void createTripletInst(final Relation r, final Instance c1, final Instance c2){
        TripletInst t = new TripletInst();
        t.relation = r;
        t.domaine = c1;
        t.codomaine = c2;
        t.sens = OntoRepresentation.RELATION_TRUE;
        //meme chose mais pour les instances, necessaire pour l'instant..
        /*if(matrix_instances_props[c1.index][c2.index][0] == null){
            matrix_instances_props[c1.index][c2.index][0] = 0;
        }
        matrix_instances_props[c1.index][c2.index][0] = matrix_instances_props[c1.index][c2.index][0]+1;//r.index;
        matrix_instances_props[c1.index][c2.index][matrix_instances_props[c1.index][c2.index][0]] = r.index;
        */
        
        //RadixTree.RadixNode addString = this.by_d_by_inst.addString(c1.char_index, t, RadixTree.OVERRIDE_VALUES); //remplacer par t
        //System.out.println(">>>"+addString);

        if(matrix_instances_props[c2.index] == null){
            matrix_instances_props[c2.index] = new Integer[4000][];
        }
        if(matrix_instances_props[c2.index][c1.index] == null){
            matrix_instances_props[c2.index][c1.index] = new Integer[50];
            Arrays.fill(matrix_instances_props[c2.index][c1.index], 0);
        }

        /*if(matrix_instances_props[c2.index][c1.index][0] == null){
            matrix_instances_props[c2.index][c1.index][0] = 0;
        }*/
        matrix_instances_props[c2.index][c1.index][0] = matrix_instances_props[c2.index][c1.index][0]+1;//r.index;
        matrix_instances_props[c2.index][c1.index][matrix_instances_props[c2.index][c1.index][0]] = r.index;

        this.instance_triples.add(t);
    }
    
    /**
     * Renvoie tous les concepts relies a un concept donne (domaine ou codomaine)
     * @param c
     * @return 
     */
    public AnchoredLinkedList<Triplet> getRelatedTriplets(final Concept c){        
        //return AnchoredLinkedList.merge((AnchoredLinkedList)this.by_c.get(c.index+""), (AnchoredLinkedList)this.by_d.get(c.index+""));
        
        
        //System.out.println(((LinkedList)this.by_d.get(c.char_index)).size+"-----------");
        //System.out.println(((LinkedList)this.by_c.get(c.char_index)).size+"-----------");
        
        return AnchoredLinkedList.merge((AnchoredLinkedList)this.by_c.get(c.char_index), (AnchoredLinkedList)this.by_d.get(c.char_index));
    }
    
    /**
     * Ajoute un concept fils a plusieurs concepts. Multiheritage
     */
    public void extendConcepts(){
        
    }
    
    /**
     * Ajoute une instance a un concept
     * @param i
     * @return 
     */
    public Instance createInstance(int i){
        Instance instance = new Instance();
        //this.instances.ensureCapacity(i+1);
        this.instances.add(i, instance);
        
        instance.index = i;
        instance.char_index = (instance.index+"").toCharArray();
        return instance;
    }
    
    public void addInstance(Instance instance, Concept c){
        instance.concept = c;
        //gros racourci mais suffisant pour l'instant
        //cf. code PHP (semantics2.php)
    }
    
    public Instance getInstance(int id){
        return this.instances.get(id);
    }

    public ArrayList<Instance> getAllInstances(){
        return this.instances;
    }

    /**
     * Renvoie les instances d'un concept
     */
    public ArrayList<Instance> getInstances(final Concept c){
        return null;
    }
    
    /**
     * Renvoie la structure hierarchique de l'ontologie
     */
    public void getTaxonomie(){
        
    }
    
    /**
     * Permet de savoir si une instance est instance d'un concept
     * @param c
     * @param i
     * @return 
     */
    public boolean isInstanceOf(Concept c, Instance i){
        return true;
    }
    
    /**
     * Reprend la methode de l'ontologie originale. Utile pour conserver le code orginel
     * @param ancestor
     * @param descendant
     * @return 
     */
    public boolean isConceptDescendant(Concept ancestor, Concept descendant){
        return this.isAncestorOf(ancestor, descendant);
    }
    
    /**
     * 
     * @param ancestor
     * @param descendant
     * @return 
     */
    public boolean isAncestorOf(Concept ancestor, Concept descendant){
        //on peut soit regarder tous les parents du "fils" pour savoir si il coincident avec le "parent"
        //ou alors regarder les fils du parent pour savoir si ils correspondent avec le fils        
        //la methode des listes chainees permet facilement
        return this.ancestor_matrix_c.isAncestor((char)descendant.index, (char)ancestor.index);
    }
    
    
    /**
     * ANCIENNES METHODES
     */
    
    //------------------------------------------------- Package private methods
    /**
     * A quoi correspond cette propriete ?
     */
    void getRootPropertiesByConcepts( )
    {
            //return rootPropertiesByConcepts;
    }

    void getNonRootPropertiesByConcepts( )
    {
            //return nonRootPropertiesByConcepts;
    }

    //------------------------------------------------------- Setters / Getters
    /**
     * Gets all concepts in the hierarchy concepts.
     * 
     * Changer ca par l'array list des concepts
     * @return All concepts.
     */
    public LinkedList<Concept> getAllConcepts( )
    {
            //return hierarchyConcepts.getAllNodes( );
        //return this.index_concepts_by_taxonomy.root;
        return this.concepts;
    }

    /**
     * Gets all properties in the hierarchy properties.
     * Changer ca par l'arraylist des relations
     * 
     * @return All properties.
     */
    public LinkedList<Relation> getAllProperties( )
    {
            //return hierarchyProperties.getAllNodes( );
        return this.relations;
    }

    /**
     * Gets all children of the concept in the hierarchy concepts.
     * 
     * @param concept Parent concept.
     * 
     * @return All children of the concept.
     */
    public ArrayList<Concept> getConceptChildren( final Concept concept )
    {
        //Set<A> children = hierarchyConcepts.getChildrenNodes( concept );
        //return children;
        return this.index_concepts_by_taxonomy.getSonsValues(concept.p_index);
    }
    
    public ArrayList<Concept> getConceptChildren(final Integer index){
        Concept c = this.getConcept(index);
        return this.getConceptChildren(c);
    }

    /**
     * Gets all children of the property in the hierarchy properties.
     * 
     * @param property Parent property.
     * 
     * @return All children of the property.
     */
    public ArrayList<Relation> getPropertyChildren( final Relation property )
    {
            //Set<A> children = hierarchyProperties.getChildrenNodes( property );
            //return children;
        return this.index_relations_by_taxonomy.getSonsValues(property.p_index);
    }
    
    public ArrayList<Relation> getPropertyChildren(final Integer index){
        Relation r = this.getRelation(index);
        return this.getPropertyChildren(r);
    }

    /**
     * Gets all root concepts the hierarchy concepts.
     * 
     * @return All root concepts.
     */
    public ArrayList<Concept> getRootConcepts(boolean o) 
    {
        //return true;    
        //return hierarchyConcepts.getRootNodes( );
        //return this.root_concepts;
        return this.conceptsByLevel.get(0);
    }

    /**
     * Gets all root properties the hierarchy properties.
     * 
     * @return All root properties.
     */
    public ArrayList<Relation> getRootProperties(boolean o) 
    {
        //return true;    
        //return hierarchyProperties.getRootNodes( );
        return this.root_relations;
    }

    /**
     * Renvoie les concepts racines de l'ontologie
     * @param depth
     * @return
     */
    public ArrayList<Concept> getRootConcepts(final int depth){
        if(depth < this.conceptsByLevel.size()){
            //System.out.println("on a "+this.conceptsByLevel.get(depth).size()+" au niveau "+depth);
            return this.conceptsByLevel.get(depth);
        }
        else return null;
    }

    public int findDepth(Concept concept) {

//        System.out.println(concept.getName());
//        System.out.println(concept.getIndex());
//        System.out.println(getParents(concept));

        Iterator iter = getParents(concept).iterator();

        if (!iter.hasNext()) {
            return 0;
        }

        int depth=0;

        while (iter.hasNext()) {
//                System.out.println(depth);
            Concept parent = (Concept) iter.next();

            depth = Math.max(depth, findDepth(parent));
        }

        return depth + 1;
    }

    public int findDepthRel(Relation rel) {
//        System.out.println(concept.getName());
//        System.out.println(concept.getIndex());
//        System.out.println(getParents(concept));
        Iterator iter = getParents(rel).iterator();
        if (!iter.hasNext()) {
            return 0;
        }
        int depth=0;
        while (iter.hasNext()) {
//                System.out.println(depth);
            Relation parent = (Relation) iter.next();
            depth = Math.max(depth, findDepthRel(parent));
        }
        return depth + 1;
    }

    public Concept getConceptByLevel(Concept c, final Integer index){
        ArrayList<Concept> roots = getRootConcepts(0);
        Concept find=c;
        for (Concept rc:roots){
            Iterator iter = getParents(c).iterator();
//            System.out.println("Roots: "+rc.getName());
            while (iter.hasNext()) {
//                System.out.println(depth);
                Concept parent = (Concept) iter.next();
//                System.out.println("Parent: "+parent.getName());
                if (parent.equals(rc))
                    find = rc;
            }
        }
        return find;
    }

    /**
     * Gets the non-root properties which exist in the ontology between a subject and an object concepts.
     * 
     * @return Non-root properties between the subject and the object concepts.
     */
    public ArrayList<Triplet> getNonRootProperties( final Concept sujet, final Concept objet ){
        //System.out.println("on recherche par sujet "+Arrays.toString(sujet.char_index));
        AnchoredLinkedList<Triplet> _triplets = (AnchoredLinkedList<Triplet>)this.by_d.get(sujet.char_index, this.by_d.root);
        //LinkedList<Triplet> res_triplets = null;
        ArrayList<Triplet> res_triplets = new ArrayList<>();
        //puis on va enlever les elements dont "objet" n'est pas l'objet
        if(_triplets!=null){
            _triplets.reset();
            do{
                Triplet t = (Triplet)_triplets.curr().value;
                /*if(remove){
                    _triplets.remove(temp);
                }*/
                //System.out.println("on examine "+t.toString());
                if(t.codomaine.index==objet.index&&(!this.isRootProperty(t.relation))){
                    res_triplets.add(t);
                    
                }
            }
            while(_triplets.hasNext());
        }
        return res_triplets;
    }
    
    public ArrayList<Relation> getNonRootProperties_OnlyProp( final Concept sujet, final Concept objet ){
        //System.out.println("on recherche par sujet "+Arrays.toString(sujet.char_index));
        AnchoredLinkedList<Triplet> _triplets = (AnchoredLinkedList<Triplet>)this.by_d.get(sujet.char_index, this.by_d.root);
        //LinkedList<Triplet> res_triplets = null;
        ArrayList<Relation> res_triplets = new ArrayList<>();
        //puis on va enlever les elements dont "objet" n'est pas l'objet
        if(_triplets!=null){
            _triplets.reset();
            do{
                Triplet t = (Triplet)_triplets.curr().value;
                /*if(remove){
                    _triplets.remove(temp);
                }*/
                //System.out.println("on examine "+t.toString());
                if(t.codomaine.index==objet.index&&(!this.isRootProperty(t.relation))){
                    res_triplets.add(t.relation);
                }
            }
            while(_triplets.hasNext());
        }
        return res_triplets;
    }
    
    public ArrayList<Triplet> getNonRootProperties( final Instance sujet, final Instance objet ){
        //System.out.println("on recherche par sujet "+Arrays.toString(sujet.char_index));
        AnchoredLinkedList<TripletInst> _triplets = (AnchoredLinkedList<TripletInst>)this.by_d_by_inst.get(sujet.char_index, this.by_d_by_inst.root);
        //LinkedList<Triplet> res_triplets = null;
        ArrayList<Triplet> res_triplets = new ArrayList<>();
        //puis on va enlever les elements dont "objet" n'est pas l'objet
        if(_triplets!=null){
            _triplets.reset();
            do{
                Triplet t = (Triplet)_triplets.curr().value;
                /*if(remove){
                    _triplets.remove(temp);
                }*/
                //System.out.println("on examine "+t.toString());
                if(t.codomaine.index==objet.index&&(!this.isRootProperty(t.relation))){
                    res_triplets.add(t);
                    
                }
            }
            while(_triplets.hasNext());
        }
        return res_triplets;
    }

    /**
     * Gets the root properties which exist in the ontology between a subject and an object concepts.
     *
     * 
     * @return Root properties between the subject and the object concepts.
     */
    public ArrayList<Triplet> getRootProperties( final Concept sujet, final Concept objet){
        AnchoredLinkedList<Triplet> _triplets = (AnchoredLinkedList<Triplet>)this.by_d.get(sujet.char_index, this.by_d.root);
        //LinkedList<Triplet> res_triplets = null;
        ArrayList<Triplet> res_triplets = new ArrayList<>();
        //puis on va enlever les elements dont "objet" n'est pas l'objet
        if(_triplets!=null){
            //LinkedList.LinkedNode temp = null;
            //boolean remove = false;
            /**
             * Ameliorer la methode remove de la liste
             * envoyer le sens de parcours actuel pour mettre a jour "curr" dans la foulee
             */
            _triplets.reset();
            do{
                if(_triplets.curr().value instanceof LinkedList){
                    break;//This is a patch, it should be modified
                }
                else{
                    Triplet t = (Triplet)_triplets.curr().value;
                    if(t.codomaine.index==objet.index&&this.isRootProperty(t.relation)){
                        res_triplets.add(t);
                    }
                }
            }
            while(_triplets.hasNext());
        }
        return res_triplets;
    }
    
    public ArrayList<Relation> getRootProperties_OnlyProp( final Concept sujet, final Concept objet ){
        AnchoredLinkedList<Triplet> _triplets = (AnchoredLinkedList<Triplet>)this.by_d.get(sujet.char_index, this.by_d.root);
        //LinkedList<Triplet> res_triplets = null;
        ArrayList<Relation> res_triplets = new ArrayList<>();
        //puis on va enlever les elements dont "objet" n'est pas l'objet
        if(_triplets!=null){
            //LinkedList.LinkedNode temp = null;
            //boolean remove = false;
            /**
             * Ameliorer la methode remove de la liste
             * envoyer le sens de parcours actuel pour mettre a jour "curr" dans la foulee
             */
            _triplets.reset();
            do{
                if(_triplets.curr().value instanceof LinkedList){
                    break;//This is a patch, it should be modified
                }
                else{
                    Triplet t = (Triplet)_triplets.curr().value;
                    if(t.codomaine.index==objet.index&&this.isRootProperty(t.relation)){
                        res_triplets.add(t.relation);
                    }
                }
            }
            while(_triplets.hasNext());
        }
        return res_triplets;
    }

    /**
     * Sets the hierarchy concepts.
     */
    public void setHierarchyConcepts(  )
    //public void setHierarchyConcepts( final Hierarchy<A> hierarchyConcepts )
    {
            //this.hierarchyConcepts = hierarchyConcepts;
    }

    /**
     * Sets the hierarchy properties.
     *
     */
    public void setHierarchyProperties(  )
    //public void setHierarchyProperties( final Hierarchy<A> hierarchyProperties )
    {
            //this.hierarchyProperties = hierarchyProperties;
    }

    //---------------------------------------------------------- Public methods
    /**
     * Adds a root concept to this hierarchy representation.
     *
     */
    public void addRootConcept(final String name, final Concept c ) 
    //public void addRootConcept( final A rootConcept ) 
    {
        this.addConcept(name, c);
        //this.root_concepts.add(c);
        this.conceptsByLevel.get(0).add(c);
    }
    

    /**
     * Adds a root property to this hierarchy representation.
     */
    public void addRootProperty(final String name, final Relation r)
    //public void addRootProperty( final A rootProperty ) 
    {
        this.addRelation(name, r);
        this.root_relations.add(r);
    }

    /**
     * Adds a child concept to this hierarchy representation.<br/>
     * Links the child concept and the parent concept.<br/>
     * If the parent concept is not already in the hierarchy representation, it will be added as a root concept.
     *
     * @param parent Parent concept of the child.
     */
    public void addConceptChild(final Concept fils, final Concept parent)
    //public void addConceptChild( final A child, final A parent )
    {
        //hierarchyConcepts.addNonRootNode( child, parent );
        this.extendConcept(fils, parent, null);
    }

    /**
     * Adds a child property to this hierarchy representation.<br/>
     * Links the child property and the parent property.<br/>
     * If the parent property is not already in the hierarchy representation, it will be added as a root property.
     *
     * @param parent Parent property of the child.
     */
    public void addPropertyChild(final Relation fils, final Relation parent )
    //public void addPropertyChild( final A child, final A parent )
    {
        //hierarchyProperties.addNonRootNode( child, parent );
        ArrayList<Integer> _indexes = new ArrayList<>();
        _indexes.add(parent.index);
        _indexes.add(fils.index);

        fils.parent = parent;

        ArrayList<Relation> _relations = new ArrayList<>();
        _relations.add(parent);
        _relations.add(fils);
        try {
            //Ajoute a l'arbre des suffixes representant la hierarchie des relations
            this.index_relations_by_taxonomy.addNumericIndexes(_indexes, _relations);
            
            /**
             * Qu'en est il de cette instruction ? De quoi heritent les proprietes ?
             */
            //AnchoredLinkedList.merge((AnchoredLinkedList)this.by_d.get((fils.char_index)), (AnchoredLinkedList)this.by_d.get(parent.char_index));
            
            //Ajoute le fils comme nouvel element de la matrice de parente
            this.ancestor_matrix_r.addItem((char)fils.index);
            //on doit recuperer tous les ancetres du parent d'abord
            HashSet<Character> ancestors = this.ancestor_matrix_r.getAncestors((char)parent.index);
            if(ancestors!=null){
                for(Character c : ancestors){
                    this.ancestor_matrix_r.addAncestor((char)fils.index, c);
                }
            }
            //Ajoute le parent comme nouvel item de la matrice de parente du fils
            this.ancestor_matrix_r.addAncestor((char)fils.index, (char)parent.index);
            
            
        } catch (Exception ex) {
            Logger.getLogger(OntoRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Indicates if a concept (descendant) is the descendant of another concept (ancestor).
     *
     * 
     * @return true if the ancestor concept is actually an ancestor of the descendant concept.
     */
    public boolean isConceptDescendant( )
    //public boolean isConceptDescendant( final A descendant, final A ancestor )
    {
            /*Set<A> children = getConceptChildren( ancestor );

            // Test if descendant is a child
            if( children.contains( descendant ) )
            {
                    return true;
            }
            else if( children.isEmpty( ) )
            // There is no more descendants to test
            {
                    return false;
            }

            // Test if descendant is the child of one of the children
            for ( A child : children ) 
            {
                    if( isConceptDescendant( descendant, child ) )
                    {
                            return true;
                    }
            }

            // descendant is the child of nobody*/
            return false;
    }

    /**
     * Indicates if a property (descendant) is the descendant of another property (ancestor).
     *
     * @return true if the ancestor property is actually an ancestor of the descendant property.
     */
    public boolean isPropertyDescendant( )
    //public boolean isPropertyDescendant( final A ancestor, final A descendant )
    {
            /*Set<A> children = getPropertyChildren( ancestor );

            // Test if descendant is a child
            if( children.contains( descendant ) )
            {
                    return true;
            }
            else if( children.isEmpty( ) )
            // There is no more descendants to test
            {
                    return false;
            }

            // Test if descendant is the child of one of the children
            for ( A child : children ) 
            {
                    if( isPropertyDescendant( child, descendant ) )
                    {
                            return true;
                    }
            }

            // descendant is the child of nobody*/
            return false;
    }

    /**
     * Indicates if a concept (descendant) is the descendant of another concept (ancestor),
     * or if they are the same concept.
     * 
     * @param descendant The descendant concept to test.
     * @param ancestor The ancestor concept to test.
     * 
     * @return true if the ancestor concept is actually an ancestor of the descendant concept,
     * or if they are the same concept.
     */
    public boolean isConceptEqualOrDescendant( final Concept ancestor, final Concept descendant )
    //public boolean isConceptEqualOrDescendant( final A ancestor, final A descendant )
    {
        //return ( descendant.equals( ancestor ) || isConceptDescendant( descendant, ancestor ) );
        return this.ancestor_matrix_c.isEqualOrDescendant((char)descendant.index, (char)ancestor.index);
    }
    
    /**
     * La meme mais directement avec les index
     * @param ancestor
     * @param descendant
     * @return 
     */
    public boolean isConceptEqualOrDescendant( final int ancestor, final int descendant )
    //public boolean isConceptEqualOrDescendant( final A ancestor, final A descendant )
    {
        //return ( descendant.equals( ancestor ) || isConceptDescendant( descendant, ancestor ) );
        return this.ancestor_matrix_c.isEqualOrDescendant((char)descendant, (char)ancestor);
    }

    /**
     * Indicates if a property (descendant) is the descendant of another property (ancestor),
     * or if they are the same property.
     * 
     * @param descendant The descendant property to test.
     * @param ancestor The ancestor property to test.
     * 
     * @return true if the ancestor property is actually an ancestor of the descendant property,
     * or if they are the same property.
     */
    public boolean isPropertyEqualOrDescendant(final Relation ancestor, final Relation descendant )
    //public boolean isPropertyEqualOrDescendant( final A ancestor, final A descendant )
    {
            //return ( descendant.equals( ancestor ) || isPropertyDescendant( ancestor, descendant ) );
        return this.ancestor_matrix_r.isEqualOrDescendant((char)descendant.index, (char)ancestor.index);
    }
    
    /**
     * La meme mais directement avec les index
     * @param ancestor
     * @param descendant
     * @return 
     */
    public boolean isPropertyEqualOrDescendant(final int ancestor, final int descendant )
    {
            //return ( descendant.equals( ancestor ) || isPropertyDescendant( ancestor, descendant ) );
        return this.ancestor_matrix_r.isEqualOrDescendant((char)descendant, (char)ancestor);
    }

    /**
     * Links a property to a subject and a object concept (and their descendants).
     */
    
    public void linkPropertyByConcepts( )
    //public void linkPropertyByConcepts( final A property, final A subjectConcept, final A objectConcept )
    {
            /*if( isRootProperty( property ) )
            {
                    linkRootPropertyByConcepts( property, subjectConcept, objectConcept );
            }
            else
            {
                    linkNonRootPropertyByConcepts( property, subjectConcept, objectConcept );
            }*/
    }

    /**
     * Links properties to a subject and a object concept (and their descendants).
     */
    public void linkPropertiesByConcepts(ArrayList<Relation> predicats, Concept sujet, Concept objet)
    //public void linkPropertiesByConcepts( final Set<A> properties, final A subjectConcept, final A objectConcept )
    {  
        for(Relation predicat : predicats){
            this.linkPropertyByConcepts(predicat, sujet, objet);
        }
        /*for ( A property : properties )
            {
                    linkPropertyByConcepts( property, subjectConcept, objectConcept );
            }*/
    }

    //---------------------------------------------------------- Private methods

    // Link a property to a subject and a object concept (and their descendants) 
    // in the specified propertyByConcepts hash map
    public void linkPropertyByConcepts(Relation predicat, Concept sujet, Concept objet)
    //private void linkPropertyByConcepts( final A property, final A subjectConcept, final A objectConcept, PropertiesByConcepts<A> propertiesByConcepts )
    {
        this.createTriplet(predicat, sujet, objet);
        /*// Linkage of the concepts and the property
            Pair<A, A> concepts = new Pair<A,A>( subjectConcept, objectConcept );
            Set<A> properties = propertiesByConcepts.get( concepts );
            if( properties == null )
            // If there is no property between these two concepts yet
            {
                    properties = new HashSet<A>( );
                    propertiesByConcepts.put( concepts, properties );
            }
            properties.add( property );

            // Linkage between the descendant concepts and the property
            Set<A> subjectChildren = getConceptChildren( subjectConcept );
            Set<A> objectChildren = getConceptChildren( objectConcept );
            for ( A subjectChild : subjectChildren )
            {
                    // Link the children of the subject
                    linkPropertyByConcepts( property, subjectChild, objectConcept, propertiesByConcepts );
            }
            for( A subjectTarget : objectChildren )
            {
                    // Link the children of the object
                    linkPropertyByConcepts( property, subjectConcept, subjectTarget, propertiesByConcepts );
            }*/
    }


    // Return the properties applicable between a subject and a object concept
    // in the specified propertiesByConcept hash map
    /**
     * Dans la representation actuelle, comment fait-on?
     * d'abord get triplets avec d puis ceux avec c. on fait l'intersection des deux
     * 
     * Ceci devrait fonctionner mais n'est pas tres optimal. On doit boucler tous les triplets d'un concept donné
     */
    private AnchoredLinkedList<Triplet> getProperties(final Concept sujet, final Concept objet )
    //private Set<A> getProperties( final A subjectConcept, final A objectConcept, PropertiesByConcepts<A> propertiesByConcepts )
    {
        //on recupere tous les triplets dont "sujet" est le sujet, 
        AnchoredLinkedList<Triplet> _triplets = (AnchoredLinkedList<Triplet>)this.by_c.get(sujet.char_index, this.by_c.root);
        //puis on va enlever les elements dont "objet" n'est pas l'objet
        if(_triplets!=null){
            LinkedList.LinkedNode temp = null;
            boolean remove = false;
            _triplets.reset();
            do{
                Triplet t = (Triplet)_triplets.curr().value;
                if(remove){
                    _triplets.remove(temp);
                }
                if(t.codomaine.index!=objet.index){
                    remove=true;
                    temp = _triplets.curr();
                }
            }
            while(_triplets.hasNext());
        }
        return _triplets;
    }	

    // Initialization of the attributes of this agent
    //Cette methode devient useless avec la nouvelle representation
    private void initializeAttributes( )
    {
            /*hierarchyConcepts = new Hierarchy<A>( );
            hierarchyProperties = new Hierarchy<A>( );
            rootPropertiesByConcepts = new PropertiesByConcepts<A>( );
            nonRootPropertiesByConcepts = new PropertiesByConcepts<A>( );
            */ 
    }

    // Indicate if a property is a root property
    private boolean isRootProperty(final Relation r )
    //private boolean isRootProperty( final A property )
    {
         return this.root_relations.contains(r);
         //return this.index_relations_by_taxonomy.hasParents();
    }

    /**
     * Ces methodes deviennent inutiles avec la nouvelle representation
     */
    // Link a non-root property to a subject and a object concept (and their descendants)
    private void linkNonRootPropertyByConcepts( )
    //private void linkNonRootPropertyByConcepts( A property, A subjectConcept, A objectConcept )
    {
            //linkPropertyByConcepts( property, subjectConcept, objectConcept, nonRootPropertiesByConcepts );
    }

    // Link a root property to a subject and a object concept (and their descendants)
    private void linkRootPropertyByConcepts( )
    //private void linkRootPropertyByConcepts( A property, A subjectConcept, A objectConcept )
    {
            //linkPropertyByConcepts( property, subjectConcept, objectConcept, rootPropertiesByConcepts );
    }
    
    /**
     * METHODES UTILITAIRES SUR LES TABLEAUX
     */
    
    /**
     * Permet de fusionner plusieurs tableaux
     * @param arrays
     * @return 
     */
    public static int[] merge(final int[][] arrays ) {
        int size = 0;
        for ( int[] a: arrays ){
            size += a.length;
        }
        int[] res = new int[size];

        int destPos = 0;
        for ( int i = 0; i < arrays.length; i++ ) {
            if ( i > 0 ) {
                destPos += arrays[i-1].length;
            }
            int length = arrays[i].length;
            System.arraycopy(arrays[i], 0, res, destPos, length);
        }

        return res;
    }
    
    public static Triplet[] mergetwo(final Object[] array1, final Object[] array2 ) {
        //on compte length + length
        int length_one = array1.length;
        int length_two = array2.length;
        //on alloue un nouveau tableau de cette taille
        Triplet[] res = new Triplet[length_one+length_two];
        //on copie le premier tableau
        System.arraycopy(array1, 0, res, 0, length_one);
        //on copie le deuxieme tableau
        System.arraycopy(array2, 0, res, length_one, length_two);
        return res;
    }


    //seulement une copue shallow de tout SAUF des ancestormatrix.
    @Override
    public OntoRepresentation clone(){
        final OntoRepresentation copy = new OntoRepresentation();
        copy.index_relations_by_name = this.index_relations_by_name;
        copy.root_concepts = this.root_concepts;
        copy.root_relations = this.root_relations;
        copy.by_c = this.by_c;
        copy.by_d = this.by_d;
        copy.by_r = this.by_r;
        copy.by_d_by_inst = this.by_d_by_inst;
        copy.relations = this.relations;
        copy.concepts = this.concepts;
        copy.instances = this.instances;
        copy.triplets = this.triplets;
        copy.triplets_inv = this.triplets_inv;
        copy.index_concepts_by_taxonomy = this.index_concepts_by_taxonomy;
        copy.index_relations_by_taxonomy = this.index_relations_by_taxonomy;
        copy.index_concepts_by_name = this.index_concepts_by_name;
        copy.index_relations_by_id = this.index_relations_by_id;
        copy.index_concepts_by_id = this.index_concepts_by_id;
        copy.index_triplets = this.index_triplets;
        //final HashMap<Character, HashSet<Character>> copyMatrixR = (HashMap<Character, HashSet<Character>>) this.ancestor_matrix_r.matrix.clone();
        //final HashMap<Character, HashSet<Character>> copyMatrixC = (HashMap<Character, HashSet<Character>>) this.ancestor_matrix_c.matrix.clone();

        final Map<Character, HashSet<Character>> copyMatrixC = this.ancestor_matrix_c.matrix.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> new HashSet(e.getValue())));
        final Map<Character, HashSet<Character>> copyMatrixR = this.ancestor_matrix_r.matrix.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> new HashSet(e.getValue())));

        copy.ancestor_matrix_r = new AncestorMatrix();
        copy.ancestor_matrix_c = new AncestorMatrix();
        copy.ancestor_matrix_c.matrix = copyMatrixC;
        copy.ancestor_matrix_r.matrix = copyMatrixR;
        return copy;
    }
}

