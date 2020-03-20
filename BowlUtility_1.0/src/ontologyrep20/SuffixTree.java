package ontologyrep20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Enridestroy
 * 
 * ameliorations:
 * tout contenir dans une hashtable.
 * chaque item dans la hashtable correspond a une donnee specifique. chaque donnee contient une ref vers chaque noeud qui la contient.
 * quand on fait une recherche sur une valeur donnee, on n'a qu'a faire une recherche sur la hashtable et on recupere une des "instances" de la valeur
 * quand on fait une recherche d'index, hashtable ? b-tree ? tester les deux. voir quel est le meilleur
 * 
 */
public final class SuffixTree<A> {
    //remplacer root par un noeud unique -> getChildrenValues() !!
    public ArrayList<Node> root = new ArrayList<>();
    //permet de contenir tous les noeuds avec un index assigne
    //l'index va de 0 a 65
    public HashMap<Integer, Object> alphabet = new HashMap<>();
    public Node suffix_link = null;
    public char n_strings_inserted = 0;
    public static int ID = 0;
    //Liste de references vers les elements dont le parent = null
    //public ArrayList<Node> root_concepts = new ArrayList<>();
    
    public SuffixTree(){
        
        
    }
    
    public void createTree(){
        //pas besoin de creer de premier noeud fils.
    }
    
    /**
     * 
     * @param o
     * @return 
     */
    public boolean treeContainsValue(Object o){
        return this.alphabet.containsValue(o);
    }
    
    /**
     * 
     * @param index
     * @return 
     */
    public boolean treeContainsKey(Character index){
        return this.alphabet.containsKey(index);
    }
    
    /**
     * 
     * @param i
     * @param val
     * @throws Exception 
     */
    public void addItem(Character i, A val) throws Exception{
        Integer ii = (int)i;
        this.addItem(ii, val);
    }
    
    /**
     * 
     * @param i
     * @param val
     * @throws Exception 
     */
    public void addItem(Integer i, A val) throws Exception{
        boolean found=false;//flag qui indique si ya matching ou pas
        int j=0;
        Node curr2=null;
        //pourquoi ?, j est toujours = 0, donc toujours inferieur ou egal a la taille de this.root ?
        if(j<this.root.size()) {
            curr2 = this.root.get(j);
        }
        while(curr2!=null){//tant qu'on a des elements suivants
            if(curr2.index==i){//si l'element courrant correspond a l'index du nouvel element, 
                found=true;
                if((curr2.index)>-1){
                    //System.out.println("Found:"+curr2.index+"<br/>");
                }
                else{
                    //System.out.println("No index for $curr2(1)<br/>");
                }
                break;//on casse la boucle et on conserve l'element courrant
            }
            if((curr2.index)>-1){
                //System.out.println( "Not found:"+curr2.index+"!="+i+" go to next node<br/>" );
            }
            else {
                //System.out.println("No index for $curr2(2)<br/>");
            }
            j++;
            if(j<this.root.size()) {
                curr2 = this.root.get(j);
            }
            else {
                curr2 = null;
            }
        }
        //si oui, on va a l'element suivant
        if(found){
            //$curr = $curr2->firstSon;//on va au suivant
            if(this.suffix_link!=null){
                Node test = this.suffix_link.firstSon;
                boolean contains = false;
                //avant on avait un while : pourquoi ?
                while(test!=null){
                    if(test==curr2){
                        contains = true;
                        break;
                    }
                    else{
                        test=test.next;
                    }
                }
                //alors le parent ne contient pas l'item actuel, on doit donc creer la relation
                if(!contains){
                    this.addChild(this.suffix_link,curr2);//on cree le lien de filiation
                }
            }
            this.suffix_link = curr2;//on conserve la reference de l'element qui matche.
            //System.out.println("Recuperation du lien suffixe...:"+this.suffix_link.index+"<br/>");
            //$curr->suffix_link = $tsl;//on insere le lien suffixe
        }
        else{
            //System.out.println("Not found<br/>");
            //on ajoute a la racine
            Node createElement = this.createElement(i,val);
             this.root.add(createElement);
             
             this.alphabet.put(i, createElement);//attention faire un choix entre les deux
             
             Node ref = this.root.get(this.root.size()-1);
             //System.out.println("Node added:"+ref.index);//' to ',$this->root->index,'';
             if(this.suffix_link!=null){
                 //System.out.println("("+this.suffix_link.index+")");
             }
             //System.out.println("<br/>");

             //on ajoute le lien du dernier element cree vers le lien suffixe. autrement dit celui cree avant ou matché avant                   
             ref.suffix_links.add(this.suffix_link);//on cree le lien 15 -> 17

             //si le lien suffixe est different de null
             //en fait, il l'est toujours, la question c'est estce qu'il est différent de la racine?
             if(this.suffix_link!=null){
                 //System.out.println("Suffix link exist:"+this.suffix_link.index+"<br/>");
                 //System.out.println("Ajout de relation de parente entre "+this.suffix_link.index+" et le noeud recement cree:"+ref.index+"<br/>");
                 if(this.suffix_link==ref) {
                     throw new Exception("Meme noeuds!!!!!");
                 }
                 this.addChild(this.suffix_link,ref);//on cree le lien 17->15
                 //System.out.println("Child added:"+ref.index+" to "+this.suffix_link.index+"<br/>");
             }
             else{
                 //System.out.println("PAS DE LIEN SUFFIXE!!!!<br/>");
             }
             //modification du lien suffixe. on prend le dernier element cree
             this.suffix_link = ref;//le lien suffixe est maintenant 15
             //System.out.println("modif du lien suffixe:"+this.suffix_link.index+"<br/>");
        }
    }
    
    /**
     * 
     * @param indexes
     * @param val 
     */
    public void addIndexes(ArrayList<Character> indexes, A val) throws Exception{
        this.suffix_link = null;
        //si on est en mode integer, alors les caracteres de fin de chaine sont -1, -2 etc...
        //indexes.add((++this.n_strings_inserted)*-1);//on fait +1 car -0 n'existe pas...
        //indexes.add(this.n_strings_inserted);
        for(char i : indexes){
            this.addItem(i, val);
        }
        //quel est le but de ca ?
        //this.addItem(this.n_strings_inserted, null);
        ++this.n_strings_inserted;//on cree un caractere de fin
        //return this.suffix_link;//renvoie le dernier lien
        
    }
    
    public void addNumericIndexes(ArrayList<Integer> indexes, A val) throws Exception{
        this.suffix_link = null;
        //si on est en mode integer, alors les caracteres de fin de chaine sont -1, -2 etc...
        //indexes.add((++this.n_strings_inserted)*-1);//on fait +1 car -0 n'existe pas...
        //indexes.add(this.n_strings_inserted);
        for(int i : indexes){
            this.addItem(i, val);
        }
        //quel est le but de ca ?
        //this.addItem(this.n_strings_inserted, null);
        ++this.n_strings_inserted;//on cree un caractere de fin
        //return this.suffix_link;//renvoie le dernier lien
        
    }
    
    /**
     * 
     * @param string
     * @param val
     * @throws Exception 
     */
    public void addString(String string, A val) throws Exception{
        this.suffix_link = null;
        //string += "$" + this.n_strings_inserted;//on cree un caractere de fin
        char[] indexes = string.toCharArray();
        for(char c : indexes){//on itere le suffixe
            this.addItem(c, val);
        }
        ++this.n_strings_inserted;//on cree un caractere de fin
        //this.suffix_link = null;
    }
    
    /**
     * 
     */
    public void showTree(){
        if(this.root==null){
            return;
        }
        Node node = this.root.get(0);
        //System.out.println("size:"+this.root.size());
        while(node!=null){
            //System.out.println("-"+node.index+"----<br/>");
            if(node.hasSons()){
               //System.out.println("HASSONS!<br/>");
               this.showNodes(node); 
            }
            node=node.next;
        }
    }
    
    /**
     * 
     */
    public void showNodes(Node node){
        if(node==null){
            return;
        }
        node=node.firstSon;
        while(node!=null){
            //System.out.println("-->"+node.index+"----<br/>");
            if(node.hasSons()){
                //System.out.println("HASSONS!<br/>");
                this.showNodes(node);
            }
            node=node.next;  
        }
    }
    
    /**
     * Cette methode via decomposer un string en array et decouper en paires de valeurs
     * @param type $string
     */
    /*
    public void addBigString($string){
        if(!is_array($string))$string = str_split($string,1);//decoupe en un tableau
        $this->addString($string);
        
    }*/
    
    /**
     * 
     */
    public Node createElement(int index, A val){
        //on cree un nouveau noeud
        Node<A> node = new Node<>(index,val);
        //System.out.println("Node created:"+node.index+"<br/>");
        return node;
    }
        
    /**
     * Ajoute un fils en fin de liste
     * Il faut ajouter les verifs de null
     */
    public Node addChild(Node p, Node n){
        //System.out.println(""+p.index+"has "+p.computeNbrSons()+" nodes!<br/>");
        Node s=p.firstSon;
        while(s!=null){
            //System.out.println("->"+s.index+"<br/>");
            s=s.next;
        }
        //echo 'AND LASTSON:',$p->lastSon->index,'<br/>';
        if(p.n==0){
            //mise a jour du premier fils
            //System.out.println("Updating first son.<br/>");
            p.firstSon = n;
            //p.currnode = p.firstSon;
        }else{
            
            //on cree la reference du dernier fils au nouveau dernier
            p.lastSon.next = n;
            //on cree la reference du nouveau dernier fils a l'ancier dernier
            n.prev = p.lastSon;
            //System.out.println(p.lastSon.index);
            //System.out.println("Updating new son.<br/>");
        }
        //on cree la reference du nouveau fils vers son parent
        if(n.parent==null||(p.depth>n.parent.depth)){
            n.parent = p;
        }
        //On ajoute ce parent a la liste des parents possibles
        n.parents.add(p);
        
        n.depth = (char) (p.depth+1);
        //en gros, on lie la liste des parents de n a la liste de parents de p
        //n._parents.bottom_anchor.addLast(p._parents);
        
        //System.out.println("Node added.");
        //System.out.println(""+p.index+"has now "+p.computeNbrSons()+" nodes!<br/>");
        //lien du dernier fils
        p.lastSon = n;
        //on incremente le nombre de fils
        ++p.n;
        //$this->updateCycle();
        //System.out.println("Node added.");
        //System.out.println(""+p.index+"has now "+p.computeNbrSons()+" nodes!<br/>");
        s=p.firstSon;
        while(s!=null){
            //System.out.println("->"+s.index+"<br/>");
            s=s.next;
        }
        //System.out.println("AND LASTSON:"+p.lastSon.index+"<br/>");
        return n;
    }
    
    /**
     * 
     * @param node
     * @return 
     */
    public ArrayList<Node> getSons(Node<A> node){
        //on prend l'element a la racine et on recupere tous ses fils directs
        ArrayList<Node> sons=new ArrayList<>();
        if(node!=null){
            node=node.firstSon;
            while(node!=null){
                sons.add(node);
                node=node.next;
            }
        }
        return sons;
    }
    
    /**
     * 
     * @param node
     * @return 
     */
    public ArrayList<A> getSonsValues(Node<A> node){
        //on prend l'element a la racine et on recupere tous ses fils directs
        ArrayList<A> values = new ArrayList<>();
        if(node!=null){
            node=node.firstSon;
            while(node!=null){
                values.add(node.value);
                node=node.next;
            }
        }
        //System.out.println("nbr of sons:"+values.size());
        return values;
    }
    
    /**
     * 
     * @param index
     * @return 
     */
    public Node get(ArrayList<Integer> index){
        //on doit chercher dans la racine puis on renvoie le noeud?
        //pour chaque item dans la liste, 
        //on prend la racine et on cherche.
        Iterator it = index.iterator();
        Node n = this.root.get(0);
        while(it.hasNext()){
            n = this.get((Integer)it.next(), n, true);
        }
        return n;
    }
    
    /**
     * 
     * @param index
     * @param n
     * @return 
     */
    public Node get(int index, Node n, boolean usehash){
        if(usehash){
            Object get = this.alphabet.get(index);
            if(get!=null){
                return (Node)get;
            }
            else{
                return null;
            }
        }else{
            //on doit prendre le firstSon. puis tant que ca matche pas, on fait firstSon.next
            n=n.firstSon;
            while(n!=null){
                if(n.index==index){
                    return n;
                }
                n=n.next;
            }
            //est ce que la racine est en fait l'alphabet ou une vraie racine?
            return null;
        }
    }
    
    /**
     * Renvoie tous les ancetres d'un noeud
     * @param type $n
     * @return type
     */
    public ArrayList<Node> getParents(Node<A> node){
        //remonter les liens suffixes jusqu'a vide -> on est le plus loin dans la chaine,
        //remonter jusqu'a la racine->on a le chemin complet.
        //on fait ca pour tous les liens suffixes.
        ArrayList<Node> p = new ArrayList<>();//tableau des parents
        for(Node<A> s : node.suffix_links){
            if(node.suffix_links.size()>0){
                p.addAll(this.getParents(s));//on recupere les parents du noeud cible
            }
            else{
                //tant que l'on peut remonter dans la hierarchie, 
                while(node!=null){
                    //on remonte
                    node=node.parent;
                    //on ajoute dans un tableau
                    p.add(node);
                }
                return p;
            }
        }
        //enleve les elements qui se recoupent. surement moyen de faire mieux mais bon la osef
        //array_unique($p);
        return p;
    }
    
    /**
     * Il va falloir refaire cette methode. Elle ne fonctionne pas.
     * @param node
     * @return 
     */
    public ArrayList<A> getParentValues(Node<A> node){
        if(node==null){
            //System.out.println("Node is null.");
            return null;
        }
        ArrayList<A> p = new ArrayList<>();//tableau des parents
        node = node.parent;
        while(node!=null){
            //System.out.println("On remonte dans la hierarchie.");
            //on ajoute dans un tableau
            p.add(node.value);
            //on remonte
            node=node.parent;
        }
        return p;
        /*
        //remonter les liens suffixes jusqu'a vide -> on est le plus loin dans la chaine,
        //remonter jusqu'a la racine->on a le chemin complet.
        //on fait ca pour tous les liens suffixes.
        ArrayList<A> p = new ArrayList<>();//tableau des parents
        for(Node<A> s : node.suffix_links){
            if(node.suffix_links.size()>0){
                System.out.println("There are suffix links.");
                ArrayList<A> ret = this.getParentValues(s);
                if(ret!=null){
                    System.out.println("Ajout");
                    p.addAll(ret);//on recupere les parents du noeud cible
                }
            }
            else{
                System.out.println("On essaie de remonter la hierarchie.");
                //tant que l'on peut remonter dans la hierarchie, 
                node = node.parent;
                while(node!=null){
                    System.out.println("On remonte dans la hierarchie.");
                    //on ajoute dans un tableau
                    p.add(node.value);
                    //on remonte
                    node=node.parent;
                }
                return p;
            }
        }
        //enleve les elements qui se recoupent. surement moyen de faire mieux mais bon la osef
        //array_unique($p);
        return p;*/
    }
    
    
    public boolean hasParents(){
        //si l'element a un lien suffixe, alors oui, il a au moins un parent, 
        return true;
    }
    
    public int repetitions(String string){
        //on prend un element a la racine. on compte ses fils.
        return 0;
    }
    
    
    /**
     * Contient la valeur de la donnée
     * Contient les references de toutes les instances
     * @param <A> 
     */
    public final class NodeClass<A>{
        
    }
    
    /**
     * Contient toutes les infos specifiques a un noeud : 
     * -index
     * -ref vers la donnee (classe)
     * -parent canonique
     * -parents
     * -fils (firsSon)
     * 
     * @param <A> 
     */
    public final class NodeInstance<A>{
        
    }
    
    /**
     * Noeud de l'arbre des suffixes
     */
    public final class Node<A>{
        public int id;//identifiant unique du noeud
        //liens suffixes ? tableau de listes chainees? parce que si un element n'est present qu'une seule fois, il ne peut faire reference a d'autres que lui meme.
        public ArrayList<Node> suffix_links = new ArrayList<>();
        public int index = 0;//index
        public A value = null;//valeur indexee
        //public $children = array();
        public Node parent = null;//represente le parent canonique
        public ArrayList<Node> parents = new ArrayList<>();
        public AnchoredLinkedList<Node> _parents = null;
        public Node next = null;
        public Node prev = null;
        public Node firstSon = null;
        public Node lastSon = null;
        public Node top = null;//parent de toute la hierarchie
        public int n=0;
        public char depth = 0;//indique la profondeur d'un noeud, permet d'acceler le calcul des ancetres
        //complexite: autant de noeuds separant les deux items. Si fils direct, une seule operation.
        //ou alors, pire que ca, chaque noeud contient ses ancetres : consomation de memeoire abusee...
        
        
        public Node(int index, A val){
            this.index = index;
            this.value = val;
            this.id = ++SuffixTree.ID;
        }
        
        /**
         * 
         * @return 
         */
        public int computeNbrSons(){
            int i=0;
            Node node=this.firstSon;
            while(node!=null){
                ++i;
                node=node.next;
            }
            return i;
        }

        /**
         * 
         * @return 
         */
        public boolean hasSons(){
            //on prend l'element a la racine et si firstSon != null, oui, il a des enfants
            if(this.firstSon!=null) {
                return true;
            }
            else {
                return false;
            }
        }
    }
}
