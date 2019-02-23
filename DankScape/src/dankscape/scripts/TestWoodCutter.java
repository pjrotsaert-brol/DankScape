/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.scripts;

import dankscape.api.DankScript;
import dankscape.api.Game;
import dankscape.api.GameObject;
import dankscape.api.Inventory;
import dankscape.api.Item;
import dankscape.api.Login;
import dankscape.api.Player;
import dankscape.api.ScriptManifest;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Pieterjan
 */
@ScriptManifest(author = "Zelfrax", name = "Dank Lumbridge Woodcutter")
public class TestWoodCutter extends DankScript
{
    int[] tree_ids = { 1276, 1278, 1286 };
    
    @Override
    protected void run()
    {
        while(true)
        {
            if(!Game.isInGame())
                Login.login();
            
            try
            {
                GameObject nearestTree = GameObject.get(tree_ids).stream().sorted(Comparator.comparing((GameObject go) ->
                {
                    return go.distanceTo(Player.getLocal().getCenter());
                })).findFirst().get();

                if(nearestTree != null)
                {
                    print("tree found, interacting?");
                    nearestTree.interact("Chop down").sleep(1820, 2534);
                }
                else
                    print("fucking no trees found, uwot?");

                while(Player.getLocal().getPeer().getAnimation() != -1 || Game.isPlayerMoving() ){ sleep(300); }

                List<Item> items = Inventory.getItems();
                if(items.size() >= 28)
                {
                    for(Item it : items)
                    {
                        if(it.getId() == 1511)
                            it.drop();
                    }
                }
            }
            catch(Exception ex)
            {
                print("ERROR: " + ex.toString());
            }
            
            sleep(1000, 2000);
        }
    }
}
