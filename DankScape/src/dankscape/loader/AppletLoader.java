/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.loader;

import dankscape.api.Login;
import dankscape.api.rs.RSClient;
import dankscape.bot.gui.BotGUI;
import dankscape.misc.APIGenerator;
import dankscape.misc.ClassHook;
import dankscape.misc.DebugWriter;
import java.awt.Color;
import sun.awt.windows.WComponentPeer;
import java.net.URL;
import java.net.URLClassLoader;
import java.applet.Applet;
import java.applet.AppletContext;
import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.applet.AppletStub;
import java.awt.Frame;
import java.util.HashMap;
import dankscape.misc.FieldHook;
import dankscape.misc.JarUtils;
import dankscape.misc.ResizeRequestedEvent;
import java.awt.Canvas;
import java.awt.Window;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import jdk.internal.org.objectweb.asm.tree.ClassNode;


/**
 *
 * @author Pieterjan
 */
public class AppletLoader extends DebugWriter implements AppletStub
{
    private AppletFrame appletFrame = null;
    private static AppletLoader singleton = null;
    
    private URLClassLoader loader = null;
    private Applet applet = null;
    
    private long hWnd = 0;
    private int clientWindowHandle = 0; // Window handle of the native client
    
    private final HashMap<String, String> parameters = new HashMap<>();
    
    private HashMap<String, ClassHook> hooks = new HashMap<>(); 
    
    private Canvas gameCanvas = null;
    private boolean hooksLoaded = false;
    
    private long nativeGuiThreadId = 0;
    
    private ReentrantLock reflectLock = new ReentrantLock();
    
    public AppletLoader(){}
    
    // This is the actual main function that's being called from C++ by the bot.
    public static AppletLoader onCreate(String jarFile, String className, String[] paramNames, String[] paramValues, int appletW, int appletH, int hWND) 
    {
        getSingleton().nativeGuiThreadId = Thread.currentThread().getId();
        getSingleton().clientWindowHandle = hWND;
        BotGUI.setupUi();
        getSingleton().init(jarFile, className, paramNames, paramValues, appletW, appletH);
        return singleton;
    }
    
    // Called from native code
    public void onResize(int w, int h)
    {
        ResizeRequestedEvent e = new ResizeRequestedEvent(appletFrame, 0, w, h);
        appletFrame.dispatchEvent(e);
    }
    
    private long init(String jarFile, String className, String[] paramNames, String[] paramValues, int appletW, int appletH)
    {
        for(int i = 0;i < paramNames.length && i < paramValues.length;i++)
            parameters.put(paramNames[i], paramValues[i]);
        
        ConfigLoader.loadConfig();
        Login.setAccount(ConfigLoader.getDefaultAccount());
        
        parameters.put("4", ConfigLoader.getSetting("DefaultWorld", "302"));
        
        nativeGuiThreadId = Thread.currentThread().getId();
        
        appletFrame = new AppletFrame();
        appletFrame.setBackground(Color.black);
        appletFrame.setSize(appletW, appletH);
        appletFrame.setFocusCycleRoot(false);
        //appletFrame.add(innerFrame);
        
        File gamepack = new File(jarFile);
        URL[] urls;
        
        try
        {
            urls = new URL[]{ gamepack.toURI().toURL() };
            
            loader = new URLClassLoader(urls, this.getClass().getClassLoader());
            applet = (Applet)loader.loadClass(className).newInstance();
            applet.setStub(this);
            applet.setLayout(null);
            applet.resize(appletFrame.getSize());
            applet.init();
            applet.start();
            
            applet.setFocusCycleRoot(false);
            
            appletFrame.add(applet);
        } 
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | MalformedURLException ex)
        {
            System.out.println("ERROR: " + ex.getMessage());
            Logger.getLogger(AppletLoader.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return 0;
        }
         
        appletFrame.setVisible(true);
        appletFrame.setVisible(false);
        hWnd = ((WComponentPeer)appletFrame.getPeer()).getHWnd();
        
        debug("Applet started, window handle: " + hWnd + ".");
        return hWnd;
    }
    
    // Called from native code
    public long getWindowHandle()
    {
        return hWnd;
    }
    
    public long getNativeWindowHandle()
    {
        return (long)clientWindowHandle;
    }
    
    public Window getNativeClientWindow()
    {
        Window[] windows = Window.getWindows();
        for(Window w : windows)
        {
            if(((WComponentPeer)w.getPeer()).getHWnd() == (long)clientWindowHandle)
                return w;
        }
        return null;
    }
    
    // Called from native code
    public void loadHooks(HashMap<String, ClassHook> hooks)
    {
        this.hooks = hooks;
        /*int count = 0;
        for(ClassHook hC : hooks.values())
        {
            try 
            {
                Class<?> clazz = loader.loadClass(hC.internalName);
                hC.clazz = clazz;
                for(FieldHook hF : hC.fields.values())
                {
                    Field f = clazz.getDeclaredField(hF.internalName);
                    hF.field = f;
                    count++;
                }
            } 
            catch (Exception ex)
            {
                Logger.getLogger(AppletLoader.class.getName()).log(Level.SEVERE, null, ex);
                debug("ERROR: " + ex.toString() + "\n");
                debug("Failed to load hooks!");
                hooksLoaded = false;
                return;
            }
        }*/
        
        hooksLoaded = true;
        debug("Skipped loading hooks.");
//debug("" + count + " field hooks loaded.");
        //debug("Client revision is " + RSClient.getRevision() + ".");
        appletFrame.setVisible(true);
    }
    
    public long getNativeGuiThreadId()
    {
        return nativeGuiThreadId;
    }
    
    public HashMap<String, ClassHook> getHooks()
    {
        return hooks;
    }
    
    public boolean generateAPI()
    {
        if(!hooksLoaded)
        {
            debug("Hooks have not yet loaded! Please wait before trying to generate the API!");
            return false;
        }
        
        debug("Reading classes from gamepack.jar...");
        HashMap<String, ClassNode> classes = JarUtils.readJarClasses("gamepack.jar"); // Read classes for some extra info about the hooks
        APIGenerator.run(hooks, classes);
        return true;
    }
    
    public void onCanvasReady(Canvas canvas)
    {
        gameCanvas = canvas;
    }
    
    public URLClassLoader getClassLoader()
    {
        return loader;
    }
    
    public Canvas getGameCanvas()
    {
        return gameCanvas;
    }
    
    public Frame getAppletFrame()
    {
        return appletFrame;
    }
    
    public Applet getApplet()
    {
        return applet;
    }
    
    public boolean hasLoaded()
    {
        return hooksLoaded;
    }
    
    public Object getFieldValue(String className, String fieldName, Object obj) throws Exception
    {
        debug("ATTEMPTING TO GET HOOK: " + className + "." + fieldName);
        
        if(!hooksLoaded)
            ;
        else 
        {
            try 
            {
                throw new Exception("Dank");
            }
            catch(Exception ex)
            {
                 StackTraceElement[] stack = ex.getStackTrace();
                String stackTrace = "";
                for(StackTraceElement i : stack)
                    stackTrace += "    at " + i.toString() + "\n";
                debug("FATAL ERROR: " + ex.toString() + "\n" + stackTrace);
            }
            
        }
        
        Object v = null;
        //reflectLock.lock();
        try
        {
            FieldHook hF = hooks.get(className).fields.get(fieldName);
            boolean wasAccessible = hF.field.isAccessible();
            hF.field.setAccessible(true);
            if(hF.multiplier == 0)
                v = hF.field.get(obj);
            else
                v = (Integer)hF.field.get(obj) * hF.multiplier;
            hF.field.setAccessible(wasAccessible);
        }
        catch(IllegalAccessException | IllegalArgumentException ex)
        {
            debug("Error getting field '" + className + "." + fieldName + "': " + ex.toString() + ": " + Arrays.toString(ex.getStackTrace()));
            return v;
        }
        finally
        {
            //reflectLock.unlock();
        }
        return v;
    }
    
    public void setInputEnabled(boolean state)
    {
        System.out.println("Applet enabled: " + state);
        applet.setEnabled(state);
        applet.setFocusable(state);
        appletFrame.setEnabled(state);
        appletFrame.setFocusable(state);
        
        if(state)
        {
            applet.requestFocus();
            appletFrame.requestFocus();
        }
    }

    public static AppletLoader getSingleton()
    {
        if(singleton == null)
            singleton = new AppletLoader();
        return singleton;
    }
    
    @Override
    public boolean isActive()
    {
        return true; 
    }

    @Override
    public URL getDocumentBase()
    {
        try
        {
            return new URL("http://oldschool1.runescape.com/"); 
        } 
        catch (MalformedURLException ex)
        {
            Logger.getLogger(AppletLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public URL getCodeBase()
    {
        return getDocumentBase();
    }

    @Override
    public String getParameter(String name)
    {
        return parameters.get(name);
    }

    @Override
    public AppletContext getAppletContext()
    {
        return null;
    }

    @Override
    public void appletResize(int width, int height)
    {
    }
}
