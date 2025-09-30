package org.duckdns.hjow.samples.colonyman.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.research.Research;

/** 연구 하나의 상태를 출력하는 컴포넌트 */
public class ResearchPanel extends JPanel {
    private static final long serialVersionUID = -4914214161092118509L;
    
    protected long researchKey = 0L;
    protected JProgressBar prog;
    protected JLabel lbName, lbLevel;
    protected JTextArea ta;
    
    public ResearchPanel(Research r) { 
        super();
        researchKey = r.getKey();
        
        setLayout(new BorderLayout());
        
        JPanel pnUp, pnCenter;
        pnUp     = new JPanel();
        pnCenter = new JPanel();
        pnUp.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        add(pnUp    , BorderLayout.NORTH);
        add(pnCenter, BorderLayout.CENTER);
        
        lbName  = new JLabel();
        lbLevel = new JLabel();
        
        JPanel pnLb = new JPanel();
        pnLb.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnLb.add(lbName);
        pnLb.add(lbLevel);
        pnUp.add(pnLb, BorderLayout.CENTER);
        
        prog = new JProgressBar();
        pnUp.add(prog, BorderLayout.EAST);
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnCenter.add(ta, BorderLayout.CENTER);
    }
    
    public Research getResearch(Colony col) {
        for(Research r : col.getResearches()) {
            if(r.getKey() == researchKey) return r;
        }
        return null;
    }
    
    public void refresh(int cycle, City city, Colony colony) {
        Research r = getResearch(colony);
        if(r != null) { if(r.getLevel() <= 0 && r.getProgress() <= 0) r = null; }
        if(r == null) {
            lbName.setText("");
            lbLevel.setText("");
            prog.setValue(0);
            ta.setText("");
            return;
        }
        
        lbName.setText(r.getTitle());
        if(r.getLevel() >= 1) lbLevel.setText("(Lv " + r.getLevel() + ")");
        else                  lbLevel.setText("(연구 중)");
        
        if(r.getMaxProgress() >= (Integer.MAX_VALUE / 10)) {
            prog.setMaximum((int) (r.getMaxProgress() / 10000));
            prog.setValue((int) (r.getProgress() / 10000));
        } else {
            prog.setMaximum((int) r.getMaxProgress());
            prog.setValue((int) r.getProgress());
        }
        
        ta.setText(r.getDescription());
    }
}
