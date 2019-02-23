/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api.internal.projection;

import dankscape.api.rs.RSClient;
import java.util.Optional;

/**
 *
 * @author Pieterjan
 */
public class Scene
{
    public static final int SIZE = 104;
    
    public static int getBaseX()
    {
        return RSClient.getBaseX();
    }
    
    public static int getBaseY()
    {
        return RSClient.getBaseY();
    }
    
    public static int getPlane()
    {
        return RSClient.getPlane();
    }
    
    public static Optional<Integer> getGroundHeight(int x, int y) 
    {
        int x1 = x >> 7;
        int y1 = y >> 7;
        if (x1 < 0 || x1 > SIZE || y1 < 0 || y1 > SIZE) {
            return Optional.empty();
        }
        byte[][][] rules = RSClient.getTileSettings();
        if (rules == null) {
            return Optional.empty();
        }
        int[][][] heights = RSClient.getTileHeights();
        if (heights == null) {
            return Optional.empty();
        }
        int plane = Scene.getPlane();
        if (plane < 3 && (rules[1][x1][y1] & 0x2) == 2) {
            plane++;
        }
        int x2 = x & 0x7F;
        int y2 = y & 0x7F;
        int h1 = heights[plane][x1][y1] * (ProjectionHelpers.TILE_PIXEL_SIZE - x2) + heights[plane][x1 + 1][y1] * x2 >> 7;
        int h2 = heights[plane][x1][y1 + 1] * (ProjectionHelpers.TILE_PIXEL_SIZE - x2) + heights[plane][x1 + 1][y1 + 1] * x2 >> 7;
        return Optional.of(h1 * (ProjectionHelpers.TILE_PIXEL_SIZE - y2) + h2 * y2 >> 7);
    }
}
