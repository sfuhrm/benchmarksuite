package de.tynne.benchmarksuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
    
    @Getter
    private final NullBenchmark nullBenchmark;
    
    private final static boolean RUN_NULL_BENCHMARK = true;
    
    public BenchmarkRunner(Collection<Benchmark> in, long warmupTimeNanos, long runTimeNanos) {
        log.debug("Init with {} benchmarks", in.size());
        
        nullBenchmark = new NullBenchmark();
        
        this.benchmarks = new ArrayList<>(in);
        checkIdsUnique(benchmarks);
        this.warmupTimeNanos = warmupTimeNanos;
        this.runTimeNanos = runTimeNanos;
    }
    
    private static void checkIdsUnique(Collection<Benchmark> in) {
        List<String> idList = in.stream().map(b -> b.getId()).collect(Collectors.toList());
        Set<String> idSet = new HashSet<String>(idList);
        
        if (idList.size() != idSet.size()) {
            Map<String,Long> histo = idList.stream().collect(Collectors.groupingBy(b -> b, Collectors.counting()));
            List<String> multiList = histo.entrySet().stream().filter(e -> e.getValue() > 1).map(e -> e.getKey()).collect(Collectors.toList());
            
            throw new IllegalArgumentException("Non unique ids: "+multiList);
        }
    }
    
    @Override
    public void run() {
        int progress = 0;
        // all benchmarks plus null
        int total = benchmarks.size() + 1;
        
        long totalTimeSecs = total * (warmupTimeNanos + runTimeNanos) / SEC_IN_NANOS;
        long totalStart = System.nanoTime();
        
        if (RUN_NULL_BENCHMARK) {
            measureBenchmark(nullBenchmark, totalStart, progress, total, totalTimeSecs);
        }
        
        for (Benchmark b : benchmarks) {
            if (RUN_NULL_BENCHMARK) {
                b.setNullBenchmark(nullBenchmark);
            }
            measureBenchmark(b, totalStart, progress, total, totalTimeSecs);
        }
        MDC.remove("benchmark");
    }

    private void measureBenchmark(Benchmark b, long totalStart, int progress, int total, long totalTimeSecs) throws IllegalArgumentException {
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

    private void printProgress(int progress, int total, Benchmark b, long elapsed, long totalTimeSecs, String phase) {
        System.err.printf("%d / %d (%g%%) (%s, %s), %s (%ds elapsed, %ds to go)\n",
                progress, total, (100.*progress)/total, b.getId(), b.getName(),
                phase,
                elapsed,
                totalTimeSecs - elapsed
        );
    }
}
