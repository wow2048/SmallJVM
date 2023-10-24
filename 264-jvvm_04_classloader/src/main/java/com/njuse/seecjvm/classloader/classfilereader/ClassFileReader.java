package com.njuse.seecjvm.classloader.classfilereader;

import com.njuse.seecjvm.classloader.classfilereader.classpath.ArchivedEntry;
import com.njuse.seecjvm.classloader.classfilereader.classpath.CompositeEntry;
import com.njuse.seecjvm.classloader.classfilereader.classpath.DirEntry;
import com.njuse.seecjvm.classloader.classfilereader.classpath.Entry;
import com.njuse.seecjvm.classloader.classfilereader.classpath.EntryType;
import com.njuse.seecjvm.classloader.classfilereader.classpath.WildEntry;
import com.njuse.seecjvm.util.PathUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;

/**
 * This class is the simulated implementation of Java Classloader.
 */
public class ClassFileReader {
    private static ClassFileReader reader = new ClassFileReader();
    private static final String FILE_SEPARATOR = File.separator;
    private static final String PATH_SEPARATOR = File.pathSeparator;

    private ClassFileReader() {
    }

    public static ClassFileReader getInstance() {
        return reader;
    }

    private static Entry bootClasspath = null;//bootstrap class entry
    private static Entry extClasspath = null;//extension class entry
    private static Entry userClasspath = null;//user class entry

    public static void setBootClasspath(String classpath) {
        bootClasspath = chooseEntryType(classpath);
    }

    public static void setExtClasspath(String classpath) {
        extClasspath = chooseEntryType(classpath);
    }

    public static void setUserClasspath(String classpath) {
        userClasspath = chooseEntryType(classpath);
    }

    /**
     * select Entry by type of classpath
     */
    public static Entry chooseEntryType(String classpath) {
        if (classpath.contains(PATH_SEPARATOR)) {
            return new CompositeEntry(classpath);
        }
        if (classpath.endsWith("*")) {
            return new WildEntry(classpath);
        }
        if (classpath.endsWith(".jar") || classpath.endsWith(".JAR")
                || classpath.endsWith(".zip") || classpath.endsWith(".ZIP")) {
            return new ArchivedEntry(classpath);
        }
        return new DirEntry(classpath);
    }

    /**
     * @param className class to be read
     * @param privilege privilege of relevant class
     * @return content of class file and the privilege of loaded class
     */
    public Pair<byte[], Integer> readClassFile(String className, EntryType privilege) throws IOException, ClassNotFoundException {
        String realClassName = className + ".class";
        realClassName = PathUtil.transform(realClassName);
        //todo
        /**
         * Add some codes here.
         *
         * You can pass realClassName to readClass()
         *
         * Read class file in privilege order
         * USER_ENTRY has highest privileges and Boot_Entry has lowest privileges.
         * If there is no relevant class loaded before, use default privilege.
         * Default privilege is USER_ENTRY
         *
         * Return the result once you read it.
         */
        int value = (privilege == null) ? EntryType.USER_ENTRY : privilege.getValue();
        byte[] data;
        if (value >= EntryType.BOOT_ENTRY) {
            if ((data = bootClasspath.readClass(realClassName)) != null) {
                return Pair.of(data, EntryType.BOOT_ENTRY);
            }
        }
        if (value >= EntryType.EXT_ENTRY) {
            if ((data = extClasspath.readClass(realClassName)) != null) {
                return Pair.of(data, EntryType.EXT_ENTRY);
            }
        }
        if (value >= EntryType.USER_ENTRY) {
            if ((data = userClasspath.readClass(realClassName)) != null) {
                return Pair.of(data, EntryType.USER_ENTRY);
            }
        }

        throw new ClassNotFoundException();
    }
}
