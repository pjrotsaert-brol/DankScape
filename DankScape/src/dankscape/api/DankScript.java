/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import dankscape.api.internal.ActionContext;
import dankscape.misc.DebugWriter;
import java.awt.Graphics;

/**
 *
 * @author Pieterjan
 */
public abstract class DankScript extends DebugWriter 
{
    private boolean _isRunning = false;
    private boolean _isPainting = false;
    private boolean _dankScriptFinished = false;
    private String _scriptName = "Script";
    private String _scriptAuthor = "Anonymous";
    private String _scriptVersion = "1.0";
    private String _description = "No description available.";
    private String _customHTMLFile = "";
    private String _customCSSFile = "";
    
    private Thread thread;
    
    public DankScript()
    {
        setScriptName(this.getClass().getSimpleName());
    }
    
    // NOTE: Methods with an underscore (_) prefix are NOT supposed to be used by users!
    
    public void _execute(Thread t)
    {
        if(_isRunning)
        {
            print("ERROR: Do not call execute() from within the script!");
            return;
        }
        thread = t;
        _isRunning = true;
        try
        {
            run();
        }
        catch(Exception ex)
        {
            StackTraceElement[] stack = ex.getStackTrace();
            String stackTrace = "";
            for(StackTraceElement i : stack)
                stackTrace += "    at " + i.toString() + "\n";
            print("FATAL ERROR: " + ex.toString() + "\n" + stackTrace);
        }
        _dankScriptFinished = true;
    }
    
    public void _paint(Graphics g)
    {
        if(_isPainting)
        {
            print("ERROR: Do not call _paint() from within the script!");
            return;
        }
        _isPainting = true;
        
        try
        {
            paint(g);
        }
        catch(Exception ex)
        {
            StackTraceElement[] stack = ex.getStackTrace();
            String stackTrace = "";
            for(StackTraceElement i : stack)
                stackTrace += "    at " + i.toString() + "\n";
            print("ERROR: " + ex.toString() + "\n" + stackTrace);
        }
        _isPainting = false;
    }
    
    public boolean _isFinished()
    {
        return _dankScriptFinished;
    }
    
    /**** Overridable functions ****/
    
    protected abstract void run();
    protected void paint(Graphics g){}
    
    
    
    /**** Utility functions ****/
    public Thread getThread()
    {
        return thread;
    }
    
    public String getScriptName()
    {
        return _scriptName;
    }
    
    public String getAuthor()
    {
        return _scriptAuthor;
    }
    
    public String getVersion()
    {
        return _scriptVersion;
    }
    public String getDescription()
    {
        return _description;
    }
    public String getCustomHTML()
    {
        return _customHTMLFile;
    }
    public String getCustomCSS()
    {
        return _customCSSFile;
    }
    
    protected ActionContext actions()
    {
        return ActionContext.get();
    }
    
    protected int time()
    {
        return (int)System.currentTimeMillis();
    }
    
    public void setScriptName(String name)
    {
        _scriptName = name;
    }
    
    public void setAuthor(String s)
    {
        _scriptAuthor = s;
    }
    
    public void setVersion(String s)
    {
        _scriptVersion = s;
    }
    public void setDescription(String s)
    {
        _description = s;
    }
    public void setCustomHTML(String s)
    {
        _customHTMLFile = s;
    }
    public void setCustomCSS(String s)
    {
        _customCSSFile = s;
    }
    
    protected void printf(String format, Object... args)
    {
        debug(String.format(format, args));
    }
    protected void print(String text)
    {
        debug(text);
    }
    protected void sleep(int millis)
    {
        Misc.sleep((long)millis);
    }
    protected void sleep(int minMs, int maxMs)
    {
        Misc.sleep((int)Misc.random(minMs, maxMs));
    }
    
    protected static int random(int min, int max)
    {
        return Misc.random(min, max);
    }
    
    protected static double random()
    {
        return Misc.random();
    }
    
    @Override 
    public final String toString()
    {
        return _scriptName;
    }

}
