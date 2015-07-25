package de.tynne.benchmarksuite;

import java.util.List;
import java.util.function.Supplier;

/** The signature of a benchmark factory class.
 */
public interface BenchmarkProducer extends Supplier<List<Benchmark>> {
}
