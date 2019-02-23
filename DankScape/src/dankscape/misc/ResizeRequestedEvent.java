/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

import java.awt.AWTEvent;

/**
 *
 * @author Pieterjan
 */
public class ResizeRequestedEvent extends AWTEvent
{
    private final int w, h;

    public ResizeRequestedEvent(Object source, int id, int w, int h)
    {
        super(source, id);
        this.w = w;
        this.h = h;
    }
    
    public int getWidth()
    {
        return w;
    }
    
    public int getHeight()
    {
        return h;
    }
}

