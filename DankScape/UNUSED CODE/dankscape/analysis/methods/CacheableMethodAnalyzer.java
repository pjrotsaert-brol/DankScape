package dankscape.analysis.methods;

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
 * Created by Kyle on 10/16/2015.
 */
public class CacheableMethodAnalyzer extends AbstractMethodAnalyzer {

    public CacheableMethodAnalyzer() {
        setId("Cacheable");
        setNeededHooks(Arrays.asList("Next", "Prev"));
    }

    
    @Override
    public void identify() {

        List<MethodNode> methodList = getClassNode().methods;
        for (MethodNode method : methodList) {
            if (method.name.equals("<init>"))
                continue;

            AbstractInsnNode[] instructions = method.instructions.toArray();
            Searcher search = new Searcher(method);
            int L = search.find(new int[]{Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.IFNONNULL}, 0);
            if (instructions[L + 1] instanceof FieldInsnNode) {
                addHook(new Hook("Next", instructions, L + 1));

                List<FieldNode> fields = getClassNode().fields;
                for (FieldNode field : fields) {
                    if (field.name.contains(getOther("Cacheable").getMethodAnalyzer().getHook("Next").getName()))
                        continue;
                    addHook(new Hook("Prev", field));
                    break;
                }
            }
        }
    }
}
