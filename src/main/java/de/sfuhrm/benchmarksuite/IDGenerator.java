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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates unique IDs for benchmarks.
 * @author Stephan Fuhrmann
 */
class IDGenerator {
    
    private final AtomicInteger atomicInteger = new AtomicInteger();
    
    public Integer generate() {
        return atomicInteger.addAndGet(1);
    }
    
}
