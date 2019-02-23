/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import dankscape.api.internal.Actor;
import dankscape.api.internal.Projection;
import dankscape.api.rs.RSClient;
import dankscape.api.rs.RSModel;
import dankscape.api.rs.RSPlayer;
import dankscape.api.rs.RSPlayerComposition;
import dankscape.bot.DebugRenderer;
import dankscape.misc.Mat4;
import dankscape.misc.Vec3;
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
public class Player extends Actor<RSPlayer, RSPlayerComposition>
{
    private Model model;
    
    private static HashMap<RSPlayer, Player> playerCache = new HashMap();
    
    public Player(RSPlayer peer)
    {
        super(peer);
    }
    
    @Override
    public Model getModel()
    {
        RSModel modelPeer = peer.getModel();
        if(model == null && modelPeer != null)
            model = new Model(peer.getModel());
        return model;
    }
    

    @Override
    public double getWidthScale()
    {
        return 1.0;
    }
    
    @Override
    public double getHeightScale()
    {
        return 1.0;
    }

    @Override
    public String getName()
    {
        return peer.getName();
    }
    
    @Override
    public RSPlayerComposition getDef()
    {
        return peer.getComposition();
    }
    

    @Override
    public List<String> getActions()
    {
        return Arrays.stream(peer.getActions()).filter(a -> a != null).collect(Collectors.toList());
    }
    
    @Override
    public int getCombatLevel()
    {
        return peer.getCombatLevel();
    }
    
    public static Player getLocal()
    {
        Player pc = playerCache.get(RSClient.getLocalPlayer());
        if(pc == null)
        {    
            pc = new Player(RSClient.getLocalPlayer());
            playerCache.put(RSClient.getLocalPlayer(), pc);
        }
        return pc;
    }

    public static Player getSingle(Predicate<Player> predicate)
    {
        List<Player> results = getNearby(predicate);
        if(results.size() > 0)
            return results.get(0);
        return null;
    }
    
    public static List<Player> getNearby()
    {
        ArrayList<Player> npcs = new ArrayList();
        List<RSPlayer> peers = Arrays.stream(RSClient.getCachedPlayers()).filter(pc -> pc != null).collect(Collectors.toList()); 
        for(RSPlayer peer : peers)
        {
            Player npc = playerCache.get(peer);
            if(npc == null)
            {
                npc = new Player(peer);
                playerCache.put(peer, npc);
            }
            npcs.add(npc);
        }
        return npcs;
    }
    
    public static List<Player> getNearby(Predicate<Player> predicate)
    {
        return getNearby().stream().filter(predicate).collect(Collectors.toList());
    }
    
    public static void renderDebug(Graphics g)
    {
        List<Player> npcs = getNearby();
        
        g.setColor(Color.magenta);
        for(Player npc : npcs)
        {
            Vec3 pos = new Vec3(npc.getSceneX(), npc.getSceneY() + npc.getHeight(), npc.getSceneZ());
            Vec3 view = Projection.transform(pos);
            if(Projection.isInFrustum(view))
            {
                Point p = Projection.project(view);
                
                String str = npc.getName();
                g.drawString(str, p.x - g.getFontMetrics().stringWidth(str) / 2, p.y - 4);
                str = "Level " + npc.getCombatLevel();
                g.drawString(str, p.x - g.getFontMetrics().stringWidth(str) / 2, p.y - 17);
                
                g.fillOval(p.x, p.y, 3, 3);
            }
        }
    }
    
    public static void renderModelDebug(Graphics g)
    {
        List<Player> players = getNearby();
        
        g.setColor(Color.yellow);
        for(Player player : players)
        {
            Vec3 pos = new Vec3(player.getSceneX(), player.getSceneY() + player.getHeight(), player.getSceneZ());
            Vec3 view = Projection.transform(pos);
            if(Projection.isInFrustum(view))
            {
                DebugRenderer.drawCube(g, player.getSceneX(), player.getSceneY() + player.getHeight() / 2, player.getSceneZ(), 
                        player.getWidth(), player.getDepth(), player.getHeight(), 0.0, player.getRotation());
                
                Mat4 t = new Mat4();
                t.translate(player.getSceneX(), player.getSceneY(), player.getSceneZ());
                t.rotateY(player.getRotation());
                t.scale(player.getWidthScale(), player.getHeightScale(), player.getWidthScale());
                
                //if(npc.getName().toLowerCase().contains("duck"))
                //     NativeInterface.println(""+npc.peer.getAngle());
                
                Projection.setModelMatrix(t);
                if(player.getModel() != null)
                    player.getModel().renderWireframe(g);
                Projection.loadIdentity();
            }
        }
    }
}