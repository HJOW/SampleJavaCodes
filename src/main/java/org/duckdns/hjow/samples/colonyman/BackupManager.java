package org.duckdns.hjow.samples.colonyman;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyBackup;
import org.duckdns.hjow.samples.interfaces.Disposeable;
import org.duckdns.hjow.samples.util.UIUtil;

public class BackupManager implements Disposeable {
    protected JDialog dialog;
    protected JTextField tfName, tfFile;
    protected JTextArea  ta;
    protected JButton    btnSave, btnClose, btnSelFile;
    protected JProgressBar prog;
    protected JFileChooser backupChooser;
    
    protected transient boolean saveMode = true;
    protected transient ColonyManager superInstance = null;
    protected transient List<Colony> colonies = new ArrayList<Colony>();
    
    public BackupManager(ColonyManager superInstance) {
        this.superInstance = superInstance;
        
        dialog = new JDialog(superInstance.getDialog());
        dialog.setTitle("백업");
        dialog.setSize(400, 300);
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
        
        JPanel pnCtrl = new JPanel();
        pnCtrl.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnDown.add(pnCtrl, BorderLayout.CENTER);
        
        tfFile = new JTextField(15);
        btnSelFile = new JButton("...");
        btnSave = new JButton("저장");
        btnClose = new JButton("취소");
        
        pnCtrl.add(tfFile);
        pnCtrl.add(btnSelFile);
        pnCtrl.add(btnSave);
        pnCtrl.add(btnClose);
        
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
        
        btnSave.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {   
                    @Override
                    public void run() {
                        if(saveMode) onSaveRequested();
                        else         onLoadCompleteRequested();
                        prog.setIndeterminate(false);
                    }
                }).start(); 
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
        
        tfFile.setText("");
        tfFile.setEditable(false);
        btnSelFile.setVisible(false);
        btnSave.setText("복원");
        tfName.setText("");
        ta.setText("");
        tfName.setEditable(false);
        ta.setEditable(false);
        saveMode = false;
        
        int sel = backupChooser.showOpenDialog(dialog);
        if(sel != JFileChooser.APPROVE_OPTION) { dialog.setVisible(false); return; }
        
        File file = backupChooser.getSelectedFile();
        tfFile.setText(file.getAbsolutePath());
        
        prog.setIndeterminate(true);
        btnSave.setEnabled(false);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                onLoadRequested();
                prog.setIndeterminate(false);
                btnSave.setEnabled(true);
            }
        }).start();
        
        try { dialog.setVisible(true); } catch(Exception ex) {  }
    }
    
    public void openSave(List<Colony> colonies) {
        this.colonies.clear();
        this.colonies.addAll(colonies);
        
        tfFile.setText("");
        tfFile.setEditable(true);
        btnSelFile.setVisible(true);
        btnSave.setText("저장");
        tfName.setEditable(true);
        ta.setEditable(true);
        tfName.setText("이 곳에 백업의 이름을 입력해 주세요.");
        ta.setText("이 곳에 백업의 설명을 입력해 주세요.");
        saveMode = true;
        
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
        
        try {
            File file = new File(strFile);
            FileUtil.writeString(file, "UTF-8", bak.toJson().toJSON(), GZIPOutputStream.class);
            
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
            
            ColonyBackup bak = new ColonyBackup();
            bak.fromJson(json);
            json = null;
            
            colonies.addAll(bak.getColonies());
            tfName.setText(bak.getName());
            ta.setText(bak.getDescription());
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
        
        superInstance.applyRestore(colonies, this);
        dialog.setVisible(false);
        colonies.clear();
    }
}
