package de.tynne.streamperformance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;

public class BenchmarkRunner implements Runnable {
    @Getter
    private final List<Benchmark> benchmarks;

    private final long SEC_IN_NANOS = 1_000_000l;
    private final long WARMUP_TIME_NANOS = 5l*SEC_IN_NANOS;
    private final long RUN_TIME_NANOS = 30l*SEC_IN_NANOS;
        
    public BenchmarkRunner(Collection<Benchmark> in) {
        this.benchmarks = new ArrayList<>(in);
    }
    
    @Override
    public void run() {
        int progress = 0;
        int total = benchmarks.size();
        
        for (Benchmark b : benchmarks) {
            System.err.printf("%d / %d (%g%%) (%s, %s), WARMUP\n", progress, total, (100.*progress)/total, b.getId(), b.getName());
            
            long start = System.nanoTime();
            while ((System.nanoTime() - start) < WARMUP_TIME_NANOS) {
                b.reset();
                
                // warm up the jit
                b.run();                
            }
            
            System.err.printf("%d / %d (%g%%) (%s, %s), MAIN RUN\n", progress, total, (100.*progress)/total, b.getId(), b.getName());
            b.reset();
            start = System.nanoTime();
            while ((System.nanoTime() - start) < RUN_TIME_NANOS) {
                b.run();
            }
            progress++;
        }
    }
}
