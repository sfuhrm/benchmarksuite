package de.tynne.benchmarksuite;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.reflections.Reflections;

/**
 *
 * @author fury
 */
public class Main {

    private static double standardDeviationOf(DoubleStream doubleStream, double average) {
        double sum = doubleStream.map(x -> Math.pow(x - average, 2.)).average().getAsDouble();
        return Math.sqrt(sum);
    }
    
    private static void listBenchmarks(BenchmarkProducer benchmarkProducer, PrintStream ps) {
        benchmarkProducer.get().stream().forEach((b) -> 
            ps.printf("%s: %s\n", b.getId(), b.getName())
        );
        ps.flush();
    }
    
    private static String nameFor(BenchmarkSuite benchmarkSuite, BenchmarkProducer benchmarkProducer) {
        if (benchmarkSuite.name().isEmpty()) {
            return benchmarkProducer.getClass().getSimpleName();
        } else
            return benchmarkSuite.name();
    }
    
    private static void listSuites(Map<BenchmarkSuite, BenchmarkProducer> suites, PrintStream ps) {
        suites.entrySet().stream().sorted((a, b) -> nameFor(a.getKey(), a.getValue()).compareToIgnoreCase(nameFor(b.getKey(), b.getValue()))).
                forEach((e) -> {
                    ps.printf("%s: %s\n",
                            nameFor(e.getKey(), e.getValue()),
                            e.getKey().enabled());
                }
                );
        ps.flush();
    }
    
    private static void nanoNullLoop(int times, List<Long> addTo) {
        for (int i = 0; i < times; i++) {
            long start = System.nanoTime();
            long end = System.nanoTime();
            
            long diff = end - start;
            addTo.add(diff);
        }
    }
    
    private static void checkNano(PrintStream ps) {
        List<Long> diffs = new ArrayList<>();

        nanoNullLoop(1000000, diffs);
        diffs.clear();
        nanoNullLoop(1000000, diffs);
        
        LongSummaryStatistics statistics = diffs.stream().mapToLong(l -> l).summaryStatistics();
        
        ps.printf("min=%d, avg=%g, max=%d\n", statistics.getMin(), statistics.getAverage(), statistics.getMax());
    }

    private static void runBenchmarks(Args args, BenchmarkProducer benchmarkProducer) throws IOException {
        BackupHelper.backupIfNeeded(args.getOutput());
        
        // this looks like NOT comma seperated values, but excel and libreoffice load this automatically
        final CSVFormat format = CSVFormat.EXCEL.withDelimiter(';').withHeader("#", "ID", "Name", "Min [ns]", "Avg [ns]", "Median [ns]", "Max [ns]", "Std Dev [ns]", "Chart Pos", "Best Increase [%]");
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
                    DoubleSummaryStatistics doubleSummaryStatistics = chart.getStats().get(b);
                    printer.print(matching.indexOf(b));
                    printer.print(b.getId());
                    printer.print(b.getName());
                    printer.print(doubleSummaryStatistics.getMin());
                    printer.print(doubleSummaryStatistics.getAverage());
                    printer.print(b.getMedian().getAsDouble());
                    printer.print(doubleSummaryStatistics.getMax());
                    printer.print(standardDeviationOf(b.getNanoTimes(), doubleSummaryStatistics.getAverage()));
                    printer.print(chart.getChart().get(b).chartPosition);
                    double bestAvg = chart.getStats().get(chart.getPerformanceChart().get(0)).getAverage();
                    double thisAvg = doubleSummaryStatistics.getAverage();

                    printer.print(100. * (thisAvg - bestAvg) / bestAvg);
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
        return suites.entrySet().stream().filter(e -> nameFor(e.getKey(), e.getValue()).equalsIgnoreCase(suite)).map(e -> e.getValue()).findFirst();
    }
    
    /** Returns a producer for all benchmarks in the named suites.
     */
    private static BenchmarkProducer findAllByName(List<String> suiteNames, Map<BenchmarkSuite, BenchmarkProducer> suites) {
        List<BenchmarkProducer> benchmarkProducers = suiteNames.stream().
                map(s -> findByName(s, suites)).
                filter(bp -> bp.isPresent()).
                map(o -> o.get()).
                collect(Collectors.toList());

        return () -> {
            List<Benchmark> result = new ArrayList<>();
            benchmarkProducers.forEach(bp -> {
                result.addAll(bp.get());
            });
            return result;
        };
    }
    
    private static Map<BenchmarkSuite, BenchmarkProducer> getBenchmarkSuites() throws InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections("de.tynne.benchmarksuite");

        Set<Class<?>> benchmarkSuites = reflections.getTypesAnnotatedWith(BenchmarkSuite.class);
        
        Map<BenchmarkSuite, BenchmarkProducer> result = new HashMap<>();
        for (Class<?> clazz : benchmarkSuites) {
            BenchmarkSuite benchmarkSuite = clazz.getAnnotation(BenchmarkSuite.class);
            BenchmarkProducer benchmarkProducer = clazz.asSubclass(BenchmarkProducer.class).newInstance();
            result.put(benchmarkSuite, benchmarkProducer);
        }
        return result;
    }
    
    public static void main(String[] cmdLine) throws Exception {  
        Args args = Args.parse(cmdLine);
        if (args == null) {
            return;
        }
        
        Map<BenchmarkSuite, BenchmarkProducer> suites = getBenchmarkSuites();
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
