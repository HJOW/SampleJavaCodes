package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.GUIColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.HoldingJob;
import org.duckdns.hjow.samples.util.UIUtil;

public class NewFacilityManager extends JDialog {
    private static final long serialVersionUID = 8433244450809087631L;
    protected ColonyManager colonyManager;
    protected City city;
    
    protected JComboBox<FacilityInformation> cbxFacInfos;
    protected JTextArea ta;
    protected JButton btnOk, btnClose;
    
    public NewFacilityManager() {
        super();
    }
    public NewFacilityManager(GUIColonyManager colonyManager, City city) {
        super(colonyManager.getDialog());
        init(colonyManager, city);
        refresh();
    }
    
    public void dispose() {
        disposeFields();
        setVisible(false);
    }
    
    public void disposeFields() {
        this.colonyManager = null;
        this.city          = null;
    }
    
    public void init(ColonyManager colonyManager, City city) {
        if(this.city != null) disposeFields();
        
        this.colonyManager = colonyManager;
        this.city = city;
        
        setSize(400, 300);
        setLayout(new BorderLayout());
        setTitle("새 시설 건설");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disposeFields();
            }
        });
        UIUtil.center(this);
        
        JPanel pnMain, pnCenter, pnUp, pnDown;
        
        pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        add(pnMain, BorderLayout.CENTER);
        
        pnUp     = new JPanel();
        pnCenter = new JPanel();
        pnDown   = new JPanel();
        
        pnUp.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        pnMain.add(pnUp    , BorderLayout.NORTH);
        pnMain.add(pnCenter, BorderLayout.CENTER);
        pnMain.add(pnDown  , BorderLayout.SOUTH);
        
        Vector<FacilityInformation> list = new Vector<FacilityInformation>();
        list.addAll(FacilityManager.getFacilityInformations());
        cbxFacInfos = new JComboBox<>(list);
        pnUp.add(cbxFacInfos, BorderLayout.CENTER);
        
        ta = new JTextArea();
        ta.setEditable(false);
        ta.setLineWrap(true);
        pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
        
        btnOk    = new JButton("건설");
        btnClose = new JButton("취소");
        
        pnDown.add(btnOk);
        pnDown.add(btnClose);
        
        cbxFacInfos.addItemListener(new ItemListener() {   
            @Override
            public void itemStateChanged(ItemEvent e) {
                refresh();
            }
        });
        
        btnClose.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FacilityInformation info;
                Colony col;
                
                try {
                    info = (FacilityInformation) cbxFacInfos.getSelectedItem();
                    col = city.getColony(colonyManager);
                    
                    if(col.getMoney() < info.getPrice().longValue()) {
                        JOptionPane.showMessageDialog(getDialog(), "예산이 부족합니다.\n" + (info.getPrice() - col.getMoney()) + " 의 예산이 더 필요합니다.");
                        return;
                    };
                    
                    if(col.getTech() < info.getTech().longValue()) {
                        JOptionPane.showMessageDialog(getDialog(), "기술이 부족합니다.\n" + (info.getTech() - col.getTech()) + " 의 기술이 더 필요합니다.");
                        return;
                    };
                    
                    Method mthdChecker = info.getFacilityClass().getMethod("isBuildAvail", Colony.class, City.class);
                    String chkRes = (String) mthdChecker.invoke(null, col, city);
                    if(chkRes != null) {
                        JOptionPane.showMessageDialog(getDialog(), chkRes);
                        return;
                    }
                    
                    HoldingJob job = new HoldingJob(info.getBuildingCycle(), info.getBuildingCycle(), "NewFacility", info.getName());
                    city.getHoldings().add(job);
                    
                    col.modifyingMoney(info.getPrice() * (-1) , city, city, info.getTitle() + " 건설");
                    
                    colonyManager.refreshColonyContent();
                    dispose();
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(getDialog(), "오류가 발생하였습니다.\n" + ex.getMessage());
                }
            }
        });
        
        refresh();
    }
    
    public JDialog getDialog() { return this; }
    
    public void refresh() {
        FacilityInformation info = (FacilityInformation) cbxFacInfos.getSelectedItem();
        
        if(info == null) {
            btnOk.setEnabled(false);
            ta.setText("");
            return;
        }
        
        Colony col = city.getColony(colonyManager);
        boolean avail = true;
        String prepends = "";
        String appends  = "";
        
        if(col.getMoney() < info.getPrice().longValue()) {
            prepends = "\n건설에 " + (info.getPrice() - col.getMoney()) + " 의 예산이 더 필요합니다.";
            avail = false;
        }
        if(col.getTech() < info.getTech().longValue()) {
            prepends = "\n건설에 " + (info.getTech() - col.getTech()) + " 의 기술이 더 필요합니다.";
            avail = false;
        }
        
        appends = appends + "\n" + "비용 : " + info.getPrice();
        appends = appends + "\n" + "기술 : " + info.getTech();
        appends = appends + "\n" + "소요 : " + info.getBuildingCycle();
        
        ta.setText(new String(prepends + "\n\n" + info.getDescription() + "\n\n" + appends).trim());
        btnOk.setEnabled(avail);
    }
}
