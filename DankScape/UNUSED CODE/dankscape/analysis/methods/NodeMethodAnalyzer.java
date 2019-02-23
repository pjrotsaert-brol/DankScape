package dankscape.analysis.methods;

import dankscape.misc.Hook;
import eUpdater.searchers.FieldSearcher;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Kyle on 7/27/2015.
 */
public class NodeMethodAnalyzer extends AbstractMethodAnalyzer {

    public NodeMethodAnalyzer() {
        setId("Node");
        setNeededHooks(Arrays.asList("Next", "Prev", "UID"));
    }

    
    @Override
    public void identify() {
        
        List<MethodNode> methodList = getClassNode().methods;
        for (MethodNode m : methodList) {
            if (m.instructions.size() < 11) {
                AbstractInsnNode[] Instructions = m.instructions.toArray();
                Searcher search = new Searcher(m);
                int L = search.findSingle(Opcodes.GETFIELD, 0);
                if (L != -1)
                    addHook(new Hook("Prev", Instructions, L));
            }
        }
        FieldSearcher fs = new FieldSearcher(getClassNode());
        addHook(new Hook("UID", fs.findDesc("J")));

        for (int I = 0; I < 3; ++I) {
            if (getHook("Prev").getName().equals(fs.findDescInstance(String.format("L%s;", getClassNode().name), I).name))
                continue;
            addHook(new Hook("Next", fs.findDescInstance(String.format("L%s;", getClassNode().name), I)));
            break;
        }
    }
}
