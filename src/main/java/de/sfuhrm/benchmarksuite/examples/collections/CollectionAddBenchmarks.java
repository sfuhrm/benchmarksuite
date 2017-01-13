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
