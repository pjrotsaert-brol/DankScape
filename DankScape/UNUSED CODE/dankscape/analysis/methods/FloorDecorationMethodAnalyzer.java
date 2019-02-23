package dankscape.analysis.methods;

import dankscape.misc.ClassNodeWrapper;
import dankscape.misc.Hook;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

import java.util.Arrays;
import jdk.internal.org.objectweb.asm.tree.ClassNode;


/**
 * Created by Kyle on 11/16/2015.
 */
public class FloorDecorationMethodAnalyzer extends AbstractMethodAnalyzer {

    public FloorDecorationMethodAnalyzer() {
        setId("GameObject");
        setNeededHooks(Arrays.asList("ID", "Flags", "Plane", "Render", "LocalX", "LocalY"));
    }

    
    public void identify() {
        MethodNode method = null;
        for (ClassNode c : getClasses().values()) {
            method = getMethod(c, true, "(IIIIL" + getOther("Renderable").getName() + ";II)V");
            if (method != null)
                break;
        }
        Searcher search = new Searcher(method);
        AbstractInsnNode[] Instructions = method.instructions.toArray();
        int L = 0;
        int S = 0;
        for (int I = 0; L != -1; ++I) {
            L = search.find(new int[]{Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.PUTFIELD}, I);
            if (((VarInsnNode) Instructions[L]).var == 8 && ((VarInsnNode) Instructions[L + 1]).var == 5) {
                S = L;
                L = -1;
            }

        }
        addHook(new Hook("Render", Instructions, S + 2));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, S, 50, 1);
        addHook(new Hook("LocalX", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("LocalY", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("Plane", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("ID", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("Flags", Instructions, L));
    }
}
