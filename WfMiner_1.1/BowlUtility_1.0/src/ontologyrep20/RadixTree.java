package ontologyrep20;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * Meme principe que pour le Trie mais avec des noeuds regroupes -> splitNode()
 * Remplacer les ArrayList<RadixNode<A>> par des B-tree ? ou des HashMap ?
 * Faire des tests...
 * @author Enridestroy
 * @version 0.9.0
 * @param <A>
 * 
 */
public final class RadixTree<A>{
    public RadixNode<A> root = null;
    public static char _ID_ = 0;//commence a 1 du coup.
    public Character[] alphabet = new Character[65536];//[128];
    public final static boolean OVERRIDE_VALUES = true;
    public final static boolean NOT_OVERRIDE_VALUES = false;
    public boolean DEFAULT_ORV;
    
    public final static int SIMPLE_NODES = 0;
    public final static int LINKED_NODES = 1;
    public int DEFAULT_NODE;
    public RadixNode<A> EMPTY_NODE = null;
   
    
    //faire un tableau segmente. -> Linked Bucket Arrays ?
    //liste chainee, si on lui envoie 40, on doit faire ++ 40 fois.
    //on fait un tableau segmente. le premier element est lie a tous les autres.
    //si on veut l'item 40 et que les tableaux sont de 5 items, 
    //on fait 40/5 = 8.
    //Donc on prend le huitieme lien.
    
    
    //un tableau dans lequel on ajoute les elements un par un. tableau1.40 = 1, donc tableau2.1 = chr(40)
    //un autre tableau qui contient les caracteres.
    
    //public HashMap<Character> alphabet2 = new HashSet<>();
    //public char alphabet_size = 0;
    //liste des caractères Unicode codés de U+0000 à U+0FFF en hexadécimal (0 à 4 095 en décimal).
    
    public RadixTree(int default_node){
        this.DEFAULT_ORV = RadixTree.NOT_OVERRIDE_VALUES;
        this.DEFAULT_NODE = default_node;
        
        this.root = this.createNode();
    }
    
    /**
     * Permet de creer un noeud quelconque, peu importe le type de noeud.
     * @return 
     */
    public RadixNode<A> createNode(){
        RadixNode<A> node = null;
        switch(this.DEFAULT_NODE){
            case RadixTree.SIMPLE_NODES:
                node = new SimpleRadixNode<A>();
                break;
            case RadixTree.LINKED_NODES:
                node = new LinkedRadixNode<A>();
                break;
            default:
                break;
        }
        return node;
    }
    
    
    /**
     * Il faut le noeud a spliter, la chaine qui va causer le split
     * On peut faire mieux, on envoie la nouvelle position finale du noeud, et la fin de la nouvelle chaine
     * @param rn
     * @param chars
     * @param where
     * @param o
     * @return 
     */
    public RadixNode<A> splitNode(RadixNode<A> rn, Character[] chars, int where, A o){
        //System.out.println("START SPLITING : !!!!");
        //on cree les deux noeuds
                      
        RadixNode<A> first = this.createNode();
        RadixNode<A> second = null;
        if(chars.length != 0){
            second = this.createNode();
        }
        /*StringBuilder ss = new StringBuilder();
        for(int i=0;i<chars.length;i++){
            ss.append(chars[i]);
        }
        System.out.println("INDEX TO SPLIT:"+ss);
        */
        /**
         * On va tronquer l'index de rn.
         * Dans le premier noeud, on va copier les fils de rn. Il aura pour index ce que l'on a tronqué
         * Dans le deuxieme noeud, on va mettre aucun fils. Il aura pour index le tableau tronqué de la meme facon que rn.index
         * rn aura alors ces deux fils, les siens etant devenus ceux de first.
         * On doit set les parents de f et s a rn.
         */
        
        
        //Pourquoi on a besoin de split ??????
        where += 1;
        //System.out.println("WHERE:"+where);
        
        //FIRST
        Character[] remain = Arrays.copyOfRange(rn.index, where, rn.index.length);
        first.index  = remain;
        
        /*StringBuilder s = new StringBuilder();
        for(int i=0;i<remain.length;i++){
            s.append(remain[i]);
        }
        System.out.println("FIRST INDEX:"+s);
        */
        //SECOND
        if(chars.length !=0){
            second.index = chars;
        }
        /*s = new StringBuilder();
        for(int i=0;i<second.index.length;i++){
            s.append(second.index[i]);
        }
        System.out.println("SECOND INDEX:"+s);
        */
        
        //System.out.println("RNSIZE:"+rn.children.size());
        //on met a jour les fils du nouveau noeud
        //first.children = (ArrayList<RadixNode>)rn.children.clone();
                
        //first.children = rn.children;
        //on modifie le paretn de first
        first.parent = rn;
        if(chars.length != 0){
            second.parent = rn;
        }
        
        first.children = rn.children;
        //pas tres bon mais ca ira
        for(final RadixNode fils : first.children){
            fils.parent = first;
        }
        
        
        
        rn.children = new ArrayList<>();

        rn.children.add(first);
        if(chars.length != 0){
            rn.children.add(second);
        }
        
        //System.out.println("RNVALUE1:"+((LinkedRadixNode)rn).toString());
        
        first.setValue(rn.getValue());
        
        //System.out.println("RNVALUE2:"+((LinkedRadixNode)rn).toString());
        rn.clear();
        {
            final A value = first.getValue();
            if(value instanceof ObjectWithPointedValues){
                //final ObjectWithPointedValues obj = (ObjectWithPointedValues)first.getValue();//updateObject(first);
                ((ObjectWithPointedValues)value).updateObject(first);
            }
        }
        
        if(chars.length != 0){
            second.append(o);
        }
        else{
            rn.setValue(o);
        }
        //System.out.println("RNVALUE3:"+((LinkedRadixNode)first).toString());
        //on recupere ce qui reste de la chaine
        
        //on met a jour l'index
        //rn.index = Arrays.copyOfRange(rn.index, 0, where);
        
        //System.out.println("AND WHERE IS:"+(rn.index.length - where));
        Character[] temp = rn.index;
        //rn.index = new Character[temp.length - where];
        rn.index = new Character[where];
        //System.out.println("LENGTH OF ARRAY:"+(temp.length - where));
        //System.out.println("WHEREEEEE:"+where);
        int count = 0;
        for(int z=0;z<where;z++){
            if(this.alphabet[temp[z]]==null){
                this.alphabet[temp[z]] = temp[z];
            }
            //System.out.println("COUNTTTTTT:"+this.alphabet[temp[z]]);
            rn.index[count] = this.alphabet[temp[z]];
            ++count;
        }
        /*
        for(Character c : rn.index){
            System.out.print(c);
        }*/     
                
        /*
        s = new StringBuilder();
        for(int i=0;i<rn.index.length;i++){
            s.append(rn.index[i]);
        }
        System.out.println("RN INDEX:"+s);
        
        System.out.println("END SPLITING : !!!!");
        
        System.out.println("RNSIZE2:"+rn.children.size());
        System.out.println("RNSIZE3:"+first.children.size());
        System.out.println("RNSIZE4:"+second.children.size());
        */
        if(chars.length == 0){
            return rn;
        }
        else return second;
        /*if(chars.length != 0){
            return second;
        }
        else return first;//renvoie l'id de second node !*/
    }
    
    public RadixNode<A> add(RadixNode<A> n, Character[] s, A o){
        //creation du nouveau noeud
        //RadixNode node = new SimpleRadixNode();
        //RadixNode node = this.EMPTY_NODE.clone();
        RadixNode node = this.createNode();
                
        node.append(o);
        //System.out.println("WHAT APPENDED?:"+o+" VS "+node.toString());
        node.index = s;

        n.children.add(node);
        node.parent = n;
        
        ++n.n;
        return node;
    }
    
    
    /**
     * 
     * @param string
     * @param o
     * @return 
     */
    public RadixNode<A> addString(String string, A o){
        return this.addString(string, o, this.DEFAULT_ORV);
    }
    /**
     * 
     * @param string
     * @param o
     * @return 
     */
    public RadixNode<A> addString(char[] string, A o){
        return this.addString(string, o, this.DEFAULT_ORV);
    }
    
    /**
     * 
     * @param chars
     * @param o
     * @param ow
     * @return 
     */
    public RadixNode<A> addString(char[] chars, A o, boolean ow){
        
        //System.out.println("///////////////////////////////////////////");
        
        RadixNode<A> n = this.root;//on recupere la racine
        //on renverse le tableau, plus simple a traiter ?        
        /*for(int i = 0; i < chars.length / 2; i++){
            char temp = chars[i];
            chars[i] = chars[chars.length - i - 1];
            chars[chars.length - i - 1] = temp;
        }*/
        int id;
        int next = 0;
        int split = 0;
        int pos = 0;
        char count = 0;
        int test_test = chars.length;
        int remaining_chars = chars.length;//indique le nombre de caracteres restants
        int last_next = 0;
        Character[] sub;
        while(test_test>0){
            
            int[] res = this.containsChildValue(n,chars,pos, chars.length-test_test);
            id = res[0];//identifiant du noeud enfant qui matche au mieux
            next = res[1];//index du debut du prochain element a matcher
            split = res[2];//besoin de split le noeud ?
            pos = res[3];
            
            /*System.out.println("00:"+id);
            System.out.println("11:"+next);
            */
            //System.out.println("22:"+split);
            /*System.out.println("33:"+pos);
            */            
            //System.out.println("ID:"+id+"next:"+next);
            //si aucun noeud ne correspond, meme pas un petit peu
            //si id=0, cela peut vouloir dire que soit c'est le noeud 0 qui matche, 
            //si id<0, cela veur dire que rien n'a matche, meme pas un seul caractere. On doit donc l'ajouter directement
            //plutot que de renvoyer l'id, renvoyer le noeud directement.
            if(id<0){
                //on recupere la sous chaine a ajouter...
                //sub = Arrays.copyOfRange(chars, next, chars.length);
                
                //System.out.println("LENGTH:"+(chars.length-(last_next+pos)));
                sub = new Character[test_test];
                count = 0;
                for(int z=chars.length-test_test;z<chars.length;z++){
                    if(this.alphabet[chars[z]]==null){
                        this.alphabet[chars[z]] = chars[z];
                    }
                    //System.out.println("COUNT:"+(int)chars[z]);
                    sub[count] = this.alphabet[chars[z]];
                    ++count;
                }
                
                //on dit que chars = tableau de ints. donc sub = tableau de pointeurs vers alphebet[int]?
                
                //on ajoute dans l'arbre,
                //System.out.println("SIZE:"+n.children.size());
                return this.add(n,sub,o);
                //System.out.println("SIZE:"+n.children.size());
                
                
                //id = id-1;
                
                //System.out.println("WHAT APPENDED 2 ?:"+o+" VS "+n.children.get(id).toString());
                
                /*if(n.children.get(id) instanceof LinkedRadixNode){
                    //System.out.println("INSTANCE OF LINKED RADIX NODE:!!!");
                    if(n.children.get(id).getValue()!=null){
                        //System.out.println("NOT NULLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL!!!!!!");
                    }
                }*/
                //System.out.println("Last chars added");
                //return this;
            }
            else if(split>0){
                //on recupere la sous chaine
                //System.out.println(next + ":" + pos);
                //sub = Arrays.copyOfRange(chars, pos+1, chars.length);//id dans ce cas contient le nombre de caracteres maximaux qui ont matche avec l'index du noeud teste
                
                int start = (chars.length-test_test)+pos+1;
                sub = new Character[chars.length-start];
                count = 0;
                for(int z=start;z<chars.length;z++){
                    if(this.alphabet[chars[z]]==null){
                        this.alphabet[chars[z]] = chars[z];
                    }
                    //System.out.println("COUNT:"+(int)chars[z]);
                    sub[count] = this.alphabet[chars[z]];
                    ++count;
                }
                
                
                //on splite le noeud
                /*StringBuilder ss = new StringBuilder();
                for(int i=0;i<sub.length;i++){
                    ss.append(sub[i]);
                }
                System.out.println("SUB INDEX:"+ss);
                */
        
                return this.splitNode(n.children.get(id), sub, pos, o);//ATTENTION, on doit aussi renvoyer l'id du noeud dans tous les cas!!!!
                //System.out.println("NEXT SPLITED ID:"+id);
                //return this;
            }
            else{
                //besoin de rien faire...
                test_test -= pos+1;
                if(test_test == 0){
                    if(n.children.get(id).getValue() == null)
                        n.children.get(id).append(o);
                    else if(ow){
                        n.children.get(id).append(o);
                    }
                }
                /*
                test_test -= pos+1;
                if(ow && test_test<1){
                    //System.out.println("LA VALEUR EST DEJA PRESENTE, L'ECRASER?:"+n.index);
                    n.children.get(id).append(o);//La ca marche!!!!!
                    return n.children.get(id);
                }*/
            }
            n = n.children.get(id);
            remaining_chars = pos;
        }
        return n;
    }
    
    /**
     * 
     * @param t
     * @param string
     * @param o
     * @return 
     */
    public RadixNode<A> addString(String string, A o, boolean ow){
        //pour chaque element de la chaine, 
        char[] chars = string.toCharArray();
        return this.addString(chars, o);
    }
    
    /**
     * Dans l'ideal, il faudrait comparer caractere par caractere, afin de dire a quel moment les chaines sont differentes...
     * Il faut aussi s'assurer que le nombre de deplacements depuis le debut est inférieur a la taille de la chaine a chercher.
     * @param n
     * @param s
     * @return 
     */
    private int[] containsChildValue(RadixNode<A> n, char[] chars, int next, int other_index){
        int i = 0;
        int curr = next;//indique la position dans le tableau
        //int curr_next;//indique la position suivante
        //Character[] sub;
        //int id;
        //int l;
        int[] res = new int[4];
        int index_of_match;
        int m=n.children.size();
        int[] matches = new int[m];
        //System.out.println("CHILD:"+m);
        for(RadixNode node : n.children){
            
            index_of_match = -1;
            
            //System.out.println("je cherche si un des fils contient...");
            //System.out.println("i have children:"+node.children.size());
            Character[] ichars = node.index;
            //pour chque fils, on regarde sa longueur
            int l = ichars.length;//on recupere la longueur de l'index suivant.
            
            //curr_next = curr + l;
            //on recupere la partie suivante de la chaine qui correspond a cette longueur.
            //si le noeud courrant contient la valeur a rechercher
            /*while(curr<curr_next){
                sub += chars[curr];
                ++curr;
            }*/
            
            //sub = Arrays.copyOfRange(chars, curr, curr_next);
            //System.out.println("curr:"+curr+"vs "+other_index);
            //sub = new Character[chars.length-next];//on va jusqu'a l plutot ? l est la longueur du caractere a matcher.
            //replacement:
            Character[] sub = new Character[l];
            int count = 0;
            
            /**
             * Revoir ce qui suit...
             */
            
            //si l est plus grand que le nombre de caracteres a matcher, alors on va devoir spliter
            if(l>chars.length){
                l = chars.length;
            }
            if(l < chars.length){
                l = ichars.length;
            }
            int top = other_index+l;
            if(top>chars.length){
                //System.out.println("top exceeds bounds !!!");
                top = chars.length;
            }
            //for(int z=curr;z<chars.length;z++){
            for(int z=other_index;z<top;z++){
                if(this.alphabet[chars[z]]==null){
                    this.alphabet[chars[z]] = chars[z];
                }
                //System.out.println("COUNT:"+(int)chars[z]);
                sub[count] = this.alphabet[chars[z]];
                //System.out.println("item:"+sub[count]);
                ++count;
            }
            //System.out.println("l is:"+l+" and chars is "+sub.length);
                        
            //pour chaque caractere, tant que les caracteres sont identiques, on continue, des qu'il y a diff on breake la boucle
            for(int j=0;j<count;j++){
                //System.out.println("Matching:"+ichars[j]+" with "+sub[j]);
                //System.out.println("ichars["+j+"]:"+ichars[j]);
                if(Objects.equals(ichars[j], sub[j])){
                    //System.out.println("Matching:"+ichars[j]+"=="+sub[j]);
                    index_of_match++;
                }
                else{
                    break;
                }
            }
            //System.out.println("index of match:"+index_of_match+" vs "+ichars.length+" vs "+l);
            //ajoute la position de dernier matching (est ce que le -1 est necessaire ?)
            if(index_of_match==(ichars.length-1)){
                //c'est bon, pas la peine d'aller plus loin, tout est identique !
                res[0] = i;
                res[1] = curr;
                res[2] = 0;//pas besoin de split -- on a tout trouve
                res[3] = index_of_match;//tout matche
                //System.out.println("Perfect matching!!!:"+res[3]);
                return res;
            }
            else{
                matches[i] = index_of_match;//next+index_of_match
                //apparement ca marche mtn
                //System.out.println(next + " / " + index_of_match + " / " + matches[i]);
            }
            ++i;
        }
        
        //on regarde lequel des matches est le plus grand et on conserve celui la
        int max = -1;
        res[0] = -1;
        for(int j=0;j<m;j++){
            if(matches[j]>max){
                max = matches[j];
                res[0] = j;
                //System.out.println("Assigning res0:"+j);
            }
        }
        //res[0] = max;
        //System.out.println("res0:"+res[0]);
        res[1] = curr;//le caractere est encore le meme, vu qu'on n'a rien matche
        res[2] = 1;//besoin de split -- on n'en a trouve qu'une partie
        res[3] = max;
        return res;
    }
        
    public Object get(char[] chars, RadixNode<A> root){
        int[] res = new int[4];
        //int test_test = chars.length;
        int remaining_chars = chars.length;//nombre de caracteres restants a matcher
        RadixNode<A> n = root;
        
        while(remaining_chars>0){
            //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            //faire un containsChildValue
            //System.out.println("nextindex:"+(chars.length-remaining_chars));
            res = this.containsChildValue(n,chars,res[3], chars.length-remaining_chars);
            /*System.out.println("0:"+res[0]);
            System.out.println("1:"+res[1]);
            System.out.println("2:"+res[2]);
            System.out.println("3:"+res[3]);
            */
            //si oui, on continue et on decremente les index
            if(res[0]>-1&&res[2]!=1){
                n = n.children.get(res[0]);
                //System.out.println("found one");
            }
            //sinon, on renvoie null.
            else{
                //System.out.println("NOT FOUND!");
                return null;
            }
            remaining_chars -= res[3]+1;
            //System.out.println("Remaining chars :"+remaining_chars);
            //test_test -= res[1];
            //System.out.println("Res 1:"+res[1]);
            //System.out.println("Res 2:"+res[2]);
            //System.out.println("Res 3:"+res[3]);
        }
        if(n.getValue()!=null){
            return n.getValue();
        }
        else{
            return null;
        }
    }
    
    public RadixNode getNode(char[] chars, RadixNode<A> root){
        int[] res = new int[4];
        //int test_test = chars.length;
        int remaining_chars = chars.length;//nombre de caracteres restants a matcher
        RadixNode<A> n = root;
        
        while(remaining_chars>0){
            //WebWriter::write( '<h2>C-RC:',$c-$remaining_chars,'</h2>';
            res = this.containsChildValue(n,chars,res[3], chars.length-remaining_chars);
            if(res[0]>-1&&res[2]!=1){
                n = n.children.get(res[0]);
                //WebWriter::write( $res[0],'-',$res[1],'-',$res[3];
            }
            else{
                //WebWriter::write( 'Not found!!!!';
                return null;
            }
            remaining_chars -= res[3]+1;
            //WebWriter::write( 'Il reste '.$remaining_chars.' elements.';
        }
        //WebWriter::write( 'Found!';
        return n;
    }
    
    /**
     * 
     * @param s
     * @param root
     * @return 
     */
    public Object get(String s, RadixNode<A> root){
        char[] chars = s.toCharArray();
        return this.get(chars, root);
    }
    
    /**
     * 
     * @param s
     * @return 
     */
    public Object get(String s){
        return this.get(s, this.root);
    }
    
    public Object get(char[] chars){
        return this.get(chars, this.root);
    }
    
    /**
     * Permet de modifier un noeud directement
     * @param s
     * @param node 
     */
    public void set(String s, Object o){
        this.set(s, o, this.root);
    }
    
    /**
     * 
     * @param s
     * @param node
     * @param root 
     */
    public void set(String s, Object o, RadixNode<A> root){
        char[] chars = s.toCharArray();
        //int id=0;
        int[] res = new int[4];
        int test_test = chars.length;
        int remaining_chars = chars.length;//nombre de caracteres restants a matcher
        RadixNode<A> n = root;
        
        while(remaining_chars>0){
        //while(remaining_chars>0){
            //faire un containsChildValue
            res = this.containsChildValue(n,chars, res[3], chars.length-remaining_chars);
            /*System.out.println("0:"+res[0]);
            System.out.println("1:"+res[1]);
            System.out.println("2:"+res[2]);
            System.out.println("3:"+res[3]);
            */
            //si oui, on continue et on decremente les index
            if(res[0]>-1&&res[2]!=1){
                n = n.children.get(res[0]);
            }
            //sinon, on renvoie null.
            else{
                //System.out.println("NOT FOUND!");
                //return null;
                break;
            }
            remaining_chars -= res[3]+1;
            //System.out.println("Remaining chars :"+remaining_chars);
            //test_test -= res[1];
        }
        n.setValue((A) o);
    }
    
    /**
     * 
     * @param root 
     */
    public void showTrie(RadixNode<A> root){
        
        /*Iterator it = root.children.iterator();
        // tant qu’il un élément suivant dans la collection 
        while(it.hasNext())
        {
            RadixTree.SimpleRadixNode n = (RadixTree.SimpleRadixNode) it.next();
            StringBuilder s = new StringBuilder();
            for(int i=0;i<n.index.length;i++){
                s.append(n.index[i]);
            }
            System.out.println("-->"+s);
            if(!n.empty()){
                if(this.DEFAULT_NODE==RadixTree.SIMPLE_NODES){
                    System.out.println("VAL:"+n.getValue());
                }
                else{
                    System.out.println("VAL:"+n.getValue().toString());
                }
            }
            if(n!=null){
                this.showTrie(n);
                //System.out.println("N.SIZE:"+n.children.get(0).value.toString());
            }
        }*/
        
        
        for(RadixTree.RadixNode n : root.children){
            StringBuilder s = new StringBuilder();
            for(int i=0;i<n.index.length;i++){
                s.append(n.index[i]);
            }
            //System.out.println("-->"+n.getKey()+"("+n.getIndex()+")");
            //System.out.println(n.parent);
            if(n.getValue()!=null){
                if(this.DEFAULT_NODE==RadixTree.LINKED_NODES){
                    //System.out.println("VAL:"+((LinkedRadixNode)n).toString());
                }else{
                     //System.out.println("VAL:"+n.getValue().toString());
                }
            }
            if(n!=null){
                this.showTrie(n);
                //System.out.println("N.SIZE:"+n.children.get(0).value.toString());
            }
        }
    }
    
    /************************************************************
     *  CLASSES INTERNES
     */
    
    /**
     * Represente un element de l'arbre radix. Tenter la variante HAT-TRIE ?
     */
    public final class SimpleRadixNode<A> extends RadixNode{
        public A value;
        
        public SimpleRadixNode(){
            super();
        }

        @Override
        public void append(Object item) {
            this.value = (A) item;
        }

        @Override
        public boolean empty() {
            if(this.value!=null){
                return true;
            }
            return false;
        }

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        public void clear() {
            this.value = null;
        }

        @Override
        public Object duplicateValue() {
            return this.value;
        }

        @Override
        public void setValue(Object item) {
            this.value = (A) item;
        }
    }
    
    /**
     * Classe abstraite de noeud du RadixTree
     * @param <A> 
     */
    public abstract class RadixNode<A> implements Cloneable{
        public Character[] index;
        public ArrayList<RadixNode<A>> children = new ArrayList<>();
        //est ce que ce serait judicieux de remplacer le tableau de chars par un tableau de byte ?
        //representer les fils avec un arbre de recherche ? table de hash ? arbre binaire ?
        public int n;//peut etre un byte etant donne que l'on ne peut avoir que 26 + special chars elements directs.
        public RadixNode parent = null;
        public char id;//indentifiant qui va servir pour la comparaison
        //private A value;
        
        public RadixNode(){
            this.id = ++RadixTree._ID_;
        }
        
        /**
         * Renvoie le morceau d'index propre a l'element courant
         * @return 
         */
        public String getIndex(){
            StringBuilder s = new StringBuilder();
            if(this.index==null){
                return s.toString();
            }
            for(int i=0;i<this.index.length;i++){
                s.append(index[i]);
            }
            return s.toString();
        }
        
        /**
         * Renvoie la chaine de caracteres complete de l'element courant
         * @param with_separators
         * @return 
         */
        public String getKey(boolean... with_separators){
            StringBuilder s;
            ArrayList<String> full_sequence = new ArrayList<>();
            
            RadixNode node = this.parent;
            while(node!=null){                
                
                //s.append("(").append(node.getIndex()).append(")");
                if(with_separators!=null && with_separators.length!=0 && with_separators[0]){
                    s = new StringBuilder();
                    s.append("(").append(node.getIndex()).append(")");
                     full_sequence.add(s.toString());//on ajoute le morceau a la sequence
                }
                else{
                    full_sequence.add(node.getIndex());//on ajoute le morceau a la sequence
                }
                node = node.parent;
            }
            s= new StringBuilder();
            for(int i=full_sequence.size()-1;i>-1;i--){
                s.append(full_sequence.get(i));
            }
            s.append(this.getIndex());
            return s.toString();
        }
        
        @Override
        public RadixNode<A> clone() {
            RadixNode<A> o = null;
            try {
                // On récupère l'instance à renvoyer par l'appel de la 
                // méthode super.clone()
                o = (RadixNode<A>)super.clone();
            } catch(CloneNotSupportedException cnse) {
                // Ne devrait jamais arriver car nous implémentons 
                // l'interface Cloneable
                cnse.printStackTrace(System.err);
            }
            // on renvoie le clone
            return o;
	}
        
        /**
         * 
         */
        public abstract void append(A item);
        public abstract boolean empty();
        public abstract A getValue();
        public abstract void clear();
        public abstract A duplicateValue();
        public abstract void setValue(A item);
    }
    
    /**
     * Noeud particulier permettant de maintenir des listes chainees dans chaque index
     * @param <A> 
     */
    public final class LinkedRadixNode<A> extends RadixNode implements Iterable{
        public AnchoredLinkedList<A> value = null; //= new LinkedList<>();

        public LinkedRadixNode(){
            super();
        }
        
        public void addNext(A item){
            if(!this.empty()){
                //Avant, on avait addLast mais on a change a cause de l'heritage, qui demande de se placer en fin de liste chainee 
                this.value.addFirst(item);
                //System.out.println("LINKED LIST NOT NULL");
            }
            else{
                this.value = new AnchoredLinkedList<>(item);
                //System.out.println("NEW LINKED LIST CREATED");
            }
        }
        
        
        @Override
        public String toString(){
            if(this.value!=null){
                this.value.reset();
            }
            StringBuilder b = new StringBuilder("Values:");
            Iterator it = this.iterator();
            do{
                LinkedList.LinkedNode ll = (LinkedList.LinkedNode)it.next();
                b.append(",");
                //b.append(this.value.curr().value.toString());
                if(ll!=null){
                    b.append(ll.toString());
                }
            }
            while(it.hasNext());
            return b.toString();
        }

        @Override
        public void append(Object item) {
            if(item!=null){
                this.addNext((A)item);
                //System.out.println("ITEM ADDED:"+this.value.lastChild.value.toString());
            }
        }

        @Override
        public boolean empty() {
            if(this.value!=null&&this.value.size>0){
                return false;
            }
            return true;
        }        

        @Override
        public Object getValue() {
            return this.value;
        }
        
        @Override
        public LinkedRadixNodeIterator iterator(){
            return new LinkedRadixNodeIterator(this);
        }

        @Override
        public void clear() {
            this.value = null;
        }

        @Override
        public A duplicateValue() {
            return (A) this.value.clone();
        }

        @Override
        public void setValue(Object item) {
            this.value = new AnchoredLinkedList<A>((A) item);
        }
        
        /**
         * Iterateur sur les noeuds
         */
        public class LinkedRadixNodeIterator implements Iterator<LinkedList.LinkedNode>{
            private LinkedRadixNode collection=null;
            public LinkedRadixNodeIterator(LinkedRadixNode m){
                collection=m;  
            }
            
            public boolean isEmpty(){
                if(this.collection!=null){
                    return false;
                }return true;
            }

            @Override
            public boolean hasNext(){
                return collection.value.hasNext();
            }
            @Override
            public LinkedList.LinkedNode next(){
                return collection.value.curr();
            }

            @Override
            public void remove(){
                throw new UnsupportedOperationException();
            }
        }   
    }
}
