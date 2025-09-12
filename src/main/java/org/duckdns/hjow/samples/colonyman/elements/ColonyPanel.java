package org.duckdns.hjow.samples.colonyman.elements;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.duckdns.hjow.samples.colonyman.ColonyMan;

public class ColonyPanel extends JPanel implements ColonyElementPanel {
    private static final long serialVersionUID = 3851432705333464777L;
    protected Colony colony;
    
    protected transient List<CityPanel> pnCities = new Vector<CityPanel>();
    protected transient JPanel pnColonies, pnColonyBasics;
    protected transient JTabbedPane tabCities;
    protected transient JProgressBar progHp;
    protected transient JLabel lbColonyName;
    protected transient JTextField tfColonyTime;
    
    public ColonyPanel() {
        super();
    }
    
    public ColonyPanel(Colony colony, ColonyMan superInstance) {
        this();
        init(colony, superInstance);
    }
    
    public void init(Colony colony, ColonyMan superInstance) {
        if(colony != null) dispose();
        this.colony = colony;
        
        setLayout(new BorderLayout());
        
        // tabCities = new JTabbedPane();
        // add(tabCities, BorderLayout.CENTER);
        pnColonies = new JPanel();
        add(pnColonies, BorderLayout.CENTER);
        
        JPanel pnColTop, pnColBottom;
        pnColTop = new JPanel();
        pnColBottom = new JPanel();
        pnColTop.setLayout(new BorderLayout());
        pnColBottom.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        add(pnColTop   , BorderLayout.NORTH);
        add(pnColBottom, BorderLayout.SOUTH);
        
        pnColonyBasics = new JPanel();
        pnColonyBasics.setLayout(new BorderLayout());
        
        JPanel pnTopLeft, pnTopCenter, pnTopRight;
        pnTopLeft   = new JPanel();
        pnTopCenter = new JPanel();
        pnTopRight  = new JPanel();
        pnTopLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnTopCenter.setLayout(new BorderLayout());
        pnTopRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        pnColonyBasics.add(pnTopLeft  , BorderLayout.WEST);
        pnColonyBasics.add(pnTopCenter, BorderLayout.CENTER);
        pnColonyBasics.add(pnTopRight , BorderLayout.EAST);
        pnColTop.add(pnColonyBasics, BorderLayout.CENTER);
        
        lbColonyName = new JLabel();
        pnTopLeft.add(lbColonyName);
        
        tfColonyTime = new JTextField(14);
        tfColonyTime.setEditable(false);
        pnTopRight.add(tfColonyTime);
        
        progHp = new JProgressBar(JProgressBar.HORIZONTAL);
        pnTopRight.add(progHp);
    }
    
    @Override
    public void dispose() {
        colony = null;
        for(CityPanel c : pnCities) {
            c.dispose();
        }
        removeAll();
    }

    @Override
    public void setEditable(boolean editable) {
        for(CityPanel c : pnCities) {
            if(c.getCity().getHp() <= 0) c.setEditable(false);
            else c.setEditable(editable);
        }
    }

    @Override
    public void refresh(int cycle, City city, Colony colony, ColonyMan superInstance) { // city is null
        progHp.setMaximum(colony.getMaxHp());
        progHp.setValue(colony.getHp());
        
        if(cycle == 0 || cycle % 100 == 0) {
            pnColonies.removeAll();
            for(CityPanel c : pnCities) { c.dispose(); }
            pnCities.clear();
            
            List<City> cities = colony.getCities();
            
            JPanel[] pns = new JPanel[cities.size() + 1];
            pnColonies.setLayout(new GridLayout(pns.length, 1));
            
            for(int idx=0; idx<cities.size(); idx++) {
                CityPanel c = new CityPanel(cities.get(idx), colony, superInstance);
                pns[idx] = c;
                pnCities.add(c);
                pnColonies.add(c);
            }
            
            JPanel pnEmpty = new JPanel();
            pns[pns.length - 1] = pnEmpty;
            pnColonies.add(pnEmpty);
        }
        
        lbColonyName.setText(colony.getName());
        tfColonyTime.setText(colony.getDateString());
        
        for(CityPanel c : pnCities) {
            c.refresh(cycle, c.getCity(), colony, superInstance);
        }
    }
    
    public CityPanel getCityPanel(City city) {
        for(CityPanel c : pnCities) {
            if(c.getCity().getKey() == city.getKey()) {
                return c;
            }
        }
        return null;
    }

    public Colony getColony() {
        return colony;
    }

    public void setColony(Colony colony) {
        this.colony = colony;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getTargetName() {
        return colony.getName();
    }
}
