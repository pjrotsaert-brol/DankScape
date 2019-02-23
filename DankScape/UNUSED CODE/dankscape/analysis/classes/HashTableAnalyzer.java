package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import eUpdater.searchers.FieldSearcher;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/22/2015.
 */
public class HashTableAnalyzer extends AbstractClassAnalyzer {
    
    public HashTableAnalyzer() {
        setId("HashTable");
    }
    
    @Override
    public void identify(ClassNode c) {

        if (c.superName.equals("java/lang/Object")) {
            FieldSearcher FSearch = new FieldSearcher(c);
            int nodeArray = FSearch.countDesc("[L" + getOther("Node").getName() + ";");
            int nodeCount = FSearch.countDesc("L" + getOther("Node").getName() + ";");
            if (nodeArray == 1 && nodeCount == 2) {
                addClassNode(c);
            }
        }
    }
}
