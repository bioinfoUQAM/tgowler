package ontologyrep2;

/**
 *
 * @author Enridestroy
 * @param <A>
 */
public final class AnchoredLinkedList<A> extends LinkedList{
    //ancre superieure (avant firstChild)
    public LinkedList top_anchor = null;
    //anre inferieure (apres lastChild)
    public LinkedList bottom_anchor = null;
    
    public AnchoredLinkedList(){
        super();
    }
        
    public AnchoredLinkedList(A object){
        super(object);
    }
    
    public AnchoredLinkedList(LinkedList list){
        super(list);
    }
    
    public AnchoredLinkedList(AnchoredLinkedList alist){
        super((Object)alist);
    }
    
    /**
     * 
     */
    @Override
    public void reset(){
        super.reset();
        if(this.bottom_anchor!=null){
            this.bottom_anchor.reset();
        }
        if(this.top_anchor!=null){
            this.top_anchor.reset();
        }
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public boolean hasNext(){
        if(this.curr!=null){    
            if(this.curr.next!=null){
                //System.out.println("Normal next...");
                this.next();
                return true;
            }
            //Dans le cas ou il n'y a plus aucun element dans la liste, on regarde l'ancre et on itere dessus
            else if(this.bottom_anchor!=null&&this.bottom_anchor.size>0){
                //System.out.println("Trying to use anchors..."+this.bottom_anchor.size+((LinkedList)this.bottom_anchor.curr.value).size);
                //Si la liste chainee actuelle dans l'ancre n'est pas nulle
                //alors curr = liste chainee actuelle dans l'ancre . next
                
                if(this.bottom_anchor.curr == null) return false;
                
                final LinkedList l = (LinkedList)this.bottom_anchor.curr.value;
                if(l==null) {
                    return false;
                }
                
                
                if(l.curr!=null){
                //if(this.bottom_anchor.hasNext()){  
                    //System.out.println("Doing something");
                    this.curr = l.firstChild;//on considere le premier fil
                    //l.next();
                    this.bottom_anchor.next();
                    //System.out.println("Goto anchor:"+((LinkedList)this.bottom_anchor.curr.value).curr);
                    return true;
                }
                //la sous liste n'a plus d'items
                else if(l.curr==null){
                    //System.out.println("la sous liste n'a plus d'items.");
                    l.reset();//on remet la liste a zero pour plus tard...
                    this.bottom_anchor.next();
                    LinkedList ll = (LinkedList)this.bottom_anchor.curr.value;
                    if(ll!= null){
                        ll.reset();
                        if(ll.firstChild!=null){
                            this.curr = ll.firstChild;
                            return true;
                        }
                        else{
                            this.bottom_anchor.next();
                        }
                    }
                    //System.out.println("Goto2 anchor:"+this.bottom_anchor.curr);
                }
            }
        }
        return false;
    }
    
    /**
     * Est ce que l'on a besoin de surcharger la methode ? Apparement oui.
     * Revoir la methode. Essayer de la faire bugger.
     * @param a
     * @param b
     * @return 
     */
    public static AnchoredLinkedList merge(AnchoredLinkedList a, AnchoredLinkedList b){        
        if(a!=null&&b==null){
            //System.out.println("List B is null.");
            
            /*if(a.bottom_anchor!=null){
                //Avant, on avait addLast mais on a change a cause de l'heritage, qui demande de se placer en fin de liste chainee 
                a.bottom_anchor.addLast(b);
                //System.out.println("ANCHOR ADDED");
            }
            else{
                //this.value = new AnchoredLinkedList<>(item);
                a.bottom_anchor = new AnchoredLinkedList<>(b);
                //System.out.println("ANCHOR LIST CREATED");
            }*/
            return a;
            
            //System.out.println("A.SIZE:"+a.size);
            
            //return a;
        }
        else if(a==null&&b!=null){
            //System.out.println("List A is null.");
            //System.out.println("B.SIZE:"+b.size);
            a = b;
            return a;
            /*
            a = new AnchoredLinkedList<>(b);
            //System.out.println("ASIZE:"+a.size);
            
            if(a.bottom_anchor!=null){
                //Avant, on avait addLast mais on a change a cause de l'heritage, qui demande de se placer en fin de liste chainee 
                a.bottom_anchor.addLast(b);//?????
                //System.out.println("ANCHOR ADDED");
            }
            else{
                //this.value = new AnchoredLinkedList<>(item);
                a.bottom_anchor = new AnchoredLinkedList<>(b);
               //System.out.println("ANCHOR LIST CREATED");
            }
            return b;*/
        }
        
        else if(a!=null&&b!=null&&b.firstChild!=null){
            //System.out.println("B.SIZE:"+b.size);
            //System.out.println("A.SIZE:"+a.size);
            
            /*
            //manque a peaufiner mais l'idee de base y est
            a.lastChild.next = b.firstChild;
            a.lastChild = b.lastChild;
            a.size += b.size;
            a.reset();
            //return a;*/
            if(a.bottom_anchor!=null){
                //System.out.println("ANCHOR.SIZE:"+a.bottom_anchor.size);
                //Avant, on avait addLast mais on a change a cause de l'heritage, qui demande de se placer en fin de liste chainee 
                a.bottom_anchor.addLast(b);
                //System.out.println("ANCHOR ADDED");
            }
            else{
                //System.out.println("ANCHOR.SIZE:null");
                //this.value = new AnchoredLinkedList<>(item);
                
                
                //a.bottom_anchor = new AnchoredLinkedList<>(b);
                
                //System.out.println("ANCHOR LIST CREATED");
            }
            //System.out.println("ANCHOR.SIZE:"+a.bottom_anchor.size);
            return a;
        }
        //return (AnchoredLinkedList)LinkedList.merge((LinkedList)a, (LinkedList)b);
        return null;
    }

    private AnchoredLinkedList addAll(AnchoredLinkedList b) {
        AnchoredLinkedList nlist = null;
        if(b!=null&&b.firstChild!=null){
            nlist = new AnchoredLinkedList(b.firstChild);//est ce que c'est bon?
            b.reset();
            do{
                nlist.addLast(b.curr);
            }while(b.hasNext());
        }
        return nlist;
    }
}
