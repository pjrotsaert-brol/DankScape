package dankscape.analysis.methods;

import dankscape.misc.Hook;
import eUpdater.searchers.FieldSearcher;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import java.util.Arrays;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 10/16/2015.
 */
public class NPCDefinitionMethodAnalyzer extends AbstractMethodAnalyzer {

    public NPCDefinitionMethodAnalyzer() {
        setId("NPCDefinition");
        setNeededHooks(Arrays.asList("Actions", "Name", "ID", "CombatLevel"));
    }

    
    @Override
    public void identify() {

        FieldSearcher fs = new FieldSearcher(getClassNode());
        addHook(new Hook("Actions", fs.findDesc("[Ljava/lang/String;")));
        addHook(new Hook("Name", fs.findDesc("Ljava/lang/String;")));

        MethodNode method = getMethod(getClassNode(), false, getOther("AnimationSequence").getName() + ";IL" +
                getOther("AnimationSequence").getName());

        Searcher search = new Searcher(method);
        AbstractInsnNode[] Instructions = method.instructions.toArray();
        int L = search.find(new int[]{Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL, Opcodes.I2L}, 0);
        if (L != -1)
            addHook(new Hook("ID", Instructions, L));

        for (ClassNode c : getClasses().values()) {
            method = getMethod(c, false, "L" + getOther("NPCDefinition").getName() + ";III");
            if (method == null)
                continue;
            search = new Searcher(method);
            L = search.find(new int[]{Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL,
                    Opcodes.GETSTATIC, Opcodes.GETFIELD}, 0);
            Instructions = method.instructions.toArray();
            if (L != -1)
                addHook(new Hook("CombatLevel", Instructions, L + 1));
        }
    }

}
