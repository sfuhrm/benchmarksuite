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
package de.tynne.benchmarksuite;

/**
 * A benchmark doing nothing.
 * Used to calibrate the measuring.
 * @author Stephan Fuhrmann
 */
class NullBenchmark extends Benchmark<Void> {

    public NullBenchmark() {
        super(() -> null, v -> {}, "Null Benchmark", 1);
        setId("0");
    }
}
