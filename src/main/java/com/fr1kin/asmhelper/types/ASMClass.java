package com.fr1kin.asmhelper.types;

import com.fr1kin.asmhelper.ASMHelper;
import com.google.common.collect.Maps;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

/**
 * Created on 1/4/2017 by fr1kin
 */
public class ASMClass implements IASMType {
    protected static final Map<Type, ASMClass> ASM_CLASS_CACHE = Maps.newConcurrentMap();

    public static ASMClass getOrCreateClass(Type type) {
        return ASM_CLASS_CACHE.computeIfAbsent(type, key -> new ASMClass(type));
    }

    public static ASMClass getOrCreateClass(String classDescriptor) {
        return getOrCreateClass(ASMHelper.getInternalClassType(classDescriptor));
    }

    public static ASMClass getOrCreateClass(ClassNode classNode) {
        return getOrCreateClass(classNode.signature);
    }



    private final Type descriptor;

    /**
     * Constructs by type
     * @param type type
     */
    protected ASMClass(Type type) {
        this.descriptor = type;
    }

    /**
     * Gets the class name
     * @return class name with the internal class name syntax
     */
    public String getName() {
        return descriptor.getInternalName();
    }

    /**
     * Gets the class name
     * @return class name with regular name syntax
     */
    public String getClassName() {
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
     * @param isStatic if method is static
     * @param methodType return type and arguments
     * @return new instance of ASMMethod
     */
    public ASMMethod childMethod(String name, boolean isStatic, Type methodType) {
        return new ASMMethod(name, this, isStatic, methodType);
    }

    /**
     * Create new instance of ASMMethod
     * @param name name of method
     * @param isStatic if method is static
     * @param returnType methods return type
     * @param argumentTypes methods arguments
     * @return new instance of ASMMethod
     */
    public ASMMethod childMethod(String name, boolean isStatic, Type returnType, Type... argumentTypes) {
        return childMethod(name, isStatic, Type.getMethodType(returnType, argumentTypes));
    }

    /**
     * Creates new instance of ASMMethod
     * @param name name of method
     * @param isStatic if method is static
     * @param descriptor method descriptor
     * @return new instance of ASMMethod
     */
    public ASMMethod childMethod(String name, boolean isStatic, String descriptor) {
        return childMethod(name, isStatic, Type.getMethodType(descriptor));
    }

    /**
     * Create new instance of ASMMethod
     * ONLY ACCEPTS THESE OBJECT TYPES: String, ASMClass, Class, Type
     * @param name name of method
     * @param isStatic if method is static
     * @param returnType methods return type
     * @param argumentTypes methods arguments
     * @return new instance of ASMMethod
     */
    public ASMMethod childMethod(String name, boolean isStatic, Object returnType, Object... argumentTypes) {
        return childMethod(name, isStatic, ASMHelper.generateMethodDescriptor(returnType, argumentTypes));
    }

    /**
     * Creates new instance of ASMMethod
     * @param methodNode MethodNode object
     * @return new instance of ASMMethod
     */
    public ASMMethod childMethod(MethodNode methodNode) {
        return childMethod(methodNode.name, (methodNode.access & Opcodes.ACC_STATIC) != 0, methodNode.desc);
    }

    /**
     * Creates new instance of ASMField
     * @param name name of field
     * @param isStatic if field is static
     * @param type type descriptor of field
     * @return new instance of ASMField
     */
    public ASMField childField(String name, boolean isStatic, Type type) {
        return new ASMField(name, this, isStatic, type);
    }

    /**
     * Creates new instance of ASMField
     * @param name name of field
     * @param isStatic if field is static
     * @param typeDescriptor type descriptor of field
     * @return new instance of ASMField
     */
    public ASMField childField(String name, boolean isStatic, String typeDescriptor) {
        return childField(name, isStatic, Type.getObjectType(typeDescriptor));
    }

    /**
     * Creates new instance of ASMField
     * @param fieldNode field node
     * @return new instance of ASMField
     */
    public ASMField childField(FieldNode fieldNode) {
        return childField(fieldNode.name, (fieldNode.access & Opcodes.ACC_STATIC) != 0,fieldNode.desc);
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
