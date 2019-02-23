package dankscape.deobfuscation;
/**
 * Created by Kyle on 1/12/2015.
 */

import dankscape.misc.Searcher;
import java.util.HashMap;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.JumpInsnNode;
import jdk.internal.org.objectweb.asm.tree.LabelNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.List;


public class OpaquePredicateDeobber extends AbstractDeobfuscator {

    
    public OpaquePredicateDeobber(HashMap<String, ClassNode> classList){
        super(classList);
    }
    
    private int run() {
        int nFixed = 0;
        int patterns[][] = new int[][]{
                {Opcodes.ILOAD, Searcher.CONSTPUSH, Searcher.IF, Opcodes.RETURN},
                {Opcodes.ILOAD, Opcodes.LDC, Searcher.IF, Opcodes.RETURN},
        };

        for (ClassNode clazz : classes.values()) {
            List<MethodNode> methodList = clazz.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        LabelNode jmp = ((JumpInsnNode) method.instructions.get(patternIndex + 2)).label;
                        method.instructions.insertBefore(method.instructions.get(patternIndex), new JumpInsnNode(Opcodes.GOTO, jmp));
                        for (int j = 1; j < pattern.length; ++j)
                            method.instructions.remove(method.instructions.get(patternIndex + 1));
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
            }
        }
        return nFixed;
    }

   

    @Override
    public int deobfuscate() {
        int nTotal = 0;
        int nFixed = 10;
        while (nFixed != 0) {
            nFixed = run();
            nTotal = nTotal + nFixed;
        }
        System.out.print("Removed " + nTotal + " Opaque Predicates");
        return nTotal;
    }
}
