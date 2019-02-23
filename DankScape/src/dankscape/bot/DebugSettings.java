/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot;

import dankscape.api.plugin.menu.Menu;
import dankscape.api.plugin.menu.MenuAction;

/**
 *
 * @author Pieterjan
 */
public class DebugSettings
{
    public static boolean showFPS = true;
    public static boolean showMouseHistory = true;
    public static boolean showTileProjection = false;
    public static boolean showLoginScreenDebug = false;
    public static boolean showNPCs = false;
    public static boolean showModels = false;
    public static boolean showInventory = false;
    public static boolean showCamera = false;
    public static boolean showPosition = false;
    public static boolean showAnimationId = false;
    public static boolean showUpText = false;
    public static boolean showMenuOptions = false;
    public static boolean showInteractiveGameObjects = false;
    public static boolean showOtherGameObjects = false;
    
    public static boolean showAllWidgets = false;
    public static boolean showNoWidgets = true;
    public static int widgetRangeMin = -1;
    public static int widgetRangeMax = -1;
    
    private static boolean initialized = false;
    
    public static void setupUi(Menu debugMenu)
    {
        if(initialized)
            return;
        
        new MenuAction(debugMenu, "FPS", state -> { showFPS = state; }, true);
        new MenuAction(debugMenu, "Mouse History", state -> { showMouseHistory = state; }, true);
        
        new MenuAction(debugMenu, "Login Screen", state -> { showLoginScreenDebug = state; });
        
        new MenuAction(debugMenu, "Inventory", state -> { showInventory = state; });
        
        new MenuAction(debugMenu, "Camera", state -> { showCamera = state; });
        new MenuAction(debugMenu, "Positions", state -> { showPosition = state; });
        new MenuAction(debugMenu, "Animation Ids", state -> { showAnimationId = state; });
        
        new MenuAction(debugMenu, "NPCs", state -> { showNPCs = state; });
        
        Menu gobjects = new Menu("GameObjects", debugMenu);
        
        new MenuAction(gobjects, "Interactable Objects", state -> { showInteractiveGameObjects = state; });
        new MenuAction(gobjects, "Other Objects", state -> { showOtherGameObjects = state; });
        
        new MenuAction(debugMenu, "Models", state -> { showModels = state; });
        new MenuAction(debugMenu, "Tiles", state -> { showTileProjection = state; });
        new MenuAction(debugMenu, "Uptext", state -> { showUpText = state; });
        new MenuAction(debugMenu, "Menu Options", state -> { showMenuOptions = state; });
        

        initialized = true;
    }
}
