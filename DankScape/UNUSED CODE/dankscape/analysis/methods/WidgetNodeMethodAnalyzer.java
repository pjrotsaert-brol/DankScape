package dankscape.analysis.methods;

/**
 * Created by Kyle on 11/10/2015.
 */

import dankscape.misc.ClassNodeWrapper;
import dankscape.misc.Hook;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

import java.util.Collections;
import java.util.List;
import jdk.internal.org.objectweb.asm.tree.ClassNode;


/**
 * Created by Kyle on 11/5/2015.
 */
public class WidgetNodeMethodAnalyzer extends AbstractMethodAnalyzer {

    public WidgetNodeMethodAnalyzer() {
        setId("WidgetNode");
        setNeededHooks(Collections.singletonList("Id"));
    }
    
    @Override
    public void identify() {
      
        out:
        for (ClassNode c : getClasses().values()) {
            List<MethodNode> methods = getMethods(c, true, "([L" + getOther("Widget").getName() + ";I)V");
            for (MethodNode m : methods) {
                if (m != null) {
                    int L = 0;
                    for (int I = 0; L != -1; ++I) {
                        Searcher search = new Searcher(m);
                        AbstractInsnNode[] Instructions = m.instructions.toArray();
                        L = search.find(new int[]{Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL}, I);
                        if (L != -1 && ((VarInsnNode) m.instructions.get(L)).var == 5 && ((FieldInsnNode) m.instructions.get(L + 1)).owner.equals(getOther("WidgetNode").getName())) {
                            addHook(new Hook("Id", Instructions, L + 1));
                            break out;
                        }
                    }
                }
            }
        }

    }

}
