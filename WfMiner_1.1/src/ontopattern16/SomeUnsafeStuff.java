/**
 *
 * @author Enridestroy
*/
package ontopattern16;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;


public final class SomeUnsafeStuff {
    /**
     * 
     */
    public static Unsafe unsafe = null;
    
    /**
     * 
     */
    public static void loadUnsafeFast(){
        Field theUnsafe;
        try {
            theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            SomeUnsafeStuff.unsafe = (Unsafe) theUnsafe.get(null);
            
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(SomeUnsafeStuff.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     */
    public static void loadUnsafe(){
        Constructor<Unsafe> unsafeConstructor;
        try {
            unsafeConstructor = Unsafe.class.getDeclaredConstructor();
            unsafeConstructor.setAccessible(true);
            SomeUnsafeStuff.unsafe = unsafeConstructor.newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(SomeUnsafeStuff.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    public static long sizeOf(Class<?> clazz){
        if(unsafe == null){
            System.out.println("no unsafe reference...");
            System.exit(1);
        }
        long maximumOffset = 0;
        do {
          for (Field f : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) {
              maximumOffset = Math.max(maximumOffset, unsafe.objectFieldOffset(f));
            }
          }
        } while ((clazz = clazz.getSuperclass()) != null);
        return maximumOffset + 8;
    }
    
    public static long array_sizeOf(int[] array){
        if(unsafe == null){
            System.out.println("no unsafe reference...");
            System.exit(1);
        }
        long offset = unsafe.arrayBaseOffset(array.getClass());
        long scale = unsafe.arrayIndexScale(array.getClass());
        long size = offset + scale * Array.getLength(array);
        //System.out.println("offset of array is "+offset);
        //System.out.println("scale of array is "+scale);
        //System.out.println("size of array is "+size);
        return size;
    }
    
}
