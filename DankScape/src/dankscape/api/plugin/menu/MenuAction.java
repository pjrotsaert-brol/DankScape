/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api.plugin.menu;

import dankscape.loader.AppletLoader;
import dankscape.nativeinterface.NativeInterface;
import java.util.ArrayList;

/**
 *
 * @author Pieterjan
 */
public class MenuAction
{
    private static int cbIdCounter = 0;
    
    private final int id;
    private boolean state = false;
    
    final ArrayList<ActionToggledListener> toggledListeners = new ArrayList();
    final ArrayList<ActionTriggeredListener> triggeredListeners = new ArrayList();
    
    public enum Alignment
    {
        ALIGN_LEFT,
        ALIGN_RIGHT
    }
    
    public MenuAction(Menu parent, String title, ActionToggledListener listener, boolean defaultState)
    {
        if(Thread.currentThread().getId() != AppletLoader.getSingleton().getNativeGuiThreadId())
            throw new IllegalThreadStateException("You may only create actions and menus on the native gui thread!");
        
        toggledListeners.add(listener);
        state = defaultState;
        id = NativeInterface.createAction(title, parent.getId(), "actionToggledCallback", cbIdCounter, true, defaultState);
        
        NativeInterface.bindActionCallback(cbIdCounter, this);
        cbIdCounter++;
    }
    
    public MenuAction(Menu parent, String title, ActionToggledListener listener)
    {
        if(Thread.currentThread().getId() != AppletLoader.getSingleton().getNativeGuiThreadId())
            throw new IllegalThreadStateException("You may only create actions and menus on the native gui thread!");
        
        toggledListeners.add(listener);
        state = false;
        id = NativeInterface.createAction(title, parent.getId(), "actionToggledCallback", cbIdCounter, true, false);
        
        NativeInterface.bindActionCallback(cbIdCounter, this);
        cbIdCounter++;
    }
    
    public MenuAction(Menu parent, String title, ActionTriggeredListener listener)
    {
        if(Thread.currentThread().getId() != AppletLoader.getSingleton().getNativeGuiThreadId())
            throw new IllegalThreadStateException("You may only create actions and menus on the native gui thread!");
        
        triggeredListeners.add(listener);
        state = false;
        id = NativeInterface.createAction(title, parent.getId(), "actionTriggeredCallback", cbIdCounter, false, false);
        
        NativeInterface.bindActionCallback(cbIdCounter, this);
        cbIdCounter++;
    }
    
    public void addOnTriggeredListener(ActionTriggeredListener listener)
    {
        triggeredListeners.add(listener);
    }
    
    public void addOnToggledListener(ActionToggledListener listener)
    {
        toggledListeners.add(listener);
    }
    
    public void onTriggered()
    {
        for(ActionTriggeredListener listener : triggeredListeners)
            listener.triggered();
    }
    
    public void onToggled(boolean state)
    {
        if(state == this.state)
            return;
        
        this.state = state;
        for(ActionToggledListener listener : toggledListeners)
            listener.toggled(state);
    }
    
    public boolean getState()
    {
        return state;
    }
    
    public void setState(boolean state)
    {
        onToggled(state);
        this.state = state;
        NativeInterface.setActionState(id, state); // This is actually a threadsafe, queued call. (the only one at that.)
    }
    
    public void addToToolbar(Alignment align)
    {
        if(Thread.currentThread().getId() != AppletLoader.getSingleton().getNativeGuiThreadId())
            throw new IllegalThreadStateException("You may only create actions and menus on the native gui thread!");
        NativeInterface.addActionToToolBar(id, (align == Alignment.ALIGN_LEFT ? 0 : 1));
    }
    
    
}
