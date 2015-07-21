/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tynne.streamperformance;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;

/**
 *
 * @author fury
 */
public class BackupHelper {
    
    private final static String BAK_SUFFIX = ".bak-";
    
    public static void backupIfNeeded(File f) throws IOException {
        if (f.exists()) {
            File backupName = backupNameFor(f);
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
