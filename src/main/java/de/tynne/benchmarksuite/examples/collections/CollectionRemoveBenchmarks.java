package de.tynne.benchmarksuite.examples.collections;

import de.tynne.benchmarksuite.Benchmark;
import de.tynne.benchmarksuite.BenchmarkProducer;
import de.tynne.benchmarksuite.BenchmarkSuite;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@BenchmarkSuite(name = "CollectionsRemove")
public class CollectionRemoveBenchmarks extends AbstractCollectionBenchmarks implements BenchmarkProducer {

    public CollectionRemoveBenchmarks() {
    }
    
    @Override
    protected Benchmark operation(String id, List<Long> inData, Supplier<Collection<Long>> supplier) {
        Consumer<Collection<Long>> consumer = l -> {
            Iterator<Long> iter = l.iterator();
            while (iter.hasNext()) {
                iter.next();
                iter.remove();
            }
        };
        
        Supplier<Collection<Long>> real = () -> {
            Collection<Long> collection = supplier.get();
            collection.addAll(inData);
            return collection;
        };
        
        return bench("R"+id, "Remove "+inData.size()+" objs ", inData,  real, consumer);
    }

}
