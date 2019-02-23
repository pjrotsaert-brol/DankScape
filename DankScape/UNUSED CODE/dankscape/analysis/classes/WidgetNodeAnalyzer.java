package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import java.lang.reflect.Modifier;

/**
 * Created by Kyle on 7/22/2015.
 */
public class WidgetNodeAnalyzer extends AbstractClassAnalyzer {
    
    public WidgetNodeAnalyzer() {
        setId("WidgetNode");
    }
    
    @Override
    public void identify(ClassNode c) {
        int intCount = 0, boolCount = 0;
        if (c.superName.equals(getOther("Node").getName())) {
            for (int I = 0; I < c.fields.size(); ++I) {
                FieldNode Field = (FieldNode) c.fields.get(I);
                if (Modifier.isStatic(Field.access))
                    continue;
                if (Field.desc.equals("I"))
                    ++intCount;
                if (Field.desc.equals("Z"))
                    ++boolCount;
            }
            if (boolCount == 1 && intCount == 2)
                addClassNode(c);
        }
    }

}
