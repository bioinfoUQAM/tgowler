package ontopattern16.benchmark;

import java.util.Random;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class SIMDTestInterEight {
    final int[] a = new int[Integer.MAX_VALUE / 100];
    final int l = a.length;
    final int[] b = new int[l];
    final int l_ = l - (l % 8);
    
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
        for (int i=0;i!=l_;i += 8){
            a[i+0] = a[i+0] & b[i+0];// a is an int[], I benchmarked with size 32K
            a[i+1] = a[i+1] & b[i+1];
            a[i+2] = a[i+2] & b[i+2];
            a[i+3] = a[i+3] & b[i+3];
            
            a[i+4] = a[i+4] & b[i+4];
            a[i+5] = a[i+5] & b[i+5];
            a[i+6] = a[i+6] & b[i+6];
            a[i+7] = a[i+7] & b[i+7];
        }
    }
}
