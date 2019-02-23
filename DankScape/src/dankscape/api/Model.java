/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import dankscape.api.internal.Projection;
import dankscape.api.rs.RSModel;
import dankscape.loader.AppletLoader;
import dankscape.misc.DebugWriter;
import dankscape.misc.Vec3;
import dankscape.nativeinterface.NativeInterface;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.HashMap;

/**
 *
 * @author Pieterjan
 */
public class Model extends DebugWriter
{
    private static final int MIN_REFRESH_INTERVAL = 2000;
    private static long tLastRefresh = 0;
    private static final HashMap<Long, Model> modelCache = new HashMap();
    
    private final RSModel peer;
    
    static
    {
        //setStaticTag("Model");
    }
    
    public Model(RSModel peer)
    {
        this.peer = peer;
    }
    
    public RSModel getPeer()
    {
        return peer;
    }
    
    public static int getCacheSize()
    {
        return modelCache.size();
    }
    
    public void renderWireframe(Graphics g)
    {
        int x[] = peer.getVerticesX();
        int y[] = peer.getVerticesY();
        int z[] = peer.getVerticesZ();
        
        // Triangle vertex indices
        int indicesA[] = peer.getIndices1();
        int indicesB[] = peer.getIndices2();
        int indicesC[] = peer.getIndices3();
        
        Polygon p;
        for(int i = 1;i < indicesA.length && i < indicesB.length && i < indicesC.length;i++)
        {
            p = new Polygon();
            
            Point p1 = Projection.project(Projection.transform(new Vec3(x[indicesA[i]], -y[indicesA[i]], z[indicesA[i]])));
            Point p2 = Projection.project(Projection.transform(new Vec3(x[indicesB[i]], -y[indicesB[i]], z[indicesB[i]])));
            Point p3 = Projection.project(Projection.transform(new Vec3(x[indicesC[i]], -y[indicesC[i]], z[indicesC[i]])));
            
            p.addPoint(p1.x, p1.y);
            p.addPoint(p2.x, p2.y);
            p.addPoint(p3.x, p3.y);
            g.drawPolygon(p);
        }
    }
    
    int getVertexCount()
    {
        return (int)Misc.min(Misc.min(peer.getVerticesX().length, peer.getVerticesY().length), peer.getVerticesZ().length);
    }
    
    public Vec3 getRandomVertex()
    {
        int idx = Misc.random(0, getVertexCount());
        return new Vec3(peer.getVerticesX()[idx], -peer.getVerticesY()[idx], peer.getVerticesZ()[idx]);
        
    }
    
    public int computeWidth()
    {
        int coords[] = peer.getVerticesX();
        if(coords == null)
            return 0;
        if(coords.length <= 0)
            return 0;
        
        int min = coords[0], max = coords[0];
        
        for(int i : coords)
        {
            if(i < min)
                min = i;
            if(i > max)
                max = i;
        }
        
        return max-min;
    }
    
    public int computeDepth()
    {
        int coords[] = peer.getVerticesZ();
        if(coords == null)
            return 0;
        if(coords.length <= 0)
            return 0;
        
        int min = coords[0], max = coords[0];
        
        for(int i : coords)
        {
            if(i < min)
                min = i;
            if(i > max)
                max = i;
        }
        return max-min;
    }
    
    public int computeHeight()
    {
        int coords[] = peer.getVerticesY();
        if(coords == null)
            return 0;
        if(coords.length <= 0)
            return 0;
        
        int min = -coords[0], max = -coords[0];
        
        for(int i : coords)
        {
            i = -i;
            if(i < min)
                min = i;
            if(i > max)
                max = i;
        }
        return max-min;
    }
    
    public static void refreshDefinitions()
    {   
        long tCurrent = System.currentTimeMillis();
        if(tCurrent - tLastRefresh < MIN_REFRESH_INTERVAL)
            return;
        
        tLastRefresh = tCurrent;
        
        sdebug("Refreshing model definitions...");
        Object[] objects = NativeInterface.getAllInstancesOfClass(AppletLoader.getSingleton().getHooks().get("Model").clazz);
        modelCache.clear();
        for(Object obj : objects)
        {
            RSModel comp = new RSModel(obj);
            modelCache.put(comp.getHash(), new Model(comp));
        }
    }
    
    public static Model getModel(int id)
    {
        Model comp = modelCache.get((long)id);

        if(comp == null)
            refreshDefinitions();
        else if(comp.peer.getRSObjectReference() == null)
            refreshDefinitions();
        else
            return comp;
        
        return modelCache.get((long)id);
    }
}
