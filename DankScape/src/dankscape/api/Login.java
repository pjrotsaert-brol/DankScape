/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api;

import dankscape.api.internal.ActionContext;
import dankscape.api.rs.RSClient;
import dankscape.loader.AppletLoader;
import dankscape.loader.ConfigLoader.AccountConfig;
import dankscape.misc.DebugWriter;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author Pieterjan
 */
public class Login extends DebugWriter
{
    private static final int LOGININDEX_START  = 0;
    private static final int LOGININDEX_PROMPT = 2;
    private static final int LOGININDEX_ERROR  = 3;
    private static final int LOGININDEX_RECOVER = 5;
    
    private static final int INTERFACE_WELCOME = 378;
    private static final int INTERFACE_WELCOME_PLAYBUTTON = 6;
    
    public static final int INTERFACE_LOGOUT   = 182;
    public static final int INTERFACE_LOGOUT_LOGOUTBTN = 10;
    
    private static boolean welcomeScreenHasLoaded = false;
    
    private static final int LOGINSCREEN_W = 765;
    //private static final int LOGINSCREEN_H = 503;
    
    private static AccountConfig loginAccount = null;
    
    public static enum Status 
    {
        UNKNOWN,
        BOOTING,
        LOADING,
        LOGINSCREEN_START,
        LOGINSCREEN_PROMPT,
        LOGINSCREEN_CONNECTING,
        LOGINSCREEN_ERROR,
        LOGINSCREEN_RECOVER,
        LOGINSCREEN_BANNED,
        LOGINSCREEN_MEMBERS,
        WORLDSELECTION,
        WELCOMESCREEN,
        INGAME
    }
    
    static
    {
        //setStaticTag("Login API");
    }
    
    public static void setAccount(AccountConfig config)
    {
        loginAccount = config;
    }
    
    public static AccountConfig getAccount()
    {
        return loginAccount;
    }
    
    public static Status getStatus()
    {
        //Debug.println("Login index: " + RSClient.getLoginIndex() + " " +  Game.getLoginResponse() + "  " + RSClient.getPassword());
        if( (
                AppletLoader.getSingleton().getGameCanvas() == null || 
                Misc.checkScreenColor(8 + getScreenXOffset(), 11, 0, 0, 0, 1)
            ) && RSClient.getGameState() <= Game.GAMESTATE_LOGINSCREEN)
        {
            return Status.BOOTING;
        }
        
        if(RSClient.getGameState() == Game.GAMESTATE_INGAME)
        {
            Widget welcomeItf = Widget.get(INTERFACE_WELCOME, 0);
            
            if(welcomeItf == null && welcomeScreenHasLoaded)
                return Status.INGAME;
            if(welcomeItf != null && welcomeScreenHasLoaded)
                if(!welcomeItf.isVisible())
                    return Status.INGAME;

            if(!welcomeScreenHasLoaded)
            {
                //if(!Misc.checkScreenColor(getScreenXOffset() + 271 + 9, 292 + 10, 72, 2, 0, 30))
                Widget playBtn = Widget.get(INTERFACE_WELCOME, INTERFACE_WELCOME_PLAYBUTTON);
                boolean isVis = false;
                if(playBtn != null)
                    isVis = playBtn.isVisible();
                
                if(!isVis)
                    return Status.LOADING;
                else
                {
                    welcomeScreenHasLoaded = true;
                    return Status.WELCOMESCREEN;
                }
            }
            return Status.WELCOMESCREEN;
        }
        
        if(RSClient.getWorldSelectShown())
            return Status.WORLDSELECTION;
        
        switch(RSClient.getLoginIndex())
        {
        case LOGININDEX_START:
            return Status.LOGINSCREEN_START;
        case LOGININDEX_PROMPT:
            if(getLoginMessage().toLowerCase().contains("account locked"))
                return Status.LOGINSCREEN_BANNED;
            if(getLoginMessage().toLowerCase().contains("connecting"))
                return Status.LOGINSCREEN_CONNECTING;
            if(getLoginMessage().toLowerCase().contains("members account"))
                return Status.LOGINSCREEN_MEMBERS;
            return Status.LOGINSCREEN_PROMPT;
        case LOGININDEX_ERROR:
            return Status.LOGINSCREEN_ERROR;
        case LOGININDEX_RECOVER:
            return Status.LOGINSCREEN_RECOVER;
        }
        
        if(RSClient.getGameState() == Game.GAMESTATE_LOADING)
            return Status.LOADING;
        
        return Status.UNKNOWN;
    }
    
    /*public static void setLoginCredentials(String username, String password)
    {
        loginUsername = username;
        loginPassword = password;
    }*/
    
    public static void login()
    {
        Status s = getStatus();
        if(s == Status.INGAME)
            return;
            
        int nTries = 0;
        
        if(loginAccount == null)
        {
            Misc.messageBox("ERROR: No account has been set!\nGo to 'Tools' -> 'Account Manager' and add an account to resolve this problem!");
            throw new IllegalStateException("No account has been set!\nGo to 'Tools' -> 'Account Manager' and add an account to resolve this problem!");
        }
        
        while(s != Status.INGAME)
        {
            s = getStatus();
            
            if(s == Status.LOGINSCREEN_START)
            {
                Point p = Misc.randomPointInRect(getButtonBounds_Start_ExistingUser());
                ActionContext.get().clickLeft(p.x, p.y);
            }

            if(s == Status.LOGINSCREEN_PROMPT)
            {
                if(!RSClient.getUsername().equals(loginAccount.username))
                {
                    if(!RSClient.getUsername().equals(""))
                    {
                        ActionContext.get()
                                .clickLeft(Misc.randomPointInRect(getTextBoxBounds_Username()))
                                .sleep(100, 250)
                                .pressBackspace(RSClient.getUsername().length())
                                //.pressKey(KeyEvent.VK_BACKSPACE, o -> RSClient.getUsername().equals(""), 104, 166)
                                .sleep(60, 170)
                                .enterText(loginAccount.username)
                                .sleep(100, 250);
                    }
                    else
                    {
                        ActionContext.get()
                               .clickLeft(Misc.randomPointInRect(getTextBoxBounds_Username()))
                               .enterText(loginAccount.username)
                               .sleep(100, 250);
                    }
                }
                
                if(!RSClient.getPassword().equals(loginAccount.password))
                {
                    if(!RSClient.getPassword().equals(""))
                    {
                        ActionContext.get()
                                .clickLeft(Misc.randomPointInRect(getTextBoxBounds_Password()))
                                .sleep(100, 250)
                                .pressBackspace(RSClient.getPassword().length())
                                //.pressKey(KeyEvent.VK_BACKSPACE, o -> RSClient.getPassword().equals(""), 104, 166)
                                .sleep(60, 170)
                                .enterText(loginAccount.password)
                                .sleep(100, 250);
                    }
                    else
                    {
                        ActionContext.get()
                               .clickLeft(Misc.randomPointInRect(getTextBoxBounds_Password()))
                               .enterText(loginAccount.password)
                               .sleep(100, 250);
                    }
                }
                
                if(RSClient.getUsername().equals(loginAccount.username) && RSClient.getPassword().equals(loginAccount.password))
                {
                    welcomeScreenHasLoaded = false;
                    ActionContext.get()
                            .clickLeft(Misc.randomPointInRect(getButtonBounds_Prompt_Login()))
                            .sleep(260, 340);
                    nTries++;
                }
            }
            
            if(s == Status.WELCOMESCREEN)
            {
                ActionContext.get()
                        .clickLeft(Misc.randomPointInRect(getButtonBounds_Welcome_Play()))
                        .sleep(2200, 3254);
            }
            
            if(s == Status.LOGINSCREEN_ERROR)
            {
                sdebug("Login attempt unsuccessful.");
                if(nTries >= 5)
                    throw new IllegalStateException("Failed to log in 5 times! Aborting.");
                
                sdebug("Trying again..");
                ActionContext.get()
                        .clickLeft(Misc.randomPointInRect(getButtonBounds_Error_TryAgain()))
                        .sleep(1250, 5000);
            }
            
            if(s == Status.LOGINSCREEN_BANNED)
            {
                sdebug("Oops, something seems to be wrong with your account..");
                sdebug("(Note that I am not in any way responsible for this occurrence)");
                throw new IllegalStateException("Account locked.");
            }
            
            if(s == Status.LOGINSCREEN_MEMBERS)
                throw new IllegalStateException("F2P account trying to log into a members world.");
            
            if(s == Status.LOGINSCREEN_RECOVER)
                ActionContext.get().clickLeft(Misc.randomPointInRect(getButtonBounds_Recover_Cancel())).sleep(100, 250);
            
            Misc.sleep(1);
        }
        sdebug("Login successful!");
    }
    
    public static boolean logout()
    {
        Status s = getStatus();
        while(s == Status.INGAME || s == Status.LOADING)
        {
            Widget logoutItf = Widget.get(INTERFACE_LOGOUT, 0);
            Widget logoutTab = Widget.get(Widget.TAB_LOGOUT);
            
            if(logoutItf == null || logoutTab == null)
            {
                sdebug("ERROR: Could not retrieve logout interface!");
                //return false;
            }
            
            if(!logoutItf.isVisible())
            {
                ActionContext.get().clickLeft(Misc.randomPointInRect(logoutTab.getBounds()))
                        .sleep(100, 200);
            }
            
            Widget logoutButton = Widget.get(INTERFACE_LOGOUT, INTERFACE_LOGOUT_LOGOUTBTN);
            if(logoutButton == null)
            {
                sdebug("ERROR: Could not find logout button!");
            }
            else
            {
                ActionContext.get()
                        .clickLeft(Misc.randomPointInRect(logoutButton.getBounds()))
                        .sleep(4042, 6073);
            }
            
            s = getStatus();
            Misc.sleep(100);
        }
        return true;
    }
    
    private static int getScreenXOffset()
    {
        return (AppletLoader.getSingleton().getGameCanvas().getWidth() - LOGINSCREEN_W) / 2;
    }
    
    private static Rectangle getButtonBounds_Start_ExistingUser()
    {
        return new Rectangle(389 + getScreenXOffset(), 271, 146, 40);
    }
    
    private static Rectangle getButtonBounds_Prompt_Cancel()
    {
        return new Rectangle(389 + getScreenXOffset(), 301, 146, 40);
    }
    
    private static Rectangle getButtonBounds_Recover_Recover()
    {
        return getButtonBounds_Prompt_Login();
    }
    
    private static Rectangle getButtonBounds_Recover_Cancel()
    {
        return getButtonBounds_Prompt_Cancel();
    }
    
    private static Rectangle getButtonBounds_Prompt_Login()
    {
        return new Rectangle(229 + getScreenXOffset(), 301, 146, 40);
    }
    
    private static Rectangle getButtonBounds_Error_TryAgain()
    {
        return new Rectangle(309 + getScreenXOffset(), 256, 146, 40);
    }
    
    private static Rectangle getButtonBounds_Error_ForgotPassword()
    {
        return new Rectangle(309 + getScreenXOffset(), 306, 146, 40);
    }
    
    private static Rectangle getTextBoxBounds_Username()
    {
        return new Rectangle(272 + getScreenXOffset(), 242, 242, 12);
    }
    
    private static Rectangle getTextBoxBounds_Password()
    {
        return new Rectangle(272 + getScreenXOffset(), 256, 242, 12);
    }
    
    private static Rectangle getButtonBounds_Welcome_Play()
    {
        Widget playButton = Widget.get(INTERFACE_WELCOME, INTERFACE_WELCOME_PLAYBUTTON);
        if(playButton != null)
            return playButton.getBounds();
        return new Rectangle(0, 0, 0, 0);
    }
    
    private static Rectangle getButtonBounds_SelectWorld()
    {
        return new Rectangle(5 + getScreenXOffset(), 463, 100, 35);
    }
    
    private static Rectangle getButtonBounds_WorldSelect_Cancel()
    {
        return new Rectangle(708 + getScreenXOffset(), 4, 50, 16);
    }
    
    public static void debugDrawButtons(Graphics g)
    {
        Status s = getStatus(); 
        
        if(s != Status.INGAME && s != Status.WELCOMESCREEN && 
                s != Status.WORLDSELECTION && s != Status.UNKNOWN && s != Status.LOADING)
            Misc.debugDrawRect(g, getButtonBounds_SelectWorld(), 0, 255, 255);
        
        switch (s)
        {
            case LOGINSCREEN_START:
                Misc.debugDrawRect(g, getButtonBounds_Start_ExistingUser(), 0, 255, 255);
                break;
            case LOGINSCREEN_PROMPT:
            case LOGINSCREEN_MEMBERS:
                Misc.debugDrawRect(g, getButtonBounds_Prompt_Cancel(), 0, 255, 255);
                Misc.debugDrawRect(g, getButtonBounds_Prompt_Login(), 0, 255, 255);
                Misc.debugDrawRect(g, getTextBoxBounds_Username(), 0, 255, 255);
                Misc.debugDrawRect(g, getTextBoxBounds_Password(), 0, 255, 255);
                break;
            case LOGINSCREEN_ERROR:
                Misc.debugDrawRect(g, getButtonBounds_Error_TryAgain(), 0, 255, 255);
                Misc.debugDrawRect(g, getButtonBounds_Error_ForgotPassword(), 0, 255, 255);
                break;
            case LOGINSCREEN_RECOVER:
                Misc.debugDrawRect(g, getButtonBounds_Recover_Cancel(), 0, 255, 255);
                Misc.debugDrawRect(g, getButtonBounds_Recover_Recover(), 0, 255, 255);
                break;
            case WELCOMESCREEN:
                Misc.debugDrawRect(g, getButtonBounds_Welcome_Play(), 0, 255, 255);
            case WORLDSELECTION:
                Misc.debugDrawRect(g, getButtonBounds_WorldSelect_Cancel(), 0, 255, 255);
                break;
            default:
                break;
        }
    }
    
    public static String getLoginMessage()
    {
        return RSClient.getLoginMessage1() + RSClient.getLoginMessage2() + RSClient.getLoginMessage3();
    }
    
    public static String getLoginResponse()
    {
        return RSClient.getLogin_response0();
    }
}
