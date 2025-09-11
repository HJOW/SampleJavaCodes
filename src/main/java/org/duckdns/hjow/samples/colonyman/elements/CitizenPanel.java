package org.duckdns.hjow.samples.colonyman.elements;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.duckdns.hjow.samples.colonyman.ColonyMan;

public class CitizenPanel extends JPanel implements ColonyElementPanel {
    private static final long serialVersionUID = 8988684923024941632L;
    protected long citizenKey = 0L;
    
    protected transient JTextField tfName;
    protected transient JTextArea  ta;
    
    public CitizenPanel() {
        super();
    }
    
    public CitizenPanel(Citizen c) {
        this();
        init(c);
    }
    
    public void init(Citizen c) {
        dispose();
        setCitizenKey(c.getKey());
        
        setLayout(new BorderLayout());
        
        JPanel pnNorth, pnCenter;
        pnNorth  = new JPanel();
        pnCenter = new JPanel();
        add(pnNorth , BorderLayout.NORTH);
        add(pnCenter, BorderLayout.CENTER);
        
        pnNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnCenter.setLayout(new BorderLayout());
        
        tfName = new JTextField(20);
        tfName.setEditable(false);
        pnNorth.add(tfName);
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
    }
    
    public Citizen getCitizen(City city) {
        if(getCitizenKey() < 0L) return null;
        for(Citizen c : city.getCitizens()) {
            if(c.getKey() == getCitizenKey()) return c;
        }
        return null;
    }

    @Override
    public void setEditable(boolean editable) {
        
    }

    @Override
    public void refresh(int cycle, City city, Colony colony, ColonyMan superInstance) {
        Citizen c = getCitizen(city);
        if(c == null) { dispose(); return; }
        
        tfName.setText(c.getName());
        ta.setText(getCitizenStatusText().trim());
    }
    
    public String getCitizenStatusText() {
        
        return "";
    }

    @Override
    public void dispose() {
        citizenKey = 0L;
        removeAll();
    }

    public long getCitizenKey() {
        return citizenKey;
    }

    public void setCitizenKey(long citizenKey) {
        this.citizenKey = citizenKey;
    }
}
