package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/22/2015.
 */
public class ClientAnalyzer extends AbstractClassAnalyzer {

    public ClientAnalyzer() {
        setId("Client");
    }
    
    @Override
    public void identify(ClassNode c) {
        if (c.name.equals("client"))
            addClassNode(c);
    }

}
