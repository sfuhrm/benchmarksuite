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

/** Test for {@link IDGenerator}. */
public class IDGeneratorTest {
    @Test
    public void testGenerate() {
        IDGenerator idg = new IDGenerator();
        assertThat(idg.generate(), is(1));
        assertThat(idg.generate(), is(2));
        assertThat(idg.generate(), is(3));
        assertThat(idg.generate(), is(4));
        assertThat(idg.generate(), is(5));
    }
            
    @Test
    public void testGenerateWithTwoInstances() {
        IDGenerator idg1 = new IDGenerator();
        IDGenerator idg2 = new IDGenerator();
        assertThat(idg1.generate(), is(1));
        assertThat(idg1.generate(), is(2));
        assertThat(idg1.generate(), is(3));
        assertThat(idg2.generate(), is(1));
        assertThat(idg1.generate(), is(4));
        assertThat(idg2.generate(), is(2));
    }
}
