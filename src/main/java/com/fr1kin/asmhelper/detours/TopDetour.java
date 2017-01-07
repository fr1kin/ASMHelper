package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.exceptions.DetourException;
import com.fr1kin.asmhelper.types.ASMMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created on 1/5/2017 by fr1kin
 *
 * Places a detour at the top of a method
 */
public class TopDetour extends Detour {
    protected boolean insertBefore;

    public TopDetour(ASMMethod method, ASMMethod hookMethod) throws IllegalArgumentException {
        super(method, hookMethod);
        insertBefore = true;
    }

    protected InsnList generateInstructions(InsnList insnList, MethodNode methodNode, ASMMethod method) {
        // if method is static
        boolean isStatic = (methodNode.access & Opcodes.ACC_STATIC) != 0;
        // current index
        int index = 0;
        if(!isStatic) {
            // if not static then push the this instance
            insnList.insert(new VarInsnNode(ALOAD, index++));
        }
        // push all method arguments
        for(Type type : method.getArguments()) {
            int loadOpcode = type.getOpcode(type.getSort() != Type.ARRAY ? Opcodes.ILOAD : Opcodes.IALOAD);
            insnList.insert(new VarInsnNode(loadOpcode, index++));
        }
        return insnList;
    }

    protected AbstractInsnNode findInsertNode(MethodNode methodNode, ASMMethod method) {
        return methodNode.instructions.getFirst();
    }

    @Override
    protected void inject(MethodNode methodNode, ASMMethod method) throws RuntimeException {
        InsnList insnList = new InsnList();
        generateInstructions(insnList, methodNode, method);

        // call hook method
        insnList.insert(new MethodInsnNode(INVOKESTATIC,
                getHookMethod().getParentClass().getDescriptor(),
                getHookMethod().getName(),
                getHookMethod().getDescriptor(),
                false
        ));

        AbstractInsnNode injectNode = findInsertNode(methodNode, method);

        if(injectNode == null)
            throw new DetourException("[%s] failed to find injection node for method '%s'", getClass().getSimpleName(), method.toString());

        // insert the code
        if(insertBefore) {
            // call the hook method before any instructions
            methodNode.instructions.insertBefore(injectNode, insnList);
        } else {
            // call the hook method after
            methodNode.instructions.insert(injectNode, insnList);
        }
    }
}
