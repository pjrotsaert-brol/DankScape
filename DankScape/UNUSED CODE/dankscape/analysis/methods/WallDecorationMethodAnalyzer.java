package dankscape.analysis.methods;

import dankscape.misc.ClassNodeWrapper;
import dankscape.misc.Hook;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 11/16/2015.
 */
public class WallDecorationMethodAnalyzer extends AbstractMethodAnalyzer {

    public WallDecorationMethodAnalyzer() {
        setId("WallDecoration");
        setNeededHooks(Arrays.asList("ID", "Flags", "LocalX", "LocalY", "Plane", "Renderable",
                "Renderable2", "Orientation", "Height", "RelativeX", "RelativeY"));
    }
    
    @Override
    public void identify() {
        MethodNode method = null;
        for (ClassNode c : getClasses().values()) {
            method = getMethod(c, true, "(IIIIL" + getOther("Renderable").getName() +
                    ";L" + getOther("Renderable").getName() + ";IIIIII)V");
            if (method != null)
                break;
        }
        Searcher search = new Searcher(method);
        AbstractInsnNode[] Instructions = method.instructions.toArray();
        int L = 0;
        L = search.find(new int[]{Opcodes.NEW, Opcodes.DUP, Opcodes.INVOKESPECIAL}, 0);
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 0);
        addHook(new Hook("ID", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("Flags", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("LocalX", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("LocalY", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("Plane", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("Renderable", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("Renderable2", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("Orientation", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("Height", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("RelativeX", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 50, 1);
        addHook(new Hook("RelativeY", Instructions, L));
    }

}
