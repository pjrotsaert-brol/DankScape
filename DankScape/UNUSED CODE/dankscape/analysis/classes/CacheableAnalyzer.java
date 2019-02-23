package dankscape.analysis.classes;

import dankscape.analysis.methods.AbstractMethodAnalyzer;
import dankscape.misc.ClassNodeWrapper;
import eUpdater.searchers.FieldSearcher;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/21/2015.
 */
public class CacheableAnalyzer extends AbstractClassAnalyzer {
    
    public CacheableAnalyzer() {
        setId("Cacheable");
    }
    
    @Override
    public void identify(ClassNode c) {
        int nodeCount;
        if (c.superName.equals(getOther("Node").getName()) && c.fields.size() == 2) {
            FieldSearcher fieldSearch = new FieldSearcher(c);
            nodeCount = fieldSearch.countDesc(String.format("L%s;", c.name));
            if (nodeCount == 2)
                addClassNode(c);
        }
    }

}
