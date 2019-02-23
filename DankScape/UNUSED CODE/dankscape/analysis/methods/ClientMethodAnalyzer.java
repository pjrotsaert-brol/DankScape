package dankscape.analysis.methods;

import dankscape.misc.Hook;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;


import java.util.Arrays;
import java.util.List;

/**
 * Created by Kyle on 11/10/2015.
 */
public class ClientMethodAnalyzer extends AbstractMethodAnalyzer {

    public ClientMethodAnalyzer() {
        setId("Client");
        setNeededHooks(Arrays.asList("LoopCycle", "IsMenuOpen", "MenuX", "MenuY", "MenuHeight", "MenuCount", "MenuWidth",
                "MenuActions", "MenuOptions", "LocalPlayers", "Region", "Plane", "DestinationY", "DestinationX", "LocalPlayer", "BaseX",
                "BaseY", "Widgets", "GameSettings", "CurrentLevels", "RealLevels", "Experiences", "Weight", "Energy", "CurrentWorld",
                "WidgetNodeCache", "TileSettings", "TileHeights", "LocalNpcs", "NpcIndices", "CrossHairColor", "MapAngle", "MapOffset", "MapScale",
                "Sine", "Cosine", "CameraScale", "CameraPitch", "CameraYaw", "CameraZ", "CameraY", "CameraX", "ViewportWidth", "ViewportHeight",
                "GroundItems", "LoginState", "PlayerIndex", "WidgetPositionX", "WidgetPositionY", "WidgetWidths", "WidgetHeights"));
    }
    
    @Override
    public void identify() {

        for (ClassNode c : getClasses().values()) {
            if (c.name.equals(getOther("Animable").getName())) {
                List<MethodNode> methodList = c.methods;
                for (MethodNode m : methodList) {
                    if (m.name.equals("<init>")) {
                        AbstractInsnNode[] Instructions = m.instructions.toArray();
                        Searcher search = new Searcher(m);
                        int L = search.find(new int[]{Opcodes.IMUL, Opcodes.LDC, Opcodes.ISUB, Opcodes.PUTFIELD}, 0);
                        L = search.find(new int[]{Opcodes.GETSTATIC}, 0, L - 3);
                        addHook(new Hook("LoopCycle", Instructions, L));
                    }
                }
            }
        }

        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methodList = c.methods;
            for (MethodNode m : methodList) {
                if (m.desc.contains("([L" + getOther("Widget").getName() + ";IIIIII")) {
                    AbstractInsnNode[] Instructions = m.instructions.toArray();
                    Searcher searcher = new Searcher(m);
                    int L = searcher.findSingleFieldDesc(Opcodes.GETSTATIC, "[[L" + getOther("Widget").getName() + ";");
                    //addHook(new Hook("Widgets", Instructions, L));
                    L = searcher.find(new int[]{Opcodes.GETSTATIC, Opcodes.ICONST_0, Opcodes.LDC, Opcodes.AASTORE}, 0);
                    if (L != -1)
                        addHook(new Hook("MenuOptions", Instructions, L));
                    L = searcher.find(new int[]{Opcodes.GETSTATIC, Opcodes.ICONST_0, Opcodes.GETSTATIC, Opcodes.AASTORE}, 0);
                    if (L != -1)
                        addHook(new Hook("MenuActions", Instructions, L));
                }
            }
        }

        int L;
        AbstractInsnNode[] Instructions = null;
        MethodNode method = null;
        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methodList = c.methods;
            for (MethodNode m : methodList) {
                if (!m.desc.contains("(II") || !m.desc.endsWith(")V"))
                    continue;
                Searcher search = new Searcher(m);
                for (int I = 0; I < 100; ++I) {
                    L = search.find(new int[]{Opcodes.IINC}, I);
                    if (L != -1) {
                        Instructions = m.instructions.toArray();
                        if (((IincInsnNode) Instructions[L]).incr == 8 && ((IincInsnNode) Instructions[L]).var == 3) {
                            method = m;
                            break;
                        }
                    } else
                        break;
                }
            }
        }
        
        Searcher search;
        
        if(method != null){
            search = new Searcher(method);
            
            Instructions = method.instructions.toArray();
            L = search.find(new int[]{Opcodes.ICONST_1, Opcodes.PUTSTATIC}, 0);
            if (L != -1)
                addHook(new Hook("IsMenuOpen", Instructions, L + 1));
      
            L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTSTATIC, L, 50, 1);
            addHook(new Hook("MenuX", Instructions, L));

            L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTSTATIC, L, 50, 1);
            addHook(new Hook("MenuY", Instructions, L));

            L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTSTATIC, L, 50, 1);
            addHook(new Hook("MenuWidth", Instructions, L));

            L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTSTATIC, L, 50, 1);
            addHook(new Hook("MenuHeight", Instructions, L));
        }
        
        for (ClassNode Class : getClasses().values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode Method : methodList) {
                if (Method.desc.contains("(Ljava/lang/String;Ljava/lang/String;IIII")) {
                    search = new Searcher(Method);
                    L = search.find(new int[]{Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Opcodes.SIPUSH, Searcher.IF}, 0);
                    if (L != -1) {
                        Instructions = Method.instructions.toArray();
                        addHook(new Hook("MenuCount", Instructions, L));
                        break;
                    }
                }
            }
        }

        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, false, ";)V");
            for (MethodNode m : methods) {
                search = new Searcher(m);
                if (search.findSingleIntValue(Opcodes.SIPUSH, 200) != -1 &&
                        search.findSingleIntValue(Opcodes.BIPUSH, 50) != -1)
                    method = m;//Various, m.ae Rev 98
            }
        }
        
        if(method != null) {
            Instructions = method.instructions.toArray();
            search = new Searcher(method);

            boolean found = false;
            for (int I = 0; I < 3; ++I) {
                L = search.find(new int[]{Opcodes.GETSTATIC, Opcodes.GETSTATIC}, I);
                if (((FieldInsnNode) Instructions[L]).desc.contains("["))
                    addHook(new Hook("LocalPlayers", Instructions, L));
                else {
                    if (!found) {
                        addHook(new Hook("Region", Instructions, L));
                        addHook(new Hook("Plane", Instructions, L + 1));
                        found = true;
                    }
                }
            }

            L = search.find(new int[]{Opcodes.ICONST_0, Opcodes.PUTSTATIC}, 0);
            int S = L;
            for (int I = 0; I < 2; ++I) {
                L = search.find(new int[]{Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Opcodes.GETSTATIC,
                        Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL, Opcodes.BIPUSH, Opcodes.ISHR}, I);
                if (((FieldInsnNode) Instructions[L]).name.equals(((FieldInsnNode) Instructions[S + 1]).name))
                    continue;
                else
                    addHook(new Hook("DestinationY", Instructions, L));
                addHook(new Hook("DestinationX", Instructions, S + 1));

            }

            L = search.findSingleFieldDesc(Opcodes.GETSTATIC, "L" + getOther("Player").getName() + ";", 0);
            addHook(new Hook("LocalPlayer", Instructions, L));
        
        }
        else
        {
            addHook(new Hook("LocalPlayers", null, -1));
            addHook(new Hook("Region", null, -1));
            addHook(new Hook("Plane", null, -1));
            addHook(new Hook("DestinationX", null, -1));
            addHook(new Hook("DestinationY", null, -1));
        }
        
        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, false, "()V");
            for (MethodNode m : methods) {
                if (m.name.equals("<clinit>")) {
                    search = new Searcher(m);
                    if (search.findSingleIntValue(Opcodes.SIPUSH, 2000) != -1 &&
                            !getOther("Model").getName().equals(c.name))
                        method = m;
                }
            }
        }

        for (ClassNode c : getClasses().values()) {
            List<FieldNode> fields = c.fields;
            for (FieldNode f : fields) {
                if (f.desc.contains("[[L" + getOther("Widget").getName() + ";")) {
                    System.out.println(c.name);
                    addHook(new Hook("Widgets", f));
                }
            }

        }

        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = c.methods;
            for (MethodNode m : methods) {
                search = new Searcher(m);
                Instructions = m.instructions.toArray();
                L = search.findSingleFieldDesc(Opcodes.GETSTATIC, "L" + getOther("HashTable").getName() + ";");
                if (L != -1) {
                    System.out.println(((FieldInsnNode) Instructions[L]).owner + "." + ((FieldInsnNode) Instructions[L]).name);
                }

            }
        }

        search = new Searcher(method);
        Instructions = method.instructions.toArray();
        L = search.findSingleIntValue(Opcodes.SIPUSH, 2000);
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTSTATIC, L, 15, 0);
        addHook(new Hook("GameSettings", Instructions, L));

        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, false, "(");
            for (MethodNode m : methods) {
                search = new Searcher(m);
                if (search.findSingleIntValue(Opcodes.SIPUSH, 3308) != -1 &&
                        search.findSingleIntValue(Opcodes.SIPUSH, 3305) != -1) {
                    method = m;//am.f rev 138
                }
            }
        }
        search = new Searcher(method);
        Instructions = method.instructions.toArray();
        L = search.findSingleIntValue(Opcodes.SIPUSH, 3308);
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 15, 1);
        addHook(new Hook("BaseX", Instructions, L));

        L = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 15, 2);
        addHook(new Hook("BaseY", Instructions, L));

        L = search.findSingleIntValue(Opcodes.SIPUSH, 3305);
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 50, 4);
        addHook(new Hook("CurrentLevels", Instructions, L));

        L = search.findSingleIntValue(Opcodes.SIPUSH, 3306);
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 50, 4);
        addHook(new Hook("RealLevels", Instructions, L));

        L = search.findSingleIntValue(Opcodes.SIPUSH, 3307);
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L,  50, 4);
        addHook(new Hook("Experiences", Instructions, L));

        L = search.findSingleIntValue(Opcodes.SIPUSH, 3322);
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 30, 2);
        addHook(new Hook("Weight", Instructions, L));

        L = search.findSingleIntValue(Opcodes.SIPUSH, 3321);
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 30, 2);
        addHook(new Hook("Energy", Instructions, L));

        L = search.findSingleIntValue(Opcodes.SIPUSH, 3318);
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 30, 2);
        addHook(new Hook("CurrentWorld", Instructions, L));


        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, false, "(");
            for (MethodNode m : methods) {
                search = new Searcher(m);
                if (search.findSingleIntValue(Opcodes.SIPUSH, 2702) != -1 &&
                        search.findSingleIntValue(Opcodes.SIPUSH, 2701) != -1) {
                    method = m;//z.c rev 138
                }
            }
        }
        search = new Searcher(method);
        Instructions = method.instructions.toArray();
        L = search.findSingleIntValue(Opcodes.SIPUSH, 2702);
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 30, 2);
        addHook(new Hook("WidgetNodeCache", Instructions, L));

        out:
        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, false, ")V");
            for (MethodNode m : methods) {
                search = new Searcher(m);
                Instructions = m.instructions.toArray();
                L = search.find(new int[]{Opcodes.GETSTATIC, Opcodes.ICONST_1,
                        Opcodes.AALOAD}, 0);
                if (L != -1 && ((FieldInsnNode) Instructions[L]).desc.equals("[[[B")) {
                    addHook(new Hook("TileSettings", Instructions, L)); //TODO FIX TEH BROKE
                    method = m;
                    break out;
                }
            }
        }

        out:
        for (ClassNode Class : getClasses().values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode Method : methodList) {
                Instructions = Method.instructions.toArray();
                search = new Searcher(Method);
                L = search.find(new int[]{Opcodes.GETSTATIC, Opcodes.ILOAD, Opcodes.AALOAD, Opcodes.ASTORE}, 0);
                if (L != -1) {
                    if (((FieldInsnNode) Instructions[L]).desc.equals("[[[I")) {
                        addHook(new Hook("TileHeights", Instructions, L));
                        break out;
                    }
                }
            }
        }

        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, true, "(Z)V");
            for (MethodNode m : methods) {
                search = new Searcher(m);
                if (search.findSingleIntValue(Opcodes.BIPUSH, 104) != -1 &&
                        search.findSingleIntValue(Opcodes.BIPUSH, 127) != -1 && m.instructions.size() < 500)
                    method = m;// k.ai Rev 98
            }
        }

        search = new Searcher(method);
        Instructions = method.instructions.toArray();
        L = search.findSingleFieldDesc(Opcodes.GETSTATIC, "[L" + getOther("NPC").getName() + ";");
        addHook(new Hook("LocalNpcs", Instructions, L));
        L = search.findSingleFieldDesc(Opcodes.GETSTATIC, "[I", L);
        addHook(new Hook("NpcIndices", Instructions, L));

        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, true, "(IIIILjava/lang/String;Ljava/lang/String;II)V");
            for (MethodNode m : methods) {
                search = new Searcher(m);
                Instructions = m.instructions.toArray();
                for (int I = 0; L != -1; ++I) {
                    L = search.find(new int[]{Opcodes.ILOAD, Opcodes.BIPUSH, Opcodes.IF_ICMPNE}, I);
                    if (L != -1 && ((IntInsnNode) Instructions[L + 1]).operand == 20) {
                        int S = search.findSingleJump(Opcodes.GOTO, Opcodes.PUTSTATIC, L, 50, 2);
                        addHook(new Hook("CrossHairColor", Instructions, S));
                        L = -1;
                    }
                }
            }
        }


        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, false, "([Lfd;IIIIIIII)V");
            for (MethodNode m : methods) {
                method = m;
            }
        }
        search = new Searcher(method);
        Instructions = method.instructions.toArray();

        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, false, "(IIIIL");
            for (MethodNode m : methods) {
                search = new Searcher(m);
                if (search.findSingleIntValue(Opcodes.SIPUSH, 2047) != -1 &&
                        search.findSingleIntValue(Opcodes.SIPUSH, 2500) != -1) {
                    method = m;// a.dl Rev 99, TileToMM
                }
            }
        }
        search = new Searcher(method);
        Instructions = method.instructions.toArray();
        L = search.findSingleJump(Opcodes.IFNONNULL, Opcodes.GETSTATIC, 0, 25, 0);
        addHook(new Hook("MapOffset", Instructions, L));
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 15, 1);
        addHook(new Hook("MapAngle", Instructions, L));
        L = search.findSingleIntValue(Opcodes.SIPUSH, 256);
        L = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 15, 0);
        addHook(new Hook("MapScale", Instructions, L));


        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, false, "(III");
            for (MethodNode m : methods) {
                search = new Searcher(m);
                if (search.findSingleIntValue(Opcodes.SIPUSH, 13056) != -1 &&
                        search.findSingleIntValue(Opcodes.SIPUSH, 128) != -1) {
                    method = m;// ClientAnalyzer.fg Rev 99, TileToMS
                }
            }
        }
        search = new Searcher(method);
        Instructions = method.instructions.toArray();
        for (int I = 0; I < 10; ++I) {
            L = search.find(new int[]{Opcodes.GETSTATIC, Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL,
                    Opcodes.IALOAD, Opcodes.ISTORE}, I);
            if (L != -1 && ((VarInsnNode) Instructions[L + 5]).var == 5) {
                addHook(new Hook("CameraPitch", Instructions, L + 1));
                addHook(new Hook("Sine", Instructions, L));
            }
            if (L != -1 && ((VarInsnNode) Instructions[L + 5]).var == 7)
                addHook(new Hook("CameraYaw", Instructions, L + 1));
            if (L != -1 && ((VarInsnNode) Instructions[L + 5]).var == 6)
                addHook(new Hook("Cosine", Instructions, L));
        }
        for (int I = 0; I < 10; ++I) {
            L = search.find(new int[]{Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Opcodes.ISUB, Opcodes.ISTORE}, I);
            if (L != -1 && ((VarInsnNode) Instructions[L + 4]).var == 0)
                addHook(new Hook("CameraX", Instructions, L));
            if (L != -1 && ((VarInsnNode) Instructions[L + 4]).var == 1)
                addHook(new Hook("CameraY", Instructions, L));
            if (L != -1 && ((VarInsnNode) Instructions[L + 4]).var == 4)
                addHook(new Hook("CameraZ", Instructions, L));
        }
        for (int I = 0; I < 10; ++I) {
            L = search.find(
                    new int[]{
                            Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Opcodes.ICONST_2, Opcodes.IDIV, Opcodes.ILOAD,
                            Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Opcodes.IMUL, Opcodes.ILOAD, Opcodes.IDIV,
                            Opcodes.IADD, Opcodes.LDC, Opcodes.IMUL, Opcodes.PUTSTATIC
                    }, I);
            if (L != -1) {
                addHook(new Hook("ViewportWidth", Instructions, L));
                addHook(new Hook("CameraScale", Instructions, L + 6));
                break;
            }
        }
        for (int I = 0; I < 10; ++I) {
            L = search.find(
                    new int[]{
                            Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Opcodes.ILOAD, Opcodes.IMUL, Opcodes.ILOAD,
                            Opcodes.IDIV, Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Opcodes.ICONST_2, Opcodes.IDIV,
                            Opcodes.IADD, Opcodes.IMUL, Opcodes.PUTSTATIC
                    }, I);
            if (L != -1) {
                Hook scale = getHook("CameraScale");
                FieldInsnNode potential = (FieldInsnNode) Instructions[L];
                if (scale != null && potential.name.equals(scale.getName()) && potential.owner.equals(scale.getOwner())) {
                    addHook(new Hook("ViewportHeight", Instructions, L + 7));
                    break;
                }
            }
        }

        for (ClassNode c : getClasses().values()) {
            if (getFields(c, "[[[L" + getOther("LinkedList").getName() + ";").size() != 0) {
                Hook temp = new Hook("GroundItems", getFields(c, "[[[L" + getOther("LinkedList").getName() + ";").get(0));
                temp.setOwner(c.name);
                addHook(temp);
            }
        }

        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, true, "(L" + getOther("GameShell").getName() + ";)V");
            for (MethodNode m : methods) {
                search = new Searcher(m);
                if (search.findSingleIntValue(Opcodes.BIPUSH, 75) != -1) {
                    L = 0;
                    for (int I = 0; L != -1; ++I) {
                        search = new Searcher(m); // y.f Rev 98
                        Instructions = m.instructions.toArray();
                        L = search.find(new int[]{Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Opcodes.BIPUSH, Searcher.IF}, I);
                        if (((IntInsnNode) Instructions[L + 3]).operand == 11) {
                            addHook(new Hook("LoginState", Instructions, L));
                            L = -1;
                        }
                    }
                }
            }
        }

        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, false, "(L");
            for (MethodNode m : methods) {
                search = new Searcher(m);
                if (search.findSingleIntValue(Opcodes.SIPUSH, 2048) != -1 &&
                        search.findSingleIntValue(Opcodes.BIPUSH, 28) != -1) {
                    search = new Searcher(m); // j.z Rev 98
                    Instructions = m.instructions.toArray();
                    L = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, 0, 10, 0);
                    if (L != -1) //TODO Fix
                        addHook(new Hook("PlayerIndex", Instructions, L));
                }
            }
        }

        for (ClassNode c : getClasses().values()) {
            MethodNode method1 = getMethod(c, true, "([L" + getOther("Widget").getName() + ";IIIIIIII)V");
            if (method1 != null)
                method = method1;
        }
        search = new Searcher(method);
        Instructions = method.instructions.toArray();
        for (int I = 0; I < 10; ++I) {
            L = search.find(new int[]{Opcodes.ILOAD, Opcodes.ICONST_M1, Opcodes.IF_ICMPNE}, I);
            if (L != -1) {
                if (((VarInsnNode) Instructions[L]).var == 8) {
                    int S = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 50, 0);
                    if (S != -1)
                        addHook(new Hook("WidgetPositionX", Instructions, S));
                    S = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 50, 2);
                    if (S != -1)
                        addHook(new Hook("WidgetPositionY", Instructions, S));
                    S = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 50, 4);
                    if (S != -1)
                        addHook(new Hook("WidgetWidths", Instructions, S));
                    S = search.findSingleJump(Opcodes.GOTO, Opcodes.GETSTATIC, L, 50, 6);
                    if (S != -1)
                        addHook(new Hook("WidgetHeights", Instructions, S));
                }
            }
        }
    }

}