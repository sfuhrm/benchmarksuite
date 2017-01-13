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
package de.sfuhrm.benchmarksuite.examples.collections;

import de.sfuhrm.benchmarksuite.Benchmark;
import de.sfuhrm.benchmarksuite.BenchmarkProducer;
import de.sfuhrm.benchmarksuite.BenchmarkSuite;
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
