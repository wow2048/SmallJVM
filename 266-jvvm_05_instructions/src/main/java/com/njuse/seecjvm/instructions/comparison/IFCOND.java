package com.njuse.seecjvm.instructions.comparison;

import com.njuse.seecjvm.instructions.base.BranchInstruction;
import com.njuse.seecjvm.runtime.OperandStack;
import com.njuse.seecjvm.runtime.StackFrame;

public abstract class IFCOND extends BranchInstruction {

    /**
     * TODO：实现这条指令
     * 其中，condition 方法是对具体条件的计算，当条件满足时返回true，否则返回false
     */
    @Override
    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        int value = stack.popInt();
        if (condition(value)) {
            int branchPC = frame.getNextPC() - 3 + offset;
            frame.setNextPC(branchPC);
        }
    }

    protected abstract boolean condition(int value);

}
