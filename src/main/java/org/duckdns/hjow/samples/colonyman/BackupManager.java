package org.duckdns.hjow.samples.colonyman;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyBackup;
import org.duckdns.hjow.samples.util.UIUtil;

public class BackupManager implements Disposeable {
    protected JDialog dialog;
    protected JPanel pnSecurity;
    protected JTextField tfName, tfFile;
    protected JLabel lbPassword;
    protected JCheckBox chkUseEnc;
    protected JPasswordField tfPassword;
    protected JTextArea  ta, taDet;
    protected JButton    btnAccept, btnConcat, btnClose, btnSelFile;
    protected JProgressBar prog;
    protected JFileChooser backupChooser;
    
    protected transient boolean saveMode = true;
    protected transient ColonyManager superInstance = null;
    protected transient List<Colony> colonies = new ArrayList<Colony>();
    protected transient String security = "";
    
    public BackupManager(ColonyManager superInstance) {
        this.superInstance = superInstance;
        
        dialog = new JDialog(superInstance.getDialog());
        dialog.setTitle("백업");
        dialog.setSize(500, 300);
        UIUtil.center(dialog);
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
        
        pnMain.add(pnUp, BorderLayout.NORTH);
        pnMain.add(pnCenter, BorderLayout.CENTER);
        pnMain.add(pnDown, BorderLayout.SOUTH);
        
        tfName = new JTextField();
        pnUp.add(tfName);
        
        ta = new JTextArea();
        ta.setLineWrap(true);
        pnCenter.add(new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        
        JPanel pnCenterDown = new JPanel();
        pnCenterDown.setLayout(new BorderLayout());
        pnCenter.add(pnCenterDown, BorderLayout.SOUTH);
        
        taDet = new JTextArea();
        taDet.setLineWrap(true);
        taDet.setEditable(false);
        pnCenterDown.add(taDet, BorderLayout.CENTER);
        
        pnSecurity = new JPanel();
        pnSecurity.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnCenterDown.add(pnSecurity, BorderLayout.SOUTH);
        
        chkUseEnc = new JCheckBox("암호화");
        pnSecurity.add(chkUseEnc);
        
        lbPassword = new JLabel("암호");
        pnSecurity.add(lbPassword);
        
        tfPassword = new JPasswordField(10);
        pnSecurity.add(tfPassword);
        
        pnSecurity.setVisible(false);
        
        JPanel pnCtrl = new JPanel();
        pnCtrl.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnDown.add(pnCtrl, BorderLayout.CENTER);
        
        tfFile = new JTextField(20);
        btnSelFile = new JButton("...");
        btnAccept = new JButton("저장");
        btnConcat = new JButton("복원 (병합)");
        btnClose = new JButton("취소");
        
        pnCtrl.add(tfFile);
        pnCtrl.add(btnSelFile);
        pnCtrl.add(btnAccept);
        pnCtrl.add(btnConcat);
        pnCtrl.add(btnClose);
        
        btnConcat.setVisible(false);
        
        btnSelFile.addActionListener(new ActionListener() {    
            @Override
            public void actionPerformed(ActionEvent e) {
                int sel = backupChooser.showSaveDialog(dialog);
                if(sel != JFileChooser.APPROVE_OPTION) return;
                tfFile.setText(backupChooser.getSelectedFile().getAbsolutePath());
            }
        });
        
        btnClose.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false); 
            }
        });
        
        btnAccept.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean enBtnAccept = btnAccept.isEnabled();
                boolean enBtnConcat = btnConcat.isEnabled();
                btnAccept.setEnabled(false);
                btnConcat.setEnabled(false);
                
                new Thread(new Runnable() {   
                    @Override
                    public void run() {
                        if(saveMode) { try { onSaveRequested();         } catch(Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(dialog, "오류 : " + ex.getMessage()); } }
                        else         { try { onLoadCompleteRequested(); } catch(Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(dialog, "오류 : " + ex.getMessage()); } }
                        prog.setIndeterminate(false);
                        btnAccept.setEnabled(enBtnAccept);
                        btnConcat.setEnabled(enBtnConcat);
                    }
                }).start(); 
            }
        });
        
        btnConcat.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean enBtnAccept = btnAccept.isEnabled();
                boolean enBtnConcat = btnConcat.isEnabled();
                btnAccept.setEnabled(false);
                btnConcat.setEnabled(false);
                
                new Thread(new Runnable() {   
                    @Override
                    public void run() {
                        try { onLoadConcatRequested(); } catch(Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(dialog, "오류 : " + ex.getMessage()); }
                        prog.setIndeterminate(false);
                        btnAccept.setEnabled(enBtnAccept);
                        btnConcat.setEnabled(enBtnConcat);
                    }
                }).start();
            }
        });
        
        chkUseEnc.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(chkUseEnc.isSelected()) tfPassword.setEditable(true);
                else                       tfPassword.setEditable(false);
            }
        });
        
        prog = new JProgressBar();
        pnDown.add(prog, BorderLayout.NORTH);
        
        backupChooser = new JFileChooser();
        backupChooser = new JFileChooser();
        backupChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        backupChooser.setMultiSelectionEnabled(false);
        backupChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public String getDescription() {
                return "정착지 백업 파일 (*.colbak)";
            }
            
            @Override
            public boolean accept(File f) {
                if(f == null) return false;
                if(f.isDirectory()) return false;
                return f.getName().toLowerCase().endsWith(".colbak");
            }
        });
    }
    
    public void openLoad() {
        this.colonies.clear();
        this.colonies.addAll(colonies);
        
        chkUseEnc.setSelected(false);
        tfFile.setText("");
        tfFile.setEditable(false);
        tfPassword.setText("");
        tfPassword.setEditable(true);
        btnSelFile.setVisible(false);
        btnConcat.setVisible(true);
        lbPassword.setVisible(true);
        pnSecurity.setVisible(false);
        chkUseEnc.setVisible(false);
        taDet.setVisible(true);
        btnAccept.setText("복원 (대체)");
        tfName.setText("");
        ta.setText("");
        tfName.setEditable(false);
        ta.setEditable(false);
        dialog.setTitle("복원");
        saveMode = false;
        security = "";
        
        int sel = backupChooser.showOpenDialog(dialog);
        if(sel != JFileChooser.APPROVE_OPTION) { dialog.setVisible(false); return; }
        
        File file = backupChooser.getSelectedFile();
        tfFile.setText(file.getAbsolutePath());
        
        dialog.setTitle("복원 - " + file.getName());
        
        prog.setIndeterminate(true);
        btnAccept.setEnabled(false);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                onLoadRequested();
                prog.setIndeterminate(false);
                btnAccept.setEnabled(true);
            }
        }).start();
        
        try { dialog.setVisible(true); } catch(Exception ex) {  }
    }
    
    public void openSave(List<Colony> colonies) {
        this.colonies.clear();
        this.colonies.addAll(colonies);
        
        chkUseEnc.setSelected(false);
        tfFile.setText("");
        tfFile.setEditable(true);
        tfPassword.setText("");
        tfPassword.setEditable(false);
        btnSelFile.setVisible(true);
        btnConcat.setVisible(false);
        taDet.setVisible(false);
        lbPassword.setVisible(false);
        pnSecurity.setVisible(true);
        chkUseEnc.setVisible(true);
        btnAccept.setText("저장");
        tfName.setEditable(true);
        ta.setEditable(true);
        tfName.setText("이 곳에 백업의 이름을 입력해 주세요.");
        ta.setText("이 곳에 백업의 설명을 입력해 주세요.");
        dialog.setTitle("백업");
        saveMode = true;
        security = "";
        
        dialog.setVisible(true);
        tfName.requestFocus();
    }
    
    public void close() {
        if(dialog != null) dialog.setVisible(false);
    }

    @Override
    public void dispose() {
        if(dialog != null) dialog.setVisible(false);
        if(tfName != null) tfName.setText("");
        if(ta     != null) ta.setText("");
        if(taDet  != null) taDet.setText("");
        colonies.clear();
        superInstance = null;
    }
    
    protected void onSaveRequested() {
        String strFile = tfFile.getText().trim();
        if(strFile.equals("")) { JOptionPane.showMessageDialog(dialog, "저장할 파일 경로와 이름을 지정해 주세요."); return; }
        
        prog.setIndeterminate(true);
        
        ColonyBackup bak = new ColonyBackup();
        bak.setName(tfName.getText());
        bak.setDescription(ta.getText());
        bak.getColonies().addAll(colonies);
        bak.setCreated(new Date(System.currentTimeMillis()));
        
        JsonObject json;
        String password = null;
        
        if(chkUseEnc.isSelected()) password = new String(tfPassword.getPassword());
        
        try {
            String lower = strFile.toLowerCase();
            if(! lower.endsWith(".colbak")) strFile += ".colbak";
            
            if(password != null) json = bak.toJson("AES", password);
            else                 json = bak.toJson();
            
            File file = new File(strFile);
            FileUtil.writeString(file, "UTF-8", json.toJSON(), GZIPOutputStream.class);
            
            dialog.setVisible(false);
            colonies.clear();
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "오류 : " + ex.getMessage());
        }
    }
    
    protected void onLoadRequested() {
        File file = new File(tfFile.getText());
        if(! file.exists()) {
            JOptionPane.showMessageDialog(dialog, "파일을 찾을 수 없습니다.\n" + file.getAbsolutePath());
            dialog.setVisible(false);
            return;
        }
        
        colonies.clear();
        
        try {
            String strJson = FileUtil.readString(file, "UTF-8", GZIPInputStream.class);
            JsonObject json = (JsonObject) JsonObject.parseJson(strJson);
            strJson = null;
            
            Exception exc = null;
            
            ColonyBackup bak = new ColonyBackup();
            try { bak.fromJson(json); } catch(Exception ex) { ex.printStackTrace(); exc = ex; }
            security = json.get("security").toString().trim();
            json = null;
            
            if(security.equalsIgnoreCase("AES")) {
                colonies.clear();
                pnSecurity.setVisible(true);
                chkUseEnc.setVisible(false);
                tfPassword.setEditable(true);
            } else {
                if(exc != null) throw new RuntimeException(exc.getMessage(), exc);
                colonies.addAll(bak.getColonies());
            }
            
            tfName.setText(bak.getName());
            ta.setText(bak.getDescription());
            taDet.setText("저장일시 : " + new Date(bak.getCreated()));
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "오류 : " + ex.getMessage());
            dialog.setVisible(false);
        }
    }
    
    protected void onLoadCompleteRequested() {
        int sel = JOptionPane.showConfirmDialog(dialog, "이 백업을 복원하시겠습니까?\n기존 정착지들이 모두 사라지고 이 백업으로 대체됩니다 !", "확인", JOptionPane.YES_NO_OPTION);
        if(sel != JOptionPane.YES_OPTION) return;
        
        prog.setIndeterminate(true);
        handleSecurityOnLoading();
        
        superInstance.applyRestore(colonies, this, false);
        dialog.setVisible(false);
        colonies.clear();
    }
    
    protected void onLoadConcatRequested() {
        int sel = JOptionPane.showConfirmDialog(dialog, "이 백업을 복원하시겠습니까?\n기존 정착지들과 더불어 백업된 정착지들이 추가됩니다.\n단, 고유 키가 중복되는 정착지는 사라질 수 있습니다 !", "확인", JOptionPane.YES_NO_OPTION);
        if(sel != JOptionPane.YES_OPTION) return;
        
        prog.setIndeterminate(true);
        handleSecurityOnLoading();
        
        superInstance.applyRestore(colonies, this, true);
        dialog.setVisible(false);
        colonies.clear();
    }
    
    protected void handleSecurityOnLoading() {
        if(security.equalsIgnoreCase("AES")) { // 암호화된 경우 다시 불러와야 함
            try {
                File file = new File(tfFile.getText());
                String strJson = FileUtil.readString(file, "UTF-8", GZIPInputStream.class);
                JsonObject json = (JsonObject) JsonObject.parseJson(strJson);
                strJson = null;
                
                ColonyBackup bak = new ColonyBackup();
                bak.fromJson(json, new String(tfPassword.getPassword()));
                
                colonies = bak.getColonies();
            } catch(Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }
}
