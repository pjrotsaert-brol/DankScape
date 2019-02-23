/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import dankscape.loader.AppletLoader;
import dankscape.misc.DankKeyEvent;
import dankscape.misc.DankMouseEvent;
import dankscape.misc.DankMouseWheelEvent;
import java.awt.Point;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author Pieterjan
 */
public class Input // Contains basic helper functions for sending input events to the RS Applet
{
    public static final int LEFTBUTTON = 0;
    public static final int RIGHTBUTTON = 1;
    public static final int MIDDLEBUTTON = 2;
    
    public static final int SCROLLUP = -1;
    public static final int SCROLLDOWN = 1;
    
    public static int mouseX = 200, mouseY = 200;
    
    public static boolean userMovedMouse = false; // set to true whenever the user manually moves the mouse, this is then set to false again by a MouseMoveTask
    
    public static boolean leftDown = false, middleDown = false, rightDown = false;
    
    public static Point getMousePos()
    {
        return new Point(mouseX, mouseY);
    }
    public static void typeKey(char charCode, int keyCode)
    {
        AppletLoader.getSingleton().getApplet().dispatchEvent(new DankKeyEvent(AppletLoader.getSingleton().getGameCanvas(), 
                    KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, keyCode, charCode));
    }
    public static void typeKey(char charCode)
    {
        typeKey(charCode, KeyEvent.VK_UNDEFINED);
    }
    public static void typeKey(int keyCode)
    {
        typeKey(KeyEvent.CHAR_UNDEFINED, keyCode);
    }
    
    public static void pressKey(char charCode, int keyCode)
    {
        AppletLoader.getSingleton().getApplet().dispatchEvent(new DankKeyEvent(AppletLoader.getSingleton().getGameCanvas(), 
                    KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyCode, charCode));
    }
    public static void pressKey(char charCode)
    {
        pressKey(charCode, KeyEvent.VK_UNDEFINED);
    }
    public static void pressKey(int keyCode)
    {
        pressKey(KeyEvent.CHAR_UNDEFINED, keyCode);
    }
    
    public static void releaseKey(char charCode, int keyCode)
    {
        AppletLoader.getSingleton().getApplet().dispatchEvent(new DankKeyEvent(AppletLoader.getSingleton().getGameCanvas(), 
                    KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, keyCode, charCode));
    }
    public static void releaseKey(char charCode)
    {
        releaseKey(charCode, KeyEvent.VK_UNDEFINED);
    }
    public static void releaseKey(int keyCode)
    {
        releaseKey(KeyEvent.CHAR_UNDEFINED, keyCode);
    }
    
    public static void mousePress(int button, int x, int y)
    {
        if(x < 0 || x > AppletLoader.getSingleton().getGameCanvas().getWidth() || y < 0 || y > AppletLoader.getSingleton().getGameCanvas().getHeight())
            return;
        
        MouseEvent e = new DankMouseEvent(AppletLoader.getSingleton().getGameCanvas(),
                MouseEvent.MOUSE_PRESSED, 0, x, y, 1, getAWTButton(button));
        
        //AppletLoader.getSingleton().getApplet().dispatchEvent(e);
        AppletLoader.getSingleton().getGameCanvas().sendMouseEvent(e);
    }
    
    public static void mouseRelease(int button, int x, int y)
    {
        if(x < 0 || x > AppletLoader.getSingleton().getGameCanvas().getWidth() || y < 0 || y > AppletLoader.getSingleton().getGameCanvas().getHeight())
            return;
        
        MouseEvent e = new DankMouseEvent(AppletLoader.getSingleton().getGameCanvas(),
                MouseEvent.MOUSE_RELEASED, 0, x, y, 1, getAWTButton(button));
        
        //AppletLoader.getSingleton().getApplet().dispatchEvent(e);
        AppletLoader.getSingleton().getGameCanvas().sendMouseEvent(e);
    }
    
    public static void moveMouse(int x, int y)
    {
        if(x < 0 || x > AppletLoader.getSingleton().getGameCanvas().getWidth() || y < 0 || y > AppletLoader.getSingleton().getGameCanvas().getHeight())
            return;
        
        MouseEvent e = new DankMouseEvent(AppletLoader.getSingleton().getGameCanvas(),
                MouseEvent.MOUSE_MOVED, 0, x, y, 0, MouseEvent.NOBUTTON);
        
        //AppletLoader.getSingleton().getApplet().dispatchEvent(e);
        
        AppletLoader.getSingleton().getGameCanvas().sendMouseMotionEvent(e);
    }
    
    public static void scroll(int mode)
    {
        MouseWheelEvent e = new DankMouseWheelEvent(AppletLoader.getSingleton().getGameCanvas(), 
                MouseWheelEvent.MOUSE_WHEEL, 0, mouseX, mouseY, MouseWheelEvent.WHEEL_UNIT_SCROLL, 3, mode);
        
        AppletLoader.getSingleton().getGameCanvas().sendMouseWheelEvent(e);
    }
    
    public static void scrollUp()
    {
        scroll(SCROLLUP);
    }
    
    public static void scrollDown()
    {
        scroll(SCROLLDOWN);
    }
    
    private static int getAWTButton(int button)
    {
        switch (button)
        {
            case LEFTBUTTON:
                return MouseEvent.BUTTON1;
            case RIGHTBUTTON:
                return MouseEvent.BUTTON3;
            case MIDDLEBUTTON:
                return MouseEvent.BUTTON2;
            default:
                return MouseEvent.BUTTON1;
        }
    }
    
    
    
    public static void mousePress(int button)
    {
        mousePress(button, mouseX, mouseY);
    }
    
    public static void mouseRelease(int button)
    {
        mouseRelease(button, mouseX, mouseY);
    }
    
    
    public static void onUserMouseMoved(int x, int y)
    {
        mouseX = x;
        mouseY = y;
        userMovedMouse = true;
        // TODO: trigger some event or whatever
    }
    
}
