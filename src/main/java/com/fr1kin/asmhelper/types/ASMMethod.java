package com.fr1kin.asmhelper.types;

import com.fr1kin.asmhelper.ASMHelper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

/**
 * Created on 1/4/2017 by fr1kin
 */
public class ASMMethod extends ASMClassMember {
    private final Type descriptor;

    /**
     * Sets this classes parent
     *
     * @param name   name of the derived object
     * @param parent parent class to what this class is representing
     * @param descriptor method descriptor
     */
    public ASMMethod(String name, ASMClass parent,  boolean isStatic, Type descriptor) {
        super(name, parent, isStatic);
        this.descriptor = descriptor;
    }

    /**
     * Gets the descriptor for the return type
     * @return descriptor for the return type
     */
    public String getReturnTypeDescriptor() {
        return descriptor.getReturnType().getDescriptor();
    }

    /**
     * Gets the full descriptor for this method (args)return_type
     * @return descriptor
     */
    public String getDescriptor() {
        return descriptor.getDescriptor();
    }

    /**
     * Gets return type
     * @return instance of Type
     */
    public Type getReturnType() {
        return descriptor.getReturnType();
    }

    /**
     * Returns a list of arguments
     * @return list of args as Type objects
     */
    public Type[] getArguments() {
        return descriptor.getArgumentTypes();
    }

    @Override
    public String toString() {
        return super.toString() + getDescriptor();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ASMMethod) {
            return this.getDescriptor().equals(((ASMMethod) obj).getDescriptor()) && super.equals(obj);
        } else return false;
    }
}
