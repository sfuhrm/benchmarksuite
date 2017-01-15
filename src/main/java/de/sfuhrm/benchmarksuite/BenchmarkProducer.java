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
package de.sfuhrm.benchmarksuite;

import java.util.List;
import java.util.function.Supplier;

/** The signature of a benchmark factory class.
 * @see BenchmarkSuite
 * @author Stephan Fuhrmann
 */
public interface BenchmarkProducer extends Supplier<List<Benchmark>> {
    
    /** Gets the name for a benchmark producer.
     * @param benchmarkSuite the annotation for the suite.
     * @param benchmarkProducer the benchmark producer.
     * @return the name of the benchmarkSuite annotation, or the class name of the producer if the annotation name is empty.
     */
    static String nameFor(BenchmarkSuite benchmarkSuite, BenchmarkProducer benchmarkProducer) {
        if (benchmarkSuite.name().isEmpty()) {
            return benchmarkProducer.getClass().getSimpleName();
        } else
            return benchmarkSuite.name();
    }
}
