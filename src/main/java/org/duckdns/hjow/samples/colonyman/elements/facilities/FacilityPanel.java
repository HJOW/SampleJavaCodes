package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.duckdns.hjow.samples.colonyman.ColonyMan;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElementPanel;
import org.duckdns.hjow.samples.colonyman.elements.Facility;

public class FacilityPanel extends JPanel implements ColonyElementPanel {
    private static final long serialVersionUID = -6078767714905474678L;
    
    protected transient JProgressBar progHp;
    protected transient JPanel pnUp, pnCenter, pnDown;
    protected transient JTextField tfName;
    protected transient JTextArea ta;
    
    protected long facilityKey = 0L;
    protected String targetName;
    
    public FacilityPanel() {
        super();
    }
    
    public FacilityPanel(Facility f, City city, Colony colony, ColonyMan superInstance) {
        this();
        init(f, city, colony, superInstance);   
    }
    
    public void init(Facility f, final City city, final Colony colony, final ColonyMan superInstance) {
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
        
        JPanel pnName = new JPanel();
        pnName.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnUp.add(pnName, BorderLayout.CENTER);
        
        tfName = new JTextField(15);
        pnName.add(tfName);
        tfName.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                Facility f = getFacility(city);
                if(f != null) {
                    f.setName(tfName.getText());
                    superInstance.refreshColonyContent();
                }
            }
        });
        
        JPanel pnHp = new JPanel();
        pnHp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnUp.add(pnHp, BorderLayout.EAST);
        
        progHp = new JProgressBar(JProgressBar.HORIZONTAL);
        pnHp.add(progHp);
        
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
        if(fac == null) {
            tfName.setText("");
            ta.setText("");
            return;
        }
        
        progHp.setMaximum(fac.getMaxHp());
        progHp.setValue(fac.getHp());
        
        tfName.setText(fac.getName());
        setTargetName(fac.getName());
        
        StringBuilder res = new StringBuilder("");
        res = res.append("\n").append("Type : ").append(fac.getType());
        if(fac instanceof Home) res = res.append(" (").append("Home").append(")");
        
        res = res.append("\n").append("HP : ").append(fac.getHp()).append(" / ").append(fac.getMaxHp());
        
        String desc = fac.getStatusDescription(city, colony);
        if(desc != null) res = res.append("\n").append(desc);
        
        if(fac instanceof Home) {
            Home home = (Home) fac;
            res = res.append("\n").append("거주인원 : ").append(home.getCitizens(city, colony).size()).append(" / ").append(home.getCapacity());
            res = res.append("\n").append("편안함 : ").append(fac.getComportGrade());
            res = res.append("\n").append("거주자...");
            for(Citizen c : home.getCitizens(city, colony)) {
                res = res.append("\n    ").append(c.getName());
            }
        }
        
        List<Citizen> workers = fac.getWorkingCitizens(city, colony);
        if(! workers.isEmpty()) {
            res = res.append("\n").append("재직자...");
            
            for(Citizen c : workers) {
                res = res.append("\n    ").append(c.getName());
            }
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
        tfName.setEditable(editable);
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
