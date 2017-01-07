package com.fr1kin.asmhelper.types;

import com.fr1kin.asmhelper.ASMHelper;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created on 1/4/2017 by fr1kin
 */
public class ASMClass implements IASMType {
    private final Type descriptor;

    /**
     * Constructs by type
     * @param type type
     */
    public ASMClass(Type type) {
        this.descriptor = type;
    }

    /**
     * Gets the class name
     * @return class name with the regular class name syntax
     */
    public String getName() {
        return descriptor.getClassName();
    }

    /**
     * Gets the class as a descriptor
     * @return class descriptor
     */
    public String getDescriptor() {
        return descriptor.getDescriptor();
    }

    /**
     * Create new instance of ASMMethod
     * @param name name of method
     * @param returnType methods return type
     * @param argumentTypes methods arguments
     * @return new instance of ASMMethod
     */
    public ASMMethod childMethod(String name, Type returnType, Type... argumentTypes) {
        return new ASMMethod(name, this, Type.getMethodType(returnType, argumentTypes));
    }

    /**
     * Creates new instance of ASMMethod
     * @param name name of method
     * @param descriptor method descriptor
     * @return new instance of ASMMethod
     */
    public ASMMethod childMethod(String name, String descriptor) {
        return new ASMMethod(name, this, Type.getMethodType(descriptor));
    }

    /**
     * Create new instance of ASMMethod
     * ONLY ACCEPTS THESE OBJECT TYPES: String, ASMClass, Class, Type
     * @param name name of method
     * @param returnType methods return type
     * @param argumentTypes methods arguments
     * @return new instance of ASMMethod
     */
    public ASMMethod childMethod(String name, Object returnType, Object... argumentTypes) {
        return childMethod(name, ASMHelper.generateMethodDescriptor(returnType, argumentTypes));
    }

    /**
     * Creates new instance of ASMMethod
     * @param methodNode MethodNode object
     * @return new instance of ASMMethod
     */
    public ASMMethod childMethod(MethodNode methodNode) {
        return childMethod(methodNode.name, methodNode.desc);
    }

    /**
     * Creates new instance of ASMField
     * @param name name of field
     * @param type type descriptor of field
     * @return new instance of ASMField
     */
    public ASMField childField(String name, Type type) {
        return new ASMField(name, this, type);
    }

    /**
     * Creates new instance of ASMField
     * @param name name of field
     * @param typeDescriptor type descriptor of field
     * @return new instance of ASMField
     */
    public ASMField childField(String name, String typeDescriptor) {
        return childField(name, Type.getObjectType(typeDescriptor));
    }

    /**
     * Creates new instance of ASMField
     * @param fieldNode field node
     * @return new instance of ASMField
     */
    public ASMField childField(FieldNode fieldNode) {
        return childField(fieldNode.name, fieldNode.desc);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ASMClass)
            return this.getDescriptor().equals(((ASMClass) obj).getDescriptor());
        else return false;
    }
}
