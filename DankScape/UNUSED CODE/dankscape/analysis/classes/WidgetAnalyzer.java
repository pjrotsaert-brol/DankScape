package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import eUpdater.searchers.FieldSearcher;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/22/2015.
 */
public class WidgetAnalyzer extends AbstractClassAnalyzer {
    
    public WidgetAnalyzer() {
        setId("Widget");
    }
    
    @Override
    public void identify(ClassNode c) {
        if (c.superName.equals(getOther("Node").getName()) && c.fields.size() > 50) {
            addClassNode(c);
        }
    }
}
