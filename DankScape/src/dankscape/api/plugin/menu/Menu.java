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
public class Menu
{
    final int id;
    public Menu(String title)
    {
        if(Thread.currentThread().getId() != AppletLoader.getSingleton().getNativeGuiThreadId())
            throw new IllegalThreadStateException("You may only create actions and menus on the native gui thread!");
        id = NativeInterface.createMenu(title);
        if(id == -1)
            throw new IllegalStateException("Failed to create Menu!");
    }
    
    public Menu(String title, Menu parent)
    {
        if(Thread.currentThread().getId() != AppletLoader.getSingleton().getNativeGuiThreadId())
            throw new IllegalThreadStateException("You may only create actions and menus on the native gui thread!");
        id = NativeInterface.createMenu(title, parent.getId());
        if(id == -1)
            throw new IllegalStateException("Failed to create Menu!");
    }

    public int getId()
    {
        return id;
    }
    
    public void addSeparator()
    {
        if(Thread.currentThread().getId() != AppletLoader.getSingleton().getNativeGuiThreadId())
            throw new IllegalThreadStateException("You may only create actions and menus on the native gui thread!");
        NativeInterface.addMenuSeparator(id);
    }
}
