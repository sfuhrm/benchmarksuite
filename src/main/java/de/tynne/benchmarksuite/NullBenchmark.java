package de.tynne.benchmarksuite;

/**
 * A benchmark doing nothing.
 * Used to calibrate the measuring.
 * @author Stephan Fuhrmann
 */
class NullBenchmark extends Benchmark<Void> {

    public NullBenchmark() {
        super(() -> null, v -> {}, "Null Benchmark", 1);
        setId("0");
    }
}
