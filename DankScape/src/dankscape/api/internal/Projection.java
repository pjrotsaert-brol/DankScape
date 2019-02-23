/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api.internal;

import dankscape.api.Misc;
import dankscape.api.Tile;
import dankscape.api.rs.RSClient;
import dankscape.loader.AppletLoader;
import dankscape.misc.Mat4;
import dankscape.misc.Vec3;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


/**
 *
 * @author Pieterjan
 */
public class Projection
{
    public static final double CAMERAPITCH_MIN = 22.0 * Math.PI / 180.0;
    public static double CAMERAPITCH_MAX = 67.50 * Math.PI / 180.0;
    
    private static Mat4 viewMatrix = new Mat4();
    private static Mat4 modelMatrix = new Mat4();
    private static Mat4 modelViewMatrix = new Mat4();
    
    private static ReentrantLock lock = new ReentrantLock();
    private static ArrayList<Mat4> savedModelMatrices = new ArrayList();
    
    private static double fovScaleFactor = 0.000118469956287061224;
    
    // These are used to roughly check if the mouse isn't hovering over one of the HUD elements
    private static final int CHATWINDOW_W = 525;
    private static final int CHATWINDOW_H = 170;
    
    private static final int INVENTORY_W = 245;
    private static final int INVENTORY_H = 340;
    
    private static final int MINIMAP_W = 220;
    private static final int MINIMAP_H = 175;
    
    // User settable stuff
    private static double zNear = 0.1, zFar = 100.0, vpWidth = 100.0, vpHeight = 100.0, fovX = 80.0, fovY = 60.0, vpScale = 1.0;
    
    // Calculated stuff
    private static double planeHalfH = 0.0, planeHalfW = 0.0;
    
    
    public static double getCameraYaw()
    {
        return (RSClient.getCameraYaw() / 2048.0) * Math.PI * 2.0;
    }
    public static double getCameraPitch()
    {
        return (RSClient.getCameraPitch() / 384.0) * CAMERAPITCH_MAX;
    }
    
    public static Vec3 getCameraPosition()
    {
        return new Vec3(RSClient.getCameraX(), -RSClient.getCameraZ(), RSClient.getCameraY());
    }
    
    public static double getAspectRatio()
    {
        return getWidth() / getHeight();
    }
    
    public static double getZNear()
    {
        return zNear;
    }
    
    public static double getZFar()
    {
        return zFar;
    }
    
    public static double getViewportScale()
    {
        return vpScale;
    }
    
    public static boolean isInFrustum(Vec3 v) // Vector must be in View-space!
    {
        if(v.z < zNear || v.z > zFar)
            return false;
        
        double frustumW = planeHalfW * (v.z / zNear);
        double frustumH = planeHalfH * (v.z / zNear);
        if(Math.abs(v.x) <= frustumW && Math.abs(v.y) <= frustumH)
            return true;
        return false;
    }
    
    public static double getWidth()
    {
        return (double)AppletLoader.getSingleton().getGameCanvas().getWidth();
    }
    
    public static double getHeight()
    {
        return (double)AppletLoader.getSingleton().getGameCanvas().getHeight();
    }
    
    public static void update()
    {
        // These values were found out by experiment, don't question them, bitch.
        double fovx = 2.0 * Math.atan((getWidth() * fovScaleFactor) / zNear) * 180.0 / Math.PI;
        double fovy = 2.0 * Math.atan((getHeight() *  fovScaleFactor) / zNear) * 180.0 / Math.PI;
        // fovy factor: 0.000117185762706122448
        setProjection(getWidth(), getHeight(), fovx, fovy, 
                0.1, 25.0 * Tile.TILE_SIZE, RSClient.getScale() / 422.0);
    }
    
    public static double getFovX()
    {
        return fovX * 180.0 / Math.PI;
    }
    
    public static double getFovY()
    {
        return fovY * 180.0 / Math.PI;
    }
    
    public static void setProjection(double w, double h, double fovx, double fovy, double near, double far, double scale)
    {
        lock.lock();
        try
        {
            vpScale = scale;
            vpWidth = w;
            vpHeight = h;
            fovX = fovx * Math.PI / 180.0;
            zNear = near;
            zFar = far;
            fovY = fovy * Math.PI / 180.0;
            planeHalfW = zNear * Math.tan(fovX / 2.0);
            planeHalfH = zNear * Math.tan(fovY / 2.0);
            
            Vec3 eye = getCameraPosition();
            viewMatrix = new Mat4();
            viewMatrix.rotateX(-getCameraPitch());
            viewMatrix.rotateY(getCameraYaw());
            viewMatrix.translate(-eye.x, -eye.y, -eye.z);
            
            modelViewMatrix = viewMatrix.mult(modelMatrix);
        } 
        finally
        {
            lock.unlock();
        }
    }

    public static Point project(Vec3 p) // Projects a Vertex from View-coordinates to the screen.
    {
        double x = vpScale * ((vpWidth / 2.0) * (zNear * (p.x / p.z)) / planeHalfW) + vpWidth / 2.0;
        double y = vpScale * -((vpHeight / 2.0) * (zNear * (p.y / p.z)) / planeHalfH) + vpHeight / 2.0;
        return new Point((int)x, (int)y);
    }
    
    public static List<Point> project(List<Vec3> input)
    {
        return input.stream().map(vec -> project(vec)).collect(Collectors.toList());
    }
    
    public static Vec3 transform(Vec3 p) // Transforms a Vertex from RS World-coordinates to View-coordinates
    {
        return modelViewMatrix.mult(p);
    }
    
    public static List<Vec3> transform(List<Vec3> input)
    {
        return input.stream().map(vec -> transform(vec)).collect(Collectors.toList());
    }
    
    public static void loadIdentity()
    {
        lock.lock();
        try
        {
            modelMatrix.setIdentity();
            modelViewMatrix = viewMatrix.mult(modelMatrix);
        } 
        finally
        {
            lock.unlock();
        }
    }
    
    public static void setModelMatrix(Mat4 mat)
    {
        lock.lock();
        try
        {
            modelMatrix = mat;
            modelViewMatrix = viewMatrix.mult(modelMatrix);
        } 
        finally
        {
            lock.unlock();
        }
    }
    
    public static Mat4 getModelMatrix() // Replace with translate/scale/rotateX/Y/Z with locks!
    {
        return modelMatrix;
    }
    
    public static void pushMatrix()
    {
        lock.lock();
        try
        {
            savedModelMatrices.add(modelMatrix);
        } 
        finally
        {
            lock.unlock();
        }
    }
    
    public static void popMatrix()
    {
        lock.lock();
        try
        {
            setModelMatrix(savedModelMatrices.remove(savedModelMatrices.size() - 1));
        } 
        finally
        {
            lock.unlock();
        }
    }
    
    private static boolean isOverUI(Point p)
    {
        return Misc.isPointInRect(p, new Rectangle((int)getWidth() - INVENTORY_W, (int)getHeight() - INVENTORY_H, INVENTORY_W, INVENTORY_H)) || 
               Misc.isPointInRect(p, new Rectangle(0, (int)getHeight() - CHATWINDOW_H, CHATWINDOW_W, CHATWINDOW_H)) ||
               Misc.isPointInRect(p, new Rectangle((int)getWidth() - MINIMAP_W, 0, MINIMAP_W, MINIMAP_H));
    }
    
    public static boolean isOnScreen(Vec3 world)
    {
        loadIdentity();
        Vec3 view = transform(world);
        Point p = project(view);
        return isInFrustum(view) && 
                Misc.isPointInRect(p, new Rectangle(30, 30, (int)getWidth() - 60, (int)getHeight() - 60)) && 
                !isOverUI(p);
    }

}
