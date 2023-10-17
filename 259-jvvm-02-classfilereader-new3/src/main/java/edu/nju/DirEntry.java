package edu.nju;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * format : dir/subdir/.../
 */
public class DirEntry extends Entry{
    public DirEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClassFile(String className) throws IOException {
        File dir = new File(classpath);
        String absoluteDir = dir.getAbsolutePath();
        String absolutePath = IOUtil.transform(String.join(FILE_SEPARATOR, absoluteDir, className));
        File file = new File(absolutePath);
        if(file.exists() && file.isFile()){
            return IOUtil.readFileByBytes(new FileInputStream(absolutePath));
        }else{
            return null;
        }
    }
}
