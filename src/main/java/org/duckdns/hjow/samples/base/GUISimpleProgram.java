package org.duckdns.hjow.samples.base;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public abstract class GUISimpleProgram extends DefaultProgram {
    private static final long serialVersionUID = -2130731750368759720L;
    protected JDialog dialog;
    protected JTextArea taLog;
    protected JPanel pnUp, pnCenter, pnDown, pnContent;
    protected JSplitPane splits;
    protected JMenuBar menuBar;
    
    public GUISimpleProgram(SampleJavaCodes superInstance) {
        super(superInstance);
    }
    
    @Override
    public void init(SampleJavaCodes superInstance) {
        Window superDialog = superInstance.getWindow();
        if(     superDialog instanceof Frame ) dialog = new JDialog((Frame)  superDialog);
        else if(superDialog instanceof Dialog) dialog = new JDialog((Dialog) superDialog);
        else dialog = new JDialog();
        
        dialog.setSize(600, 400);
        dialog.setTitle(getTitle() == null ? getName() : getTitle());
        dialog.setLayout(new BorderLayout());
        
        JPanel pnMain;
        
        pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        dialog.add(pnMain, BorderLayout.CENTER);
        
        pnUp = new JPanel();
        pnCenter = new JPanel();
        pnDown = new JPanel();
        
        pnUp.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new BorderLayout());
        
        pnMain.add(pnUp, BorderLayout.NORTH);
        pnMain.add(pnCenter, BorderLayout.CENTER);
        pnMain.add(pnDown, BorderLayout.SOUTH);
        
        splits = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        pnCenter.add(splits, BorderLayout.CENTER);
        
        taLog = new JTextArea();
        taLog.setEditable(false);
        taLog.setLineWrap(true);
        
        pnContent = new JPanel();
        pnContent.setLayout(new BorderLayout());
        
        splits.setTopComponent(pnContent);
        splits.setBottomComponent(new JScrollPane(taLog));
        
        menuBar = new JMenuBar();
        dialog.setJMenuBar(menuBar);
    }

    @Override
    public JDialog getDialog() {
        return dialog;
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
    public void alert(String msg) { 
        System.out.println(msg); 
        JOptionPane.showMessageDialog(dialog, msg);
    }
    
    @Override
    public void dispose() {
        dialog.setVisible(false);
        dialog = null;
    }
}
