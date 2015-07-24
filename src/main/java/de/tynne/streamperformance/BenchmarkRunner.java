package de.tynne.streamperformance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class BenchmarkRunner implements Runnable {    
    @Getter
    private final List<Benchmark> benchmarks;

    static final long SEC_IN_NANOS = 1_000_000_000l;
        
    private final long warmupTimeNanos;
    private final long runTimeNanos;
    
    public BenchmarkRunner(Collection<Benchmark> in, long warmupTimeNanos, long runTimeNanos) {
        log.debug("Init with {} benchmarks", in.size());
        
        this.benchmarks = new ArrayList<>(in);
        this.warmupTimeNanos = warmupTimeNanos;
        this.runTimeNanos = runTimeNanos;
    }
    
    @Override
    public void run() {
        int progress = 0;
        int total = benchmarks.size();
        
        long totalTimeSecs = total * (warmupTimeNanos + runTimeNanos) / SEC_IN_NANOS;
        long totalStart = System.nanoTime();
        
        for (Benchmark b : benchmarks) {
            MDC.put("benchmark", b.getId());
            log.debug("Benchmark {}: {}", b.getId(), b.getName());
            
            long elapsed = (System.nanoTime() - totalStart) / SEC_IN_NANOS;
            printProgress(progress, total,
                    b, elapsed, totalTimeSecs, "WARUMP");
            
            b.reset();
            long start = System.nanoTime();
            while ((System.nanoTime() - start) < warmupTimeNanos) {
                // warm up the jit
                b.run();                
            }
            
            log.info("Warmup stats: {}", b);
            
            elapsed = (System.nanoTime() - totalStart) / SEC_IN_NANOS;
            printProgress(progress, total,
                    b, elapsed, totalTimeSecs, "MAIN RUN");
            b.reset();
            start = System.nanoTime();
            while ((System.nanoTime() - start) < runTimeNanos) {
                b.run();
            }
            
            log.info("Run stats: {}", b);
            
            progress++;
        }
        MDC.remove("benchmark");
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
