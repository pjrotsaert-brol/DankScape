/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pieterjan
 */
public class Bank
{
    private static final int INTERFACE_BANK = 12;
    private static final int INTERFACE_BANK_ITEMCONTAINER = 12;
    private static final int INTERFACE_BANK_SCROLLBAR = 13;
    
    private static final int INTERFACE_BANK_GENERIC = 3;
    private static final int INTERFACE_BANK_GENERIC_CLOSEBUTTON = 11;
    
    private static final int INTERFACE_BANK_SCROLLBAR_BTNUP = 4;
    private static final int INTERFACE_BANK_SCROLLBAR_BTNDOWN = 5;
    private static final int INTERFACE_BANK_SCROLLBAR_HANDLE = 1;
    private static final int INTERFACE_BANK_SCROLLBAR_REGION = 0;
    
    private static final int INTERFACE_BANK_WITHDRAWITEM = 22;
    private static final int INTERFACE_BANK_WITHDRAWNOTED = 24;
    private static final int INTERFACE_BANK_DEPOSITINVENTORY = 29;
    private static final int INTERFACE_BANK_DEPOSITEQUIPMENT = 31;
    
    public static boolean isOpen()
    {
        Widget w = Widget.get(INTERFACE_BANK, 0);
        if(w == null)
            return false;
        return w.isVisible();
    }
    
    public static void depositEquipment()
    {
        if(!isOpen())
            throw new IllegalStateException("Bank window not open!");
        Widget w = Widget.get(INTERFACE_BANK, INTERFACE_BANK_DEPOSITEQUIPMENT);
        if(w == null)
            throw new NullPointerException("Could not obtain widget pointer of deposit-all button.");
        w.click();
    }
    
    public static void depositInventory()
    {
        if(!isOpen())
            throw new IllegalStateException("Bank window not open!");
        Widget w = Widget.get(INTERFACE_BANK, INTERFACE_BANK_DEPOSITINVENTORY);
        if(w == null)
            throw new NullPointerException("Could not obtain widget pointer of deposit-all button.");
        w.click();
    }
    
    public static List<Item> getItems()
    {
        ArrayList<Item> items = new ArrayList();
        Widget w = Widget.get(INTERFACE_BANK, INTERFACE_BANK_ITEMCONTAINER);
        if(w == null)
            throw new NullPointerException("Could not obtain widget pointer of bank item container.");
        List<Widget> children = w.getChildren();
        int idx = 0;
        for(Widget child : children)
        {
            if(child.getPeer().getItemId() != 0)
            {
                Item item = new Item(child.getPeer().getItemId(), idx, child.getPeer().getItemQuantity(), child, Item.Location.BANK);
                items.add(item);
            }
            idx++;
        }
        return items;
    }
    
    public static void close()
    {
        while(Bank.isOpen())
        {
            Widget generic = Widget.get(INTERFACE_BANK, INTERFACE_BANK_GENERIC);
            if(generic != null)
            {
                Widget btnClose = generic.getChild(INTERFACE_BANK_GENERIC_CLOSEBUTTON);
                if(btnClose == null)
                    throw new IllegalStateException("Bank closebutton not found!");
                else 
                    btnClose.click();
            }
            else 
                throw new IllegalStateException("Bank closebutton container not found!");
        }
    }
    
}
