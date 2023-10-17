package edu.nju;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
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
        Enumeration<? extends ZipEntry> entries = zipfile.entries();
        while(entries.hasMoreElements()){
            ZipEntry ze = entries.nextElement();
            String zeName = IOUtil.transform(ze.getName());
            if(zeName.equals(className)){
                return IOUtil.readFileByBytes(zipfile.getInputStream(ze));
            }
        }
        return null;
    }
}
