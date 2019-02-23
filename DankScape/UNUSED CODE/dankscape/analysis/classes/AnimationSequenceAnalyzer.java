package dankscape.analysis.classes;


import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.List;

/**
 * Created by Kyle on 7/22/2015.
 */
public class AnimationSequenceAnalyzer extends AbstractClassAnalyzer {
    
    public AnimationSequenceAnalyzer() {
        setId("AnimationSequence");
    }
    
    @Override
    public void identify(ClassNode c) {
        if (c.superName.equals(getOther("Cacheable").getName())) {
            List<MethodNode> methodList = c.methods;
            for (MethodNode m : methodList) {
                if (m.desc.equals("(L" + getOther("Model").getName() + ";IL" + c.name + ";I)L"
                        + getOther("Model").getName() + ";"))
                    addClassNode(c);
            }
        }
    }
}
