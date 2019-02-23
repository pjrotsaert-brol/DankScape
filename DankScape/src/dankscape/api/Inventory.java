/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import dankscape.api.internal.ActionContext;
import dankscape.nativeinterface.NativeInterface;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;



/**
 *
 * @author Pieterjan
 */
public class Inventory
{
    public static final int INV_ITEM_WIDTH     = 32;
    public static final int INV_ITEM_HEIGHT    = 32;
    public static final int INV_ITEM_SPACING_H = 10;
    public static final int INV_ITEM_SPACING_V = 4;
    
    
    public static final int WIDGET_TAB_INVENTORY = (Widget.INTERFACE_GLOBAL << 16) | 54;
    public static final int WIDGET_INVDEFAULT = (149 << 16) | 0;
    public static final int WIDGET_INVBANK    = (15 << 16)  | 3;
    public static final int WIDGET_INVSHOP    = (301 << 16) | 0;
    public static final int WIDGET_INVGE      = (467 << 16) | 0;
    
    
    private static Widget getDefaultWidget()
    {
        return Widget.get(WIDGET_INVDEFAULT);
    }
    
    public static Widget getWidget()
    {
        Widget w = Widget.get(WIDGET_INVBANK);
        if(w != null)
            if(w.isVisible())
                return w;
        
        w = Widget.get(WIDGET_INVGE);
        if(w != null)
            if(w.isVisible())
                return w;
        
        w = Widget.get(WIDGET_INVSHOP);
        if(w != null)
            if(w.isVisible())
                return w;
        
        w = Widget.get(WIDGET_INVDEFAULT);
        if(w != null)
        {
            if(w.isVisible())
                return w;
        }

        return null;
    }
    
    public static boolean isOpen()
    {
        Widget w = getWidget();
        if(w != null)
            return true;
        return false;
    }
    
    public static boolean open()
    {
        if(isOpen())
            return true;
        
        Widget w = Widget.get(WIDGET_TAB_INVENTORY);
        if(w == null)
            return false;
        
        Rectangle bounds = w.getBounds();
        Point p = Misc.randomPointInRect(bounds);
        ActionContext.get().clickLeft(p);
        
        return isOpen();
    }
    
    
    public static List<Item> getItems()
    {
        ArrayList<Item> items = new ArrayList();
        Widget w = getWidget();
        
        int[] itemIds = null;
        int[] quantities = null;
        if(w != null)
        {
            List<Widget> children = w.getChildren();
            if(children.size() > 0)
            {
                for(int i = 0;i < children.size() && i < 28;i++)
                {
                    int nActions = 0;
                    if(children.get(i).getPeer().getActions() != null)
                        nActions = children.get(i).getPeer().getActions().length;
                    
                    if(children.get(i).getPeer().getItemId() > 0 && !children.get(i).getPeer().getIsHidden() && nActions > 0)
                    {
                        items.add(new Item(children.get(i).getPeer().getItemId(),  i, children.get(i).getPeer().getItemQuantity(), 
                                children.get(i), Item.Location.INVENTORY));
                    }
                }
            }
            else // For some reason the default inventory UI doesn't have a child per item.. the itemIds are hardcoded in it?
            {
                itemIds    = w.getPeer().getItemIds();
                quantities = w.getPeer().getItemQuantities();
            }
        }
        else if(getDefaultWidget() != null)
        {
            w = getDefaultWidget();
            itemIds    = w.getPeer().getItemIds();
            quantities = w.getPeer().getItemQuantities();
        }
        
        if(itemIds != null && quantities != null && w != null)
        {
            for(int i = 0;i < itemIds.length;i++)
            {
                if(itemIds[i] > 0)
                    items.add(new Item(itemIds[i] - 1, i, quantities[i], w, Item.Location.INVENTORY));
            }
        }
        
        //Debug.println("Invcount: " + items.size() + "!");
        return items;
    }
    
    public static List<Item> getItems(Predicate<Item> predicate)
    {
        return getItems().stream().filter(predicate).collect(Collectors.toList());
    }
    
    public static List<Item> getItems(String itemName)
    {
        return getItems(item -> item.getDef().getName().equals(itemName));
    }
    
    public static List<Item> getItems(int itemId)
    {
        return getItems(item -> item.getId() == itemId);
    }
    
    public static Item getItem(Predicate<Item> predicate)
    {
        Optional<Item> item = getItems().stream().filter(predicate).findFirst();
        if(item.isPresent())
            return item.get();
        return null;
    }
    public static Item getItem(String name)
    {
        return getItem(i -> i.getDef().getName().equals(name));
    }
    
    public static Item getItem(int id)
    {
        return getItem(i -> i.getId() == id);
    }
    
    public static Rectangle getItemBounds(int slotIdx)
    {
        Widget w = getDefaultWidget();
        if(w == null)
            return new Rectangle();
        
        int slotX = slotIdx % 4;
        int slotY = slotIdx / 4;
        
        int x = w.getAbsX() + slotX * (INV_ITEM_WIDTH + INV_ITEM_SPACING_H);
        int y = w.getAbsY() + slotY * (INV_ITEM_HEIGHT + INV_ITEM_SPACING_V);
        
        return new Rectangle(x, y, INV_ITEM_WIDTH, INV_ITEM_HEIGHT);
    }
    
    
}
