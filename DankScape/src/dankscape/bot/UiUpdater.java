/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot;

import dankscape.bot.TaskScheduler;
import dankscape.api.Game;
import dankscape.api.GameObject;
import dankscape.api.Input;
import dankscape.api.Inventory;
import dankscape.api.Item;
import dankscape.api.Login;
import dankscape.api.Misc;
import dankscape.api.Widget;
import dankscape.api.internal.HashTable;
import dankscape.api.internal.Projection;
import dankscape.api.rs.RSClient;
import dankscape.api.rs.RSGraphics3D;
import dankscape.api.rs.RSItemComposition;
import dankscape.api.rs.RSNode;
import dankscape.api.rs.RSRegion;
import dankscape.api.rs.RSTile;
import dankscape.bot.BotTask;
import dankscape.bot.gui.ScriptBrowser;
import dankscape.loader.AppletLoader;
import dankscape.nativeinterface.NativeInterface;
import dankscape.misc.DebugWriter;
import dankscape.misc.Mat4;
import dankscape.scripts.TestScript;
import java.awt.Color;
import java.util.List;

/**
 *
 * @author Pieterjan
 */
public class UiUpdater extends DebugWriter
{
    private static final UiUpdater singleton = new UiUpdater();
    
    public UiUpdater()
    {
    }
    
    public static UiUpdater get()
    {
        return singleton;
    }
    
    public void update()
    {
        String gameState = "Status: " + Login.getStatus().toString();
        
        if(TaskScheduler.get().getCurrentScript() != null)
            gameState += " | Script: " + TaskScheduler.get().getCurrentScript().getScriptName();
        else
            gameState += " | Script: null";
        
        gameState += " | Task: " + 
                (TaskScheduler.get().getCurrentTask() == null ? "null" : TaskScheduler.get().getCurrentTask().getDescription());

        
        String cursorInfo = " | x: " + Input.mouseX + " y: " + Input.mouseY + " | ";
        if(AppletLoader.getSingleton().getGameCanvas() != null)
        {
            Color c = Misc.grabScreenColor(Input.mouseX, Input.mouseY);
            cursorInfo += "r: " + c.getRed() + " g: " + c.getGreen() + " b: " + c.getBlue();
        }
        NativeInterface.setStatusTextLeft(gameState + cursorInfo + "  ");
        
        NativeInterface.setStatusTextRight("Selected script: " + (ScriptBrowser.getSelectedScript() == null ? "N/A" : ScriptBrowser.getSelectedScript().getScriptName()));

        //debug("FOVSCLA: " + Projection.fovScaleFactor);
        //GameObject.refreshDefinitions();
        //GameObject.getLoadedDefs();
        //List<GameObject> objects = GameObject.getAllInteractable();
        //String outp = "Objects: " + objects.size();
        
   
        /*for(GameObject obj : objects)
        {
            if(obj.getDef() != null)
                outp += (obj.getDef().getActions()[0]) + ", ";
        }*/
        //debug(outp);
       
        //List<Widget> wid = Widget.getAllContainingText("Walk here");
        //wid.stream().forEach(w -> { debug("Widget: " + w.getContainerId() + ", " + w.getChildId()); });
        
        /*if(RSClient.getLocalPlayer() != null && RSClient.getTileHeights() != null)
        {
            debug("Camera: X: " + RSClient.getCameraX() + "  Y: " + -RSClient.getCameraZ() + "  Z: " + RSClient.getCameraY() + 
                    "  Yaw: " + Projection.getCameraYaw() + "  Pitch: " + Projection.getCameraPitch() + 
                    "\nPlayer: X: " + RSClient.getLocalPlayer().getX() + "  Y: " + RSClient.getScene_plane() + "  Z: " + RSClient.getLocalPlayer().getY() + 
                    " \nPlane:" + RSClient.getScene_plane());
        }*/
 
        
        /*System.out.println("KDLKLDKFLKSLFKLKF --------------- " );

            List<Item> items = Inventory.getItems();
            System.out.println("RETRIEVED!");
            for(Item i : items)
            {
                if(i.getDef() != null)
                    System.out.println("ITEM: " + i.getDef().getName());
                else 
                    System.out.println("SHIT IS NULL!! CRIIIIIIIIIIIIIIIIIII");
                //debug("Item in inv: " + i.getDef().getName() + ".");
                break;
            }
            debug("Item count: " + items.size());
        
        
        System.out.println("KLAARKUT)");*/
       
        
    }
}
