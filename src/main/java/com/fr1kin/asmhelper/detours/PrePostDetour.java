package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.ASMHelper;
import com.fr1kin.asmhelper.utils.locator.ILocator;
import com.fr1kin.asmhelper.exceptions.IncompatibleMethodException;
import com.fr1kin.asmhelper.types.ASMMethod;
import com.fr1kin.asmhelper.utils.InsnBuilder;
import com.fr1kin.asmhelper.utils.Verifier;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created on 1/7/2017 by fr1kin
 */
public class PrePostDetour extends Detour {
    private final ILocator preLocator;
    private final ILocator postLocator;

    public PrePostDetour(ASMMethod method, ASMMethod hookMethod, ILocator preLocator, ILocator postLocator) throws IllegalArgumentException {
        super(method, hookMethod);
        this.preLocator = preLocator;
        this.postLocator = postLocator;
    }

    protected ILocator getPreLocator() {
        return preLocator;
    }

    protected ILocator getPostLocator() {
        return postLocator;
    }

    protected InsnList generatePushHookCall(int opcode) {
        return InsnBuilder.newInstance()
                .push(new InsnNode(opcode))
                .pushArguments(getTargetMethod())
                .pushInvoke(INVOKESTATIC, getHookMethod())
                .getInstructions();
    }

    protected void insert(MethodNode methodNode, InsnList insnListPre, InsnList insnListPost) {
        ASMHelper.insertIntoMethod(
                methodNode,
                getPreLocator().apply(methodNode, getTargetMethod(), getHookMethod()),
                insnListPre,
                getPreLocator().isInsertedBefore()
        );
        ASMHelper.insertIntoMethod(
                methodNode,
                getPostLocator().apply(methodNode, getTargetMethod(), getHookMethod()),
                insnListPost,
                getPostLocator().isInsertedBefore()
        );
    }

    @Override
    protected boolean validate() throws IncompatibleMethodException {
        Verifier.checkIfArgumentPresent(getHookMethod(), 0, Type.INT_TYPE);
        Verifier.checkHookContainsAllArguments(getHookMethod(), getTargetMethod());
        Verifier.checkHookReturnType(getHookMethod(), Type.VOID_TYPE);
        return true;
    }

    @Override
    protected void inject(MethodNode methodNode) throws RuntimeException {
        insert(methodNode, generatePushHookCall(ICONST_0), generatePushHookCall(ICONST_1));
    }
}
