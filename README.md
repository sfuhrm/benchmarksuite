# Benchmark Suite

## What is it?

A young framework for executing Java code benchmarks.

### Basic concept

The concept of the suite is to run multiple benchmarks in a row
and then output the results in CSV format.

Each benchmark run is split into a warmup phase and a run phase.
The warmup phase is to fill the cache lines and/or to give the JIT compiler
of the JDK a little time to compile and optimize the code.

## Usage

Usage is thru the command line. 

The following options are available:

...


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