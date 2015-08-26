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
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *  A benchmark.
 * A benchmark has two parts: 
 * <ol>
 * <li> An initialization part that is not measured.
 * <li> The benchmark itself that is measured and uses the initialization part from above.
 * </ol>
 * @author Stephan Fuhrmann
 * @param <T> The type of the initialization result.
 */
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
    
    /** Nano times as measured by {@link #run()}. */
    private final List<Long> nanoTimes;
    
    private final static IDGenerator GENERATOR = new IDGenerator();
    
    /** Optional null benchmark that will be used to correct the times to netto times.
     */
    @Getter @Setter
    private NullBenchmark nullBenchmark;

    /**
     * Creates a new benchmark.
     * @param init non-benchmarked initialization routing creating the object for the benchmark to use.
     * @param benchmark the benchmarked code using the result of the init routine.
     * @param name the benchmark name.
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
    
    /** Resets the remembered timing data. */
    public void reset() {
        nanoTimes.clear();
    }
    
    /** Calculates one benchmark and measures the execution time.
     */
    @Override
    public void run() {      
        T prep = init.get();
        long startNanos = System.nanoTime();
        benchmark.accept(prep);
        long endNanos = System.nanoTime();

        long nanos = endNanos - startNanos;
        
        nanoTimes.add(nanos);
    }

    /** Gets the measured times for each {@link #run() } invocation.
     * @return stream of times in nano seconds, divided by the multiplicity.
     */
    public DoubleStream getNanoTimes() {
        DoubleStream doubleStream;
        if (nullBenchmark == null) {
            doubleStream = nanoTimes.stream().mapToDouble(s -> (double)s / (double)(multiplicity));
        } else {
            double min = nullBenchmark.getNanoTimes().min().getAsDouble();
            doubleStream = nanoTimes.stream().mapToDouble(s -> (double)(s-min) / (double)(multiplicity));
        }
        return doubleStream;
    }
    
    /** Gets the median of the {@link #getNanoTimes() () times}.
     * @return the optional median in nanos.
     */
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
