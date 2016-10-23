/**
 *
 * @author Enridestroy
 */

package ontopattern16;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class BenkmarkStarter {
    
    public static String[] jvmParams = new String[]{
        "-XX:-UseSuperWord", 
        "-XX:+UseSuperWord", 
        "-server", 
        "-Xmx16G", 
        "-XX:UseAVX=1", 
        "-XX:UseAVX=0"
    };
    
    public final static TimeValue measurementTime = TimeValue.seconds(1);
    public final static int nbr_warmup = 5;
    public final static int nbr_real_iter = 5;
    public final static TimeValue warmup_time = TimeValue.seconds(1);
    
    public static void main(final String[] _args) throws RunnerException{
        Options opt = new OptionsBuilder()
        .include("ontopattern16.benchmark.*").exclude("ontopattern16.benchmark.TestInterWithBoolean")
        .warmupTime(warmup_time)
        .warmupIterations(nbr_warmup)
        .measurementTime(measurementTime)
        .measurementIterations(nbr_real_iter)
        .threads(1)
        .forks(1)
        .shouldFailOnError(true)
        .shouldDoGC(true)
        .jvmArgs(new String[]{jvmParams[4], jvmParams[2]})
        .build();
        new Runner(opt).run();
    }
}
