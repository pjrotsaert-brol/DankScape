package dankscape.analysis.classes;

import dankscape.misc.ClassNodeWrapper;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/22/2015.
 */
public class GameShellAnalyzer extends AbstractClassAnalyzer {
    
    public GameShellAnalyzer() {
        setId("GameShell");
    }
    
    @Override
    public void identify(ClassNode c) {
        if (c.superName.equals("java/applet/Applet")) {
            if (c.interfaces.size() == 3)
                addClassNode(c);
        }
    }
}
