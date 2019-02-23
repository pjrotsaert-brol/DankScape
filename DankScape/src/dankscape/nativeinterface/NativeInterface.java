/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.nativeinterface;

import dankscape.api.plugin.menu.MenuAction;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.jar.JarInputStream;

/**
 *
 * @author Pieterjan
 */
public class NativeInterface 
{
    public static native Object[] getAllInstancesOfClass(Class clazz); // From DankAgent.dll
    
    /*********** Functions from DankScape.exe **********/
    // These functions are threadsafe
    public static native void setStatusTextLeft(String text);
    public static native void setStatusTextRight(String text);
    public static native void println(String text, boolean replaceLastLine);  
    public static native void setWindowCaption(String text);
    public static native void showMessageBox(String title, String text);
    
    // NOTE: Functions below may ONLY be called in the native GUI thread, which
    //       you can call from by requesting a callback via requestUICallback().
    public static native void requestUICallback(String cbFuncName, int cbId);
    public static native int  createMenu(String title);
    public static native int  createMenu(String title, int parentId);
    public static native int  createAction(String title, int menuId, String cbFuncName, int cbId, boolean checkable, boolean isChecked);
    public static native void addMenuSeparator(int menuId);
    public static native void addToolBarSeparator();
    public static native void addActionToToolBar(int actionId, int alignment);
    public static native boolean getActionState(int id);
    public static native void setActionState(int id, boolean state);
    
    // ------- Glue ------- //
    
    static HashMap<Integer, MenuAction> actionCallbacks = new HashMap();
    
    public static void println(String tag, String s)
    {
        println("[" + tag + "]: " + s, false);
    }
    
    public static void println(String text)
    {
        println("JVM-Debug", text + "\n");
    }
    
    public static void bindActionCallback(int callbackId, MenuAction action)
    {
        actionCallbacks.put(callbackId, action);
    }
    
    public static void actionTriggeredCallback(int id)
    {
        MenuAction action = actionCallbacks.get(id);
        if(action != null)
            action.onTriggered();
    }
    
    public static void actionToggledCallback(int id, boolean state)
    {
        MenuAction action = actionCallbacks.get(id);
        if(action != null)
            action.onToggled(state);
    }
    
    
    
    /*********** Functions Called By DankScape.exe **********/
    
    public static int getLocalHash(String filename) 
    {
        System.out.println("Retrieving local jar hash: " + filename + "...");
        try {
            URL url = new File(filename).toURI().toURL();
            try (JarInputStream stream = new JarInputStream(url.openStream())) {
                return stream.getManifest().hashCode();
            } catch (Exception e) {
                System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                return -1;
            }
        } 
        catch (MalformedURLException e) {
            System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            return -1;
        }
    }
     
    public static int getRemoteHash(String jarLink)
    {
        System.out.println("Retrieving remote jar hash: " + jarLink + "...");
        try {
            URL url = new URL(jarLink);
            JarInputStream stream = new JarInputStream(url.openStream());
            return stream.getManifest().hashCode();
        } catch (IOException e) {
            System.out.println(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            return -1;
        }
    }
    
    
}
