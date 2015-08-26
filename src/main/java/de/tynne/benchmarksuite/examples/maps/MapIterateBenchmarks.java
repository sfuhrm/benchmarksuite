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
