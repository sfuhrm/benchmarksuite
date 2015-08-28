/*
 * Copyright 2015 Stephan Fuhrmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * Executes a collection of benchmarks.
 * @author Stephan Fuhrmann
 */
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
        Set<String> idSet = new HashSet<>(idList);
        
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
        int total = benchmarks.size() + (RUN_NULL_BENCHMARK ? 1 : 0);
        
        long totalStart = System.nanoTime();
        
        if (RUN_NULL_BENCHMARK) {
            measureBenchmark(nullBenchmark, totalStart, progress, total);
            progress++;
        }
        
        for (Benchmark b : benchmarks) {
            if (RUN_NULL_BENCHMARK) {
                b.setNullBenchmark(nullBenchmark);
            }
            measureBenchmark(b, totalStart, progress, total);
            progress++;
        }
        MDC.remove("benchmark");
    }

    private void measureBenchmark(Benchmark b, long totalStart, int progress, int total) throws IllegalArgumentException {
        MDC.put("benchmark", b.getId());
        log.debug("Benchmark {}: {}", b.getId(), b.getName());
        
        long elapsed = (System.nanoTime() - totalStart) / SEC_IN_NANOS;
        printProgress(progress, total,
                b, elapsed, "WARUMP");
        
        b.reset();
        long start = System.nanoTime();
        while ((System.nanoTime() - start) < warmupTimeNanos) {
            // warm up the jit
            b.run();
        }
        
        log.info("Warmup stats: {}", b);
        
        elapsed = (System.nanoTime() - totalStart) / SEC_IN_NANOS;
        printProgress(progress, total,
                b, elapsed, "MAIN RUN");
        b.reset();
        start = System.nanoTime();
        while ((System.nanoTime() - start) < runTimeNanos) {
            b.run();
        }
        
        log.info("Run stats: {}", b);
    }

    private void printProgress(int progress, int total, Benchmark b, long elapsed, String phase) {
        double done = (double)progress / (double)total;
        System.err.printf("%d / %d (%g%%) (%s, %s), %s (%ds elapsed, %ds to go)\n",
                progress, total, (100.*done), b.getId(), b.getName(),
                phase,
                elapsed,
                (int)((1.-done) * (elapsed/done))
        );
    }
}
