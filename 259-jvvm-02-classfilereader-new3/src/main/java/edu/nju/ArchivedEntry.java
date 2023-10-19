package edu.nju;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * format : dir/subdir/target.jar
 */
public class ArchivedEntry extends Entry{
    public ArchivedEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClassFile(String className) throws IOException {
        File dir = new File(classpath);
        String absoluteDir = dir.getAbsolutePath();
        ZipFile zipfile = new ZipFile(absoluteDir);
        ZipEntry entry = zipfile.getEntry(StringUtils.replace(className, FILE_SEPARATOR, "/"));
        if (entry != null) {
            return IOUtil.readFileByBytes(zipfile.getInputStream(entry));
        }
        return null;
    }
}
