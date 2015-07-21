package de.tynne.streamperformance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * @author fury
 */
public class MinimumJ8Benchmarks implements BenchmarkProducer {

    private final Random random = new Random();
    private final int times = 1000;
    
    private Benchmark bench(String id, String name, List<Long> source, Consumer<List<Long>> consumer)  {
        Supplier<List<Long>> supplier = () -> new ArrayList<>(source);
        Benchmark b = new Benchmark<>(supplier, consumer, String.format(name, source.size()), times, source.size());
        b.setId(id);
        return b;
    }


    private List<Benchmark> of(int multi, String suffix) {
        List<Long> longs = random.longs(multi).mapToObj(l -> l).collect(Collectors.toList());


        Benchmark benchmarks[] = new Benchmark[]{
            bench("A"+suffix, 
                "List<Long>.stream.maptoLong().min() with %d", longs,
            (l) -> {
                l.stream().mapToLong(x -> x).min();
            }),
            bench("B"+suffix,
                "List<Long>.parallelStream.maptoLong().min() with %d", longs,
            (l) -> {
                l.parallelStream().mapToLong(x -> x).min();
            }),
            bench("C"+suffix,
                "List<Long>.stream.min() with %d", longs,
            (l) -> {
                l.stream().min((a, b) -> Long.compare(a, b));
            }),
            bench(
                "D"+suffix,
                "List<Long>.parallelStream.min() with %d", longs,
            (l) -> {
                l.parallelStream().min((a, b) -> Long.compare(a, b));
            }),            
            bench(
                "E"+suffix,
                "Plain old for loop with %d", longs,
                        (l) -> {
                Long min = l.get(0);
                for (int i = 1; i < l.size(); i++) {
                    Long cur = l.get(i);
                    if (cur.compareTo(min) < 0) {
                        min = cur;
                    }
                }
            }),
            bench(
                "F"+suffix,
                "Plain old for loop with dual-threading with %d", longs,
                        (l) -> {
                            findMinimumSlicedWithThreading(l, 2);
            }),
            bench(
                "G"+suffix,
                "Plain old for loop with quad-threading with %d", longs,
                        (l) -> {
                            findMinimumSlicedWithThreading(l, 4);
            }),
        };

        return Arrays.asList(benchmarks);    
    }
    
    /** Dirty implementation of a multi threaded sharding approach. */
    private static long findMinimumSlicedWithThreading(List<Long> longs, int threads) {
        List<MinFinder> finders = new ArrayList<>();
        int count = longs.size() / threads;
        int offset = 0;
        for (int i = 0; i < threads; i++) {
            
            if (i == threads - 1) {
                // correct count for potential rounding errors
                count = longs.size() - offset;
            }
            MinFinder finder = new MinFinder(longs, offset, count);
            finder.start();
            finders.add(finder);
            offset += count;
        }
        
        Long min = null;
        for (MinFinder f : finders) {
            try {
                f.join();
                if (min == null) {
                    min = f.minimum;
                } else {                    
                    min = Math.min(f.minimum, min);
                }
            } catch (InterruptedException ex) {
            }
        }
        return min;
    }
    
    private static class MinFinder extends Thread {
        private final List<Long> longs;
        private Long minimum;
        private final int startOffset;
        private final int count;

        public MinFinder(List<Long> longs, int startOffset, int count) {
            this.longs = longs;
            this.startOffset = startOffset;
            this.count = count;
        }
        
        @Override
        public void run() {
            minimum = longs.get(0);
            for (int i = 0; i < count; i++) {
                Long cur = longs.get(i + startOffset);
                if (cur.compareTo(minimum) < 0) {
                    minimum = cur;
                }
            }            
        }
    }
    
    @Override
    public List<Benchmark> get() {
        List<Benchmark> benchmarks = new ArrayList<>();
        
        benchmarks.addAll(of(10, "1"));
        benchmarks.addAll(of(1000, "2"));
        benchmarks.addAll(of(100000, "3"));
        return benchmarks;
    }
}
