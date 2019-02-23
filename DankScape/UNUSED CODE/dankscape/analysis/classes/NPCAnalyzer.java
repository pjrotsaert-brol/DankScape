package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/22/2015.
 */
public class NPCAnalyzer extends AbstractClassAnalyzer {
    
    public NPCAnalyzer() {
        setId("NPC");
    }
    
    @Override   
    public void identify(ClassNode c) {
        if (c.superName.equals(getOther("Actor").getName()) && c.access == 49
                && c.fields.size() < 10)
            addClassNode(c);
    }
}
