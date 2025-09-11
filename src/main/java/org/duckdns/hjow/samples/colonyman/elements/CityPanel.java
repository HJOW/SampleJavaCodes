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
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.duckdns.hjow.samples.colonyman.ColonyMan;
import org.duckdns.hjow.samples.colonyman.elements.facilities.FacilityPanel;
import org.duckdns.hjow.samples.colonyman.elements.facilities.SupportGUIFacility;

public class CityPanel extends JPanel implements ColonyElementPanel {
    private static final long serialVersionUID = 3475480727850203183L;
    protected City city;
    protected JTextArea ta;
    protected JTextField tfName;
    protected JPanel pnGrid, pnCitizens, pnFacilities;
    protected JSplitPane splits;
    protected List<FacilityPanel> facilityPns = new Vector<FacilityPanel>();
    protected List<CitizenPanel>  citizenPns  = new Vector<CitizenPanel>();
    
    public CityPanel() {
        
    }
    
    public CityPanel(City city, Colony colony, ColonyMan superInstance) {
        super();
        init(city, colony, superInstance);
    }
    
    public void init(City city, Colony colony, ColonyMan superInstance) {
        if(this.city != null) dispose();
        
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
        
        splits = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        pnCenter.add(splits, BorderLayout.CENTER);
        
        pnGrid = new JPanel();
        pnGrid.setLayout(new BorderLayout());
        splits.setRightComponent(pnGrid);
        
        ta = new JTextArea();
        ta.setEditable(false);
        splits.setLeftComponent(new JScrollPane(ta));
        
        JTabbedPane tab = new JTabbedPane();
        pnGrid.add(tab, BorderLayout.CENTER);
        
        pnFacilities = new JPanel();
        pnCitizens   = new JPanel();
        tab.add("시설", new JScrollPane(pnFacilities));
        tab.add("시민", new JScrollPane(pnCitizens));
        
        refresh(0, city, colony, superInstance);
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
    
    @Override
    public void refresh(int cycle, City city, Colony colony, ColonyMan superInstance) {
        tfName.setText(city.getName());
        
        List<Facility> facList = city.getFacility();
        int idx = 0;
        int sizes = facList.size();
        
        // Remove destroyed facilities panel
        for(Facility f : facList) {
            if(f.getHp() <= 0) {
                idx = 0;
                while(idx < facilityPns.size()) {
                    if(f.getKey() == facilityPns.get(idx).getFacilityKey()) { facilityPns.get(idx).dispose(); facilityPns.remove(idx); continue; }
                    idx++;
                }
            }
        }
        
        idx = 0;
        if(sizes != facilityPns.size()) {
            for(FacilityPanel p : facilityPns) { p.dispose(); }
            facilityPns.clear();
            pnFacilities.removeAll();
            pnFacilities.setLayout(new GridLayout(sizes, 1));
            
            for(idx=0; idx<sizes; idx++) {
                FacilityPanel pn;
                if(facList.get(idx) instanceof SupportGUIFacility) {
                    SupportGUIFacility sFac = (SupportGUIFacility) facList.get(idx);
                    pn = sFac.createPanel();
                } else {
                    pn = new FacilityPanel(facList.get(idx));
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
                    refresh(cycle, city, colony, superInstance);
                    return;
                }
            }
            
            pn.refresh(fac, city, colony, superInstance);
        }
        
        facList = null;
        pnCitizens.removeAll();
        for(CitizenPanel p : citizenPns) { p.dispose(); }
        citizenPns.clear();
        
        List<Citizen> citizens = city.getCitizens();
        pnCitizens.setLayout(new GridLayout(citizens.size(), 1));
        for(Citizen c : citizens) {
            CitizenPanel p = new CitizenPanel(c);
            citizenPns.add(p);
            pnCitizens.add(p);
            p.refresh(cycle, city, colony, superInstance);
        }
        
        ta.setText(city.getStatusString(superInstance));
        splits.setDividerLocation(0.5);
        
        if(city.getHp() <= 0) {
            setEditable(false);
        }
    }
    
    public void setEditable(boolean editable) {
        for(FacilityPanel p : facilityPns) {
            if(p.getFacility(city).getHp() <= 0) p.setEditable(false);
            else p.setEditable(editable); 
        }
    }
    
    @Override
    public void dispose() {
        city = null;
        for(FacilityPanel p : facilityPns) { p.dispose(); }
        facilityPns.clear();
        for(CitizenPanel p : citizenPns) { p.dispose(); }
        citizenPns.clear();
        
        removeAll();
    }
}
