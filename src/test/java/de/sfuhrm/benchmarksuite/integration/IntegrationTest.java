/*
 * Copyright 2017 Stephan Fuhrmann.
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
package de.sfuhrm.benchmarksuite.integration;

import de.sfuhrm.benchmarksuite.Main;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


/**
 * Integration test running a whole suite and then checking
 * whether the produced CSV file looks sane.
 * @author Stephan Fuhrmann
 */
public class IntegrationTest {

    @Test
    public void testWithNoArgs() throws Exception {
        Main.main(new String[0]);
    }
    
    @Test
    public void testWithCollectionsIterateSuite() throws Exception {
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputBytes, true, "UTF-8"));
        ByteArrayOutputStream errorBytes = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errorBytes, true, "UTF-8"));
        File csvOutput = File.createTempFile(getClass().getName(), ".csv");
        Main.main(
                "-suite", "collectionsIterate", 
                "-output", csvOutput.getAbsolutePath(),
                "-runTime", "2",
                "-warmUpTime", "1");
        
        outputBytes.close();
        String output = outputBytes.toString("UTF-8");
        errorBytes.close();
        String error = errorBytes.toString("UTF-8");
        
        assertThat(output, is(""));
        assertThat(error, not(""));
        
        CSVParser parser = CSVParser.parse(csvOutput, Charset.forName("ISO-8859-15"), CSVFormat.EXCEL.withDelimiter(';'));
        List<CSVRecord> records = parser.getRecords();
        assertThat(records.get(1).get(0), is("0"));
        assertThat(records.get(1).get(1), is("IA1"));
        assertThat(records.get(1).get(2), is("Iterate 10 objs LinkedHashSet"));
        
        assertThat(records.get(2).get(0), is("1"));
        assertThat(records.get(2).get(1), is("IB1"));
        assertThat(records.get(2).get(2), is("Iterate 10 objs HashSet"));
        
        assertThat(records.get(3).get(0), is("2"));
        assertThat(records.get(3).get(1), is("IC1"));
        assertThat(records.get(3).get(2), is("Iterate 10 objs TreeSet"));

        assertThat(records.size(), is(22));

        csvOutput.delete();
    }
}
