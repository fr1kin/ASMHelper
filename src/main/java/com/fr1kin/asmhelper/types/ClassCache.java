package com.fr1kin.asmhelper.types;

import com.fr1kin.asmhelper.ASMHelper;
import com.google.common.collect.Maps;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

/**
 * Created on 1/6/2017 by fr1kin
 */
public class ClassCache {
    private static final Map<Type, ASMClass> ASM_CLASS_CACHE = Maps.newConcurrentMap();

    public static ASMClass getOrCreateClass(Type type) {
        return ASM_CLASS_CACHE.computeIfAbsent(type, key -> new ASMClass(type));
    }

    public static ASMClass getOrCreateClass(String classDescriptor) {
        return getOrCreateClass(ASMHelper.getInternalClassType(classDescriptor));
    }

    public static ASMClass getOrCreateClass(ClassNode classNode) {
        return getOrCreateClass(classNode.signature);
    }
}
