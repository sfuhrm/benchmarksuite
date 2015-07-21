package de.tynne.streamperformance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 *
 * @author fury
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException {        
        BenchmarkProducer benchmarkProducer = new MinimumJ8Benchmarks();
               
        File out = new File("out.csv");
  
        final CSVFormat format = CSVFormat.EXCEL.withHeader("ID", "Name", "Min [ns]", "Avg [ns]", "Max [ns]", "Chart Pos", "Best Increase [%]");
        try (CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(out), Charset.forName("UTF-8")), format)) {
            List<Benchmark> benchmarks = benchmarkProducer.get();
            BenchmarkRunner benchmarkRunner = new BenchmarkRunner(benchmarks);
            benchmarkRunner.run();
            Chart chart = Chart.of(benchmarks);
            
            chart.getPerformanceChart().stream().forEach(
                    b -> {
                        try {
                            DoubleSummaryStatistics doubleSummaryStatistics = chart.getStats().get(b);
                            printer.print(b.getId());
                            printer.print(b.getName());
                            printer.print(doubleSummaryStatistics.getMin());
                            printer.print(doubleSummaryStatistics.getAverage());
                            printer.print(doubleSummaryStatistics.getMax());
                            printer.print(chart.getChart().get(b).chartPosition);
                            double bestAvg = chart.getStats().get(chart.getPerformanceChart().get(0)).getAverage();
                            double thisAvg = doubleSummaryStatistics.getAverage();
                            
                            printer.print(100.*(thisAvg -bestAvg)/bestAvg);
                            printer.println();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
            );
        }
    }
}
