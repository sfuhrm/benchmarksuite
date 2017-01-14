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

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/** Test for {@link StatRecord}. 
 * @author Stephan Fuhrmann
 */
public class StatRecordTest {
    @Test
    public void testInit() {
        StatRecord r = new StatRecord();
        assertThat(r.getAverage(), is(0.));
        assertThat(r.getMax(), is(0.));
        assertThat(r.getMin(), is(0.));
        assertThat(r.getSum(), is(0.));
        assertThat(r.getCount(), is(0));
    }
            
    @Test
    public void testPutWithSingle() {
        StatRecord r = new StatRecord();
        r.put(42);
        assertThat(r.getAverage(), is(42.));
        assertThat(r.getMax(), is(42.));
        assertThat(r.getMin(), is(42.));
        assertThat(r.getSum(), is(42.));
        assertThat(r.getCount(), is(1));
    }
    
    @Test
    public void testPutWithTriple() {
        StatRecord r = new StatRecord();
        r.put(42);
        r.put(4);
        r.put(-5);
        assertThat(r.getAverage(), is((42.+4.-5.)/3.));
        assertThat(r.getMax(), is(42.));
        assertThat(r.getMin(), is(-5.));
        assertThat(r.getSum(), is((42.+4.-5.)));
        assertThat(r.getCount(), is(3));
    }
    
    @Test
    public void testScaleTo() {
        StatRecord r = new StatRecord();
        r.put(42);
        r.put(4);
        r.put(-5);
        StatRecord t = r.scaleTo(0.5);
        assertThat(t.getAverage(), is((42.+4.-5.)/6.));
        assertThat(t.getMax(), is(21.));
        assertThat(t.getMin(), is(-2.5));
        assertThat(t.getSum(), is((42.+4.-5.)*0.5));
        assertThat(t.getCount(), is(3));
    }
}
