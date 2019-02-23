/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api.internal;

import dankscape.api.Game;
import dankscape.api.Misc;
import dankscape.api.Model;
import dankscape.api.Tile;
import dankscape.api.rs.RSActor;
import dankscape.api.rs.RSClient;
import dankscape.misc.Mat4;
import dankscape.misc.Vec3;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

/**
 *
 * @author Pieterjan
 */
public abstract class Actor <T extends RSActor, TCOMP extends RSClassWrapper> extends Interactable
{
    protected T peer;
    
    public Actor(T peer)
    {
        this.peer = peer;
    }
    
    public abstract Model getModel();
    
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
    
    public Vec3 getCenter()
    {
        return new Vec3(getSceneX(), getSceneY() + getHeight() / 2, getSceneZ());
    }
    
    public int getX()
    {
        return RSClient.getBaseX() + (getSceneX() / Tile.TILE_SIZE);
    }
    
    public int getY()
    {
        return RSClient.getBaseY() + (getSceneZ() / Tile.TILE_SIZE);
    }
    
    public int getHeight()
    {
        //NativeInterface.println("Height: " + peer.getModelHeight() + " hScale: " + getDef().getHeightScale());
        return (int)(peer.getModelHeight() * getHeightScale());
    }
    
    public int getWidth()
    {
        Model model = getModel();
        if(model != null)
            return (int)(model.computeWidth() * getWidthScale());
        else
            return (int)(50 * getWidthScale());
    }
    
    public int getDepth()
    {
        Model model = getModel();
        if(model != null)
            return (int)(model.computeDepth() * getWidthScale());
        else
            return (int)(50 * getWidthScale());
    }
    
    public abstract double getWidthScale();
    public abstract double getHeightScale();
    
    public double getRotation()
    {
        return (peer.getAngle() / 2048.0) * Math.PI * 2;
    }
   
    public abstract TCOMP getDef();
    
    public T getPeer()
    {
        return peer;
    }
    
    public abstract List<String> getActions();
    
    public abstract int getCombatLevel();
    
    public double distanceTo(Vec3 point)
    {
        Vec3 diff = point.subtract(getCenter());
        return diff.getMagnitude();
    }
    
    
    public Vec3 getRandomWorldPoint()
    {
        Model m = getModel();
        if(m != null)
            return m.getRandomVertex();
        
        return new Vec3(0, Misc.random(10, getHeight() - 10), 0);
    }

    @Override
    public Point getRandomPoint()
    {
        Mat4 t = new Mat4();
        t.translate(getSceneX(), getSceneY(), getSceneZ());
        t.rotateY(getRotation());
        t.scale(getWidthScale(), getHeightScale(), getWidthScale());
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