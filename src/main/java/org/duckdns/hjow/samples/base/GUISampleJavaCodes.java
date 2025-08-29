package org.duckdns.hjow.samples.base;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class GUISampleJavaCodes extends SampleJavaCodes {
    protected JFrame frame;
    protected JMenu menuSamples;
    protected JTextArea taLog;
    
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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        frame.add(pnMain, BorderLayout.CENTER);
        
        taLog = new JTextArea();
        taLog.setEditable(false);
        taLog.setLineWrap(true);
        pnMain.add(new JScrollPane(taLog), BorderLayout.CENTER);
        
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
                frame.setVisible(false);
                System.exit(0);
            }
        });
        
        menuSamples = new JMenu("프로그램");
        menuBar.add(menuSamples);
    }
    
    @Override
    public void run(Properties args) {
        frame.setVisible(true);
    }
    
    @Override
    public void log(String msg) {
        super.log(msg);
        
        if(taLog != null) {
            taLog.setText(taLog.getText() + "\n" + msg);
            taLog.setCaretPosition(taLog.getDocument().getLength() - 1);
        }
    }
    
    @Override
    public void applySampleList(SampleJavaCodes instances) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                menuSamples.removeAll();
                JMenuItem mn;
                for(final Program p : programs) {
                    mn = new JMenuItem(p.getTitle());
                    mn.addActionListener(new ActionListener() {   
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            p.open(instances);
                        }
                    });
                    menuSamples.add(mn);
                }
            }
        });
    }
}
