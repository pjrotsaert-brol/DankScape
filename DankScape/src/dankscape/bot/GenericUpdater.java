/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot;

import dankscape.api.Game;
import dankscape.api.Player;

/**
 *
 * @author Pieterjan
 */
public class GenericUpdater
{
    private static int lastPlayerX, lastPlayerY;
    private static double lastPlayerRot;
    public static boolean isPlayerMoving = false;
    public static boolean[] movingLastFrames = new boolean[40];
    
    public static void update()
    {
       
        boolean movingThisFrame = false;
        if(Player.getLocal() != null && Game.isInGame())
        {
            if(Player.getLocal().getSceneX() != lastPlayerX || Player.getLocal().getSceneY() != lastPlayerY || 
                    Player.getLocal().getRotation() != lastPlayerRot)
                movingThisFrame = true;
            else
                movingThisFrame = false;

            lastPlayerX = Player.getLocal().getSceneX();
            lastPlayerY = Player.getLocal().getSceneY();
            lastPlayerRot = Player.getLocal().getRotation();
        }
        for(int i = 0;i < movingLastFrames.length - 1;i++)
            movingLastFrames[i] = movingLastFrames[i+1];
        movingLastFrames[movingLastFrames.length - 1] = movingThisFrame;
        
        isPlayerMoving = false;
        for(boolean b : movingLastFrames)
        {
            if(b)
            {
                isPlayerMoving = true;
                break;
            }
        }

    }
}
