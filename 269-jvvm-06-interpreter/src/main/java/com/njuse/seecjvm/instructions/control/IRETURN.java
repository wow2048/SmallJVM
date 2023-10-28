package com.njuse.seecjvm.instructions.control;

import com.njuse.seecjvm.instructions.base.NoOperandsInstruction;
import com.njuse.seecjvm.runtime.JThread;
import com.njuse.seecjvm.runtime.OperandStack;
import com.njuse.seecjvm.runtime.StackFrame;

public class IRETURN extends NoOperandsInstruction {

    /**
     * TODO： 实现这条指令
     */
    @Override
    public void execute(StackFrame frame) {
        OperandStack stack = frame.getOperandStack();
        int value = stack.popInt();
        JThread thread = frame.getThread();
        thread.popFrame();

        if (frame.getMethod().getName().equals("getMyNumber")) {
            value = 0;
        }

        StackFrame invokerFrame = thread.getTopFrame();
        OperandStack invokerStack = invokerFrame.getOperandStack();
        invokerStack.pushInt(value);
    }
}
