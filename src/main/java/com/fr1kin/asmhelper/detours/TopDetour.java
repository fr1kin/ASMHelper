package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.exceptions.DetourException;
import com.fr1kin.asmhelper.exceptions.IncompatibleMethodException;
import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created on 1/5/2017 by fr1kin
 *
 * Places a detour at the top of a method
 */
public class TopDetour extends Detour {
    protected boolean insertedBefore = true;

    public TopDetour(ASMMethod method, ASMMethod hookMethod) throws IllegalArgumentException {
        super(method, hookMethod);
    }

    /**
     * @return If the injected code will inject before the injection point
     */
    public boolean isInsertedBefore() {
        return insertedBefore;
    }

    /**
     * Tells code if the injected code will inject before the injection point
     * @param insertBefore to insert code before instructions
     */
    public void setInsertedBefore(boolean insertBefore) {
        this.insertedBefore = insertBefore;
    }

    /**
     * Gets the insert node
     * @param methodNode method node to search
     * @return node to inject code at
     */
    protected AbstractInsnNode getInsertNode(MethodNode methodNode) {
        return methodNode.instructions.getFirst();
    }

    @Override
    protected boolean validate() throws IncompatibleMethodException {
        if(!checkArguments(getHookMethod(), getMethod()))
            throw new IncompatibleMethodException(
                    getClass(),
                    "hook '%s' has missing arguments from '%s'",
                    getHookMethod().toString(), getMethod().toString()
            );
        if(!getHookMethod().getReturnType().equals(Type.VOID_TYPE))
            throw new IncompatibleMethodException(
                    getClass(),
                    "hook '%s' has bad return type '%s' (should be a void type)",
                    getHookMethod().toString(),
                    getHookMethod().getReturnTypeDescriptor()
            );
        if(getHookMethod().isStatic())
            throw new IncompatibleMethodException(
                    getClass(),
                    "non-static hook '%s' is not supported",
                    getHookMethod().toString()
            );
        return true;
    }

    @Override
    protected void inject(MethodNode methodNode) throws RuntimeException {
        InsnList insnList = new InsnList();
        // current index
        int index = 0;
        if(!getMethod().isStatic()) {
            // if not static then push the this instance
            insnList.insert(new VarInsnNode(ALOAD, index++));
        }
        // push all the hooked methods arguments
        getMethod().pushArguments(insnList, index);
        // invoke the hook
        getHookMethod().pushInvokeMethod(insnList, INVOKESTATIC);

        AbstractInsnNode injectNode = getInsertNode(methodNode);

        if(injectNode == null)
            throw new DetourException(getClass(), "failed to find injection node in method '%s'", getMethod().toString());

        // insert the code
        if(isInsertedBefore()) {
            // call the hook method before any instructions
            methodNode.instructions.insertBefore(injectNode, insnList);
        } else {
            // call the hook method after
            methodNode.instructions.insert(injectNode, insnList);
        }
    }
}
