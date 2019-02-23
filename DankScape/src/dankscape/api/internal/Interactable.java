/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api.internal;

import dankscape.api.Game;
import dankscape.api.Input;
import dankscape.api.Misc;
import dankscape.api.rs.RSClient;
import dankscape.misc.DebugWriter;
import java.awt.Point;
import java.util.List;

/**
 *
 * @author Pieterjan
 */
public abstract class Interactable extends DebugWriter
{
    private static final int MAX_HOVER_TRIES = 15;
    
    // Name (e.g. NPC name) and Action (e.g. Talk-to) of the currently attempted interaction.
    private String attemptedActionTarget = "";
    private String attemptedAction = "";
            
    public abstract Point getRandomPoint();
    public abstract String getName();
    public abstract boolean isOnScreen();
    public abstract void bringOnScreen();
    protected abstract boolean isHovering();
    public abstract int getX();
    public abstract int getY();
    
    private void moveMouseToTarget()
    {
        int nTries = 0;
        Point p;
        
        if(!isOnScreen() || Misc.random(0, 50) < 4)
                bringOnScreen();
        
        p = getRandomPoint();
            ActionContext.get().moveMouse(p);
            
        while(!isHovering() && nTries < MAX_HOVER_TRIES)
        {
            if(!isOnScreen())
                bringOnScreen();
            
            p = getRandomPoint();
            ActionContext.get().moveMouse(p);
            nTries++;
        }
        
        if(!isHovering())
            throw new IllegalStateException("Unable to hover over target!");
    }
    
    protected String getAttemptedAction()
    {
        return attemptedAction;
    }
    
    protected String getAttemptedActionTarget()
    {
        return attemptedActionTarget;
    }
    
    protected int getOptionIndexDisambiguous()
    {
        int i = 0;
        List<Integer> xOpts = Game.getContextActionParams0();
        List<Integer> yOpts = Game.getContextActionParams1();
        int curX = getX() - RSClient.getBaseX();
        int curY = getY() - RSClient.getBaseY();
        for(String opt : Game.getContextOptions())
        {  
            if(opt.toLowerCase().contains(attemptedActionTarget.toLowerCase()) && 
                    opt.toLowerCase().contains(attemptedAction.toLowerCase()) &&
                    ((xOpts.get(i) == curX && yOpts.get(i) == curY) || (getX() == -1 && getY() == -1)))
                return i;
            i++;
        }
        return -1;
    }
    
    protected int getOptionIndex()
    {
        int i = 0;
        int match = -1;
        int nMatches = 0;
        for(String opt : Game.getContextOptions())
        {  
            if(opt.toLowerCase().contains(attemptedActionTarget.toLowerCase()) && 
                    opt.toLowerCase().contains(attemptedAction.toLowerCase()))
            {
                match = i;
                nMatches++;
            }
            i++;
        }
        if(nMatches == 1)
            return match;
        else if(nMatches > 1)
            return getOptionIndexDisambiguous();
        return -1;
    }
    
    public ActionContext interact(String actionName, String targetName)
    {
        attemptedActionTarget = targetName;
        attemptedAction = actionName;
        
        moveMouseToTarget();   
        int idx = getOptionIndex();
        while(idx == -1)
        {
            moveMouseToTarget();
            idx = getOptionIndex();
        }
        
        if(idx != -1)
        {
            if(idx == 0) // The action is the first one in the menu, we can just leftclick
                return ActionContext.get().clickLeft(Input.getMousePos());
            else // We have to open the context menu to find the action
            {
                ActionContext.get().clickRight(Input.getMousePos()).sleep(153, 267);
                if(!Game.isContextMenuOpen())
                    throw new IllegalStateException("Unable to open action contextmenu!");
                
                while(true)
                {
                    ActionContext.get().moveMouse(Misc.randomPointInRect(Game.getContextOptionBounds(idx)));
                    if(!Game.isContextMenuOpen())
                        ActionContext.get().clickRight(Input.getMousePos()).sleep(153, 267);
                    else
                        break;
                }

                return ActionContext.get().clickLeft(Input.getMousePos());
            }
        }
        
        throw new IllegalArgumentException("Action/Name combination not found!");
    }
    
    public ActionContext interact(String actionName)
    {
        return interact(actionName, getName());
    }
    
    public ActionContext click()
    {
        moveMouseToTarget();
        return ActionContext.get().clickLeft(Input.getMousePos());
    }
}
