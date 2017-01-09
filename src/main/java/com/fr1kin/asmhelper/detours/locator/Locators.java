package com.fr1kin.asmhelper.detours.locator;

import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created on 1/8/2017 by fr1kin
 */
public class Locators {
    /**
     * Locates the first node
     */
    @InsertedBefore(true)
    public static AbstractInsnNode firstNode(MethodNode methodNode, ASMMethod hookedMethod, ASMMethod hookMethod) {
        return methodNode.instructions.getFirst();
    }

    /**
     * Locates the return node
     */
    @InsertedBefore(true)
    public static AbstractInsnNode returnNode(MethodNode methodNode, ASMMethod hookedMethod, ASMMethod hookMethod) {
        Type returnType = hookedMethod.getReturnType();
        // gets the proper opcode for the return type
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
