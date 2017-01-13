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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import lombok.extern.slf4j.Slf4j;

/**
 * Some logic for backing up existing files.
 * @author Stephan Fuhrmann
 */
@Slf4j
class BackupHelper {
    
    private final static String BAK_SUFFIX = ".bak-";
    
    public static void backupIfNeeded(File f) throws IOException {
        log.debug("Checking for file {}", f.getAbsolutePath());
        if (f.exists() && f.length() > 0) {
            File backupName = backupNameFor(f);
            log.debug("Renaming file {} to {}", f.getAbsolutePath(), backupName.getAbsolutePath());
            f.renameTo(backupName);
        }
    }
    
    private static File backupNameFor(File f) throws IOException {
        StringBuilder nameBuilder = new StringBuilder();
        
        String name = f.getName();
        nameBuilder.append(name);
        nameBuilder.append(BAK_SUFFIX);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        FileTime fileTime = Files.getLastModifiedTime(f.toPath());
        String timePart = dateFormat.format(fileTime.toMillis());
        
        nameBuilder.append(timePart);
        return new File(f.getParentFile(), nameBuilder.toString());
    }
}
