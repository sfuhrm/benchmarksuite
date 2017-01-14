# Benchmark Suite ![Travis CI Status](https://travis-ci.org/sfuhrm/benchmarksuite.svg?branch=master)

## What is it?

A young framework for executing Java code benchmarks.

### Basic concept

The concept of the suite is to run multiple benchmarks in a row
and then output the results in CSV format.

Each benchmark run is split into a warmup phase and a run phase.
The warmup phase is to fill the cache lines and/or to give the JIT compiler
of the JDK a little time to compile and optimize the code.

### Suite versus benchmark

A suite is a set of benchmarks that somehow belong to each other.
An example would be a benchmark for reading files with 1k, 4k, 8k and 16k.
Each size would be tested by a benchmark, and all benchmarks
together are a suite.

## Usage

Usage is thru the command line. 

The following options are available:

```
 -charset (-c) VAL                      : Output CSV file charset to use.
                                          (default: ISO-8859-15)
 -check-nano (-C, -checkNano)           : Check nano timing of JDK (default:
                                          false)
 -decimal-dot (-d) VAL                  : The decimal dot sign to use (default:
                                          .)
 -execute (-e) REGEX                    : Regex defining which benchmark IDs to
                                          execute. (default: .*)
 -help (-h)                             : Show this command line help.
                                          (default: true)
 -list-benchmarks (-l)                  : Show the available benchmarks and
                                          then exit (default: false)
 -list-suites (-L)                      : Show the available benchmark suites
                                          and then exit (default: false)
 -output (-out, -o) FILE                : Output CSV file to write to.
                                          (default: out.csv)
 -package-root (-R) VAL                 : The root package to search benchmark
                                          suites in. (default: de.sfuhrm.benchma
                                          rksuite)
 -run-time (-r, -run, -runTime) SECS    : Run up time in seconds for each
                                          benchmark. The performance within
                                          this IS benchmarked. (default: 30)
 -suite (-s) VAL                        : The benchmark suite(s) to execute
                                          benchmarks of.
 -warm-up-time (-w, -warmup,            : Warm up time in seconds for each
 -warmUpTime) SECS                        benchmark. The performance within
                                          this is NOT benchmarked. (default: 5)```

## Example

A simple Excel-graphed example can be found in the following graph:

![Benchmark example](http://sfuhrm.de/wp-content/uploads/2015/07/cetus-10.png)

## License

Copyright 2015 Stephan Fuhrmann

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
