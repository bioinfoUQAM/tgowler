package ontopattern16.benchmark;

import java.util.Random;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class SIMDTestInterThirtyTwo {
    final int[] a = new int[Integer.MAX_VALUE / 100];
    final int l = a.length;
    final int[] b = new int[l];
    final int l_ = l - (l % 32);
    
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
        for (int i=0;i!=l_;i += 32){
            a[i+0] = a[i+0] & b[i+0];// a is an int[], I benchmarked with size 32K
            a[i+1] = a[i+1] & b[i+1];
            a[i+2] = a[i+2] & b[i+2];
            a[i+3] = a[i+3] & b[i+3];
            
            a[i+4] = a[i+4] & b[i+4];
            a[i+5] = a[i+5] & b[i+5];
            a[i+6] = a[i+6] & b[i+6];
            a[i+7] = a[i+7] & b[i+7];
            
            a[i+8] = a[i+8] & b[i+8];
            a[i+9] = a[i+9] & b[i+9];
            a[i+10] = a[i+10] & b[i+10];
            a[i+11] = a[i+11] & b[i+11];
            
            a[i+12] = a[i+12] & b[i+12];
            a[i+13] = a[i+13] & b[i+13];
            a[i+14] = a[i+14] & b[i+14];
            a[i+15] = a[i+15] & b[i+15];
            
            a[i+16] = a[i+16] & b[i+16];
            a[i+17] = a[i+17] & b[i+17];
            a[i+18] = a[i+18] & b[i+18];
            a[i+19] = a[i+19] & b[i+19];
            
            a[i+20] = a[i+20] & b[i+20];
            a[i+21] = a[i+21] & b[i+21];
            a[i+22] = a[i+22] & b[i+22];
            a[i+23] = a[i+23] & b[i+23];
            
            a[i+24] = a[i+24] & b[i+24];
            a[i+25] = a[i+25] & b[i+25];
            a[i+26] = a[i+26] & b[i+26];
            a[i+27] = a[i+27] & b[i+27];
            
            a[i+28] = a[i+28] & b[i+28];
            a[i+29] = a[i+29] & b[i+29];
            a[i+30] = a[i+30] & b[i+30];
            a[i+31] = a[i+31] & b[i+31];
        }
    }
}
