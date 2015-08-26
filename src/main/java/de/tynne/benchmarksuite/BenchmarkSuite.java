/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tynne.benchmarksuite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for {@link BenchmarkProducer} 
 * classes that shall be automatically found and
 * instantiated by the
 * software.
 * @author Stephan Fuhrmann
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.TYPE)
public @interface BenchmarkSuite {
    String name() default "";
    boolean enabled() default true;
}
