package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.exceptions.DetourException;
import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created on 1/7/2017 by fr1kin
 *
 * Inserts a post and pre detour that can be cancelled
 * NOTE: One has to specify the post node manually
 */
public abstract class PrePostCancellableDetour extends PrePostDetour {
    public PrePostCancellableDetour(ASMMethod method, ASMMethod hookMethod, boolean insertedBeforePre, boolean insertedBeforePost)
            throws IllegalArgumentException {
        super(method, hookMethod, insertedBeforePre, insertedBeforePost);
    }

    @Override
    protected void inject(MethodNode methodNode) throws RuntimeException {
        // node to jump to if the hook is cancelled
        LabelNode jumpTo = new LabelNode();

        // get instructions for calling hooks
        InsnList insnListPre = generateCallbackInsc(true);
        InsnList insnListPost = generateCallbackInsc(false);

        // pre: jump to label if the event is canceled
        insnListPre.insert(new JumpInsnNode(IFEQ, jumpTo));

        // post: pop the stack to get the boolean off
        insnListPost.insert(new InsnNode(POP));
        // post: insert the label to jump to
        insnListPost.insert(jumpTo);

        AbstractInsnNode preNode = getPreInsertNode(methodNode);
        AbstractInsnNode postNode = getPostInsertNode(methodNode);

        if (preNode == null)
            throw new DetourException(this, "Failed to find PRE injection node in method '%s'", getMethod().toString());
        if (postNode == null)
            throw new DetourException(this, "Failed to find POST injection node in method '%s'", getMethod().toString());

        Detour.insertCode(methodNode, preNode, insnListPre, insertedBeforePre);
        Detour.insertCode(methodNode, postNode, insnListPost, insertedBeforePost);
    }
}
