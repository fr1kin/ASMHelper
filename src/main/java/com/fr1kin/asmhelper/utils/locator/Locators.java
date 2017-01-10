package com.fr1kin.asmhelper.utils.locator;

import com.fr1kin.asmhelper.ASMHelper;
import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created on 1/8/2017 by fr1kin
 */
public class Locators {
    /**
     * Finds the first node that is a valid opcode
     * Inserts code before
     * @param methodNode method to search
     * @param hookedMethod method being hooked
     * @param hookMethod hook method
     * @return null if no node is found, otherwise the target node
     */
    @InsertedBefore(true)
    public static AbstractInsnNode firstNode(MethodNode methodNode, ASMMethod hookedMethod, ASMMethod hookMethod) {
        return ASMHelper.parseNextNode(methodNode.instructions.getFirst(), n -> ASMHelper.isValidOpcode(n.getOpcode()));
    }

    /**
     * Finds the return node
     * Inserts code before
     * @param methodNode method to search
     * @param hookedMethod method being hooked
     * @param hookMethod hook method
     * @return null if no node is found, otherwise the target node
     */
    @InsertedBefore(true)
    public static AbstractInsnNode returnNode(MethodNode methodNode, ASMMethod hookedMethod, ASMMethod hookMethod) {
        // gets the proper opcode for the return type
        final int returnTypeOpcode = hookedMethod.getReturnType().getOpcode(Opcodes.IRETURN);
        return ASMHelper.parsePreviousNode(methodNode.instructions.getFirst(), n -> n.getOpcode() == returnTypeOpcode);
    }

    /**
     * Finds the first label node
     * Inserts code after
     * @param methodNode method to search
     * @param hookedMethod method being hooked
     * @param hookMethod hook method
     * @return null if no node is found, otherwise the target node
     */
    @InsertedBefore(false)
    public static AbstractInsnNode firstLabelNode(MethodNode methodNode, ASMMethod hookedMethod, ASMMethod hookMethod) {
        return ASMHelper.parseNextNode(methodNode.instructions.getLast(), n -> n instanceof LabelNode);
    }

    /**
     * Finds the last label node
     * Inserts code after
     * @param methodNode method to search
     * @param hookedMethod method being hooked
     * @param hookMethod hook method
     * @return null if no node is found, otherwise the target node
     */
    @InsertedBefore(false)
    public static AbstractInsnNode lastLabelNode(MethodNode methodNode, ASMMethod hookedMethod, ASMMethod hookMethod) {
        return ASMHelper.parsePreviousNode(methodNode.instructions.getLast(), n -> n instanceof LabelNode);
    }
}
