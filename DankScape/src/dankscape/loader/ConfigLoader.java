/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.loader;

import dankscape.misc.DebugWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Pieterjan
 */
public class ConfigLoader extends DebugWriter
{
    public static class AccountConfig 
    {
        public String username = "", password = "";
        public int bankpin = -1;
        public boolean isDefault = false;
    }
    
    public static final String CONFIG_PATH = System.getenv("APPDATA") + "/DankScape";
    public static final String CONFIG_NAME = "config.txt";
    
    public static HashMap<String, String> settings = new HashMap();
    public static ArrayList<AccountConfig> accounts = new ArrayList();
    
    private static Node getChildByName(Node parent, String name)
    {
        NodeList childNodes = parent.getChildNodes();
        for(int i = 0;i < childNodes.getLength();i++)
        {
            if(childNodes.item(i).getNodeName().equals(name))
                return childNodes.item(i);
        }
        return null;
    }
    
    private static String getChildValueByName(Node parent, String name)
    {
        NodeList childNodes = parent.getChildNodes();
        for(int i = 0;i < childNodes.getLength();i++)
        {
            if(childNodes.item(i).getNodeName().equals(name))
                return childNodes.item(i).getTextContent();
        }
        return null;
    }
    
    public static void loadConfig()
    {
        sdebug("Loading config...");
        File dir = new File(CONFIG_PATH);
        if(!dir.isDirectory())
            dir.mkdirs();
        File config = new File(CONFIG_PATH + "/" + CONFIG_NAME);
        
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docbuilder = factory.newDocumentBuilder();
            Document doc = docbuilder.parse(config);
            NodeList rootNodes = doc.getElementsByTagName("Config");
            
            if(rootNodes.getLength() <= 0)
                throw new SAXException("Rootnode not found!");
            
            Node node = getChildByName(rootNodes.item(0), "Settings");
            settings = new HashMap();
            accounts = new ArrayList();
            
            NodeList nodes = node.getChildNodes();
            for(int i = 0;i < nodes.getLength();i++)
            {
                Node n = nodes.item(i);
                if(n.getNodeType() == Node.ELEMENT_NODE)
                {
                String name = n.getNodeName();
                String value = n.getTextContent();
                settings.put(name, value);
                }
            }
            
            node = getChildByName(rootNodes.item(0), "Accounts");
            nodes = node.getChildNodes();
            for(int i = 0;i < nodes.getLength();i++)
            {
                Node n = nodes.item(i);
                if(n.getNodeName().equals("account"))
                {
                    AccountConfig cfg = new AccountConfig();
                    cfg.username = getChildValueByName(n, "username");
                    cfg.password = getChildValueByName(n, "password");
                    
                    if(getChildValueByName(n, "bankpin") != null)
                        cfg.bankpin = Integer.parseInt(getChildValueByName(n, "bankpin"));
                    else 
                        cfg.bankpin = -1;

                    Node defaultAttrib = n.getAttributes().getNamedItem("default");
                    if(defaultAttrib != null)
                        cfg.isDefault = Boolean.parseBoolean(defaultAttrib.getNodeValue());

                    accounts.add(cfg);
                }
                
            }
            
        } 
        catch (FileNotFoundException ex)
        {
            sdebug("ERROR: Could not open config: " + ex.toString());
        } catch (SAXException | ParserConfigurationException | IOException ex)
        {
            sdebug("ERROR: Could not open config: " + ex.toString());
        }
    }
    
    public static void saveConfig()
    {
        sdebug("Saving config...");
        File dir = new File(CONFIG_PATH);
        if(!dir.isDirectory())
            dir.mkdirs();
        File config = new File(CONFIG_PATH + "/" + CONFIG_NAME);
        try
        {
            String tab = "    ";
            FileOutputStream out = new FileOutputStream(config);
            out.write("<Config>\r\n\r\n".getBytes());
            out.write("<Settings>\r\n".getBytes());
            for(String setting : settings.keySet())
                out.write((tab + "<" + setting + ">" + settings.get(setting) + "</" + setting + ">\r\n").getBytes());
            out.write("</Settings>\r\n".getBytes());
            out.write("<Accounts>\r\n".getBytes());
            for(AccountConfig cfg : accounts)
            {
                if(cfg.isDefault)
                    out.write((tab + "<account default='true'>\r\n").getBytes());
                else
                    out.write((tab + "<account>\r\n").getBytes());
                out.write((tab + tab + "<username>" + cfg.username + "</username>\r\n").getBytes());
                out.write((tab + tab + "<password>" + cfg.password + "</password>\r\n").getBytes());
                out.write((tab + tab + "<bankpin>" + cfg.bankpin + "</bankpin>\r\n").getBytes());
                
                out.write((tab + "</account>\r\n").getBytes());
            }
            out.write("</Accounts>\r\n".getBytes());
            out.write("\r\n</Config>\r\n".getBytes());
            out.close();
        } 
        catch (FileNotFoundException ex)
        {
            sdebug("ERROR: Could not save config: " + ex.toString());
        } catch (IOException ex)
        {
            sdebug("ERROR: Could not save config: " + ex.toString());
        }
    }
    
    public static AccountConfig getDefaultAccount()
    {
        for(AccountConfig cfg : accounts)
        {
            if(cfg.isDefault)
                return cfg;
        }
        return null;
    }
    
    public static AccountConfig getAccountByName(String username)
    {
        for(AccountConfig cfg : accounts)
        {
            if(cfg.username.equals(username))
                return cfg;
        }
        return null;
    }
    
    public static void setDefaultAccountByName(String username)
    {
        for(AccountConfig cfg : accounts)
        {
            if(cfg.username.equals(username))
                cfg.isDefault = true;
            else
                cfg.isDefault = false;
        }
    }
    
    public static void setAccount(String username, String password, int bankpin, boolean setAsDefault)
    {
        AccountConfig cfg = getAccountByName(username);
        if(cfg == null)
            cfg = new AccountConfig();
        
        cfg.username = username;
        cfg.password = password;
        cfg.bankpin = bankpin;
        accounts.add(cfg);
        
        if(setAsDefault || getDefaultAccount() == null)
            setDefaultAccountByName(username);
    }
    
    public static void setSetting(String name, String value)
    {
        settings.put(name, value);
    }
    
    public static void setSetting(String name, int value)
    {
        settings.put(name, "" + value);
    }
    
    public static void setSetting(String name, double value)
    {
        settings.put(name, "" + value);
    }
    
    public static void setSetting(String name, boolean value)
    {
        settings.put(name, value ? "true" : "false");
    }
    
    public static String getSetting(String name)
    {
        return settings.get(name);
    }
    
    public static String getSetting(String name, String defaultVal)
    {
        String s = settings.get(name);
        if(s == null)
            return defaultVal;
        return s;
    }
    
    public static int getSetting(String name, int defaultVal)
    {
        String s = settings.get(name);
        if(s == null)
            return defaultVal;
        return Integer.parseInt(s);
    }
    
    public static double getSetting(String name, double defaultVal)
    {
        String s = settings.get(name);
        if(s == null)
            return defaultVal;
        return Double.parseDouble(s);
    }
    
    public static boolean getSetting(String name, boolean defaultVal)
    {
        String s = settings.get(name);
        if(s == null)
            return defaultVal;
        return Boolean.parseBoolean(s);
    }
}
