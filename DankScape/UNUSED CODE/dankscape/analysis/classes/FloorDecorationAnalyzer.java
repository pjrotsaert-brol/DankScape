package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/23/2015.
 */
public class FloorDecorationAnalyzer extends AbstractClassAnalyzer {
    
    public FloorDecorationAnalyzer() {
        setId("FloorDecoration");
    }
    
    @Override
    public void identify(ClassNode c) {

        if ((getFields(c, "L" + getOther("Renderable").getName() + ";").size() == 1)
                && (getFields(c, Opcodes.ACC_PUBLIC, Opcodes.ACC_STATIC)).size() == 4)
            addClassNode(c);
    }
}
