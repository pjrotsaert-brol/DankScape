package dankscape.analysis.methods;

import dankscape.misc.ClassNodeWrapper;
import dankscape.misc.Hook;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 11/9/2015.
 */
public class NPCMethodAnalyzer extends AbstractMethodAnalyzer {

    public NPCMethodAnalyzer() {
        setId("NPC");
        setNeededHooks(Collections.singletonList("Definition"));
    }
    
    

    @Override
    public void identify() {
        int L = 0;
        for (ClassNode c : getClasses().values()) {
            MethodNode method = getMethod(c, true, "(IIIILjava/lang/String;Ljava/lang/String;II)V");
            if (method != null) {
                AbstractInsnNode[] Instructions = method.instructions.toArray();
                Searcher search = new Searcher(method);
                for (int I = 0; L != -1; ++I) {
                    L = search.find(new int[]{Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.ASTORE}, I);
                    if (L != -1)
                        if ((((FieldInsnNode) Instructions[L + 1]).desc.equals("L" + getOther("NPCDefinition").getName() + ";")))
                            addHook(new Hook("Definition", Instructions, L + 1));
                }
            }
        }
    }

}

