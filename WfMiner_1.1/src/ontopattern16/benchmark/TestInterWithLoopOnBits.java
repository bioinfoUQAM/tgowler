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
public class TestInterWithLoopOnBits {
    final int[] a = new int[Integer.MAX_VALUE / 100];
    final int l = a.length;
    final int[] b = new int[l];
    
    {
        Random rand = new Random();
        for (int i=0;i!=l;++i){
            a[i] = rand.nextInt(Integer.MAX_VALUE);
            b[i] = rand.nextInt(Integer.MAX_VALUE);
        }
    }
    
    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE) //makes looking at assembly easier
    public void inc() {
        for (int i=0;i!=l;++i){
            int tmp = 0;
            //a[i] = a[i] & b[i];// a is an int[], I benchmarked with size 32K
            for(int j=0;j!=32;++j){
                final int mask = (1 << j);
                tmp = tmp | (((a[i] & mask) & (b[i] & mask))!=0 ? mask : 0);
            }
            a[i] = tmp;
        }
    }
}
