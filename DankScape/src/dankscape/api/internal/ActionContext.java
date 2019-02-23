/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api.internal;

import com.sun.glass.events.KeyEvent;
import dankscape.api.Input;
import dankscape.api.Misc;
import dankscape.bot.BotTask;
import dankscape.bot.BotTaskResult;
import dankscape.bot.tasks.*;
import dankscape.bot.TaskScheduler;
import java.awt.Point;
import java.util.function.Predicate;

/**
 *
 * @author Pieterjan
 */
public class ActionContext
{
    private boolean valid = true;
    
    public ActionContext()
    {
    }
    
    public static ActionContext get() // Just an alias for 'new ActionContext()' that looks a bit cleaner :)
    {
        return new ActionContext();
    }
    
    public boolean isValid()
    {
        return valid;
    }
    
    private ActionContext runTask(BotTask task)
    {
        if(!valid)
            return this;
        TaskScheduler.get().pushTask(task);
        BotTaskResult result = null;
        
        while(result == null)
        {
            result = TaskScheduler.get().takeTaskResult(task);
            Misc.sleep(1);
        }
         
        if(!result.successful())
            valid = false;
        return this;
    }
    
    /********** Actions **********/
    
    public ActionContext sleep(int time)
    {
        Misc.sleep(time);
        return this;
    }
    
    public ActionContext sleep(int min, int max)
    {
        Misc.sleep(Misc.random(min, max));
        return this;
    }
    
    public ActionContext moveMouse(int x, int y)
    {
        if(Input.mouseX == x && Input.mouseY == y)
            return this;
        return runTask(new MouseMoveTask(x, y));
    }
    public ActionContext moveMouse(Point p)
    {
        return moveMouse(p.x, p.y);
    }
    
    public ActionContext scrollUp(int nTimes)
    {
        return runTask(new MouseScrollTask(nTimes, Input.SCROLLUP));
    }
    
    public ActionContext scrollDown(int nTimes)
    {
        return runTask(new MouseScrollTask(nTimes, Input.SCROLLDOWN));
    }
    
    public ActionContext scrollUp(Predicate<Object> p, Object userdata)
    {
        return runTask(new MouseScrollTask(p, userdata, Input.SCROLLUP));
    }
    
    public ActionContext scrollDown(Predicate<Object> p, Object userdata)
    {
        return runTask(new MouseScrollTask(p, userdata, Input.SCROLLDOWN));
    }
    
    public ActionContext clickLeft(Point p)
    {
        return clickLeft(p.x, p.y);
    }
    public ActionContext clickRight(Point p)
    {
        return clickRight(p.x, p.y);
    }
    public ActionContext clickMiddle(Point p)
    {
        return clickMiddle(p.x, p.y);
    }
    
    public ActionContext clickLeft(int x, int y)
    {
        if(Input.mouseX != x || Input.mouseY != y)
            moveMouse(x, y);
        return runTask(new MouseClickTask(Input.LEFTBUTTON));
    }
    
    public ActionContext clickRight(int x, int y)
    {
        if(Input.mouseX != x || Input.mouseY != y)
            moveMouse(x, y);
        return runTask(new MouseClickTask(Input.RIGHTBUTTON));
    }
    
    public ActionContext clickMiddle(int x, int y)
    {
        if(Input.mouseX != x || Input.mouseY != y)
            moveMouse(x, y);       
        return runTask(new MouseClickTask(Input.MIDDLEBUTTON));
    }
    
    public ActionContext pressKey(char charCode)
    {
        return runTask(new KeyPressTask(charCode));
    }
    public ActionContext pressKey(int keyCode)
    {
        return runTask(new KeyPressTask(keyCode));
    }
    public ActionContext pressKey(char charCode, int holdMin, int holdMax)
    {
        return runTask(new KeyPressTask(charCode, holdMin, holdMax));
    }
    public ActionContext pressKey(int keyCode, int holdMin, int holdMax)
    {
        return runTask(new KeyPressTask(keyCode, holdMin, holdMax));
    }
     public ActionContext pressKey(char charCode, Predicate p)
    {
        return runTask(new KeyPressTask(charCode, p, 0, 0));
    }
    public ActionContext pressKey(int keyCode, Predicate p)
    {
        return runTask(new KeyPressTask(keyCode, p, 0, 0));
    }
    public ActionContext pressKey(char charCode, Predicate p, int postholdMin, int postholdMax)
    {
        return runTask(new KeyPressTask(charCode, p, postholdMin, postholdMax));
    }
    public ActionContext pressKey(int keyCode, Predicate p, int postholdMin, int postholdMax)
    {
        return runTask(new KeyPressTask(keyCode, p, postholdMin, postholdMax));
    }
    public ActionContext pressKey(char charCode, Predicate p, Object userdata, int postholdMin, int postholdMax)
    {
        return runTask(new KeyPressTask(charCode, p, userdata, postholdMin, postholdMax));
    }
    public ActionContext pressKey(int keyCode, Predicate p, Object userdata, int postholdMin, int postholdMax)
    {
        return runTask(new KeyPressTask(keyCode, p, userdata, postholdMin, postholdMax));
    }
    
    public ActionContext pressKey(char charCode, Predicate p, Object userdata)
    {
        return runTask(new KeyPressTask(charCode, p, userdata, 0, 0));
    }
    public ActionContext pressKey(int keyCode, Predicate p, Object userdata)
    {
        return runTask(new KeyPressTask(keyCode, p, userdata, 0, 0));
    }
    
    public ActionContext enterText(String text)
    {
        for(int i = 0;i < text.length();i++)
            pressKey(text.charAt(i)).sleep(0, 104);
        return this;
    }
    
    public ActionContext pressBackspace(int nTimes)
    {
        for(int i = 0;i < nTimes;i++)
            pressKey(KeyEvent.VK_BACKSPACE).sleep(0, 20);
        return this;
    }
    
}
