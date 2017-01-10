package com.fr1kin.asmhelper.utils;

import com.fr1kin.asmhelper.exceptions.NullNodeException;
import com.fr1kin.asmhelper.types.ASMField;
import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

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

    public InsnBuilder push(AbstractInsnNode node) throws NullNodeException {
        Verifier.checkIfNullNode(node);
        insnList.insert(node);
        return this;
    }

    public InsnBuilder pop() {
        push(new InsnNode(POP));
        return this;
    }

    public InsnBuilder pushArguments(ASMMethod method) {
        int startingIndex = 0;
        for(Type type : method.getArguments()) {
            push(new VarInsnNode(
                    type.getOpcode(type.getSort() != Type.ARRAY ? ILOAD : IALOAD),
                    startingIndex++
            ));
        }
        return this;
    }

    public InsnBuilder pushInvoke(int opcode, ASMMethod member) {
        push(new MethodInsnNode(opcode,
                member.getParentClass().getDescriptor(),
                member.getName(),
                member.getDescriptor(),
                false
        ));
        return this;
    }

    public InsnBuilder pushInvoke(int opcode, ASMField field) {
        push(new FieldInsnNode(opcode,
                field.getParentClass().getDescriptor(),
                field.getName(),
                field.getDescriptor()
        ));
        return this;
    }
}
