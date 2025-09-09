package org.duckdns.hjow.samples.base;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.duckdns.hjow.samples.scripts.ScriptBase;
import org.duckdns.hjow.samples.scripts.ScriptObject;
import org.duckdns.hjow.samples.uicomponent.JLogArea;
import org.duckdns.hjow.samples.util.UIUtil;

public class GUISampleJavaCodes extends SampleJavaCodes {
    protected JFrame frame;
    protected JMenu menuSamples;
    protected JToolBar toobarUp;
    protected JPanel desktop;
    protected JSplitPane splits;
    protected JLogArea taLog;
    protected JTextField tfScript;
    protected JButton btnScript;
    
    public GUISampleJavaCodes() {
        
    }
    
    @Override
    public void init(SampleJavaCodes instances) {
        try {
            LookAndFeelInfo[] looAndFeels = UIManager.getInstalledLookAndFeels();
            for(LookAndFeelInfo lookAndFeelOne : looAndFeels) {
                if(lookAndFeelOne.getName().equalsIgnoreCase("Nimbus")) {
                    UIManager.setLookAndFeel(lookAndFeelOne.getClassName());
                }
            }
        } catch(Exception ex) { ex.printStackTrace(); }
        
        frame = new JFrame();
        frame.setSize(600, 400);
        frame.setTitle("Sample Java Codes");
        frame.setLayout(new BorderLayout());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { exit(); }
        });
        frame.setIconImage(UIUtil.iconToImage(getDefaultIcon()));
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        frame.add(pnMain, BorderLayout.CENTER);
        
        JPanel pnUp = new JPanel();
        JPanel pnCenter = new JPanel();
        pnUp.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnMain.add(pnUp, BorderLayout.NORTH);
        pnMain.add(pnCenter, BorderLayout.CENTER);
        
        JToolBar toolbarScripts = new JToolBar();
        pnMain.add(toolbarScripts, BorderLayout.SOUTH);
        
        toolbarScripts.add(new JLabel("Script"));
        
        tfScript = new JTextField(30);
        toolbarScripts.add(tfScript);
        
        btnScript = new JButton(">>");
        toolbarScripts.add(btnScript);
        
        ActionListener listenerRunScript = new ActionListener() {  
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        actionRunScript();
                    }
                });
            }
        };
        tfScript.addActionListener(listenerRunScript);
        btnScript.addActionListener(listenerRunScript);
        
        splits = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        pnCenter.add(splits, BorderLayout.CENTER);
        
        taLog = new JLogArea();
        taLog.setLineWrap(true);
        splits.setBottomComponent(new JScrollPane(taLog));
        
        desktop = new JPanel();
        desktop.setLayout(null);
        splits.setTopComponent(desktop);
        
        toobarUp = new JToolBar();
        pnUp.add(toobarUp, BorderLayout.CENTER);
        
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu menu;
        JMenuItem mnItem;
        
        menu = new JMenu("파일");
        menuBar.add(menu);
        
        mnItem = new JMenuItem("종료");
        menu.add(mnItem);
        
        mnItem.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        
        menuSamples = new JMenu("프로그램");
        menuBar.add(menuSamples);
        
        List<ScriptObject> objList = new ArrayList<ScriptObject>();
        for(GUIProgram p : programs) { objList.add(p); }
        ScriptBase.init(instances, objList);
    }
    
    @Override
    public void exit() {
        frame.setVisible(false);
        super.exit();
    }
    
    @Override
    public void run(Properties args) {
        frame.setVisible(true);
        splits.setDividerLocation(0.5);
        tfScript.requestFocus();
    }
    
    @Override
    public void log(String msg) {
        super.log(msg);
        
        if(taLog != null) {
            taLog.log(msg);
        }
    }
    
    
    
    @Override
    public void applySampleList(SampleJavaCodes instances) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                refreshMenuSamplesIn(instances);
                refreshDesktopIcons(instances);
            }
        });
    }
    
    protected void refreshMenuSamplesIn(SampleJavaCodes instances) {
        menuSamples.removeAll();
        JMenuItem mn;
        
        Icon defaultIcon = getDefaultIcon();
        
        for(final GUIProgram p : programs) {
            if(p.isHidden()) continue;
            
            mn = new JMenuItem(p.getTitle());
            
            Icon icon = p.getIcon();
            if(icon != null) mn.setIcon(icon);
            else if(defaultIcon != null) mn.setIcon(defaultIcon);
            
            mn.addActionListener(new ActionListener() {   
                @Override
                public void actionPerformed(ActionEvent e) {
                    p.open(instances);
                }
            });
            menuSamples.add(mn);
        }
    }
    
    protected void refreshDesktopIcons(SampleJavaCodes instances) {
        desktop.removeAll();
        JButton btn;
        
        Icon defaultIcon = getDefaultIcon();
        int x, y, w, h;
        x = 10;
        y = 10;
        w = 80;
        h = 80;
        
        for(final GUIProgram p : programs) {
            if(p.isHidden()) continue;
            
            Icon icon = p.getIcon();
            if(icon == null && defaultIcon != null) icon = defaultIcon;
            if(icon == null) continue;
            
            btn = new JButton(p.getTitle(), icon);
            btn.setToolTipText(p.getTitle());
            
            btn.addActionListener(new ActionListener() {   
                @Override
                public void actionPerformed(ActionEvent e) {
                    p.open(instances);
                }
            });
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            
            btn.setBounds(new Rectangle(x, y, w, h));
            desktop.add(btn);
            
            x += w + 10;
            if(x >= desktop.getWidth()) { x = 10; y += h + 10; }
        }
    }
    
    protected void actionRunScript() {
        tfScript.setEditable(false);
        btnScript.setEnabled(false);
        
        final String scripts = tfScript.getText();
        tfScript.setText("");
        
        log(">> " + scripts);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Object res = ScriptBase.eval(null, scripts);
                    
                    if(res == null) log("");
                    else log(String.valueOf(res));
                } catch(Throwable t) {
                    JOptionPane.showMessageDialog(getWindow(), "Error : " + t.getMessage());
                } finally {
                    tfScript.setEditable(true);
                    btnScript.setEnabled(true);
                }
            }
        }).start();
    }
    
    public static Icon getDefaultIcon() {
        return UIManager.getIcon("FileView.fileIcon");
    }
}
