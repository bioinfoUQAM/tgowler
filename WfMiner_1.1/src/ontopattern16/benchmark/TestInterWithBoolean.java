/**
 *
 * @author Enridestroy
 */

package ontopattern16.benchmark;

import java.util.Random;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class TestInterWithBoolean {
    //final int[] a = new int[Integer.MAX_VALUE / 100];
    final int l = (Integer.MAX_VALUE / 100);
    //final int[] b = new int[l];
    
    final boolean[] c = new boolean[l * 32];
    final boolean[] d = new boolean[l * 32];
    {
        Random rand = new Random();
        for (int i=0;i!=l;++i){
            int a = rand.nextInt(Integer.MAX_VALUE);
            int b = rand.nextInt(Integer.MAX_VALUE);
            
            //a[i] = a[i] & b[i];// a is an int[], I benchmarked with size 32K
            for(int j=0;j!=32;++j){
                final int mask = (1 << j);
                c[(i * 32)+j] = ((a & mask)!=0);
            }
            
            for(int j=0;j!=32;++j){
                final int mask = (1 << j);
                d[(i * 32)+j] = ((b & mask)!=0);
            }
        }
    }
    
    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE) //makes looking at assembly easier
    public void inc() {
        final int ll=l*32;
        for (int i=0;i!=ll;++i)
            c[i] = c[i] & d[i];// a is an int[], I benchmarked with size 32K

    }
}
