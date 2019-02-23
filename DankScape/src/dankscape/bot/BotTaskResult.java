/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot;

/**
 *
 * @author Pieterjan
 */
public class BotTaskResult
{
    private boolean success = true;
    private String errorMsg = "";
    BotTask task = null;
    
    public BotTaskResult(BotTask task, boolean success, String errMsg)
    {
        this.success = success;
        this.errorMsg = errMsg;
        this.task = task;
    }
    
    public BotTaskResult(BotTask task)
    {
        this.task = task;
    }
    
    public boolean successful()
    {
        return success;
    }
    
    public String getErrorMessage()
    {
        return errorMsg;
    }
    
    public BotTask getTask()
    {
        return task;
    }
}
