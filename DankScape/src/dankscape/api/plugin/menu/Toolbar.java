/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api.plugin.menu;

import dankscape.loader.AppletLoader;
import dankscape.nativeinterface.NativeInterface;

/**
 *
 * @author Pieterjan
 */
public class Toolbar
{
    public static void addSeparator()
    {
        if(Thread.currentThread().getId() != AppletLoader.getSingleton().getNativeGuiThreadId())
            throw new IllegalThreadStateException("You may only create actions and menus on the native gui thread!");
        NativeInterface.addToolBarSeparator();
    }
}
