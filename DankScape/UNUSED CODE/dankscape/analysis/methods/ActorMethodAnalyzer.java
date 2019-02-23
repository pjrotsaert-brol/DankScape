package dankscape.analysis.methods;

import dankscape.analysis.Analysis;
import dankscape.misc.ClassNodeWrapper;
import dankscape.misc.Hook;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.List;


/**
 * Created by Kyle on 11/5/2015.
 */
public class ActorMethodAnalyzer extends AbstractMethodAnalyzer {
    
    public ActorMethodAnalyzer() {
        setId("Actor");
    }
    
    @Override
    public void identify() {
        this.setNeededHooks(Arrays.asList("WorldX", "WorldY", "QueueX", "QueueY", "QueueSize",
                "Animation", "SpokenText", "CombatCycle", "Health", "MaxHealth", "InteractingIndex"));

        MethodNode method;
        Searcher search;
        AbstractInsnNode[] Instructions = null;


//  Causes duplicates
//        for (ClassNodeWrapper c : CLASSES.values()) {
//            method = c.getMethod(true, "(L" + classes.myActor.getName() + ";)V");
//            if (method != null) {
//                Instructions = method.instructions.toArray();
//                search = new Searcher(method);
//                int L = search.find(new int[]{Opcodes.GETFIELD}, 0);
//                addHook(new Hook("WorldX", Instructions, L));
//                L = search.find(new int[]{Opcodes.GETFIELD}, 1);
//                addHook(new Hook("WorldY", Instructions, L));
//            }
//        }

        int L;
        out:
        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, true, "(L" + getParent().getName() + ";)V");
            for (MethodNode m : methods) {
                if (m != null) {
                    Instructions = m.instructions.toArray();
                    search = new Searcher(m);
                    for (int I = 0; I < 1000; ++I) {
                        L = search.find(new int[]{Opcodes.IINC}, I);
                        if (L != -1)
                            if ((((IincInsnNode) Instructions[L]).incr) == -2048) {
                                L = search.find(new int[]{Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL,
                                        Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.ALOAD, Opcodes.GETFIELD,
                                        Opcodes.LDC, Opcodes.IMUL, Opcodes.ICONST_1}, 0);
                                addHook(new Hook("QueueX", Instructions, L + 4));
                                L = search.find(new int[]{Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL,
                                        Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.ALOAD, Opcodes.GETFIELD,
                                        Opcodes.LDC, Opcodes.IMUL, Opcodes.ICONST_1}, 1);
                                addHook(new Hook("QueueY", Instructions, L + 4));
                                addHook(new Hook("QueueSize", Instructions, L + 6));
                                break out;
                            }
                    }
                }
            }
        }

        if (containsHook("QueueX")) {
            Hook queueHook = getHook("QueueX");
            for (ClassNode c : getClasses().values()) {
                List<MethodNode> methods = getMethods(c, true, "(L" + getParent().getName() + ";)V");
                for (MethodNode m : methods) {
                    if (m != null && (m.access & Opcodes.ACC_STATIC) != 0) {
                        Instructions = m.instructions.toArray();
                        search = new Searcher(m);
                        for (int I = 0; I < 1000; ++I) {
                            L = search.find(new int[]{Opcodes.GETFIELD}, I);
                            if (L != -1) {
                                FieldInsnNode queueNode = (FieldInsnNode) Instructions[L];
                                if (queueNode != null && queueNode.name.equals(queueHook.getName())
                                        && queueNode.owner.equals(queueHook.getOwner())) {
                                    int H = search.find(new int[]{Opcodes.PUTFIELD}, 0, L, L + 10);
                                    if (H != -1) {
                                        FieldInsnNode xNode = (FieldInsnNode) Instructions[H];
                                        if (xNode != null && xNode.owner.equals(queueHook.getOwner())
                                                && xNode.desc.equals("I")) {
                                            addHook(new Hook("WorldX", Instructions, H));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (containsHook("QueueY")) {
            Hook queueHook = getHook("QueueY");
            for (ClassNode c : getClasses().values()) {
                List<MethodNode> methods = getMethods(c, true, "(L" + getParent().getName() + ";)V");
                for (MethodNode m : methods) {
                if (m != null && (m.access & Opcodes.ACC_STATIC) != 0) {
                    Instructions = m.instructions.toArray();
                    search = new Searcher(m);
                    for (int I = 0; I < 1000; ++I) {
                        L = search.find(new int[]{Opcodes.GETFIELD}, I);
                        if (L != -1) {
                            FieldInsnNode queueNode = (FieldInsnNode) Instructions[L];
                            if (queueNode != null && queueNode.name.equals(queueHook.getName())
                                    && queueNode.owner.equals(queueHook.getOwner())) {
                                int H = search.find(new int[]{Opcodes.PUTFIELD}, 0, L, L + 10);
                                if (H != -1) {
                                    FieldInsnNode yNode = (FieldInsnNode) Instructions[H];
                                    if (yNode != null && yNode.owner.equals(queueHook.getOwner())
                                            && yNode.desc.equals("I")) {
                                        addHook(new Hook("WorldY", Instructions, H));
                                    }
                                }
                            }
                        }
                    }
                }
                }
            }
        }

        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methodList = c.methods;
            for (MethodNode m : methodList) {
                search = new Searcher(m);
                L = search.findSingleIntValue(Opcodes.SIPUSH, 13184);
                if (L != -1) {
                    Instructions = m.instructions.toArray();
                    L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTFIELD, L, 25, 0);
                    addHook(new Hook("Animation", Instructions, L));
                }
            }
        }


/* Depreciated due to gamepack update
        L = 0;
        boolean fail = false;
        for (ClassNodeWrapper c : CLASSES.values()) {
            method = c.getMethod(true, "(L" + classes.myActor.getName() + ";III)V");
            if (method != null) {
                Instructions = method.instructions.toArray();
                search = new Searcher(method);
                for (int I = 0; L != -1; ++I) {
                    L = search.find(new int[]{Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.IFNULL}, I);//ifnull or ACONST_NULL, SEARCHER.IF
                    if (L == -1) {
                        if (!fail) {
                            I = 0;
                            fail = true;
                        }
                        L = search.find(new int[]{Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.ACONST_NULL, Searcher.IF}, I);
                    }
                    if (Instructions[L + 1] instanceof FieldInsnNode && ((FieldInsnNode) Instructions[L + 1]).desc.equals("Ljava/lang/String;")) {
                        addHook(new Hook("SpokenText", Instructions, L + 1));

                        L = search.find(new int[]{Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL,
                                Opcodes.GETSTATIC, Opcodes.LDC}, 0);
                        addHook(new Hook("CombatCycle", Instructions, L + 1));

                        L = search.find(new int[]{Opcodes.ILOAD, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC,
                                Opcodes.IMUL, Opcodes.IMUL}, 0, 0);
                        addHook(new Hook("Health", Instructions, L + 2));

                        L = search.find(new int[]{Opcodes.ALOAD, Opcodes.GETFIELD}, 0, L + 5);
                        addHook(new Hook("MaxHealth", Instructions, L + 1));

                        L = -1;
                    }
                }
            }
        }
*/
        addHook(new Hook("SpokenText", Instructions, -1));
        addHook(new Hook("CombatCycle", Instructions, -1));
        addHook(new Hook("Health", Instructions, -1));
        addHook(new Hook("MaxHealth", Instructions, -1));

        for (ClassNode c : getClasses().values()) {
            L = 0;
            List<MethodNode> methods = getMethods(c, true, "(L" + getParent().getName() + ";)V");
            for (MethodNode m : methods) {
            if (m != null) {
                Instructions = m.instructions.toArray();
                search = new Searcher(m);
                for (int I = 0; L != -1; ++I) {
                    L = search.find(new int[]{Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL, Opcodes.LDC}, I);
                    if (L != -1)
                        if ((((LdcInsnNode) Instructions[L + 3]).cst.equals(32768))) {
                            addHook(new Hook("InteractingIndex", Instructions, L));
                            L = -1;
                        }
                }
                }
            }
        }
    }
}

