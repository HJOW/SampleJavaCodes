package org.duckdns.hjow.samples.colonyman.elements;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.facilities.FacilityPanel;
import org.duckdns.hjow.samples.colonyman.elements.facilities.SupportGUIFacility;

public class CityPanel extends JPanel implements ColonyElementPanel {
    private static final long serialVersionUID = 3475480727850203183L;
    protected City city;
    protected JProgressBar progHp;
    protected JTextArea ta;
    protected JTextField tfName, tfSearchCitizen, tfSearchFacility;
    protected JPanel pnGrid, pnCitizens, pnFacilities;
    protected JSplitPane splits;
    protected List<FacilityPanel> facilityPns = new Vector<FacilityPanel>();
    protected List<CitizenPanel>  citizenPns  = new Vector<CitizenPanel>();
    
    public CityPanel() {
        
    }
    
    public CityPanel(City city, Colony colony, ColonyManager superInstance) {
        super();
        init(city, colony, superInstance);
    }
    
    public void init(City city, Colony colony, ColonyManager superInstance) {
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
        
        JPanel pnHp = new JPanel();
        pnHp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnTop.add(pnHp, BorderLayout.EAST);
        
        tfName = new JTextField(15);
        pnTopInfos.add(tfName);
        tfName.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                City c = getCity();
                if(c != null) {
                    c.setName(tfName.getText());
                    superInstance.refreshColonyContent();
                }
            }
        });
        
        progHp = new JProgressBar(JProgressBar.HORIZONTAL);
        pnHp.add(progHp);
        
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
        
        JPanel pnFacRoot, pnCitiRoot;
        pnFacRoot  = new JPanel();
        pnCitiRoot = new JPanel();
        
        pnFacRoot.setLayout(new BorderLayout());
        pnCitiRoot.setLayout(new BorderLayout());
        
        tfSearchCitizen  = new JTextField();
        tfSearchFacility = new JTextField();
        
        pnFacRoot.add(tfSearchFacility, BorderLayout.NORTH);
        pnCitiRoot.add(tfSearchCitizen, BorderLayout.NORTH);
        
        pnFacilities = new JPanel();
        pnCitizens   = new JPanel();
        
        pnFacRoot.add(new JScrollPane(pnFacilities), BorderLayout.CENTER);
        pnCitiRoot.add(new JScrollPane(pnCitizens), BorderLayout.CENTER);
        
        tab.add("시설", pnFacRoot);
        tab.add("시민", pnCitiRoot);
        
        KeyAdapter eventSearch = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                processSearch();
            }
        };
        ActionListener eventEnter = new ActionListener() {  
            @Override
            public void actionPerformed(ActionEvent e) {
                processSearch();
            }
        };
        
        tfSearchCitizen.addKeyListener(eventSearch);
        tfSearchFacility.addKeyListener(eventSearch);
        tfSearchCitizen.addActionListener(eventEnter);
        tfSearchFacility.addActionListener(eventEnter);
        
        refresh(0, city, colony, superInstance);
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
    
    @Override
    public void refresh(int cycle, City city, Colony colony, ColonyManager superInstance) {
        if(city == null) { city = getCity(); }
        if(city == null) {
            tfName.setText("");
            ta.setText("");
            pnFacilities.removeAll();
            pnCitizens.removeAll();
            return;
        }
        
        tfName.setText(city.getName());
        
        progHp.setMaximum(city.getMaxHp());
        progHp.setValue(city.getHp());
        
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
                    pn = sFac.createPanel(city, colony, superInstance);
                } else {
                    pn = new FacilityPanel(facList.get(idx), city, colony, superInstance);
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
        for(Citizen c : citizens) {
            if(c.getHp() <= 0) continue;
            CitizenPanel p = new CitizenPanel(c, city, colony, superInstance);
            citizenPns.add(p);
        }
        citizens = null;
        pnCitizens.setLayout(new GridLayout(citizenPns.size(), 1));
        for(CitizenPanel p : citizenPns) {
            pnCitizens.add(p);
            p.refresh(cycle, city, colony, superInstance);
        }
        
        ta.setText(city.getStatusString(superInstance));
        splits.setDividerLocation(0.3);
        
        if(city.getHp() <= 0) {
            setEditable(false);
        }
    }
    
    public void setEditable(boolean editable) {
        for(FacilityPanel p : facilityPns) {
            if(p.getFacility(city).getHp() <= 0) p.setEditable(false);
            else p.setEditable(editable); 
        }
        
        tfName.setEditable(editable);
        tfSearchCitizen.setEditable(editable);
        tfSearchFacility.setEditable(editable);
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
    
    protected void processSearch() {
        String keyword = tfSearchFacility.getText();
        for(FacilityPanel p : facilityPns) { processSearch(keyword, p); }
        keyword = tfSearchCitizen.getText();
        for(CitizenPanel  p : citizenPns ) { processSearch(keyword, p); }
    }
    
    protected void processSearch(String keyword, ColonyElementPanel component) {
        if(component.getComponent() == null) return;
        if(keyword == null || keyword.equals("")) {
            component.getComponent().setVisible(true);
            return;
        }
        
        String name = component.getTargetName();
        if(name == null) { component.getComponent().setVisible(false); return; }
        if(name.indexOf(keyword) >= 0) {
            component.getComponent().setVisible(true);
        } else {
            component.getComponent().setVisible(false);
        }
    }

    @Override
    public String getTargetName() {
        if(city == null) return null;
        return city.getName();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
