package org.duckdns.hjow.samples.cryptor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.duckdns.hjow.samples.cryptor.modules.CypherModule;
import org.duckdns.hjow.samples.cryptor.modules.Digest;
import org.duckdns.hjow.samples.interfaces.Disposeable;
import org.duckdns.hjow.samples.interfaces.ProcessingStream;
import org.duckdns.hjow.samples.util.LongCounter;
import org.duckdns.hjow.samples.util.UIUtil;

/** 파일 해시값 확인 도구 */
public class GFileHash implements Disposeable {
    protected transient GCypher parent;
    protected transient JDialog dialog;
    protected transient JTextField tfBefore;
    protected transient JButton btnBeforeSel, btnAct;
    protected transient JFileChooser fileChooser;
    protected transient JTextArea ta;
    protected transient JComboBox<String> cbHash;
    protected transient JProgressBar progBar;
    
    public GFileHash(GCypher parent) {
        this.parent = parent;
        
        dialog = new JDialog(parent.getDialog());
        dialog.setSize(500, 300);
        dialog.setTitle("GCypher File Converter");
        dialog.setLayout(new BorderLayout());
        UIUtil.center(dialog);
        
        JPanel pnMain = new JPanel();
        dialog.add(pnMain, BorderLayout.CENTER);
        
        pnMain.setLayout(new BorderLayout());
        
        JPanel pnCenter, pnDown;
        pnCenter = new JPanel();
        pnDown = new JPanel();
        
        pnMain.add(pnCenter, BorderLayout.CENTER);
        pnMain.add(pnDown, BorderLayout.SOUTH);
        
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
        
        progBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        pnCenter.add(progBar, BorderLayout.SOUTH);
        
        tfBefore = new JTextField(20);
        pnDown.add(tfBefore);
        
        btnBeforeSel = new JButton("...");
        pnDown.add(btnBeforeSel);
        
        Vector<String> algorithms = new Vector<String>();
        algorithms.add("SHA-256");
        algorithms.add("SHA-384");
        algorithms.add("SHA-512");
        algorithms.add("SHA-1");
        algorithms.add("MD5");
        cbHash = new JComboBox<String>(algorithms);
        cbHash.setEditable(true);
        cbHash.setSelectedIndex(0);
        pnDown.add(cbHash);
        
        btnAct = new JButton("Hash");
        pnDown.add(btnAct);
        
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
    }
    
    protected void act() {
        InputStream  inp  = null;
        ByteArrayOutputStream outp = new ByteArrayOutputStream();
        LongCounter tsize = new LongCounter(0L);
        LongCounter being = new LongCounter(0L);
        try {
            File sources = new File(tfBefore.getText());
            if(! sources.exists()) throw new FileNotFoundException("There is no file : " + tfBefore.getText());
            
            btnAct.setEnabled(false);
            
            tsize.setValue(sources.length());
            progBar.setValue(0);
            progBar.setMaximum((int) (tsize.getValue() / 1024));
            
            inp  = new FileInputStream(sources);
            
            CypherModule m = new Digest();
            
            m.convert(inp, outp, cbHash.getSelectedItem().toString(), new ProcessingStream() {    
                @Override
                public boolean processing(byte[] buffer, int sizes) {
                    being.add(sizes);
                    progBar.setValue((int) (being.getValue() / 1024));
                    return true;
                }
            });
            
            inp.close();  inp  = null;
            outp.close();
            ta.setText(Base64.getEncoder().encodeToString(outp.toByteArray()));
            outp = null;
        } catch(Exception ex) {
            ta.setText("[Error]\n" + ex.getMessage());
        } finally {
            if(inp  != null) { try { inp.close();  } catch(Exception ignores) {} }
            if(outp != null) { try { outp.close(); } catch(Exception ignores) {} }
            
            btnAct.setEnabled(true);
        }
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
