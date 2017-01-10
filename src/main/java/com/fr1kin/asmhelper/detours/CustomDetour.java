package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.exceptions.IncompatibleMethodException;
import com.fr1kin.asmhelper.types.ASMMethod;
import com.fr1kin.asmhelper.utils.InjectFunction;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created on 1/9/2017 by fr1kin
 */
public class CustomDetour extends Detour {
    private final InjectFunction injectFunction;

    protected CustomDetour(ASMMethod method, ASMMethod hookMethod, InjectFunction injectFunction) throws IllegalArgumentException {
        super(method, hookMethod);
        this.injectFunction = injectFunction;
    }

    public InjectFunction getInjectFunction() {
        return injectFunction;
    }

    @Override
    protected boolean validate() throws IncompatibleMethodException {
        return true;
    }

    protected void inject(MethodNode methodNode) throws RuntimeException {
        getInjectFunction().inject(methodNode, getTargetMethod(), getHookMethod());
    }
}
