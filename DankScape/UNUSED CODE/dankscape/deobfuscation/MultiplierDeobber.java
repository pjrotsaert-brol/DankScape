package dankscape.deobfuscation;
/**
 * Created by Kyle on 1/12/2015.
 */

import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MultiplierDeobber extends AbstractDeobfuscator {

    public MultiplierDeobber(HashMap<String, ClassNode> classList){
        super(classList);
    }

    private int run() {
        int[][] patterns = new int[][]{
                {Opcodes.LDC, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.IMUL},
                {Opcodes.LDC, Opcodes.GETSTATIC, Opcodes.IMUL},
                {Opcodes.LDC, Opcodes.GETFIELD, Opcodes.IMUL},
                {Opcodes.LDC, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LMUL},
                {Opcodes.LDC, Opcodes.GETSTATIC, Opcodes.LMUL},
                {Opcodes.LDC, Opcodes.GETFIELD, Opcodes.LMUL}};
        int nFixed = 0;
        for (ClassNode clazz : classes.values()) {
            List<MethodNode> methodList = clazz.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));

                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        int afterField;
                        if (method.instructions.get(patternIndex + 1) instanceof FieldInsnNode) {
                            afterField = 2;
                        } else {
                            afterField = 3;
                        }
                        instructions.add(patternIndex + afterField, instructions.get(patternIndex));
                        instructions.remove(patternIndex);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }

                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
            }
        }
        return nFixed;
    }

    @Override
    public int deobfuscate() {
        int nTotal = 0;
        
        int nFixed = -1;
        while (nFixed != 0) 
        {
            nFixed = run();
            nTotal = nTotal + nFixed;
        }
        
        System.out.print("Reordered " + nTotal + " Multiplier Instructions");
        return nTotal;
    }

}
