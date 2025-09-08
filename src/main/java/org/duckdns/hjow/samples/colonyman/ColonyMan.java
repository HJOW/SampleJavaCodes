package org.duckdns.hjow.samples.colonyman;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.FileFilter;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.duckdns.hjow.samples.base.GUIProgram;
import org.duckdns.hjow.samples.base.SampleJavaCodes;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.util.ResourceUtil;
import org.duckdns.hjow.samples.util.UIUtil;

public class ColonyMan implements GUIProgram {
    private static final long serialVersionUID = -5740844908011980260L;
    protected SampleJavaCodes superInstance;
    protected JDialog dialog;
    protected Thread thread;
    protected volatile boolean threadSwitch = false;
    
    protected Vector<Colony> colonies = new Vector<Colony>();
    protected volatile int selectedColony = -1;
    protected volatile int cycle = 0;
    
    protected JComboBox<Colony> cbxColony;
    protected JPanel pnColonies;
    
    public ColonyMan(SampleJavaCodes superInstance) {
        this.superInstance = superInstance;
        init(superInstance);
    }

    @Override
    public void init(SampleJavaCodes superInstance) {
        Window superDialog = superInstance.getWindow();
        
        // JDialog 설정
        if(     superDialog instanceof Frame ) dialog = new JDialog((Frame) superDialog);
        else if(superDialog instanceof Dialog) dialog = new JDialog((Dialog)superDialog);
        else dialog = new JDialog();
        
        dialog.setSize(600, 400);
        dialog.setTitle("Colony");
        dialog.setIconImage(UIUtil.iconToImage(getIcon()));
        dialog.setLayout(new BorderLayout());
        
        JPanel pnMain, pnSouth, PnCenter, pnNorth;
        pnMain   = new JPanel();
        pnSouth  = new JPanel();
        PnCenter = new JPanel();
        pnNorth  = new JPanel();
        
        dialog.add(pnMain, BorderLayout.CENTER);
        
        pnMain.setLayout(  new BorderLayout());
        pnSouth.setLayout( new BorderLayout());
        PnCenter.setLayout(new BorderLayout());
        pnNorth.setLayout( new BorderLayout());
        
        pnMain.add(pnSouth , BorderLayout.SOUTH);
        pnMain.add(PnCenter, BorderLayout.CENTER);
        pnMain.add(pnNorth , BorderLayout.NORTH);
        
        JPanel pnArena = new JPanel();
        JPanel pnCtrl  = new JPanel();
        pnArena.setLayout( new BorderLayout());
        pnCtrl.setLayout( new BorderLayout());
        PnCenter.add(pnArena, BorderLayout.CENTER);
        
        pnColonies = new JPanel();
        cbxColony  = new JComboBox<Colony>();
        
        pnArena.add(pnColonies, BorderLayout.CENTER);
        pnArena.add(pnCtrl , BorderLayout.NORTH);
        
        pnCtrl.add(cbxColony, BorderLayout.CENTER);
    }

    @Override
    public void onBeforeOpened(SampleJavaCodes superInstance) {
        if(thread != null) { try { threadSwitch = false; thread.interrupt(); Thread.sleep(1000L); } catch(Exception exc) {} }
        
        File root = ResourceUtil.getHomeDir("samplejavacodes", "colony");
        File[] lists = root.listFiles(new FileFilter() {   
            @Override
            public boolean accept(File pathname) {
                if(pathname.isDirectory()) return false;
                return pathname.getName().toLowerCase().endsWith(".colony");
            }
        });
        
        colonies.clear();
        for(File f : lists) {
            colonies.add(new Colony(f));
        }
        cbxColony.setModel(new DefaultComboBoxModel<Colony>(colonies));
    }

    @Override
    public void onAfterOpened(SampleJavaCodes superInstance) {
        thread = new Thread(new Runnable() {    
            @Override
            public void run() {
                while(threadSwitch) {
                    try { oneSecond(); } catch(Exception ex) { ex.printStackTrace(); }
                    try { Thread.sleep(990L); } catch(InterruptedException e) { threadSwitch = false; break; }
                }
            }
        });
        threadSwitch = true;
        thread.start();
    }

    @Override
    public String getTitle() {
        return "Colony";
    }

    @Override
    public String getName() {
        return "colony";
    }

    @Override
    public void log(String msg) {
        System.out.println(msg);
    }

    @Override
    public void open(SampleJavaCodes superInstance) {
        dialog.setVisible(true);
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void dispose() {
        threadSwitch = false;
        if(dialog != null) dialog.setVisible(false);
    }

    @Override
    public void alert(String msg) {
        JOptionPane.showMessageDialog(getDialog(), msg);
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public JDialog getDialog() {
        return dialog;
    }
    
    public Colony getColony() {
        if(selectedColony < 0) return null;
        if(selectedColony >= colonies.size()) { selectedColony = 0; return null; }
        return colonies.get(selectedColony);
    }
    
    public void oneSecond() {
        Colony col = getColony();
        if(col == null) return;
        
        col.oneSecond(cycle, null, col);
        cycle++;
        if(cycle >= 10000000) cycle = 0;
    }
}
