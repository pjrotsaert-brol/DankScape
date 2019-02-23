/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot.tasks;

import dankscape.api.Input;
import dankscape.api.Misc;
import dankscape.bot.BotTask;
import java.util.function.Predicate;

/**
 *
 * @author Pieterjan
 */
public class MouseScrollTask extends BotTask
{
    private static final int SCROLLINTERVAL_MIN = 24;
    private static final int SCROLLINTERVAL_MAX = 77;
    
    private static final int SCROLLSTILLWAIT_MIN = 4;
    private static final int SCROLLSTILLWAIT_MAX = 8;
    
    private static final int TIMETOWAIT_MIN = 161;
    private static final int TIMETOWAIT_MAX = 267;
    
    
    private int timesLeft = 0, timesTillWaitLeft = 0;
    private int timeToWait = 0;
    private long tLastScroll = 0, tWaitStart = 0;
    private int currentInterval = 0;
    private int scrollMode = 1;
    Predicate<Object> predicate;
    Object userdata;
    
    public MouseScrollTask(int nTimes, int mode)
    {
        scrollMode = mode;
        timesLeft = nTimes;
        currentInterval = Misc.random(SCROLLINTERVAL_MIN, SCROLLINTERVAL_MAX);
        timesTillWaitLeft = Misc.random(SCROLLSTILLWAIT_MIN, SCROLLSTILLWAIT_MAX);
        timeToWait = Misc.random(TIMETOWAIT_MIN, TIMETOWAIT_MAX);
    }
    
    public MouseScrollTask(Predicate<Object> p, Object userdata, int mode)
    {
        this.userdata = userdata;
        scrollMode = mode;
        predicate = p;
        timesLeft = 99999;
        currentInterval = Misc.random(SCROLLINTERVAL_MIN, SCROLLINTERVAL_MAX);
        timesTillWaitLeft = Misc.random(SCROLLSTILLWAIT_MIN, SCROLLSTILLWAIT_MAX);
        timeToWait = Misc.random(TIMETOWAIT_MIN, TIMETOWAIT_MAX);
    }
    
    @Override
    public void update()
    {
        long tCurrent = System.currentTimeMillis();
        if(timesTillWaitLeft <= 0)
        {
            if(timeToWait == 0)
            {
                timeToWait = Misc.random(TIMETOWAIT_MIN, TIMETOWAIT_MAX); 
                tWaitStart = System.currentTimeMillis();
            }
            
            if(tCurrent - tWaitStart >= timeToWait)
            {
                timesTillWaitLeft = Misc.random(SCROLLSTILLWAIT_MIN, SCROLLSTILLWAIT_MAX);
                timeToWait = 0;
            }
            else
                return;
        }

        boolean predicateIsSatisfied = false;
        if(predicate != null)
            predicateIsSatisfied = predicate.test(userdata);
        
        if(tCurrent - tLastScroll >= currentInterval && timesLeft > 0 && !predicateIsSatisfied)
        {
            Input.scroll(scrollMode);
            tLastScroll = tCurrent;
            currentInterval = Misc.random(SCROLLINTERVAL_MIN, SCROLLINTERVAL_MAX);
            timesLeft--;
            timesTillWaitLeft--;
        }
        
        if(timesLeft <= 0 || predicateIsSatisfied)
            exit();
    }
}
