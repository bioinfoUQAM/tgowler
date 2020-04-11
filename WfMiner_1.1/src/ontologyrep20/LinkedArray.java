/**
 *
 * @author Enridestroy
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ontologyrep20;


public class LinkedArray<A> {
    public LinkedList<Object[]> buckets;
    public int size = 0;
    public boolean bucket_full = false;
    public Object[] current_bucket;
    public int[] free_indexes;
    public int next_index;
    public Object[] b_index;
    //trouver une astuce pour conserver les liens index-tableaux
    //radix?
    //en calculant un modulo, on peut connaite le numero a ouvrir.
    //en regardant le reste, on a plus qu'a ouvrir l'item no i.
    //radix tree des index -> long
    
    /**
     * Cree un nouveau tableau vide de 256 items
     */
    public Object[] createBucket(){
        Object[] bucket = new Object[256];
        this.buckets.addLast(bucket);
        
        this.b_index[this.b_index.length] = this.buckets.lastChild;//est ce qu'il va y avoir reecriture ? probablement, a verifier.
        
        return bucket;
    }
    
    /**
     * 
     * @return 
     */
    public Object getBucket(int index){
        int t_index = index % 256;
        int reste = 0;
        return this.b_index[t_index];
    }
    
    /**
     * 
     * @param index
     * @return 
     */
    public Object get(int index){
        Object[] buck = (Object[])this.getBucket(index);
        return buck[index];
    }
    
    public int getNbrBuckets(){
        return this.buckets.size;
    }
    
    public void add(A o){      
        //si le bucket courrant est plein, 
        if(this.bucket_full){
            //on cree un nouveau bucket
            this.current_bucket = this.createBucket();
        }
        this.current_bucket[this.next_index] = o;
        this.size++;
    }
}
