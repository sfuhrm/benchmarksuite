package de.tynne.streamperformance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;

public class BenchmarkRunner implements Runnable {
    @Getter
    private final List<Benchmark> benchmarks;

    private final int BATCH_SIZE = 4;
    private final int ITERATIONS_MAX = 100;
        
    public BenchmarkRunner(Collection<Benchmark> in) {
        this.benchmarks = new ArrayList<>(in);
    }
    
    @Override
    public void run() {
        int progress = 0;
        int total = benchmarks.size();
        
        for (Benchmark b : benchmarks) {
            System.err.printf("%d / %d (%g%%) (%s)\n", progress, total, (100.*progress)/total, b.getId());
            boolean warm = false;
            
            int iterations = 0;
            
            do {
                b.reset();
                
                // warm up the jit
                for (int i=0; i < BATCH_SIZE; i++) {
                    b.run();                
                }
                
                double min = b.getNanoTimes().min().getAsDouble();
                double max = b.getNanoTimes().max().getAsDouble();
                double diff = Math.abs(max - min);
                double dev = (double)diff / (double)max;
                System.out.printf("%s: %d. Dev: %g\n", b.getId(), iterations, dev);
                warm = dev  < 0.01;
                iterations++;
            } while (!warm && iterations < ITERATIONS_MAX);
            System.err.printf("%s: Warm after %d iterations.", b.getId(), iterations);
            progress++;
        }
    }
}
