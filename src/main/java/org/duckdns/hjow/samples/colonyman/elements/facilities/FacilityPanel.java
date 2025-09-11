package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.duckdns.hjow.samples.colonyman.ColonyMan;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElementPanel;
import org.duckdns.hjow.samples.colonyman.elements.Facility;

public class FacilityPanel extends JPanel implements ColonyElementPanel {
    private static final long serialVersionUID = -6078767714905474678L;
    
    protected transient JPanel pnUp, pnCenter, pnDown;
    protected transient JLabel lb;
    protected transient JTextArea ta;
    
    protected long facilityKey = 0L;
    protected String targetName;
    
    public FacilityPanel() {
        super();
    }
    
    public FacilityPanel(Facility f) {
        this();
        init(f);   
    }
    
    public void init(Facility f) {
        dispose();
        setFacilityKey(f.getKey());
        setTargetName(f.getName());
        
        removeAll();
        setLayout(new BorderLayout());
        
        pnUp     = new JPanel();
        pnCenter = new JPanel();
        pnDown   = new JPanel();
        
        pnUp.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new BorderLayout());
        
        add(pnUp    , BorderLayout.NORTH);
        add(pnCenter, BorderLayout.CENTER);
        add(pnDown  , BorderLayout.SOUTH);
        
        JPanel pnLb = new JPanel();
        pnLb.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnUp.add(pnLb, BorderLayout.CENTER);
        
        lb = new JLabel();
        pnLb.add(lb);
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
    }
    
    public long getFacilityKey() {
        return facilityKey;
    }

    public void setFacilityKey(long facilityKey) {
        this.facilityKey = facilityKey;
    }
    
    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Facility getFacility(City city) {
        if(getFacilityKey() == 0L) return null;
        for(Facility f : city.getFacility()) {
            if(f.getKey() == getFacilityKey()) return f;
        }
        return null;
    }

    public void refresh(Facility fac, City city, Colony colony, ColonyMan superInstance) {
        lb.setText(fac.getName());
        setTargetName(fac.getName());
        
        StringBuilder res = new StringBuilder("");
        res = res.append("\n").append("Type : ").append(fac.getType());
        res = res.append("\n").append("HP : ").append(fac.getHp()).append(" / ").append(fac.getMaxHp());
        if(fac instanceof Home) {
            res = res.append(" (").append("Home").append(")");
            
            Home home = (Home) fac;
            res = res.append("\n").append("Capacity : ").append(home.getCitizens(city, colony).size()).append(" / ").append(home.getCapacity());
            res = res.append("\n").append("Comport : ").append(fac.getComportGrade());
            res = res.append("\n").append("Living...");
            for(Citizen c : home.getCitizens(city, colony)) {
                res = res.append("\n    ").append(c.getName());
            }
        }
        
        res = res.append("\n").append("Working...");
        
        for(Citizen c : fac.getWorkingCitizens(city, colony)) {
            res = res.append("\n    ").append(c.getName());
        }
        
        ta.setText(res.toString().trim());
        
        if(fac.getHp() <= 0) setEditable(false);
    }
    
    @Override
    public void refresh(int cycle, City city, Colony colony, ColonyMan superInstance) {
        Facility fac = getFacility(city);
        if(fac == null) { setEditable(false); return; }
        refresh(fac, city, colony, superInstance);
    }

    @Override
    public void setEditable(boolean editable) {
        
    }

    @Override
    public void dispose() {
        facilityKey = 0L;
        removeAll();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
