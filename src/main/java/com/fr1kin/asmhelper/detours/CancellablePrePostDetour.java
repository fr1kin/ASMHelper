package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.exceptions.IncompatibleMethodException;
import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created on 1/7/2017 by fr1kin
 *
 * Inserts a post and pre detour that can be cancelled
 * NOTE: One has to specify the post node manually
 */
public abstract class CancellablePrePostDetour extends PrePostDetour {
    public CancellablePrePostDetour(ASMMethod method, ASMMethod hookMethod)
            throws IllegalArgumentException {
        super(method, hookMethod);
    }

    @Override
    protected boolean validate() throws IncompatibleMethodException {
        Verifier.checkHookContainsAllArguments(getHookMethod(), getTargetMethod());
        Verifier.checkHookReturnType(getHookMethod(), Type.BOOLEAN_TYPE);
        Verifier.checkHookIsNonStatic(getHookMethod());
        return true;
    }

    @Override
    protected void inject(MethodNode methodNode) throws RuntimeException {
        // node to jump to if the hook is cancelled
        LabelNode jumpTo = new LabelNode();

        insert(
                methodNode,
                InsnBuilder.newInstance(generateInsnList(ICONST_0))
                        .push(new JumpInsnNode(IFEQ, jumpTo))
                        .getInstructions(),
                InsnBuilder.newInstance(generateInsnList(ICONST_1))
                        .push(new InsnNode(POP))
                        .push(jumpTo)
                        .getInstructions()
        );
    }
}
