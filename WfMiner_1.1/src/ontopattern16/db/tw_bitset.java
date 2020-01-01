/**
 *
 * @author Enridestroy
 */

package ontopattern16.db;

public class tw_bitset {
    //
    public static int[] int_nouveau_bitset(final int taille, final int metablocks){
        int[] r = new int[(metablocks > 0 ? 1 : 0) + taille + metablocks];
        r[0] = taille;
        return r;
    }
    
    //ne gere pas encore les metablocks... (copier jusqu'au debut des meta blocks puis 
    //dans un deuxueme temps copier vers les m last cases
    public static int[] expand_bitset_to(final int[] bitset, final int new_size){
        final int[] new_bitset = new int[new_size];
        System.arraycopy(bitset, 0, new_bitset, 0, bitset.length);
        return new_bitset;
    }
    
    //ne gere pas encore les metablocks... (copier jusqu'au debut des meta blocks puis 
    //dans un deuxueme temps copier vers les m last cases
    public static int[] expand_bitset_with(int[] bitset, final int expansion){
        final int[] new_bitset = new int[bitset.length + expansion];
        System.arraycopy(bitset, 0, new_bitset, 0, bitset.length);
        return new_bitset;
    }
    
    //
    public static void set_bits(final int[] bitset, boolean[] bits, int from){
        final int blocksize = 32;
        if(from == 0) from = bitset[0];
        int cursor = 0;
        for(int i=1;i < from;i++){
            for(int j=0;j < blocksize;j++){
                if(bits[cursor]){
                    bitset[i] = bitset[i] | (1 << j);
                }
                else{
                    bitset[i] = bitset[i] & (Integer.MAX_VALUE ^ (1 << j));
                }
            }
        }
    }
    
    public static boolean get_bit_at(final int pos, final int[] bitset){
        final int block_size = 32;//taille de l'unite logique qui sert a maintenir bitset (ex:int, long)
        //final int meta_blocks = 0;//blocks reserves (storage de ints par ex)
        final int block_to_alter = (pos / block_size) + 1;
        final int bit_to_alter = pos % block_size;
        return (bitset[block_to_alter] & (1 << bit_to_alter)) != 0;
    }
    
    
    public static String print_bitset(final int[] bitset){
        StringBuilder b = new StringBuilder();
        final int block_size = 32;//taille de l'unite logique qui sert a maintenir bitset (ex:int, long)
        //final int meta_blocks = 0;//blocks reserves (storage de ints par ex)
        //final int block_to_alter = (pos / block_size) + 1;
        //final int bit_to_alter = pos % block_size;
        b.append("l=").append(bitset[0]);
        for(int j=1;j < bitset.length; j++){
            b.append(",[");
            for(int i=0;i < block_size;i++){
                b.append("").append(((j-1)*block_size)+i).append("=").append(((bitset[j] & (1 << i))!=0) ? 1 : 0).append(", ");
            }
            b.append("]");
        }
        return b.toString();
    }
    
    //
    public static void set_bit_at_one(final int pos, final int[] bitset){
        final int block_size = 32;//taille de l'unite logique qui sert a maintenir bitset (ex:int, long)
        //final int meta_blocks = 0;//blocks reserves (storage de ints par ex)
        final int block_to_alter = (pos / block_size) + 1;
        final int bit_to_alter = pos % block_size;
        bitset[block_to_alter] = bitset[block_to_alter] | (1 << bit_to_alter);
    }
    
    //
    public static void set_bit_at_zero(final int pos, final int[] bitset){
        final int block_size = 32;//taille de l'unite logique qui sert a maintenir bitset (ex:int, long)
        //final int meta_blocks = 0;//blocks reserves (storage de ints par ex)
        final int block_to_alter = (pos / block_size) + 1;
        final int bit_to_alter = pos % block_size;
        bitset[block_to_alter] = bitset[block_to_alter] & (Integer.MAX_VALUE ^ (1 << bit_to_alter));
    }
    
    //
    public static int get_length(final int[] bitset){
        return bitset[0];
    }
    
    //
    public static int get_metablock_length(final int[] bitset){
        return bitset.length - bitset[0] - 1;
    }
    
    //
    public static void set_metaparam_at(final int[] bitset, final int at, final int value){
        final int block_size = 32;//taille de l'unite logique qui sert a maintenir bitset (ex:int, long)
        final int block_to_alter = 1 + bitset[0] + (at / block_size);
        bitset[block_to_alter] = value;
    }
    
    //
    public static int[] and_diff_length(final int[] left, final int[] right, final int l1, final int l2){
        return new int[0];
    }
    
    //NOTA : ne garde pas les metablocks
    public static int[] and_same_length(final int[] left, final int[] right, int l){
        if(l == 0){
            //longueur pas connue
            l = left[0];
        }
        int[] result = new int[l];
        for(int i = 1;i < l; i++){
            result[i] = left[i] & right[i];
        }
        result[0] = l;
        return result;
    }
    
    //
    public static int[] or(final int[] left, final int[] right, int l){
        if(l == 0){
            //longueur pas connue
            l = left[0];
        }
        int[] result = new int[l];
        for(int i = 1;i < l; i++){
            result[i] = left[i] | right[i];
        }
        result[0] = l;
        return result;
    }
    
    //
    public static int[] xor(final int[] left, final int[] right, int l){
        if(l == 0){
            //longueur pas connue
            l = left[0];
        }
        int[] result = new int[l];
        for(int i = 1;i < l; i++){
            result[i] = left[i] ^ right[i];
        }
        return result;
    }
    
    //
    public static int[] not(final int[] left, final int[] right, int l){
        if(l == 0){
            //longueur pas connue
            l = left[0];
        }
        int[] result = new int[l];
        for(int i = 1;i < l; i++){
            result[i] = (Integer.MAX_VALUE ^ left[i]);
        }
        return result;
    }
    
    
    public static int popcount_partial(final int[] vector, final int from, final int length){
        final int block_size = 32;
        final int block_to_alter = (from / block_size) + 1;
        final int nbr_other_blocks = (from / block_size) + block_to_alter + 1;
        int count = 0;
        
        int cur = 1;
        int[] result = new int[1 + (nbr_other_blocks - block_to_alter)];
        
        //on doit savoir la position du premier bit dans le premier block
        
        //meme chose pour le dernier.
        
        for(int i = block_to_alter;i < nbr_other_blocks; i++, cur++){
            count += Integer.bitCount(vector[i]);
        }
        return count;
    }
    
    public static String print_block(final int block){
        StringBuilder b = new StringBuilder();
        final int block_size = 32;//taille de l'unite logique qui sert a maintenir bitset (ex:int, long)
        //final int meta_blocks = 0;//blocks reserves (storage de ints par ex)
        //final int block_to_alter = (pos / block_size) + 1;
        //final int bit_to_alter = pos % block_size;
        b.append("l=").append(block);
        b.append(",[");
        for(int i=0;i < block_size;i++){
            b.append("").append(i).append("=").append(((block & (1 << i))!=0) ? 1 : 0).append(", ");
        }
        b.append("]");
        return b.toString();
    }
    
    public static int blockcount_partial(final int[] vector, final int from, final int length){        
        final int block_size = 32;
        
        final int begin_pos = (from % block_size);
        final int block_to_alter = ((from - begin_pos) / block_size) + 1;//le numero du block ou commencer
        final int nbr_other_blocks = (((begin_pos + length) - ((begin_pos + length) % block_size)) / block_size) + block_to_alter + 1;//le nombre de blocks a intersecter...
        final int end_pos = (from + length) % block_size;//calcule la position de fin dans le dernier block    
        
        int count = 0;
        //System.out.println("-------------- begin "+from+" -(+)-> "+length+"--------------------");
        //System.out.println("from="+from+" and length="+length);
        //si plusieurs blocs
        if((nbr_other_blocks - block_to_alter) != 1){
            //System.out.println("plusieurs blocs, "+block_to_alter+"@"+begin_pos+" to "+(nbr_other_blocks)+"@"+end_pos);
            //si il y a plusieurs blocs
            
            //savoir si le premier bloc commence pas a zero...
            if(begin_pos!=0){
                //int lowestOneBit = Integer.lowestOneBit(vector[block_size]);
                //count += (!(lowestOneBit < begin_pos)) ? 1 : 0;
                final int left_mask = (-1 << begin_pos);
                count += ((vector[block_to_alter]&left_mask) != 0) ? 1 : 0;
            }
            else{
                //c ok
                count += vector[block_to_alter]!=0 ? 1 : 0;
            }
            //tous les blocs au milieu
            for(int i=block_to_alter+1;i!=(nbr_other_blocks-1);++i){
                count += vector[i]!=0 ? 1 : 0;
            }
            //dernier block
            if(end_pos!=(block_size-1)){
                //int highestOneBit = Integer.highestOneBit(vector[block_to_alter+nbr_other_blocks]);
                //count += (highestOneBit > end_pos) ? 1 : 0;
                final int right_mask = ~(-1 << end_pos);
                count += (vector[nbr_other_blocks-1]&right_mask) != 0 ? 1 : 0;
            }
            else{
                //ok
                count += vector[nbr_other_blocks-1]!=0 ? 1:0;
            }
        }
        //si un seul block
        else{
            //System.out.println("un seul bloc "+block_to_alter +"@"+ begin_pos+" to "+end_pos);
            //System.out.println(">"+print_block(vector[block_to_alter]));
            
            //apliquer un masque a gauche
            //System.out.println(">>>"+print_block(-1));
            int left_mask = (-1 << begin_pos);
            //System.out.println("lm="+print_block(left_mask));
            //un masque a droite...
            int right_mask = ~(-1 << end_pos);
            //System.out.println("rm="+print_block(right_mask));
            //comparer vs zero
            count += ((vector[block_to_alter] & left_mask & right_mask) != 0) ? 1 : 0;
        }
        //System.out.println("-------------- end --------------------");
        return count;
    }
    
    //renvoie un bitmap contenant le and de deux autres entre deux positions
    //NOTA : supprime les metablocks...
    //NOTA : ne verifie pas les tailles, le range demande doit etre valide sur les deux bitsets en entree
    public static int[] and_same_length_with_ranges(final int[] left, final int[] right, final int from_l, final int for_l){
        final int block_size = 32;
        final int block_to_alter = (from_l / block_size) + 1;
        final int nbr_other_blocks = (for_l / block_size) + block_to_alter + 1;
        
        int cur = 1;
        int[] result = new int[1 + (nbr_other_blocks - block_to_alter)];
        for(int i = block_to_alter;i < nbr_other_blocks; i++, cur++){
            result[cur] = left[i] & right[i];
        }
        result[0] = nbr_other_blocks - block_to_alter;
        return result;
    }
    
    public static int[] and_same_length_with_diff_ranges(final int[] left, final int[] right, final int from_l, final int for_l, final int from_r){
        final int block_size = 32;
        final int block_to_alter_l = (from_l / block_size) + 1;//le numero du block ou commencer
        final int nbr_other_blocks = (for_l / block_size) + block_to_alter_l + 1;//le nombre de blocks a intersecter...
        final int block_to_alter_r = (from_r / block_size);
        
        //System.out.println("left="+print_bitset(left));
        //System.out.println("right="+print_bitset(right));
        //System.out.println("bl="+block_to_alter_l);
        //System.out.println("br="+block_to_alter_r);
        //System.out.println("nb="+nbr_other_blocks);
        
        int cur = 1;
        int[] result = new int[1 + (nbr_other_blocks - block_to_alter_l)];//pourquoi ajouter si enlever ???
        for(int i = block_to_alter_l;i < nbr_other_blocks; i++, cur++){
            result[cur] = left[i] & right[block_to_alter_r+cur];
        }
        result[0] = nbr_other_blocks - block_to_alter_l;
        return result;
    }
    
    public static int[] or_same_length(final int[] left, final int[] right, final int from_l, final int for_l, final int from_r){
        final int block_size = 32;
        final int block_to_alter_l = (from_l / block_size) + 1;//le numero du block ou commencer
        final int nbr_other_blocks = (for_l / block_size) + block_to_alter_l + 1;//le nombre de blocks a intersecter...
        final int block_to_alter_r = (from_r / block_size);

        int cur = 1;
        int[] result = new int[1 + (nbr_other_blocks - block_to_alter_l)];//pourquoi ajouter si enlever ???
        for(int i = block_to_alter_l;i < nbr_other_blocks; i++, cur++){
            result[cur] = left[i] | right[block_to_alter_r+cur];
        }
        result[0] = nbr_other_blocks - block_to_alter_l;
        return result;
    }
    
    /**
     * les blocks sont unis en entier (pas de masquage sur le premier ou le dernier block)
     * @param left
     * @param right
     * @param from_l
     * @param for_l
     * @param from_r 
     */
    public static void or_same_length_and_apply_to_left_full(final int[] left, final int[] right, final int from_l, final int for_l, final int from_r){
        final int block_size = 32;
        final int block_to_alter_l = ((from_l - (from_l % block_size)) / block_size) + 1;//le numero du block ou commencer
        final int nbr_other_blocks = ((for_l - (for_l % block_size)) / block_size) + block_to_alter_l + 1;//le nombre de blocks a intersecter...
        final int block_to_alter_r = ((from_r - (from_r % block_size)) / block_size);

        int cur = 1;        
        
        //si le nombre de blocs a intersecter est plus que 1...
        if((nbr_other_blocks - block_to_alter_l) != 1){
            left[block_to_alter_l] = left[block_to_alter_l] | (right[block_to_alter_r+cur]);            
            cur += 1;
            
            final int last_block = nbr_other_blocks - 1;
            for(int i = block_to_alter_l+1;i < last_block; i++, cur++){                
                left[i] = left[i] | (right[block_to_alter_r+cur]);
            }
            left[last_block] = left[last_block] | (right[block_to_alter_r+cur]);
        }
        else{
            left[block_to_alter_l] = left[block_to_alter_l] | (right[block_to_alter_r+cur]);
        }
    }
    
    /**
     * les blocs sont unis avec possibilite de masquage sur le premier et le dernier
     * si les positions de depart et de fin ne correspondent pas a un block entier
     * NOTA : beaucoup d'affichage a enlever...
     * @param left
     * @param right
     * @param from_l
     * @param for_l
     * @param from_r 
     */
    public static void or_same_length_and_apply_to_left_partial(final int[] left, final int[] right, final int from_l, final int for_l, final int from_r){
        final int block_size = 32;
        
        final int block_to_alter_l = ((from_l - (from_l % block_size)) / block_size) + 1;//le numero du block ou commencer
        //final int from_pos_l = from_l % block_size;
        final int nbr_other_blocks = ((for_l - (for_l % block_size)) / block_size) + block_to_alter_l + 1;//le nombre de blocks a intersecter...
        final int block_to_alter_r = ((from_r - (from_r % block_size)) / block_size);
        final int from_pos_r = from_r % block_size;
        final int end_pos_r = (from_r + for_l) % block_size;//calcule la position de fin...

        //System.out.println("range="+(for_l));
        //System.out.println(""+for_l / 32);
        
        
        int cur = 1;
        //int[] result = new int[1 + (nbr_other_blocks - block_to_alter_l)];//pourquoi ajouter si enlever ???
        
        int first_mask = -1 >>> (block_size-from_pos_r);
        if(from_pos_r!=0) first_mask = ~first_mask;//on doit inverser pour le deplacer de l'autre cote
        /*{
            StringBuilder sb= new StringBuilder();
            for(int i=0;i < block_size;i++){
                sb.append(((first_mask & (1 << i))!=0) ? 1 : 0);
            }
            System.out.println(from_pos_r+", firstmask="+sb.toString());
        }*/
        int last_mask = -1 >>> (block_size-end_pos_r);
        /*{
            StringBuilder sb= new StringBuilder();
            for(int i=0;i < block_size;i++){
                sb.append(((last_mask & (1 << i))!=0) ? 1 : 0);
            }
            System.out.println(end_pos_r+", lastmask="+sb.toString());
        }*/
        
        
        //si le nombre de blocs a intersecter est plus que 1...
        if((nbr_other_blocks - block_to_alter_l) != 1){
            /*System.out.println("need to compute "+(nbr_other_blocks - block_to_alter_l)+" blocks");
            
            {
                StringBuilder sb= new StringBuilder();
                for(int i=0;i < block_size;i++){
                    sb.append(((left[block_to_alter_l] & (1 << i))!=0) ? 1 : 0);
                }
                System.out.println("left is ="+sb.toString());
            }*/
            
            left[block_to_alter_l] = left[block_to_alter_l] | (right[block_to_alter_r+cur] & first_mask);
            
            /*
            {
                StringBuilder sb= new StringBuilder();
                for(int i=0;i < block_size;i++){
                    sb.append(((right[block_to_alter_r+cur] & (1 << i))!=0) ? 1 : 0);
                }
                System.out.println("rigth is ="+sb.toString());
            }
            
            {
                StringBuilder sb= new StringBuilder();
                for(int i=0;i < block_size;i++){
                    sb.append((((right[block_to_alter_r+cur] & first_mask) & (1 << i))!=0) ? 1 : 0);
                }
                System.out.println("rigth+ is ="+sb.toString());
            }
            
            {
                StringBuilder sb= new StringBuilder();
                for(int i=0;i < block_size;i++){
                    sb.append(((left[block_to_alter_l] & (1 << i))!=0) ? 1 : 0);
                }
                System.out.println("left+ is ="+sb.toString());
            }*/
            
            
            /*if(right[block_to_alter_r+cur] < left[block_to_alter_l]){
                System.out.println("(1)"+block_to_alter_r + " was "+right[block_to_alter_r+cur]+" and now "+left[block_to_alter_l]);
            }*/
            
            cur += 1;
            
            final int last_block = nbr_other_blocks - 1;
            for(int i = block_to_alter_l+1;i < last_block; i++, cur++){
                /*System.out.println("block no "+i+" ------------------------");
                {
                    StringBuilder sb= new StringBuilder();
                    for(int m=0;m < block_size;m++){
                        sb.append(((left[i] & (1 << m))!=0) ? 1 : 0);
                    }
                    System.out.println("l is ="+sb.toString());
                }*/
                
                
                left[i] = left[i] | (right[block_to_alter_r+cur]);
                
                
                /*
                {
                    StringBuilder sb= new StringBuilder();
                    for(int m=0;m < block_size;m++){
                        sb.append(((right[block_to_alter_r+cur] & (1 << m))!=0) ? 1 : 0);
                    }
                    System.out.println("r is ="+sb.toString());
                }
                {
                    StringBuilder sb= new StringBuilder();
                    for(int m=0;m < block_size;m++){
                        sb.append(((left[i] & (1 << m))!=0) ? 1 : 0);
                    }
                    System.out.println("+ is ="+sb.toString());
                }*/
                
                
                
                if(right[block_to_alter_r+cur] <= left[i]){
                    System.out.println(block_to_alter_r + " was "+right[block_to_alter_r+cur]+" and now "+left[i]);
                }
            }
            left[last_block] = left[last_block] | (right[block_to_alter_r+cur] & last_mask);
            /*if(right[block_to_alter_r+cur] <= left[last_block]){
                    System.out.println("(n)"+block_to_alter_r + " was "+right[block_to_alter_r+cur]+" and now "+left[last_block]);
            }*/
        }
        else{
            System.out.println("need to compute 1 block");
            left[block_to_alter_l] = left[block_to_alter_l] | (right[block_to_alter_r+cur] & first_mask & last_mask);
            /*if(right[block_to_alter_r+cur] <= left[block_to_alter_l]){
                    System.out.println(block_to_alter_r + " was "+right[block_to_alter_r+cur]+" and now "+left[block_to_alter_l]);
            }*/
        }
        //int test2 = 1 | (1 << 3) | (1 << 31);//2^32 + 5
        
        /*
        
        String ll = Integer.toBinaryString(first_mask);
        System.out.println(from_pos_r + " >>> "+first_mask + " " + ll+"("+ll.length()+")");
        System.out.println("test="+(first_mask & (1 << 31)));
        System.out.println("="+test2);
        
        {
            StringBuilder sb= new StringBuilder();
            for(int i=0;i < block_size;i++){
                sb.append(((test2 & (1 << i))!=0) ? 1 : 0);
            }
            System.out.println("test2bitset="+sb.toString());
        }
        {
            StringBuilder sb= new StringBuilder();
            for(int i=0;i < block_size;i++){
                sb.append(((first_mask & (1 << i))!=0) ? 1 : 0);
            }
            System.out.println("firstmaskbitset="+sb.toString());
        }
        
        
        System.out.println("mask="+(first_mask & test2));
        {
            StringBuilder sb= new StringBuilder();
            for(int i=0;i < block_size;i++){
                sb.append((((first_mask & test2) & (1 << i))!=0) ? 1 : 0);
            }
            System.out.println("maskbitset="+sb.toString());
        }*/
        
        
        /*result[0] = nbr_other_blocks - block_to_alter_l;
        return result;*/
    }
}
