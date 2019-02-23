/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot;

import dankscape.api.Game;
import dankscape.api.GameObject;
import dankscape.api.Input;
import dankscape.api.Inventory;
import dankscape.api.Item;
import dankscape.api.Login;
import dankscape.api.Misc;
import dankscape.api.NPC;
import dankscape.api.Player;
import dankscape.api.Tile;
import dankscape.api.Widget;
import dankscape.api.internal.Projection;
import dankscape.api.rs.RSClient;
import dankscape.api.rs.RSGameEngine;
import dankscape.api.rs.RSNPC;
import dankscape.api.rs.RSRegion;
import dankscape.loader.AppletLoader;
import dankscape.misc.DebugWriter;
import dankscape.misc.Mat4;
import dankscape.misc.Vec3;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Pieterjan
 */
public class DebugRenderer extends DebugWriter
{
    private static final DebugRenderer singleton = new DebugRenderer();
    private static final int MOUSE_TRAIL_COUNT = 80;
    
    private Color[] debugColors;
    private Color mouseTrailColor = Color.pink;
    private ArrayList<Point> mouseTrail = new ArrayList();
    
    private Font defaultFont = null;
    
    public DebugRenderer()
    {
        if(debugColors == null)
        {
            debugColors = new Color[256];
            for(int i = 0;i < debugColors.length;i++)
                debugColors[i] = new Color(Misc.random(0, 256),  Misc.random(0, 256), Misc.random(0, 256), 255);
        }
    }
    
    public static DebugRenderer get()
    {
        return singleton;
    }
    
    public void paint(Graphics g)
    {
        if(defaultFont == null)
            defaultFont = g.getFont();
        
        g.setFont(defaultFont);
        //g.setFont(debugFont);
        

        /***** Draw Login Screen Debug *****/
        if(DebugSettings.showLoginScreenDebug)
            Login.debugDrawButtons(g);
        
        /***** Draw Tile Debug *****/
        if(DebugSettings.showTileProjection)
            Tile.renderDebug(g);
        
        /***** Draw NPC Debug *****/
        if(DebugSettings.showNPCs)
            NPC.renderDebug(g);
        
        if(DebugSettings.showInteractiveGameObjects || DebugSettings.showOtherGameObjects)
            GameObject.renderDebug(g);
        
        
        if(DebugSettings.showModels)
        {
            NPC.renderModelDebug(g);
            Player.renderModelDebug(g);
            GameObject.renderModelDebug(g);
        }
        
        
        
        /***** Draw Inventory Debug ****/
        if(Inventory.isOpen() && DebugSettings.showInventory)
        {
            g.setColor(Color.green);
            List<Item> items = Inventory.getItems();
            for(Item i : items)
            {
                Rectangle r = Inventory.getItemBounds(i.getIndex());
                g.drawString("" + i.getId(), r.x, r.y - 1);
            }
        }
        
        /***** Draw Mouse Trail *****/
        /*RSMouseRecorder recorder = RSClient.getMouseRecorder();
        if(recorder != null)
        {
            int[] cursorHistX = RSClient.getMouseRecorder().getXs();
            int[] cursorHistY = RSClient.getMouseRecorder().getYs();
            
            g.setColor(new Color(255, 0, 0, 60));
            for(int i = cursorHistX.length - 1;i >= 0 && i < cursorHistY.length;i--)
                g.fillOval(cursorHistX[i], cursorHistY[i], 8, 8);
            
        }*/
        if(DebugSettings.showMouseHistory)
        {
            if(mouseTrail.size() >= MOUSE_TRAIL_COUNT)
                mouseTrail.remove(0);
            mouseTrail.add(new Point(Input.mouseX, Input.mouseY));

            Graphics2D g2 = (Graphics2D)g;
            g2.setStroke(new BasicStroke(3));
            for(int i = 0;i < mouseTrail.size();i++)
            {
                double opacity = 1.0 - (double)(MOUSE_TRAIL_COUNT - i) / (double)MOUSE_TRAIL_COUNT;
                Color c = new Color(mouseTrailColor.getRed(), mouseTrailColor.getGreen(), mouseTrailColor.getBlue(), (int)(opacity * 255));
                g.setColor(c);
                //g.fillOval(mouseTrail.get(i).x - 3, mouseTrail.get(i).y - 3, 6, 6);

                if(i + 1 < mouseTrail.size())
                    g.drawLine(mouseTrail.get(i).x, mouseTrail.get(i).y, mouseTrail.get(i + 1).x, mouseTrail.get(i + 1).y); 
            }
            g2.setStroke(new BasicStroke(1));
        }
        
        /**** Draw Cursor ****/
        if(Input.leftDown || Input.rightDown || Input.middleDown)
            g.setColor(Color.red);
        else
            g.setColor(Color.yellow);
        g.drawLine(Input.mouseX - 12, Input.mouseY, Input.mouseX + 12, Input.mouseY);
        g.drawLine(Input.mouseX, Input.mouseY - 12, Input.mouseX, Input.mouseY + 12);
        g.fillOval(Input.mouseX - 3, Input.mouseY - 3, 6, 6);
        
        /**** Draw FPS ****/
        if(DebugSettings.showFPS)
        {
            g.setColor(Color.white);
            g.drawString("FPS:" + RSGameEngine.getFPS(), AppletLoader.getSingleton().getAppletFrame().getWidth() - 53, 15);
        }
        
        
        /**** Draw Generic text debug ****/
        
        String genericText = "";
        if(DebugSettings.showAnimationId && RSClient.getLocalPlayer() != null)
        {
            genericText += "Animation id: " + RSClient.getLocalPlayer().getAnimation() + "\n";
            genericText += "Idle pose anim id: " + RSClient.getLocalPlayer().getIdlePoseAnimation()+ "\n";
            
        }
        
        if(DebugSettings.showPosition)
        {
            if(RSClient.getLocalPlayer() != null)
            {    
                genericText += "Walking: " + Game.isPlayerMoving() + "\n";
                genericText += "Destination: (" + RSClient.getDestinationX() + ", " + RSClient.getDestinationY() + ")\n";
                genericText += "Position: (" + (RSClient.getBaseX() + RSClient.getLocalPlayer().getX() / Tile.TILE_SIZE) + ", " + 
                        (RSClient.getBaseY() + RSClient.getLocalPlayer().getY() / Tile.TILE_SIZE) + ")\n";
            }

            genericText += "Base Position: (" + RSClient.getBaseX() + ", " + RSClient.getBaseY() + ")\n";
            genericText += "Plane: " + RSClient.getScene_plane() + "\n";
        }
        
        if(DebugSettings.showCamera)
        {
            genericText += "Camera X: " + Projection.getCameraPosition().x + " Y: " + 
                    Projection.getCameraPosition().y + " Z: " + Projection.getCameraPosition().z + "\n";
            genericText += "Camera Yaw: " + Math.round(Projection.getCameraYaw() * 180.0 / Math.PI) + 
                    "  Pitch: " + Math.round(Projection.getCameraPitch() * 180.0 / Math.PI) + "\n";
            genericText += "Viewport Scale: " + Math.round(Projection.getViewportScale() * 100) + "%\n";
        }
        
        if(DebugSettings.showUpText)
        {   
            genericText += "Up Text: " + RSClient.getMenuOptions()[RSClient.getMenuOptionCount() - 1] + " " +
                    RSClient.getMenuTargets()[RSClient.getMenuOptionCount() - 1] + "\n";
        }
        if(DebugSettings.showMenuOptions)
        {
            genericText += "Menu Options:\n";
            int optCount = RSClient.getMenuOptionCount();
            for(int i = optCount - 1;i >= 0;i--)
            {
                genericText += "    -" + RSClient.getMenuOptions()[i] + " " + RSClient.getMenuTargets()[i] + 
                        " (Type: " + RSClient.getMenuTypes()[i] + ", p0: " + RSClient.getMenuActionParams0()[i] + 
                        ", p1: " + RSClient.getMenuActionParams1()[i] + ")\n";
            }
        }
        
        g.setColor(Color.cyan);
        
        if(RSClient.getIsMenuOpen())
        {
            for(int i = 0;i < RSClient.getMenuOptionCount();i++)
            {
                Rectangle r = Game.getContextOptionBounds(i);
                g.drawRect(r.x, r.y, r.width, r.height);
            }
        }
        
        g.setColor(Color.green);
        
        String[] genericStrings = genericText.split("\n");
        for(int i = 0;i < genericStrings.length;i++)
            g.drawString(genericStrings[i], 8, 50 + i * 18);
        
        /**** Draw Widgets ****/
        if(!DebugSettings.showNoWidgets) // DebugSettings.showWidgetIds
        {
            int rangeMin = DebugSettings.widgetRangeMin;
            int rangeMax = DebugSettings.widgetRangeMax;
            if(rangeMax < 0)
                rangeMax = rangeMin;
            
            if(DebugSettings.showAllWidgets)
            {
                rangeMin = 0;
                rangeMax = 999999;
            }
            
            Object[][] widgets = RSClient.getRSRef_Widgets();
            if(widgets != null)
            {
                ArrayList<String> widgetsToPrint = new ArrayList();
                for(int i = 0;i < widgets.length;i++)
                {
                    if(widgets[i] != null && i >= rangeMin && i <= rangeMax)
                    {
                        for(int j = 0;j < widgets[i].length;j++)
                        {
                            Widget widget = Widget.get(i, j);
                            if(widget != null)
                                if(widget.isVisible())
                                {
                                    int x = widget.getAbsX(), y = widget.getAbsY(), w = widget.getWidth(), h = widget.getHeight();

                                    if(Input.mouseX >= x && Input.mouseX <= x + w && Input.mouseY >= y && Input.mouseY <= y + h)
                                    {
                                        Color clr = debugColors[widgetsToPrint.size() % debugColors.length];
                                        g.setColor(new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), 50));
                                        g.fillRect(x, y, w, h);
                                        
                                        widgetsToPrint.add("> " + i + ", " + j);
                                        //g.drawString("" + i + ", " + j, widget.getAbsX(), widget.getAbsY());
                                        g.setColor(new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), 255));
                                        g.drawRect(x, y, w, h);
                                    }
                                    else
                                    {
                                        g.setColor(new Color(255, 255, 255, 128));
                                        g.drawRect(x, y, w, h);
                                    }
                                }
                        }
                    }
                }
                
                int x = 10, y = 30;
                g.setColor(new Color(0, 0, 0, 200));
                g.fillRect(x, y, 200, 20 * widgetsToPrint.size());
                for(int i = 0;i < widgetsToPrint.size();i++)
                {
                    Color clr = debugColors[i % debugColors.length];
                    g.setColor(clr);
                    g.drawString(widgetsToPrint.get(i), x, y + 20 * i + 20);
                }
                
                
            }
        }
    }
    
    public static void drawPoly(Graphics g, List<Vec3> vertices)
    {
        List<Point> points = Projection.project(Projection.transform(vertices));
        Polygon poly = new Polygon();
        for(Point p : points)
            poly.addPoint(p.x, p.y);
        g.drawPolygon(poly);
    }
    
    public static void drawCube(Graphics g, int x, int y, int z, int width, int depth, int height, double rotatex, double rotatey)
    {
        ArrayList<Vec3> v = new ArrayList();
        Mat4 t = Projection.getModelMatrix();
        t.translate(x, y, z);
        t.rotateX(rotatex);
        t.rotateY(rotatey);
        t.scale(width, height, depth);
         
        Projection.setModelMatrix(t);
        
        v.clear();
        v.add(new Vec3(-0.5, -0.5, 0.5));
        v.add(new Vec3( 0.5, -0.5, 0.5));
        v.add(new Vec3( 0.5,  0.5, 0.5));
        v.add(new Vec3(-0.5,  0.5, 0.5));
        drawPoly(g, v);
        
        v.clear();
        v.add(new Vec3(-0.5, -0.5, -0.5));
        v.add(new Vec3( 0.5, -0.5, -0.5));
        v.add(new Vec3( 0.5,  0.5, -0.5));
        v.add(new Vec3(-0.5,  0.5, -0.5));
        drawPoly(g, v);
        
        v.clear();
        v.add(new Vec3(-0.5, -0.5, -0.5));
        v.add(new Vec3(-0.5,  0.5, -0.5));
        v.add(new Vec3(-0.5,  0.5,  0.5));
        v.add(new Vec3(-0.5, -0.5,  0.5));
        drawPoly(g, v);
        
        v.clear();
        v.add(new Vec3( 0.5, -0.5, -0.5));
        v.add(new Vec3( 0.5,  0.5, -0.5));
        v.add(new Vec3( 0.5,  0.5,  0.5));
        v.add(new Vec3( 0.5, -0.5,  0.5));
        drawPoly(g, v);
        
        Projection.loadIdentity();
    }
}
