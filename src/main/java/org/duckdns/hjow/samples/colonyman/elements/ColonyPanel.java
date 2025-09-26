package org.duckdns.hjow.samples.colonyman.elements;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import org.duckdns.hjow.commons.ui.JLogArea;
import org.duckdns.hjow.samples.colonyman.AccountingData;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.research.Research;
import org.duckdns.hjow.samples.colonyman.elements.research.ResearchPanel;

/** 정착지 정보 출력 및 컨트롤을 담당하는 UI 컴포넌트 */
public class ColonyPanel extends JPanel implements ColonyElementPanel {
    private static final long serialVersionUID = 3851432705333464777L;
    protected ColonyManager superInstance;
    protected Colony colony;
    
    protected transient List<CityPanel> pnCities = new Vector<CityPanel>();
    protected transient JPanel pnColonyBasics, pnAccountingMain, pnHoldings, pnResearches;
    protected transient DefaultTableModel tableAccounting;
    protected transient JSplitPane splits;
    protected transient JTabbedPane tabMain, tabCities;
    protected transient JProgressBar progHp;
    protected transient JTextField tfColonyName, tfColonyTime, tfIncomes;
    protected transient JTextArea taStatus;
    protected transient JLogArea taLog;
    protected transient JToolBar toolbar;
    
    protected transient boolean flagSplitLocated = false;
    protected transient boolean flagEditable = true;
    
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
        this.superInstance = superInstance;
        
        setLayout(new BorderLayout());
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        add(pnMain, BorderLayout.CENTER);
        
        splits = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        pnMain.add(splits, BorderLayout.CENTER);
        
        JPanel pnCenter = new JPanel();
        pnCenter.setLayout(new BorderLayout());
        splits.setTopComponent(pnCenter);
        
        tabMain = new JTabbedPane();
        pnCenter.add(tabMain, BorderLayout.CENTER);
        
        tabCities = new JTabbedPane();
        tabMain.add("도시", tabCities);
        
        pnResearches = new JPanel();
        tabMain.add("연구", pnResearches);
        
        pnAccountingMain = new JPanel();
        tabMain.add("예산", pnAccountingMain);
        
        tableAccounting = new DefaultTableModel();
        tableAccounting.addColumn("사유");
        tableAccounting.addColumn("대상");
        tableAccounting.addColumn("금액");
        
        tfIncomes = new JTextField();
        tfIncomes.setEditable(false);
        
        pnAccountingMain.setLayout(new BorderLayout());
        pnAccountingMain.add(new JScrollPane(new JTable(tableAccounting)), BorderLayout.CENTER);
        pnAccountingMain.add(tfIncomes, BorderLayout.SOUTH);
        
        pnResearches.setLayout(new GridBagLayout());
        
        JPanel pnColTop, pnColBottom;
        pnColTop    = new JPanel();
        pnColBottom = new JPanel();
        pnColTop.setLayout(new BorderLayout());
        pnColBottom.setLayout(new BorderLayout());
        
        pnCenter.add(pnColTop   , BorderLayout.NORTH);
        pnCenter.add(pnColBottom, BorderLayout.SOUTH);
        
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
        
        taStatus = new JTextArea();
        taStatus.setEditable(false);
        pnTopDetail.add(taStatus, BorderLayout.CENTER);
        
        toolbar = new JToolBar();
        pnTopDetail.add(toolbar, BorderLayout.SOUTH);
        
        JPanel pnLog = new JPanel();
        pnLog.setLayout(new BorderLayout());
        splits.setBottomComponent(pnLog);
        
        taLog = new JLogArea();
        pnLog.add(taLog, BorderLayout.CENTER);
        
        pnHoldings = new JPanel();
        pnLog.add(new JScrollPane(pnHoldings), BorderLayout.EAST);
    }
    
    @Override
    public void dispose() {
        colony = null;
        for(CityPanel c : pnCities) {
            c.dispose();
        }
        pnCities.clear();
        
        if(taLog != null) taLog.dispose();
        removeAll();
        superInstance = null;
    }

    @Override
    public void setEditable(boolean editable) {
        flagEditable = editable;
        tfColonyName.setEditable(editable);
        for(CityPanel c : pnCities) {
            if(c.getCity().getHp() <= 0) c.setEditable(false);
            else c.setEditable(editable);
        }
    }
    
    /** 화면 새로고침 예약 */
    public void reserveRefresh() {
        if(superInstance != null) superInstance.reserveRefresh();
    }

    @Override
    public void refresh(int cycle, City city, Colony colony, ColonyManager superInstance) { // city is null
        if(colony == null) {
            tfColonyName.setText("");
            tfColonyTime.setText("");
            taStatus.setText("");
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
        taStatus.setText(colony.getStatusString(superInstance));
        
        for(CityPanel c : pnCities) {
            c.refresh(cycle, c.getCity(), colony, superInstance);
        }
        
        pnHoldings.removeAll();
        pnResearches.removeAll();
        GridBagConstraints gridBagConst;
        int rowNo = 0;
        for(Research r : colony.getResearches()) {
            if(r.getLevel() <= 0 && r.getProgress() <= 0) continue;
            
            ResearchPanel pnRes = new ResearchPanel(r);
            
            gridBagConst = new GridBagConstraints();
            gridBagConst.gridx = 0;
            gridBagConst.gridy = rowNo; rowNo++;
            gridBagConst.gridwidth = 1;
            gridBagConst.gridheight = 1;
            gridBagConst.weightx = 1.0;  // fill 옵션으로 가로 채우기가 안되면 이 옵션이 필요함.
            gridBagConst.fill = GridBagConstraints.HORIZONTAL;
            gridBagConst.anchor = GridBagConstraints.NORTH;
            
            pnResearches.add(pnRes, gridBagConst);
            pnRes.refresh(cycle, city, colony);
        }
        
        gridBagConst = new GridBagConstraints();
        gridBagConst.gridx = 0;
        gridBagConst.gridy = rowNo; rowNo++;
        gridBagConst.gridwidth = 1;
        gridBagConst.gridheight = 1;
        gridBagConst.weightx = 1.0;
        gridBagConst.weighty = 1.0;
        gridBagConst.fill = GridBagConstraints.BOTH;
        pnResearches.add(new JPanel(), gridBagConst);
        
        refreshAccoutingTable();
        setEditable(flagEditable);
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
    
    /** 모든 행 삭제 */
    public void clearAccountingTable() {
        while(tableAccounting.getRowCount() >= 1) { tableAccounting.removeRow(0); }
    }
    
    /** 회계 정보 새로고침 */
    public void refreshAccoutingTable() {
        // 모든 행 삭제
        clearAccountingTable();
        
        Colony col = getColony();
        Vector<Object> rows;
        
        // 데이터 쌓기
        List<AccountingData> list = col.getAccountingData();
        BigInteger timeStd = new BigInteger(col.getTime().toByteArray()).subtract(new BigInteger(String.valueOf(col.getAccountingPeriod())));
        long incomes = 0L;
        for(AccountingData data : list) {
            if(data.isDisposed()) continue;
            
            BigInteger time = data.getTime();
            
            if(timeStd.compareTo(time) >= 0) {
                rows = new Vector<Object>();
                
                rows.add(data.getReason());
                
                City cityCurrent = null;
                for(City ct : col.getCities()) {
                    if(ct.getKey() == data.getCityKey()) {
                        cityCurrent = ct;
                        break;
                    }
                }
                if(cityCurrent == null) continue;
                
                String sourceName = null;
                for(Facility f : cityCurrent.getFacility()) {
                    if(f.getKey() == data.getSourceKey()) {
                        sourceName = f.getName();
                        break;
                    }
                }
                if(sourceName == null) {
                    for(Citizen c : cityCurrent.getCitizens()) {
                        if(c.getKey() == data.getSourceKey()) {
                            sourceName = c.getName();
                            break;
                        }
                    }
                }
                if(sourceName == null) sourceName = "UNKNOWN";
                
                rows.add(sourceName);
                
                long val = data.getAmount();
                rows.add(new Long(val));
                
                tableAccounting.addRow(rows);
                incomes += val;
            } else {
                data.dispose();
                continue;
            }
            tfIncomes.setText(String.valueOf(incomes));
        }
        
        // 오래된 회계자료 제거
        int idx = 0;
        while(idx < col.getAccountingData().size()) {
            if(col.getAccountingData().get(idx).isDisposed()) {
                col.getAccountingData().remove(idx);
                continue;
            }
            idx++;
        }
    }
    
    /** 컨텐츠 영역과 로그 사이의 분할바 위치 조정 */
    public void setDividerLocation(double loc) {
        splits.setDividerLocation(loc);
        flagSplitLocated = true;
    }
    
    /** 최초 1회 컨텐츠 영역과 로그 사이의 분할바 위치 적절히 조정 */
    public void setDividerLocationOnlyFirst() {
        if(flagSplitLocated) return;
        setDividerLocation(0.7);
    }
    
    /** 정착지에서 발생하는 로그 */
    public void log(String msg) {
        if(taLog != null) taLog.log(msg);
    }
    
    /** 정착지에서 발생하는 로그 리셋 */
    public void clearLog() {
        if(taLog != null) taLog.clear();
    }
}
