package org.duckdns.hjow.samples.colonyman.elements;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.duckdns.hjow.samples.colonyman.ColonyManager;

public class ColonyPanel extends JPanel implements ColonyElementPanel {
    private static final long serialVersionUID = 3851432705333464777L;
    protected Colony colony;
    
    protected transient List<CityPanel> pnCities = new Vector<CityPanel>();
    protected transient JPanel pnColonyBasics;
    protected transient JTabbedPane tabCities;
    protected transient JProgressBar progHp;
    protected transient JTextField tfColonyName, tfColonyTime;
    protected transient JTextArea ta;
    protected transient JToolBar toolbar;
    
    public ColonyPanel() {
        super();
    }
    
    public ColonyPanel(Colony colony, ColonyManager superInstance) {
        this();
        init(colony, superInstance);
    }
    
    public void init(Colony colony, ColonyManager superInstance) {
        if(colony != null) dispose();
        this.colony = colony;
        
        setLayout(new BorderLayout());
        
        tabCities = new JTabbedPane();
        add(tabCities, BorderLayout.CENTER);
        
        JPanel pnColTop, pnColBottom;
        pnColTop = new JPanel();
        pnColBottom = new JPanel();
        pnColTop.setLayout(new BorderLayout());
        pnColBottom.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        add(pnColTop   , BorderLayout.NORTH);
        add(pnColBottom, BorderLayout.SOUTH);
        
        pnColonyBasics = new JPanel();
        pnColonyBasics.setLayout(new BorderLayout());
        
        JPanel pnTopLeft, pnTopCenter, pnTopRight, pnTopSouth;
        pnTopLeft   = new JPanel();
        pnTopCenter = new JPanel();
        pnTopRight  = new JPanel();
        pnTopSouth  = new JPanel();
        pnTopLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnTopCenter.setLayout(new BorderLayout());
        pnTopRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnTopSouth.setLayout(new BorderLayout());
        
        pnColonyBasics.add(pnTopLeft  , BorderLayout.WEST);
        pnColonyBasics.add(pnTopCenter, BorderLayout.CENTER);
        pnColonyBasics.add(pnTopRight , BorderLayout.EAST);
        pnColonyBasics.add(pnTopSouth , BorderLayout.SOUTH);
        pnColTop.add(pnColonyBasics, BorderLayout.CENTER);
        
        tfColonyName = new JTextField(15);
        pnTopLeft.add(tfColonyName);
        tfColonyName.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                Colony c = getColony();
                if(c != null) {
                    c.setName(tfColonyName.getText());
                    superInstance.refreshColonyList();
                }
            }
        });
        
        tfColonyTime = new JTextField(14);
        tfColonyTime.setEditable(false);
        pnTopRight.add(tfColonyTime);
        
        progHp = new JProgressBar(JProgressBar.HORIZONTAL);
        pnTopRight.add(progHp);
        
        JPanel pnTopDetail = new JPanel();
        pnTopDetail.setLayout(new BorderLayout());
        pnTopSouth.add(pnTopDetail, BorderLayout.CENTER);
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnTopDetail.add(ta, BorderLayout.CENTER);
        
        toolbar = new JToolBar();
        pnTopDetail.add(toolbar, BorderLayout.SOUTH);
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
        tfColonyName.setEditable(editable);
        for(CityPanel c : pnCities) {
            if(c.getCity().getHp() <= 0) c.setEditable(false);
            else c.setEditable(editable);
        }
    }

    @Override
    public void refresh(int cycle, City city, Colony colony, ColonyManager superInstance) { // city is null
        if(colony == null) {
            tfColonyName.setText("");
            tfColonyTime.setText("");
            ta.setText("");
            return;
        }
        
        progHp.setMaximum(colony.getMaxHp());
        progHp.setValue(colony.getHp());
        
        List<City> cities = colony.getCities();
        if(cycle == 0 || cycle % 100 == 0 || tabCities.getTabCount() != cities.size()) {
            tabCities.removeAll();
            for(CityPanel c : pnCities) { c.dispose(); }
            pnCities.clear();
            
            for(int idx=0; idx<cities.size(); idx++) {
                CityPanel c = new CityPanel(cities.get(idx), colony, superInstance);
                pnCities.add(c);
                tabCities.add(cities.get(idx).getName(), c);
            }
        } else {
            for(int idx=0; idx<pnCities.size(); idx++) {
                City cityCurrent = pnCities.get(idx).getCity();
                tabCities.setTitleAt(idx, cityCurrent == null ? "" : cityCurrent.getName());
            }
        }
        
        tfColonyName.setText(colony.getName());
        tfColonyTime.setText(colony.getDateString());
        ta.setText(colony.getStatusString(superInstance));
        
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
