package de.tynne.benchmarksuite.examples.maps;

import de.tynne.benchmarksuite.Benchmark;
import de.tynne.benchmarksuite.BenchmarkProducer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractMapBenchmarks implements BenchmarkProducer {

    protected final Random random = new Random();
    
    protected AbstractMapBenchmarks() {
    }
        
    protected Benchmark bench(String id, String actionPart,Map<Long,String> inData, Supplier<Map<Long,String>> supplier, Consumer<Map<Long,String>> consumer)  {
        Benchmark b = new Benchmark<>(supplier, consumer, 
                actionPart+
                (supplier.get().getClass().getName().replace("java.util.", "")), inData.size());
        b.setId(id);
        return b;
    }

    protected abstract Benchmark operation(String id, Map<Long,String> inData, Supplier<Map<Long,String>> supplier);

    private List<Benchmark> of(int multi, String suffix) {
        Map<Long,String> longs = random.longs(multi).mapToObj(l -> l).collect(Collectors.toMap(l -> l, l -> l.toString()));
        List<Supplier<Map<Long,String>>> typeSuppliers = Arrays.asList(
                HashMap::new,
                LinkedHashMap::new,
                TreeMap::new,
                Hashtable::new
        );
        
        char id = 'A';
        
        List<Benchmark> results = new ArrayList<>();
        
        for (Supplier<Map<Long,String>> typeSupplier : typeSuppliers) {
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
