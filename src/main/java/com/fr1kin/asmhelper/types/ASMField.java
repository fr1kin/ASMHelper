package com.fr1kin.asmhelper.types;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;

/**
 * Created on 1/4/2017 by fr1kin
 */
public class ASMField extends ASMClassMember {
    private final Type type;

    /**
     * Sets this classes parent
     * @param name name of the field
     * @param parent parent class to what this class is representing
     */
    public ASMField(String name, ASMClass parent, boolean isStatic, Type descriptor) {
        super(name, parent, isStatic);
        this.type = descriptor;
    }

    /**
     * Gets the type descriptor
     * @return type descriptor
     */
    public String getTypeDescriptor() {
        return type.getDescriptor();
    }

    /**
     * Gets type
     * @return new instance of Type
     */
    public Type getType() {
        return type;
    }

    /**
     * Creates a node that will invoke this field
     * @param opcode invoke opcode
     * @return new instance of FieldInsnNode
     */
    public FieldInsnNode getInvokeFieldNode(int opcode) {
        return new FieldInsnNode(
                opcode,
                getParentClass().getDescriptor(),
                getName(),
                getTypeDescriptor()
        );
    }

    @Override
    public String toString() {
        return getTypeDescriptor() + " " + super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ASMField) {
            return this.getTypeDescriptor().equals(((ASMField) obj).getTypeDescriptor()) && super.equals(obj);
        } else return false;
    }
}
