/*
 * Copyright 2017 Stephan Fuhrmann.
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

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

/** Test for {@link BenchmarkRunner}. 
 * @author Stephan Fuhrmann
 */
public class BenchmarkRunnerTest {
    @Test
    public void testRunWithEmpty() {
        BenchmarkRunner b = new BenchmarkRunner(Collections.emptyList(), 1, 1);
        assertEquals(Collections.emptyList(), b.getBenchmarks());
        assertNotNull(b.getNullBenchmark());
        b.run();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInitWithNegativeWarmup() {
        BenchmarkRunner b = new BenchmarkRunner(Collections.emptyList(), -1, 1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInitWithNegativeRum() {
        BenchmarkRunner b = new BenchmarkRunner(Collections.emptyList(), 1, -1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInitWithSingleBenchmark() {
        BenchmarkRunner b = new BenchmarkRunner(Arrays.asList(new Benchmark(() -> 0, t -> {}, "Foo", 1)), 1, -1);
        assertNull(b.getBenchmarks().get(0).getStatRecord());
        b.run();
        assertNotNull(b.getBenchmarks().get(0).getStatRecord());
    }
}
