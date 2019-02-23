/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot;

import dankscape.api.DankScript;
import dankscape.api.internal.Projection;
import dankscape.api.internal.ScriptExecutor;
import dankscape.api.rs.RSClient;
import java.awt.Graphics;
import dankscape.misc.DebugWriter;

import java.util.ArrayList;
import dankscape.loader.AppletLoader;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Pieterjan
 */
public class TaskScheduler extends DebugWriter
{
    private static final int UI_UPDATE_INTERVAL = 5;
    
    private static final TaskScheduler singleton = new TaskScheduler();
    
    private final ArrayList<BotTask> taskQueue = new ArrayList();
    private final HashMap<BotTask, BotTaskResult> taskResults = new HashMap(); 
    private final Lock taskQueueLock = new ReentrantLock(true);
    private boolean isPaused = false;
    
    
    private DankScript currentScript = null;
    private BotTask currentTask = null;
    
    private Thread paintThread; // Id of the thread that RS uses to update/paint
    
    int uiUpdateCounter = 0;
    
    public TaskScheduler()
    {
        super("Task Scheduler");
    }
    
    public boolean runScript(DankScript script)
    {
        if(currentScript != null)
        {
            debug("ERROR: Could run script: another script is already running!");
            return false;
        }
        
        taskQueueLock.lock();
        try
        {
            currentScript = script;
            
            Thread thread = new Thread(new ScriptExecutor(currentScript));
            thread.start();
        } 
        finally
        {
            taskQueueLock.unlock();
        }
        return true;
    }
    
    public void setPaused(boolean paused)
    {
        if(!paused)
            debug("Resuming current script.");
        else
            debug("Pausing current script.");
        
        isPaused = paused;
    }
    
    public boolean getPaused()
    {
        return isPaused;
    }
    
    public void terminateScript()
    {
        taskQueueLock.lock();
        try
        {
            if(currentScript != null)
            {
                debug("WARNING: Force-terminating scripts can be unsafe.");
                debug("Terminating current script...");
                currentScript.getThread().stop();
                currentScript = null;
            }
            else
                debug("No script is currently running.");
        } finally
        {
            taskQueueLock.unlock();
        }
    }
    
    public void pushTask(BotTask task)
    {
        taskQueueLock.lock();
        try
        {
            taskQueue.add(task);
            task.setStatus(BotTask.Status.PENDING);
        } 
        finally
        {
            taskQueueLock.unlock();
        }
    }
    
    public BotTaskResult takeTaskResult(BotTask task)
    {
        BotTaskResult result;
        taskQueueLock.lock();
        try
        {
            result = taskResults.get(task);
            taskResults.remove(task);
        } 
        finally
        {
            taskQueueLock.unlock();
        }
        return result;
    }
    
    public void update(Graphics g)
    {
        if(paintThread == null)
            paintThread = Thread.currentThread();
        
        if(!AppletLoader.getSingleton().hasLoaded())
            return;
        
        Projection.update();
        
        GenericUpdater.update();
        
        taskQueueLock.lock();
        try
        {
            currentTask = null;
            if(taskQueue.size() > 0)
                currentTask = taskQueue.get(0);
        } 
        finally
        {
            taskQueueLock.unlock();
        }
        
        if(currentTask != null)
        {
            currentTask.setStatus(BotTask.Status.RUNNING);
            if(!isPaused)
            {
                currentTask.update();
                if(currentTask.hasFinished())
                {
                    taskQueueLock.lock();
                    try 
                    {
                        taskQueue.remove(0);
                        taskResults.put(currentTask, new BotTaskResult(currentTask, !currentTask.hasError(), currentTask.getErrorMessage()));
                    } 
                    finally 
                    {
                        taskQueueLock.unlock();
                    }
                    currentTask = null;
                }
            }
        }
        
        if(uiUpdateCounter >= UI_UPDATE_INTERVAL)
        {
            UiUpdater.get().update();
            uiUpdateCounter = 0;
        }
        else 
            uiUpdateCounter++;
        
        DebugRenderer.get().paint(g);
        
        if(currentTask != null)
            currentTask.paint(g);
        
        taskQueueLock.lock();
        if(currentScript != null && !isPaused)
        {
            if(!currentScript._isFinished())
                currentScript._paint(g);
            else
            {
                debug("Script '" + currentScript.getScriptName() + "' has exited.");
                currentScript = null;
            }
        }
        taskQueueLock.unlock();
    }
    
    public DankScript getCurrentScript()
    {
        return currentScript;
    }
    
    public BotTask getCurrentTask()
    {
        return currentTask;
    }
    
    public Thread getPaintThread()
    {
        return paintThread;
    }
    
    public static TaskScheduler get()
    {
        return singleton;
    }
}
