package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.List;

/**
 * Created by Kyle on 7/22/2015.
 */
public class ObjectDefinitionAnalyzer extends AbstractClassAnalyzer {
    
    public ObjectDefinitionAnalyzer() {
        setId("ObjectDefinition");
    }
    
    @Override
    public void identify(ClassNode c) {
        
        if (c.superName.equals(getOther("Cacheable").getName())) {
            List<MethodNode> methodList = c.methods;
            for (MethodNode m : methodList) {
                if (m.desc.contains("II[[IIII")) {
                    addClassNode(c);
                    break;
                }
            }
        }
    }
}
