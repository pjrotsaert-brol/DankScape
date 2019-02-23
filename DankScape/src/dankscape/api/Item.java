/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import dankscape.api.internal.ActionContext;
import dankscape.api.internal.Interactable;
import dankscape.api.internal.RSClassWrapper;
import dankscape.api.rs.RSItemComposition;
import dankscape.loader.AppletLoader;
import dankscape.nativeinterface.NativeInterface;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;

/**
 *
 * @author Pieterjan
 */
public class Item extends Interactable
{

    @Override
    public int getX()
    {
        return -1;
    }

    @Override
    public int getY()
    {
        return -1;
    }
    
    public static enum Location 
    {
        INVENTORY,
        BANK, 
        SHOP,
        GRANDEXCHANGE
    }
    
    private Widget widget;
    private Location itemLocation = Location.INVENTORY;
    private int id = 0, index = 0, quantity = 0;
    private RSItemComposition def = null;
    
    private static final HashMap<Integer, RSItemComposition> compositions = new HashMap();
    
    public Item(int id, int index, int quantity, Widget widget, Location loc)
    {
        this.index = index;
        this.id = id;
        this.widget = widget;
        this.quantity = quantity;
        this.itemLocation = loc;
    }
    
    public int getId()
    {
        return id;
    }
    
    public int getIndex()
    {
        return index;
    }
    
    public int getQuantity()
    {
        return quantity;
    }
    
    public RSItemComposition getDef()
    {
        if(def == null)
            def = getItemDef(id);
        return def;
    }
    
    public Location getLocation()
    {
        return itemLocation;
    }
    
    public Widget getWidget()
    {
        return widget;
    }
    
    public Rectangle getBounds()
    {
        if(itemLocation == Location.INVENTORY)
            return Inventory.getItemBounds(index);
        else if(widget != null)
            return widget.getBounds();
        else
            throw new NullPointerException("Item widget was null and item is not an inventory item!");
    }
    
    @Override
    public Point getRandomPoint()
    {
        return Misc.randomPointInRect(getBounds());
    }

    @Override
    public String getName()
    {
        if(getDef() == null)
            return "";
        return getDef().getName();
    }

    @Override
    public boolean isOnScreen()
    {
        if(itemLocation == Location.INVENTORY)
            return Inventory.isOpen();
        else
            throw new UnsupportedOperationException("TODO: Implement isOnScreen() for shit other than INVENTORY.");
    }
    
    @Override
    public void bringOnScreen()
    {
        if(itemLocation == Location.INVENTORY)
            Inventory.open();
        else
            throw new UnsupportedOperationException("TODO: Implement isOnScreen() for shit other than INVENTORY.");
    }
    
    public ActionContext drop()
    {
        return interact("drop");
    }
    
    public ActionContext examine()
    {
        return interact("examine");
    }
    
    public static void refreshDefinitions()
    {
        Object[] objects = NativeInterface.getAllInstancesOfClass(AppletLoader.getSingleton().getHooks().get("ItemComposition").clazz);
        //compositions.clear();
        for(Object obj : objects)
        {
            RSClassWrapper wrapper = RSClassWrapper.getWrapper(obj);
            if(wrapper instanceof RSItemComposition)
            {
                RSItemComposition comp = (RSItemComposition)wrapper;
                compositions.put(comp.getId(), comp);
            }
        }
        NativeInterface.println("Refreshing item definitions...");
    }
    
    public static RSItemComposition getItemDef(int id)
    {
        RSItemComposition comp = compositions.get(id);
        if(comp == null)
            refreshDefinitions();
        else if(comp.getRSObjectReference() == null)
            refreshDefinitions();
        
        return compositions.get(id);
    }
    
    @Override
    public boolean isHovering()
    {
        if(!isOnScreen())
            return false;
        
        return Misc.isPointInRect(Input.getMousePos(), getBounds());
    }
    
    
}
