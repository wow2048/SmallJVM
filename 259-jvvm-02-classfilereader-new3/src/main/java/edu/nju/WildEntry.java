package edu.nju;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * format : dir/.../*
 */
public class WildEntry extends Entry{
    public WildEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClassFile(String className) throws IOException {
        classpath = classpath.substring(0, classpath.length() - 2);
        File dir = new File(classpath);
        List<String> fileArr = new ArrayList<>();
        if (dir.isDirectory()) {
            String[] items = dir.list();
            for (String each : items) {
                if (each.contains("jar") || each.contains("JAR")) {
                    fileArr.add(classpath + FILE_SEPARATOR + each);
                }
            }
            return new CompositeEntry(StringUtils.join(fileArr, PATH_SEPARATOR)).readClassFile(className);
        }
        return null;
    }
}
