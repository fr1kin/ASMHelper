package com.fr1kin.asmhelper.test;

import com.fr1kin.asmhelper.types.ASMClass;
import com.fr1kin.asmhelper.types.ASMMethod;
import com.fr1kin.asmhelper.types.ClassCache;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Created on 1/6/2017 by fr1kin
 */
public class TestMain {
    // TODO: actual unit tests

    public static void main(String[] args) {
        String name = String.class.getName();
        ASMClass asmClass = ClassCache.getOrCreateClass(name);
        ASMMethod asmMethod = asmClass.childMethod("format", String.class, int.class, Object[].class);

        for(Type type : asmMethod.getArguments()) {
            System.out.print(type.getClassName() + " : ");
            System.out.println(type.getOpcode(type.getSort() != Type.ARRAY ? Opcodes.ILOAD : Opcodes.IALOAD));
        }
    }
}
