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

import java.util.function.Consumer;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

/** Test for {@link Benchmark}.
 * @author Stephan Fuhrmann
 */
public class BenchmarkTest {
    @Test
    public void testInit() {
        Benchmark b = new Benchmark(() -> 1, t -> {}, "foo", 1);
        assertNotNull(b.getId());
        assertThat(b.getName(), is("foo"));
        assertThat(b.getMultiplicity(), is(1L));
        assertThat(b.getNullBenchmark(), nullValue());
        assertThat(b.getStatRecord(), notNullValue());
        assertThat(b.getStatRecord().getCount(), is(0));
        assertThat(b.getStatRecord().getMin(), is(0.));
        assertThat(b.getStatRecord().getMax(), is(0.));
        assertThat(b.getStatRecord().getAverage(), is(0.));
    }

    @Test
    public void testRun() {
        Consumer<Integer> consumer =  mock(Consumer.class);
        Benchmark b = new Benchmark(() -> 1, consumer, "foo", 1);
        b.run();

        assertNotNull(b.getId());
        assertThat(b.getName(), is("foo"));
        assertThat(b.getMultiplicity(), is(1L));
        assertThat(b.getNullBenchmark(), nullValue());
        assertThat(b.getStatRecord(), notNullValue());
        verify(consumer).accept(1);
        assertThat(b.getStatRecord().getCount(), is(1));
    }

    @Test
    public void testReset() {
        Consumer<Integer> consumer =  mock(Consumer.class);
        Benchmark b = new Benchmark(() -> 1, consumer, "foo", 1);
        b.run();
        b.reset();

        assertNotNull(b.getId());
        assertThat(b.getName(), is("foo"));
        assertThat(b.getMultiplicity(), is(1L));
        assertThat(b.getNullBenchmark(), nullValue());
        assertThat(b.getStatRecord(), notNullValue());
        assertThat(b.getStatRecord().getCount(), is(0));
        assertThat(b.getStatRecord().getMin(), is(0.));
        assertThat(b.getStatRecord().getMax(), is(0.));
        assertThat(b.getStatRecord().getAverage(), is(0.));
    }
}
