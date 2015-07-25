package de.tynne.benchmarksuite;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Benchmark<T> implements Runnable {
    /** Produces data for {@link #benchmark}. */
    private final Supplier<T> init;
    
    /** Consumes data from {@link #init}. */
    private final Consumer<T> benchmark;
    
    @Getter
    private final String name;    
    
    /** The multiplicity of the benchmark. This is how many elements were processed.
     * The results get divided by this number. */
    @Getter
    private final long multiplicity;
    
    @Getter
    @Setter
    private String id;
    
    private final List<Long> nanoTimes;
    
    private final static IDGenerator GENERATOR = new IDGenerator();

    /**
     * Creates a new benchmark.
     * @param init non-benchmarked initialization routing creating the object for the benchmark to use.
     * @param benchmark the benchmarked code using the result of the init routine.
     * @param name the benchmark name.
     * @param times execute this often the benchmark code in one run.
     * @param multiplicity this is the number of elements the benchmark processes itself.
     */
    public Benchmark(Supplier<T> init, Consumer<T> benchmark, String name, long multiplicity) {
        this.id = "A"+GENERATOR.generate().toString();
        this.init = init;
        this.benchmark = benchmark;
        this.name = name;
        this.multiplicity = multiplicity;
        this.nanoTimes = new ArrayList<>();
        
        log.debug("Created {} with multiplicity={}", name, multiplicity);
    }
    
    public void reset() {
        nanoTimes.clear();
    }
    
    @Override
    public void run() {      
        T prep = init.get();
        long startNanos = System.nanoTime();
        benchmark.accept(prep);
        long endNanos = System.nanoTime();

        long nanos = endNanos - startNanos;
        
        nanoTimes.add(nanos);
    }

    public DoubleStream getNanoTimes() {
        return nanoTimes.stream().mapToDouble(s -> (double)s / (double)(multiplicity));
    }
    
    public OptionalDouble getMedian() {
        return getNanoTimes().sorted().skip(nanoTimes.size()/2).findFirst();
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
