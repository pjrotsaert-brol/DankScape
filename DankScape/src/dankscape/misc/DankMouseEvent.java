/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

import java.awt.Component;
import java.awt.event.MouseEvent;

/**
 *
 * @author Pieterjan
 */
public class DankMouseEvent extends MouseEvent
{
    public DankMouseEvent(Component source, int id, int modifiers, int x, int y, int nClicks, int button)
    {
        super(source, id, System.currentTimeMillis() + 10, modifiers, x, y, nClicks, false, button);
        
    }
}
