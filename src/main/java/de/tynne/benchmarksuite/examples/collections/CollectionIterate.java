package de.tynne.benchmarksuite.examples.collections;

import de.tynne.benchmarksuite.Benchmark;
import de.tynne.benchmarksuite.BenchmarkProducer;
import de.tynne.benchmarksuite.BenchmarkSuite;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@BenchmarkSuite(name = "CollectionsIterate")
public class CollectionIterate extends AbstractCollectionBenchmarks implements BenchmarkProducer {

    public CollectionIterate() {
    }
    
    @Override
    protected Benchmark operation(String id, List<Long> inData, Supplier<Collection<Long>> supplier) {
        Consumer<Collection<Long>> consumer = l -> {
            Iterator<Long> iter = l.iterator();
            while (iter.hasNext()) {
                iter.next();
            }
        };
        
        Supplier<Collection<Long>> real = () -> {
            Collection<Long> collection = supplier.get();
            collection.addAll(inData);
            return collection;
        };
        
        return bench("I"+id, "Iterate "+inData.size()+" objs ", inData,  real, consumer);
    }

}
