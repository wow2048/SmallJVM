package com.njuse.seecjvm.classloader.classfileparser.constantpool.info;

import com.njuse.seecjvm.classloader.classfileparser.BuildUtil;
import com.njuse.seecjvm.classloader.classfileparser.constantpool.ConstantPool;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.ByteBuffer;


@Getter
public class UTF8Info extends ConstantPoolInfo {
    /**
     * Add some codes here.
     * <p>
     * tips:
     * step1
     * UTF8Info need some fields, what are they?
     * step2
     * You need to add some args in constructor
     * and don't forget to set tag
     * <p>
     * super method and super key word will help you
     * <p>
     * step3
     * The length of String is unknown for getConstantPoolInfo method
     * How to return the instance with its length?
     * <p>
     * return a Pair<UTF8Info,Integer> or get the length of string in UTF8Info?
     */
    //todo attributes of UTF8Info
    private int length;
    private byte[] bytes;
    private String string;

    //todo constructor of UTF8Info
    public UTF8Info(ConstantPool constantPool, int length, byte[] bytes) {
        super(constantPool);
        this.length = length;
        this.bytes = bytes;
        this.string = new String(bytes);
        super.tag = ConstantPoolInfo.UTF8;
    }

    /**
     * Add some codes here.
     * return the string of UTF8Info
     */
    //todo getInstance
    static Pair<UTF8Info, Integer> getInstance(ConstantPool constantPool, byte[] in, int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(in, offset, in.length - offset);
        BuildUtil bu = new BuildUtil(buffer);
        bu.getU1();

        int bytesRead = 0;
        int len = bu.getU2();
        UTF8Info ret = new UTF8Info(constantPool, len, readNBytes(len, bu));
        bytesRead += 2 + len;
        return Pair.of(ret, bytesRead);
    }

    //todo return string
    public String getString() {
        return string;
    }

    //todo return string
    public String getMyString() {
        return string;
    }

    private static byte[] readNBytes(int len, BuildUtil in) {
        byte[] res = new byte[len];
        for (int i = 0; i < len; i++) {
            res[i] = in.getByteBuffer().get();
        }
        return res;
    }
}
