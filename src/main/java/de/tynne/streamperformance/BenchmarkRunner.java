package de.tynne.streamperformance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;

public class BenchmarkRunner implements Runnable {
    @Getter
    private final List<Benchmark> benchmarks;

    private final int batchSize = 4;
        
    public BenchmarkRunner(Collection<Benchmark> in) {
        this.benchmarks = new ArrayList<>(in);
    }
    
    @Override
    public void run() {
        for (Benchmark b : benchmarks) {
            
            boolean warm = false;
            
            int iterations = 0;
            
            do {
                b.reset();
                
                // warm up the jit
                for (int i=0; i < batchSize; i++) {
                    b.run();                
                }
                
                double min = b.getNanoTimes().min().getAsDouble();
                double max = b.getNanoTimes().max().getAsDouble();
                double diff = Math.abs(max - min);
                double dev = (double)diff / (double)max;
                System.out.printf("Dev: %g\n", dev);
                warm = dev  < 0.01;
                iterations++;
            } while (!warm);
            System.err.println("Warm after "+iterations+" iterations.");            
        }
    }
}
