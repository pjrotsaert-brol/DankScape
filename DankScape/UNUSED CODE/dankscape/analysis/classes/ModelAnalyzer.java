package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.List;

/**
 * Created by Kyle on 7/22/2015.
 */
public class ModelAnalyzer extends AbstractClassAnalyzer {
    
    public ModelAnalyzer() {
        setId("Model");
        
    }
    
    @Override
    public void identify(ClassNode c) {
        if (c.superName.equals(getOther("Renderable").getName()) && c.access == 33 && c.fields.size() > 50) {
            List<MethodNode> methodList = c.methods;
            for (MethodNode m : methodList) {
                if (m.desc.equals("([[IIIIZI)" + "L" + c.name + ";"))
                    addClassNode(c);
            }
        }
    }
}
