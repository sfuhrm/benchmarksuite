package de.tynne.benchmarksuite;

import de.tynne.benchmarksuite.examples.CryptoBenchmarks;
import de.tynne.benchmarksuite.examples.JDK8StreamBenchmarks;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

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
    
    public static void main(String[] cmdLine) throws FileNotFoundException, IOException {  
        Args args = Args.parse(cmdLine);
        if (args == null) {
            return;
        }
        
        BenchmarkProducer benchmarkProducer = new CryptoBenchmarks(); //new JDK8StreamBenchmarks();
        
        if (args.isList()) {
            listBenchmarks(benchmarkProducer, System.out);
            return;
        }

        BackupHelper.backupIfNeeded(args.getOutput());
        
        // this looks like NOT comma seperated values, but excel and libreoffice load this automatically
        final CSVFormat format = CSVFormat.EXCEL.withDelimiter(';').withHeader("#", "ID", "Name", "Min [ns]", "Avg [ns]", "Median [ns]", "Max [ns]", "Std Dev [ns]", "Chart Pos", "Rel Increase [%]", "Best Increase [%]");
        try (CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(args.getOutput()), Charset.forName(args.getCharset())), format)) {
            List<Benchmark> benchmarks = benchmarkProducer.get();
            List<Benchmark> matching = benchmarks.stream().filter(b -> args.getExecute().matcher(b.getId()).matches()).collect(Collectors.toList());
            BenchmarkRunner benchmarkRunner = new BenchmarkRunner(matching, 
                    BenchmarkRunner.SEC_IN_NANOS * args.getWarumUpTime(), 
                    BenchmarkRunner.SEC_IN_NANOS * args.getRunTime());
            benchmarkRunner.run();
            Chart chart = Chart.of(matching);
            
            Benchmark pred = null;
            for (Benchmark b : chart.getPerformanceChart()) {
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
                    double predAvg = pred != null ? chart.getStats().get(pred).getAverage() : thisAvg;

                    printer.print(100. * (thisAvg - predAvg) / predAvg);
                    printer.print(100. * (thisAvg - bestAvg) / bestAvg);
                    printer.println();
                    pred = b;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
