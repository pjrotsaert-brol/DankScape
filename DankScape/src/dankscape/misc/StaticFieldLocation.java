/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

/**
 *
 * @author Pieterjan
 */
public class StaticFieldLocation
{
    String fieldName, typename, dstClassName;
    public StaticFieldLocation(){}
    public StaticFieldLocation(String fieldName, String typename, String designatedLocation)
    {
        this.fieldName = fieldName;
        this.typename = typename;
        this.dstClassName = designatedLocation;
    }
}