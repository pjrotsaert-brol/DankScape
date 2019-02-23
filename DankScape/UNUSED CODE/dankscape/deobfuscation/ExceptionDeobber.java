package dankscape.deobfuscation;
/**
 * Created by Kyle on 1/12/2015.
 */

import dankscape.misc.Searcher;
import java.util.HashMap;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.List;


public class ExceptionDeobber extends AbstractDeobfuscator {

    public int run() {
        int Patterns[][] = new int[][]{
                {Opcodes.ILOAD, Opcodes.LDC, Searcher.IF, Opcodes.NEW, Opcodes.DUP, Opcodes.INVOKESPECIAL, Opcodes.ATHROW},
                {Opcodes.ILOAD, Searcher.CONSTPUSH, Searcher.IF, Opcodes.NEW, Opcodes.DUP, Opcodes.INVOKESPECIAL, Opcodes.ATHROW},
                {Opcodes.ILOAD, Opcodes.ICONST_0, Opcodes.IF_ICMPEQ, Opcodes.NEW, Opcodes.DUP, Opcodes.INVOKESPECIAL, Opcodes.ATHROW},
                {Opcodes.ILOAD, Opcodes.ICONST_M1, Opcodes.IF_ICMPNE, Opcodes.NEW, Opcodes.DUP, Opcodes.INVOKESPECIAL, Opcodes.ATHROW},
                {Opcodes.ILOAD, Opcodes.ICONST_0, Opcodes.IF_ICMPGT, Opcodes.NEW, Opcodes.DUP, Opcodes.INVOKESPECIAL, Opcodes.ATHROW},
        };
        int nFixed = 0;
        for (ClassNode clazz : classes.values()) {
            List<MethodNode> methodList = clazz.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                for (int[] pattern : Patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    int check = 0;
                    out:
                    while (patternIndex != -1) {
                        ++check;
                        if (check > 100)
                            break out;
                        if (method.instructions.get(patternIndex + 5) instanceof MethodInsnNode) {
                            LabelNode jmp = ((JumpInsnNode) method.instructions.get(patternIndex + 2)).label;
                            method.instructions.insertBefore(method.instructions.get(patternIndex), new JumpInsnNode(Opcodes.GOTO, jmp));
                            for (int j = 0; j < pattern.length; ++j)
                                method.instructions.remove(method.instructions.get(patternIndex + 1));
                            ++count;
                            ++nFixed;
                            patternIndex = searcher.find(pattern, count);
                        }
                    }
                }
            }
        }

        return nFixed;
    }

    public ExceptionDeobber(HashMap<String, ClassNode> classList)
    {
        super(classList);
    }

    @Override
    public int deobfuscate() {
        int nTotal = 0;
        int nFixed = 10;
        while (nFixed != 0) {
            nFixed = run();
            nTotal = nTotal + nFixed;
        }
        System.out.print("Removed " + nTotal + " Exceptions");
        return nTotal;
    }
}
