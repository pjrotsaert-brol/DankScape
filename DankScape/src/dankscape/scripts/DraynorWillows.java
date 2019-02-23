/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.scripts;

import dankscape.api.Bank;
import dankscape.api.DankScript;
import dankscape.api.Game;
import dankscape.api.GameObject;
import dankscape.api.Inventory;
import dankscape.api.Item;
import dankscape.api.Login;
import dankscape.api.Player;
import dankscape.api.ScriptManifest;
import dankscape.api.Tile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Pieterjan
 */
@ScriptManifest(author = "Zelfrax", name = "Dank Lumbridge Woodcutter")
public class DraynorWillows extends DankScript
{
    int[] willowtree_ids = { 1758, 1756, 1750, 1760 };
    int[] oaktree_ids = { 1751 };
    
    Integer[] oakXs = { 3103, 3107 };
    Integer[] oakYs = { 3243, 3248 };
    
    int bankBoothId = 6943;
    
    int bankposX = 3094, bankposY = 3243;
    
    int willowLogId = 1519;
    int oakLogId = 1500;
    
    
    List<Integer> arrToList(int[] arr)
    {
        ArrayList<Integer> l = new ArrayList();
        for(int i : arr)
            l.add(i);
        return l;
    }
    
    private <T> boolean arrContains(T[] arr, T val)
    {
        for(T t : arr)
            if(t.equals(val))
                return true;
        
        return false;
    }
            
    GameObject getNearestAtPos(int[] ids, Integer[] xs, Integer[] ys)
    {
        
        return GameObject.get(ids).stream().filter(go -> arrContains(xs, go.getX()) && arrContains(ys, go.getY()))
                .sorted(Comparator.comparing((GameObject go) ->
        {
            return go.distanceTo(Player.getLocal().getCenter());
        })).findFirst().get();
    }
    
    GameObject getNearest(int[] ids)
    {
        return GameObject.get(ids).stream().sorted(Comparator.comparing((GameObject go) ->
        {
            return go.distanceTo(Player.getLocal().getCenter());
        })).findFirst().get();
    }
    
    GameObject getNearest(int id)
    {
        int[] ids = { id };
        return getNearest(ids);
    }
    
    void waitUntilDone()
    {
        while(Player.getLocal().getPeer().getAnimation() != -1 || Game.isPlayerMoving() ){ sleep(300); }
    }
    
    @Override
    protected void run()
    {
        while(true)
        {
            if(!Game.isInGame())
                Login.login();
            
            try
            {
                GameObject nearestTree = getNearest(willowtree_ids);
                
                //GameObject nearestTree = getNearestAtPos(oaktree_ids, oakXs, oakYs);

                if(nearestTree != null)
                {
                    //print("tree found, interacting?");
                    nearestTree.interact("Chop down").sleep(1820, 2534);
                }
                else
                    print("fucking no trees found, uwot?");

                waitUntilDone();

                List<Item> items = Inventory.getItems();
                if(items.size() >= 28)
                {
                    Tile t = Tile.get(0, random(bankposX - 1, bankposX + 1), random(bankposY - 1, bankposY + 1));
                    if(t != null)
                        t.click().sleep(1000, 3000);
                    
                    waitUntilDone();
                    
                    while(!Bank.isOpen())
                    {
                        GameObject booth = getNearest(bankBoothId);
                        if(booth != null)
                            booth.interact("bank");
                    }
                    
                    Item logs = Inventory.getItem(oakLogId);
                    if(logs != null)
                        logs.interact("Deposit-All");
                    logs = Inventory.getItem(willowLogId);
                    if(logs != null)
                        logs.interact("Deposit-All");
                    
                    Bank.close();
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
