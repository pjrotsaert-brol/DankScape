/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

import dankscape.nativeinterface.NativeInterface;

/**
 *
 * @author Pieterjan
 */
public class Vec3
{
    public double x, y, z;
    
    public Vec3(){}
    public Vec3(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vec3(int x, int y, int z)
    {
        this.x = (double)x;
        this.y = (double)y;
        this.z = (double)z;
    }
    public Vec3(Vec3 other)
    {
        x = other.x;
        y = other.y;
        z = other.z;
    }
    
    public Vec3 subtract(Vec3 other)
    {
        Vec3 r = new Vec3(this);
        r.x -= other.x;
        r.y -= other.y; 
        r.z -= other.z;
        return r;
    }
    public Vec3 add(Vec3 other)
    {
        Vec3 r = new Vec3(this);
        r.x += other.x;
        r.y += other.y; 
        r.z += other.z;
        return r;
    }
    
    public double getMagnitude()
    {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    public Vec3 div(double divider)
    {
        Vec3 r = new Vec3(this);
        r.x /= divider;
        r.y /= divider;
        r.z /= divider;
        return r;
    }
    
    public Vec3 normalize()
    {
        Vec3 r = new Vec3(this);
        double mag = getMagnitude();
        r.x /= mag;
        r.y /= mag;
        r.z /= mag;
        return r;
    }
    
    public Vec3 cross(Vec3 other)
    {
        Vec3 r = new Vec3(this);
        r.x = y * other.z - z * other.y;
        r.y = z * other.x - x * other.z;
        r.z = x * other.y - y * other.x;
        return r;
    }
    
    public Vec3 neg()
    {
        Vec3 r = new Vec3(this);
        r.x = -x;
        r.y = -y;
        r.z = -z;
        return r;
    }
    
    public double dot(Vec3 other)
    {
        return x * other.x + y * other.y + z * other.z;
    }
    
    public void dump()
    {
        NativeInterface.println("{ " + x + ", " + y + ", " + z + "}");
    }
    
}
