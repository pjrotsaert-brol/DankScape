package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.List;

/**
 * Created by Kyle on 7/23/2015.
 */
public class GameObjectAnalyzer extends AbstractClassAnalyzer {
    
    public GameObjectAnalyzer() {
        setId("GameObject");
    }
    
    @Override
    public void identify(ClassNode c) {

        ClassNode removed = c;
        //removed.removeFields("I");
        int count = 0, count2 = 0;
        List<FieldNode> fields = removed.fields;
        for (FieldNode fN : fields) {
            if (fN.desc.equals("I")) {
                if ((fN.access & Opcodes.ACC_PUBLIC) == 0
                        && (fN.access & Opcodes.ACC_STATIC) == 0)
                    ++count;
                if ((fN.access & Opcodes.ACC_PUBLIC) != 0 && (fN.access & Opcodes.ACC_STATIC) == 0)
                    ++count2;
            }
        }
        if ((count == 12) && count2 == 1)
            addClassNode(c);
    }
}
