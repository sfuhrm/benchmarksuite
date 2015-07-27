package de.tynne.benchmarksuite.examples.collections;

import de.tynne.benchmarksuite.Benchmark;
import de.tynne.benchmarksuite.BenchmarkProducer;
import de.tynne.benchmarksuite.BenchmarkSuite;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@BenchmarkSuite(name = "CollectionsAdd")
public class CollectionAddBenchmarks extends AbstractCollectionBenchmarks implements BenchmarkProducer {

    public CollectionAddBenchmarks() {
    }
    
    @Override
    protected Benchmark operation(String id, List<Long> inData, Supplier<Collection<Long>> supplier) {
        Consumer<Collection<Long>> consumer = l -> {
            for (Long val : inData) {
                l.add(val);
            }
        };
        return bench("A"+id, "Add "+inData.size()+" objs ", inData,  supplier, consumer);
    }
}
