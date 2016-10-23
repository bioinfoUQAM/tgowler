package ontopattern16.benchmark;

import java.util.Random;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 *
 * @author Enridestroy
 */
@State(Scope.Benchmark)
public class SIMDTestAdd {
    
    final int[] a = new int[Integer.MAX_VALUE / 100];
    final int l = a.length;
    
    {
        Random rand = new Random();
        for (int i=0;i!=l;++i){
            a[i] = rand.nextInt(Integer.MAX_VALUE/2);
        }
    }
    
    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE) //makes looking at assembly easier
    public void inc() {
        for (int i=0;i!=l;++i)
            a[i]++;// a is an int[], I benchmarked with size 32K
    }
}
