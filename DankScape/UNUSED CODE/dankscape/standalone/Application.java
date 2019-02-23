/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dankscape.standalone;

import dankscape.analysis.Analysis;
import dankscape.deobfuscation.Deobfuscation;
import dankscape.misc.JarUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 *
 * @author Pieterjan
 */
public class Application {
    
    public static void main(String[] args)
    {
        System.out.println("Note: Standalone launch is available for testing purposes only.\n");
        
        try {
            
            HashMap<String, ClassNode> classes = JarUtils.readJarClasses("gamepack.jar");
            Deobfuscation.deobfuscate(classes);
            Analysis.getSingleton().analyze(classes);
            JarUtils.writeJarClasses("gamepack.jar", "gamepack_out.jar", classes);
            
        } catch (Exception ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
