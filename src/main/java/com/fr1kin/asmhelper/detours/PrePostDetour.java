package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.detours.locator.ILocator;
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
public class PrePostDetour extends Detour {
    private ILocator preLocator = null;
    private ILocator postLocator = null;

    public PrePostDetour(ASMMethod method, ASMMethod hookMethod) throws IllegalArgumentException {
        super(method, hookMethod);
    }

    public ILocator getPreLocator() {
        return preLocator;
    }

    public ILocator getPostLocator() {
        return postLocator;
    }

    public PrePostDetour setLocators(ILocator preLocator, ILocator postLocator) {
        this.preLocator = preLocator;
        this.postLocator = postLocator;
        return this;
    }

    protected InsnList generateInsnList(int opcode) {
        return InsnBuilder.newInstance()
                .push(new InsnNode(opcode))
                .pushArguments(getTargetMethod())
                .pushInvoke(INVOKESTATIC, getHookMethod())
                .getInstructions();
    }

    protected void insert(MethodNode methodNode, InsnList insnListPre, InsnList insnListPost) {
        AbstractInsnNode preNode = getPreLocator().apply(methodNode, getTargetMethod(), getHookMethod());
        AbstractInsnNode postNode = getPostLocator().apply(methodNode, getTargetMethod(), getHookMethod());

        Verifier.checkIfNullNode("preNode", preNode);
        Verifier.checkIfNullNode("postNode", postNode);

        insertCode(methodNode, preNode, insnListPre, getPreLocator().isInsertedBefore());
        insertCode(methodNode, postNode, insnListPost, getPostLocator().isInsertedBefore());
    }

    @Override
    protected boolean validate() throws IncompatibleMethodException {
        Verifier.checkHookContainsAllArguments(getHookMethod(), getTargetMethod());
        Verifier.checkHookReturnType(getHookMethod(), Type.VOID_TYPE);
        Verifier.checkHookIsNonStatic(getHookMethod());
        return true;
    }

    @Override
    protected void inject(MethodNode methodNode) throws RuntimeException {
        insert(methodNode, generateInsnList(ICONST_0), generateInsnList(ICONST_1));
    }
}
