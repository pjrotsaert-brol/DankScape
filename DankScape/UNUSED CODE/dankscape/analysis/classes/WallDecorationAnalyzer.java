package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/23/2015.
 */
public class WallDecorationAnalyzer extends AbstractClassAnalyzer {
    
    public WallDecorationAnalyzer() {
        setId("WallDecoration");
    }
    
    @Override
    public void identify(ClassNode c) {
        if ((getFields(c, "L" + getOther("Renderable").getName() + ";").size() == 2)
                && (getFields(c, Opcodes.ACC_PUBLIC, Opcodes.ACC_STATIC).size() == 8))
            addClassNode(c);
    }
}
