package org.duckdns.hjow.samples.colonyman;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.samples.base.GUIProgram;
import org.duckdns.hjow.samples.base.SampleJavaCodes;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.CityPanel;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyPanel;
import org.duckdns.hjow.samples.colonyman.elements.Facility;
import org.duckdns.hjow.samples.colonyman.elements.facilities.PowerStation;
import org.duckdns.hjow.samples.colonyman.elements.facilities.Residence;
import org.duckdns.hjow.samples.colonyman.elements.facilities.Restaurant;
import org.duckdns.hjow.samples.util.ResourceUtil;
import org.duckdns.hjow.samples.util.UIUtil;

public class ColonyMan implements GUIProgram {
    private static final long serialVersionUID = -5740844908011980260L;
    protected transient SampleJavaCodes superInstance;
    protected transient JDialog dialog;
    protected transient Thread thread;
    protected transient volatile boolean threadSwitch, threadPaused, threadShutdown, reserveSaving;
    protected transient JButton btnThrPlay;
    
    protected transient Vector<Colony> colonies = new Vector<Colony>();
    protected transient volatile int selectedColony = -1;
    protected transient volatile int cycle = 0;
    
    protected transient JPanel pnCols;
    protected transient ColonyPanel cpNow;
    protected transient JComboBox<Colony> cbxColony;
    protected transient List<ColonyPanel> pnColonies = new Vector<ColonyPanel>();
    
    protected transient JProgressBar progThreadStatus;
    
    public ColonyMan(SampleJavaCodes superInstance) {
        super();
        this.superInstance = superInstance;
        
        threadSwitch = false;
        threadPaused = true;
        threadShutdown = true;
        reserveSaving = false;
        
        init(superInstance);
    }

    @Override
    public void init(SampleJavaCodes superInstance) {
        Window superDialog = superInstance.getWindow();
        
        // JDialog 설정
        if(     superDialog instanceof Frame ) dialog = new JDialog((Frame) superDialog);
        else if(superDialog instanceof Dialog) dialog = new JDialog((Dialog)superDialog);
        else dialog = new JDialog();
        
        dialog.setSize(800, 600);
        dialog.setTitle("Colony");
        dialog.setIconImage(UIUtil.iconToImage(getIcon()));
        dialog.setLayout(new BorderLayout());
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disposeContents();
            }
        });
        
        JPanel pnMain, pnSouth, pnCenter, pnNorth;
        pnMain   = new JPanel();
        pnSouth  = new JPanel();
        pnCenter = new JPanel();
        pnNorth  = new JPanel();
        
        dialog.add(pnMain, BorderLayout.CENTER);
        
        pnMain.setLayout(  new BorderLayout());
        pnSouth.setLayout( new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnNorth.setLayout( new BorderLayout());
        
        pnMain.add(pnSouth , BorderLayout.SOUTH);
        pnMain.add(pnCenter, BorderLayout.CENTER);
        pnMain.add(pnNorth , BorderLayout.NORTH);
        
        JToolBar toolbarNorth = new JToolBar();
        pnNorth.add(toolbarNorth, BorderLayout.NORTH);
        
        btnThrPlay = new JButton("▶");
        toolbarNorth.add(btnThrPlay);
        
        btnThrPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                threadPaused = (! threadPaused);
                if(threadPaused) {
                    btnThrPlay.setText("▶");
                } else {
                    reserveSaving = true;
                    btnThrPlay.setText("||");
                }
            }
        });
        
        progThreadStatus = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        toolbarNorth.add(progThreadStatus);
        
        pnCols  = new JPanel();
        JPanel pnArena = new JPanel();
        JPanel pnCtrl  = new JPanel();
        
        pnArena.setLayout( new BorderLayout());
        pnCtrl.setLayout( new BorderLayout());
        pnCols.setLayout( new BorderLayout());
        pnCenter.add(pnArena, BorderLayout.CENTER);
        
        cbxColony  = new JComboBox<Colony>();
        
        pnArena.add(pnCols, BorderLayout.CENTER);
        pnArena.add(pnCtrl , BorderLayout.NORTH);
        
        // pnCols.add(pnColonies, BorderLayout.CENTER);
        pnCtrl.add(cbxColony, BorderLayout.CENTER);
        
        cbxColony.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                selectedColony = cbxColony.getSelectedIndex();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        refreshColonyContent();
                    }
                });
            }
        });
        refreshColonyContent();
    }

    @Override
    public void onBeforeOpened(SampleJavaCodes superInstance) {
        if(thread != null) { try { threadSwitch = false; thread.interrupt(); Thread.sleep(1000L); } catch(Exception exc) {} }
        if(dialog == null) init(superInstance);
        
        loadColonies();
    }

    @Override
    public void onAfterOpened(SampleJavaCodes superInstance) {
        thread = new Thread(new Runnable() {    
            @Override
            public void run() {
                while(threadSwitch) {
                    threadShutdown = false;
                    
                    try { if(! threadPaused) oneSecond(); } catch(Exception ex) { ex.printStackTrace(); }
                    try { Thread.sleep(990L); } catch(InterruptedException e) { threadSwitch = false; break; }
                    if(reserveSaving) { try { saveColonies(); } catch(Exception ex) { ex.printStackTrace(); } reserveSaving = false; }
                    
                    threadShutdown = false;
                    progThreadStatus.setIndeterminate(! threadPaused);
                }
                threadShutdown = true;
                progThreadStatus.setIndeterminate(false);
                btnThrPlay.setEnabled(false);
            }
        });
        threadSwitch   = true;
        threadPaused   = true;
        threadShutdown = false;
        reserveSaving  = false;
        btnThrPlay.setText("▶");
        btnThrPlay.setEnabled(true);
        thread.start();
    }
    
    public void loadColonies() {
        File root = ResourceUtil.getHomeDir("samplejavacodes", "colony");
        File[] lists = root.listFiles(new FileFilter() {   
            @Override
            public boolean accept(File pathname) {
                if(pathname.isDirectory()) return false;
                return pathname.getName().toLowerCase().endsWith(".colony");
            }
        });
        
        colonies.clear();
        boolean exists = false;
        for(File f : lists) {
            try { 
                Colony c = new Colony(f);
                exists = false;
                for(Colony cx : colonies) { if(c.getName().equals(cx.getName())) exists = true; break; }
                if(exists) continue;
                
                c.setOriginalFileName(f.getName());
                colonies.add(c); 
            } catch(Exception ex) { ex.printStackTrace(); }
        }
        
        if(colonies.isEmpty()) {
            newColony();
        } else {
            refreshColonyList();
        }
    }
    
    public void saveColonies() {
        File root = ResourceUtil.getHomeDir("samplejavacodes", "colony");
        for(Colony c : colonies) {
            String name = c.getOriginalFileName();
            if(name == null) name = "col_" + c.getKey() + ".colony";
            
            File colFile = new File(root.getAbsolutePath() + File.separator + name);
            try { FileUtil.writeString(colFile, "UTF-8", c.toJson().toJSON()); } catch(Exception ex) { ex.printStackTrace(); }
        }
        
    }
    
    public void newColony() {
        Colony newCol = new Colony();
        
        City newCity = new City();
        newCol.getCities().add(newCity);
        
        Citizen citi;
        int idx;
        
        for(idx=0; idx<20; idx++) {
            citi = new Citizen();
            newCity.getCitizens().add(citi);
        }
        
        Facility fac;
        
        for(idx=0; idx<6; idx++) {
            fac = new Residence();
            ((Residence) fac).setComportGrade(0);
            newCity.getFacility().add(fac);
        }
        
        fac = new PowerStation();
        newCity.getFacility().add(fac);
        
        fac = new Restaurant();
        newCity.getFacility().add(fac);
        
        colonies.add(newCol);
        refreshColonyList();
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
        onBeforeOpened(superInstance);
        dialog.setVisible(true);
        onAfterOpened(superInstance);
    }

    @Override
    public boolean isHidden() {
        return false;
    }
    
    protected void waitThreadShutdown() {
        threadSwitch = false;
        int prevInfinites = 0;
        while(true) {
            if(threadShutdown) break;
            try { Thread.sleep(100L); } catch(Exception ex) {  }
            
            prevInfinites++;
            if(prevInfinites >= 100000) break;
        }
    }

    @Override
    public void dispose() {
        disposeContents();
        
        cpNow = null;
        for(ColonyPanel p : pnColonies) {
            p.dispose();
        }
        pnColonies.clear();
        colonies.clear();
        
        if(dialog != null) dialog.setVisible(false);
        dialog = null;
    }
    
    public void disposeContents() {
        threadSwitch = false;
        waitThreadShutdown();
        saveColonies();
    }

    @Override
    public void alert(String msg) {
        JOptionPane.showMessageDialog(getDialog(), msg);
    }

    @Override
    public Icon getIcon() {
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        Image img = UIUtil.iconToImage(icon);
        img = img.getScaledInstance(12, 12, Image.SCALE_SMOOTH);
        ImageIcon newIcon = new ImageIcon(img);
        
        return newIcon;
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
        
        try { col.oneSecond(cycle, null, col, 100); } catch(Exception ex) { ex.printStackTrace(); }
        try { refreshArenaPanel(cycle);             } catch(Exception ex) { ex.printStackTrace(); }
        
        cycle++;
        if(cycle >= 10000000) cycle = 0;
    }
    
    public void refreshColonyList() {
        cbxColony.setModel(new DefaultComboBoxModel<Colony>(colonies));
        selectedColony = cbxColony.getSelectedIndex();
        refreshColonyContent();
    }
    
    public void refreshColonyContent() {
        refreshArenaPanel(0);
    }
    
    public synchronized void refreshArenaPanel(int cycle) {
        Colony col = getColony();
        if(col == null) {
            pnCols.removeAll();
            return;
        }
        
        ColonyPanel colPn = null;
        for(ColonyPanel cp : pnColonies) {
            if(cp.getColony().getKey() == col.getKey()) {
                colPn = cp; break;
            }
        }
        
        if(colPn == null) {
            colPn = new ColonyPanel(col, this);
            pnColonies.add(colPn);
        }
        
        if(cpNow == null || cpNow != colPn) {
            pnCols.removeAll();
            cpNow = colPn;
            if(cpNow != null) pnCols.add(colPn, BorderLayout.CENTER);
        }
        
        if(cycle == 0 || cycle % 100 == 0) {
            colPn.refresh(cycle, null, col, this);
        }
    }
    
    public CityPanel getCityPanel(City city) {
        Colony col = getColony();
        ColonyPanel colPn = null;
        for(ColonyPanel cp : pnColonies) {
            if(cp.getColony().getKey() == col.getKey()) {
                colPn = cp; break;
            }
        }
        if(colPn == null) return null;
        return colPn.getCityPanel(city);
    }
    
    public static long generateKey() {
        Random rd = new Random();
        long key = rd.nextLong();
        while(key == 0L) key = rd.nextLong();
        return key;
    }
    
    public static int generateNaturalNumber() {
        return Math.abs(new Random().nextInt());
    }
}
