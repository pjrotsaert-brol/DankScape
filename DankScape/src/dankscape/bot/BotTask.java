/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot;

import dankscape.misc.DebugWriter;
import java.awt.Graphics;

/**
 *
 * @author Pieterjan
 */
public abstract class BotTask extends DebugWriter
{
    private boolean finished = false, errorOccurred = false;
    String errMsg, description;
    
    public static enum Status 
    {
        READY,
        PENDING,
        RUNNING,
        FINISHED
    }
    
    private Status status = Status.READY;
    
    public BotTask() 
    {
        setDescription(this.getClass().getSimpleName());
    }
    
    protected void setDescription(String text)
    {
        description = text;
    }
    
    public void setStatus(Status status)
    {
        this.status = status;
    }
    
    public Status getStatus()
    {
        return status;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public boolean hasFinished()
    {
        return finished || errorOccurred;
    }
    public boolean hasError()
    {
        return errorOccurred;
    }
    public String getErrorMessage()
    {
        return errMsg;
    }
    
    protected void exit()
    {
        finished = true;
        setStatus(Status.FINISHED);
    }
    protected void exitError(String errorMsg)
    {
        errMsg = errorMsg;
        errorOccurred = true;
        exit();
    }
    
    public abstract void update();
    public void paint(Graphics g){}
    
}
