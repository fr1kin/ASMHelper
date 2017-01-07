package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created on 1/6/2017 by fr1kin
 *
 * Places a detour before the return statement
 */
public class ReturnDetour extends TopDetour {
    public ReturnDetour(ASMMethod method, ASMMethod hookMethod) throws IllegalArgumentException {
        super(method, hookMethod);
        insertBefore = true;
    }

    @Override
    protected AbstractInsnNode findInsertNode(MethodNode methodNode, ASMMethod method) {
        Type returnType = method.getReturnType();
        int returnTypeOpcode = returnType.getOpcode(Opcodes.IRETURN);

        AbstractInsnNode next = methodNode.instructions.getFirst();
        while(next != null) {
            if(next.getOpcode() == returnTypeOpcode)
                return next;
            else
                next = next.getNext();
        }
        return null;
    }
}
