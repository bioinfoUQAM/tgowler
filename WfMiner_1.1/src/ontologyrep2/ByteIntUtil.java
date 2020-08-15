/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ontologyrep2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.BitSet;

/**
 *
 * @author Enridestroy
 */
public final class ByteIntUtil {
    
    /**
     * Tranforme un int en un tableau d'octets
     * @param integer
     * @return
     * @throws IOException 
     */
    public static byte[] intToBytes(int integer) throws IOException {
       ByteArrayOutputStream bos = new ByteArrayOutputStream();
       ObjectOutput out = new ObjectOutputStream(bos);
       out.writeInt(integer);
       out.close();
       byte[] int_bytes = bos.toByteArray();
       bos.close();
       return int_bytes;
   }   
    
    /**
     * Transforme un tableau d'octets en integer
     * @param int_bytes
     * @return
     * @throws IOException 
     */
    public int bytesToInt(byte[] int_bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(int_bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        int my_int = ois.readInt();
        ois.close();
        return my_int;
    }
    
    /**
     * Transforme un octet en un ensemble de bits
     * @param bs
     * @return 
     */
    public static BitSet convert(byte bs) {
        BitSet result = new BitSet();
        System.out.println("T:"+Byte.SIZE);
        //int offset = 0;
        for (int i=0;i<Byte.SIZE; i++) {
            if (((bs >> i) & 1) == 1) {
                result.set(i);
            }
        }
        System.out.println(result.size());
        return result;
    }
    
    public static boolean[] convert(Character c){
        //BitSet result = new BitSet();
        boolean[] result = new boolean[Character.SIZE];
        boolean[] t_result;
        int last = 0;
        System.out.println("TT:"+Character.SIZE);
        for (int i=0;i<Character.SIZE; i++) {
            if (((c >> i) & 1) == 1) {
                result[i] = true;
                last = i;
            }
        }
        t_result = new boolean[last];
        //le vrai resultat que l'on renvoie contient un dernier item a 1. donc le dernier est inutile. sauf si length=Character.SIZE
        //System.arraycopy(result, 0, last, last, last);
        System.out.println(result.length);
        return result;
    }
}
