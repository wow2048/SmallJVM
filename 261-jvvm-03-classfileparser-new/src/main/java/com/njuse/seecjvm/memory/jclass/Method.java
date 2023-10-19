package com.njuse.seecjvm.memory.jclass;

import com.njuse.seecjvm.classloader.classfileparser.MethodInfo;
import com.njuse.seecjvm.classloader.classfileparser.attribute.CodeAttribute;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Method extends ClassMember {
    private int maxStack;
    private int maxLocal;
    private int argc;
    private byte[] code;

    public Method(MethodInfo info, JClass clazz) {
        this.clazz = clazz;
        accessFlags = info.getAccessFlags();
        name = info.getName();
        descriptor = info.getDescriptor();

        CodeAttribute codeAttribute = info.getCodeAttribute();
        if (codeAttribute != null) {
            maxLocal = codeAttribute.getMaxLocal();
            maxStack = codeAttribute.getMaxStack();
            code = codeAttribute.getCode();
        }
        argc = calculateArgcFromDescriptor(descriptor);
    }

    //todo calculateArgcFromDescriptor
    private int calculateArgcFromDescriptor(String descriptor) {
        /**
         * Add some codes here.
         * Here are some examples in README!!!
         *
         * You should refer to JVM specification for more details
         *
         * Beware of long and double type
         */
        int res = 0;
        String args = descriptor.substring(descriptor.indexOf("(") + 1, descriptor.indexOf(")"));
        int index = 0;
        while (index < args.length()) {
            char ch = args.charAt(index);
            switch (ch) {
                case '[':
                    index++;
                    while (args.charAt(index) == '[') {
                        index++;
                    }
                    if (isBasicType(args.charAt(index))) {
                        res++;
                        index++;
                    } else if (args.charAt(index) == 'L') {
                        res++;
                        index++;
                        while (args.charAt(index) != ';') {
                            index++;
                        }
                        index++;
                    }
                    break;
                case 'B':
                case 'C':
                case 'F':
                case 'I':
                case 'S':
                case 'Z':
                    res += 1;
                    index++;
                    break;
                case 'J':
                case 'D':
                    res += 2;
                    index++;
                    break;
                case 'L':
                    res += 1;
                    index++;
                    while (args.charAt(index) != ';') {
                        index++;
                    }
                    index++;
                    break;
            }
        }

        return res;
    }

    private boolean isBasicType(char ch) {
        return ch == 'B' || ch == 'C' || ch == 'F' || ch == 'I' || ch == 'S' || ch == 'Z' || ch == 'J' || ch == 'D';
    }
}
