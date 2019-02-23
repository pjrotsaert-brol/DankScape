package eUpdater.refactor;

import dankscape.analysis.classes.AbstractClassAnalyzer;
import dankscape.analysis.Analysis;
import dankscape.misc.ClassNodeWrapper;
import dankscape.misc.Hook;
import eUpdater.main.eUpdater;
import eUpdater.misc.JarHandler;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.List;

import static eUpdater.misc.JarHandler.CLASSES;

/**
 * Created by Kyle on 12/5/2015.
 */
public class refactor {

    public static void run() {

        for (ClassNodeWrapper c : CLASSES.values()) {
            List<FieldNode> fieldNodes = c.fields;
            for (FieldNode f : fieldNodes) {
                for (AbstractClassAnalyzer cf : Analysis.getSingleton().getClassAnalyzers()) {
                    if (f.desc.contains("L" + cf.getName())) {
                        String original = f.desc;
                        f.desc = original.replace(cf.getName(), cf.getId());
                    }
                }
            }
        }

        for (AbstractClassAnalyzer cf : Analysis.getSingleton().getClassAnalyzers()) {
            for (ClassNodeWrapper c : CLASSES.values()) {
                if (c.name.equals(cf.getName())) {
                    c.name = cf.getId();
                    if (cf.hasMethodAnalyzer) {
                        for (Hook h : cf.getMethodAnalyzer().getHooks()) {
                            List<FieldNode> fieldNodes = c.fields;
                            for (FieldNode f : fieldNodes) {
                                if (f.name.equals(h.getName())) {
                                    f.name = h.getId();
                                }
                            }
                        }
                    }
                }
            }
        }

        for (AbstractClassAnalyzer cf : Analysis.getSingleton().getClassAnalyzers()) {
            if (cf.hasMethodAnalyzer) {
                for (Hook h : cf.getMethodAnalyzer().getHooks()) {
                    for (ClassNodeWrapper c : CLASSES.values()) {
                        List<FieldNode> fieldNodes = c.fields;
                        for (FieldNode f : fieldNodes) {
                            if (h.getName().equals(f.name) && h.getOwner() != null && h.getOwner().equals(c.name))
                                f.name = h.getId();
                        }
                    }
                }
            }
        }

        for (ClassNodeWrapper c : CLASSES.values()) {
            for (MethodNode m : (List<MethodNode>) c.methods) {
                AbstractInsnNode[] instructions = m.instructions.toArray();
                for (AbstractInsnNode instruction : instructions) {
                    if (instruction instanceof FieldInsnNode) {
                        for (AbstractClassAnalyzer cf : Analysis.getSingleton().getClassAnalyzers()) {
                            if (((FieldInsnNode) instruction).owner.equals(cf.getName())) {
                                ((FieldInsnNode) instruction).owner = (cf.getId());
                                if (cf.hasMethodAnalyzer) {
                                    for (Hook h : cf.getMethodAnalyzer().getHooks()) {
                                        if (((FieldInsnNode) instruction).name.equals(h.getName()))
                                            ((FieldInsnNode) instruction).name = (h.getId());
                                    }
                                }
                            }
                        }
                    }
                }

            }

        }

        JarHandler.save("H:/Documents/Updaters/Current/IDE Version/eUpdater/res/Gamepacks/" + eUpdater.Revision + "/Refactor.jar");
    }
}


