/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import dankscape.api.internal.ActionContext;
import dankscape.api.internal.Interactable;
import dankscape.api.internal.Projection;
import dankscape.api.internal.RSClassWrapper;
import dankscape.api.rs.RSClient;
import dankscape.api.rs.RSGameObject;
import dankscape.api.rs.RSRegion;
import dankscape.api.rs.RSTile;
import dankscape.misc.Vec3;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 *
 * @author Pieterjan
 */
public class Tile extends Interactable
{
    public static final int TILE_SIZE  = 128;
    public static final int REGION_SIZE = 104;
    
    //private static final HashMap<Integer, HashMap<RSRegion, List<Tile>>> regionCache = new HashMap();
    private static int lastBaseX, lastBaseY;
    private static final ReentrantLock cacheLock = new ReentrantLock();
    private static final HashMap<RSTile, Tile> tilePeerCache = new HashMap();
    
    private int indexX = 0, indexY = 0, indexPlane = 0; 
    
    private final RSRegion region;
    private RSTile peer;
    
    public Tile(RSRegion region, int indexX, int indexY, int indexPlane)
    {
        this.region = region;
        this.indexX = indexX;
        this.indexY = indexY;
        this.indexPlane = indexPlane;
    }
    
    private int getTileHeight()
    {
        int curPlane      = indexPlane;
        byte[][][] flags  = RSClient.getTileSettings();
        int[][][] heights = RSClient.getTileHeights();
        
        if (curPlane < 3 && (flags[1][indexX][indexY] & 0x2) == 2)
            curPlane++;
        
        return heights[curPlane][indexX][indexY];
    }
    
    public byte getFlags()
    {
        byte flags = 0;
        byte[][][] tileFlags  = RSClient.getTileSettings();
        try
        {
            flags = tileFlags[indexPlane][indexX][indexY];
        }
        catch(Exception ex){}
        return flags;
    }
    
    public RSTile getPeer()
    {
        if(peer != null)
            return peer;
        
        if(region == null)
            return null;
        
        Object[][][] peers = region.getRSRef_Tiles();
        if(peers != null)
            if(peers[indexPlane] != null)
                if(peers[indexPlane][indexX] != null)
                    peer = (RSTile)RSClassWrapper.getWrapper(peers[indexPlane][indexX][indexY]);
        
        if(peer != null)
        {
            cacheLock.lock();
            try {
                tilePeerCache.put(peer, this);
            } finally {
                cacheLock.unlock();
            }
        }
        
        return peer;
    }
    
    public Tile getNeighbour(int offsetX, int offsetY) // Returns the tile itself if offsetX/Y is 0
    {
        if(indexX + offsetX >= REGION_SIZE || indexY + offsetY >= REGION_SIZE)
            return null; // Out of bounds
        
        RSTile targetPeer = null;
        Object[][][] peers = region.getRSRef_Tiles();
        if(peers != null)
            if(peers[indexPlane] != null)
                if(peers[indexPlane][indexX + offsetX] != null)
                    targetPeer = (RSTile)RSClassWrapper.getWrapper(peers[indexPlane][indexX + offsetX][indexY + offsetY]);
        
        if(targetPeer != null)
            return forPeer(targetPeer, region, indexX + offsetX, indexY + offsetY, indexPlane);
        
        // WARNING: calling this multiple times when the peer isn't available yet may result in multiple Tile instances created for the same tile!
        return new Tile(region, indexX + offsetX, indexY + offsetY, indexPlane);
    }
    
    public int getSceneX()
    {
        return indexX * TILE_SIZE;
    }
    
    public int getSceneZ()
    {
        return indexY * TILE_SIZE;
    }
    
    public int getSceneY()
    {
        return -getTileHeight();
    }
    
    @Override
    public int getX()
    {
        return RSClient.getBaseX() + indexX;
    }
    
    @Override
    public int getY()
    {
        return RSClient.getBaseY() + indexY;
    }
    
    public int getPlane()
    {
        return indexPlane;
    }
    
    private Vec3 getViewPos()
    {
        return Projection.transform(new Vec3(getSceneX(), getSceneY(), getSceneZ()));
    }
    
    private Point getScreenPos()
    {
        return Projection.project(getViewPos());
    }
    
    public List<GameObject> getGameObjects()
    {
        peer = getPeer();
        ArrayList<GameObject> objects = new ArrayList();
        if(peer != null)
        {
            RSGameObject[] objs = peer.getObjects();
            for(RSGameObject o : objs)
            {
                if(o != null)
                {
                    GameObject go = GameObject.forPeer(o);
                    go.parentTile = this;
                    objects.add(go);
                }
            }
        }
        return objects;
    }

    
    // Gets the precise terrain height for the given scene X-Z coords by interpolating tiles with their neighbours
    public static int getGroundHeight(int sceneX, int sceneZ)
    {
        int tileX = RSClient.getBaseX() + (int)Math.floor(sceneX / (double)TILE_SIZE);
        int tileY = RSClient.getBaseY() + (int)Math.floor(sceneZ / (double)TILE_SIZE);
        Tile tile = get(RSClient.getScene_plane(), tileX, tileY);
        
        if(tile == null)
            return 0;
        
        if(tile.getNeighbour(0, 1) == null || tile.getNeighbour(1, 1) == null || tile.getNeighbour(1, 0) == null)
            return -tile.getTileHeight();
        
        //
        // A ------------ C
        //  |            |
        //  |            |
        //  |   .        |
        //  |            |
        // O ------------ B
        //
        // Neighbour layout
        
        double affVert = (sceneZ - tile.getSceneZ()) / (double)TILE_SIZE;
        double affHor  = (sceneX - tile.getSceneX()) / (double)TILE_SIZE;
        
        double heightOA = tile.getTileHeight() * (1.0 - affVert) + tile.getNeighbour(0, 1).getTileHeight() * affVert;
        double heightBC = tile.getNeighbour(1, 0).getTileHeight() * (1.0 - affVert) + tile.getNeighbour(1, 1).getTileHeight() * affVert;
        double height   = heightOA * (1.0 - affHor) + heightBC * affHor;
        return (int)-height;
    }
    
    public Vec3 getRandomWorldPoint()
    {
        if(getNeighbour(0, 1) == null || getNeighbour(1, 1) == null || getNeighbour(1, 0) == null)
            return new Vec3(getSceneX(), getSceneY(), getSceneZ());
        
        //
        // A ------------ C
        //  |            |
        //  |            |
        //  |   .        |
        //  |            |
        // O ------------ B
        //
        // Neighbour layout
        
        double affVert = Misc.random(0.2, 0.8);
        double affHor  = Misc.random(0.2, 0.8);
        
        double heightOA = getTileHeight() * (1.0 - affVert) + getNeighbour(0, 1).getTileHeight() * affVert;
        double heightBC = getNeighbour(1, 0).getTileHeight() * (1.0 - affVert) + getNeighbour(1, 1).getTileHeight() * affVert;
        double height   = heightOA * (1.0 - affHor) + heightBC * affHor;
        
        return new Vec3(getSceneX() + affHor * TILE_SIZE, -height, getSceneZ() + affVert * TILE_SIZE);
    }
    
    public static Tile forPeer(RSTile peer, RSRegion region, int x, int y, int plane)
    {
        if(peer == null)
            return new Tile(region, x, y, plane);
        
        cacheLock.lock();
        Tile t = null;
        try
        {
            t = tilePeerCache.get(peer);
            if(t == null)
            {
                t = new Tile(region, x, y, plane);
                tilePeerCache.put(peer, t);
            }
        } finally
        {
            cacheLock.unlock();
        }
        return t;
    }
    
    public static void renderDebug(Graphics g)
    {
        Projection.loadIdentity();

        List<Tile> tiles = get(Game.getCurrentPlane()).stream()
                .filter(tile -> tile.isOnScreen()).collect(Collectors.toList());
        
        Color transblue = new Color(0, 0, 255, 128);
        
        for(Tile tile : tiles)
        {
            Point p1 = tile.getScreenPos();
            
            g.setColor(Color.BLUE);
            if(tile.getNeighbour(1, 0) != null)
            {
                Point p2 = tile.getNeighbour(1, 0).getScreenPos();
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
            
            if(tile.getNeighbour(0, 1) != null)
            {
                Point p2 = tile.getNeighbour(0, 1).getScreenPos();
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
            
            if(tile.getNeighbour(1, 0) != null && tile.getNeighbour(0, 1) != null && tile.getNeighbour(1, 1) != null)
            {
                p1 = tile.getNeighbour(1, 0).getScreenPos();               
                Point p2 = tile.getNeighbour(1, 1).getScreenPos();
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
                
                p1 = tile.getNeighbour(0, 1).getScreenPos();               
                p2 = tile.getNeighbour(1, 1).getScreenPos();
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
                
                if(tile.isHovering())
                {
                    g.setColor(transblue);
                    g.fillPolygon(tile.getAWTPolygon());
                }
            }
        }
    }
    
    public static Tile get(int plane, int x, int y)
    {
        RSRegion region = RSClient.getRegion();
        if(region == null)
            return null;
        
        x -= RSClient.getBaseX();
        y -= RSClient.getBaseY();
        
        Object[][][] tilesRef   = region.getRSRef_Tiles();
        if(tilesRef == null)
            return null;
        if(plane >= 0 && plane < tilesRef.length)
            if(tilesRef[plane] != null)
                if(x >= 0 && x < tilesRef[plane].length)
                    if(tilesRef[plane][x] != null)
                        if(y >= 0 && y < tilesRef[plane][x].length)
                        {
                            RSTile wrapper = (RSTile)RSClassWrapper.getWrapper(tilesRef[plane][x][y]);
                            return forPeer(wrapper, region, x, y, plane);
                        }
        return null;
    }
    
    public static List<Tile> get(int plane)
    {
        ArrayList<Tile> tiles = new ArrayList();
        RSRegion region = RSClient.getRegion();
        if(region == null)
            return tiles;

        Object[][][] tilesRef   = region.getRSRef_Tiles();
        if(tilesRef == null)
            return tiles;
        
        if(plane >= 0 && plane < tilesRef.length)
        {
            if(tilesRef[plane] != null)
                for(int i = 0;i < tilesRef[plane].length;i++)
                {
                    if(tilesRef[plane][i] != null)
                        for(int j = 0;j < tilesRef[plane][i].length;j++)
                        {
                            RSTile wrapper = (RSTile)RSClassWrapper.getWrapper(tilesRef[plane][i][j]);
                            tiles.add(forPeer(wrapper, region, i, j, plane));
                        }
                }
        }
        return tiles;
    }

    @Override
    public Point getRandomPoint()
    {
        Projection.loadIdentity();
        Vec3 p = getRandomWorldPoint();
        //debug("Random point in tile(" + getX() + ", " + getY() +")");
        //p.dump();
        return Projection.project(Projection.transform(p));
    }

    @Override
    public String getName()
    {
        return "";
    }
    
    public List<Point> getPolygon() // Returns the tile polygon in clockwise order
    {
        ArrayList<Vec3> v = new ArrayList();
        v.add(new Vec3(getSceneX(), getSceneY(), getSceneZ()));
        v.add(new Vec3(getNeighbour(0, 1).getSceneX(), getNeighbour(0, 1).getSceneY(), getNeighbour(0, 1).getSceneZ()));
        v.add(new Vec3(getNeighbour(1, 1).getSceneX(), getNeighbour(1, 1).getSceneY(), getNeighbour(1, 1).getSceneZ()));
        v.add(new Vec3(getNeighbour(1, 0).getSceneX(), getNeighbour(1, 0).getSceneY(), getNeighbour(1, 0).getSceneZ()));
        
        Projection.loadIdentity();
        return Projection.project(Projection.transform(v));
    }
    
    public Polygon getAWTPolygon()
    {
        List<Point> vertices = getPolygon();
        Polygon p = new Polygon();
        vertices.forEach(v -> p.addPoint(v.x, v.y));
        return p;
    }
    
    public boolean isFacingCamera()
    {
        Projection.loadIdentity();
        
        if(getNeighbour(0, 1) == null || getNeighbour(1, 1) == null || getNeighbour(1, 0) == null)
            return false;
        
        Vec3 vO = Projection.transform(new Vec3(getSceneX(), getSceneY(), getSceneZ()));

        Vec3 v1 = Projection.transform(new Vec3(getNeighbour(0, 1).getSceneX(), getNeighbour(0, 1).getSceneY(), getNeighbour(0, 1).getSceneZ()))
                .subtract(vO).normalize();
        
        Vec3 v2 = Projection.transform(new Vec3(getNeighbour(1, 0).getSceneX(), getNeighbour(1, 0).getSceneY(), getNeighbour(1, 0).getSceneZ()))
                .subtract(vO).normalize();
        
        Vec3 view = new Vec3(0.0, 0.0, -1.0);
        

        Vec3 normalA = v1.cross(v2).normalize();
        
        vO = Projection.transform(new Vec3(getNeighbour(1, 0).getSceneX(), getNeighbour(1, 0).getSceneY(), getNeighbour(1, 0).getSceneZ()));
        
        
        v1 = Projection.transform(new Vec3(getSceneX(), getSceneY(), getSceneZ()))
                .subtract(vO).normalize();
        
        v2 = Projection.transform(new Vec3(getNeighbour(1, 1).getSceneX(), getNeighbour(1, 1).getSceneY(), getNeighbour(1, 1).getSceneZ()))
                .subtract(vO).normalize();
        
        Vec3 normalB = v1.cross(v2).normalize();
        
        Vec3 normal = normalA.add(normalB).div(2.0);

        return Math.acos(view.dot(normal)) < (90.0 * Math.PI / 180.0);
    }
    
    @Override
    public boolean isOnScreen()
    {
        Projection.loadIdentity();
        Vec3 view = getViewPos();
        Point p = Projection.project(view);
        
        
        return Misc.isPointInRect(p, new Rectangle(0, 0, (int)Projection.getWidth(), (int)Projection.getHeight())) &&
                Projection.isInFrustum(view) && isFacingCamera();
    }
    
    @Override 
    public void bringOnScreen()
    {
        Game.lookAt(new Vec3(getSceneX(), getSceneY(), getSceneZ()));
        ActionContext.get().pressKey(KeyEvent.VK_UP, o -> 
        {
            return isFacingCamera() ||
                    (Projection.CAMERAPITCH_MAX - Projection.getCameraPitch() < 2.0 * Math.PI / 180.0);
        });
    }
    
    @Override 
    public boolean isHovering()
    {
        if(!isOnScreen())
            return false;
        
        List<Point> poly = getPolygon();
        Collections.reverse(poly);
        return Misc.isPointInsidePolygonCCW(Input.getMousePos(), poly);
    }
    
    
}
