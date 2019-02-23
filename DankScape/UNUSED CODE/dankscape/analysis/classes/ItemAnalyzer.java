package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.List;

/**
 * Created by Kyle on 7/24/2015.
 */
public class ItemAnalyzer extends AbstractClassAnalyzer {
    
    public ItemAnalyzer() {
        setId("Item");
    }
    
    @Override
    public void identify(ClassNode c) {
        if (c.superName.equals(getOther("Renderable").getName()) && c.access == 49) {
            List<FieldNode> fields = c.fields;
            int count = 0;
            for (FieldNode fN : fields) {
                if (fN.desc.equals("I") && (fN.access & Opcodes.ACC_PUBLIC) == 0 && (fN.access & Opcodes.ACC_STATIC) == 0)
                    ++count;
            }
            if (count == 2)
                addClassNode(c);
        }
    }
}
