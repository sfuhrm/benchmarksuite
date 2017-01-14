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
import static org.hamcrest.CoreMatchers.*;

/** Test for {@link Chart}. */
public class ChartTest {
    @Test
    public void testOfWithEmpty() {
        Chart c = Chart.of(Collections.emptyList());
        assertThat(c.getPerformanceChart(), is(Collections.emptyList()));
        assertThat(c.getChart(), is(Collections.emptyMap()));
        assertThat(c.getStats(), is(Collections.emptyMap()));
    }
            
    @Test
    public void testOfWithSingleBenchmark() {
        Benchmark b = new Benchmark<>(()->1,t->{},"foo",1);
        Chart c = Chart.of(Arrays.asList(b));
        assertThat(c.getPerformanceChart(), is(Collections.singletonList(b)));
        assertThat(c.getStats(), is(Collections.singletonMap(b, b.getStatRecord())));
        assertThat(c.getStats(), is(Collections.singletonMap(b, b.getStatRecord())));
        assertThat(c.getChart().size(), is(1));
        assertThat(c.getChart().get(b).chartPosition, is(1));
    }
}
