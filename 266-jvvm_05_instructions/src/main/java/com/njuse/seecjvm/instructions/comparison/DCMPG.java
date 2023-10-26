package com.njuse.seecjvm.instructions.comparison;

import com.njuse.seecjvm.instructions.base.NoOperandsInstruction;
import com.njuse.seecjvm.runtime.OperandStack;
import com.njuse.seecjvm.runtime.StackFrame;

public class DCMPG extends NoOperandsInstruction {

    /**
     * TODO：实现这条指令
     */
    @Override
    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        double var2 = stack.popDouble();
        double var1 = stack.popDouble();
        if (Double.isNaN(var1) || Double.isNaN(var2)) {
            stack.pushInt(1);
        }
        int res = Double.compare(var1, var2);
        stack.pushInt(res);
    }
}
