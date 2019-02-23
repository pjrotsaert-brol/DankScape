/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import dankscape.api.internal.ActionContext;
import dankscape.api.internal.Projection;
import dankscape.api.rs.RSClient;
import dankscape.bot.GenericUpdater;
import dankscape.loader.AppletLoader;
import dankscape.misc.DebugWriter;
import dankscape.misc.Vec3;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Pieterjan
 */
public class Game extends DebugWriter
{
    public static final int GAMESTATE_BOOTING       = 5;
    public static final int GAMESTATE_LOGINSCREEN   = 10;
    public static final int GAMESTATE_AUTHENTICATOR = 11;
    public static final int GAMESTATE_LOGGINGIN     = 20;
    public static final int GAMESTATE_LOADING       = 25;
    public static final int GAMESTATE_INGAME        = 30;
    public static final int GAMESTATE_CONNLOST      = 40;
    public static final int GAMESTATE_RECONNECTING  = 45;
    
    public static final int LOGINSTATE_START     = 0;
    public static final int LOGINSTATE_PROGRESS1 = 3;
    public static final int LOGINSTATE_PROGRESS2 = 6;
    public static final int LOGINSTATE_FINISHED  = 10;
    
    public static final int CTXMENU_ROWHEIGHT = 15;
    public static final int CTXMENU_MARGIN_H = 3;
    public static final int CTXMENU_MARGIN_V = 1;
    public static final int CTXMENU_HEADERHEIGHT = 19;
    
    private static final int LOOKATMARGIN_TOP = 150;
    private static final int LOOKATMARGIN_BOTTOM = 200;
    
    Login.Status getStatus()
    {
        return Login.getStatus();
    }

    public static boolean isInGame()
    {
        return Login.getStatus() == Login.Status.INGAME;
    }
    
    public static void enterText(String text)
    {
        ActionContext.get().enterText(text);
    }
    
    public static boolean isPlayerMoving()
    {
        return GenericUpdater.isPlayerMoving;
    }
    
    // Moves the mouse to a position in between the chat and inventory, where scrollevents are seen as camera zoom inputs etc.
    public static ActionContext moveMouseToViewportArea() 
    {
        Rectangle rect = new Rectangle(521, (int)Projection.getHeight() - 475, 3, 475);
        if(Misc.isPointInRect(Input.getMousePos(), rect))
            return ActionContext.get();
        Point p = Misc.randomPointInRect(rect);
        return ActionContext.get().moveMouse(p);
    }
    
    public static ActionContext zoomOutFully()
    {
        moveMouseToViewportArea();
        class pDouble 
        {
            long time = 0;
            double value = 0.0;
        }
        pDouble userdata = new pDouble();
        return ActionContext.get().scrollDown(o -> 
        {
            long tCurrent = System.currentTimeMillis();
            pDouble ud = (pDouble)o;
            if(tCurrent - ud.time >= 1000)
            {
                ud.time = tCurrent;
                double prevScale = ud.value;
                ud.value = Projection.getViewportScale();
                return prevScale == ud.value;
            }
            return false;
        } , (Object)userdata);
    }
    
    public static ActionContext lookAt(Vec3 point) // Faces the camera towards a certain point in 3D space
    {
        zoomOutFully();
        
        Vec3 diff = point.subtract(Player.getLocal().getCenter());
        double yawAngle = Math.atan2(diff.z, diff.x) * 180.0 / Math.PI - 90;

        setCameraYaw(yawAngle);
        
        Point p = Projection.project(Projection.transform(point));
        
        class pDouble 
        {
            long time = 0;
            double value = 0.0;
        }
        pDouble userdata = new pDouble();
        
        if(p.y < LOOKATMARGIN_TOP)
        {
            //sdebug("Looking down");
            return ActionContext.get().pressKey(KeyEvent.VK_DOWN, o -> {
                pDouble ud = (pDouble)o;
                double prevPitch = ud.value;
                ud.value = Projection.getCameraPitch();

                return prevPitch == Projection.getCameraPitch() ||
                (Projection.isInFrustum(Projection.transform(point)) && Projection.project(Projection.transform(point)).y >= LOOKATMARGIN_TOP) ||
                Projection.getCameraPitch() - Projection.CAMERAPITCH_MIN <= 5 * Math.PI / 180.0; }, (Object)userdata);
        }
        else if(p.y >= LOOKATMARGIN_TOP + 25)
        {
            //sdebug("Looking up");
            return ActionContext.get().pressKey(KeyEvent.VK_UP, o -> {
                    
                pDouble ud = (pDouble)o;
                double prevPitch = ud.value;
                ud.value = Projection.getCameraPitch();

                return prevPitch == Projection.getCameraPitch() ||
                Projection.project(Projection.transform(point)).y <= LOOKATMARGIN_TOP + 20 ||
                Projection.CAMERAPITCH_MAX - Projection.getCameraPitch() <= 5 * Math.PI / 180.0; }, (Object)userdata); 
        }
        else
            return ActionContext.get();
    }
    
    public static ActionContext setCameraYaw(double angle)
    {
        while(angle < 0.0)
            angle += 360.0;
        while(angle > 360.0)
            angle -= 360.0;
        
        final double resultingAngle = angle;
        
        double curAngle = Projection.getCameraYaw() * 180.0 / Math.PI;
        double diff = angle - curAngle;
        int key = diff < 0 ? KeyEvent.VK_LEFT : KeyEvent.VK_RIGHT;
        
        if(Math.abs(diff) > 180.0)
            key = key == KeyEvent.VK_LEFT ? KeyEvent.VK_RIGHT : KeyEvent.VK_LEFT;
        
        return ActionContext.get().pressKey(key, o ->  Math.abs(Projection.getCameraYaw() * 180.0 / Math.PI - resultingAngle) < 5.0);
    }
    
    public static ActionContext setCameraPitch(double angle)
    {
        int key = KeyEvent.VK_UP;
        if(angle * Math.PI / 180.0 < Projection.getCameraPitch())
            key = KeyEvent.VK_DOWN;
        
        final double resultingAngle = Misc.clamp(angle * Math.PI / 180.0, Projection.CAMERAPITCH_MIN, Projection.CAMERAPITCH_MAX) * 180.0 / Math.PI;
        
        return ActionContext.get().pressKey(key, o ->  Math.abs(Projection.getCameraPitch() * 180.0 / Math.PI - resultingAngle) < 5.0);
    }
    
    public static int getCurrentPlane()
    {
        return RSClient.getScene_plane();
    }
    
    public static String getUpText()
    {
        if(RSClient.getMenuOptionCount() <= 0)
            return "";
        return RSClient.getMenuOptions()[RSClient.getMenuOptionCount() - 1] + " " + 
                RSClient.getMenuTargets()[RSClient.getMenuOptionCount() - 1];
    }
    
    public static List<String> getContextOptions()
    {
        ArrayList<String> options = new ArrayList();
        for(int i = 0;i < RSClient.getMenuOptionCount();i++)
            options.add(RSClient.getMenuOptions()[i] + " " + RSClient.getMenuTargets()[i]);
        Collections.reverse(options);
        return options;
    }
    
    public static List<Integer> getContextActionParams0()
    {
        ArrayList<Integer> options = new ArrayList();
        for(int i = 0;i < RSClient.getMenuOptionCount();i++)
            options.add(RSClient.getMenuActionParams0()[i]);
        Collections.reverse(options);
        return options;
    }
    
    public static List<Integer> getContextActionParams1()
    {
        ArrayList<Integer> options = new ArrayList();
        for(int i = 0;i < RSClient.getMenuOptionCount();i++)
            options.add(RSClient.getMenuActionParams1()[i]);
        Collections.reverse(options);
        return options;
    }
    
    public static boolean isContextMenuOpen()
    {
        return RSClient.getIsMenuOpen();
    }
    
    public static Rectangle getContextOptionBounds(int optionIndex)
    {
        int menuX = RSClient.getMenuX();
        int menuY = RSClient.getMenuY();
        
        return new Rectangle(menuX + CTXMENU_MARGIN_H,
                menuY + CTXMENU_HEADERHEIGHT + optionIndex * CTXMENU_ROWHEIGHT + CTXMENU_MARGIN_V, 
                RSClient.getMenuWidth() - CTXMENU_MARGIN_H * 2,
                CTXMENU_ROWHEIGHT - CTXMENU_MARGIN_V * 2);
    }
    
    public static int getCanvasWidth()
    {
        if(AppletLoader.getSingleton().getGameCanvas() == null)
            return 765;
        return AppletLoader.getSingleton().getGameCanvas().getWidth();
    }
    
    public static int getCanvasHeight()
    {
        if(AppletLoader.getSingleton().getGameCanvas() == null)
            return 503;
        return AppletLoader.getSingleton().getGameCanvas().getHeight();
    }
    
    public static void closeContextMenu()
    {
        while(isContextMenuOpen())
            ActionContext.get().moveMouse(Misc.random(2, getCanvasWidth()), Misc.random(2, getCanvasHeight()));
    }
    
}
