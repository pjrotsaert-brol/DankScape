package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.List;

/**
 * Created by Kyle on 7/21/2015.
 */
public class RenderableAnalyzer extends AbstractClassAnalyzer {

    public RenderableAnalyzer() {
        setId("Renderable");
    }
    
    @Override
    public void identify(ClassNode c) {

        if (c.superName.equals(getOther("Cacheable").getName())) {
            for (int I = 0; I < c.methods.size(); ++I) {
                List<MethodNode> methodList = c.methods;
                for (MethodNode m : methodList) {
                    if (m.desc.contains("(IIIIIIII")) {
                        Searcher search = new Searcher(m);
                        int L = search.find(new int[]{Opcodes.ALOAD, Opcodes.ILOAD, Opcodes.ILOAD, Opcodes.ILOAD, Opcodes.ILOAD, Opcodes.ILOAD}, 0);
                        if (L > 0) {
                            addClassNode(c);
                        }
                    }
                }
            }
        }
    }
}
