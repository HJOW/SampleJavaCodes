package org.duckdns.hjow.samples.colonyman.elements;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import org.duckdns.hjow.samples.colonyman.AccountingData;
import org.duckdns.hjow.samples.colonyman.ColonyManager;

public class ColonyPanel extends JPanel implements ColonyElementPanel {
    private static final long serialVersionUID = 3851432705333464777L;
    protected Colony colony;
    
    protected transient List<CityPanel> pnCities = new Vector<CityPanel>();
    protected transient JPanel pnColonyBasics, pnAccountingMain;
    protected transient DefaultTableModel tableAccounting;
    protected transient JTabbedPane tabMain, tabCities;
    protected transient JProgressBar progHp;
    protected transient JTextField tfColonyName, tfColonyTime, tfIncomes;
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
        
        tabMain = new JTabbedPane();
        add(tabMain, BorderLayout.CENTER);
        
        tabCities = new JTabbedPane();
        tabMain.add("도시", tabCities);
        
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
        pnCities.clear();
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
        
        refreshAccoutingTable();
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
}
