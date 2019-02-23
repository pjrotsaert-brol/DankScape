/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot.tasks;

import dankscape.api.Input;
import dankscape.api.Misc;
import dankscape.bot.BotTask;

/**
 *
 * @author Pieterjan
 */
public class MouseClickTask extends BotTask
{
    private static final int OUTLIER_PCT = 9;
    
    private static final int CLICKSPEED_MAX = 117;
    private static final int CLICKSPEED_MIN = 41;
    
    private static final int CLICKSPEED_MAX_OUTLIER = 224;
    private static final int CLICKSPEED_MIN_OUTLIER = 74;
    
    private static final int POSTWAIT_MIN = 0;
    private static final int POSTWAIT_MAX = 0;
    
    private static final int OP_PRESS = 0, OP_RELEASE = 1, OP_WAIT = 2;
    
    
    private boolean started = false;
    private int button = Input.LEFTBUTTON, timeToWait = 0, curOp = OP_PRESS;
    private long tStart = 0;
    

    public MouseClickTask(int button)
    {
        this.button = button;
    }
    
    private int genTime()
    {
        boolean isOutlier = Misc.random(0, 100) <= OUTLIER_PCT;
        if(isOutlier)
            return Misc.random(CLICKSPEED_MIN_OUTLIER, CLICKSPEED_MAX_OUTLIER);
        else
            return Misc.random(CLICKSPEED_MIN, CLICKSPEED_MAX);
    }
    
    @Override
    public void update()
    {
        if(!started)
        {
            tStart = System.currentTimeMillis();
            timeToWait = genTime();
            started = true;
        }
        
        long tCurrent = System.currentTimeMillis();
        if(tCurrent - tStart > (long)timeToWait)
        {
            if(curOp == OP_PRESS)
            {
                curOp = OP_RELEASE;
                started = false;
                Input.mousePress(button);
            }
            else if(curOp == OP_RELEASE)
            {
                curOp = OP_WAIT;
                Input.mouseRelease(button);
                tStart = System.currentTimeMillis();
                timeToWait = Misc.random(POSTWAIT_MIN, POSTWAIT_MAX);
            }
            else
                exit();
        }
    }
    
}
