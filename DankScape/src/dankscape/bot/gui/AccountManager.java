/*
 * DankScape - An Old-School Runescape Bot written by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.bot.gui;

import dankscape.api.Login;
import dankscape.loader.AppletLoader;
import dankscape.loader.ConfigLoader;
import dankscape.loader.ConfigLoader.AccountConfig;
import dankscape.nativeinterface.NativeInterface;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Pieterjan
 */
public class AccountManager extends WindowAdapter
{
    private static JDialog window;
    private static JButton btnAddUser;
    private static JButton btnRemoveUser;
    private static JButton btnSetDefault;
    private static JButton btnSave;
    private static JTable tblAccounts;
    private static DefaultTableModel model;
    private static JLabel lblDefaultAccount;
    
    private static ArrayList<AccountConfig> accounts;
    
    private static AccountManager singleton = new AccountManager();
        
    protected AccountManager()
    {
        
    }
    
    private static void setupUi()
    {
        window = new JDialog(AppletLoader.getSingleton().getAppletFrame(), "Account Credentials Manager");
        window.addWindowListener(singleton);
        window.setSize(550, 250);
        window.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        
        JPanel panel;
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Account Credentials"));
        
        
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        window.add(mainPanel, c);
        
        
        tblAccounts = new JTable();
        model = (DefaultTableModel)tblAccounts.getModel();
        tblAccounts.createDefaultColumnsFromModel();
        tblAccounts.setRowSelectionAllowed(true);
        tblAccounts.setMaximumSize(new Dimension(9999, 9999));
        tblAccounts.setPreferredSize(new Dimension(9999, 9999));
        
        
        mainPanel.add(new JScrollPane(tblAccounts), c);
        
        lblDefaultAccount = new JLabel("Active account: ");
        
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        mainPanel.add(lblDefaultAccount, c);
        
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        
        btnAddUser = new JButton("Add User");
        btnRemoveUser = new JButton("Remove User");
        btnSetDefault = new JButton("Set as Active");
        btnSave = new JButton("Save");
        
        btnAddUser.addActionListener(a -> 
        { 
            accounts.add(new AccountConfig());
            copyUi();
            updateUi();
        });
        
        btnRemoveUser.addActionListener(a ->
        {
            int idx = tblAccounts.getSelectedRow();
            accounts.remove(idx);
            model.removeRow(idx);
            copyUi();
            updateUi();
        });
        
        btnSetDefault.addActionListener(a -> 
        {
            int idx = tblAccounts.getSelectedRow();
            for(AccountConfig cfg : accounts)
                cfg.isDefault = false;
            accounts.get(idx).isDefault = true;
            lblDefaultAccount.setText("Active account: " + accounts.get(idx).username);
            copyUi();
            updateUi();
        });
        
        btnSave.addActionListener(a -> 
        {
            commit();
        });
        
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(btnAddUser, c);
        c.gridy = 1;
        panel.add(btnRemoveUser, c);
        c.gridy = 2;
        panel.add(btnSetDefault, c);
        c.gridy = 3;
        panel.add(btnSave, c);
        c.gridy = 4;
        panel.add(Box.createVerticalGlue());
        
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(9, 5, 4, 5);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 1;
        window.add(panel, c);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(screenSize.width / 2 - window.getWidth() / 2, screenSize.height / 2 - window.getHeight() / 2);
    }
    
    private static void updateUi()
    {
        String columns[] = {"Username", "Password", "Bank Pin"};
        
        model.setRowCount(0);
        model.setColumnCount(columns.length);
        model.setColumnIdentifiers(columns);
        
        String defaultUser = "";
        for(AccountConfig cfg : accounts)
        {
            Object[] row = { cfg.username, cfg.password, cfg.bankpin };
            model.addRow(row);
            if(cfg.isDefault)
                defaultUser = cfg.username;
        }
        
        lblDefaultAccount.setText("Active account: " + defaultUser);
        
        model.fireTableDataChanged();
    }
    
    private static void copyUi()
    {
        //accounts = new ArrayList();
        for(int i = 0;i < tblAccounts.getRowCount();i++)
        {
            AccountConfig cfg = new AccountConfig();
            if(i < accounts.size())
                cfg = accounts.get(i);
            cfg.username = (String)model.getValueAt(i, 0);
            cfg.password = (String)model.getValueAt(i, 1);
            cfg.bankpin = (int)model.getValueAt(i, 2);
            if(i >= accounts.size())
                accounts.add(cfg);
        }
    }
    
    private static void commit()
    {
        try
        {
            int nDefaults = 0;
            int i = 0;

            HashSet<String> usernames = new HashSet();
            for(AccountConfig cfg : accounts)
            {
                cfg.username = (String)model.getValueAt(i, 0);
                cfg.password = (String)model.getValueAt(i, 1);
                cfg.bankpin = (int)model.getValueAt(i, 2);

                if(cfg.isDefault)
                    nDefaults++;

                if(usernames.contains(cfg.username))
                    throw new IllegalStateException("Duplicate usernames!");
                usernames.add(cfg.username);
                if(cfg.username.trim().equals("") || cfg.password.trim().equals(""))
                    throw new IllegalStateException("Missing Username/Password in row " + i + "!");

                i++;
            }
            if(nDefaults > 1 || nDefaults == 0)
                throw new IllegalStateException("More than one account selected as default!");
            
            ConfigLoader.accounts = accounts;
            ConfigLoader.saveConfig();
            Login.setAccount(ConfigLoader.getDefaultAccount());
            window.setVisible(false);
        }
        catch(IllegalStateException ex)
        {
            NativeInterface.println("ERROR: " + ex.toString());
        }
    }
    
    public static void open()
    {
        if(window == null)
            setupUi();
        accounts = new ArrayList();
        for(AccountConfig cfg : ConfigLoader.accounts)
        {
            AccountConfig c = new AccountConfig();
            c.username = cfg.username;
            c.password = cfg.password;
            c.bankpin = cfg.bankpin;
            c.isDefault = cfg.isDefault;
            accounts.add(c);
        }
        updateUi();
        window.setVisible(true);
    }
    
    @Override
    public void windowClosing(WindowEvent e)
    {
        window.setVisible(false);
    }
}
