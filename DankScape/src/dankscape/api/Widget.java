/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import dankscape.api.internal.HashTable;
import dankscape.api.internal.Interactable;
import dankscape.api.internal.RSClassWrapper;
import dankscape.api.rs.RSClient;
import dankscape.api.rs.RSNode;
import dankscape.api.rs.RSWidget;
import dankscape.api.rs.RSWidgetNode;
import dankscape.loader.AppletLoader;
import dankscape.nativeinterface.NativeInterface;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Pieterjan
 */
public class Widget extends Interactable
{
    public static final int CONTAINER_EQUIPMENT     = 387;
    public static final int CONTAINER_SKILLS        = 320;
    public static final int CONTAINER_FRIENDS       = 429;
    public static final int CONTAINER_CLANCHAT      = 432;
    public static final int CONTAINER_COMBATSTYLE   = 593;
    public static final int CONTAINER_SETTINGS      = 261;
    public static final int CONTAINER_MUSIC         = 239;
    public static final int CONTAINER_MAGIC         = 218;
    public static final int CONTAINER_EMOTES        = 216;
    public static final int CONTAINER_LOGOUT        = 182;
    public static final int CONTAINER_INVENTORY     = 149;
    public static final int CONTAINER_BANK          = 12;
    
    public static final int INTERFACE_CHAT          = 162;
    public static final int INTERFACE_GLOBAL        = 161;
    public static final int INTERFACE_MINIMAP       = 160;
    public static final int INTERFACE_XPDROPS       = 122;
    
    public static final int TAB_CLANCHAT        = (INTERFACE_GLOBAL << 16) | (35 & 0xFFFF);
    public static final int TAB_FRIENDS         = (INTERFACE_GLOBAL << 16) | (36 & 0xFFFF);
    public static final int TAB_IGNORES         = (INTERFACE_GLOBAL << 16) | (37 & 0xFFFF);
    public static final int TAB_LOGOUT          = (INTERFACE_GLOBAL << 16) | (38 & 0xFFFF);
    public static final int TAB_SETTINGS        = (INTERFACE_GLOBAL << 16) | (39 & 0xFFFF);
    public static final int TAB_EMOTES          = (INTERFACE_GLOBAL << 16) | (40 & 0xFFFF);
    public static final int TAB_MUSIC           = (INTERFACE_GLOBAL << 16) | (41 & 0xFFFF);   
    public static final int TAB_COMBATSTYLE     = (INTERFACE_GLOBAL << 16) | (51 & 0xFFFF);
    public static final int TAB_SKILLS          = (INTERFACE_GLOBAL << 16) | (52 & 0xFFFF);
    public static final int TAB_QUESTS          = (INTERFACE_GLOBAL << 16) | (53 & 0xFFFF);
    public static final int TAB_INVENTORY       = (INTERFACE_GLOBAL << 16) | (54 & 0xFFFF);
    public static final int TAB_EQUIPMENT       = (INTERFACE_GLOBAL << 16) | (55 & 0xFFFF);
    public static final int TAB_PRAYER          = (INTERFACE_GLOBAL << 16) | (56 & 0xFFFF);
    public static final int TAB_MAGIC           = (INTERFACE_GLOBAL << 16) | (57 & 0xFFFF);
    
    private static final HashMap<RSWidget, Widget> widgetCache = new HashMap();
    private static final HashMap<Integer, RSWidgetNode> widgetNodeCache = new HashMap();
    
    private RSWidget peer = null;
    private Widget parentRef = null;
    
    protected Widget(RSWidget peer)
    {
        this.peer = peer;
    }
    protected Widget(RSWidget peer, Widget parent)
    {
        this.parentRef = parent;
        this.peer = peer;
    }
    
    public static Widget get(int containerId, int widgetId)
    {
        Object[][] widgets = RSClient.getRSRef_Widgets();
        if(widgets != null)
            if(containerId >= 0 && containerId < widgets.length)
                if(widgets[containerId] != null)
                    if(widgetId >= 0 && widgetId < widgets[containerId].length)
                        if(widgets[containerId][widgetId] != null)
                        {
                            RSWidget wrapper = (RSWidget)RSClassWrapper.getWrapper(widgets[containerId][widgetId]);
                            if(wrapper == null)
                                return null;
                            Widget w = widgetCache.get(wrapper);
                            if(w == null)
                            {
                                w = new Widget(wrapper);
                                widgetCache.put(wrapper, w);
                            }
                            return w;
                        }
        
        return null;
    }
    
    public static Widget get(int combinedWidgetId)
    {
        return get((combinedWidgetId >> 16), combinedWidgetId & 0xFFFF);
    }
    
    public static List<Widget> getAllVisible()
    {
        ArrayList<Widget> widgets = new ArrayList();
        Object[][] peers = RSClient.getRSRef_Widgets();
        if(peers != null)
        {
            for(int i = 0;i < peers.length;i++)
            {
                if(peers[i] != null)
                {
                    for(int j = 0;j < peers[i].length;j++)
                    {
                        RSWidget wrapper = (RSWidget)RSClassWrapper.getWrapper(peers[i][j]);
                        if(wrapper != null)
                        {
                            Widget w = widgetCache.get(wrapper);
                            if(w == null)
                            {
                                w = new Widget(wrapper);
                                widgetCache.put(wrapper, w);
                            }
                            if(w.isVisible())
                                widgets.add(w);
                        }
                    }
                }
            }
        }
        return widgets;
    }
    
    private static void getAllChildrenContainingText(Widget w, String text, ArrayList<Widget> output)
    {
        if(w.getText().contains(text))
            output.add(w);
        
        List<Widget> children = w.getChildren();
        for(Widget child : children)
            getAllChildrenContainingText(child, text, output);
    }
    
    public static List<Widget> getAllContainingText(String text)
    {
        List<Widget> widgets = getAllVisible();
        ArrayList<Widget> output = new ArrayList();
        
        for(Widget w : widgets)
            getAllChildrenContainingText(w, text, output);
        
        return output;
    }
    
    public RSWidget getPeer()
    {
        return peer;
    }
    
    public Widget getParent()
    {
        if(parentRef != null)
            return parentRef;
        
        if(peer.getParentId() != -1)
            return get(peer.getParentId());
        
        HashTable tbl = HashTable.fromPeer(RSClient.getComponentTable());
        int maxIters = 1000, iter = 0;
        for(RSNode node = tbl.getFirst();node != null && iter < maxIters;node = tbl.getNext())
        {
            if(node instanceof RSWidgetNode)
            {
                RSWidgetNode wNode = (RSWidgetNode)node;
                if(wNode.getId() == getContainerId())
                    return get((int)wNode.getHash());
            }
            iter++;
        }
        return null;
    }
    
    public int getContainerId()
    {
        return peer.getId() >> 16;
    }
    
    public int getChildId()
    {
        return peer.getId() & 0xFFFF;
    }
    
    public int getWidgetId()
    {
        return peer.getId();
    }

    public List<Widget> getChildren()
    {
        ArrayList<Widget> children = new ArrayList();
        RSWidget[] childPeers = peer.getChildren();
        if(childPeers != null)
        {
            for(RSWidget pW : childPeers)
            {
                if(pW != null)
                {
                    Widget w = widgetCache.get(pW);
                    if(w == null)
                    {
                        w = new Widget(pW, this);
                        widgetCache.put(pW, w);
                    }
                    children.add(w);
                }
            }
        }
        return children;
    }
    
    public Widget getChild(int childIndex)
    {
        Object[] children = peer.getRSRef_Children();
        if(children != null)
        {
            if(childIndex < 0 || childIndex >= children.length)
                return null;
            if(children[childIndex] == null)
                return null;
            
            RSWidget childPeer = (RSWidget)RSClassWrapper.getWrapper(children[childIndex]);
            Widget w = widgetCache.get(childPeer);
            if(w == null)
            {
                w = new Widget(childPeer, this);
                widgetCache.put(childPeer, w);
            }
            return w;
        }
        return null;
    }
    
    public Point getAbsPosition()
    {
        int x = getX();
        int y = getY();
        
        Widget parent = getParent();
        
        if(getParent() == null)
        {
            int idx = peer.getBoundsIndex();
            int[] positionsX = RSClient.getWidgetPositionX();
            int[] positionsY = RSClient.getWidgetPositionY();
            if(idx >= 0 && idx < positionsX.length && idx < positionsY.length)
            {
                x += positionsX[idx];
                y += positionsY[idx];
            }
        }
        
        while(parent != null)
        {
            x += parent.getX();
            y += parent.getY();
            x -= parent.peer.getScrollX();
            y -= parent.peer.getScrollY();
            
            parent = parent.getParent();
        }
        
        return new Point(x, y);
    }
    
    public int getAbsX()
    {
        return getAbsPosition().x;
    }
    
    public int getAbsY()
    {
        return getAbsPosition().y;
    }
    
    public int getWidth()
    {
        return peer.getWidth();
    }
    
    public int getHeight()
    {
        return peer.getHeight();
    }
    
    public int getX()
    {
        /*if(getParent() == null)
        {
            int idx = peer.getBoundsIndex();
            int[] positionsX = RSClient.getWidgetPositionX();
            if(idx >= 0 && idx < positionsX.length && getParent() == null)
                return peer.getRelativeX() + positionsX[idx];
        }*/
        return peer.getRelativeX();
    }
    
    public int getY()
    {
        /*if(getParent() == null)
        {
            int idx = peer.getBoundsIndex();
            int[] positionsY = RSClient.getWidgetPositionY();
            if(idx >= 0 && idx < positionsY.length && getParent() == null)
                return peer.getRelativeY() + positionsY[idx];
        }*/
        return peer.getRelativeY();
    }
    
    public Point getPosition()
    {
        return new Point(getX(), getY());
    }
    
    public Rectangle getBounds()
    {
        return new Rectangle(getAbsX(), getAbsY(), getWidth(), getHeight());
    }
    
    public boolean isVisible()
    {
        Widget parent = getParent();
        while(parent != null)
        {
            if(parent.peer.getIsHidden())
                return false;
            parent = parent.getParent();
        }
        return !peer.getIsHidden();
    }
    
    public boolean isHidden()
    {
        return !isVisible();
    }
    
    public String getText()
    {
        if(peer.getText() == null)
            return "";
        return peer.getText();
    }
    
    public String getTooltip()
    {
        return peer.getTooltip();
    }
    
    @Override
    public String getName()
    {
        return peer.getName();
    }
    
    public int getType()
    {
        return peer.getType();
    }

    @Override
    public Point getRandomPoint()
    {
        return Misc.randomPointInRect(getBounds());
    }
    
    @Override
    public boolean isOnScreen()
    {
        return isVisible();
    }
    
    @Override
    public void bringOnScreen(){} // Can't implement this for generic widgets
    
    @Override 
    public boolean isHovering()
    {
        if(!isOnScreen())
            return false;
        return Misc.isPointInRect(Input.getMousePos(), getBounds());
    }
    
    
    private static void refreshDefinitions()
    {
        Object[] objects = NativeInterface.getAllInstancesOfClass(AppletLoader.getSingleton().getHooks().get("WidgetNode").clazz);
        //compositions.clear();
        for(Object obj : objects)
        {
            RSClassWrapper wrapper = RSClassWrapper.getWrapper(obj);
            if(wrapper instanceof RSWidgetNode)
            {
                RSWidgetNode comp = (RSWidgetNode)wrapper;
                widgetNodeCache.put(comp.getId(), comp);
            }
        }
        NativeInterface.println("Refreshing widgetnode definitions...");
    }
    
    private static RSWidgetNode getWidgetNode(int containerId)
    {
        RSWidgetNode comp = widgetNodeCache.get(containerId);
        if(comp == null)
            refreshDefinitions();
        else if(comp.getRSObjectReference() == null)
            refreshDefinitions();
        
        return widgetNodeCache.get(containerId);
    }
}
