package org.duckdns.hjow.samples.cryptor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.duckdns.hjow.samples.cryptor.modules.CypherModule;
import org.duckdns.hjow.samples.cryptor.modules.ModuleLoader;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.samples.interfaces.ProcessingStream;
import org.duckdns.hjow.samples.util.LongCounter;
import org.duckdns.hjow.samples.util.UIUtil;

/** 파일을 암/복호화할 수 있는 도구 */
public class GFileCypher implements Disposeable {
    protected transient GCypher parent;
    protected transient JDialog dialog;
    protected transient JLabel lbBefore, lbAfter;
    protected transient JTextField tfBefore, tfAfter;
    protected transient JPasswordField pwField;
    protected transient JButton btnBeforeSel, btnAfterSel, btnAct;
    protected transient JFileChooser fileChooser;
    protected transient JTextArea ta;
    protected transient JComboBox<String> cbModule;
    protected transient JProgressBar progBar;
    
    public GFileCypher(GCypher parent) {
        this.parent = parent;
        
        dialog = new JDialog(parent.getDialog());
        dialog.setSize(500, 300);
        dialog.setTitle("GCypher File Converter");
        dialog.setLayout(new BorderLayout());
        UIUtil.center(dialog);
        
        JPanel pnMain = new JPanel();
        dialog.add(pnMain, BorderLayout.CENTER);
        
        pnMain.setLayout(new BorderLayout());
        
        JPanel pnCenter = new JPanel();
        pnMain.add(pnCenter, BorderLayout.CENTER);
        
        pnCenter.setLayout(new BorderLayout());
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
        
        JPanel pn1, pn2;
        pn1 = new JPanel();
        pn2 = new JPanel();
        
        pn1.setLayout(new BorderLayout());
        pn2.setLayout(new BorderLayout());
        
        pnCenter.add(pn1, BorderLayout.SOUTH);
        pn1.add(pn2, BorderLayout.SOUTH);
        
        JPanel pnf1, pnf2, pnf3;
        pnf1 = new JPanel();
        pnf2 = new JPanel();
        pnf3 = new JPanel();
        
        pnf1.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnf2.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnf3.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        pn1.add(pnf1, BorderLayout.CENTER);
        pn2.add(pnf2, BorderLayout.CENTER);
        pn2.add(pnf3, BorderLayout.SOUTH);
        
        lbBefore = new JLabel("Before..");
        lbAfter  = new JLabel("After...");
        
        pnf1.add(lbBefore);
        pnf2.add(lbAfter);
        
        tfBefore = new JTextField(30);
        tfAfter  = new JTextField(30);
        
        pnf1.add(tfBefore);
        pnf2.add(tfAfter);
        
        btnBeforeSel = new JButton("...");
        btnAfterSel = new JButton("...");
        
        pnf1.add(btnBeforeSel);
        pnf2.add(btnAfterSel);
        
        Vector<String> moduleNames = new Vector<String>();
        moduleNames.addAll(ModuleLoader.getNames());
        cbModule = new JComboBox<String>(moduleNames);
        pnf3.add(cbModule);
        
        pwField = new JPasswordField(10);
        pnf3.add(pwField);
        
        progBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        pnf3.add(progBar);
        
        btnAct = new JButton("Convert");
        pnf3.add(btnAct);
        
        fileChooser = new JFileChooser();
        
        btnBeforeSel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {    
                    @Override
                    public void run() {
                        int sel = fileChooser.showOpenDialog(getDialog());
                        if(sel == JFileChooser.APPROVE_OPTION) {
                            tfBefore.setText(fileChooser.getSelectedFile().getAbsolutePath());
                        }
                    }
                });
                
            }
        });
        
        btnAfterSel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {    
                    @Override
                    public void run() {
                        int sel = fileChooser.showSaveDialog(getDialog());
                        if(sel == JFileChooser.APPROVE_OPTION) {
                            tfAfter.setText(fileChooser.getSelectedFile().getAbsolutePath());
                        }
                    }
                });
                
            }
        });
        
        btnAct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {    
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                act();
                            }
                        }).start();
                    }
                });
            }
        });
        
        ta.setText("Warning !!\n" + "This tool is not tested enough !");
    }
    
    protected void act() {
        ta.setText("Started.");
        InputStream  inp  = null;
        OutputStream outp = null;
        LongCounter tsize = new LongCounter(0L);
        LongCounter being = new LongCounter(0L);
        try {
            File sources = new File(tfBefore.getText());
            if(! sources.exists()) throw new FileNotFoundException("There is no file : " + tfBefore.getText());
            
            File target = new File(tfAfter.getText());
            
            btnAct.setEnabled(false);
            
            char[] cpw = pwField.getPassword();
            String pw = new String(cpw);
            cpw = null;
            
            tsize.setValue(sources.length());
            progBar.setValue(0);
            progBar.setMaximum((int) (tsize.getValue() / 1024));
            
            CypherModule m = ModuleLoader.get(cbModule.getSelectedItem().toString());
            appendMsg("Module " + m.name() + " selected.");
            
            inp  = new FileInputStream(sources);
            outp = new FileOutputStream(target);
            
            m.convert(inp, outp, pw, new ProcessingStream() {    
                @Override
                public boolean processing(byte[] buffer, int sizes) {
                    being.add(sizes);
                    progBar.setValue((int) (being.getValue() / 1024));
                    return true;
                }
            });
            
            inp.close();  inp  = null;
            outp.close(); outp = null;
            appendMsg("Success");
        } catch(Exception ex) {
            appendMsg("[Error]\n" + ex.getMessage());
        } finally {
            if(inp  != null) { try { inp.close();  } catch(Exception ignores) {} }
            if(outp != null) { try { outp.close(); } catch(Exception ignores) {} }
            appendMsg("Complete.");
            
            btnAct.setEnabled(true);
        }
    }
    
    public void appendMsg(String msg) {
        ta.setText(ta.getText() + "\n" + msg);
        ta.setCaretPosition(ta.getDocument().getLength() - 1);
    }
    
    public void open() {
        dialog.setVisible(true);
    }
    
    public JDialog getDialog() {
        return dialog;
    }

    @Override
    public void dispose() {
        parent = null;
        if(dialog != null) {
            dialog.setVisible(false);
            dialog = null;
        }
    }
}
