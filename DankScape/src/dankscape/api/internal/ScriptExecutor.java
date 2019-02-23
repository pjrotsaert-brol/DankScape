/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api.internal;

import dankscape.api.DankScript;

/**
 *
 * @author Pieterjan
 */
public class ScriptExecutor implements Runnable
{
    DankScript script;
    public ScriptExecutor(DankScript script)
    {
        this.script = script;
    }
    
    @Override
    public void run()
    {
        script._execute(Thread.currentThread());
    }
    
}
