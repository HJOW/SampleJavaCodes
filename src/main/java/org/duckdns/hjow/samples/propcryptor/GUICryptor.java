package org.duckdns.hjow.samples.propcryptor;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.duckdns.hjow.samples.base.GUIProgram;
import org.duckdns.hjow.samples.base.SampleJavaCodes;

@Deprecated
public class GUICryptor implements GUIProgram {
    private static final long serialVersionUID = 9127870350945283621L;

    protected SimpleCryptor cryptor;
    
    protected JDialog           dialog;
    protected JPanel            pnText, pnGlobals;
    protected JToolBar          toolbar;
    protected JTabbedPane       tab;
    
    protected JSplitPane        jsplitText, jsplitProp;
    protected JTextArea         taBefText, taAftText, taBefProp, taAftProp;
    
    protected JPasswordField    tfPassword;
    protected JComboBox<String> cbAlg;
    
    protected JButton           btnToolbarEncrypt, btnToolbarDecrypt;
    
    public GUICryptor(SampleJavaCodes superInstance) {
        super();
        init(superInstance);
    }
    
    @Override
    public void open(SampleJavaCodes superInstance) {
        if(dialog == null) init(superInstance);
        
        onBeforeOpened(superInstance);
        dialog.setVisible(true);
        onAfterOpened(superInstance);
        
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
        JOptionPane.showMessageDialog(dialog, content);
    }

    @Override
    public void init(SampleJavaCodes superInstance) {
        try {
            cryptor = new SimpleCryptor();
        } catch (Throwable t) {
            new RuntimeException(t.getMessage(), t);
        }
        
        Window superDialog = superInstance.getWindow(); 
        if(     superDialog instanceof Frame ) dialog = new JDialog((Frame) superDialog);
        else if(superDialog instanceof Dialog) dialog = new JDialog((Dialog)superDialog);
        else dialog = new JDialog();
        
        dialog.setSize(790, 590);
        dialog.setLayout(new BorderLayout());
        dialog.setTitle("암호화 도구");
        
        JPanel pnMain;
        
        pnMain = new JPanel();
        dialog.add(pnMain, BorderLayout.CENTER);
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

    @Override
    public void onBeforeOpened(SampleJavaCodes superInstance) {
        
    }

    @Override
    public void onAfterOpened(SampleJavaCodes superInstance) {
        
    }

    @Override
    public String getTitle() {
        return "Cryptor";
    }

    @Override
    public void log(String msg) {
        System.out.println(msg);
    }

    @Override
    public String getName() {
        return "Cryptor";
    }

    @Override
    public JDialog getDialog() {
        return dialog;
    }

    @Override
    public void dispose() {
        dialog.setVisible(false);
        dialog = null;
    }
    
    @Override
    public Icon getIcon() {
        return null;
    }
    
    @Override
    public boolean isHidden() {return true;}
}
