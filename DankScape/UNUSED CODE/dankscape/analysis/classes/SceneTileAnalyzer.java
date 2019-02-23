package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import java.util.List;

/**
 * Created by Kyle on 7/23/2015.
 */
public class SceneTileAnalyzer extends AbstractClassAnalyzer {
    
    public SceneTileAnalyzer() {
        setId("SceneTile");
    }
    
    @Override
    public void identify(ClassNode c) {
        List<FieldNode> fields = c.fields;
        int count = 0, count2 = 0;
        for (FieldNode fN : fields) {
            if (fN.desc.equals("I") && (fN.access & Opcodes.ACC_PUBLIC) == 0 && (fN.access & Opcodes.ACC_STATIC) == 0)
                ++count;
            if (fN.desc.equals("[I") && (fN.access & Opcodes.ACC_PUBLIC) == 0 && (fN.access & Opcodes.ACC_STATIC) == 0)
                ++count2;
        }
        if ((count == 11) && count2 == 1)
            addClassNode(c);
    }
}
