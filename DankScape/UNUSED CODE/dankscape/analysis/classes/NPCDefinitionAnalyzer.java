package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.List;

/**
 * Created by Kyle on 7/22/2015.
 */
public class NPCDefinitionAnalyzer extends AbstractClassAnalyzer {
    
    public NPCDefinitionAnalyzer() {
        setId("NPCDefinition");
    }
    
    @Override
    public void identify(ClassNode c) {

        if (c.superName.equals(getOther("Cacheable").getName()) && c.access == 33) {
            List<MethodNode> methodList = c.methods;
            for (MethodNode m : methodList) {
                if (m.desc.contains(getOther("AnimationSequence").getName()
                        + ";IL" + getOther("AnimationSequence").getName()))
                    addClassNode(c);
            }
        }
    }
    
}
