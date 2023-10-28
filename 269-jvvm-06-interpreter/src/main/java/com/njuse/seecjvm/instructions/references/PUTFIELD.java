package com.njuse.seecjvm.instructions.references;

import com.njuse.seecjvm.instructions.base.Index16Instruction;
import com.njuse.seecjvm.memory.jclass.Field;
import com.njuse.seecjvm.memory.jclass.InitState;
import com.njuse.seecjvm.memory.jclass.JClass;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.RuntimeConstantPool;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.ref.FieldRef;
import com.njuse.seecjvm.runtime.OperandStack;
import com.njuse.seecjvm.runtime.StackFrame;
import com.njuse.seecjvm.runtime.Vars;
import com.njuse.seecjvm.runtime.struct.JObject;
import com.njuse.seecjvm.runtime.struct.NonArrayObject;


public class PUTFIELD extends Index16Instruction {
    /**
     * TODO 实现这条指令
     * 其中 对应的index已经读取好了
     */
    @Override
    public void execute(StackFrame frame) {
        RuntimeConstantPool runtimeConstantPool = frame.getMethod().getClazz().getRuntimeConstantPool();
        FieldRef fieldRef = (FieldRef) runtimeConstantPool.getConstant(index);
        Field field;
        try {
            field = fieldRef.getResolvedFieldRef();
            JClass targetClazz = field.getClazz();

            //check class whether init
            if (targetClazz.getInitState() == InitState.PREPARED) {
                frame.setNextPC(frame.getNextPC() - 3);//opcode + operand = 3bytes
                targetClazz.initClass(frame.getThread(), targetClazz);
                return;
            }

            if (field.isStatic()) {
                throw new IncompatibleClassChangeError();
            }
            String descriptor = field.getDescriptor();
            int slotID = field.getSlotID();
            OperandStack stack = frame.getOperandStack();
            switch (descriptor.charAt(0)) {
                case 'Z':
                case 'B':
                case 'C':
                case 'S':
                case 'I':
                    setInt(slotID, stack);
                    break;
                case 'F':
                    setFloat(slotID, stack);
                    break;
                case 'J':
                    setLong(slotID, stack);
                    break;
                case 'D':
                    setDouble(slotID, stack);
                    break;
                case 'L':
                case '[':
                    setRef(slotID, stack);
                    break;
                default:
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setInt(int slotID, OperandStack stack) {
        int value = stack.popInt();
        JObject objectRef = stack.popObjectRef();
        assert objectRef instanceof NonArrayObject;
        Vars fields = ((NonArrayObject) objectRef).getFields();
        fields.setInt(slotID, value);
    }

    private void setFloat(int slotID, OperandStack stack) {
        float value = stack.popFloat();
        JObject objectRef = stack.popObjectRef();
        assert objectRef instanceof NonArrayObject;
        Vars fields = ((NonArrayObject) objectRef).getFields();
        fields.setFloat(slotID, value);
    }

    private void setLong(int slotID, OperandStack stack) {
        long value = stack.popLong();
        JObject objectRef = stack.popObjectRef();
        assert objectRef instanceof NonArrayObject;
        Vars fields = ((NonArrayObject) objectRef).getFields();
        fields.setLong(slotID, value);
    }

    private void setDouble(int slotID, OperandStack stack) {
        double value = stack.popDouble();
        JObject objectRef = stack.popObjectRef();
        assert objectRef instanceof NonArrayObject;
        Vars fields = ((NonArrayObject) objectRef).getFields();
        fields.setDouble(slotID, value);
    }

    private void setRef(int slotID, OperandStack stack) {
        JObject jObject = stack.popObjectRef();
        JObject objectRef = stack.popObjectRef();
        assert objectRef instanceof NonArrayObject;
        Vars fields = ((NonArrayObject) objectRef).getFields();
        fields.setObjectRef(slotID, jObject);
    }

}
