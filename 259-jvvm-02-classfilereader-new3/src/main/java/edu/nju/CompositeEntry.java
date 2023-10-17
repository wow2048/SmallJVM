package edu.nju;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * format : dir/subdir;dir/subdir/*;dir/target.jar*
 */
public class CompositeEntry extends Entry{
    public CompositeEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClassFile(String className) throws IOException {
        String[] classPaths = classpath.split(PATH_SEPARATOR);
        for(String eachPath: classPaths){
            if(eachPath.contains("zip") || eachPath.contains("ZIP")
                    || eachPath.contains("JAR") || eachPath.contains("jar")){
                File dir = new File(eachPath);
                String absoluteDir = dir.getAbsolutePath();
                ZipFile zipfile = new ZipFile(IOUtil.transform(absoluteDir));
                Enumeration<? extends ZipEntry> entries = zipfile.entries();
                while(entries.hasMoreElements()){
                    ZipEntry ze = entries.nextElement();
                    String zeName = IOUtil.transform(ze.getName());
                    if(zeName.equals(className)){
                        return IOUtil.readFileByBytes(zipfile.getInputStream(ze));
                    }
                }
            }
            else{
                File dir = new File(eachPath);
                String absoluteDir = dir.getAbsolutePath();
                String absolutePath = IOUtil.transform(String.join(FILE_SEPARATOR, absoluteDir, className));
                File file = new File(absolutePath);
                if(file.exists() && file.isFile()){
                    return IOUtil.readFileByBytes(new FileInputStream(absolutePath));
                }
            }
        }
        return null;
    }
}
