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
 * Created on 1/5/2017 by fr1kin
 */
public class SimpleDetour extends Detour {
    private final ILocator locator;

    protected SimpleDetour(ASMMethod method, ASMMethod hookMethod, ILocator locator) throws IllegalArgumentException {
        super(method, hookMethod);
        this.locator = locator;
    }

    /**
     * @return Gets the node locator
     */
    protected ILocator getLocator() {
        return locator;
    }

    @Override
    protected boolean validate() throws IncompatibleMethodException {
        Verifier.checkHookContainsAllArguments(getHookMethod(), getTargetMethod());
        Verifier.checkHookReturnType(getHookMethod(), Type.VOID_TYPE);
        return true;
    }

    @Override
    protected void inject(MethodNode methodNode) throws RuntimeException {
        ASMHelper.insertIntoMethod(
                methodNode,
                getLocator().apply(methodNode, getTargetMethod(), getHookMethod()),
                InsnBuilder.newInstance()
                        .addArguments(getTargetMethod())
                        .addInvoke(INVOKESTATIC, getHookMethod())
                        .getInstructions(),
                getLocator().isInsertedBefore()
        );
    }
}
