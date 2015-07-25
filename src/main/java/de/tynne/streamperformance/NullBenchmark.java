package de.tynne.streamperformance;

/**
 * A benchmark doing nothing.
 * Used to calibrate the measuring.
 * @author fury
 */
public class NullBenchmark extends Benchmark<Void> {

    public NullBenchmark() {
        super(() -> null, v -> {}, "Null Benchmark", 1);
        setId("0");
    }
}
