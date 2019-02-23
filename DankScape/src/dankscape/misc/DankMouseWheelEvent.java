/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

import java.awt.Component;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author Pieterjan
 */
public class DankMouseWheelEvent extends MouseWheelEvent
{
    public DankMouseWheelEvent(Component source, int id, int modifiers, int x, int y, int scrollType, int scrollAmount, int wheelRotation)
    {
        super(source, id, System.currentTimeMillis(), modifiers, x, y, 1, false, scrollType, scrollAmount, wheelRotation);
    }
}
