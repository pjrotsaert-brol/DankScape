/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.loader;

import dankscape.misc.ResizeRequestedEvent;
import java.awt.AWTEvent;
import java.awt.Frame;

/**
 *
 * @author Pieterjan
 */

public class AppletFrame extends Frame
{
    public AppletFrame()
    {
        super("Old School RuneScape");
    }
    
    @Override
    public void processEvent(AWTEvent event)
    {
        if(event instanceof ResizeRequestedEvent)
        {
            int w = ((ResizeRequestedEvent)event).getWidth();
            int h = ((ResizeRequestedEvent)event).getHeight();
            this.setSize(w, h);
        }
        else 
            super.processEvent(event);
    }
}
