package org.duckdns.hjow.samples.svndirdel;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.duckdns.hjow.samples.base.Program;
import org.duckdns.hjow.samples.base.SampleJavaCodes;
import org.duckdns.hjow.samples.uicomponent.JLogArea;

public class SVNDirDelete implements Program {
    private static final long serialVersionUID = -792627049639601026L;
    protected JDialog dialog;
    protected JFileChooser fileChooser;
    protected JTextField tfRoot, tfStatus;
    protected JButton btnSel, btnRun, btnStop;
    protected JToolBar toolbar;
    
    protected JProgressBar prog;
    protected JLogArea taLog;
    
    protected List<File> listTargetDelete = new Vector<File>();
    protected volatile boolean flagStop = false;
    protected volatile int     sleepTiming = 0;
    
    protected volatile int     totals = 0;
    protected volatile int     counts = 0;
    
    public SVNDirDelete(SampleJavaCodes superInstance) {
        super();
        init(superInstance);
    }
    
    public void log(String str) {
        taLog.log(str);
    }
    
    public void open() {
        dialog.setVisible(true);
    }
    
    protected void runProcess() {
        try { 
            flagStop = false;
            sleepTiming = 0;
            listTargetDelete.clear();
            btnRun.setEnabled(false);
            btnSel.setEnabled(false);
            tfRoot.setEditable(false);
            btnStop.setEnabled(true);
            
            prog.setMaximum(100);
            prog.setValue(0);
            prog.setIndeterminate(true);
            
            log("Started."); 
            runProcess(new File(tfRoot.getText()));
        } catch(RuntimeException ex) {
            log("Error : " + ex.getMessage());
        } catch(Throwable ex) { 
            ex.printStackTrace();
            log("Error : " + ex.getMessage()); 
        } finally {
            log("END");
            
            prog.setMaximum(100);
            prog.setValue(0);
            prog.setIndeterminate(false);
            btnRun.setEnabled(true);
            btnSel.setEnabled(true);
            btnStop.setEnabled(false);
            tfRoot.setEditable(true);
            
            flagStop = false;
            sleepTiming = 0;
        }
    }
    
    protected void finishJob() {
        flagStop = true;
    }
    
    protected void refreshStatus() {
        tfStatus.setText("Needs to delete : " + totals + ", Finished : " + counts);
    }
    
    public void runProcess(File root) {
        if(root == null) throw new NullPointerException("Please input a root directory !");
        if(! root.exists()) throw new RuntimeException("Please input existing directory !");
        if(! root.isDirectory()) throw new RuntimeException("Please input a directory !");
        
        // 사전준비
        
        listTargetDelete.clear();
        sleepTiming = 0;
        totals = 0;
        counts = 0;
        // 목록 만들기
        scan(root);
        
        totals = listTargetDelete.size();
        prog.setIndeterminate(false);
        prog.setMaximum(totals + 3);
        refreshStatus();
        
        int nows = 1;
        prog.setValue(nows);
        
        // 집행
        for(File fx : listTargetDelete) {
            deletes(fx); 
            
            nows++;
            prog.setValue(nows);
        }
        
        prog.setValue(totals + 3);
    }
    
    protected void scan(File root) {
        if(flagStop) throw new RuntimeException("Job stopped.");
        
        sleepTiming++;
        if(sleepTiming % 10 == 0) { try { Thread.sleep(20L); } catch(InterruptedException ex) { throw new RuntimeException("Job interrupted."); } sleepTiming = 0; }
        
        log("SCAN " + root.getAbsolutePath());
        
        if(root.isDirectory()) {
            if(root.getName().equals(".svn")) {
                registerDelTarget(root);
            }
            
            File[] files = root.listFiles();
            for(File f : files) { scan(f); }
        }
    }
    
    protected void registerDelTarget(File root) {
        if(flagStop) throw new RuntimeException("Job stopped.");
        
        sleepTiming++;
        if(sleepTiming % 10 == 0) { try { Thread.sleep(20L); } catch(InterruptedException ex) { throw new RuntimeException("Job interrupted."); } sleepTiming = 0; }
        
        log("REG " + root.getAbsolutePath());
        
        if(! root.isDirectory()) { 
            if(! listTargetDelete.contains(root)) { listTargetDelete.add(root); totals++; if(totals % 10 == 0) refreshStatus(); } 
            return; 
        }
        
        File[] files = root.listFiles();
        for(File f : files) { registerDelTarget(f); }
        if(! listTargetDelete.contains(root)) { listTargetDelete.add(root); totals++; if(totals % 10 == 0) refreshStatus(); }
    }
    
    protected void deletes(File root) {
        if(flagStop) throw new RuntimeException("Job stopped.");
        
        sleepTiming++;
        if(sleepTiming % 10 == 0) { try { Thread.sleep(20L); } catch(InterruptedException ex) { throw new RuntimeException("Job interrupted."); } sleepTiming = 0; }
        
        if(! root.isDirectory()) {
            log("DEL " + root.getAbsolutePath());
            root.delete();
            counts++;
            if(counts % 10 == 0) refreshStatus();
            return;
        }
        
        File[] lists = root.listFiles();
        for(File f : lists) {
            deletes(f);
        }
        
        log("DEL " + root.getAbsolutePath());
        root.delete();
        counts++;
        if(counts % 10 == 0)refreshStatus();
    }

    @Override
    public void dispose() {
        if(dialog != null) dialog.setVisible(false);
        dialog = null;
    }

    @Override
    public void init(SampleJavaCodes superInstance) {
        if(dialog != null) dispose();
        
        Window superDialog = superInstance.getWindow();
        
        if(     superDialog instanceof Frame ) dialog = new JDialog((Frame) superDialog);
        else if(superDialog instanceof Dialog) dialog = new JDialog((Dialog)superDialog);
        else dialog = new JDialog();
        dialog.setSize(700, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setTitle("SVN Directory Delete");
        
        JPanel pnMain, pnCenter, pnNorth, pnSouth, pn;
        
        pnMain   = new JPanel();
        pnNorth  = new JPanel();
        pnCenter = new JPanel();
        pnSouth  = new JPanel();
        pnMain.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnNorth.setLayout(new BorderLayout());
        pnSouth.setLayout(new BorderLayout());
        
        dialog.add(pnMain, BorderLayout.CENTER);
        
        pnMain.add(pnNorth, BorderLayout.NORTH);
        pnMain.add(pnCenter, BorderLayout.CENTER);
        pnMain.add(pnSouth, BorderLayout.SOUTH);
        
        pn = new JPanel();
        pn.setLayout(new BorderLayout());
        pnNorth.add(pn, BorderLayout.CENTER);
        
        taLog = new JLogArea();
        pnCenter.add(taLog, BorderLayout.CENTER);
        
        tfRoot = new JTextField(20);
        pn.add(tfRoot, BorderLayout.CENTER);
        
        btnSel = new JButton("...");
        pn.add(btnSel, BorderLayout.EAST);
        
        fileChooser = new JFileChooser();
        
        toolbar = new JToolBar();
        pnNorth.add(toolbar, BorderLayout.NORTH);
        btnSel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {       
                    @Override
                    public void run() {
                        int sel = fileChooser.showOpenDialog(dialog);
                        if(sel != JFileChooser.APPROVE_OPTION) return;
                        File f = fileChooser.getSelectedFile();
                        
                        tfRoot.setText(f.getAbsolutePath());
                    }
                });
            }
        });
        
        btnRun = new JButton("▶");
        toolbar.add(btnRun);
        
        btnRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {       
                    @Override
                    public void run() {
                        new Thread(new Runnable() {   
                            @Override
                            public void run() {
                                runProcess();
                            }
                        }).start();
                    }
                });
            }
        });
        
        btnStop = new JButton("■");
        btnStop.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                flagStop = true;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        btnStop.setEnabled(false);
                    }
                });
            }
        });
        btnStop.setEnabled(false);
        toolbar.add(btnStop);
        
        prog = new JProgressBar();
        pnCenter.add(prog, BorderLayout.NORTH);
        
        tfStatus = new JTextField();
        tfStatus.setEditable(false);
        pnSouth.add(tfStatus, BorderLayout.CENTER);
    }

    @Override
    public void onBeforeOpened(SampleJavaCodes superInstance) { }

    @Override
    public void onAfterOpened(SampleJavaCodes superInstance) { }

    @Override
    public String getTitle() {
        return "SVN Directory Delete";
    }

    @Override
    public void alert(String msg) {
        JOptionPane.showMessageDialog(getDialog(), msg);
    }

    @Override
    public String getName() {
        return "svndel";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public JDialog getDialog() {
        return dialog;
    }

    @Override
    public void open(SampleJavaCodes superInstance) {
        dialog.setVisible(true);
    }

    @Override
    public boolean isHidden() {
        return false;
    }
}
