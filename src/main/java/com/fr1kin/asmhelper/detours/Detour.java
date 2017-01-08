package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.exceptions.IncompatibleMethodException;
import com.fr1kin.asmhelper.exceptions.NoMethodFound;
import com.fr1kin.asmhelper.types.ASMClass;
import com.fr1kin.asmhelper.types.ASMMethod;
import com.fr1kin.asmhelper.types.ClassCache;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created on 1/5/2017 by fr1kin
 */
public abstract class Detour {
    public static final int PRE_METHOD = 0;
    public static final int POST_METHOD = 1;

    protected static void insertCode(MethodNode methodNode, AbstractInsnNode node, InsnList list, boolean before) {
        if(before) methodNode.instructions.insertBefore(node, list);
        else methodNode.instructions.insert(node, list);
    }

    protected static void insertCode(MethodNode methodNode, AbstractInsnNode node, AbstractInsnNode insnNode, boolean before) {
        if(before) methodNode.instructions.insertBefore(node, insnNode);
        else methodNode.instructions.insert(node, insnNode);
    }

    protected static boolean checkArguments(Type[] args1, Type... args2) {
        // match everything inbetween ( )
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        // return type doesnt matter. regexing it right out
        String args1Desc = pattern.matcher(Type.getMethodDescriptor(Type.VOID_TYPE, args1)).group(1);
        String args2Desc = pattern.matcher(Type.getMethodDescriptor(Type.VOID_TYPE, args2)).group(1);
        // args1 (hook method) will have more arguments (atleast it should), so we check if the entirety of args2 is present in args1
        // because the hooked method should be passing all of its arguments to the hook
        return args1Desc.contains(args2Desc);
    }

    protected static boolean checkArguments(ASMMethod hookMethod, Type... hookedMethodsArgs) {
        List<Type> types = Arrays.asList(hookMethod.getArguments());
        // if hook is non-static then remove first argument from descriptor (the this pointer)
        if(!hookMethod.isStatic()) types.remove(0);
        return checkArguments((Type[])types.toArray(), hookedMethodsArgs);
    }

    protected static boolean checkArguments(ASMMethod hookMethod, ASMMethod hookedMethod) {
        return checkArguments(hookMethod, hookedMethod.getArguments());
    }

    private final ASMMethod method;
    private final ASMMethod hookMethod;

    public Detour(ASMMethod method, ASMMethod hookMethod)
            throws IllegalArgumentException {
        if(method.getParentClass() == null) throw new IllegalArgumentException(String.format("method '%s' has no parent class defined", method.toString()));
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
     * @param parentClass ASMClass of the runtime class containing the hooked method
     * @param classNode ClassNode to attempt code injection on
     * @return true if the class was patched successfully with no exceptions thrown
     *          false if the class node did not match with the hooked methods class name
     * @throws RuntimeException if method is not found
     */
    public boolean apply(ASMClass parentClass, ClassNode classNode)
            throws RuntimeException {
        if(parentClass.equals(getParentClass())) {
            MethodNode methodNode = findMethod(parentClass, classNode);
            if(validate()) inject(methodNode);
            return true;
        } else return false;
    }

    private MethodNode findMethod(ASMClass parentClass, ClassNode node)
            throws NoMethodFound {
        for(MethodNode method : node.methods) {
            if(parentClass.childMethod(method).equals(getMethod()))
                return method;
        }
        throw new NoMethodFound(getClass(), "failed to match method '%s' in class '%s'", getMethod().toString(), node.name);
    }

    protected abstract boolean validate() throws IncompatibleMethodException;

    protected abstract void inject(MethodNode methodNode) throws RuntimeException;
}
