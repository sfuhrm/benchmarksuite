package de.tynne.benchmarksuite.examples;

import de.tynne.benchmarksuite.Benchmark;
import de.tynne.benchmarksuite.BenchmarkProducer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * @author fury
 */
public class CollectionBenchmarks implements BenchmarkProducer {

    private final Random random = new Random();
    
    public CollectionBenchmarks() {
    }
    
    private Benchmark removeWithIterator(String id, List<Long> inData, Supplier<Collection<Long>> typeSupplier)  {
        Consumer<Collection<Long>> consumer = l -> {
            Iterator<Long> iter = l.iterator();
            while (iter.hasNext()) {
                iter.next();
                iter.remove();
            }
        };
        
        Supplier<Collection<Long>> supplier = () -> {
            Collection<Long> collection = typeSupplier.get();
            collection.addAll(inData);
            return collection;
        };
        
        return bench(id, "Add objs ", inData,  supplier, consumer);
    }
    
    private Benchmark add(String id, List<Long> inData, Supplier<Collection<Long>> supplier)  {
        Consumer<Collection<Long>> consumer = l -> {
            for (Long val : inData) {
                l.add(val);
            }
        };
        return bench(id, "Add objs ", inData,  supplier, consumer);
    }
    
    private Benchmark bench(String id, String actionPart,List<Long> inData, Supplier<Collection<Long>> supplier, Consumer<Collection<Long>> consumer)  {
        Benchmark b = new Benchmark<>(supplier, consumer, actionPart+supplier.get().getClass().getName(), inData.size());
        b.setId(id);
        return b;
    }


    private List<Benchmark> of(int multi, String suffix) {
        List<Long> longs = random.longs(multi).mapToObj(l -> l).collect(Collectors.toList());
        List<Supplier<Collection<Long>>> typeSuppliers = Arrays.asList(
                HashSet::new,
                TreeSet::new,
                ArrayList::new,
                LinkedList::new,
                Vector::new
        );
        
        char id = 'A';
        
        List<Benchmark> results = new ArrayList<>();
        
        for (Supplier<Collection<Long>> typeSupplier : typeSuppliers) {
            results.add(add((id++)+suffix,
                    longs,
                    typeSupplier));
            results.add(removeWithIterator((id++)+suffix,
                    longs,
                    typeSupplier));
        }

        return results;    
    }
        
    @Override
    public List<Benchmark> get() {
        List<Benchmark> benchmarks = new ArrayList<>();
        
        benchmarks.addAll(of(10, "1"));
        benchmarks.addAll(of(1000, "2"));
        benchmarks.addAll(of(100000, "3"));
        return benchmarks;
    }
}
