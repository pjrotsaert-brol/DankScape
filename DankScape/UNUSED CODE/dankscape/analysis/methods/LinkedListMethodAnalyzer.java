package dankscape.analysis.methods;

import dankscape.misc.Hook;
import eUpdater.searchers.FieldSearcher;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.util.Arrays;


/**
 * Created by Kyle on 11/5/2015.
 */
public class LinkedListMethodAnalyzer extends AbstractMethodAnalyzer {

    public LinkedListMethodAnalyzer() {
        setId("LinkedList");
        setNeededHooks(Arrays.asList("Head", "Current"));
        
    }
    
    @Override
    public void identify() {
        FieldSearcher FSearch = new FieldSearcher(getClassNode());
        addHook(new Hook("Head", FSearch.findAccess(Opcodes.ACC_PUBLIC)));
        addHook(new Hook("Current", FSearch.findAccess(0)));
    }
}
