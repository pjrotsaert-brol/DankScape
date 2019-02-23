/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Pieterjan
 */
public class ClassHook 
{
    
    
    public String name, internalName;
    public int access = 0;
    public HashMap<String, FieldHook> fields = new HashMap<>();
    
    // Internal use 
    public ArrayList<String> ignoredFields = new ArrayList<>();
    public ArrayList<RelocatedField> relocatedFields = new ArrayList<>();
    public Class clazz = null;
    
    public ClassHook(){}
    public ClassHook(ClassHook source, int _access)
    {
        name = source.name;
        internalName = source.internalName;
        access = _access;
    }
    public ClassHook(String name, String internalName, FieldHook[] fields)
    {
        this.name = name;
        this.internalName = internalName;
        this.fields = new HashMap<>();
        for(int i = 0;i < fields.length;i++)
        {
            this.fields.put(fields[i].name, fields[i]);
        }
    }
    
    public boolean isIgnoredField(String fieldName)
    {
        for(String s : ignoredFields)
        {
            if(s.equals(fieldName))
                return true;
        }
        return false;
    }
}
