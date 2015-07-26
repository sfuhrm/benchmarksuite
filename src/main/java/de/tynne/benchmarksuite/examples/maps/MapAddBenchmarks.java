package de.tynne.benchmarksuite.examples.maps;

import de.tynne.benchmarksuite.Benchmark;
import de.tynne.benchmarksuite.BenchmarkProducer;
import de.tynne.benchmarksuite.BenchmarkSuite;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@BenchmarkSuite(name = "MapAdd")
public class MapAddBenchmarks extends AbstractMapBenchmarks implements BenchmarkProducer {

    public MapAddBenchmarks() {
    }
    
    @Override
    protected Benchmark operation(String id, Map<Long, String> inData, Supplier<Map<Long, String>> supplier) {
        Consumer<Map<Long,String>> consumer = l -> {
            for (Map.Entry<Long,String> val : inData.entrySet()) {
                l.put(val.getKey(), val.getValue());
            }
        };
        return bench(id, "Put "+inData.size()+" objs ", inData,  supplier, consumer);
    }
}
