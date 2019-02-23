/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.loader;

import dankscape.api.DankScript;
import dankscape.api.ScriptManifest;
import dankscape.misc.DebugWriter;
import dankscape.misc.JarUtils;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pieterjan
 */
public class ScriptLoader extends DebugWriter
{
    private static URLClassLoader classLoader;
    private static List<String> classList;
    private static List<Class> availableScriptClasses;
    private static String jarName;
    
    public static boolean loadJar(String jarName)
    {
        ScriptLoader.jarName = jarName;
        if(classLoader != null)
        {    
            try 
            {
                classLoader.close();
            } catch (IOException ex) {
                sdebug("ERROR: Could not close classloader: " + ex.toString());
            }
        }
        try
        {
            URL[] urls = { new File(jarName).toURI().toURL() };
            classLoader = new URLClassLoader(urls);
            classList = JarUtils.readJarClassNames(jarName);
            availableScriptClasses = readAvailableScriptClasses();
            return true;
        } 
        catch (MalformedURLException ex)
        {
            sdebug("ERROR: Could not load jar: " + ex.toString());
        }
        return false;
    }
    
    public static URLClassLoader getClassLoader()
    {
        return classLoader;
    }
    
    private static List<Class> readAvailableScriptClasses()
    {
        if(classList == null || classLoader == null)
            return new ArrayList();
        
        ArrayList<Class> scripts = new ArrayList();
        for(String className : classList)
        {
            try
            {
                Class clazz = classLoader.loadClass(className.replace('/', '.'));
                boolean isScript = false;
                Class superClass = clazz;
                while(superClass != null)
                {
                    superClass = superClass.getSuperclass();
                    if(superClass != null)
                    {
                        if(superClass.getName().equals(DankScript.class.getName()))
                        {
                            isScript = true;
                            break;
                        }
                    }
                }
                
                if(isScript)
                {
                    Constructor[] ctors = clazz.getDeclaredConstructors();
                    boolean hasProperCtor = false;
                    for(Constructor ctor : ctors)
                    {
                        if(ctor.getParameterCount() == 0)
                        {
                            hasProperCtor = true;
                            break;
                        }
                    }
                    if(hasProperCtor)
                        scripts.add(clazz);
                }
                
            } 
            catch (ClassNotFoundException ex)
            {
                sdebug("ERROR: Could not load class: " + ex.toString());
            }
        }
        return scripts;
    }
    
    public static List<Class> getAvailableScriptClasses()
    {
        return availableScriptClasses;
    }
    
    private static String loadJarResourceAsString(String resourceName, Class c)
    {
        if(resourceName.equals(""))
            return "";
        
        String packageName = c.getName().replace('.', '/');
        if(packageName.lastIndexOf('/') != -1)
            packageName = packageName.substring(0, packageName.lastIndexOf('/'));
        else
            packageName = "";
        
        String filename;
        if(resourceName.charAt(0) == '/') // '/' at the start denotes an absolute path
            filename = resourceName;
        else
            filename = packageName + "/" + resourceName;
        
        byte[] bytes = JarUtils.readJarFile(jarName, filename);
        if(bytes == null)
        {
            sdebug("ERROR: Failed to read jar resource '" + filename + "' in " + jarName + ".");
            return "";
        }
        char[] chars = new char[bytes.length];
        for(int i = 0;i < bytes.length;i++)
            chars[i] = (char)bytes[i];
        
        return String.copyValueOf(chars);
    }
    
    public static DankScript instantiateScript(Class c)
    {
        Class[] argTypes = {};
        try
        {
            Object[] args = {};
            Constructor ctor = c.getDeclaredConstructor(argTypes);
            DankScript script = (DankScript)ctor.newInstance(args);
            
            ScriptManifest manifest = (ScriptManifest)c.getAnnotation(ScriptManifest.class);
            if(manifest != null)
            {
                if(!manifest.name().equals(""))
                    script.setScriptName(manifest.name());
                else
                    script.setScriptName(c.getSimpleName());
                
                if(!manifest.author().equals(""))
                    script.setAuthor(manifest.author());
                
                if(!manifest.description().equals(""))
                    script.setDescription(manifest.description());
                
                if(!manifest.version().equals(""))
                    script.setVersion(manifest.version());
                
                script.setCustomHTML(loadJarResourceAsString(manifest.html(), c));
                script.setCustomCSS(loadJarResourceAsString(manifest.css(), c));
            }
            else
            {
                script.setScriptName(c.getSimpleName());
            }
            
            return script;
        } 
        catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            sdebug("ERROR: Could not instantiate script '" + c.getName() + "'!\n" + ex.toString() + " " + ex.getCause());
        }
        return null;
    }
    
}
