package dankscape.analysis.methods;

import dankscape.analysis.Analysis;
import dankscape.misc.Hook;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.List;


/**
 * Created by Kyle on 11/10/2015.
 */
public class HashTableMethodAnalyzer extends AbstractMethodAnalyzer {

    public HashTableMethodAnalyzer() {
        setId("HashTable");
        setNeededHooks(Arrays.asList("Buckets", "Size", "Index"));
    }

    
    @Override
    public void identify() {

        addHook(new Hook("Buckets", getFields(getClassNode(), "[L" + getOther("Node").getName() + ";").get(0)));

        List<MethodNode> methodList = getClassNode().methods;
        for (MethodNode m : methodList) {
            Searcher Search = new Searcher(m);
            int L = Search.find(new int[]{Opcodes.GETFIELD}, 0);
            if (L != -1) {
                AbstractInsnNode[] Instructions = m.instructions.toArray();
                if (((FieldInsnNode) Instructions[L]).name.equals(Analysis.getSingleton().getMethodAnalyzer("HashTable").getHook("Buckets").getName())) {
                    L = Search.find(new int[]{Opcodes.GETFIELD}, 0, L + 1);
                    if (L != -1 && ((FieldInsnNode) Instructions[L]).desc.equals("I")) {
                        addHook(new Hook("Size", Instructions, L));
                        break;
                    }
                }
            }
        }
        
        List<FieldNode> fs = getFields(getClassNode(), "I");
        for (FieldNode f : fs) {
            if (!f.name.equals(Analysis.getSingleton().getMethodAnalyzer("HashTable").getHook("Size").getName()))
                addHook(new Hook("Index", f));
        }
    }
}
