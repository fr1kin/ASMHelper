package com.fr1kin.asmhelper.utils.locator;

import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;

/**
 * Created on 1/8/2017 by fr1kin
 */
public interface ILocator {
    AbstractInsnNode apply(MethodNode methodNode, ASMMethod hookedMethod, ASMMethod hookMethod);

    default boolean isInsertedBefore() {
        try {
            Method method = getClass().getMethod("apply", MethodNode.class, ASMMethod.class, ASMMethod.class);
            return !method.isAnnotationPresent(InsertedBefore.class) || method.getAnnotation(InsertedBefore.class).value();
        } catch (Exception e) {
            // return true by default
            return true;
        }
    }
}
