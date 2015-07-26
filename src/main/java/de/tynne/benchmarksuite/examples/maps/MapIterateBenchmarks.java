package de.tynne.benchmarksuite.examples.maps;

import de.tynne.benchmarksuite.Benchmark;
import de.tynne.benchmarksuite.BenchmarkProducer;
import de.tynne.benchmarksuite.BenchmarkSuite;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@BenchmarkSuite(name = "MapIterate")
public class MapIterateBenchmarks extends AbstractMapBenchmarks implements BenchmarkProducer {

    public MapIterateBenchmarks() {
    }
    
    @Override
    protected Benchmark operation(String id, Map<Long, String> inData, Supplier<Map<Long, String>> supplier) {
        Supplier<Map<Long,String>> real = () -> {
            Map<Long,String> result = supplier.get();
            result.putAll(inData);
            return result;
        };
        
        Consumer<Map<Long,String>> consumer = l -> {
            Iterator<Map.Entry<Long,String>> iterator = l.entrySet().iterator();
            while (iterator.hasNext()) {
                iterator.next();
            }
        };
        return bench(id, "Iterate "+inData.size()+" objs ", inData,  supplier, consumer);
    }
}
