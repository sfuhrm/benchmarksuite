package de.tynne.benchmarksuite;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates unique IDs for benchmarks.
 * @author Stephan Fuhrmann
 */
class IDGenerator {
    
    private final AtomicInteger atomicInteger = new AtomicInteger();
    
    public Integer generate() {
        return atomicInteger.addAndGet(1);
    }
    
}
