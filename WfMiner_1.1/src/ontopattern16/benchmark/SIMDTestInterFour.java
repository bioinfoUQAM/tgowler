package ontopattern16.benchmark;

import java.util.Random;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class SIMDTestInterFour {
    final int[] _a = new int[Integer.MAX_VALUE / 100];
    final int l = _a.length;
    final int[] _b = new int[l];
    final int l_ = l - (l % 4);
    {
        Random rand = new Random();
        for (int i=0;i!=l;++i){
            _a[i] = rand.nextInt(Integer.MAX_VALUE);
            _b[i] = rand.nextInt(Integer.MAX_VALUE);
        }
    }
    
    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE) //makes looking at assembly easier
    public void inc() {
        final int[] a = _a;
        final int[] b = _b;
        for (int i=0;i!=l_;i += 4){
            /*a[i+0] = a[i+0] & b[i+0];// a is an int[], I benchmarked with size 32K
            a[i+1] = a[i+1] & b[i+1];
            a[i+2] = a[i+2] & b[i+2];
            a[i+3] = a[i+3] & b[i+3];*/
            
            
            //c'est pire avec la partie du bas !!!
            //c'est meme pire sans final !!!
            
            /*final*/ int a0 = a[i+0] & b[i+0];// a is an int[], I benchmarked with size 32K
            /*final*/ int a1 = a[i+1] & b[i+1];
            /*final*/ int a2 = a[i+2] & b[i+2];
            /*final*/ int a3 = a[i+3] & b[i+3];
            a[i+0] = a0;
            a[i+1] = a1;
            a[i+2] = a2;
            a[i+3] = a3;
        }
    }
}
