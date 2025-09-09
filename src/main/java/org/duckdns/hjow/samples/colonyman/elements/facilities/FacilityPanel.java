package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.duckdns.hjow.samples.colonyman.ColonyMan;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.Facility;

public class FacilityPanel extends JPanel {
    private static final long serialVersionUID = -6078767714905474678L;
    
    protected JPanel pnUp, pnCenter, pnDown;
    protected JLabel lb;
    protected JTextArea ta;
    public FacilityPanel() {
        super();
        
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
    
    public void refresh(Facility fac, City city, Colony colony, ColonyMan superInstance) {
        lb.setText(fac.getName());
        
        StringBuilder res = new StringBuilder("");
        res = res.append("\n").append("Type : ").append(fac.getType());
        res = res.append("\n").append("HP : ").append(fac.getHp()).append(" / ").append(fac.getMaxHp());
        if(fac instanceof Home) {
            res = res.append(" (").append("Home").append(")");
            
            Home home = (Home) fac;
            res = res.append("\n").append("Capacity : ").append(home.getCitizens(city, colony)).append(" / ").append(home.getCapacity());
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
    }
}
