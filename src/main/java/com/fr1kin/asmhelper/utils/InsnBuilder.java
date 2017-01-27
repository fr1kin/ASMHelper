package com.fr1kin.asmhelper.utils;

import com.fr1kin.asmhelper.types.ASMField;
import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created on 1/8/2017 by fr1kin
 */
public class InsnBuilder {
    public static final InsnBuilder newInstance() {
        return new InsnBuilder();
    }

    public static final InsnBuilder newInstance(InsnList insnList) {
        return new InsnBuilder(insnList);
    }

    private final InsnList insnList;

    public InsnBuilder() {
        this.insnList = new InsnList();
    }

    public InsnBuilder(InsnList insnList) {
        this.insnList = insnList;
    }

    public InsnList getInstructions() {
        return insnList;
    }

    public InsnBuilder add(AbstractInsnNode node) throws NullPointerException {
        Objects.requireNonNull(node, "cannot add null node");
        insnList.add(node);
        return this;
    }

    public InsnBuilder pop() {
        add(new InsnNode(POP));
        return this;
    }

    public InsnBuilder addArguments(ASMMethod method) {
        int startingIndex = 0;
        if(!method.isStatic()) {
            add(new VarInsnNode(ALOAD, startingIndex++));
        }
        for(Type type : method.getArguments()) {
            add(new VarInsnNode(
                    type.getOpcode(type.getSort() != Type.ARRAY ? ILOAD : IALOAD),
                    startingIndex++
            ));
        }
        return this;
    }

    public InsnBuilder addInvoke(int opcode, ASMMethod member) {
        add(new MethodInsnNode(opcode,
                member.getParentClass().getName(),
                member.getName(),
                member.getDescriptor(),
                false
        ));
        return this;
    }

    public InsnBuilder addInvoke(int opcode, ASMField field) {
        add(new FieldInsnNode(opcode,
                field.getParentClass().getName(),
                field.getName(),
                field.getDescriptor()
        ));
        return this;
    }
}
