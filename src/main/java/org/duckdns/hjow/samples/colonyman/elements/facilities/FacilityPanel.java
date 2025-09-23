package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElementPanel;
import org.duckdns.hjow.samples.colonyman.elements.Facility;
import org.duckdns.hjow.samples.colonyman.elements.research.Research;
import org.duckdns.hjow.samples.colonyman.elements.states.State;

public class FacilityPanel extends JPanel implements ColonyElementPanel {
    private static final long serialVersionUID = -6078767714905474678L;
    
    protected transient JProgressBar progHp;
    protected transient JPanel pnUp, pnCenter, pnDown;
    protected transient JButton btnToggle, btnDestroy;
    protected transient JTextField tfName;
    protected transient JTextArea ta;
    protected transient JComboBox<Research> cbxResearch;
    
    protected long facilityKey = 0L;
    protected String targetName;
    
    public FacilityPanel() {
        super();
    }
    
    public FacilityPanel(Facility f, City city, Colony colony, ColonyManager superInstance) {
        this();
        init(f, city, colony, superInstance);   
    }
    
    public void init(final Facility f, final City city, final Colony colony, final ColonyManager superInstance) {
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
        pnName.setLayout(new BorderLayout());
        pnUp.add(pnName, BorderLayout.CENTER);
        
        tfName = new JTextField();
        pnName.add(tfName, BorderLayout.CENTER);
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
        
        JPanel pnCtrls = new JPanel();
        pnCtrls.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnUp.add(pnCtrls, BorderLayout.EAST);
        
        progHp = new JProgressBar(JProgressBar.HORIZONTAL);
        pnCtrls.add(progHp);
        
        btnDestroy = new JButton("철거");
        pnCtrls.add(btnDestroy);
        
        btnToggle = new JButton("▼");
        pnCtrls.add(btnToggle);
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
        
        JPanel pnCenterDown = new JPanel();
        pnCenterDown.setLayout(new BorderLayout());
        pnCenter.add(pnCenterDown, BorderLayout.SOUTH);
        
        cbxResearch = new JComboBox<Research>();
        pnCenterDown.add(cbxResearch);
        
        if(! (f instanceof ResearchCenter)) cbxResearch.setVisible(false);
        else {
            List<Research> tResearches = colony.getResearches();
            Vector<Research> researches = new Vector<Research>();
            
            for(Research r : tResearches) {
                if(r.isResearchAvail(colony)) researches.add(r);
            }
            
            tResearches = null;
            
            cbxResearch.setModel(new DefaultComboBoxModel<Research>(researches));
        }
        
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
        
        btnDestroy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int sel = JOptionPane.showConfirmDialog(superInstance.getDialog(), "이 시설을 철거하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                if(sel != JOptionPane.YES_OPTION) return;
                
                f.setHp(0);
                superInstance.refreshColonyContent();
                JOptionPane.showMessageDialog(superInstance.getDialog(), "철거 지시가 내려졌습니다. 시뮬레이션을 진행해 주세요.");
            }
        });
        
        cbxResearch.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(f instanceof ResearchCenter) {
                    ResearchCenter c = (ResearchCenter) f;
                    Research r = (Research) cbxResearch.getSelectedItem();
                    if(r != null) {
                        c.setResearchKey(r.getKey());
                    }
                }
            }
        });
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

    public void refresh(Facility fac, City city, Colony colony, ColonyManager superInstance) {
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
            List<Citizen> citizens = home.getCitizens(city, colony);
            if(citizens.isEmpty()) {
                res = res.append("\n    ").append("거주 인원이 없습니다.");
            } else {
                for(Citizen c : citizens) {
                    res = res.append("\n    ").append(c.getName());
                }
            }
            
        }
        
        List<Citizen> workers = fac.getWorkingCitizens(city, colony);
        if(! workers.isEmpty()) {
            res = res.append("\n").append("재직자...");
            
            for(Citizen c : workers) {
                res = res.append("\n    ").append(c.getName());
            }
        } else {
            res = res.append("\n    ").append("재직 중인 인원이 없습니다.");
        }
        
        if(fac instanceof ResearchCenter) {
            ResearchCenter rcenter = (ResearchCenter) fac;
            long sel = rcenter.getResearchKey();
            
            List<Research> tResearches = colony.getResearches();
            Vector<Research> researches = new Vector<Research>();
            for(Research r : tResearches) {
                if(r.isResearchAvail(colony)) researches.add(r);
            }
            tResearches = null;
            
            cbxResearch.setModel(new DefaultComboBoxModel<Research>(researches));
            
            boolean selectedRes = false;
            for(Research r : researches) {
                if(r.getKey() == sel) {
                    cbxResearch.setSelectedItem(r);
                    selectedRes = true;
                    break;
                }
            }
            if(! selectedRes) {
                if(! researches.isEmpty()) cbxResearch.setSelectedIndex(0);
                Research r = (Research) cbxResearch.getSelectedItem();
                if(r == null) rcenter.setResearchKey(0L);
                else rcenter.setResearchKey(r.getKey());
            }
        }
        
        List<State> states = fac.getStates();
        if(! states.isEmpty()) {
            res = res.append("\n").append("상태...");
            res = res.append("\n    ");
            for(State st : states) {
                res = res.append(st.getTitle()).append("\t");
            }
        }
        
        ta.setText(res.toString().trim());
        
        if(fac.getHp() <= 0) setEditable(false);
    }
    
    @Override
    public void refresh(int cycle, City city, Colony colony, ColonyManager superInstance) {
        Facility fac = getFacility(city);
        if(fac == null) { setEditable(false); return; }
        refresh(fac, city, colony, superInstance);
    }

    @Override
    public void setEditable(boolean editable) {
        tfName.setEditable(editable);
        btnDestroy.setEnabled(editable);
        cbxResearch.setEnabled(editable);
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
