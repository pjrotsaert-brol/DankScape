/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.scripts;

import dankscape.api.DankScript;
import dankscape.api.Game;
import dankscape.api.Inventory;
import dankscape.api.Item;
import dankscape.api.Login;
import dankscape.api.Misc;
import dankscape.api.NPC;
import dankscape.api.Tile;
import dankscape.api.Widget;
import dankscape.api.internal.ActionContext;
import dankscape.api.internal.Projection;
import dankscape.api.rs.RSClient;
import dankscape.api.rs.RSDeque;
import dankscape.api.rs.RSNPC;
import dankscape.loader.AppletLoader;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Pieterjan
 */
public class TestScript extends DankScript
{
    
    
    @Override
    protected void run()
    {
        
        //while(AppletLoader.getSingleton() != null)
        //    actions().moveMouse(Misc.random(0, 760), Misc.random(0, 500));
        
        print("Script started!");
        Login.login();
        
        sleep(5000);
        
        Game.lookAt(NPC.getSingle(npc -> npc.getName().equals("Town crier")).getCenter());
        print("Looking at town crier");
        sleep(2000);
        Game.lookAt(NPC.getSingle(npc -> npc.getName().equals("Fortunato")).getCenter());
        print("Looking at Fortunato");
        sleep(2000);
        Game.lookAt(NPC.getSingle(npc -> npc.getName().equals("Diango")).getCenter());
        print("Looking at Diango");
        sleep(2000);
        Game.lookAt(NPC.getSingle(npc -> npc.getName().equals("Bank guard")).getCenter());
        print("Looking at Bank guard");
        sleep(2000);
        
        /*sleep(2000);
        
        actions().pressKey(KeyEvent.VK_UP, o ->  RSClient.getCameraPitch() > 350);
        actions().pressKey(KeyEvent.VK_RIGHT, o -> RSClient.getCameraYaw() > 1100 && RSClient.getCameraYaw() < 1200);
        
        List<Item> items = Inventory.getItems();
        for(Item i : items)
        {
            //i.drop();
            Tile.get(Game.getCurrentPlane(), 
                    RSClient.getBaseX() + (RSClient.getLocalPlayer().getX() / Tile.TILE_SIZE) - 1,
                    RSClient.getBaseY() + (RSClient.getLocalPlayer().getY() / Tile.TILE_SIZE)).click();
            sleep(1000, 2000);
        }*/
        
        //
        
        
        
        
        
        print("End of script.");
    }
    
}
