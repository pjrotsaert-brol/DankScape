/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import dankscape.api.internal.Actor;
import dankscape.api.internal.Projection;
import dankscape.api.rs.RSClient;
import dankscape.api.rs.RSNPC;
import dankscape.api.rs.RSNPCComposition;
import dankscape.bot.DebugRenderer;
import dankscape.bot.DebugSettings;
import dankscape.misc.Mat4;
import dankscape.misc.Vec3;
import dankscape.nativeinterface.NativeInterface;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author Pieterjan
 */
public class NPC extends Actor<RSNPC, RSNPCComposition>
{
    private Model model;
    
    private static HashMap<RSNPC, NPC> npcCache = new HashMap();
    
    public NPC(RSNPC peer)
    {
        super(peer);
    }
    
    @Override
    public Model getModel()
    {
        if(model != null)
            return model;
        
        Model m = Model.getModel(getId());
        if(m != null)
        {
            //NativeInterface.println("Found model: " + getId());
            model = m;
            return m;
        }
        
        /*if(model == null)
        {
            String out = "ModelList :";
            
            if(getDef() == null)
                out += "DEF IS NULL!";
            else if(getDef().getModels() == null)
                out += "MODELS IS NULL!";
            else
            {
                for(int id : getDef().getModels())
                    out += " " + id + "(" + (id << 8) + ")";
            }

            NativeInterface.println(out + "\nModelCacheSize: " + Model.getCacheSize());
            NativeInterface.println("Unable to find model: " + getId());
        }*/
        return model;
    }

    @Override
    public double getWidthScale()
    {
        return peer.getComposition().getWidthScale()/ 128.0;
    }
    
    @Override
    public double getHeightScale()
    {
        return peer.getComposition().getHeightScale()/ 128.0;
    }

    public int getId()
    {
        return peer.getComposition().getId();
    }
    
    @Override
    public String getName()
    {
        return peer.getComposition().getName();
    }
    

    @Override
    public List<String> getActions()
    {
        return Arrays.stream(peer.getComposition().getActions()).filter(a -> a != null).collect(Collectors.toList());
    }
    
    @Override
    public int getCombatLevel()
    {
        return peer.getComposition().getCombatLevel();
    }
    
    public static List<NPC> get(int id)
    {
        return getNearby(npc -> npc.getId() == id);
    }
    
    public static NPC getSingle(int id)
    {
        return getSingle(npc -> npc.getId() == id);
    }
    
    public static NPC getSingle(Predicate<NPC> predicate)
    {
        List<NPC> results = getNearby(predicate);
        if(results.size() > 0)
            return results.get(0);
        return null;
    }
    
    public static List<NPC> getNearby()
    {
        ArrayList<NPC> npcs = new ArrayList();
        List<RSNPC> peers = Arrays.stream(RSClient.getCachedNPCs()).filter(npc -> npc != null).collect(Collectors.toList()); 
        for(RSNPC peer : peers)
        {
            NPC npc = npcCache.get(peer);
            if(npc == null)
            {
                npc = new NPC(peer);
                npcCache.put(peer, npc);
            }
            npcs.add(npc);
        }
        return npcs;
    }
    
    public static List<NPC> getNearby(Predicate<NPC> predicate)
    {
        return getNearby().stream().filter(predicate).collect(Collectors.toList());
    }
    
    public static void renderDebug(Graphics g)
    {
        List<NPC> npcs = getNearby();
        
        g.setColor(Color.yellow);
        for(NPC npc : npcs)
        {
            Vec3 pos = new Vec3(npc.getSceneX(), npc.getSceneY() + npc.getHeight(), npc.getSceneZ());
            Vec3 view = Projection.transform(pos);
            if(Projection.isInFrustum(view))
            {
                Point p = Projection.project(view);
                
                String str = npc.getName();
                
                if(DebugSettings.showPosition)
                    str += " (" + npc.getX() + ", " + npc.getY() + ")";
                
                g.drawString(str, p.x - g.getFontMetrics().stringWidth(str) / 2, p.y - 4);
                str = "" + npc.getId();
                if(DebugSettings.showAnimationId)
                    str += " (animId: " + npc.getPeer().getAnimation() + ")";
                
                g.drawString(str, p.x - g.getFontMetrics().stringWidth(str) / 2, p.y - 17);
                
                g.fillOval(p.x, p.y, 3, 3);
            }
        }
    }
    
    public static void renderModelDebug(Graphics g)
    {
        List<NPC> npcs = getNearby();
        
        g.setColor(Color.yellow);
        for(NPC npc : npcs)
        {
            Vec3 pos = new Vec3(npc.getSceneX(), npc.getSceneY() + npc.getHeight(), npc.getSceneZ());
            Vec3 view = Projection.transform(pos);
            if(Projection.isInFrustum(view))
            {
                DebugRenderer.drawCube(g, npc.getSceneX(), npc.getSceneY() + npc.getHeight() / 2, npc.getSceneZ(), 
                        npc.getWidth(), npc.getDepth(), npc.getHeight(), 0.0, npc.getRotation());
                
                Mat4 t = new Mat4();
                t.translate(npc.getSceneX(), npc.getSceneY(), npc.getSceneZ());
                t.rotateY(npc.getRotation());
                t.scale(npc.getWidthScale(), npc.getHeightScale(), npc.getWidthScale());
                
                //if(npc.getName().toLowerCase().contains("duck"))
                //     NativeInterface.println(""+npc.peer.getAngle());
                
                Projection.setModelMatrix(t);
                if(npc.model != null)
                    npc.model.renderWireframe(g);
                Projection.loadIdentity();
            }
        }
    }

    @Override
    public RSNPCComposition getDef()
    {
        return peer.getComposition();
    }
}
