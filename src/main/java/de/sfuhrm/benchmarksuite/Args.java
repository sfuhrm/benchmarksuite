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

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Command line arguments for running the benchmarks.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class Args {
    
    private final long DEFAULT_WARMUP_TIME_SECS = 5;
    private final long DEFAULT_RUN_TIME_SECS = 30;
        
    @Getter
    @Option(name = "-warm-up-time", aliases = {"-w","-warmup","-warmUpTime"}, metaVar = "SECS", usage = "Warm up time in seconds for each benchmark. The performance within this is NOT benchmarked.")
    private long warumUpTime = DEFAULT_WARMUP_TIME_SECS;
    
    @Getter
    @Option(name = "-run-time", aliases = {"-r","-run","-runTime"}, metaVar = "SECS", usage = "Run up time in seconds for each benchmark. The performance within this IS benchmarked.")
    private long runTime = DEFAULT_RUN_TIME_SECS;
    
    @Getter
    @Option(name = "-output", aliases = {"-out","-o"}, usage = "Output CSV file to write to.")
    private File output = new File("out.csv");
    
    @Getter
    @Option(name = "-charset", aliases = {"-c"}, usage = "Output CSV file charset to use.")
    private String charset = "ISO-8859-15";
    
    @Getter
    @Option(name = "-execute", aliases = {"-e"}, usage = "Regex defining which benchmark IDs to execute.")
    private Pattern execute = Pattern.compile(".*");
    
    @Getter
    @Option(name = "-list-suites", aliases = {"-L"}, usage = "Show the available benchmark suites and then exit")
    private boolean listSuites;
    
    @Getter
    @Option(name = "-suite", aliases = {"-s"}, usage = "The benchmark suite(s) to execute benchmarks of.")
    private List<String> suites;
    
    @Getter
    @Option(name = "-list-benchmarks", aliases = {"-l"}, usage = "Show the available benchmarks and then exit")
    private boolean listBenchmarks;
    
    @Getter
    @Option(name = "-check-nano", aliases = {"-C","-checkNano"}, usage = "Check nano timing of JDK")
    private boolean checkNano;
    
    @Getter
    @Option(name = "-decimal-dot", aliases = {"-d"}, usage = "The decimal dot sign to use")
    private String decimalDot = ".";
    
    @Getter
    @Option(name = "-package-root", aliases = {"-R"}, usage = "The root package to search benchmark suites in.")
    private String suiteSearchPackagePrefix = "de.sfuhrm.benchmarksuite";
    
    @Getter
    @Option(name = "-help", aliases = {"-h"}, usage = "Show this command line help.", help = true)
    private boolean help;
        
    private Args() {
    }
    
    public static Args parse(String in[]) {
        Args result = new Args();
        
        CmdLineParser cmdLineParser = new CmdLineParser(result);
        boolean showHelp = false;
        try {
            cmdLineParser.parseArgument(in);
            showHelp |= result.help;
            
            result.logProperties();
            
            if ((result.suites == null || result.suites.isEmpty()) && ! result.listSuites) {
                System.err.println("Need a suite specified.");
                showHelp = true;
            }
            
        } catch (CmdLineException ex) {
            System.err.println(ex.getMessage());
            showHelp = true;
        }

        if (showHelp) {
            cmdLineParser.printUsage(System.err);
            result = null;
        }
        
        return result;
    }
    
    private void logProperties() {
        for (Field field : getClass().getFields()) {
            try {
                field.setAccessible(true);
                log.info("Field {}: {}", field.getName(), field.get(this));
            } catch (IllegalAccessException | IllegalArgumentException ex) {
                log.warn("Field "+field.getName()+" unreadable", ex);
            }
        }
    }
}
