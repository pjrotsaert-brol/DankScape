package dankscape.deobfuscation;
/**
 * Created by Kyle on 1/12/2015.
 */

import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MethodNameDeobber extends AbstractDeobfuscator {
    
    private ArrayList<MethodInfo> dummyParamMethods = new ArrayList();
    
    public MethodNameDeobber(HashMap<String, ClassNode> classList){
        super(classList);
    }
    
    private static void removeLastParam(MethodNode Method) {
        String signature = Method.desc;
        StringBuilder descBuilder = new StringBuilder(signature);
        int Index = signature.indexOf(")");
        String c = Character.toString(descBuilder.charAt(Index - 1));
        if (!c.equals(";")) {
            descBuilder.deleteCharAt(Index - 1);
            Method.desc = descBuilder.toString();
        }
    }

    private static void removeLastParam(MethodInsnNode Method) {
        String signature = Method.desc;
        StringBuilder descBuilder = new StringBuilder(signature);
        int Index = signature.indexOf(")");
        descBuilder.deleteCharAt(Index - 1);
        Method.desc = descBuilder.toString();
    }

    

    private int run() {
        int fixedParams = 0;
        for (ClassNode classNode : classes.values()) {
            List<MethodNode> methodList = classNode.methods;
            for (MethodNode method : methodList) {
                if (method.access != Opcodes.ACC_ABSTRACT) {
                    if (method.name.contains("<"))
                        continue;
                    int paramCount;
                    boolean hasDummy = true;
                    Type[] types = Type.getArgumentTypes(method.desc);
                    paramCount = types.length;
                    String lastParam;
                    if (paramCount > 1) {
                        lastParam = types[paramCount - 2].toString();
                    } else if (paramCount == 1)
                        lastParam = types[paramCount - 1].toString();
                    else
                        continue;
                    if (Modifier.isStatic(method.access))
                        continue;
                    if (lastParam.equals("B") || lastParam.equals("I") || lastParam.equals("S") || lastParam.equals("Z")) {
                        Searcher searcher = new Searcher(method);
                        int L = searcher.findMultiPatterns(new int[][]{{Opcodes.ALOAD}, {Opcodes.ILOAD}}, 0);
                        if (L == -1)
                            hasDummy = false;
                        for (int I = 0; I < method.instructions.size(); ++I) {
                            if (method.instructions.get(I) instanceof VarInsnNode) {
                                if (((VarInsnNode) (method.instructions.get(I))).var == paramCount) {
                                    hasDummy = false;
                                }
                            }
                        }
                        if (hasDummy) {
                            dummyParamMethods.add(new MethodInfo(classNode.name, method.name, method.desc));
                            removeLastParam(method);
                            ++fixedParams;
                        }
                    }
                }
            }
        }

        for (ClassNode classNode : classes.values()) {
            List<MethodNode> methodList = classNode.methods;
            for (MethodNode method : methodList) {
                if (method.access != Opcodes.ACC_ABSTRACT) {
                    int paramCount;
                    boolean hasDummy = true;
                    if (method.name.contains("<"))
                        continue;
                    Type[] types = Type.getArgumentTypes(method.desc);
                    paramCount = types.length;
                    if (paramCount > 0) {
                        String lastParam = types[paramCount - 1].toString();
                        if (!Modifier.isStatic(method.access))
                            continue;
                        if (lastParam.equals("B") || lastParam.equals("I") || lastParam.equals("S")) {
                            Searcher searcher = new Searcher(method);
                            int L = searcher.findMultiPatterns(new int[][]{{Opcodes.ALOAD}, {Opcodes.ILOAD}}, 0);
                            if (L == -1)
                                hasDummy = false;
                            for (int I = 0; I < method.instructions.size(); ++I) {
                                if (method.instructions.get(I) instanceof VarInsnNode) {
                                    if (((VarInsnNode) (method.instructions.get(I))).var == paramCount - 1) {
                                        hasDummy = false;
                                    }
                                }
                            }
                            if (hasDummy) {
                                dummyParamMethods.add(new MethodInfo(classNode.name, method.name, method.desc));
                                removeLastParam(method);
                                ++fixedParams;
                            }
                        }
                    }
                }
            }
        }

        for (ClassNode classNode : classes.values()) {
            List<MethodNode> methodList = classNode.methods;
            for (MethodNode method : methodList) {
                for (int I = 0; I < method.instructions.size(); ++I) {
                    if (method.instructions.get(I) instanceof MethodInsnNode) {
                        MethodInfo Temp = new MethodInfo(classNode.name, ((MethodInsnNode) method.instructions.get(I)).name, ((MethodInsnNode) method.instructions.get(I)).desc);
                        if (dummyParamMethods.contains(Temp)) {
                            removeLastParam(((MethodInsnNode) method.instructions.get(I)));
                            method.instructions.remove(method.instructions.get(I - 1));
                        }
                    }
                }
            }
        }
        return fixedParams;
    }

    

    @Override
    public int deobfuscate() {
        int fSafe = 0;
        int nTotal = 0;
        int nFixed = -1;
        while (nFixed != 0 && fSafe < 5) { // Apparently this can only run max. 5 times?
            ++fSafe;
            nFixed = run();
            nTotal = nTotal + nFixed;
        }
        System.out.print("Removed " + nTotal + " Dummy Parameters");
        return nTotal;
    }

}