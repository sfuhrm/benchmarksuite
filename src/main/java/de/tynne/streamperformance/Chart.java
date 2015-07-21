package de.tynne.streamperformance;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 *
 * @author fury
 */
public class Chart {
    
    public static class ChartEntry {
        @Getter
        private Benchmark benchmark;
        
        @Getter
        int chartPosition;
    }
    
    @Getter
    private final Map<Benchmark, DoubleSummaryStatistics> stats = new HashMap<>();
    
    @Getter
    private final Map<Benchmark, ChartEntry> chart = new HashMap<>();    
    
    @Getter
    private List<Benchmark> performanceChart;
    
    private Chart() {
    }
    
    public static Chart of(List<Benchmark> benchmarks) {
        Chart result = new Chart();
        
        benchmarks.stream().forEach((b) -> {
            DoubleSummaryStatistics doubleSummaryStatistics = b.getNanoTimes().summaryStatistics();
            result.stats.put(b, doubleSummaryStatistics);
        });
        
        result.performanceChart = new ArrayList<>(benchmarks);
        result.performanceChart.sort((a,b) -> Double.compare(result.stats.get(a).getAverage(), result.stats.get(b).getAverage()));

        for (int i=0; i < result.performanceChart.size(); i++) {
            Benchmark b = result.performanceChart.get(i);
            ChartEntry ce = new ChartEntry();
            ce.benchmark = b;
            ce.chartPosition = i+1;
            result.chart.put(b, ce);
        }
        
        return result;
    }
}
