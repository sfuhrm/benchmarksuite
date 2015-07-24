/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tynne.streamperformance;

import java.io.File;
import java.lang.reflect.Field;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 *
 * @author fury
 */
@Slf4j
public class Args {
    
    private final long DEFAULT_WARMUP_TIME_SECS = 5;
    private final long DEFAULT_RUN_TIME_SECS = 30;
        
    @Getter
    @Option(name = "-warmUpTime", aliases = {"-w","-warmup"}, metaVar = "SECS", usage = "Warm up time in seconds for each benchmark. The performance within this is NOT benchmarked.")
    private long warumUpTime = DEFAULT_WARMUP_TIME_SECS;
    
    @Getter
    @Option(name = "-runTime", aliases = {"-r","-run"}, metaVar = "SECS", usage = "Run up time in seconds for each benchmark. The performance within this IS benchmarked.")
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
