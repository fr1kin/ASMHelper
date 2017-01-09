package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.types.ASMField;
import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

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

    public InsnBuilder push(AbstractInsnNode node) {
        if(node != null) insnList.insert(node);
        return this;
    }

    public InsnBuilder pushArguments(ASMMethod method) {
        int startingIndex = 0;
        for(Type type : method.getArguments()) {
            insnList.insert(new VarInsnNode(
                    type.getOpcode(type.getSort() != Type.ARRAY ? Opcodes.ILOAD : Opcodes.IALOAD),
                    startingIndex++
            ));
        }
        return this;
    }

    public InsnBuilder pushInvoke(int opcode, ASMMethod member) {
        insnList.insert(new MethodInsnNode(opcode,
                member.getParentClass().getDescriptor(),
                member.getName(),
                member.getDescriptor(),
                false
        ));
        return this;
    }

    public InsnBuilder pushInvoke(int opcode, ASMField field) {
        insnList.insert(new FieldInsnNode(opcode,
                field.getParentClass().getDescriptor(),
                field.getName(),
                field.getDescriptor()
        ));
        return this;
    }
}
