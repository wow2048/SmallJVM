package edu.nju;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * format : dir/.../*
 */
public class WildEntry extends Entry{
    public WildEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClassFile(String className) throws IOException {
        classpath = classpath.substring(0, classpath.length()-2);
        File dir = new File(classpath);
        String absoluteDir = dir.getAbsolutePath();
        String[] fileNames = dir.list();
        for(String each: fileNames){
            if(each.contains("jar") || each.contains("JAR")){
                ZipFile zipfile = new ZipFile(String.join(FILE_SEPARATOR, absoluteDir, each));
                Enumeration<? extends ZipEntry> entries = zipfile.entries();
                while(entries.hasMoreElements()){
                    ZipEntry ze = entries.nextElement();
                    String zeName = IOUtil.transform(ze.getName());
                    if(zeName.equals(className)){
                        return IOUtil.readFileByBytes(zipfile.getInputStream(ze));
                    }
                }

            }
        }
        return null;
    }
}
