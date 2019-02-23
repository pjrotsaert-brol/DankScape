/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot.gui;

import dankscape.api.Misc;
import dankscape.api.plugin.menu.Menu;
import dankscape.api.plugin.menu.MenuAction;
import dankscape.bot.DebugSettings;
import dankscape.bot.TaskScheduler;
import dankscape.loader.AppletLoader;
import dankscape.loader.ScriptLoader;
import dankscape.nativeinterface.NativeInterface;
import java.util.Arrays;

/**
 *
 * @author Pieterjan
 */
public class BotGUI
{
    public static boolean inputEnabled = true;
    
    private static void debug(String text)
    {
        NativeInterface.println("BotGUI", text + "\n");
    }
    
    public static void setupUi()
    {
        Menu script = new Menu("Script");
        MenuAction actBrowse = new MenuAction(script, "Open Script Browser..", () -> { ScriptBrowser.open(); });
        script.addSeparator();
        MenuAction actStart = new MenuAction(script, "Start script", () -> 
        { 
            if(ScriptBrowser.getSelectedScript() != null)
            {
                // Instantiate another copy of the script and run that..
                TaskScheduler.get().runScript(ScriptLoader.instantiateScript(ScriptBrowser.getSelectedScript().getClass()));
            }
            else
                Misc.messageBox("ERROR: No script selected!\nSelect Script -> Open Script Browser to resolve this problem.");
        });
        MenuAction actPause = new MenuAction(script, "Pause script", state -> { TaskScheduler.get().setPaused(state); }, false);
        MenuAction actStop = new MenuAction(script, "Terminate script", () -> { TaskScheduler.get().terminateScript(); });
        
        Menu debug = new Menu("Debug");
        DebugSettings.setupUi(debug);
        
        Menu tools = new Menu("Tools");
        
        new MenuAction(tools, "Account Manager", () -> { AccountManager.open();});
        
        Menu stackTraces = new Menu("Stack Traces", tools);

        new MenuAction(tools, "Generate API", () -> { AppletLoader.getSingleton().generateAPI(); });
        new MenuAction(stackTraces, "Paint thread", () -> 
        { debug("Paint thread trace: \n" + Arrays.toString(TaskScheduler.get().getPaintThread().getStackTrace())); });
        new MenuAction(stackTraces, "Script thread", () -> 
        { 
            if(TaskScheduler.get().getCurrentScript() != null)
                debug("Script thread trace: \n" + Arrays.toString(TaskScheduler.get().getCurrentScript().getThread().getStackTrace())); 
            else
                debug("No script active.");
        });
    }
}
