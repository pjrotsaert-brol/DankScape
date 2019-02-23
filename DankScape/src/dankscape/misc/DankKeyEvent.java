/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

import java.awt.Component;
import java.awt.event.KeyEvent;

/**
 *
 * @author Pieterjan
 */
public class DankKeyEvent extends KeyEvent
{
    public DankKeyEvent(Component source, int id, long when, int modifiers, int keyCode, char keyChar)
    {
        super(source, id, when, modifiers, keyCode, keyChar);
    }
}
