package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import eUpdater.searchers.FieldSearcher;
import jdk.internal.org.objectweb.asm.tree.ClassNode;


/**
 * Created by Kyle on 7/22/2015.
 */
public class LinkedListAnalyzer extends AbstractClassAnalyzer {
    
    public LinkedListAnalyzer() {
        setId("LinkedList");
    }
    
    @Override
    public void identify(ClassNode c) {
        if (c.superName.equals("java/lang/Object") && c.fields.size() == 2) {
            FieldSearcher FSearch = new FieldSearcher(c);
            int L = FSearch.countDesc("L" + getOther("Node").getName() + ";");
            if (L == 2 && c.methods.size() > 9)
                addClassNode(c);
        }
    }
}
