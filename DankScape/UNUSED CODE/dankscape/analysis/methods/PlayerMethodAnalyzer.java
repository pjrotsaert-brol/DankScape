package dankscape.analysis.methods;

import dankscape.misc.ClassNodeWrapper;
import dankscape.misc.Hook;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.List;

import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 11/10/2015.
 */
public class PlayerMethodAnalyzer extends AbstractMethodAnalyzer {

    public PlayerMethodAnalyzer() {
        setId("Player");
        setNeededHooks(Arrays.asList("Name", "Definition", "CombatLevel"));
    }

    
    @Override
    public void identify() {

        addHook(new Hook("Name", getFields(getClassNode(), "Ljava/lang/String;").get(0)));

        List<MethodNode> methods = getMethods(getClassNode(), false, "L" + getOther("Model").getName() + ";");
        for (MethodNode m : methods) {
            Searcher search = new Searcher(m);
            AbstractInsnNode[] instruction = m.instructions.toArray();
            int L = search.find(new int[]{Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.ALOAD}, 0);
            if (L != -1) {
                addHook(new Hook("Definition", instruction, L + 1));
                break;
            }
        }

        for (ClassNode c : getClasses().values()) {
            methods = getMethods(c, true, "(L" + getOther("Widget").getName() + ";I)I");
            for (MethodNode m : methods) {
                Searcher search = new Searcher(m);
                if (search.findSingleIntValue(Opcodes.BIPUSH, 100) != -1) {
                    AbstractInsnNode[] Instructions = m.instructions.toArray();
                    int L = search.findSingleIntValue(Opcodes.BIPUSH, 8);
                    L = search.findSingleJump(Opcodes.GOTO, Opcodes.GETFIELD, L, 10, 0);
                    addHook(new Hook("CombatLevel", Instructions, L));

                }
            }
        }
    }
}
