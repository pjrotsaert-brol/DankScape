/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api.internal.projection;

import dankscape.api.rs.RSClient;

/**
 *
 * @author Pieterjan
 */
public class LocalPlayer
{
    public static WorldLocation getWorldLocation()
    {
        return new WorldLocation(RSClient.getLocalPlayer().getX(), RSClient.getLocalPlayer().getY());
    }
}
