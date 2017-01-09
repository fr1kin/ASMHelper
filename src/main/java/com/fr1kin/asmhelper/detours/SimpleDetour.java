package com.fr1kin.asmhelper.detours;

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
    private ILocator locator = null;

    public SimpleDetour(ASMMethod method, ASMMethod hookMethod) throws IllegalArgumentException {
        super(method, hookMethod);
    }

    /**
     * @return Gets the node locator
     */
    public ILocator getLocator() {
        return locator;
    }

    /**
     * Sets the node locator
     * @param locateNode node located
     */
    public SimpleDetour setLocator(ILocator locateNode) {
        this.locator = locateNode;
        return this;
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
        AbstractInsnNode injectNode = getLocator().apply(methodNode, getTargetMethod(), getHookMethod());

        Verifier.checkIfNullNode(injectNode);

        insertCode(
                methodNode,
                injectNode,
                InsnBuilder.newInstance()
                        .pushArguments(getTargetMethod())
                        .pushInvoke(INVOKESTATIC, getHookMethod())
                        .getInstructions(),
                getLocator().isInsertedBefore()
        );
    }
}
