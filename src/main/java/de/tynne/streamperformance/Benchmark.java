package de.tynne.streamperformance;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import lombok.Getter;
import lombok.Setter;

public class Benchmark<T> implements Runnable {
    /** Produces data for {@link #benchmark}. */
    private final Supplier<T> init;
    
    /** Consumes data from {@link #init}. */
    private final Consumer<T> benchmark;
    
    @Getter
    private final String name;
    
    /** Execute the benchmark this often for sake of statistic stability. */
    @Getter
    private final int times;
    
    /** The multiplicity of the benchmark. This is how many elements were processed.
     * The results get divided by this number. */
    @Getter
    private final long multiplicity;
    
    @Getter
    @Setter
    private String id;
    
    private final List<Long> nanoTimes;
    
    private final static IDGenerator GENERATOR = new IDGenerator();

    public Benchmark(Supplier<T> init, Consumer<T> benchmark, String name) {
        this(init, benchmark, name, 1000, 1);
    }
    
    public Benchmark(Supplier<T> init, Consumer<T> benchmark, String name, int times, long multiplicity) {
        this.id = "A"+GENERATOR.generate().toString();
        this.init = init;
        this.benchmark = benchmark;
        this.name = name;
        this.times = times;
        this.multiplicity = multiplicity;
        this.nanoTimes = new ArrayList<>(times);
    }
    
    public void reset() {
        nanoTimes.clear();
    }
    
    @Override
    public void run() {
        long nanos = 0;
        
        for (int i=0; i < times; i++) {
            T prep = init.get();
            long startNanos = System.nanoTime();
            benchmark.accept(prep);
            long endNanos = System.nanoTime();
            
            nanos += endNanos - startNanos;
        }
        
        nanoTimes.add(nanos);
    }

    public DoubleStream getNanoTimes() {
        return nanoTimes.stream().mapToDouble(s -> (double)s / (double)(times*multiplicity));
    }

    @Override
    public String toString() {
        return getName()+" "+
                "count="+nanoTimes.size()+", "+
                "min="+getNanoTimes().min()+", "+
                "avg="+getNanoTimes().average()+", "+
                "max="+getNanoTimes().max()
        ;
    }
}
