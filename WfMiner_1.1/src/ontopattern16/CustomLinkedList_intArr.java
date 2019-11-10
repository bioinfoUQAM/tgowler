/**
 *
 * @author Enridestroy
 */

package ontopattern16;

import java.util.Iterator;

public final class CustomLinkedList_intArr implements Iterable{
    private transient int counter = 0;
    private transient Node_IntArr head;
    private transient Node_IntArr tail;
    private transient boolean head_equals_tail = false;

    // Default constructor
    public CustomLinkedList_intArr(final int[][] firstData) {
        head = new Node_IntArr(firstData);
        tail = head;
        counter = 1;
    }
  
    // inserts the specified element at the specified position in this list
    public final Node_IntArr add(final int[][] data) {
        boolean special_case = false;
        if(tail == head){
            special_case = true;
        }
        
        final Node_IntArr crunchifyTemp = new Node_IntArr(data);
        
        if(tail == null){
            //System.out.println(""+special_case+" "+this.size());
            head = crunchifyTemp;
            tail = head;
            counter = 1;
            //System.exit(1);
            return null;
        }
        else{
            final Node_IntArr pointer_to_return = tail;
            tail.setNext(crunchifyTemp);
            tail = crunchifyTemp;

            counter += 1;
            
            if(special_case){
                this.head = pointer_to_return;
            }
            return pointer_to_return;
        }
    }
    
    public final int[][] getFirst(){
        return (this.head!=null ? this.head.getData() : null);
    }
    
    public final Node_IntArr getFirstNode(){
        return this.head;
    }
    
    public final int[][] getLast(){
        return this.tail.getData();
    }
    
    
    /**
     * attention, une suppresion par reference rend unsafe l'element
     * qui contient la reference : cad elle n'est plus utilisable...
     * NOTA : pas grave car on detruit aussi le concept..
     * @param pointer
     * @return 
     */
    public final Node_IntArr remove(final Node_IntArr pointer){
        //changer uniquement a celui qui correpond a la liste
        final Node_IntArr old = pointer.next;
        pointer.next = pointer.next.next;
        //System.out.println(""+old+" vs "+pointer.next);
        if(pointer.next == null) {
            //System.out.println("updated tail from "+this.tail+" to "+pointer);
            //alors maintenant le dernier est pointer...
            this.tail = pointer;
        }
        counter -= 1;
        return old;
    }
    
    //attention, ne supprime que le premier ?
    //ne tient pas compte du fait que si un seul element, alors on doit supprimer 
    //a la fois la tete et la queue...
    public final boolean removeFirst(){
        boolean special_case = false;
        if(tail == head){
            special_case = true;
        }
        
        final Node_IntArr new_h = this.head.next;
        this.head = new_h;//ca devrait etre suffisant...
        counter -= 1;
        
        if(special_case){
            this.tail = this.head;
        }
        return true;
    }

    // returns the number of elements in this list.
    public final int size() {
        return this.counter;
    }

    @Override
    public String toString() {
        String output = this.counter + "{";
        if (this.head != null) {
            int actual_c = 1;
            if(this.head.getData()!=null) output += "["+this.head.getData().toString()+"]";
            else output += "...";
            Node_IntArr crunchifyCurrent = head.getNext();
            while (crunchifyCurrent != null) {
                actual_c += 1;
                output += "[" + crunchifyCurrent.getData().toString() + "]";
                crunchifyCurrent = crunchifyCurrent.getNext();
            }
            if(actual_c != this.counter){
                System.out.println("ERROR => "+actual_c + " vs " + output);
                System.exit(1);
            }
        }
        return output+"}";
    }

    @Override
    public final Iterator iterator() {
        final Iterator it = new Iterator() {
            private Node_IntArr currentNode = head;

            @Override
            public final boolean hasNext() {
                return currentNode != null;
            }

            @Override
            public final int[][] next() {
                //System.out.println("on prend le prochain dans la liste...");
                final int[][] tor = currentNode.data;
                currentNode = currentNode.next;
                return tor;
            }

            @Override
            public final void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }

    public final static class Node_IntArr {
        // reference to the next node in the chain, or null if there isn't one.
        private transient Node_IntArr next;
        // data carried by this node. could be of any type you need.
        private transient int[][] data;

        // Node constructor
        public Node_IntArr(final int[][] dataValue) {
            next = null;
            data = dataValue;
        }

        // another Node constructor if we want to specify the node to point to.
        @SuppressWarnings("unused")
        public Node_IntArr(final int[][] dataValue, final Node_IntArr nextValue) {
            next = nextValue;
            data = dataValue;
        }

        // these methods should be self-explanatory
        public int[][] getData() {
            return data;
        }

        @SuppressWarnings("unused")
        public void setData(final int[][] dataValue) {
            data = dataValue;
        }

        public Node_IntArr getNext() {
            return next;
        }

        public void setNext(Node_IntArr nextValue) {
            next = nextValue;
        }
    }
}

