/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

/**
 *
 * @author Pieterjan
 */

public class RelocatedField
{
    String fieldName, className;

    public RelocatedField()
    {
    }

    public RelocatedField(String fieldName, String className)
    {
        this.fieldName = fieldName;
        this.className = className;
    }
}