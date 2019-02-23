/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import dankscape.api.rs.RSHashTable;
import dankscape.api.rs.RSNode;
import dankscape.loader.AppletLoader;
import dankscape.misc.ClassHook;
import dankscape.misc.FieldHook;
import dankscape.nativeinterface.NativeInterface;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Pieterjan
 */
public class Misc
{
    public static int random(int min, int max)
    {
        if(min == max)
            return min;
        return ThreadLocalRandom.current().nextInt(min, max);
    }
    
    public static double random()
    {
        return ThreadLocalRandom.current().nextDouble();
    }
    
    public static double random(double min, double max)
    {
        if(min == max)
            return min;
        
        return min + random() * (max - min);
    }
    
    public static double clamp(double value, double min, double max)
    {
        if(value > max)
            value = max;
        if(value < min)
            value = min;
        return value;
    }
    
    public static double min(double a, double b)
    {
        return a > b ? b : a;
    }
    public static double max(double a, double b)
    {
        return a < b ? b : a;
    }
    
    // https://www.desmos.com/calculator/3zhzwbfrxd
    public static double smoothstepEx(double x, double steepness, double center)
    {
        x = clamp(x, 0.0, 1.0);
        
        double c = (2.0/(1.0 - steepness)) - 1.0;
        
        if(x <= center)
        {
            return Math.pow(x, c) / Math.pow(center, c - 1);
        }
        else
        {
            return 1.0 - Math.pow(1.0 - x, c) / Math.pow(1.0 - center, c - 1.0);
        }
    }
    public static double smoothstepEx(double x, double steepness)
    {
        return smoothstepEx(x, steepness, 0.5);
    }
    
    //Factorial
    public static int fact(int n) 
    {
        int fact = 1;
        for (int i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }
    //Bernstein polynomial
    public static double bernstein(double t, int n, int i)
    {
       return (fact(n) / (fact(i) * fact(n-i))) * Math.pow(1.0 - t, n-i) * Math.pow(t, i);
    }
    
    public static Point bezierCurve(double t, Point[] points)
    {
        double bPoly[] = new double[points.length];
        
        t = clamp(t, 0.0, 0.99999999);

        for(int i = 0; i < points.length; i++){
            bPoly[i] = bernstein(t, points.length - 1, i+1);
        }

        double sumX = 0;
        double sumY = 0;

        for(int i = 0; i < points.length;  i++){
            sumX += bPoly[i] * (double)points[i].x;
            sumY += bPoly[i] * (double)points[i].y;
        }

        int x, y;
        x = (int) Math.round(sumX);
        y = (int) Math.round(sumY);

        return new Point(x, y);
    }
    
    public static Point randomPointInCircle(Point c, double minRadius, double maxRadius)
    {
        double ang = random(0, Math.PI * 2.0);
        double len = random(minRadius, maxRadius);
        return new Point((int)(c.x + Math.cos(ang) * len), (int)(c.y + Math.sin(ang) * len));
    }
    
    public static Color grabScreenColor(int x, int y)
    {
        if(AppletLoader.getSingleton().getGameCanvas() == null)
            return Color.black;
        
        if(x >= 0 && y >= 0 && x < AppletLoader.getSingleton().getGameCanvas().getWidth() &&  
                y < AppletLoader.getSingleton().getGameCanvas().getHeight())
            return new Color(AppletLoader.getSingleton().getGameCanvas().getImage().getRGB(x, y));
        else
            return Color.black;
    }
    
    public static boolean checkScreenColor(int x, int y, int r, int g, int b, int tolerance)
    {
        Color c = grabScreenColor(x, y);
        if(Math.abs(c.getRed() - r) <= tolerance &&
                Math.abs(c.getGreen() - g) <= tolerance &&
                Math.abs(c.getBlue() - b) <= tolerance)
            return true;
        else
            return false;
    }
    
    public static void messageBox(String title, String text)
    {
        NativeInterface.showMessageBox(title, text);
    }
    
    public static void messageBox(String text)
    {
        messageBox("DankScape Client", text);
    }
    
    public static void setClientWindowTitle(String text)
    {
        NativeInterface.setWindowCaption(text);
    }
    
    public static Point randomPointInRect(Rectangle rect)
    {
        return new Point(rect.x + random(0, rect.width), rect.y + random(0, rect.height));
    }
    
    public static boolean isPointInRect(Point p, Rectangle rect)
    {
        return (p.x >= rect.x && p.x <= rect.x + rect.width && 
                p.y >= rect.y && p.y <= rect.y + rect.height);
    }
    
    public static int sgn(int val) 
    {
        if(val > 0)
            return 1;
        else if(val < 0)
            return -1;
        else
            return 0;
    }
    
    public static boolean isPointInsidePolygonCCW(Point point, List<Point> poly)
    {
        Point p1, p2;
        int indx1, indx2;
        for(int i = 1;i <= (int)poly.size();i++)
        {
            indx1 = i - 1;
            indx2 = i;
            if(i == (int)poly.size())
                indx2 = 0;

            p1 = poly.get(indx1);
            p2 = poly.get(indx2);

            if(sgn((p2.x-p1.x)*(point.y-p1.y) - (p2.y-p1.y)*(point.x-p1.x)) >= 0)
                return false;
        }
        return true;
    }
    
    public static boolean polygonIsClockwise(List<Point> vertices)
    {
        float edgeSum = 0.0f;
        for(int counter = 0;counter < vertices.size();counter++)
        {
            int i1 = counter;
            int i2 = counter + 1;
            if(counter + 1 >= counter)
                i2 = 0;
            edgeSum += ((float)(vertices.get(i2).x - vertices.get(i1).x) * (float)(vertices.get(i2).y + vertices.get(i1).y));
        }

        if(edgeSum > 0.0f)
            return true;
        else
            return false;
    }
    
    public static Color[][] grabScreenColors(int x, int y, int w, int h)
    {
        int[] buffer = new int[w * h];
        
        int xReal = 0;
        int yReal = 0;
        int wReal = 0;
        int hReal = 0;
        int[] region = null;
        
        if(AppletLoader.getSingleton().getGameCanvas() != null)
        {
            xReal = (int)Misc.clamp(x, 0, AppletLoader.getSingleton().getGameCanvas().getWidth() - 1);
            yReal = (int)Misc.clamp(y, 0, AppletLoader.getSingleton().getGameCanvas().getHeight() - 1);
            wReal = (int)Misc.clamp(w, 0, AppletLoader.getSingleton().getGameCanvas().getWidth() - 1 - xReal);
            hReal = (int)Misc.clamp(h, 0, AppletLoader.getSingleton().getGameCanvas().getHeight() - 1 - yReal);

            region = new int[wReal * hReal];
            region = AppletLoader.getSingleton().getGameCanvas().getImage().getRGB(xReal, yReal, wReal, hReal, region, 0, wReal);
        }
        
        // Copy the subregion into the buffer that the user expects
        int xOffset = xReal - x;
        int yOffset = yReal - y;
        
        for(int i = 0;i < wReal;i++)
            for(int j = 0;j < hReal;j++)
                buffer[(j + yOffset) * w + (i + xOffset)] = region[j * wReal + i];
            
        Color[][] colors = new Color[w][];
        for(int i = 0;i < w;i++)
        {
            colors[i] = new Color[h];
            for(int j = 0;j < h;j++)
                colors[i][j] = new Color(buffer[j * w + i]);
        }
        return colors;
    }
    
    public static void debugDrawRect(Graphics g, Rectangle r, int red, int green, int blue)
    {
        g.setColor(new Color(red, green, blue, 80));
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(new Color(red, green, blue, 255));
        g.drawRect(r.x, r.y, r.width, r.height);
    }
    
    public static void sleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        } 
        catch (InterruptedException ex)
        {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static List<RSNode> rsHashTableToList(RSHashTable tbl)
    {
        HashSet<Object> retrievedObjects = new HashSet();
        ArrayList<RSNode> results = new ArrayList();
        
        RSNode node = tbl.getCurrent();
        while(node != null)
        {
            if(!retrievedObjects.contains(node.getRSObjectReference()))
            {
                retrievedObjects.add(node.getRSObjectReference());
                results.add(node);
                
                node = node.getNext();
            }
            else
                node = null;
        }
        return results;
    }
    
    // Searches statically obtainable integers (including arrays up to 3D) for a certain values and returns info about matching fields.
    public static List<String> searchStaticIntsByValue(int value)
    {
        ArrayList<String> result = new ArrayList<>();
        
        HashMap<String, ClassHook> hooks = AppletLoader.getSingleton().getHooks();
        for(ClassHook hC : hooks.values())
        {
            for(FieldHook hF : hC.fields.values())
            {
                if((hF.field.getModifiers() & Modifier.STATIC) !=0)
                {
                    Object obj = AppletLoader.getSingleton().getFieldValue(hC.name, hF.name, null);
                    
                    if(hF.field.getType() == int.class)
                    {
                        try
                        {
                            if((int)hF.field.get(null) == value)
                                result.add(hC.name + "." + hF.name + ": " + hF.field.toString());
                        } 
                        catch (IllegalAccessException ex)
                        {
                            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else if(hF.field.getType() == int[].class)
                    {
                        try
                        {
                            int[] arr = (int[])hF.field.get(null);
                            if(arr != null)
                            {
                                for(int i = 0;i < arr.length;i++)
                                {
                                    int v = arr[i];
                                    if(v == value)
                                    {
                                        result.add(hC.name + "." + hF.name + "[" + i + "]: " +  hF.field.toString());
                                    }
                                }
                            }
                        } 
                        catch (IllegalAccessException ex)
                        {
                            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else if(hF.field.getType() == int[][].class)
                    {
                        try
                        {
                            int[][] arr = (int[][])hF.field.get(null);
                            if(arr != null)
                            {
                                for(int i = 0;i < arr.length;i++)
                                {
                                    if(arr[i] != null)
                                    for(int j = 0;j < arr[i].length;j++)
                                    {
                                        int v = arr[i][j];
                                        if(v == value)
                                        {
                                            result.add(hC.name + "." + hF.name + "[" + i + "][" + j + "]: " +  hF.field.toString());
                                        }
                                    }
                                }
                            }
                        } 
                        catch (IllegalAccessException ex)
                        {
                            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else if(hF.field.getType() == int[][][].class)
                    {
                        try
                        {
                            int[][][] arr = (int[][][])hF.field.get(null);
                            if(arr != null)
                            {
                                for(int i = 0;i < arr.length;i++)
                                {
                                    if(arr[i] != null)
                                    for(int j = 0;j < arr[i].length;j++)
                                    {
                                        if(arr[i][j] != null)
                                        for(int k = 0;k < arr[i][j].length;k++)
                                        {
                                            int v = arr[i][j][k];
                                            if(v == value)
                                            {
                                                result.add(hC.name + "." + hF.name + "[" + i + "][" + j + "][" + k + "]: " + hF.field.toString());
                                            }
                                        }
                                    }
                                }
                            }
                        } 
                        catch (IllegalAccessException ex)
                        {
                            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        
        return result;
    }
}
