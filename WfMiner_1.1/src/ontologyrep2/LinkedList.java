/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ontologyrep2;

/**
 *
 * @author Enridestroy
 */
public class LinkedList<A> implements Cloneable{
    public LinkedNode curr;
    //public LinkedNode<A> root;
    public LinkedNode firstChild;
    public LinkedNode lastChild;
    public int size = 0;
    
    public LinkedList(){
        
    }
    
    public LinkedList(A object){
        this.firstChild = new LinkedNode<>(object);
        this.lastChild = this.firstChild;
        this.reset();
        this.size++;
    }
    
    /*public LinkedList(LinkedNode node){
        this.firstChild = node;
    }*/
    
    public LinkedList(LinkedList list){
        if(list!=null){
            this.firstChild = list.firstChild;
            this.lastChild = list.lastChild;
            this.reset();
            this.size = list.size;
            //System.out.println("LinkedList copy :"+this.size);
        }
        else{
            //System.out.println("LIST is NULL, cannot copy.");
            this.size = 0;
        }
    }
    
    /**
     * Ajoute un element en premier (pile)
     * @param object 
     */
    public LinkedNode<A> addFirst(A object){        
        LinkedNode n = new LinkedNode<>(object);    
        n.next = this.firstChild;
        n.prev = this.firstChild.prev;
        this.firstChild.prev = n;
        this.firstChild = n;
        this.size++;
        return n;
    }
    
    @Override
    public Object clone() {
        Object o = null;
        try {
            // On récupère l'instance à renvoyer par l'appel de la 
            // méthode super.clone()
            o = super.clone();
        } catch(CloneNotSupportedException cnse) {
            // Ne devrait jamais arriver car nous implémentons 
            // l'interface Cloneable
            cnse.printStackTrace(System.err);
        }
        // on renvoie le clone
        return o;
    }
    
    /**
     * Ajoute un element a la fin (file)
     * @param object 
     */
    public LinkedNode<A> addLast(A object){
        LinkedNode l = new LinkedNode<>(object);
        l.next = null;
        l.prev = this.lastChild;
        
        //sinon, on cree un nouvel objet temp
        this.lastChild.next = l;
        
        this.lastChild = l;
        this.size++;
        return l;
    }
    
    /**
     * Supprime un element de la liste
     * @param l 
     */
    public void remove(LinkedNode l){
        if(l==null){
            System.out.println("l is null.");
            return;
        }
        if(l.prev!=null){
            System.out.println("l.prev not null");
            l.prev.next = l.next;
        }
        if(l.next!=null){
            System.out.println("l.next not null");
            l.next.prev = l.prev;  
        }
        this.size--;
    }
    
    /**
     * 
     * @param l
     * @param sens 
     */
    public LinkedNode<A> remove(LinkedNode l, boolean sens){
        //si le noeud a enlever est le noeud courrant
        System.out.println("we have "+this.size+" items.");
        if(l == this.curr){
            System.out.println(" is current !!!!");
            
            if(!this._hasNext()){
                System.out.println(" and is last node !!!");
                //this.lastChild = null;
                //this.firstChild = null;
                this.curr = this.curr.prev;
                
                if(l == this.lastChild){
                    this.lastChild = null;
                }
                if(l == this.firstChild){
                    this.firstChild = null;
                }
                
            }
            else{
               this.curr = this.curr.prev;
            }
            //this.curr = this.curr.prev;
        }
        this.remove(l);//on enleve l'item demande
        return this.curr;
    }
    
    /**
     * Renvoie l'element courrant
     * @return 
     */
    public LinkedNode<A> curr(){
        return this.curr;
    }
    
    /**
     * Va au prochain element de la liste
     * @return 
     */
    public LinkedNode<A> next(){
        this.curr = this.curr.next;
        return this.curr;
    }
    
    /**
     * Remet le curseur sur le premier element
     */
    public void reset(){
        this.curr = this.firstChild;
    }
    
    /**
     * Recherche si il existe un prochain element
     * @return 
     */
    public boolean hasNext(){
        if(this.curr!=null&&this.curr.next!=null){
            this.next();
            return true;
        }
        return false;
    }
    
    public boolean _hasNext(){
        if(this.curr!=null&&this.curr.next!=null){
            //this.next();
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @param a
     * @param b
     * @return 
     */
    public static LinkedList merge(LinkedList a, LinkedList b){
        if(a!=null&&b==null){
            //System.out.println("List B is null.");
            return a;
        }
        if(a==null&&b!=null){
            //System.out.println("List A is null.");
            a = new LinkedList<>(b);
            //System.out.println("ASIZE:"+a.size);
            return a;
        }
        
        if(a!=null&&b!=null&&b.firstChild!=null){
            //manque a peaufiner mais l'idee de base y est
            a.lastChild.next = b.firstChild;
            a.lastChild = b.lastChild;
            a.size += b.size;
            a.reset();
            return a;
        }
        /*if(a.lastChild!=null){
            //b.addFirst(a.lastChild);
        }*/
        return a;
    }
    
    public final class LinkedNode<A>{
        public LinkedNode prev;
        public LinkedNode next;
        public A value;
        
        public LinkedNode(A value){
            this.value = value;
        }
        
        @Override
        public String toString(){
            if(this.value!=null){
                return value.toString();
            }
            else{
                return "";
            }
        }
    }
}
