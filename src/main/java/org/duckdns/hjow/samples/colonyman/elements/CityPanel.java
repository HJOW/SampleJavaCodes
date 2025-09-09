package org.duckdns.hjow.samples.colonyman.elements;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.duckdns.hjow.samples.colonyman.ColonyMan;
import org.duckdns.hjow.samples.colonyman.elements.facilities.FacilityPanel;
import org.duckdns.hjow.samples.colonyman.elements.facilities.SupportGUIFacility;

public class CityPanel extends JPanel {
    private static final long serialVersionUID = 3475480727850203183L;
    protected City city;
    protected JTextArea ta;
    protected JTextField tfName;
    protected JPanel pnGrid, pnFacilities;
    protected List<FacilityPanel> facilityPns = new Vector<FacilityPanel>();
    
    public CityPanel(City city, Colony colony, ColonyMan superInstance) {
        super();
        this.city = city;
        
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, getForeground(), Color.GRAY));
        
        JPanel pnTop, pnCenter, pnDown;
        pnTop    = new JPanel();
        pnCenter = new JPanel();
        pnDown   = new JPanel();
        
        pnTop.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new BorderLayout());
        
        this.add(pnTop   , BorderLayout.NORTH);
        this.add(pnCenter, BorderLayout.CENTER);
        this.add(pnDown  , BorderLayout.SOUTH);
        
        JPanel pnTopInfos = new JPanel();
        pnTopInfos.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnTop.add(pnTopInfos, BorderLayout.CENTER);
        
        tfName = new JTextField(12);
        tfName.setEditable(false);
        pnTopInfos.add(tfName);
        
        pnGrid = new JPanel();
        pnGrid.setLayout(new GridLayout(2, 1));
        pnCenter.add(pnGrid, BorderLayout.CENTER);
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnGrid.add(new JScrollPane(ta));
        
        pnFacilities = new JPanel();
        pnGrid.add(pnFacilities);
        
        refresh(0, colony, superInstance);
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
    
    public void refresh(int cycle, Colony colony, ColonyMan superInstance) {
        tfName.setText(city.getName());
        
        List<Facility> facList = city.getFacility();
        int idx;
        int sizes = facList.size();
        if(sizes != facilityPns.size()) {
            facilityPns.clear();
            pnFacilities.removeAll();
            pnFacilities.setLayout(new GridLayout(sizes, 1));
            
            for(idx=0; idx<sizes; idx++) {
                FacilityPanel pn;
                if(facList.get(idx) instanceof SupportGUIFacility) {
                    SupportGUIFacility sFac = (SupportGUIFacility) facList.get(idx);
                    pn = sFac.createPanel();
                } else {
                    pn = new FacilityPanel();
                }
                pnFacilities.add(pn);
                facilityPns.add(pn);
            }
        }
        
        for(idx=0; idx<sizes; idx++) {
            FacilityPanel pn = facilityPns.get(idx);
            Facility fac = facList.get(idx);
            
            if(fac instanceof SupportGUIFacility) {
                if(! ((SupportGUIFacility) fac).checkPanelAccept(pn) ) {
                    facilityPns.clear();
                    refresh(cycle, colony, superInstance);
                    return;
                }
            }
            
            pn.refresh(fac, city, colony, superInstance);
        }
    }
}
