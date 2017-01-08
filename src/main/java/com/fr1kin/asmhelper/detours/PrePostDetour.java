package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.exceptions.DetourException;
import com.fr1kin.asmhelper.exceptions.IncompatibleMethodException;
import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created on 1/7/2017 by fr1kin
 *
 * Detours
 */
public abstract class PrePostDetour extends Detour {
    protected boolean insertedBeforePre = true;
    protected boolean insertedBeforePost = false;

    public PrePostDetour(ASMMethod method, ASMMethod hookMethod, boolean insertedBeforePre, boolean insertedBeforePost)
            throws IllegalArgumentException {
        super(method, hookMethod);
        this.insertedBeforePre = insertedBeforePre;
        this.insertedBeforePost = insertedBeforePost;
    }

    /**
     * Gets the pre node
     * @param methodNode method node to search
     * @return node to inject code at
     */
    protected AbstractInsnNode getPreInsertNode(MethodNode methodNode) {
        return methodNode.instructions.getFirst();
    }

    /**
     * Gets the post node
     * @param methodNode method node to search
     * @return node to inject code at
     */
    protected abstract AbstractInsnNode getPostInsertNode(MethodNode methodNode);

    protected InsnList generateCallbackInsc(boolean preMethod) {
        InsnList insnList = new InsnList();

        // pre: push 0 for the first instruction list (PRE_METHOD)
        // post: push 1 for the first instruction list (POST_METHOD)
        insnList.insert(new InsnNode(preMethod ? ICONST_0 : ICONST_1));

        int i = 0;
        // push THIS if not static
        if(!getMethod().isStatic()) insnList.insert(new VarInsnNode(ALOAD, i++));
        // push all of the hooked methods arguments
        getMethod().pushArguments(insnList, i);
        // push invoke method that calls the hook
        getHookMethod().pushInvokeMethod(insnList, INVOKESTATIC);

        return insnList;
    }

    @Override
    protected boolean validate() throws IncompatibleMethodException {
        if(!checkArguments(getHookMethod(), getMethod()))
            throw new IncompatibleMethodException(
                    getClass(),
                    "hook '%s' has missing arguments from '%s'",
                    getHookMethod().toString(),
                    getMethod().toString()
            );
        if(!getHookMethod().getReturnType().equals(Type.BOOLEAN_TYPE))
            throw new IncompatibleMethodException(
                    getClass(),
                    "hook '%s' has bad return type '%s' (should be a boolean type)",
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
        // get instructions for calling hooks
        InsnList insnListPre = generateCallbackInsc(true);
        InsnList insnListPost = generateCallbackInsc(false);

        AbstractInsnNode preNode = getPreInsertNode(methodNode);
        AbstractInsnNode postNode = getPostInsertNode(methodNode);

        if (preNode == null)
            throw new DetourException(getClass(), "failed to find PRE injection node for method '%s'", getMethod().toString());
        if (postNode == null)
            throw new DetourException(getClass(), "failed to find POST injection node for method '%s'", getMethod().toString());

        insertCode(methodNode, preNode, insnListPre, insertedBeforePre);
        insertCode(methodNode, postNode, insnListPost, insertedBeforePost);
    }
}
