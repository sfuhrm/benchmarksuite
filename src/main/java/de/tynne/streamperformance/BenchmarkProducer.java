package de.tynne.streamperformance;

import java.util.List;
import java.util.function.Supplier;


public interface BenchmarkProducer extends Supplier<List<Benchmark>> {
}
