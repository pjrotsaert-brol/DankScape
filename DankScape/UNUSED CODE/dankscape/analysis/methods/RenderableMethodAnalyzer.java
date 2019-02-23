package dankscape.analysis.methods;

import dankscape.misc.Hook;
import eUpdater.searchers.FieldSearcher;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Kyle on 10/16/2015.
 */
public class RenderableMethodAnalyzer extends AbstractMethodAnalyzer {

    public RenderableMethodAnalyzer() {
        setId("Renderable");
        setNeededHooks(Arrays.asList("ModelHeight"));
    }

    
    @Override
    public void identify() {
        List<MethodNode> methodList = getClassNode().methods;
        for (MethodNode method : methodList) {
            if (method.desc.contains("(IIIIIIII")) {
                Searcher Search = new Searcher(method);
                int L = Search.find(new int[]{Opcodes.ALOAD, Opcodes.ILOAD, Opcodes.ILOAD, Opcodes.ILOAD, Opcodes.ILOAD, Opcodes.ILOAD}, 0);
                if (L > 0) {
                    FieldSearcher fs = new FieldSearcher(getClassNode());
                    //Searcher search = new Searcher(method);
                    addHook(new Hook("ModelHeight", fs.findAccess(1)));
                }
            }
        }
    }

}
