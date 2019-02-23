/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot.tasks;


import dankscape.api.Input;
import dankscape.api.Misc;
import dankscape.bot.BotTask;
import java.awt.event.KeyEvent;
import java.util.function.Predicate;

/**
 *
 * @author Pieterjan
 */
public class KeyPressTask extends BotTask
{
    private static final int OUTLIER_PCT = 9;
    
    private static final float SPEEDMULT = 0.12f;
    
    private static final int PRESS_SPEED_MAX = (int)(137 * SPEEDMULT);
    private static final int PRESS_SPEED_MIN = (int)(91 * SPEEDMULT);
    
    private static final int PRESS_SPEED_MAX_OUTLIER = (int)(174 * SPEEDMULT);
    private static final int PRESS_SPEED_MIN_OUTLIER = (int)(64 * SPEEDMULT);
    
    private int postwaitMin = 0;
    private int postwaitMax = 0;
    
    private static final int OP_PRESS = 0, OP_RELEASE = 1, OP_WAIT = 2;
    
    private boolean started = false;
    private int timeToWait = 0, curOp = OP_PRESS;
    private long tStart = 0;
    
    private char charCode = KeyEvent.CHAR_UNDEFINED;
    private int keyCode = KeyEvent.VK_UNDEFINED;
    
    private boolean customHoldTime = false;
    private int holdMin, holdMax;
    private Predicate holdPredicate = null;
    private Object predicateUserData = null;

    public KeyPressTask(int keyCode)
    {
        this.keyCode = keyCode;
    }
    public KeyPressTask(char charCode)
    {
        this.charCode = charCode;
    }
    public KeyPressTask(int keyCode, int holdMin, int holdMax)
    {
        this.keyCode = keyCode;
        this.holdMax = holdMax;
        this.holdMin = holdMin;
        this.customHoldTime = true;
    }
    public KeyPressTask(char charCode, int holdMin, int holdMax)
    {
        this.charCode = charCode;
        this.holdMax = holdMax;
        this.holdMin = holdMin;
        this.customHoldTime = true;
    }
    public KeyPressTask(int keyCode, Predicate p, int postHoldMin, int postHoldMax)
    {
        this.keyCode = keyCode;
        this.holdPredicate = p;
        this.postwaitMin = postHoldMin;
        this.postwaitMax = postHoldMax;
    }
    public KeyPressTask(char charCode, Predicate p, int postHoldMin, int postHoldMax)
    {
        this.charCode = charCode;
        this.holdPredicate = p;
        this.postwaitMin = postHoldMin;
        this.postwaitMax = postHoldMax;
    }
    
    public KeyPressTask(int keyCode, Predicate p, Object userdata, int postHoldMin, int postHoldMax)
    {
        this.keyCode = keyCode;
        this.holdPredicate = p;
        this.postwaitMin = postHoldMin;
        this.postwaitMax = postHoldMax;
        this.predicateUserData = userdata;
    }
    public KeyPressTask(char charCode, Predicate p, Object userdata, int postHoldMin, int postHoldMax)
    {
        this.charCode = charCode;
        this.holdPredicate = p;
        this.postwaitMin = postHoldMin;
        this.postwaitMax = postHoldMax;
        this.predicateUserData = userdata;
    }
    
    private int genTime()
    {
        boolean isOutlier = Misc.random(0, 100) <= OUTLIER_PCT;
        if(isOutlier)
            return Misc.random(PRESS_SPEED_MIN_OUTLIER, PRESS_SPEED_MAX_OUTLIER);
        else
            return Misc.random(PRESS_SPEED_MIN, PRESS_SPEED_MAX);
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
        
        boolean predicateIsTrue = false;
        if(curOp == OP_RELEASE && holdPredicate != null)
            predicateIsTrue = holdPredicate.test(predicateUserData);
        
        
        long tCurrent = System.currentTimeMillis();
        if((tCurrent - tStart > (long)timeToWait && holdPredicate == null || curOp != OP_RELEASE) || predicateIsTrue)
        {
            if(curOp == OP_PRESS)
            {
                curOp = OP_RELEASE;
                Input.pressKey(charCode, keyCode);
                if(charCode != KeyEvent.CHAR_UNDEFINED)
                    Input.typeKey(charCode, keyCode);
                
                if(customHoldTime)
                {
                    tStart = System.currentTimeMillis();
                    timeToWait = Misc.random(holdMin, holdMax);
                }
                else if(holdPredicate != null)
                {
                    tStart = 0;
                    timeToWait = 0x7FFFFFFF;
                }
                else 
                    started = false;
            }
            else if(curOp == OP_RELEASE)
            {
                curOp = OP_WAIT;
                Input.releaseKey(charCode, keyCode);
                tStart = System.currentTimeMillis();
                timeToWait = Misc.random(postwaitMin, postwaitMax);
            }
            else
                exit();
        }
    }
    
}
