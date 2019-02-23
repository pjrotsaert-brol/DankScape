/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot.tasks;

import dankscape.api.Input;
import dankscape.api.Misc;
import dankscape.bot.BotTask;
import dankscape.misc.Vec2;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 *
 * @author Pieterjan
 */
public class MouseMoveTask extends BotTask
{
    public static final int DEFAULT_SPEEDPER100 = 300;
    
    private double speedPer100;
    
    private int originX = 0, originY = 0;
    private int destX = 0, destY = 0;
    private boolean started = false;
    private long tStart = 0;
    private long tTotal = 1, tLast = 0;
    
    private Point[] curvePoints = null;
    
    private double curveSteepness = 0.5;
    private double curveCenter = 0.5;
    
    public MouseMoveTask(int x, int y)
    {
        destX = x;
        destY = y;
        speedPer100 = DEFAULT_SPEEDPER100;
    }
    
    @Override
    public void update()
    {
        if(Input.userMovedMouse)
        {
            started = false; // Restart the task if the user moves the mouse manually
            Input.userMovedMouse = false;
        }
        
        if(!started)
        {
            started = true;
            originX = Input.mouseX;
            originY = Input.mouseY;
            tStart = System.currentTimeMillis();
            
            Vec2 vec = new Vec2(originX, originY, destX, destY);
            speedPer100 = Misc.random(75.0, 140.0) + DEFAULT_SPEEDPER100 / (vec.length() / 100.5);
            
            
            tTotal = (long)(vec.length() * (double)speedPer100 / 100.0);
            if(tTotal <= 0)
                tTotal = 1;
            
            int pCount = 2 + (int)(Math.floor(vec.length() / 125));
            
            double maxOffset = (vec.length() / (pCount + 1)) * 3.0;
            if(maxOffset < 70.0)
                maxOffset = 70;
            
            curvePoints = new Point[pCount + 3];
            curvePoints[0] = new Point(0, 0);
            curvePoints[curvePoints.length - 1] = new Point(destX - originX, destY - originY);
            curvePoints[curvePoints.length - 2] = new Point(destX - originX, destY - originY);
            for(int i = 0;i < pCount;i++)
            {
                double prog = (1.0 / (pCount)) * (i + 1.0);
                Point p = vec.getPoint(prog);
                curvePoints[i + 1] = Misc.randomPointInCircle(new Point(p.x - originX, p.y - originY), 0.0, maxOffset);
            }
            curveSteepness = Misc.random(0.2, 0.3);
            curveCenter    = Misc.random(0.1, 0.25);
        }
        
        long tCurrent = System.currentTimeMillis();
        double progress = Misc.smoothstepEx(Misc.clamp((double)(tCurrent - tStart) / (double)tTotal, 0.0, 1.0), curveSteepness, curveCenter);
           
        Point p = Misc.bezierCurve(progress, curvePoints);
        
        if(tCurrent > tLast)
            Input.moveMouse(p.x + originX, p.y + originY);
        
        tLast = tCurrent;
        if(tCurrent - tStart > tTotal)
            exit();
    }
    
    @Override
    public void paint(Graphics g)
    {
        // DEBUG DRAW
        
        g.setColor(Color.red);
        g.fillOval(destX - 3, destY - 3, 6, 6);
        /*
        
        g.fillOval(originX - 3, originY - 3, 6, 6);
        g.drawLine(destX, destY, originX, originY);
        
        if(curvePoints != null)
        {
            g.setColor(Color.green);
            for(int i = 0;i < curvePoints.length;i++)
            {
                g.fillOval(curvePoints[i].x - 3 + originX, curvePoints[i].y - 3 + originY, 6, 6);
            }
        }*/
    }
    
}
