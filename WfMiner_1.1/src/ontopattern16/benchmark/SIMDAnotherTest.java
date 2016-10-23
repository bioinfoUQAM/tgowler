package ontopattern16.benchmark;

import java.util.Random;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class SIMDAnotherTest {
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
        for(int i=0; i != l; ++i){
           a[i&i+1] = a[i&i+1] & b[i&i+1];
        }
    }
}
