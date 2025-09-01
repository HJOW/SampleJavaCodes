package org.duckdns.hjow.samples.uicomponent;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class JLogArea extends JScrollPane {
    private static final long serialVersionUID = -8363517413706955543L;

    protected JTextArea ta = new JTextArea();
    protected List<String> list = new LinkedList<String>();
    protected StringBuilder buffer = new StringBuilder();
    protected int counts = 0;
    
    protected boolean firsts = false;
    
    public JLogArea() {
        super();
        setViewportView(ta);
        ta.setEditable(false);
    }
    
    public synchronized void refreshFull() {
        buffer.setLength(0);
        
        firsts = true;
        for(String str : list) {
            if(firsts) { buffer = buffer.append("\n"); firsts = false; }
            buffer = buffer.append(str);
        }
        ta.setText(buffer.toString());
        buffer.setLength(0);
        
        ta.setCaretPosition(ta.getDocument().getLength() - 1);
    }
    
    public void setText(String msg) {
        list.clear();
        list.add(msg);
        counts = 1;
        refreshFull();
    }
    
    public String getText() {
        return ta.getText();
    }
    
    public void setLineWrap(boolean wrap) {
        ta.setLineWrap(wrap);
    }
    
    public synchronized void log(String msg) {
        list.add(msg);
        counts++;
        
        if(counts == 0) ta.append(msg);
        else ta.append("\n" + msg);
        
        ta.setCaretPosition(ta.getDocument().getLength() - 1);
    }
}
