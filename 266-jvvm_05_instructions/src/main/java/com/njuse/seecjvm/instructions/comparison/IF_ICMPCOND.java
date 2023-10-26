package com.njuse.seecjvm.instructions.comparison;

import com.njuse.seecjvm.instructions.base.BranchInstruction;
import com.njuse.seecjvm.runtime.OperandStack;
import com.njuse.seecjvm.runtime.StackFrame;

public abstract class IF_ICMPCOND extends BranchInstruction {
    /**
     * TODO：实现这条指令
     * 其中，condition 方法是对具体条件的计算，当条件满足时返回true，否则返回false
     */
    @Override
    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        int var2 = stack.popInt();
        int var1 = stack.popInt();
        if (condition(var1, var2)) {
            int branchPC = frame.getNextPC() - 3 + offset;
            frame.setNextPC(branchPC);
        }
    }

    protected abstract boolean condition(int v1, int v2);
}
