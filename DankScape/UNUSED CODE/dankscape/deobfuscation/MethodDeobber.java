package dankscape.deobfuscation;
/**
 * Created by Kyle on 1/12/2015.
 */

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MethodDeobber extends AbstractDeobfuscator 
{
    private ArrayList<MethodInfo> totalMethods = new ArrayList();
    private ArrayList<MethodInfo> goodMethods = new ArrayList();
    
    public MethodDeobber(HashMap<String, ClassNode> classList){
        super(classList);
    }
    
    private void getInterfaces(ClassNode clazz) 
    {
        if (clazz.interfaces.size() > 0) {
            for (String itfName : clazz.interfaces) 
            {
                ClassNode interfaceClass;
                interfaceClass = new ClassNode();
                if (!itfName.contains("java"))
                    interfaceClass = classes.get(itfName);
                List<MethodNode> Methods = interfaceClass.methods;
                for (MethodNode Method : Methods)
                    add(new MethodInfo(clazz.name, Method.name, Method.desc), goodMethods);
            }
        }
    }

    private boolean isOverridden(ClassNode clazz, MethodNode method) {
        MethodInfo methodInfo = new MethodInfo(clazz.name, method.name, method.desc);
        String superClassName = clazz.superName;
        while (superClassName != null && !superClassName.equals("java/lang/Object")) {
            ClassNode superClass;
            if (superClassName.startsWith("java")) {
                superClass = new ClassNode();
                try {
                    //System.out.println(superClassName);
                    ClassReader cr = new ClassReader(superClassName);
                    cr.accept(superClass, 0);
                } catch (Exception e) {
                }
            } else
                superClass = classes.get(superClassName);
            if (hasMethod(superClass, methodInfo.name, methodInfo.desc))
                return true;
            superClassName = superClass.superName;
        }
        return false;
    }

    private void getInvoked(ClassNode clazz) {
        List<MethodNode> Methods = clazz.methods;
        for (MethodNode Method : Methods) {
            AbstractInsnNode[] instructions = Method.instructions.toArray();
            for (AbstractInsnNode instruction : instructions) {
                if (instruction instanceof MethodInsnNode) {
                    MethodInsnNode methodInstruction = (MethodInsnNode) instruction;
                    MethodInfo instructionInfo = new MethodInfo(methodInstruction.owner, methodInstruction.name, methodInstruction.desc);
                    if (!instructionInfo.owner.contains("java")) {
                        if (hasMethod(classes.get(instructionInfo.owner), instructionInfo.name, instructionInfo.desc))
                            add(instructionInfo, goodMethods);
                        else {
                            String supperClassName = classes.get(instructionInfo.owner).superName;
                            while (!supperClassName.contains("java")) {
                                ClassNode superClass = classes.get(supperClassName);
                                if (hasMethod(superClass, instructionInfo.name, instructionInfo.desc)) {
                                    MethodInfo superMethod = new MethodInfo(superClass.name, instructionInfo.name, instructionInfo.desc);
                                    add(superMethod, goodMethods);
                                    break;
                                }
                                supperClassName = superClass.superName;
                            }
                        }
                    }
                }
            }
        }
    }

    private ArrayList<MethodInfo> findRedundantMethods() {
        ArrayList<MethodInfo> methodsToRemove = new ArrayList();
        for (MethodInfo mi : totalMethods)
            if (!goodMethods.contains(mi))
                methodsToRemove.add(mi);
        return methodsToRemove;
    }

    private static void add(MethodInfo info, ArrayList<MethodInfo> usedMethods) {
        if (!usedMethods.contains(info)) {
            usedMethods.add(info);
        }
    }

    private static boolean hasMethod(ClassNode clazz, String methodName, String methodDesc) {
        List<MethodNode> methods = clazz.methods;
        for (MethodNode method : methods)
            if (method.name.equals(methodName) && method.desc.equals(methodDesc))
                return true;
        return false;
    }

    private int removeDummyMethods() {
        int tempResult = 0;
        ArrayList<MethodInfo> removeMethods;
        removeMethods = findRedundantMethods();
        for (ClassNode classNode : classes.values()) {
            for (int I = 0; I < removeMethods.size(); ++I) {
                if (classNode.name.equals(removeMethods.get(I).owner)) {
                    for (int C = 0; C < classNode.methods.size(); ++C) {
                        MethodNode Method = (MethodNode) classNode.methods.get(C);
                        if (Method.name.equals(removeMethods.get(I).name)) {
                            if (Method.desc.equals(removeMethods.get(I).desc)) {
                                classNode.methods.remove(Method);
                                ++tempResult;
                            }
                        }
                    }
                }
            }
        }
        return tempResult;
    }

    private int run() {
        for (ClassNode clazz : classes.values()) {
            getInterfaces(clazz);
            getInvoked(clazz);
            List<MethodNode> Methods = clazz.methods;
            for (MethodNode Method : Methods) {
                totalMethods.add(new MethodInfo(clazz.name, Method.name, Method.desc));
                if (Method.name.length() > 2 || Modifier.isAbstract(Method.access) || isOverridden(clazz, Method)) {
                    MethodInfo mInfo = new MethodInfo(clazz.name, Method.name, Method.desc);
                    add(mInfo, goodMethods);
                }
            }
        }
        return removeDummyMethods();
    }

    @Override
    public int deobfuscate() {
        int nTotal = 0;
        int nFixed = 10;
        while (nFixed != 0) {
            nFixed = run();
            nTotal = nTotal + nFixed;
        }
        System.out.print("Removed " + nTotal + " Dummy Methods");
        return nTotal;
    }

}
