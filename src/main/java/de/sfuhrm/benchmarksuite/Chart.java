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

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Getter;

/**
 * A comparison chart for multiple benchmarks.
 * @see #of(java.util.List) 
 * @author Stephan Fuhrmann
 */
public class Chart {
    
    /** Single chart entry with comparative content. */
    public static class ChartEntry {
        @Getter
        private Benchmark benchmark;
        
        @Getter
        int chartPosition;
    }
    
    @Getter
    private final Map<Benchmark, StatRecord> stats = new HashMap<>();
    
    @Getter
    private final Map<Benchmark, ChartEntry> chart = new HashMap<>();    
    
    @Getter
    private List<Benchmark> performanceChart;
    
    private final static Function<StatRecord, Number> SORTING_CRITERIUM = StatRecord::getMin;
    
    private Chart() {
    }
    
    public static Chart of(List<Benchmark> benchmarks) {
        Chart result = new Chart();
        
        benchmarks.stream().forEach((b) -> {
            StatRecord statRecord = b.getStatRecord();
            result.stats.put(b, statRecord);
        });
        
        result.performanceChart = new ArrayList<>(benchmarks);
        result.performanceChart.sort((a,b) -> Double.compare(SORTING_CRITERIUM.apply(result.stats.get(a)).doubleValue(), SORTING_CRITERIUM.apply(result.stats.get(b)).doubleValue()));

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
