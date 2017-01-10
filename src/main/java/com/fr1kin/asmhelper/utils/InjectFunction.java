package com.fr1kin.asmhelper.utils;

import com.fr1kin.asmhelper.exceptions.DetourException;
import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created on 1/9/2017 by fr1kin
 */
public interface InjectFunction {
    void inject(MethodNode methodNode, ASMMethod targetMethod, ASMMethod hookMethod) throws DetourException;
}
