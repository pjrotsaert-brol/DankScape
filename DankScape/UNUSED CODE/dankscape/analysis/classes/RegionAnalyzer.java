package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import eUpdater.searchers.FieldSearcher;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/22/2015.
 */
public class RegionAnalyzer extends AbstractClassAnalyzer {
    
    public RegionAnalyzer() {
        setId("Region");
    }
    
    @Override
    public void identify(ClassNode c) {
        if (c.superName.equals("java/lang/Object")) {
            FieldSearcher s = new FieldSearcher(c);
            int L = s.countDesc("[[[[Z");
            if (L == 1)
                addClassNode(c);
        }
    }
}
