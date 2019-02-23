package dankscape.deobfuscation;
/**
 * Created by Kyle on 1/12/2015.
 */

import dankscape.misc.Misc;
import java.util.ArrayList;
import java.util.HashMap;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

public class Deobfuscation {
    private final ArrayList<AbstractDeobfuscator> deobbers = new ArrayList();
    private static Deobfuscation singleton = null;
    
    private static Deobfuscation getSingleton()
    {
        if(singleton == null)
            singleton = new Deobfuscation();
        return singleton;
    }

    private void loadDeobbers(HashMap<String, ClassNode> classList) 
    {
        deobbers.clear();
        deobbers.add(new MethodDeobber(classList));
        deobbers.add(new MultiplierDeobber(classList));
        deobbers.add(new EqualSwapDeobber(classList));
        deobbers.add(new ExceptionDeobber(classList));
        deobbers.add(new OpaquePredicateDeobber(classList));
        deobbers.add(new MethodNameDeobber(classList));
    }

    private void runDeobbers(HashMap<String, ClassNode> classList) 
    {
        loadDeobbers(classList);
        
        double totalTime = 0;
        for (AbstractDeobfuscator deobber : this.deobbers) 
        {
            long startTime = System.nanoTime();
            deobber.deobfuscate();
            long endTime = System.nanoTime();
            double tempTime = (endTime - startTime) / 1e6;
            System.out.println(" (" + Misc.round(tempTime, 2) + " ms)");
            totalTime = totalTime + tempTime;
        }
        System.out.println("Total Deob took " + Misc.round(totalTime, 2) + " ms");
    }

    public static void deobfuscate(HashMap<String, ClassNode> classList) {
        System.out.println("Beginning Deob..");
        getSingleton().runDeobbers(classList);
        System.out.println("Deob Finished..\n");
    }
}
