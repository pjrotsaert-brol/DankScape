/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

import dankscape.nativeinterface.NativeInterface;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 *
 * @author Pieterjan
 */
public class JarUtils 
{
    /******** Internal Use *********/

    // Reads ClassNodes from a jar file
    public static HashMap<String, ClassNode> readJarClasses(String filename) {
        HashMap<String, ClassNode> classes = new HashMap<>();
        try {
            JarFile jar = new JarFile(filename);
            Enumeration<?> enumeration = jar.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = (JarEntry) enumeration.nextElement();
                if (entry.getName().endsWith(".class")) {
                    ClassReader classReader = new ClassReader(jar.getInputStream(entry));
                    ClassNode classNode = new ClassNode();
                    classReader.accept(classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    classes.put(classNode.name, classNode);
                }
            }
            jar.close();
            
            System.out.println("Parsed jar: " + classes.size() + " classes found.");
            return classes;
        } catch (IOException e) {
            System.out.println(e.getCause());
            return null;
        }
    }
    
    public static List<String> readJarClassNames(String filename) 
    {
        ArrayList<String> classNames = new ArrayList();
        try 
        {
            JarFile jar = new JarFile(filename);
            Enumeration<?> enumeration = jar.entries();
            while (enumeration.hasMoreElements()) 
            {
                JarEntry entry = (JarEntry) enumeration.nextElement();
                if (entry.getName().endsWith(".class")) 
                    classNames.add(entry.getName().substring(0, entry.getName().length() - ".class".length()).replace('/', '.'));
            }
            jar.close();
            return classNames;
        } 
        catch (IOException e) 
        {
            System.out.println(e.getCause());
            return new ArrayList();
        }
    }
    
    // Returns the content of a file in the jar as an array of bytes
    public static byte[] readJarFile(String filename, String path) 
    {
        try 
        {
            byte[] data;
            JarFile jar = new JarFile(filename);
            JarEntry entry = (JarEntry) jar.getEntry(path);
            InputStream in = jar.getInputStream(entry);
                    
            data = new byte[(int)entry.getSize()];
            
            int nRead = 0;
            while(nRead < data.length)
                nRead += in.read(data, nRead, data.length - nRead);
            
            jar.close();
            return data;
        } 
        catch (IOException e) 
        {
            System.out.println(e.toString() + " " + e.getCause());
            return null;
        }
    }
    
    
}
