/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ontologyrep2;

import java.util.ArrayList;

/**
 * 
 * @author Enridestroy
 */
public final class Trie {
    public Node root = null;
    
    public Trie(){
        this.root = new Node();
    }
    
    /**
     * 
     * @param s
     * @param o
     * @return 
     */
    public Object createTrie(String s, Object o){
        if(s!=null){
            return this.addString(new Trie(), s, o);
        }
        else{
            return new Trie();
        }
    }
    
    /**
     * 
     * @param s
     * @param root
     * @return 
     */
    public Object get(String s, Node root){
        char[] chars = s.toCharArray();
        int id=0;
        Node n = root;
        for(char c : chars){
            while(id!=-1){
                id = Trie.containsChildValue(n,c);
                if(id>-1){
                    n = n.children.get(id);
                    break;
                }
                else{
                    return null;
                }
            }
        }
        return n.value;
    }
    
    
    /**
     * 
     * @param n
     * @param o
     * @return 
     */
    public int add(Node n, char c, Object o){
        //creation du noeud
        Trie.Node node = new Trie.Node();
        node.index = c;
        node.value = o;
        //ajout du noeud
        n.children.add(node);
        ++n.n;
        return (n.n-1);
    }
    
    public Trie addString(Trie t,String string, Object o){
        Node n = t.root;//on recupere la racine
        //pour chaque element de la chaine, 
        char[] chars = string.toCharArray();
        int max = chars.length-1;
        for(int i=0;i<max;i++){
            //for(char c : chars){
            //si le noeud courrant contient la valeur a rechercher
            int id = Trie.containsChildValue(n,chars[i]);
            //System.out.println(id);
            if(id<0){
                id=this.add(n,chars[i],null);
                //System.out.println(id);
            }
            n = n.children.get(id);
        }
        //si on a place toutes les lettres, 
        this.add(n,chars[max],o);
        return t;
    }
    
    /**
     * 
     * @param n
     * @param s
     * @return 
     */
    public static int containsChildValue(Node n, char s){
        int i = 0;
        for(Node node : n.children){
            if(node.index == s) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    public void showTrie(Trie.Node root){
        for(Trie.Node n : root.children){
            //System.out.println("-->"+n.index);
            if(n.value!=null){
                //System.out.println("VAL:"+n.value);
            }
            if(n!=null){
                this.showTrie(n);
            }
        }
    }
    
    /**
     * Represente un noeud de l'arbre prefixe
     */
    public final class Node{
        public ArrayList<Node> children = new ArrayList<>();
        public int n;
        public Object value = null;
        public char index;
        
    }
}
