package de.tynne.benchmarksuite.examples.collections;

import de.tynne.benchmarksuite.Benchmark;
import de.tynne.benchmarksuite.BenchmarkProducer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractCollectionBenchmarks implements BenchmarkProducer {

    protected final Random random = new Random();
    
    protected AbstractCollectionBenchmarks() {
    }
        
    protected Benchmark bench(String id, String actionPart,List<Long> inData, Supplier<Collection<Long>> supplier, Consumer<Collection<Long>> consumer)  {
        Benchmark b = new Benchmark<>(supplier, consumer, actionPart+supplier.get().getClass().getName(), inData.size());
        b.setId(id);
        return b;
    }

    protected abstract Benchmark operation(String id, List<Long> inData, Supplier<Collection<Long>> supplier);

    private List<Benchmark> of(int multi, String suffix) {
        List<Long> longs = random.longs(multi).mapToObj(l -> l).collect(Collectors.toList());
        List<Supplier<Collection<Long>>> typeSuppliers = Arrays.asList(
                LinkedHashSet::new,
                HashSet::new,
                TreeSet::new,
                ArrayDeque::new,
                ArrayList::new,
                LinkedList::new,
                Vector::new
        );
        
        char id = 'A';
        
        List<Benchmark> results = new ArrayList<>();
        
        for (Supplier<Collection<Long>> typeSupplier : typeSuppliers) {
            results.add(operation((id++)+suffix,
                    longs,
                    typeSupplier));
        }

        return results;    
    }
        
    @Override
    public List<Benchmark> get() {
        List<Benchmark> benchmarks = new ArrayList<>();
        
        benchmarks.addAll(of(10, "1"));
        benchmarks.addAll(of(100, "2"));
        benchmarks.addAll(of(1000, "3"));
        return benchmarks;
    }
}
