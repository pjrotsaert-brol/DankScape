package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import eUpdater.searchers.FieldSearcher;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/21/2015.
 */
public class NodeAnalyzer extends AbstractClassAnalyzer {
    
    public NodeAnalyzer() {
        setId("Node");
    }
    
    @Override
    public void identify(ClassNode c) {
        int nodeCount;
        int idCount;
        if (c.superName.equals("java/lang/Object")) {
            FieldSearcher fieldSearch = new FieldSearcher(c);
            idCount = fieldSearch.countDesc("J");
            nodeCount = fieldSearch.countDesc(String.format("L%s;", c.name));
            if ((idCount >= 1) & (nodeCount == 2)) {
                addClassNode(c);
            }
        }
    }
}
