package de.tynne.streamperformance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;

public class BenchmarkRunner implements Runnable {
    @Getter
    private final List<Benchmark> benchmarks;

    private final long SEC_IN_NANOS = 1_000_000_000l;
    private final long WARMUP_TIME_NANOS = 5l*SEC_IN_NANOS;
    private final long RUN_TIME_NANOS = 30l*SEC_IN_NANOS;
        
    public BenchmarkRunner(Collection<Benchmark> in) {
        this.benchmarks = new ArrayList<>(in);
    }
    
    @Override
    public void run() {
        int progress = 0;
        int total = benchmarks.size();
        
        long totalTimeSecs = total * (WARMUP_TIME_NANOS + RUN_TIME_NANOS) / SEC_IN_NANOS;
        long totalStart = System.nanoTime();
        
        for (Benchmark b : benchmarks) {
            long elapsed = (System.nanoTime() - totalStart) / SEC_IN_NANOS;
            printProgress(progress, total,
                    b, elapsed, totalTimeSecs, "WARUMP");
            
            long start = System.nanoTime();
            while ((System.nanoTime() - start) < WARMUP_TIME_NANOS) {
                b.reset();
                
                // warm up the jit
                b.run();                
            }
            
            elapsed = (System.nanoTime() - totalStart) / SEC_IN_NANOS;
            printProgress(progress, total,
                    b, elapsed, totalTimeSecs, "MAIN RUN");
            b.reset();
            start = System.nanoTime();
            while ((System.nanoTime() - start) < RUN_TIME_NANOS) {
                b.run();
            }
            progress++;
        }
    }

    private void printProgress(int progress, int total, Benchmark b, long elapsed, long totalTimeSecs, String phase) {
        System.err.printf("%d / %d (%g%%) (%s, %s), %s (%ds elapsed, %ds to go)\n",
                progress, total, (100.*progress)/total, b.getId(), b.getName(),
                phase,
                elapsed,
                totalTimeSecs - elapsed
        );
    }
}
