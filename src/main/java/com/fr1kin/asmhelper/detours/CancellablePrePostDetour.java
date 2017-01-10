package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.exceptions.IncompatibleMethodException;
import com.fr1kin.asmhelper.types.ASMMethod;
import com.fr1kin.asmhelper.utils.InsnBuilder;
import com.fr1kin.asmhelper.utils.Verifier;
import com.fr1kin.asmhelper.utils.locator.ILocator;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created on 1/7/2017 by fr1kin
 */
public class CancellablePrePostDetour extends PrePostDetour {
    protected CancellablePrePostDetour(ASMMethod method, ASMMethod hookMethod, ILocator preLocator, ILocator postLocator)
            throws IllegalArgumentException {
        super(method, hookMethod, preLocator, postLocator);
    }

    @Override
    protected boolean validate() throws IncompatibleMethodException {
        Verifier.checkIfArgumentPresent(getHookMethod(), 0, Type.INT_TYPE);
        Verifier.checkHookContainsAllArguments(getHookMethod(), getTargetMethod());
        Verifier.checkHookReturnType(getHookMethod(), Type.BOOLEAN_TYPE);
        return true;
    }

    @Override
    protected void inject(MethodNode methodNode) throws RuntimeException {
        // node to jump to if the hook is cancelled
        LabelNode jumpTo = new LabelNode();

        insert(
                methodNode,
                InsnBuilder.newInstance(generatePushHookCall(ICONST_0))
                        .push(new JumpInsnNode(IFEQ, jumpTo))
                        .getInstructions(),
                InsnBuilder.newInstance(generatePushHookCall(ICONST_1))
                        .pop()
                        .push(jumpTo)
                        .getInstructions()
        );
    }
}
