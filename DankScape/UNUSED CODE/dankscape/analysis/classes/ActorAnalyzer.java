package dankscape.analysis.classes;

import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/22/2015.
 */
public class ActorAnalyzer extends AbstractClassAnalyzer {

    public ActorAnalyzer() {
        setId("Actor");
    }
    
    @Override
    public void identify(ClassNode c) {
        if (c.superName.equals(getOther("Renderable").getName()) && c.access == 1057 && c.fields.size() > 40)
            addClassNode(c);
    }
}
