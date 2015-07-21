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
    
    private Benchmark bench(String name, List<Long> source, Consumer<List<Long>> consumer)  {
        Supplier<List<Long>> supplier = () -> new ArrayList<>(source);
        Benchmark b = new Benchmark<>(supplier, consumer, String.format(name, source.size()), times, source.size());
        return b;
    }


    private List<Benchmark> of(int multi) {
        List<Long> longs = random.longs(multi).mapToObj(l -> l).collect(Collectors.toList());


        Benchmark benchmarks[] = new Benchmark[]{
            bench("List<Long>.stream.maptoLong().min() with %d", longs,
            (l) -> {
                l.stream().mapToLong(x -> x).min();
            }),
            bench("List<Long>.parallelStream.maptoLong().min() with %d", longs,
            (l) -> {
                l.parallelStream().mapToLong(x -> x).min();
            }),
            bench("List<Long>.stream.min() with %d", longs,
            (l) -> {
                l.stream().min((a, b) -> Long.compare(a, b));
            }),
            bench("List<Long>.parallelStream.min() with %d", longs,
            (l) -> {
                l.parallelStream().min((a, b) -> Long.compare(a, b));
            }),            
            bench("Plain old for loop with %d", longs,
                        (l) -> {
                Long min = l.get(0);
                for (int i = 1; i < l.size(); i++) {
                    Long cur = l.get(i);
                    if (cur.compareTo(min) < 0) {
                        min = cur;
                    }
                }
            }),
        };

        return Arrays.asList(benchmarks);    
    }
    
    @Override
    public List<Benchmark> get() {
        List<Benchmark> benchmarks = new ArrayList<>();
        
        benchmarks.addAll(of(10));
        benchmarks.addAll(of(1000));
        benchmarks.addAll(of(100000));
        return benchmarks;
    }
}
