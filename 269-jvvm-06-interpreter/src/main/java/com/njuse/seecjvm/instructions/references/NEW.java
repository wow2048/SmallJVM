package com.njuse.seecjvm.instructions.references;

import com.njuse.seecjvm.instructions.base.Index16Instruction;
import com.njuse.seecjvm.memory.JHeap;
import com.njuse.seecjvm.memory.jclass.InitState;
import com.njuse.seecjvm.memory.jclass.JClass;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.Constant;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.ref.ClassRef;
import com.njuse.seecjvm.runtime.OperandStack;
import com.njuse.seecjvm.runtime.StackFrame;
import com.njuse.seecjvm.runtime.struct.JObject;
import com.njuse.seecjvm.runtime.struct.NonArrayObject;

public class NEW extends Index16Instruction {
    /**
     * TODO 实现这条指令
     * 其中 对应的index已经读取好了
     */
    @Override
    public void execute(StackFrame frame) {
        JClass currentClazz = frame.getMethod().getClazz();
        Constant classRef = currentClazz.getRuntimeConstantPool().getConstant(index);
        try {
            assert classRef instanceof ClassRef;
            JClass targetClazz = ((ClassRef) classRef).getResolvedClass();

            if (targetClazz.getInitState() == InitState.PREPARED) {
                frame.setNextPC(frame.getNextPC() - 3);//opcode + operand = 3bytes
                targetClazz.initClass(frame.getThread(), targetClazz);
                return;
            }

            JObject objectRef = new NonArrayObject(targetClazz);
            JHeap.getInstance().addObj(objectRef);
            OperandStack operandStack = frame.getOperandStack();
            operandStack.pushObjectRef(objectRef);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
