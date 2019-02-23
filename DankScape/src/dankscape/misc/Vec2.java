/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

import java.awt.Point;

/**
 *
 * @author Pieterjan
 */
public class Vec2
{
    public double x1, y1, x2, y2;
    
    public Vec2(double x1, double y1, double x2, double y2)
    {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
    
    public Vec2(int x1, int y1, int x2, int y2)
    {
        this.x1 = (double)x1;
        this.x2 = (double)x2;
        this.y1 = (double)y1;
        this.y2 = (double)y2;
    }
    
    public Vec2()
    {
    }
    
    public double length()
    {
        double xDiff = x2 - x1;
        double yDiff = y2 - y1;
        return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
    }
    
    public static double length(int x1, int y1, int x2, int y2)
    {
        double xDiff = x2 - x1;
        double yDiff = y2 - y1;
        return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
    }
    
    public Point getPoint(double progress)
    {
        if(x1 == x2 && y1 == y2)
            return new Point((int)x1, (int)y1);
        
        double ang = Math.atan2(y2 - y1, x2 - x1);
        double len = length();
        return new Point((int)(Math.cos(ang) * progress * len + x1), (int)(Math.sin(ang) * progress * len + y1));
    }
}
