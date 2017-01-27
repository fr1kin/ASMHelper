package com.fr1kin.asmhelper.types;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;

/**
 * Created on 1/4/2017 by fr1kin
 */
public class ASMField extends ASMClassMember {
    private final Type type;

    /**
     * Sets this test parent
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
    public String getDescriptor() {
        return type.getDescriptor();
    }

    /**
     * Gets type
     * @return new instance of Type
     */
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return getParentClass().getName() + "." + getName() + ":" + getDescriptor();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ASMField) {
            return this.getDescriptor().equals(((ASMField) obj).getDescriptor()) && super.equals(obj);
        } else return false;
    }
}
