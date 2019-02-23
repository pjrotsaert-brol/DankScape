package dankscape.analysis.methods;

import dankscape.misc.Hook;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

import java.util.Arrays;
import java.util.List;
import jdk.internal.org.objectweb.asm.tree.ClassNode;


/**
 * Created by Kyle on 11/16/2015.
 */
public class ItemMethodAnalyzer extends AbstractMethodAnalyzer {

    public ItemMethodAnalyzer() {
        setId("Item");
        setNeededHooks(Arrays.asList("ID", "StackSizes"));
    }

    
    @Override
    public void identify() {
        MethodNode method = null;
        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, true, "(II)V");
            for (MethodNode m : methods) {
                Searcher search = new Searcher(m);
                if (search.findSingleLdcValue(Opcodes.LDC, (long) -99999999) != -1)
                    method = m;

            }
        }
        AbstractInsnNode[] Instructions = method.instructions.toArray();
        Searcher search = new Searcher(method);
        Hook temp1 = new Hook();
        Hook temp2 = new Hook();
        boolean run = false;
        int L = 0;
        for (int I = 0; L != -1; ++I) {
            L = search.find(new int[]{Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL}, I);
            if (L != -1 && ((VarInsnNode) Instructions[L]).var == 7) {
                if (run && !((FieldInsnNode) Instructions[L + 1]).name.equals(temp1.getName()))
                    temp2 = new Hook("ID", Instructions, L + 1);
                else
                    temp1 = new Hook("StackSizes", Instructions, L + 1);
                run = true;
            }

        }
        addHook(temp1);
        addHook(temp2);


    }

}
