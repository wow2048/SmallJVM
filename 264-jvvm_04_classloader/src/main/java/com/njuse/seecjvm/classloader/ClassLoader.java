package com.njuse.seecjvm.classloader;

import com.njuse.seecjvm.classloader.classfileparser.ClassFile;
import com.njuse.seecjvm.classloader.classfilereader.ClassFileReader;
import com.njuse.seecjvm.classloader.classfilereader.classpath.EntryType;
import com.njuse.seecjvm.memory.MethodArea;
import com.njuse.seecjvm.memory.jclass.Field;
import com.njuse.seecjvm.memory.jclass.InitState;
import com.njuse.seecjvm.memory.jclass.JClass;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.RuntimeConstantPool;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.wrapper.DoubleWrapper;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.wrapper.FloatWrapper;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.wrapper.IntWrapper;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.wrapper.LongWrapper;
import com.njuse.seecjvm.runtime.Vars;
import com.njuse.seecjvm.runtime.struct.NullObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;

public class ClassLoader {
    private static ClassLoader classLoader = new ClassLoader();
    private ClassFileReader classFileReader;
    private MethodArea methodArea;

    private ClassLoader() {
        classFileReader = ClassFileReader.getInstance();
        methodArea = MethodArea.getInstance();
    }

    public static ClassLoader getInstance() {
        return classLoader;
    }

    /**
     * load phase
     *
     * @param className       name of class
     * @param initiatingEntry null value represents load MainClass
     */
    public JClass loadClass(String className, EntryType initiatingEntry) throws ClassNotFoundException {
        JClass ret;
        if ((ret = methodArea.findClass(className)) == null) {
            return loadNonArrayClass(className, initiatingEntry);
        }
        return ret;
    }

    private JClass loadNonArrayClass(String className, EntryType initiatingEntry) throws ClassNotFoundException {
        try {
            Pair<byte[], Integer> res = classFileReader.readClassFile(className, initiatingEntry);
            byte[] data = res.getKey();
            EntryType definingEntry = new EntryType(res.getValue());
            //define class
            JClass clazz = defineClass(data, definingEntry);
            //link class
            linkClass(clazz);
            return clazz;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ClassNotFoundException();
        }
    }

    /**
     *
     * define class
     * @param data binary of class file
     * @param definingEntry defining loader of class
     */
    private JClass defineClass(byte[] data, EntryType definingEntry) throws ClassNotFoundException {
        ClassFile classFile = new ClassFile(data);
        JClass clazz = new JClass(classFile);
        //todo
        /**
         * Add some codes here.
         *
         * update load entry of the class
         * load superclass recursively
         * load interfaces of this class
         * add to method area
         */
        clazz.setLoadEntryType(definingEntry);
        resolveSuperClass(clazz);
        resolveInterfaces(clazz);
        methodArea.addClass(clazz.getName(), clazz);
        return clazz;
    }

    /**
     * load superclass before add to method area
     */
    private void resolveSuperClass(JClass clazz) throws ClassNotFoundException {
        //todo
        /**
         * Add some codes here.
         *
         * Use the load entry(defining entry) as initiating entry of super class
         */
        String superClassName = clazz.getSuperClassName();
        if (!StringUtils.isEmpty(superClassName)) {
            loadClass(superClassName, clazz.getLoadEntryType());
        }
    }

    /**
     * load interfaces before add to method area
     */
    private void resolveInterfaces(JClass clazz) throws ClassNotFoundException {
        //todo
        /**
         * Add some codes here.
         *
         * Use the load entry(defining entry) as initiating entry of interfaces
         */
        String[] interfaceNames = clazz.getInterfaceNames();
        for (String interfaceName : interfaceNames) {
            if (!StringUtils.isEmpty(interfaceName)) {
                loadClass(interfaceName, clazz.getLoadEntryType());
            }
        }
    }

    /**
     * link phase
     */
    private void linkClass(JClass clazz) {
        verify(clazz);
        prepare(clazz);
    }

    /**
     * You don't need to write any code here.
     */
    private void verify(JClass clazz) {
        //do nothing
    }

    private void prepare(JClass clazz) {
        //todo
        /**
         * Add some codes here.
         *
         * step1 (We do it for you here)
         *      count the fields slot id in instance
         *      count the fields slot id in class
         * step2
         *      alloc memory for fields(We do it for you here) and init static vars
         * step3
         *      set the init state
         */
        calInstanceFieldSlotIDs(clazz);
        calStaticFieldSlotIDs(clazz);
        allocAndInitStaticVars(clazz);
        clazz.setInitState(InitState.PREPARED);
    }

    /**
     * count the number of field slots in instance
     * long and double takes two slots
     * the field is not static
     */
    private void calInstanceFieldSlotIDs(JClass clazz) {
        int slotID = 0;
        if (clazz.getSuperClass() != null) {
            slotID = clazz.getSuperClass().getInstanceSlotCount();
        }
        Field[] fields = clazz.getFields();
        for (Field f : fields) {
            if (!f.isStatic()) {
                f.setSlotID(slotID);
                slotID++;
                if (f.isLongOrDouble()) slotID++;
            }
        }
        clazz.setInstanceSlotCount(slotID);
    }

    /**
     * count the number of field slots in class
     * long and double takes two slots
     * the field is static
     */
    private void calStaticFieldSlotIDs(JClass clazz) {
        int slotID = 0;
        Field[] fields = clazz.getFields();
        for (Field f : fields) {
            if (f.isStatic()) {
                f.setSlotID(slotID);
                slotID++;
                if (f.isLongOrDouble()) slotID++;
            }
        }
        clazz.setStaticSlotCount(slotID);

    }

    /**
     * primitive type is set to 0
     * ref type is set to null
     */
    private void initDefaultValue(JClass clazz, Field field) {
        //todo
        /**
         * Add some codes here.
         * step 1
         *      get static vars of class
         * step 2
         *      get the slotID index of field
         * step 3
         *      switch by descriptor or some part of descriptor
         *      Handle basic type ZBCSIJDF and references (with new NullObject())
         */
        Vars staticVars = clazz.getStaticVars();
        int slotID = field.getSlotID();
        String descriptor = field.getDescriptor();
        switch (descriptor) {
            case "Z":
            case "B":
            case "C":
            case "I":
            case "S":
                staticVars.setInt(slotID, 0);
                break;
            case "J":
                staticVars.setLong(slotID, 0L);
                break;
            case "D":
                staticVars.setDouble(slotID, 0.0d);
                break;
            case "F":
                staticVars.setFloat(slotID, 0.0f);
                break;
            default:
                staticVars.setObjectRef(slotID, new NullObject());
                break;
        }
    }

    /**
     * load const value from runtimeConstantPool for primitive type
     * String is not support now
     */
    private void loadValueFromRTCP(JClass clazz, Field field) {
        //todo
        /**
         * Add some codes here.
         *
         * step 1
         *      get static vars and runtime constant pool of class
         * step 2
         *      get the slotID and constantValue index of field
         * step 3
         *      switch by descriptor or some part of descriptor
         *      just handle basic type ZBCSIJDF, you don't have to throw any exception
         *      use wrapper to get value
         *
         *  Example
         *      long longVal = ((LongWrapper) runtimeConstantPool.getConstant(constantPoolIndex)).getValue();
         */
        Vars staticVars = clazz.getStaticVars();
        RuntimeConstantPool runtimeConstantPool = clazz.getRuntimeConstantPool();
        int slotID = field.getSlotID();
        int constValueIndex = field.getConstValueIndex();
        String descriptor = field.getDescriptor();
        switch (descriptor) {
            case "Z":
            case "B":
            case "C":
            case "I":
            case "S":
                int intValue = ((IntWrapper) runtimeConstantPool.getConstant(constValueIndex)).getValue();
                staticVars.setInt(slotID, intValue);
                break;
            case "J":
                long longValue = ((LongWrapper) runtimeConstantPool.getConstant(constValueIndex)).getValue();
                staticVars.setLong(slotID, longValue);
                break;
            case "D":
                double doubleValue = ((DoubleWrapper) runtimeConstantPool.getConstant(constValueIndex)).getValue();
                staticVars.setDouble(slotID, doubleValue);
                break;
            case "F":
                float floatValue = ((FloatWrapper) runtimeConstantPool.getConstant(constValueIndex)).getValue();
                staticVars.setFloat(slotID, floatValue);
                break;
        }
    }

    /**
     * the value of static final field is in com.njuse.seecjvm.runtime constant pool
     * others will be set to default value
     */
    private void allocAndInitStaticVars(JClass clazz) {
        clazz.setStaticVars(new Vars(clazz.getStaticSlotCount()));
        Field[] fields = clazz.getFields();
        for (Field f : fields) {
            //todo
            /**
             * Add some codes here.
             *
             * Refer to manual for details.
             */
            if (f.isFinal() && f.isStatic()) {
                loadValueFromRTCP(clazz, f);
            } else if (f.isStatic()) {
                initDefaultValue(clazz, f);
            }
        }
    }
}
