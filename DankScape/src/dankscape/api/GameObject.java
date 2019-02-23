/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import dankscape.api.internal.Interactable;
import dankscape.api.internal.Projection;
import dankscape.api.rs.RSClient;
import dankscape.api.rs.RSGameObject;
import dankscape.api.rs.RSModel;
import dankscape.api.rs.RSObjectComposition;
import dankscape.api.rs.RSRenderable;
import dankscape.bot.DebugRenderer;
import dankscape.bot.DebugSettings;
import dankscape.loader.AppletLoader;
import dankscape.misc.DebugWriter;
import dankscape.misc.Mat4;
import dankscape.misc.Vec3;
import dankscape.nativeinterface.NativeInterface;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author Pieterjan
 */
public class GameObject extends Interactable
{
    private static final int MIN_REFRESH_INTERVAL = 2000;
    private static long tLastRefresh = 0;
    
    private static HashMap<RSGameObject, GameObject> objectCache = new HashMap();
    private static HashMap<Integer, RSObjectComposition> compositions = new HashMap();
    
    private final RSGameObject peer;
    private Model model;
    private RSObjectComposition def;
    Tile parentTile;
    
    private int computedWidth, computedDepth, computedHeight;
    
    public GameObject(RSGameObject peer)
    {
        this.peer = peer;
    }
    
    public RSObjectComposition getDef()
    {
        if(def == null)
            def = getObjectDef(getId());
        return def;
    }
    
    public int getId()
    {
        return peer.getHash() >> 14 & 0x7FFF;
    }
    
    public RSGameObject getPeer()
    {
        return peer;
    }
    
    public int getSceneX()
    {
        return peer.getX();
    }
    
    public int getSceneY()
    {
        return Tile.getGroundHeight(getSceneX(), getSceneZ());
    }
    
    public int getSceneZ()
    {
        return peer.getY();
    }
    
    @Override
    public int getX()
    {
        if(parentTile != null)
            return parentTile.getX() - 1;
        
        return RSClient.getBaseX() + (getSceneX() / Tile.TILE_SIZE);
    }
    
    @Override
    public int getY()
    {
        if(parentTile != null)
            return parentTile.getY() - 1;
        return RSClient.getBaseY() + (getSceneZ() / Tile.TILE_SIZE);
    }
    
    public int getWidth()
    {
        if(computedWidth > 0)
            return computedWidth;
        
        if(getModel() != null)
        {
            computedWidth = getModel().computeWidth();
            return computedWidth;
        }
        
        return getDef() != null ? getDef().getModelSizeX() : 50;
    }
    
    public int getHeight()
    {
        if(computedHeight > 0)
            return computedHeight;
        
        if(getModel() != null)
        {
            computedHeight = getModel().computeHeight();
            return computedHeight;
        }
        
        return getDef() != null ? getDef().getModelSizeHeight() : 50;
    }
    
    public int getDepth()
    {
        if(computedDepth > 0)
            return computedDepth;
        
        if(getModel() != null)
        {
            computedDepth = getModel().computeDepth();
            return computedDepth;
        }
        
        return getDef() != null ? getDef().getModelSizeY() : 50;
    }
    
    public Vec3 getCenter()
    {
        return new Vec3(getSceneX(), getSceneY() + getHeight() / 2, getSceneZ());
    }
    
    @Override
    public String getName()
    {
        return getDef() != null ? getDef().getName() : "unknown";
    }
    
    public List<String> getActions()
    {
        return getDef() != null ? Arrays.stream(getDef().getActions()).filter(s -> s != null).collect(Collectors.toList()) : new ArrayList();
    }
    
    public Model getModel()
    {
        if(model != null)
            return model;
        RSRenderable renderable = peer.getRenderable();
        if(renderable instanceof RSModel)
            model = new Model((RSModel)renderable);
        return model;
        
        /*Model m = Model.getModel(getId());
        if(m != null)
            model = m;
        return model;*/
    }
    
    public double distanceTo(Vec3 point)
    {
        Vec3 diff = point.subtract(getCenter());
        return diff.getMagnitude();
    }
    
    public static GameObject forPeer(RSGameObject peer)
    {
        if(peer == null)
            return null;
        GameObject go = objectCache.get(peer);
        if(go == null)
        {
            go = new GameObject(peer);
            objectCache.put(peer, go);
        }
        return go;
    }
    
    // Sorted by distance by default
    public static List<GameObject> getAll()
    {
        ArrayList<GameObject> objects = new ArrayList();
        List<Tile> tiles = Tile.get(Game.getCurrentPlane());
        for(Tile t : tiles)
            objects.addAll(t.getGameObjects());
        return objects;//.stream().sorted(Comparator.comparing(go -> go.distanceTo(Player.getLocal().getCenter()) )).collect(Collectors.toList());
    }
    
    public static List<GameObject> getAllInteractable()
    {
        return getAll().stream().filter(obj -> obj.getDef() == null ? false : obj.getActions().size() > 0).collect(Collectors.toList());
    }
    
    public static List<GameObject> get(int id)
    {
        return get(obj -> obj.getId() == id);
    }
    
    public static List<GameObject> get(int[] ids)
    {
        return get(obj -> 
        {
                for(int i : ids)
                {
                    if(obj.getId() == i) 
                        return true;
                }
                return false;
        });
    }
    
    public static List<GameObject> get(String name)
    {
        return get(o -> o.getName().equals(name));
    }
    
    public static List<GameObject> get(Predicate<GameObject> p)
    {
        return getAll().stream().filter(p).collect(Collectors.toList());
    }
    
    public static GameObject getSingle(Predicate<GameObject> p)
    {
        Optional<GameObject> first = getAll().stream().filter(p).findFirst();
        if(first.isPresent())
            return first.get();
        return null;
    }
    public static GameObject getSingle(int[] ids)
    {
        Optional<GameObject> first = get(ids).stream().findFirst();
        if(first.isPresent())
            return first.get();
        return null;
    }
    public static GameObject getSingle(int id)
    {
        return getSingle(o -> o.getId() == id);
    }
    public static GameObject getSingle(String name)
    {
        return getSingle(o -> o.getName().equals(name));
    }
    
    public static void renderDebug(Graphics g)
    {
        List<GameObject> objects = getAll();
        
        g.setColor(Color.cyan);
        for(GameObject go : objects)
        {
            if((go.getActions().size() > 0 && DebugSettings.showInteractiveGameObjects) ||
                    (go.getActions().isEmpty() && DebugSettings.showOtherGameObjects))
            {
                Vec3 pos = go.getCenter();
                Vec3 view = Projection.transform(pos);
                if(Projection.isInFrustum(view))
                {
                    Point p = Projection.project(view);

                    String str = go.getName();
                    if(DebugSettings.showPosition)
                        str += " (" + go.getX() + ", " + go.getY() + ")";
                    
                    g.drawString(str, p.x - g.getFontMetrics().stringWidth(str) / 2, p.y - 4);
                    str = "" + go.getId();

                    g.drawString(str, p.x - g.getFontMetrics().stringWidth(str) / 2, p.y - 16);

                    g.fillOval(p.x, p.y, 3, 3);
                }
            }
        }
    }
    
    public static void renderModelDebug(Graphics g)
    {
        List<GameObject> objects = getAll();
        
        g.setColor(Color.cyan);
        for(GameObject go : objects)
        {
            if((go.getActions().size() > 0 && DebugSettings.showInteractiveGameObjects) ||
                    (go.getActions().isEmpty() && DebugSettings.showOtherGameObjects))
            {
                Vec3 pos = new Vec3(go.getSceneX(), go.getSceneY() + go.getHeight(), go.getSceneZ());
                Vec3 view = Projection.transform(pos);
                if(Projection.isInFrustum(view))
                {
                    DebugRenderer.drawCube(g, go.getSceneX(), go.getSceneY() + go.getHeight() / 2, go.getSceneZ(), 
                            go.getWidth(), go.getDepth(), go.getHeight(), 0.0, go.getRotation());

                    Mat4 t = new Mat4();
                    t.translate(go.getSceneX(), go.getSceneY(), go.getSceneZ());
                    t.rotateY(go.getRotation());
                    t.scale(go.getScale(), go.getScale(), go.getScale());

                    //if(npc.getName().toLowerCase().contains("duck"))
                    //     NativeInterface.println(""+npc.peer.getAngle());

                    Projection.setModelMatrix(t);
                    if(go.getModel() != null)
                        go.getModel().renderWireframe(g);
                    Projection.loadIdentity();
                }
            }
        }
    }
    
    
    public static void refreshDefinitions()
    {
        long tCurrent = System.currentTimeMillis();
        if(tCurrent - tLastRefresh < MIN_REFRESH_INTERVAL)
            return;
        
        tLastRefresh = tCurrent;
        
        Object[] objects = NativeInterface.getAllInstancesOfClass(AppletLoader.getSingleton().getHooks().get("ObjectComposition").clazz);
        compositions.clear();
        for(Object obj : objects)
        {
            if(AppletLoader.getSingleton().getHooks().get("ObjectComposition").clazz.isInstance(obj))
            {
                RSObjectComposition comp = new RSObjectComposition(obj);
                compositions.put(comp.getId(), comp);
            }
        }
        sdebug("Refreshing object definitions...");
    }
    
    public static RSObjectComposition getObjectDef(int id)
    {
        RSObjectComposition comp = compositions.get(id);
        if(comp == null)
            refreshDefinitions();
        else if(comp.getRSObjectReference() == null)
            refreshDefinitions();
        
        return compositions.get(id);
    }
    
    public Vec3 getRandomWorldPoint()
    {
        Model m = getModel();
        if(m != null)
            return m.getRandomVertex();
        
        return new Vec3(0, Misc.random(10, getHeight() - 10), 0);
    }
    
    public double getRotation()
    {
        return (peer.getOrientation() / 2048.0) * Math.PI * 2.0; 
    }
    
    public double getScale()
    {
        return 1.0; // TODO: implement?
    }
    

    @Override
    public Point getRandomPoint()
    {
        Mat4 t = new Mat4();
        t.translate(getSceneX(), getSceneY(), getSceneZ());
        t.rotateY(getRotation());
        t.scale(getScale(), getScale(), getScale());
        Projection.setModelMatrix(t);
        Point p = Projection.project(Projection.transform(getRandomWorldPoint()));
        Projection.loadIdentity();
        return p;
    }

    @Override
    public boolean isOnScreen()
    {
        return Projection.isOnScreen(getCenter());
    }

    @Override
    public void bringOnScreen()
    {
        if(!isOnScreen())
            Game.lookAt(getCenter());
    }

    @Override
    protected boolean isHovering()
    {
        return (getOptionIndex() != -1) && isOnScreen();
    }
    
    
    
}
