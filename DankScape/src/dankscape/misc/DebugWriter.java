/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

import dankscape.nativeinterface.NativeInterface;

/**
 *
 * @author Pieterjan
 */
public class DebugWriter
{
    private String tag = "Generic";
    private static String staticTag = "Generic";
    
    public DebugWriter()
    {
        setTag(this.getClass().getName());
    }
    public DebugWriter(String tag)
    {
        setTag(tag);
    }
    
    protected static void setStaticTag(String t)
    {
        staticTag = t;
    }
    
    protected void setTag(String t)
    {
        tag = t;
    }
    
    protected void debug(String s)
    {
        NativeInterface.println(tag, s + "\n");
    }
    
    protected static void sdebug(String s)
    {
        NativeInterface.println(staticTag, s + "\n");
    }
}
