package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.List;

/**
 * Created by Kyle on 7/22/2015.
 */
public class BufferAnalyzer extends AbstractClassAnalyzer {
    
    public BufferAnalyzer() {
        setId("Buffer");
    }
    
    @Override
    public void identify(ClassNode c) {

        if (c.superName.equals(getOther("Node").getName())) {
            int Count = 0;
            List<MethodNode> methodList = c.methods;
            for (MethodNode m : methodList) {
                if (m.desc.contains("(Ljava/lang/CharSequence;"))
                    ++Count;
                if (m.desc.contains("(Ljava/math/BigInteger;Ljava/math/BigInteger;"))
                    ++Count;
                if (Count == 2) {
                    addClassNode(c);
                    break;
                }
            }
        }
    }
}
