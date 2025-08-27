package org.duckdns.hjow.samples.propcryptor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public class GUICryptor {
    protected SimpleCryptor cryptor;
    
    protected JFrame            frame;
    protected JPanel            pnText, pnGlobals;
    protected JToolBar          toolbar;
    protected JTabbedPane       tab;
    
    protected JSplitPane        jsplitText, jsplitProp;
    protected JTextArea         taBefText, taAftText, taBefProp, taAftProp;
    
    protected JPasswordField    tfPassword;
    protected JComboBox<String> cbAlg;
    
    protected JButton           btnToolbarEncrypt, btnToolbarDecrypt;
    
    public GUICryptor() throws InstantiationException, IllegalAccessException, ClassNotFoundException, UnsupportedLookAndFeelException {
        cryptor = new SimpleCryptor();
        
        LookAndFeelInfo[] looAndFeels = UIManager.getInstalledLookAndFeels();
        for(LookAndFeelInfo lookAndFeelOne : looAndFeels) {
            if(lookAndFeelOne.getName().equalsIgnoreCase("Nimbus")) {
                UIManager.setLookAndFeel(lookAndFeelOne.getClassName());
            }
        }
        
        frame = new JFrame();
        frame.setSize(790, 590);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("암호화 도구");
        
        JPanel pnMain;
        
        pnMain = new JPanel();
        frame.add(pnMain, BorderLayout.CENTER);
        pnMain.setLayout(new BorderLayout());
        
        toolbar = new JToolBar();
        pnMain.add(toolbar, BorderLayout.NORTH);
        
        Vector<String> algs = new Vector<String>();
        algs.add("SHA-256");
        
        btnToolbarEncrypt = new JButton("암호화");
        btnToolbarDecrypt = new JButton("복호화");
        tfPassword = new JPasswordField(15);
        cbAlg = new JComboBox<String>(algs);
        cbAlg.setEditable(true);
        
        toolbar.add(cbAlg);
        toolbar.add(tfPassword);
        toolbar.add(btnToolbarEncrypt);
        toolbar.add(btnToolbarDecrypt);
        
        tab = new JTabbedPane();
        pnMain.add(tab, BorderLayout.CENTER);
        
        pnText = new JPanel();
        tab.add("Text", pnText);
        pnText.setLayout(new BorderLayout());
        
        jsplitText = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        pnText.add(jsplitText, BorderLayout.CENTER);
        
        taBefText = new JTextArea();
        taAftText = new JTextArea();
        
        taAftText.setEditable(false);
        
        jsplitText.setTopComponent(new JScrollPane(taBefText));
        jsplitText.setBottomComponent(new JScrollPane(taAftText));
        
        pnGlobals = new JPanel();
        tab.add("Globals", pnGlobals);
        pnGlobals.setLayout(new BorderLayout());
        
        jsplitProp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        pnGlobals.add(jsplitProp, BorderLayout.CENTER);
        
        taBefProp = new JTextArea();
        taAftProp = new JTextArea();
        
        taAftProp.setEditable(false);
        
        jsplitProp.setTopComponent(new JScrollPane(taBefProp));
        jsplitProp.setBottomComponent(new JScrollPane(taAftProp));
        
        // 이벤트 부여 파트
        btnToolbarEncrypt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {   
                    @Override
                    public void run() {
                        onToolbarEncrypt();
                    }
                });
            }
        });
        
        btnToolbarDecrypt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {   
                    @Override
                    public void run() {
                        onToolbarDecrypt();
                    }
                });
            }
        });
    }
    
    public void open() {
        frame.setVisible(true);
        jsplitText.setDividerLocation(0.6);
        jsplitProp.setDividerLocation(0.6);
    }
    
    public void onToolbarEncrypt() {
        if(tab.getSelectedComponent() == pnText) {
            onEncryptText();
        }
        
        if(tab.getSelectedComponent() == pnGlobals) {
            onEncryptGlobals();
        }
    }
    
    public void onToolbarDecrypt() {
        if(tab.getSelectedComponent() == pnText) {
            onDecryptText();
        }
        
        if(tab.getSelectedComponent() == pnGlobals) {
            onDecryptGlobals();
        }
    }
    
    public void onEncryptText() {
        String strBef = taBefProp.getText().trim();
        
        try {
            cryptor.setAlgorithmInformation(new AlgorithmInformation(String.valueOf(cbAlg.getSelectedItem()), new String(tfPassword.getPassword())));
            taAftProp.setText(cryptor.encrypt(strBef));
        } catch(Exception ex) {
            ex.printStackTrace();
            alert("오류 : " + ex.getMessage());
        }
    }
    
    public void onDecryptText() {
        String strBef = taBefProp.getText().trim();
        
        try {
            cryptor.setAlgorithmInformation(new AlgorithmInformation(String.valueOf(cbAlg.getSelectedItem()), new String(tfPassword.getPassword())));
            taAftProp.setText(cryptor.decrypt(strBef));
        } catch(Exception ex) {
            ex.printStackTrace();
            alert("오류 : " + ex.getMessage());
        }
    }
    
    public void onEncryptGlobals() {
        String strBef = taBefProp.getText().trim();
        
        try {
            cryptor.setAlgorithmInformation(new AlgorithmInformation(String.valueOf(cbAlg.getSelectedItem()), new String(tfPassword.getPassword())));
            
            ByteArrayInputStream  binary  = new ByteArrayInputStream(strBef.getBytes(StandardCharsets.UTF_8));
            ByteArrayOutputStream outputs = new ByteArrayOutputStream();
            Properties prop = new Properties();
            prop.load(binary);
            binary.close(); binary = null;
            
            prop = cryptor.encryptProperties(prop);
            prop.store(outputs, "");
            
            prop = null;
            taAftProp.setText(new String(outputs.toByteArray(), "UTF-8"));
        } catch(Exception ex) {
            ex.printStackTrace();
            alert("오류 : " + ex.getMessage());
        }
    }
    
    public void onDecryptGlobals() {
        String strBef = taBefProp.getText().trim();
        
        try {
            cryptor.setAlgorithmInformation(new AlgorithmInformation(String.valueOf(cbAlg.getSelectedItem()), new String(tfPassword.getPassword())));
            
            ByteArrayInputStream  binary  = new ByteArrayInputStream(strBef.getBytes(StandardCharsets.UTF_8));
            ByteArrayOutputStream outputs = new ByteArrayOutputStream();
            Properties prop = new Properties();
            prop.load(binary);
            binary.close(); binary = null;
            
            prop = cryptor.decryptProperties(prop);
            prop.store(outputs, "");
            
            prop = null;
            taAftProp.setText(new String(outputs.toByteArray(), "UTF-8"));
        } catch(Exception ex) {
            ex.printStackTrace();
            alert("오류 : " + ex.getMessage());
        }
    }
    
    public void alert(String content) {
        System.out.println(content);
        JOptionPane.showMessageDialog(frame, content);
    }
}
