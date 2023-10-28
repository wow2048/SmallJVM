package com.njuse.seecjvm.instructions.invoke;

import com.njuse.seecjvm.instructions.base.Index16Instruction;
import com.njuse.seecjvm.memory.jclass.JClass;
import com.njuse.seecjvm.memory.jclass.Method;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.Constant;
import com.njuse.seecjvm.memory.jclass.runtimeConstantPool.constant.ref.MethodRef;
import com.njuse.seecjvm.runtime.StackFrame;
import com.njuse.seecjvm.runtime.Vars;
import com.njuse.seecjvm.runtime.struct.Slot;

public class INVOKE_STATIC extends Index16Instruction {

    /**
     * TODO 实现这条指令，注意其中的非标准部分：
     * 1. TestUtil.equalInt(int a, int b): 如果a和b相等，则跳过这个方法，
     * 否则抛出`RuntimeException`, 其中，这个异常的message为
     * ：${第一个参数的值}!=${第二个参数的值}
     * 例如，TestUtil.equalInt(1, 2)应该抛出
     * RuntimeException("1!=2")
     * <p>
     * 2. TestUtil.fail(): 抛出`RuntimeException`
     * <p>
     * 3. TestUtil.equalFloat(float a, float b): 如果a和b相等，则跳过这个方法，
     * 否则抛出`RuntimeException`. 对于异常的message不作要求
     */
    @Override
    public void execute(StackFrame frame) throws RuntimeException {
        JClass currentClazz = frame.getMethod().getClazz();
        Constant methodRef = currentClazz.getRuntimeConstantPool().getConstant(index);
        assert methodRef instanceof MethodRef;
        Method method = ((MethodRef) methodRef).resolveMethodRef();
        Slot[] args = copyArguments(frame, method);

        check(method, args);

        assert method.isStatic();
        StackFrame newFrame = new StackFrame(frame.getThread(), method, method.getMaxStack(), method.getMaxLocal() + 1);
        Vars localVars = newFrame.getLocalVars();
        int argc = method.getArgc();
        for (int i = 1; i < args.length + 1; i++) {
            localVars.setSlot(i, args[argc - i]);
        }

        frame.getThread().pushFrame(newFrame);

        if (method.isNative()) {
            if (method.getName().equals("registerNatives")) {
                frame.getThread().popFrame();
            } else {
                System.out.println("Native method:"
                        + method.getClazz().getName()
                        + method.name
                        + method.descriptor);
                frame.getThread().popFrame();
            }
        }
    }

    private Slot[] copyArguments(StackFrame frame, Method method) {
        int argc = method.getArgc();
        Slot[] argv = new Slot[argc];
        for (int i = 0; i < argc; i++) {
            argv[i] = frame.getOperandStack().popSlot();
        }
        return argv;
    }

    private void check(Method method, Slot[] args) {
        String className = method.getClazz().getName();
        String methodName = method.getName();

        if (!className.equals("TestUtil")) {
            return;
        }

        switch (methodName) {
            case "equalInt":
                int ivar2 = args[0].getValue();
                int ivar1 = args[1].getValue();
                if (ivar1 != ivar2) {
                    throw new RuntimeException(ivar1 + "!=" + ivar2);
                }
                break;
            case "fail":
                throw new RuntimeException();
            case "equalFloat":
                float fvar2 = Float.intBitsToFloat(args[0].getValue());
                float fvar1 = Float.intBitsToFloat(args[1].getValue());
                if (fvar1 != fvar2) {
                    throw new RuntimeException("shabi");
                }
                break;
        }
    }
}
