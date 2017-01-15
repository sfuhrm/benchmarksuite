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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.reflections.Reflections;

/**
 * Main class that parses the CLI parameters and starts everything.
 * @author Stephan Fuhrmann
 */
public class Main {
    
    private static void listBenchmarks(BenchmarkProducer benchmarkProducer, PrintStream ps) {
        benchmarkProducer.get().stream().forEach((b) -> 
            ps.printf("%s: %s\n", b.getId(), b.getName())
        );
        ps.flush();
    }
    
    private static void listSuites(Map<BenchmarkSuite, BenchmarkProducer> suites, PrintStream ps) {
        suites
                .entrySet()
                .stream()
                .sorted((a, b) -> BenchmarkProducer.nameFor(a.getKey(), a.getValue()).compareToIgnoreCase(BenchmarkProducer.nameFor(b.getKey(), b.getValue())))
                .forEach((e) -> 
                    ps.printf("%s: %s\n",
                            BenchmarkProducer.nameFor(e.getKey(), e.getValue()),
                            e.getKey().enabled() ? "enabled" : "disabled")
                );
        ps.flush();
    }
    
    /** Measures the time between two measurement calls. 
     * @param times the number of tests to run.
     * @param addTo the list to add the time delta in nanos.
     */
    private static void nanoNullLoop(int times, List<Long> addTo) {
        for (int i = 0; i < times; i++) {
            long start = System.nanoTime();
            long end = System.nanoTime();
            
            long diff = end - start;
            addTo.add(diff);
        }
    }
    
    /** Checks the nano timing of the JDK using {@link #nanoNullLoop(int, java.util.List)}. */
    private static void checkNano(PrintStream ps) {
        List<Long> diffs = new ArrayList<>();

        nanoNullLoop(1000000, diffs);
        diffs.clear();
        nanoNullLoop(1000000, diffs);
        
        LongSummaryStatistics statistics = diffs.stream().mapToLong(l -> l).summaryStatistics();
        
        ps.printf("min=%d, avg=%g, max=%d\n", statistics.getMin(), statistics.getAverage(), statistics.getMax());
    }
    
    /** Formats a number using the {@link Args#decimalDot}. */
    private static String format(Args args, double number) throws IOException {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        nf.setGroupingUsed(false);
        String string = nf.format(number);
        return string.replaceAll("\\.", args.getDecimalDot());
    }

    /** Runs the benchmarks given in the command line. */
    private static void runBenchmarks(Args args, BenchmarkProducer benchmarkProducer) throws IOException {
        BackupHelper.backupIfNeeded(args.getOutput());
                
        // this looks like NOT comma seperated values, but excel and libreoffice load this automatically
        final CSVFormat format = CSVFormat.EXCEL.withDelimiter(';').withHeader("#", "ID", "Name", "Min [ns]", "Avg [ns]", "Max [ns]", "Chart Pos", "Best Increase [%]", "Iterations");
        try (CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(args.getOutput()), Charset.forName(args.getCharset())), format)) {
            List<Benchmark> benchmarks = benchmarkProducer.get();
            List<Benchmark> matching = benchmarks.stream().filter(b -> args.getExecute().matcher(b.getId()).matches()).collect(Collectors.toList());
            BenchmarkRunner benchmarkRunner = new BenchmarkRunner(matching,
                    BenchmarkRunner.SEC_IN_NANOS * args.getWarumUpTime(), 
                    BenchmarkRunner.SEC_IN_NANOS * args.getRunTime());
            benchmarkRunner.run();
            Chart chart = Chart.of(matching);
            
            for (Benchmark b : matching) {
                try {
                    StatRecord statRecord = chart.getStats().get(b);
                    printer.print(matching.indexOf(b));
                    printer.print(b.getId());
                    printer.print(b.getName());
                    printer.print(format(args, statRecord.getMin()));
                    printer.print(format(args, statRecord.getAverage()));
                    printer.print(format(args, statRecord.getMax()));
                    printer.print(chart.getChart().get(b).chartPosition);
                    double bestAvg = chart.getStats().get(chart.getPerformanceChart().get(0)).getAverage();
                    double thisAvg = statRecord.getAverage();

                    printer.print(format(args, 100. * (thisAvg - bestAvg) / bestAvg));
                    printer.print(statRecord.getCount());
                    printer.println();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    /** Returns a single suite for benchmarks by name.
     */
    private static Optional<BenchmarkProducer> findByName(String suite, Map<BenchmarkSuite, BenchmarkProducer> suites) {
        return suites
                .entrySet()
                .stream()
                .filter(e -> BenchmarkProducer.nameFor(e.getKey(), e.getValue()).equalsIgnoreCase(suite))
                .map(e -> e.getValue()).findFirst();
    }
    
    /** Returns a producer for all benchmarks in the named suites.
     */
    private static BenchmarkProducer findAllByName(List<String> suiteNames, Map<BenchmarkSuite, BenchmarkProducer> suites) {
        List<BenchmarkProducer> benchmarkProducers = suiteNames.stream()
                .map(s -> findByName(s, suites))
                .filter(bp -> bp.isPresent())
                .map(o -> o.get())
                .collect(Collectors.toList());

        return () -> 
            benchmarkProducers
                    .stream()
                    .flatMap(bp -> bp.get().stream())
                    .collect(Collectors.toList());
    }
    
    /** Lists all available benchmark suites within the package subhierarchy relative to the package prefix. 
     */
    static Map<BenchmarkSuite, BenchmarkProducer> listBenchmarkSuites(String packagePrefix) throws InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections(packagePrefix);

        Set<Class<?>> benchmarkSuites = reflections.getTypesAnnotatedWith(BenchmarkSuite.class);
        
        Map<BenchmarkSuite, BenchmarkProducer> result = new HashMap<>();
        for (Class<?> clazz : benchmarkSuites) {
            BenchmarkSuite benchmarkSuite = clazz.getAnnotation(BenchmarkSuite.class);
            BenchmarkProducer benchmarkProducer = clazz.asSubclass(BenchmarkProducer.class).newInstance();
            result.put(benchmarkSuite, benchmarkProducer);
        }
        return result;
    }
    
    public static void main(String... cmdLine) throws Exception {  
        Args args = Args.parse(cmdLine);
        if (args == null) {
            return;
        }
        
        Map<BenchmarkSuite, BenchmarkProducer> suites = listBenchmarkSuites(args.getSuiteSearchPackagePrefix());
        if (args.isListSuites()) {
            listSuites(suites, System.out);
            return;
        }
        
        
        BenchmarkProducer benchmarkProducer = findAllByName(args.getSuites(), suites);
        
        if (args.isListBenchmarks()) {
            listBenchmarks(benchmarkProducer, System.out);
            return;
        }
        
        if (args.isCheckNano()) {
            checkNano(System.out);
            return;
        }

        runBenchmarks(args, benchmarkProducer);
    }
}
