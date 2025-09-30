package org.duckdns.hjow.samples.colonyman.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class CitizenPanel extends JPanel implements ColonyElementPanel {
    private static final long serialVersionUID = 8988684923024941632L;
    protected long citizenKey = 0L;
    protected String targetName;
    
    protected transient JPanel pnImage;
    protected transient JProgressBar progHp;
    protected transient JButton btnToggle;
    protected transient JTextField tfName;
    protected transient JTextArea  ta;
    
    protected transient boolean flagEditable = true;
    
    public CitizenPanel() {
        super();
    }
    
    public CitizenPanel(Citizen c, City city, Colony colony, ColonyManager superInstance) {
        this();
        init(c, city, colony, superInstance);
    }
    
    public void init(final Citizen c, final City city, final Colony colony, final ColonyManager superInstance) {
        dispose();
        setCitizenKey(c.getKey());
        setTargetName(c.getName());
        
        setLayout(new BorderLayout());
        
        JPanel pnNorth, pnCenter;
        pnNorth  = new JPanel();
        pnCenter = new JPanel();
        pnImage  = new JPanel();
        pnNorth.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnImage.setLayout(new BorderLayout());
        
        add(pnNorth , BorderLayout.NORTH);
        add(pnCenter, BorderLayout.CENTER);
        
        JPanel pnName, pnHp;
        
        pnName = new JPanel();
        pnName.setLayout(new BorderLayout());
        pnNorth.add(pnName, BorderLayout.CENTER);
        
        pnHp = new JPanel();
        pnHp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnNorth.add(pnHp, BorderLayout.EAST);
        
        tfName = new JTextField();
        pnName.add(tfName, BorderLayout.CENTER);
        tfName.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                Citizen c = getCitizen(city);
                if(c != null) {
                    c.setName(tfName.getText());
                    superInstance.refreshColonyContent();
                }
            }
        });
        
        progHp = new JProgressBar(JProgressBar.HORIZONTAL);
        pnHp.add(progHp);
        
        btnToggle = new JButton("▼");
        pnHp.add(btnToggle);
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
        pnCenter.add(pnImage, BorderLayout.WEST);
        
        pnCenter.setVisible(false);
        btnToggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(pnCenter.isVisible()) {
                    pnCenter.setVisible(false);
                    btnToggle.setText("▼");
                } else {
                    pnCenter.setVisible(true);
                    btnToggle.setText("▲");
                }
            }
        });
    }
    
    public Citizen getCitizen(City city) {
        if(getCitizenKey() == 0L) return null;
        for(Citizen c : city.getCitizens()) {
            if(c.getKey() == getCitizenKey()) return c;
        }
        return null;
    }

    @Override
    public void setEditable(boolean editable) {
        flagEditable = editable;
        tfName.setEditable(editable);
    }

    @Override
    public void refresh(int cycle, City city, Colony colony, ColonyManager superInstance) {
        Citizen c = getCitizen(city);
        if(c == null) { tfName.setName(""); ta.setText(""); return; }
        setTargetName(c.getName());
        
        progHp.setMaximum(c.getMaxHp());
        progHp.setValue(c.getHp());
        
        tfName.setText(c.getName());
        ta.setText(c.getStatusString(city, colony, superInstance));
        
        if(c.getHp() <= 0) setEditable(false);
        else setEditable(flagEditable);
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

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public String getTargetName() {
        return targetName;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
