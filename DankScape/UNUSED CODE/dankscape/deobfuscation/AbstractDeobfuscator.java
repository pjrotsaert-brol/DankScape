package dankscape.deobfuscation;

import java.util.HashMap;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 1/12/2015.
 */

public abstract class AbstractDeobfuscator {
    
    protected final HashMap<String, ClassNode> classes;
    
    public AbstractDeobfuscator(HashMap<String, ClassNode> classList){
        classes = classList;
    }
    
    public abstract int deobfuscate();
    
}
