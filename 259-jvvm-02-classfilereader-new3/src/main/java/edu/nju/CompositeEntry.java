package edu.nju;

import java.io.IOException;

/**
 * format : dir/subdir;dir/subdir/*;dir/target.jar*
 */
public class CompositeEntry extends Entry {
    public CompositeEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClassFile(String className) throws IOException {
        String[] classPaths = classpath.split(PATH_SEPARATOR);
        for (String eachPath : classPaths) {
            byte[] content;
            if (eachPath.contains("*")) {
                content = new WildEntry(eachPath).readClassFile(className);
            } else if (eachPath.contains("zip") || eachPath.contains("ZIP")
                    || eachPath.contains("JAR") || eachPath.contains("jar")) {
                content = new ArchivedEntry(eachPath).readClassFile(className);
            } else {
                content = new DirEntry(eachPath).readClassFile(className);
            }

            if (content != null) {
                return content;
            }
        }
        return null;
    }
}
