package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.types.ASMMethod;
import com.fr1kin.asmhelper.utils.InjectFunction;
import com.fr1kin.asmhelper.utils.locator.ILocator;

/**
 * Created on 1/8/2017 by fr1kin
 */
public class Detours {
    /**
     * Creates new instance of a simple detour
     * REQUIREMENTS:
     *      A void return type
     *      All of the target methods arguments
     *      To be a static method call
     * @param targetMethod target method
     * @param hookMethod hook method
     * @param locator node locator
     * @return new detour instance
     */
    public static Detour newSimpleDetour(ASMMethod targetMethod, ASMMethod hookMethod, ILocator locator) {
        return new SimpleDetour(targetMethod, hookMethod, locator);
    }

    /**
     * Creates new instance of a pre-post detour
     * Will place two calls to the hook method at the given nodes
     * REQUIREMENTS:
     *      A void return type
     *      The first argument must be a integer, this will indicate if it is a pre or post call (0 for PRE, 1 for POST)
     *      All of the target methods arguments
     *      To be a static method call
     * @param targetMethod target method
     * @param hookMethod hook method
     * @param preLocator pre node locator
     * @param postLocator post node locator
     * @return new detour instance
     */
    public static Detour newPrePosDetour(ASMMethod targetMethod, ASMMethod hookMethod, ILocator preLocator, ILocator postLocator) {
        return new PrePostDetour(targetMethod, hookMethod, preLocator, postLocator);
    }

    /**
     * Creates new instance of a pre-post detour that can be cancelled
     * Will place two calls to the hook method at the given nodes.
     * If the hook call returns true, then the hook is cancelled and will jump to after where ever the post method call is
     * and the post method will not be called. Otherwise the code continues as it normally would
     * REQUIREMENTS:
     *      A boolean return type
     *      The first argument must be a integer, this will indicate if it is a pre or post call (0 for PRE, 1 for POST)
     *      All of the target methods arguments
     *      To be a static method call
     * @param targetMethod target method
     * @param hookMethod hook method
     * @param preLocator pre node locator
     * @param postLocator post node locator
     * @return new detour instance
     */
    public static Detour newCancellablePrePosDetour(ASMMethod targetMethod, ASMMethod hookMethod, ILocator preLocator, ILocator postLocator) {
        return new CancellablePrePostDetour(targetMethod, hookMethod, preLocator, postLocator);
    }

    /**
     * A custom detour for situations where more unique code is required
     * Example:
     * Detours.newCustomDetour(targetM, hookM, (methodNode, targetMethod, hookMethod) -> {
     *     // code goes here
     * });
     * @param targetMethod target method
     * @param hookMethod hook method
     * @return new detour instance
     */
    public static Detour newCustomDetour(ASMMethod targetMethod, ASMMethod hookMethod, InjectFunction function) {
        return new CustomDetour(targetMethod, hookMethod, function);
    }
}
