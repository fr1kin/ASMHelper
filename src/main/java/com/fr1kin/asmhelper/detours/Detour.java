package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.exceptions.NoMethodFound;
import com.fr1kin.asmhelper.types.ASMClass;
import com.fr1kin.asmhelper.types.ASMMethod;
import com.fr1kin.asmhelper.types.ClassCache;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created on 1/5/2017 by fr1kin
 */
public abstract class Detour {
    private final ASMMethod method;
    private final ASMMethod hookMethod;

    public Detour(ASMMethod method, ASMMethod hookMethod)
            throws IllegalArgumentException {
        if(method.getParentClass() == null) throw new IllegalArgumentException("method does not have parent class defined");
        this.method = method;
        this.hookMethod = hookMethod;
    }

    /**
     * Parent class to the method being hooked
     * @return ASMClass instance of parent
     */
    public ASMClass getParentClass() {
        return method.getParentClass();
    }

    /**
     * Get the method being hooked
     * @return method
     */
    public ASMMethod getMethod() {
        return method;
    }

    /**
     * Get the hook to be injected into the method
     * @return hookMethod
     */
    public ASMMethod getHookMethod() {
        return hookMethod;
    }

    /**
     * Apply the hook injection onto class
     * @param classNode ClassNode to attempt code injection on
     * @return true if the class was patched successfully with no exceptions thrown
     *          false if the class node did not match with the hooked methods class name
     * @throws RuntimeException if method is not found
     */
    public boolean apply(ClassNode classNode)
            throws RuntimeException {
        if(classNode.name.equals(getParentClass().getName())) {
            MethodNode methodNode = findMethod(classNode);
            inject(methodNode, ClassCache.getOrCreateClass(classNode).childMethod(methodNode));
            return true;
        } else return false;
    }

    private MethodNode findMethod(ClassNode node)
            throws NoMethodFound {
        for(MethodNode method : node.methods) {
            if(method.name.equals(this.method.getName()) && method.desc.equals(this.method.getDescriptor()))
                return method;
        }
        throw new NoMethodFound("Failed to match method '%s' in class '%s'", this.method.toString(), node.name);
    }

    protected abstract void inject(MethodNode methodNode, ASMMethod method) throws RuntimeException;
}
