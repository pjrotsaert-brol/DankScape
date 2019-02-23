/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api.internal;

import dankscape.loader.AppletLoader;
import dankscape.misc.APIGenerator;
import dankscape.misc.ClassHook;
import dankscape.misc.DebugWriter;
import dankscape.misc.FieldHook;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pieterjan
 */
public class RSClassWrapper extends DebugWriter
{
    protected Object ref = null; // Reference to the represented object in the RS Client.
    private Object userdata = null;
    
    private static final HashMap<Object, RSClassWrapper> rsObjectCache = new HashMap();
    
    static 
    {
        //setStaticTag("RSClassWrapper");
    }
    
    public RSClassWrapper(Object ref)
    {
        setTag("Wrapper:" + this.getClass().getName());
        this.ref = ref;
    }
    
    public Object getRSObjectReference()
    {
        return this.ref;
    }
    
    public void setUserData(Object obj)
    {
        userdata = obj;
    }
    
    public Object getUserData()
    {
        return userdata;
    }
    
    private static RSClassWrapper allocWrapper(Object ref)
    {
        String targetName = ref.getClass().getName();
        int pointIdx = targetName.lastIndexOf(".");
        if(pointIdx >= 0)
            targetName = targetName.substring(pointIdx + 1);
        
        ClassHook cH = null;
        HashMap<String, ClassHook> hooks = AppletLoader.getSingleton().getHooks();
        for(ClassHook c : hooks.values())
        {
            if(c.internalName.equals(targetName))
            {
                cH = c;
                break;
            }
        }
        
        if(cH == null)
            return null;
        
        try
        { 
            return (RSClassWrapper)Class.forName("dankscape.api.rs." + APIGenerator.toApiClassName(cH.name))
                    .getConstructor(Object.class).newInstance(ref);
        } 
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException 
                | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            sdebug("ERROR: Could not create objectwrapper for class '" +  APIGenerator.toApiClassName(cH.name) + "'.");
            Logger.getLogger(RSClassWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static RSClassWrapper getWrapper(Object ref)
    {
        if(ref == null)
            return null;
        
        RSClassWrapper wrapper = rsObjectCache.get(ref);
        if(wrapper != null)
            return wrapper;
        wrapper = allocWrapper(ref);
        if(wrapper != null)
            rsObjectCache.put(ref, wrapper);
        return wrapper;
    }
    
    public void dumpDebug()
    {
        dumpDebug(ref);
    }
    
    public static void dumpDebug(Object ref)
    {
        String targetName = ref.getClass().getName();
        int pointIdx = targetName.lastIndexOf(".");
        if(pointIdx >= 0)
            targetName = targetName.substring(pointIdx + 1);
        
        HashMap<String, ClassHook> hooks = AppletLoader.getSingleton().getHooks();
        for(ClassHook c : hooks.values())
        {
            if(c.internalName.equals(targetName))
            {
                targetName = c.name;
                break;
            }
        }

        ClassHook cH = AppletLoader.getSingleton().getHooks().get(targetName);
        if(cH == null)
        {
            sdebug("dumpDebug(): ERROR: Could not find class '" + targetName + "'!");
            return;
        }
        
        sdebug("==== Debug Dump for object of '" + APIGenerator.toApiClassName(targetName) + "': " + cH.clazz.getFields().length + " fields ====");
        for(Field f : cH.clazz.getFields())
        {
            FieldHook fH = null;
            for(FieldHook hook : cH.fields.values())
            {
                if(hook.internalName.equals(f.getName()))
                {
                    fH = hook;
                    break;
                }
            }
            
            try 
            {
                if(f.getType() == int.class)
                {    
                    if(fH == null)
                        sdebug(f.toString() + ": " + f.getInt(ref));
                    else
                        sdebug(f.toString() + "(" + fH.name + "): " + f.getInt(ref) * fH.multiplier);
                }
                else if(f.getType() == long.class)
                {    
                    if(fH == null)
                        sdebug(f.toString() + ": " + f.getLong(ref));
                    else
                        sdebug(f.toString() + "(" + fH.name + "): " + f.getLong(ref) * fH.multiplier);
                }
                else if(f.getType() == double.class)
                {    
                    if(fH == null)
                        sdebug(f.toString() + ": " +  f.getDouble(ref));
                    else
                        sdebug(f.toString() + "(" + fH.name + "): " +  f.getDouble(ref));
                }
                else if(f.getType() == float.class)
                {
                    if(fH == null)
                        sdebug(f.toString() + ": " + f.getFloat(ref));
                    else
                        sdebug(f.toString() + "(" + fH.name + "): " + f.getFloat(ref));
                }
                else if(f.getType() == String.class)
                {
                    if(fH == null)
                        sdebug(f.toString() + ": " + (String)f.get(ref));
                    else
                        sdebug(f.toString() + "(" + fH.name + "): " + (String)f.get(ref));
                }
                else if(f.getType() == boolean.class)
                {
                    if(fH == null)
                        sdebug(f.toString() + ": " + (f.getBoolean(ref) ? "true" : "false"));
                    else
                        sdebug(f.toString() + "(" + fH.name + "): " + (f.getBoolean(ref) ? "true" : "false"));
                }
                else
                {
                    if(fH == null)
                        sdebug(f.toString());
                    else
                        sdebug(f.toString() + "(" + fH.name + ")");
                }
            } 
            catch (IllegalArgumentException | IllegalAccessException ex) {
                sdebug("ERROR: " + ex.toString());
                Logger.getLogger(RSClassWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
