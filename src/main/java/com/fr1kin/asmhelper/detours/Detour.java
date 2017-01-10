package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.exceptions.DetourException;
import com.fr1kin.asmhelper.exceptions.IncompatibleMethodException;
import com.fr1kin.asmhelper.exceptions.NoMethodFound;
import com.fr1kin.asmhelper.types.ASMClass;
import com.fr1kin.asmhelper.types.ASMMethod;
import com.fr1kin.asmhelper.utils.Verifier;
import org.objectweb.asm.tree.*;

/**
 * Created on 1/5/2017 by fr1kin
 */
public abstract class Detour {
    private final ASMMethod targetMethod;
    private final ASMMethod hookMethod;

    public Detour(ASMMethod targetMethod, ASMMethod hookMethod)
            throws IllegalArgumentException {
        if(targetMethod.getParentClass() == null) throw new IllegalArgumentException(String.format("targetMethod '%s' has no parent class defined", targetMethod.toString()));
        this.targetMethod = targetMethod;
        this.hookMethod = hookMethod;

    }

    /**
     * Parent class to the targetMethod being hooked
     * @return ASMClass instance of parent
     */
    public ASMClass getParentClass() {
        return targetMethod.getParentClass();
    }

    /**
     * Get the targetMethod being hooked
     * @return targetMethod
     */
    public ASMMethod getTargetMethod() {
        return targetMethod;
    }

    /**
     * Get the hook to be injected into the targetMethod
     * @return hookMethod
     */
    public ASMMethod getHookMethod() {
        return hookMethod;
    }

    /**
     * Apply the hook injection onto class
     * @param parentClass ASMClass of the runtime class containing the hooked targetMethod
     * @param classNode ClassNode to attempt code injection on
     * @return true if the class was patched successfully with no exceptions thrown
     *          false if the class node did not match with the hooked methods class name
     * @throws DetourException if targetMethod is not found
     */
    public boolean apply(ASMClass parentClass, ClassNode classNode)
            throws DetourException {
        if(parentClass.equals(getParentClass())) {
            MethodNode methodNode = findMethod(parentClass, classNode);
            Verifier.checkHookIsNonStatic(getHookMethod()); //TODO: support non-static hooks, cba atm
            if(validate()) inject(methodNode);
            return true;
        } else return false;
    }

    private MethodNode findMethod(ASMClass parentClass, ClassNode node)
            throws NoMethodFound {
        for(MethodNode method : node.methods) {
            if(parentClass.childMethod(method).equals(getTargetMethod()))
                return method;
        }
        throw new NoMethodFound("method not found");
    }

    protected abstract boolean validate() throws IncompatibleMethodException;

    protected abstract void inject(MethodNode methodNode) throws DetourException;
}
