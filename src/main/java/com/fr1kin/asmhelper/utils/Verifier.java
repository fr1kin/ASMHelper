package com.fr1kin.asmhelper.utils;

import com.fr1kin.asmhelper.exceptions.DetourException;
import com.fr1kin.asmhelper.exceptions.IncompatibleMethodException;
import com.fr1kin.asmhelper.exceptions.NullNodeException;
import com.fr1kin.asmhelper.types.ASMMethod;
import com.google.common.io.BaseEncoding;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created on 1/8/2017 by fr1kin
 * Shoving all the ugly code into one file
 */
public class Verifier {
    public static boolean isTypesEqualTo(Type type1, Type type2) {
        return type1.equals(type2);
    }

    public static boolean isReturnTypesEqualTo(ASMMethod method, Type type2) {
        return isTypesEqualTo(method.getReturnType(), type2);
    }

    protected static boolean containsAllArguments(Type[] args1, Type... args2) {
        // match everything inbetween ( )
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        // return type doesnt matter. regexing it right out
        String args1Desc = pattern.matcher(Type.getMethodDescriptor(Type.VOID_TYPE, args1)).group(1);
        String args2Desc = pattern.matcher(Type.getMethodDescriptor(Type.VOID_TYPE, args2)).group(1);
        // args1 (hook method) will have more arguments (atleast it should), so we check if the entirety of args2 is present in args1
        // because the hooked method should be passing all of its arguments to the hook
        return args1Desc.contains(args2Desc);
    }

    protected static boolean containsAllArguments(ASMMethod hookMethod, Type... hookedMethodsArgs) {
        return containsAllArguments(hookMethod.getArguments(), hookedMethodsArgs);
    }

    protected static boolean containsAllArguments(ASMMethod hookMethod, ASMMethod hookedMethod) {
        return containsAllArguments(hookMethod, hookedMethod.getArguments());
    }

    public static void checkIfArgumentPresent(ASMMethod method, int argPos, Type argType) throws IncompatibleMethodException {
        boolean throwException = true;
        try {
            throwException = !Arrays.asList(method.getArguments()).get(argPos).equals(argType);
        } catch (IndexOutOfBoundsException e) {}
        if(throwException) throw new IncompatibleMethodException("argument '%s' missing from method", argType.getClassName());
    }

    public static void checkIfNullNode(String name, AbstractInsnNode node) throws NullNodeException {
        if(node == null) throw new NullNodeException("%s is null", name);
    }

    public static void checkIfNullNode(AbstractInsnNode node) throws NullNodeException {
        checkIfNullNode("node", node);
    }

    public static void checkHookContainsAllArguments(ASMMethod hookMethod, ASMMethod hookedMethod) throws IncompatibleMethodException {
        if(!containsAllArguments(hookMethod, hookedMethod))
            throw new IncompatibleMethodException("hook does not contain all arguments that the target method has");
    }

    public static void checkHookReturnType(ASMMethod hookMethod, Type type) throws IncompatibleMethodException {
        if(!isReturnTypesEqualTo(hookMethod, type))
            throw new IncompatibleMethodException(
                    "hook has bad return type '%s' (should be '%s')",
                    hookMethod.getReturnType().getClassName(),
                    type.getClassName()
            );
    }

    public static void checkHookIsNonStatic(ASMMethod hookMethod) throws IncompatibleMethodException {
        if(!hookMethod.isStatic())
            throw new IncompatibleMethodException("non-static hook is not supported");
    }
}
