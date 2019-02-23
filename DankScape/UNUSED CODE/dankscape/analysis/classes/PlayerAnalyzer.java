package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/22/2015.
 */
public class PlayerAnalyzer extends AbstractClassAnalyzer {

    public PlayerAnalyzer() {
        setId("Player");
    }
    
    @Override
    public void identify(ClassNode c) {

        if (c.superName.equals(getOther("Actor").getName())) {
            for (int I = 0; I < c.fields.size(); ++I) {
                FieldNode field = (FieldNode) c.fields.get(I);
                if (field.desc.equals("Ljava/lang/String;")) {
                    if (c.fields.size() > 10)
                        addClassNode(c);
                }
            }
        }
    }
}
