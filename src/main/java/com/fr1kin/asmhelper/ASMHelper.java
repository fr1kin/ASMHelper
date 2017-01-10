package com.fr1kin.asmhelper;

import com.fr1kin.asmhelper.exceptions.FailedToMatchPatternException;
import com.fr1kin.asmhelper.exceptions.NullNodeException;
import com.fr1kin.asmhelper.types.ASMClass;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created on 1/4/2017 by fr1kin
 */
public class ASMHelper {
    public static final char MASK_PARSE     = 'x';
    public static final char MASK_IGNORE    = '?';

    public static final int PRE             = 0;
    public static final int POST            = 1;

    public enum ReturnNodePos {
        TOP,
        BOTTOM
    }

    /**
     * Parses a node
     * @param initialNode node to start at
     * @param nextFunction next node function
     * @param predicateFunction check node function
     * @return null if no node found, otherwise the targeted node
     */
    public static AbstractInsnNode parseNode(AbstractInsnNode initialNode, Function<AbstractInsnNode, AbstractInsnNode> nextFunction, Predicate<AbstractInsnNode> predicateFunction) {
        AbstractInsnNode n = initialNode;
        while(n != null) {
            if(predicateFunction.test(n)) return n;
            n = nextFunction.apply(n);
        }
        return null;
    }

    /**
     * Parses a node from first to last
     * @param startingNode node to start at
     * @param predicateFunction check node function
     * @return null if no node found, otherwise the targeted node
     */
    public static AbstractInsnNode parseNextNode(AbstractInsnNode startingNode, Predicate<AbstractInsnNode> predicateFunction) {
        return parseNode(startingNode, AbstractInsnNode::getNext, predicateFunction);
    }

    /**
     * Parses a node from last to first
     * @param lastNode node to start at
     * @param predicateFunction check node function
     * @return null if no node found, otherwise the targeted node
     */
    public static AbstractInsnNode parsePreviousNode(AbstractInsnNode lastNode, Predicate<AbstractInsnNode> predicateFunction) {
        return parseNode(lastNode, AbstractInsnNode::getPrevious, predicateFunction);
    }

    /**
     * Attempts to find a pattern of opcodes starting at the given node.
     * @param node node to start parsing at
     * @param pattern array of opcodes to try and match
     * @param mask array that tells the parser which opcodes to parse and which to ignore in the pattern
     * @param pos if the matched pattern should return the node at the top of the pattern or the bottom of the pattern
     * @return The node from the matched pattern
     * @throws FailedToMatchPatternException if no pattern is matched
     * @throws IllegalArgumentException if any argument is null or has an empty array
     */
    public static AbstractInsnNode findPattern(AbstractInsnNode node, final int[] pattern, final char[] mask, ReturnNodePos pos)
            throws FailedToMatchPatternException, IllegalArgumentException {
        if(node == null) throw new IllegalArgumentException("node is null");
        if(pattern == null || pattern.length < 1) throw new IllegalArgumentException("pattern is null or empty");
        if(mask == null || mask.length < 1) throw new IllegalArgumentException("mask is null or empty");
        if(pattern.length != mask.length) throw new IllegalArgumentException("pattern and mask are of different sizes");
        int found = 0;
        AbstractInsnNode next = node;
        do {
            switch(mask[found]) {
                // Analyze this node
                case MASK_PARSE: {
                    // Check if node and pattern have same opcode
                    if(next.getOpcode() == pattern[found]) {
                        // Increment number of matched opcodes
                        found++;
                    } else {
                        // Go back to the starting node
                        for(int i = 1; i <= (found - 1); i++) {
                            next = next.getPrevious();
                        }
                        // Reset the number of opcodes found
                        found = 0;
                    }
                    break;
                }
                // Skips over this node
                default:
                case MASK_IGNORE:
                    found++;
                    break;
            }
            // Check if found entire pattern
            if(found >= mask.length) {
                switch (pos) {
                    default:
                    case TOP:
                        for(int i = 1; i <= (found - 1); i++) {
                            next = next.getPrevious();
                        }
                    case BOTTOM:
                        break;
                }
                return next;
            }
            next = next.getNext();
        } while(next != null &&
                found < mask.length);
        // if we hit here, no pattern was found so a FailedToMatchPatternException is thrown
        throw new FailedToMatchPatternException("Pattern not found");
    }

    /**
     * Attempts to find a pattern of opcodes starting at the given node.
     * Will auto generate the mask (all opcodes below 0 will be ignored)
     * @param node node to start parsing at
     * @param pattern array of opcodes to try and match
     * @param pos if the matched pattern should return the node at the top of the pattern or the bottom of the pattern
     * @return The node from the matched pattern
     * @throws FailedToMatchPatternException if no pattern is matched
     * @throws IllegalArgumentException if any argument is null or has an empty array
     */
    public static AbstractInsnNode findPattern(AbstractInsnNode node, final int[] pattern, ReturnNodePos pos)
            throws FailedToMatchPatternException, IllegalArgumentException {
        return findPattern(node, pattern, generateMask(pattern), pos);
    }

    /**
     * Attempts to find a pattern of opcodes starting at the given node.
     * Returns the node at the start of the pattern
     * @param node node to start parsing at
     * @param pattern array of opcodes to try and match
     * @param mask array that tells the parser which opcodes to parse and which to ignore in the pattern
     * @return The top node from the matched pattern
     * @throws FailedToMatchPatternException if no pattern is matched
     * @throws IllegalArgumentException if any argument is null or has an empty array
     */
    public static AbstractInsnNode findPatternTop(AbstractInsnNode node, final int[] pattern, final char[] mask)
            throws FailedToMatchPatternException, IllegalArgumentException {
        return findPattern(node, pattern, mask, ReturnNodePos.TOP);
    }

    /**
     * Attempts to find a pattern of opcodes starting at the given node.
     * Will auto generate the mask (all opcodes below 0 will be ignored)
     * Returns the node at the start of the pattern
     * @param node node to start parsing at
     * @param pattern array of opcodes to try and match
     * @return The top node from the matched pattern
     * @throws FailedToMatchPatternException if no pattern is matched
     * @throws IllegalArgumentException if any argument is null or has an empty array
     */
    public static AbstractInsnNode findPatternTop(AbstractInsnNode node, final int[] pattern)
            throws FailedToMatchPatternException, IllegalArgumentException {
        return findPattern(node, pattern, generateMask(pattern), ReturnNodePos.TOP);
    }

    /**
     * Attempts to find a pattern of opcodes starting at the given node.
     * Returns the node at the end of the pattern
     * @param node node to start parsing at
     * @param pattern array of opcodes to try and match
     * @param mask array that tells the parser which opcodes to parse and which to ignore in the pattern
     * @return The bottom node from the matched pattern
     * @throws FailedToMatchPatternException if no pattern is matched
     * @throws IllegalArgumentException if any argument is null or has an empty array
     */
    public static AbstractInsnNode findPatternBottom(AbstractInsnNode node, final int[] pattern, final char[] mask)
            throws FailedToMatchPatternException, IllegalArgumentException {
        return findPattern(node, pattern, mask, ReturnNodePos.BOTTOM);
    }

    /**
     * Attempts to find a pattern of opcodes starting at the given node.
     * Will auto generate the mask (all opcodes below 0 will be ignored)
     * Returns the node at the end of the pattern
     * @param node node to start parsing at
     * @param pattern array of opcodes to try and match
     * @return The bottom node from the matched pattern
     * @throws FailedToMatchPatternException if no pattern is matched
     * @throws IllegalArgumentException if any argument is null or has an empty array
     */
    public static AbstractInsnNode findPatternBottom(AbstractInsnNode node, final int[] pattern)
            throws FailedToMatchPatternException, IllegalArgumentException {
        return findPattern(node, pattern, generateMask(pattern), ReturnNodePos.BOTTOM);
    }

    /**
     * Generates a mask from a pattern by marking all opcodes below 0 as ignored.
     * @param pattern pattern to generate the mask from
     * @return a character array containing the mask to be used
     */
    public static char[] generateMask(final int[] pattern) {
        char[] array = new char[pattern.length];
        for(int i = 0; i < pattern.length; i++) {
            if(pattern[i] < 0)
                array[i] = MASK_IGNORE;
            else
                array[i] = MASK_PARSE;
        }
        return array;
    }

    /**
     * Gets the descriptor for the object.
     * Only accepts String, ASMClass, Class, and Type as arguments.
     * @param obj object to get descriptor for
     * @return descriptor for the object
     * @throws IllegalArgumentException if obj is not a instance of String, ASMClass, or Class
     */
    public static String getObjectDescriptor(Object obj)
            throws IllegalArgumentException {
        if(obj instanceof String) {
            return (String)obj;
        } else if(obj instanceof ASMClass) {
            return ((ASMClass) obj).getDescriptor();
        } else if(obj instanceof Class) {
            return Type.getDescriptor((Class<?>)obj);
        } else if(obj instanceof Type) {
            return ((Type) obj).getDescriptor();
        } else {
            throw new IllegalArgumentException("illegal object detected - must be either a String, ASMClass, Class, or Type type");
        }
    }

    /**
     * Generates a descriptor for the given arguments
     * @param returnType return type
     * @param arguments argument list (empty if no args)
     * @return descriptor for method
     */
    public static String generateMethodDescriptor(Object returnType, Object... arguments) {
        String str = "(";
        if(arguments != null && arguments.length > 0) {
            for(Object o : arguments)
                str += getObjectDescriptor(o);
        }
        return str + ")" + getObjectDescriptor(returnType != null ? returnType : void.class);
    }

    /**
     * Corrects the class naming so that it is consistent
     * @param className classes name as string
     * @return corrected format for classpath as Type object
     */
    public static Type getInternalClassType(String className) {
        return Type.getObjectType(className.replace('.', '/'));
    }

    public static boolean isValidOpcode(int opcode) {
        return opcode >= Opcodes.NOP && opcode <= Opcodes.IFNONNULL;
    }

    public static void insertIntoMethodAt(MethodNode methodNode, AbstractInsnNode node, InsnList list) throws NullNodeException {
        methodNode.instructions.insert(node, list);
    }

    public static void insertIntoMethodBefore(MethodNode methodNode, AbstractInsnNode node, InsnList list) throws NullNodeException {
        methodNode.instructions.insertBefore(node, list);
    }

    public static void insertIntoMethod(MethodNode methodNode, AbstractInsnNode node, InsnList list, boolean before) throws NullNodeException {
        if(before) insertIntoMethodBefore(methodNode, node, list);
        else insertIntoMethodAt(methodNode, node, list);
    }
}
