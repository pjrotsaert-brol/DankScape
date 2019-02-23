/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

import java.lang.reflect.Field;

/**
 *
 * @author Pieterjan
 */
public class FieldHook {
    public String name, internalName, signature;
    public int multiplier = 0;
    public boolean isStatic = false;
    
    public Field field = null;
    
    public FieldHook(){}
    public FieldHook(FieldHook other, boolean _isStatic, String sig)
    {
        name = other.name;
        internalName = other.internalName;
        multiplier = other.multiplier;
        isStatic = _isStatic;
        signature = sig;
    }
    
    public FieldHook(String name, String internalName, int multi)
    {
        this.name = name;
        this.internalName = internalName;
        this.multiplier = multi;
    }
}
