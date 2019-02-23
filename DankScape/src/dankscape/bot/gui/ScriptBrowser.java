/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot.gui;

import dankscape.api.DankScript;
import dankscape.loader.AppletLoader;
import dankscape.loader.ScriptLoader;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author Pieterjan
 */
public class ScriptBrowser extends WindowAdapter implements ComponentListener
{
    private static final int MARGIN = 5;
    private static final int HSPACING = 5;
    private static final int VSPACING = 5;
    
    private static JDialog window = null;
    private static JTextField txtJar;
    private static JComboBox cmbClassName;
    private static JEditorPane txtDescription;
    private static JButton btnSelect;
    private static HTMLEditorKit kit;
    
    private static String selectedJar, lastJar;
    private static DankScript previewedScript = null, selectedScript = null;
    
    protected ScriptBrowser()
    {
    }
    
    private static void setupUi()
    {
        window = new JDialog(AppletLoader.getSingleton().getAppletFrame(), "Script Browser");
        window.addWindowListener(new ScriptBrowser());
        //window.setResizable(false);
        window.setSize(500, 500);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(screenSize.width / 2 - window.getWidth() / 2, screenSize.height / 2 - window.getHeight() / 2);
        
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = HSPACING;
        c.ipady = VSPACING;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0;
        c.weightx = 1;
        c.insets = new Insets(2, 5, 2, 5);
        
        window.setLayout(layout);
        window.addComponentListener(new ScriptBrowser());
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        JLabel lblInfo = new JLabel("Enter the script package (.jar) that contains the script classes:");
        lblInfo.setMaximumSize(new Dimension(9999, 22));
        
        panel.add(Box.createRigidArea(new Dimension(MARGIN, 0)));
        panel.add(lblInfo);
        panel.add(Box.createRigidArea(new Dimension(MARGIN, 0)));
        
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        window.add(panel, c);
        
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        File scriptsDir = new File("scripts");
        if(!scriptsDir.isDirectory())
            scriptsDir.mkdir();
        

        JFileChooser fileChooser = new JFileChooser(scriptsDir);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileFilter(){
            @Override
            public boolean accept(File f)
            {
                return f.getAbsolutePath().toLowerCase().endsWith(".jar");
            }

            @Override
            public String getDescription()
            {
                return "Jar files (*.jar)";
            }
        });
        
        txtJar = new JTextField();
        txtJar.setMaximumSize(new Dimension(99999, 23));
        JButton btnBrowseJar = new JButton("Browse jar..");
        btnBrowseJar.addActionListener((action) -> 
        { 
            int result = fileChooser.showOpenDialog(window);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                lastJar = null;
                selectedJar = fileChooser.getSelectedFile().getAbsolutePath();
                updateUi();
            }
        });
        btnBrowseJar.setMaximumSize(new Dimension(90, 23));
        
        txtJar.setMaximumSize(new Dimension(9999, 24));
        txtJar.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    lastJar = null;
                    updateUi();
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
            }
        });
        
        panel.add(Box.createRigidArea(new Dimension(MARGIN, 0)));
        panel.add(txtJar);
        panel.add(Box.createRigidArea(new Dimension(HSPACING, 0)));
        panel.add(btnBrowseJar);
        panel.add(Box.createRigidArea(new Dimension(MARGIN, 0)));
        
        c.gridy = 1;
        window.add(panel, c);
        
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        cmbClassName = new JComboBox();
        cmbClassName.addItem("<No script classes found>");
        cmbClassName.setMaximumSize(new Dimension(9999, 23));
        
        cmbClassName.addItemListener((ItemEvent e) -> { updateBody(); });
        panel.add(Box.createRigidArea(new Dimension(MARGIN, 0)));
        panel.add(new JLabel("Select class: "));
        panel.add(Box.createRigidArea(new Dimension(HSPACING, 0)));
        panel.add(cmbClassName);
        panel.add(Box.createRigidArea(new Dimension(MARGIN, 0)));
        c.gridy = 2;
        window.add(panel, c);
        
        txtDescription = new JEditorPane();
        txtDescription.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        kit = new HTMLEditorKit();
        txtDescription.setEditorKit(kit);
        txtDescription.setDocument(kit.createDefaultDocument());
        txtDescription.setText("<b>No script selected.</b><p>Please select one from the list above.<p>");
        
        txtDescription.setMaximumSize(new Dimension(9999, 9999));
        txtDescription.setPreferredSize(new Dimension(9999, 9999));
        txtDescription.setMinimumSize(new Dimension(9999, 9999));
        
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        panel.add(Box.createRigidArea(new Dimension(MARGIN, 0)));
        panel.add(txtDescription);
        panel.add(Box.createRigidArea(new Dimension(MARGIN, 0)));
        
        c.gridy = 3;
        c.weighty = 1;
        window.add(panel, c);
        c.weighty = 0;
        
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        
        JButton btnCancel = new JButton("Cancel");
        btnSelect = new JButton("Select");
        
        btnCancel.setMaximumSize(new Dimension(90, 24));
        btnSelect.setMaximumSize(new Dimension(90, 24));
        
        btnCancel.addActionListener(a -> { window.setVisible(false); });
        btnSelect.addActionListener(a -> { window.setVisible(false); selectedScript = previewedScript; });
        
        btnSelect.setEnabled(false);
        
        panel.add(Box.createHorizontalGlue());
        panel.add(btnCancel);
        panel.add(Box.createRigidArea(new Dimension(HSPACING, 0)));
        panel.add(btnSelect);
        panel.add(Box.createRigidArea(new Dimension(MARGIN, 0)));
        
        
        c.gridy = 4;
        window.add(panel, c);
    }
    
    private static void setCSS(String css)
    {
        if(kit == null)
            return;
        StyleSheet ss = new StyleSheet();
        ss.addRule(css);
        kit.setStyleSheet(ss);
    }
    
    private static void setHTML(String body)
    {
        if(kit == null)
            return;
        txtDescription.setText("<body>\n" + body + "\n</body>");
    }
    
    private static void updateUi()
    {
        txtJar.setText(selectedJar);
        if(selectedJar != null)
            if(!selectedJar.equals(lastJar))
            {
                lastJar = selectedJar;
                ScriptLoader.loadJar(selectedJar);
                cmbClassName.removeAllItems();
                for(Class c : ScriptLoader.getAvailableScriptClasses())
                {
                    DankScript script = ScriptLoader.instantiateScript(c); // The script's constructor gets called at this point
                    if(script != null)
                        cmbClassName.addItem(script);
                }
                if(cmbClassName.getItemCount() == 0)
                    cmbClassName.addItem("<No script classes found>");
            }
    }
    
    private static String generateDefaultHTML()
    {
        return "" + previewedScript.getClass().getName() +
                "<h1>" + previewedScript.getScriptName() + "</h1>" +
                        "<h2>Version " + previewedScript.getVersion() + "</h2>" +
                        "<p><b>Author: </b>" + previewedScript.getAuthor() + "</p>" +
                        "<p><b>Description: </b>" + previewedScript.getDescription() + "</p>";
    }
    
    
    
    public static void updateBody()
    {
        if(cmbClassName.getSelectedItem() instanceof DankScript)
            previewedScript = (DankScript)cmbClassName.getSelectedItem();
        else 
            previewedScript = null;
        
        if(previewedScript != null)
        {
            if(!previewedScript.getCustomCSS().equals(""))
                setCSS(previewedScript.getCustomCSS());
            else
                setCSS("h2 { float: right; display: float; color: red; }");
            
            if(!previewedScript.getCustomHTML().equals(""))
            {
                String html = previewedScript.getCustomHTML();
                html = html.replace("@AUTHOR", previewedScript.getAuthor());
                html = html.replace("@SCRIPTNAME", previewedScript.getScriptName());
                html = html.replace("@DESCRIPTION", previewedScript.getDescription());
                html = html.replace("@VERSION", previewedScript.getVersion());
                setHTML(html);
            }
            else
                setHTML(generateDefaultHTML());
            
            btnSelect.setEnabled(true);
        }
        else
        {
            btnSelect.setEnabled(false);
            setCSS("");
            setHTML("<b>No script selected.</b><p>Please select one from the list above.<p>");
        }
    }
    
    public static void open()
    {
        if(window == null)
            setupUi();
        
        updateUi();
        window.setVisible(true);
    }
    
    public static String getSelectedJarName()
    {
        return selectedJar;
    } 
    
    public static int getSelectedClassIndex()
    {
        return cmbClassName.getSelectedIndex();
    }
    
    public static DankScript getPreviewedScript()
    {
        return previewedScript;
    }
    
    public static DankScript getSelectedScript()
    {
        return selectedScript;
    }
    
    public static void setJarName(String s)
    {
        if(window == null)
            setupUi();
        
        lastJar = null;
        selectedJar = s;
        updateUi();
    } 
    
    public static void setSelectedClassIndex(int idx)
    {
        if(window == null)
            setupUi();
        
        if(idx < 0)
            idx = 0;
        if(idx >= cmbClassName.getItemCount())
            idx = cmbClassName.getItemCount() - 1;
        
        if(cmbClassName.getItemCount() != 0)
            cmbClassName.setSelectedIndex(idx);
        
        selectedScript = previewedScript;
    }
    
    @Override
    public void windowClosing(WindowEvent e)
    {
        window.setVisible(false);
    }

    @Override
    public void componentResized(ComponentEvent e)
    {
        if(txtDescription != null)
        {
            txtDescription.setPreferredSize(new Dimension(window.getWidth() - MARGIN * 8, 9999));
            txtDescription.setMaximumSize(new Dimension(window.getWidth() - MARGIN * 8, 9999));
        }
    }

    @Override
    public void componentMoved(ComponentEvent e)
    {
        
    }

    @Override
    public void componentShown(ComponentEvent e)
    {

    }

    @Override
    public void componentHidden(ComponentEvent e)
    {

    }
}
