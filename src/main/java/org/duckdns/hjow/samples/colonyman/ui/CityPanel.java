package org.duckdns.hjow.samples.colonyman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.Facility;
import org.duckdns.hjow.samples.colonyman.elements.HoldingJob;
import org.duckdns.hjow.samples.colonyman.elements.facilities.SupportGUIFacility;

public class CityPanel extends JPanel implements ColonyElementPanel {
    private static final long serialVersionUID = 3475480727850203183L;
    protected transient GUIColonyManager colonyManager;
    protected transient City city;
    protected transient JProgressBar progHp;
    protected transient JTextArea ta;
    protected transient JTextField tfName, tfSearchCitizen, tfSearchFacility;
    protected transient JPanel pnGrid, pnCitizens, pnFacilities, pnHoldings;
    protected transient JToolBar toolbarCity;
    protected transient JButton btnNewFac;
    protected transient JComboBox<String> cbxTax;
    protected transient JSplitPane splits;
    protected transient GridBagLayout layoutFacility, layoutCitizen;
    protected transient NewFacilityManager dialogNewFac;
    protected transient List<FacilityPanel> facilityPns = new Vector<FacilityPanel>();
    protected transient List<CitizenPanel>  citizenPns  = new Vector<CitizenPanel>();
    
    protected transient boolean flagSplitMoved = false;
    protected transient boolean flagEditable = true;
    
    public CityPanel() {
        
    }
    
    public CityPanel(City city, Colony colony, GUIColonyManager superInstance) {
        super();
        init(city, colony, superInstance);
    }
    
    public void init(City city, Colony colony, GUIColonyManager superInstance) {
        if(this.city != null) dispose();
        this.colonyManager = superInstance;
        
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
        
        Vector<String> listTax = new Vector<String>();
        for(int idx=5; idx<=15; idx++) {
            listTax.add(idx + " %");
        }
        cbxTax = new JComboBox<String>(listTax);
        cbxTax.setSelectedIndex(5);
        pnHp.add(new JLabel("Tax"));
        pnHp.add(cbxTax);
        
        cbxTax.addItemListener(new ItemListener() {    
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(! cbxTax.isEnabled()) return;
                String str = (String) cbxTax.getSelectedItem();
                if(str == null) str = "10 %";
                str = str.replace("%", "").trim();
                getCity().setTax(Integer.parseInt(str));
            }
        });
        
        progHp = new JProgressBar(JProgressBar.HORIZONTAL);
        pnHp.add(progHp);
        
        splits = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        pnCenter.add(splits, BorderLayout.CENTER);
        
        pnGrid = new JPanel();
        pnGrid.setLayout(new BorderLayout());
        splits.setRightComponent(pnGrid);
        
        JPanel pnTextStatus = new JPanel();
        pnTextStatus.setLayout(new GridLayout(2, 1));
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnTextStatus.add(new JScrollPane(ta));
        
        pnHoldings = new JPanel();
        pnHoldings.setLayout(new GridBagLayout());
        pnTextStatus.add(new JScrollPane(pnHoldings));
        
        splits.setLeftComponent(pnTextStatus);
        
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
        
        layoutFacility = new GridBagLayout();
        layoutCitizen  = new GridBagLayout();
        
        pnFacilities.setLayout(layoutFacility);
        pnCitizens.setLayout(layoutCitizen);
        
        pnFacRoot.add(new JScrollPane(pnFacilities, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
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
        
        toolbarCity = new JToolBar();
        // pnDown.add(toolbarCity, BorderLayout.CENTER);
        pnTop.add(toolbarCity, BorderLayout.SOUTH);
        
        btnNewFac = new JButton("새 시설 건설");
        toolbarCity.add(btnNewFac);
        
        btnNewFac.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewFacilityButtonPressed(superInstance);
            }
        });
        
        refresh(0, city, colony, superInstance);
    }
    
    /** 시설 추가 건설 가능여부 확인, null 리턴 시 가능한 것. null이 아닌 경우, 불가능한 사유 메시지가 리턴된 것. */
    protected String canAddFacilities() {
        String msg = null;
        
        if(city.getFacility().size() >= city.getSpaces()) {
            return "이 도시에는 더 이상 시설을 건설할 수 없습니다.";
        }
        
        return msg;
    }
    
    /** 새 시설 건설 버튼 클릭 시 호출 */
    protected void onNewFacilityButtonPressed(GUIColonyManager superInstance) {
        if(dialogNewFac != null) dialogNewFac.dispose();
        dialogNewFac = null;
        
        String msg = canAddFacilities();
        if(msg != null) {
            JOptionPane.showMessageDialog(colonyManager.getDialog(), msg);
            return;
        }
        
        dialogNewFac = new NewFacilityManager(superInstance, city);
        dialogNewFac.setVisible(true);
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
            setEditable(false);
            return;
        }
        
        // 이름, HP 출력
        tfName.setText(city.getName());
        
        progHp.setMaximum(city.getMaxHp());
        progHp.setValue(city.getHp());
        
        // 시설 목록 출력
        List<Facility> facList = city.getFacility();
        int columns = 1;
        int idx = 0;
        int rowNo = 0;
        int colNo = 0;
        int sizes = facList.size();
        GridBagConstraints gridBagConst;
        JPanel pnEmpty;
        
        // 파괴된 시설을 출력하는 영역 제거
        for(Facility f : facList) {
            if(f.getHp() <= 0) {
                idx = 0;
                while(idx < facilityPns.size()) {
                    if(f.getKey() == facilityPns.get(idx).getFacilityKey()) { facilityPns.get(idx).dispose(); facilityPns.remove(idx); continue; }
                    idx++;
                }
            }
        }
        
        columns = (int) Math.ceil((((GUIColonyManager) superInstance).getDialogWidth() - 200.0) / 700.0);
        if(columns <= 0) columns = 1;
        
        idx = 0;
        rowNo = 0;
        colNo = 0;
        
        if(sizes != facilityPns.size()) {
            for(FacilityPanel p : facilityPns) { p.dispose(); }
            facilityPns.clear();
            pnFacilities.removeAll();
            // pnFacilities.setLayout(new GridLayout(sizes, 1));
            
            for(idx=0; idx<sizes; idx++) {
                FacilityPanel pn;
                if(facList.get(idx) instanceof SupportGUIFacility) {
                    SupportGUIFacility sFac = (SupportGUIFacility) facList.get(idx);
                    pn = sFac.createPanel(city, colony, superInstance);
                } else {
                    pn = new FacilityPanel(facList.get(idx), city, colony, (GUIColonyManager) superInstance);
                }
                
                gridBagConst = new GridBagConstraints();
                gridBagConst.gridx = colNo; colNo++;
                gridBagConst.gridy = rowNo; if(colNo >= columns) { colNo = 0; rowNo++; }
                gridBagConst.gridwidth = 1;
                gridBagConst.gridheight = 1;
                gridBagConst.weightx = 1.0;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
                gridBagConst.fill = GridBagConstraints.HORIZONTAL;
                gridBagConst.anchor = GridBagConstraints.NORTH;
                
                pnFacilities.add(pn, gridBagConst);
                facilityPns.add(pn);
            }
            
            if(colNo < columns) {
                while(colNo < columns) { // 남은 빈 공간 채우기
                    pnEmpty = new JPanel();
                    
                    gridBagConst = new GridBagConstraints();
                    gridBagConst.gridx = colNo; colNo++;
                    gridBagConst.gridy = rowNo;
                    gridBagConst.gridwidth = 1;
                    gridBagConst.gridheight = 1;
                    gridBagConst.weightx = 1.0;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
                    gridBagConst.fill = GridBagConstraints.HORIZONTAL;
                    gridBagConst.anchor = GridBagConstraints.NORTH;
                    
                    pnFacilities.add(pnEmpty, gridBagConst);
                }
            }
            colNo = 0;
            rowNo++;
            
            pnEmpty = new JPanel();
            
            gridBagConst = new GridBagConstraints();
            gridBagConst.gridx = colNo; colNo++;
            gridBagConst.gridy = rowNo; if(colNo >= columns) { colNo = 0; rowNo++; }
            gridBagConst.gridwidth = columns;
            gridBagConst.gridheight = 1;
            gridBagConst.weightx = 1.0;
            gridBagConst.weighty = 1.0;
            gridBagConst.fill = GridBagConstraints.BOTH;
            
            pnFacilities.add(pnEmpty, gridBagConst);
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
        rowNo = 0;
        colNo = 0;
        // pnCitizens.setLayout(new GridLayout(citizenPns.size(), 1));
        for(CitizenPanel p : citizenPns) {
            gridBagConst = new GridBagConstraints();
            gridBagConst.gridx = colNo; colNo++;
            gridBagConst.gridy = rowNo; if(colNo >= columns) { colNo = 0; rowNo++; }
            gridBagConst.gridwidth = 1;
            gridBagConst.gridheight = 1;
            gridBagConst.weightx = 1.0;
            gridBagConst.fill = GridBagConstraints.HORIZONTAL;
            gridBagConst.anchor = GridBagConstraints.NORTH;
            
            pnCitizens.add(p, gridBagConst);
            p.refresh(cycle, city, colony, superInstance);
        }
        
        pnEmpty = new JPanel();
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = colNo; colNo++;
        gridBagConst.gridy = rowNo; if(colNo >= columns) { colNo = 0; rowNo++; }
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 1.0;
        gridBagConst.fill = GridBagConstraints.BOTH;
        gridBagConst.weighty = 1.0;
        
        pnCitizens.add(pnEmpty, gridBagConst);
        
        // 작업중 항목 출력
        refreshHoldingJobs();
        
        // 도시 정보 출력
        ta.setText(city.getStatusString(colony, superInstance));
        
        // 가로 경계선 위치 조절
        if(((GUIColonyManager) superInstance).isVisible()) {
            if(! flagSplitMoved) splits.setDividerLocation(0.3);
            flagSplitMoved = true;
        }
        
        // 이 도시 HP가 0이면 전부 비활성화
        if(city.getHp() <= 0) {
            setEditable(false);
        } else {
            setEditable(flagEditable);
            
            if(flagEditable) {
                // 시설 설치 가능여부 판단
                btnNewFac.setEnabled(city.getFacility().size() < city.getSpaces());
            }
        }
        
        cbxTax.setSelectedItem(city.getTax() + " %");
    }
    
    /** 작업중 항목 출력 */
    protected void refreshHoldingJobs() {
        pnHoldings.removeAll();
        List<HoldingJob> listJobs = city.getHoldings();
        // pnHoldings.setLayout(new GridLayout(listJobs.size(), 1));
        GridBagConstraints gridBagConst;
        int rowNo = 0;
        
        for(HoldingJob j : listJobs) {
            JPanel pnHoldingOne = new JPanel();
            pnHoldingOne.setLayout(new BorderLayout());
            
            JPanel pnLabel, pnStatus;
            pnLabel  = new JPanel();
            pnStatus = new JPanel();
            pnLabel.setLayout(new FlowLayout(FlowLayout.LEFT));
            pnStatus.setLayout(new FlowLayout(FlowLayout.RIGHT));
            
            pnHoldingOne.add(pnLabel , BorderLayout.CENTER);
            pnHoldingOne.add(pnStatus, BorderLayout.EAST);
            
            String str = j.getCommandTitle();
            if(str == null) str = j.getCommand();
            if(j.getParameter() != null) str += " - " + j.getParameter();
            
            pnLabel.add(new JLabel(str));
            
            JProgressBar prog = new JProgressBar();
            if(j.getCycleMax() <= 0 || j.getCycleLeft() < 0) {
                prog.setIndeterminate(true);
            } else {
                prog.setMaximum(j.getCycleMax());
                prog.setValue(j.getCycleMax() - j.getCycleLeft());
            }
            
            pnStatus.add(prog);
            
            gridBagConst = new GridBagConstraints();
            gridBagConst.gridx = 0;
            gridBagConst.gridy = rowNo; rowNo++;
            gridBagConst.gridwidth = 1;
            gridBagConst.gridheight = 1;
            gridBagConst.weightx = 1.0;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
            gridBagConst.fill = GridBagConstraints.HORIZONTAL;
            gridBagConst.anchor = GridBagConstraints.NORTH;
            
            pnHoldings.add(pnHoldingOne, gridBagConst);
        }
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo; rowNo++;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 1.0;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
        gridBagConst.fill = GridBagConstraints.BOTH;
        gridBagConst.weighty = 1.0;
        pnHoldings.add(new JPanel(), gridBagConst);
    }
    
    public void setEditable(boolean editable) {
        flagEditable = editable;
        for(FacilityPanel p : facilityPns) {
            if(p.getFacility(city).getHp() <= 0) p.setEditable(false);
            else p.setEditable(editable); 
        }
        
        cbxTax.setEnabled(editable);
        tfName.setEditable(editable);
        tfSearchCitizen.setEditable(editable);
        tfSearchFacility.setEditable(editable);
        toolbarCity.setEnabled(editable);
        toolbarCity.setVisible(editable);
        // btnNewFac.setEnabled(editable);
        
        if(! editable) {
            if(dialogNewFac != null) dialogNewFac.dispose();
            dialogNewFac = null;
        }
    }
    
    @Override
    public void dispose() {
        city = null;
        for(FacilityPanel p : facilityPns) { p.dispose(); }
        facilityPns.clear();
        for(CitizenPanel p : citizenPns) { p.dispose(); }
        citizenPns.clear();
        if(dialogNewFac != null) dialogNewFac.dispose();
        this.dialogNewFac = null;
        this.colonyManager = null;
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
