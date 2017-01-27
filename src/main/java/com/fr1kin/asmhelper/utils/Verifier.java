package com.fr1kin.asmhelper.utils;

import com.fr1kin.asmhelper.exceptions.IncompatibleMethodException;
import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.Type;

import java.util.Arrays;

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

    private static String extractArguments(String descriptor) {
        return descriptor.substring(descriptor.indexOf('(') + 1, descriptor.indexOf(')'));
    }

    protected static boolean containsAllArguments(Type method1, Type method2) {
        return extractArguments(method1.getDescriptor()).contains(extractArguments(method2.getDescriptor()));
    }

    protected static boolean containsAllArguments(Type[] args1, Type[] args2) {
        return containsAllArguments(Type.getMethodType(Type.VOID_TYPE, args1), Type.getMethodType(Type.VOID_TYPE, args2));
    }

    protected static boolean containsAllArguments(ASMMethod hookMethod, Type[] hookedMethodsArgs) {
        return containsAllArguments(hookMethod.getArguments(), hookedMethodsArgs);
    }

    protected static boolean containsAllArguments(ASMMethod hookMethod, ASMMethod hookedMethod) {
        return containsAllArguments(hookMethod, hookedMethod.getArguments());
    }

    public static void checkIfArgumentPresent(ASMMethod method, int argPos, Type argType) throws IncompatibleMethodException {
        boolean throwException = true;
        try {
            throwException = !Arrays.asList(method.getArguments()).get(argPos).equals(argType);
        } catch (RuntimeException e) {}
        if(throwException) throw new IncompatibleMethodException("argument '%s' missing from method", argType.getClassName());
    }

    public static void checkHookContainsAllArguments(ASMMethod hookMethod, ASMMethod hookedMethod) throws IncompatibleMethodException {
        Type[] types = hookedMethod.getArguments();
        if(!hookedMethod.isStatic()) {
            Type[] args = types;
            types = new Type[args.length + 1];
            types[0] = Type.getObjectType(hookedMethod.getParentClass().getName());
            System.arraycopy(args, 0, types, 1, args.length);
        }
        if(!containsAllArguments(hookMethod, types))
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
