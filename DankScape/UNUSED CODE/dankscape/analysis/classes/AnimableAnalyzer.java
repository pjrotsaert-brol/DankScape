package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/21/2015.
 */
public class AnimableAnalyzer extends AbstractClassAnalyzer {

    public AnimableAnalyzer() {
        setId("Animable");
    }
    
    @Override
    public void identify(ClassNode c) {
        if (c.superName.equals(getOther("Renderable").getName())) {
            for (int I = 0; I < c.methods.size(); ++I) {
                MethodNode Method = (MethodNode) c.methods.get(I);
                if (Method.desc.equals("(IIIIIIIZL" + getOther("Renderable").getName() + ";)V"))
                    addClassNode(c);
            }
        }
    }
}


