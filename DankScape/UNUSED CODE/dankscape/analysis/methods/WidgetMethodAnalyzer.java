package dankscape.analysis.methods;

import dankscape.misc.ClassNodeWrapper;
import dankscape.misc.Hook;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.IntInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.List;
import jdk.internal.org.objectweb.asm.tree.ClassNode;


/**
 * Created by Kyle on 11/9/2015.
 */
public class WidgetMethodAnalyzer extends AbstractMethodAnalyzer {

    public WidgetMethodAnalyzer() {
        setId("Widget");
        setNeededHooks(Arrays.asList("Children", "ItemID", "ItemAmount", "WidgetID", "Name", "Text", "IsHidden",
                "AbsoluteY", "AbsoluteX", "RelativeX", "RelativeY", "Width", "Height", "ParentID", "ScrollY", "ScrollX",
                "InvIDs", "BoundsIndex", "StackSizes", "TextureID", "Parent"));
    }

    
    @Override
    public void identify() {

        addHook(new Hook("Children", getFields(getClassNode(), "[L" + getOther("Widget").getName() + ";").get(0)));

        MethodNode method = null;
        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methodList = c.methods;
            for (MethodNode m : methodList) {
                if (c.name.equals(getOther("Widget").getName()) && m.name.equals("<init>"))
                    method = m;
            }
        }

        AbstractInsnNode[] Instructions = method.instructions.toArray();
        int L;
        Searcher search = new Searcher(method);
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, 0, 10, 1);
        addHook(new Hook("WidgetID", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 11);
        addHook(new Hook("AbsoluteX", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 0);
        addHook(new Hook("AbsoluteY", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 0);
        addHook(new Hook("Width", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 0);
        addHook(new Hook("Height", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 2);
        addHook(new Hook("ParentID", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 0);
        addHook(new Hook("IsHidden", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 0);
        addHook(new Hook("RelativeX", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 0);
        addHook(new Hook("RelativeY", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 12);
        addHook(new Hook("TextureID", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 150, 22);
        addHook(new Hook("Text", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 8);
        addHook(new Hook("Name", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 0);
        addHook(new Hook("Parent", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 8);
        addHook(new Hook("ItemID", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 0);
        addHook(new Hook("ItemAmount", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L + 1, 50, 8);
        addHook(new Hook("BoundsIndex", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, 0, 200, 21);
        addHook(new Hook("ScrollX", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, 0, 200, 22);
        addHook(new Hook("ScrollY", Instructions, L));

        for (ClassNode c : getClasses().values()) {
            method = getMethod(c, true, "([L" + getOther("Widget").getName() + ";IIIIIIII)V");
            if (method != null) {
                search = new Searcher(method);
                Instructions = method.instructions.toArray();
                break;
            }
        }

        L = search.find(new int[]{Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.ILOAD, Opcodes.IALOAD,
                Opcodes.ICONST_1, Opcodes.ISUB}, 0);
        if (L != -1)
            addHook(new Hook("InvIDs", Instructions, L + 1));

        for (int I = 0; L != -1; ++I) {
            L = search.find(new int[]{Opcodes.GETSTATIC, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC,
                    Opcodes.IMUL, Opcodes.ICONST_1}, I);

            if (((FieldInsnNode) Instructions[L]).desc.equals("[Z")) {
                addHook(new Hook("BoundsIndex", Instructions, L + 2));
                L = -1;
            }
        }

        List<MethodNode> methodList = getClassNode().methods;
        for (MethodNode Method : methodList) {
            if (!Method.desc.contains("(II") || !Method.desc.contains("V"))
                continue;
            Searcher Search = new Searcher(Method);
            for (int I = 0; I < 10; ++I) {
                L = Search.findSingle(Opcodes.GETFIELD, I);
                if (L != -1) {
                    AbstractInsnNode[] instructions = Method.instructions.toArray();
                    if (!((FieldInsnNode) instructions[L]).name.contains(getOther("Widget").getMethodAnalyzer().getHook("InvIDs").getName())) {
                        addHook(new Hook("StackSizes", instructions, L));
                        break;
                    }
                }
            }
        }


    }
}

