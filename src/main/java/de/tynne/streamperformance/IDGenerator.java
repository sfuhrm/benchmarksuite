package de.tynne.streamperformance;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author fury
 */
public class IDGenerator {
    
    private final AtomicInteger atomicInteger = new AtomicInteger();
    
    public Integer generate() {
        return atomicInteger.addAndGet(1);
    }
    
}
