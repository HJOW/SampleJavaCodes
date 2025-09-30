package org.duckdns.hjow.samples.colonyman.ui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.samples.colonyman.ColonyClassLoader;
import org.duckdns.hjow.samples.colonyman.elements.ColonyInformation;
import org.duckdns.hjow.samples.util.UIUtil;

public class NewColonyManager implements Disposeable {
    protected GUIColonyManager man;
    protected JDialog dialog;
    protected JComboBox<ColonyInformation> cbxColTypes;
    protected JTextArea ta;
    
    public NewColonyManager(GUIColonyManager man) {
        this.man = man;
        dialog = new JDialog(man.getDialog(), true);
        dialog.setSize(400, 300);
        UIUtil.center(dialog);
        dialog.setTitle("새 정착지 생성");
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog = null;
                dispose();
            }
        });
        
        dialog.setLayout(new BorderLayout());
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        dialog.add(pnMain, BorderLayout.CENTER);
        
        JPanel pnUp, pnCenter, pnDown;
        pnUp     = new JPanel();
        pnCenter = new JPanel();
        pnDown   = new JPanel();
        pnUp.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new BorderLayout());
        pnMain.add(pnUp    , BorderLayout.NORTH);
        pnMain.add(pnCenter, BorderLayout.CENTER);
        pnMain.add(pnDown  , BorderLayout.SOUTH);
        
        Vector<ColonyInformation> list = new Vector<ColonyInformation>();
        list.addAll(ColonyClassLoader.colonyInfos());
        cbxColTypes = new JComboBox<ColonyInformation>(list);
        pnUp.add(cbxColTypes, BorderLayout.CENTER);
        
        cbxColTypes.addItemListener(new ItemListener() {   
            @Override
            public void itemStateChanged(ItemEvent e) {
                refreshDesc();
            }
        });
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnCenter.add(new JScrollPane(ta), BorderLayout.CENTER);
        
        JPanel pnCtrl = new JPanel();
        pnCtrl.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnDown.add(pnCtrl, BorderLayout.CENTER);
        
        JButton btn;
        
        btn = new JButton("개척");
        pnCtrl.add(btn);
        btn.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                ColonyInformation info = (ColonyInformation) cbxColTypes.getSelectedItem();
                if(info == null) { JOptionPane.showMessageDialog(getDialog(), "해당 타입으로 정착지를 만들 수 없습니다."); return; }
                
                man.onNewColonyTypeDecided(info.getName(), getSelf());
                dispose();
            }
        });
        
        btn = new JButton("취소");
        pnCtrl.add(btn);
        btn.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        refreshDesc();
    }
    
    protected JDialog getDialog() {
        return dialog;
    }
    
    protected NewColonyManager getSelf() {
        return this;
    }
    
    protected void refreshDesc() {
        String desc = "";
        ColonyInformation info = (ColonyInformation) cbxColTypes.getSelectedItem();
        if(info != null) desc = info.getDescription();
        ta.setText(desc);
    }
    
    public void open() {
        dialog.setVisible(true);
    }
    
    public void close() {
        dispose();
    }

    @Override
    public void dispose() {
        if(dialog != null) dialog.setVisible(false);
        man    = null;
        dialog = null;
    }
}
