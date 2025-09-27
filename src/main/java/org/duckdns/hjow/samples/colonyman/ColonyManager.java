package org.duckdns.hjow.samples.colonyman;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.duckdns.hjow.samples.base.GUIProgram;
import org.duckdns.hjow.samples.base.SampleJavaCodes;
import org.duckdns.hjow.samples.colonyman.elements.AttackableObject;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.CityPanel;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;
import org.duckdns.hjow.samples.colonyman.elements.ColonyPanel;
import org.duckdns.hjow.samples.util.ResourceUtil;
import org.duckdns.hjow.samples.util.UIUtil;

/** Colonization 프로그램 */
public class ColonyManager implements GUIProgram, ColonyManagerUI {
    private static final long serialVersionUID = -5740844908011980260L;
    protected transient SampleJavaCodes superInstance;
    protected transient Thread thread;
    protected transient volatile boolean threadSwitch, threadPaused, threadShutdown, reserveSaving, reserveRefresh;
    protected transient volatile boolean bCheckerPauseCompleted = false;
    
    protected transient JDialog dialog;
    protected transient JPanel pnMain;
    protected transient CardLayout cardMain;
    protected transient JButton btnSaveAs, btnLoadAs, btnThrPlay;
    
    protected transient Vector<Colony> colonies = new Vector<Colony>();
    protected transient volatile int selectedColony = -1;
    protected transient volatile int cycle = 0;
    protected transient volatile long cycleGap = 99L;
    protected transient long cycleRunningTime = 0L;
    
    protected transient JPanel pnCols, pnNoColonies;
    protected transient ColonyPanel cpNow;
    protected transient JComboBox<Colony> cbxColony;
    protected transient List<ColonyPanel> pnColonies = new Vector<ColonyPanel>();
    
    protected transient JProgressBar progThreadStatus;
    protected transient JLabel lbRunningTime;
    
    protected transient JFileChooser fileChooser;
    protected transient javax.swing.filechooser.FileFilter filterCol, filterColGz;
    
    protected transient BackupManager backupManager;
    
    protected transient JMenuBar menuBar;
    protected transient JMenu menuFile, menuAction;
    protected transient JMenuItem menuActionThrPlay, menuFileSave, menuFileLoad, menuFileBackup, menuFileRestore, menuFileReset, menuFileNew, menuFileDel;
    
    protected transient boolean flagSaveBeforeClose = true; // 종료 시 저장 플래그
    
    public ColonyManager(SampleJavaCodes superInstance) {
        super();
        this.superInstance = superInstance;
        
        threadSwitch = false;
        threadPaused = true;
        threadShutdown = true;
        reserveSaving = false;
        reserveRefresh = false;
        
        init(superInstance);
    }

    @Override
    public void init(SampleJavaCodes superInstance) {
        Window superDialog = superInstance.getWindow();
        
        // JDialog 설정
        if(     superDialog instanceof Frame ) dialog = new JDialog((Frame) superDialog);
        else if(superDialog instanceof Dialog) dialog = new JDialog((Dialog)superDialog);
        else dialog = new JDialog();
        
        Dimension winSize = UIUtil.getScreenSize();
        int w, h;
        w = (int) (winSize.getWidth()  * 0.8);
        h = (int) (winSize.getHeight() * 0.8);
        
        if(w >= winSize.getWidth()  - 50) w = (int) (winSize.getWidth()  - 50);
        if(h >= winSize.getHeight() - 50) h = (int) (winSize.getHeight() - 80);
        
        if(w < 800) w = 800;
        if(h < 600) h = 600;
        
        dialog.setSize(w, h);
        UIUtil.center(dialog);
        dialog.setTitle("Colonization");
        dialog.setIconImage(UIUtil.iconToImage(getIcon()));
        dialog.setLayout(new BorderLayout());
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosing();
            }
        });
        dialog.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refreshArenaPanel(0);
            }
        });
        
        if(fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            filterCol = new javax.swing.filechooser.FileFilter() {
                @Override
                public String getDescription() {
                    return "정착지 파일 (*.colony)";
                }
                
                @Override
                public boolean accept(File f) {
                    if(f == null) return false;
                    if(f.isDirectory()) return false;
                    
                    return f.getName().toLowerCase().endsWith(".colony");
                }
            };
            filterColGz = new javax.swing.filechooser.FileFilter() {
                @Override
                public String getDescription() {
                    return "정착지 GZ 압축형 파일 (*.colgz)";
                }
                
                @Override
                public boolean accept(File f) {
                    if(f == null) return false;
                    if(f.isDirectory()) return false;
                    
                    return f.getName().toLowerCase().endsWith(".colgz");
                }
            };
            fileChooser.addChoosableFileFilter(filterCol);
            fileChooser.addChoosableFileFilter(filterColGz);
        }
        
        JPanel pnMainCard1, pnMainCard2;
        pnMain      = new JPanel();
        pnMainCard1 = new JPanel();
        pnMainCard2 = new JPanel();
        
        dialog.add(pnMain, BorderLayout.CENTER);
        
        cardMain = new CardLayout();
        pnMain.setLayout(cardMain);
        
        pnMain.add(pnMainCard1, "C1");
        pnMain.add(pnMainCard2, "C2");
        
        pnMainCard1.setLayout(new BorderLayout());
        pnMainCard2.setLayout(new BorderLayout());
        
        JPanel pnHide = new JPanel();
        pnMainCard2.add(pnHide, BorderLayout.CENTER);
        pnMainCard2.add(new JPanel(), BorderLayout.NORTH);
        pnMainCard2.add(new JPanel(), BorderLayout.SOUTH);
        
        pnHide.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JProgressBar progHide = new JProgressBar();
        progHide.setIndeterminate(true);
        pnHide.add(progHide);
        
        JPanel pnSouth, pnCenter, pnNorth;
        pnSouth  = new JPanel();
        pnCenter = new JPanel();
        pnNorth  = new JPanel();
        
        pnSouth.setLayout( new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnNorth.setLayout( new BorderLayout());
        
        pnMainCard1.add(pnSouth , BorderLayout.SOUTH);
        pnMainCard1.add(pnCenter, BorderLayout.CENTER);
        pnMainCard1.add(pnNorth , BorderLayout.NORTH);
        
        JToolBar toolbarNorth = new JToolBar();
        pnNorth.add(toolbarNorth, BorderLayout.NORTH);
        
        btnSaveAs = new JButton(UIManager.getIcon("FileView.floppyDriveIcon"));
        toolbarNorth.add(btnSaveAs);
        btnSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveRequested();
            }
        });
        
        btnLoadAs = new JButton(UIManager.getIcon("FileView.directoryIcon"));
        toolbarNorth.add(btnLoadAs);
        btnLoadAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLoadRequested();
            }
        });
        
        cbxColony  = new JComboBox<Colony>();
        toolbarNorth.add(cbxColony);
        
        cbxColony.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cardMain.show(pnMain, "C2");
                SwingUtilities.invokeLater(new Runnable() {   
                    @Override
                    public void run() {
                        refreshColonyContent();
                        cardMain.show(pnMain, "C1");
                    }
                });
            }
        });

        Vector<String> strSpeeds = new Vector<String>();
        strSpeeds.add("×1");
        strSpeeds.add("×2");
        final int speedsCount = strSpeeds.size();
        JComboBox<String> cbxSpeed = new JComboBox<String>(strSpeeds);
        cbxSpeed.setSelectedIndex(0);
        toolbarNorth.add(cbxSpeed);

        cbxSpeed.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int sel = cbxSpeed.getSelectedIndex();
                if(sel >= speedsCount) sel = speedsCount -1;
                if(sel < 0) sel = 0;
                
                if(     sel == 0) cycleGap = 99L;
                else if(sel == 1) cycleGap = 49L;
                else cycleGap = 99L;
            }
        });
        
        btnThrPlay = new JButton("시뮬레이션 시작");
        toolbarNorth.add(btnThrPlay);
        
        btnThrPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSimulationRunning();
            }
        });
        
        progThreadStatus = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        toolbarNorth.add(progThreadStatus);
        
        lbRunningTime = new JLabel();
        toolbarNorth.add(lbRunningTime);
        
        lbRunningTime.setVisible(false);
        
        pnCols       = new JPanel();
        pnNoColonies = new JPanel();
        
        JPanel pnArena = new JPanel();
        JPanel pnCtrl  = new JPanel();
        
        pnArena.setLayout( new BorderLayout());
        pnCtrl.setLayout( new BorderLayout());
        pnCols.setLayout( new BorderLayout());
        pnNoColonies.setLayout( new BorderLayout());
        pnCenter.add(pnArena, BorderLayout.CENTER);
        
        pnArena.add(pnCols, BorderLayout.CENTER);
        pnArena.add(pnCtrl , BorderLayout.NORTH);
        
        pnNoColonies.add(new JPanel(), BorderLayout.NORTH);
        pnNoColonies.add(new JPanel(), BorderLayout.SOUTH);
        pnNoColonies.add(new JPanel(), BorderLayout.EAST);
        pnNoColonies.add(new JPanel(), BorderLayout.WEST);
        
        JPanel pnNoColMain = new JPanel();
        pnNoColMain.setLayout(new BorderLayout());
        pnNoColonies.add(pnNoColMain, BorderLayout.CENTER);
        
        JPanel pnNoColCenter, pnNoColSouth;
        pnNoColCenter = new JPanel();
        pnNoColSouth  = new JPanel();
        pnNoColCenter.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnNoColSouth.setLayout( new FlowLayout(FlowLayout.CENTER));
        pnNoColMain.add(pnNoColCenter, BorderLayout.CENTER);
        pnNoColMain.add(pnNoColSouth , BorderLayout.SOUTH);
        
        JButton btnNewCol = new JButton("새 정착지");
        btnNewCol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardMain.show(pnMain, "C2");
                onNewRequested();
                cardMain.show(pnMain, "C1");
            }
        });
        pnNoColCenter.add(btnNewCol);
        
        menuBar = new JMenuBar();
        dialog.setJMenuBar(menuBar);
        
        JMenuItem menuItem;
        
        menuFile = new JMenu("파일");
        menuBar.add(menuFile);
        
        menuFileNew = new JMenuItem("새 정착지 생성");
        menuFile.add(menuFileNew);
        menuFileNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewRequested();
            }
        });
        
        menuFileDel = new JMenuItem("이 정착지 삭제");
        menuFile.add(menuFileDel);
        menuFileDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteThisRequested();
            }
        });
        
        menuFile.addSeparator();
        
        menuFileSave = new JMenuItem("다른 이름으로 이 정착지 저장");
        menuFile.add(menuFileSave);
        menuFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        menuFileSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveRequested();
            }
        });
        
        menuFileLoad = new JMenuItem("외부 정착지 파일 불러오기");
        menuFile.add(menuFileLoad);
        menuFileLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK));
        menuFileLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLoadRequested();
            }
        });
        
        menuFile.addSeparator();

        menuFileBackup = new JMenuItem("백업");
        menuFile.add(menuFileBackup);
        menuFileBackup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK));
        menuFileBackup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBackupRequested();
            }
        });
        
        menuFileRestore = new JMenuItem("복원");
        menuFile.add(menuFileRestore);
        menuFileRestore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRestoreRequested();
            }
        });
        
        menuFileReset = new JMenuItem("정착지 모두 포기 (초기화)");
        menuFile.add(menuFileReset);
        menuFileReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onResetAllRequested();
            }
        });
        
        menuFile.addSeparator();
        
        menuItem = new JMenuItem("종료");
        menuFile.add(menuItem);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flagSaveBeforeClose = false;
                dispose(true);
            }
        });
        
        menuAction = new JMenu("동작");
        menuBar.add(menuAction);
        
        menuActionThrPlay = new JMenuItem("시뮬레이션 시작");
        menuAction.add(menuActionThrPlay);
        menuActionThrPlay.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
        menuActionThrPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSimulationRunning();
            }
        });
        
        backupManager = new BackupManager(this);
        
        refreshColonyContent();
        cardMain.show(pnMain, "C2");
    }

    @Override
    public void onBeforeOpened(SampleJavaCodes superInstance) {
        if(thread != null) { try { threadSwitch = false; thread.interrupt(); Thread.sleep(1000L); } catch(Exception exc) {} }
        if(dialog == null) init(superInstance);
        
        loadColonies();
    }

    @Override
    public void onAfterOpened(SampleJavaCodes superInstance) {
        btnThrPlay.setEnabled(false);
        menuActionThrPlay.setEnabled(false);
        
        threadPaused = true;
        reserveSaving  = false;
        reserveRefresh = false;
        
        assureMainThreadRunning();
        
        btnThrPlay.setText("시뮬레이션 시작");
        btnThrPlay.setEnabled(true);
        menuActionThrPlay.setEnabled(true);
        
        setEditable(true);
        
        for(ColonyPanel pns : pnColonies) {
            pns.setDividerLocationOnlyFirst();
        }
    }
    
    /** 메인 쓰레드 구동 중인지 확인하여, 미구동 중인 경우 구동 시작 */
    public void assureMainThreadRunning() {
        if(thread == null || (! threadSwitch)) turnOnMainThread();
    }
    
    /** 메인 쓰레드 실행 */
    protected void turnOnMainThread() {
        if(thread != null) {
            thread.interrupt();
            try { Thread.sleep(1000L); } catch(InterruptedException ex) { ex.printStackTrace(); }
        }
        thread = new Thread(new Runnable() {    
            @Override
            public void run() {
                while(threadSwitch) {
                    if(! onMainThread()) break;
                }
                threadShutdown = true;
                progThreadStatus.setIndeterminate(false);
                btnThrPlay.setEnabled(false);
                menuActionThrPlay.setEnabled(false);
            }
        });
        threadSwitch   = true;
        threadShutdown = false;
        flagSaveBeforeClose = true;
        thread.start();
    }
    
    /** 메인 쓰레드 동작 */
    protected boolean onMainThread() {
        threadShutdown = false;
        long elapsed = System.currentTimeMillis();
        
        // 쓰레드에서 수행할 실질 작업 수행
        try { if(! threadPaused) { bCheckerPauseCompleted = false; oneCycle(); } } catch(Exception ex) { ex.printStackTrace(); }
        
        // 저장 요청 수행
        if(reserveSaving) { try { saveColonies(); } catch(Exception ex) { ex.printStackTrace(); } reserveSaving = false; }
        
        // 리프레시 요청 수행
        if(reserveRefresh) {
            try {
                SwingUtilities.invokeLater(new Runnable() {   
                    @Override
                    public void run() {
                        refreshColonyContent();
                    }
                });
            } catch(Exception ex) { ex.printStackTrace(); }
            reserveRefresh = false;
        }
        
        cycleRunningTime = System.currentTimeMillis() - elapsed;
        lbRunningTime.setText("  " + String.valueOf(cycleRunningTime) + " ms");
        
        // 일시정지 후 쓰레드가 실제 정지 중인지 판단하는 플래그
        if(threadPaused) bCheckerPauseCompleted = true;
        else bCheckerPauseCompleted = false;
        
        // 쓰레드 Sleep
        try { Thread.sleep(cycleGap); } catch(InterruptedException e) { threadSwitch = false; return false; }
        
        threadShutdown = false;
        progThreadStatus.setIndeterminate(! threadPaused);
        
        return true;
    }
    
    /** 정착지 생성 요청 시 호출됨 */
    protected void onNewRequested() {
        Colony newCol = newColony();
        cbxColony.setSelectedItem(newCol);
        
        cardMain.show(pnMain, "C2");
        SwingUtilities.invokeLater(new Runnable() {   
            @Override
            public void run() {
                refreshColonyContent();
                cardMain.show(pnMain, "C1");
            }
        });
        
    }
    
    /** 현재의 정착지 삭제 요청 시 호출됨 */
    protected void onDeleteThisRequested() {
        Colony col = getColony();
        int sel = JOptionPane.showConfirmDialog(getDialog(), "정착지 " + col.getName() + "을/를 포기하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
        if(sel != JOptionPane.YES_OPTION) return;
        
        cardMain.show(pnMain, "C2");
        
        // 리스트에서 삭제
        int idx = 0;
        while(idx < colonies.size()) {
            if(colonies.get(idx).getKey() == col.getKey()) {
                colonies.remove(idx);
            }
            idx++;
        }
        
        // 파일도 삭제
        File root = getColonySaveRootDirectory();
        File[] lists = root.listFiles(getColonyFileFilter());
        for(File f : lists) {
            try {
                Colony temp = new Colony(f);
                if(temp.getKey() == col.getKey()) f.delete();
            } catch(Exception ex) {} // 오류 건너뛰기
        }
        
        // 정착지 목록이 비어 있으면 생성
        if(colonies.isEmpty()) newColony();
        
        // 새로 고침
        refreshColonyList();
        cardMain.show(pnMain, "C1");
    }
    
    /** 정착지 하나를 별도 파일로 저장 요청 시 호출됨 */
    protected void onSaveRequested() {
        Colony c = getSelectedColony();
        if(c == null) { alert("저장할 정착지를 선택해 주세요."); return; }
        
        int s = fileChooser.showSaveDialog(getDialog());
        if(s == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            saveColony(c, f, true);
        }
    }
    
    /** 정착지 파일 불러오기 요청 시 호출됨 */
    protected void onLoadRequested() {
        int s = fileChooser.showOpenDialog(getDialog());
        if(s == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            loadColony(f, true);
            refreshColonyList();
        }
    }

    /** 정착지 전체 백업 요청 시 호출됨 */
    protected void onBackupRequested() {
        backupManager.openSave(colonies);
    }

    /** 정착지 복원 요청 시 호출됨 */
    protected void onRestoreRequested() {
        backupManager.openLoad();
    }
    
    /** 정착지 세이브 모두 초기화 요청 시 호출됨 */
    protected void onResetAllRequested() {
        int sel = JOptionPane.showConfirmDialog(getDialog(), "정착지들을 모두 포기하시겠습니까?\n별도로 저장하지 않은 모든 정착지가 사라집니다 !", "확인", JOptionPane.YES_NO_OPTION);
        if(sel == JOptionPane.YES_OPTION) {
            cardMain.show(pnMain, "C2");
            pauseSimulation();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    resetAllColony();
                    refreshArenaPanel(0);
                    cardMain.show(pnMain, "C1");
                }
            }).start();
        }
    }
    
    /** 백업 복원 받기 */
    public void applyRestore(List<Colony> colonies, BackupManager backupMan, boolean concat) {
        if(backupManager != backupMan) return;
        
        cardMain.show(pnMain, "C2");
        
        // 파일 다 지워야 함
        File root = getColonySaveRootDirectory();
        File[] lists = root.listFiles(getColonyFileFilter());
        for(File f : lists) {
            f.delete();
        }
        
        // 복원 처리
        if(concat) { // 병합
            List<Colony> temp = new ArrayList<Colony>();
            temp.addAll(this.colonies);
            
            this.colonies.clear();
            
            for(Colony c : temp) {
                boolean dupl = false;
                for(Colony alreadyIn : this.colonies) {
                    if(c.getKey() == alreadyIn.getKey()) { dupl = true; break; }
                }
                if(dupl) continue;
                this.colonies.add(c);
            }
            
            for(Colony c : colonies) {
                boolean dupl = false;
                for(Colony alreadyIn : this.colonies) {
                    if(c.getKey() == alreadyIn.getKey()) { dupl = true; break; }
                }
                if(dupl) continue;
                this.colonies.add(c);
            }
        } else { // 대체
            this.colonies.clear();
            this.colonies.addAll(colonies);
        }
        
        reserveSaving = true; // 저장 예약
        refreshColonyList();  // 목록 갱신
        
        cardMain.show(pnMain, "C1");
    }
    
    /** 정착지 세이브 파일 필터 생성 */
    public FileFilter getColonyFileFilter() {
        return new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.isDirectory()) return false;
                String nameLower = pathname.getName().toLowerCase();
                return nameLower.endsWith(".colony") || nameLower.endsWith(".colgz");
            }
        };
    }
    
    /** 정착지 세이브 기본 경로 반환 */
    public File getColonySaveRootDirectory() {
        return ResourceUtil.getHomeDir("samplejavacodes", "colony");
    }
    
    /** 정착지들을 기본 경로에서 불러오기 */
    public void loadColonies() {
        File root = getColonySaveRootDirectory();
        File[] lists = root.listFiles(getColonyFileFilter());
        
        colonies.clear();
        for(File f : lists) {
            if(filterCol.accept(f)) loadColony(f, false);
        }
        
        if(colonies.isEmpty()) {
            newColony();
        } else {
            refreshColonyList();
        }
    }
    
    /** 정착지 모두 포기, 초기화 */
    protected void resetAllColony() {
        colonies.clear();
        File root = getColonySaveRootDirectory();
        File[] lists = root.listFiles(getColonyFileFilter());
        for(File f : lists) {
            f.delete();
        }
        newColony();
    }
    
    /** 정착지를 별도 파일에서 불러오기 (화면에는 반영하지 않으므로, 사용 후 refreshColonyList 호출 필요) */
    public void loadColony(File f, boolean alert) {
        boolean exists = false;
        try { 
            Colony c = new Colony(f);
            exists = false;
            for(Colony cx : colonies) { if(c.getName().equals(cx.getName())) exists = true; break; }
            if(exists) return;
            
            c.setOriginalFileName(f.getName());
            colonies.add(c); 
        } catch(Exception ex) { ex.printStackTrace(); if(alert) alert("오류 : " + ex.getMessage()); }
    }
    
    /** 정착지들을 기본 경로에 저장 */
    public void saveColonies() {
        File root = ResourceUtil.getHomeDir("samplejavacodes", "colony");
        
        // 백업 준비
        SimpleDateFormat format8 = new SimpleDateFormat("yyyyMMdd");
        int no = 1;
        String date8 = format8.format(new Date(System.currentTimeMillis()));
        String dirName = "backup" + date8 + "_" + no;
        
        // 백업 디렉토리 생성
        File dir = new File(root.getAbsolutePath() + File.separator + dirName);
        while(dir.exists()) {
            no++;
            dirName = "backup" + date8 + "_" + no;
            dir = new File(root.getAbsolutePath() + File.separator + dirName);
        }
        dir.mkdirs();
        
        // 백업
        File[] lists = root.listFiles(getColonyFileFilter());
        for(File f : lists) {
            File newPath = new File(dir.getAbsolutePath() + File.separator + f.getName());
            f.renameTo(newPath); // 파일 이동
        }
        
        // 저장
        for(Colony c : colonies) {
            String name = c.getOriginalFileName();
            if(name == null) name = "col_" + c.getKey() + ".colony";
            
            File colFile = new File(root.getAbsolutePath() + File.separator + name);
            saveColony(c, colFile, false);
        }
        
        // 임시 백업 삭제
        if(dir.exists()) {
            lists = dir.listFiles();
            for(File f : lists) {
                if(f.isDirectory()) continue;
                f.delete();
            }
            if(dir.listFiles().length <= 0) dir.delete();
        }
    }
    
    /** 해당 정착지를 별도 파일로 저장 */
    public void saveColony(Colony c, File f, boolean alert) {
        try { 
            String nameLower = f.getName().toLowerCase().trim();
            if(! ( nameLower.endsWith(".colony") || nameLower.endsWith(".colgz") )) f = new File(f.getAbsolutePath() + ".colony");
            
            c.save(f); 
        } catch(Exception ex) { ex.printStackTrace(); if(alert) { alert("오류 : " + ex.getMessage()); } else { throw new RuntimeException(ex.getMessage()); } }
    }
    
    /** 새 정착지 생성 */
    public Colony newColony() {
        Colony newCol = new Colony();
        newCol.newCity();
        
        colonies.add(newCol);
        refreshColonyList();
        
        return newCol;
    }
    
    /** 정착지 추가 */
    public void addColony(Colony col) {
        for(Colony c : colonies) {
            if(c.getKey() == col.getKey()) return;
        }
        colonies.add(col);
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

    public boolean isVisible() {
        if(dialog == null) return false;
        return dialog.isVisible();
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
        dispose(true);
    }
    
    public void dispose(boolean closeDialog) {
        setEditable(false);
        disposeContents();
        
        cpNow = null;
        for(ColonyPanel p : pnColonies) {
            p.dispose();
        }
        pnColonies.clear();
        colonies.clear();
        
        cardMain = null;
        
        if(pnMain != null) pnMain.removeAll();
        pnMain = null;
        
        if(fileChooser != null) fileChooser.setVisible(false);
        fileChooser = null;
        
        if(dialog != null && closeDialog) dialog.setVisible(false);
        dialog = null;
        
        if(backupManager != null) backupManager.dispose();
        backupManager = null;
    }
    
    public void disposeContents() {
        threadSwitch = false;
        waitThreadShutdown();
        saveColonies();
    }
    
    public void onWindowClosing() {
        setEditable(false);
        if(! flagSaveBeforeClose) return;
        flagSaveBeforeClose = false;
        
        final Vector<String> flagShutdowns = new Vector<String>();
        new Thread(new Runnable() {
            @Override
            public void run() { 
                dispose(false);
                flagShutdowns.add("1");
            }
        }).start();
        int numInfLoopPrev = 0;
        while(true) {
            if(flagShutdowns.size() >= 1) break;
            try { Thread.sleep(100L); } catch(InterruptedException ex) { break; }
            numInfLoopPrev++;
            if(numInfLoopPrev >= 100) break;
        }
    }
    
    public void setEditable(boolean editable) {
        if(editable) {
            if(cardMain != null) cardMain.show(pnMain, "C1");
        } else {
            if(cardMain != null) cardMain.show(pnMain, "C2");
        }
        
        for(ColonyPanel c : pnColonies) {
            Colony col = c.getColony();
            
            if(editable) {
                if(col == null) return;
                if(col.getHp() <= 0) return;
            }
            
            c.setEditable(editable); 
        }
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
    
    public int getDialogWidth() {
        return getDialog().getWidth();
    }
    
    public int getDialogHeight() {
        return getDialog().getHeight();
    }
    
    /** 현재 선택된 정착지 반환 */
    public Colony getSelectedColony() {
        if(selectedColony < 0) return null;
        if(selectedColony >= colonies.size()) { selectedColony = 0; return null; }
        return colonies.get(selectedColony);
    }
    
    /** 현재 선택된 정착지 반환 */
    public Colony getColony() {
        return getSelectedColony();
    }
    
    /** 해당 키를 갖는 정착지 찾아 반환 (목록에 없으면 null 반환) */
    @Override
    public Colony getColony(long colonyKey) {
        for(Colony c : colonies) {
            if(c.getKey() == colonyKey) return c;
        }
        return null;
    }
    
    /** 시뮬레이션 시작/정지 토글 */
    public void toggleSimulationRunning() {
        threadPaused = (! threadPaused);
        if(threadPaused) {
            pauseSimulation();
        } else {
            resumeSimulation();
        }
    }
    
    public void pauseSimulation() {
        threadPaused = true;
        
        btnThrPlay.setEnabled(false);
        menuActionThrPlay.setEnabled(false);
        
        btnThrPlay.setText("시뮬레이션 시작");
        menuActionThrPlay.setText("시뮬레이션 시작");
        btnSaveAs.setEnabled(true);
        btnLoadAs.setEnabled(true);
        cbxColony.setEnabled(true);
        menuFileLoad.setEnabled(true);
        menuFileSave.setEnabled(true);
        menuFileBackup.setEnabled(true);
        menuFileRestore.setEnabled(true);
        menuFileReset.setEnabled(true);
        menuFileDel.setEnabled(true);
        menuFileNew.setEnabled(true);
        
        for(ColonyPanel c : pnColonies) {
            Colony col = c.getColony();
            if(col == null) return;
            if(col.getHp() <= 0) return;
            c.setEditable(true); 
        }
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try { Thread.sleep(500L); } catch(InterruptedException ex) { ex.printStackTrace(); }
                btnThrPlay.setEnabled(true);
                menuActionThrPlay.setEnabled(true);
            }
        }).start();
    }
    
    public void resumeSimulation() {
        threadPaused = false;
        reserveSaving = true;
        
        btnThrPlay.setEnabled(false);
        menuActionThrPlay.setEnabled(false);
        if(backupManager != null) backupManager.close();
        
        // 쓰레드가 완전히 종료될 때까지 대기
        try {
            int prefInfLoop = 10;
            while(! bCheckerPauseCompleted) {
                Thread.sleep(1000L);
                prefInfLoop--;
                if(prefInfLoop <= 0) break;
            }
        } catch(InterruptedException ex) { ex.printStackTrace(); }
        
        btnThrPlay.setText("시뮬레이션 정지");
        menuActionThrPlay.setText("시뮬레이션 정지");
        btnSaveAs.setEnabled(false);
        btnLoadAs.setEnabled(false);
        cbxColony.setEnabled(false);
        menuFileLoad.setEnabled(false);
        menuFileSave.setEnabled(false);
        menuFileBackup.setEnabled(false);
        menuFileRestore.setEnabled(false);
        menuFileReset.setEnabled(false);
        menuFileDel.setEnabled(false);
        menuFileNew.setEnabled(false);
        for(ColonyPanel c : pnColonies) { c.setEditable(false); }
        
        btnThrPlay.setEnabled(true);
        menuActionThrPlay.setEnabled(true);
    }
    
    /** 쓰레드에서 1 사이클 당 1회 호출됨 */
    public void oneCycle() {
        Colony col = getSelectedColony();
        if(col == null) return;
        
        try { col.oneCycle(cycle, null, col, 100, getColonyPanel(col)); } catch(Exception ex) { ex.printStackTrace(); }
        try {
            SwingUtilities.invokeLater(new Runnable() { 
                @Override
                public void run() {
                    refreshArenaPanel(cycle);
                }
            });
        } catch(Exception ex) { ex.printStackTrace(); }
        
        cycle++;
        if(cycle >= Integer.MAX_VALUE - 10) cycle = 0;
    }
    
    /** 화면 새로고침 예약 */
    public void reserveRefresh() {
        reserveRefresh = true;
    }
    
    /** 정착지 목록과 화면 내용 갱신 */
    public void refreshColonyList() {
        cbxColony.setModel(new DefaultComboBoxModel<Colony>(colonies));
        selectedColony = cbxColony.getSelectedIndex();
        refreshColonyContent();
    }
    
    /** 정착지 화면 내용 갱신 */
    public void refreshColonyContent() {
        selectedColony = cbxColony.getSelectedIndex();
        assureMainThreadRunning();
        refreshArenaPanel(0);
    }
    
    /** 사이클 진행에 따른 정착지 화면 내용 갱신 (성능을 위해 항상 전체를 새로고침하지는 않음. 확실히 새로고침하려면 refreshColonyContent 메소드 사용) */
    public synchronized void refreshArenaPanel(int cycle) {
        Colony col = getSelectedColony();
        if(col == null) {
            pnCols.removeAll();
            pnCols.add(pnNoColonies, BorderLayout.CENTER);
            return;
        }
        
        ColonyPanel colPn = getColonyPanel(col);
        if(colPn == null) {
            colPn = new ColonyPanel(col, this);
            if(isVisible()) colPn.setDividerLocationOnlyFirst();
            pnColonies.add(colPn);
        }
        
        if(cpNow == null || cpNow != colPn) {
            pnCols.removeAll();
            cpNow = colPn;
            if(cpNow != null) pnCols.add(colPn, BorderLayout.CENTER);
        }
        
        colPn.refresh(cycle, null, col, this);
    }
    
    /** 해당 정착지를 출력하는 영역 반환 */
    public ColonyPanel getColonyPanel(Colony col) {
        for(ColonyPanel cp : pnColonies) {
            if(cp.getColony().getKey() == col.getKey()) {
                return cp;
            }
        }
        return null;
    }
    
    /** 해당 도시를 출력하는 도시 영역 반환 */
    public CityPanel getCityPanel(City city) {
        Colony col = getSelectedColony();
        ColonyPanel colPn = getColonyPanel(col);
        if(colPn == null) return null;
        return colPn.getCityPanel(city);
    }
    
    /** 도시가 속한 정착지 찾기 */
    @Override
    public Colony getColonyFrom(City city) {
        for(Colony c : colonies) {
            for(City ct : c.getCities()) {
                if(ct.getKey() == city.getKey()) return c;
            }
        }
        return null;
    }
    
    /** 각 요소들을 위한 고유키 생성 (절대 0이 나오지 않음) */
    public static long generateKey() {
        Random rd = new Random();
        long key = rd.nextLong();
        while(key == 0L) { key = rd.nextLong(); }
        return key;
    }
    
    /** 각 요소들의 고유 이름을 위한 자연수 반환 */
    public static int generateNaturalNumber() {
        return Math.abs(new Random().nextInt());
    }
    
    /** 공격자의 대미지에 추가 연산 (랜덤성 부여, 속성 및 방어력, 상태 적용) */
    public static int naturalizeDamage(AttackableObject attacker, ColonyElements target, int damage) {
        if(damage < 0) return damage; // 대미지가 음수인 경우 그대로 반환
        double correctRate = 1.0; // 명중률
        int    defType = target.getDefenceType();
        int    atkType = attacker.getAttackType();
        
        // 속성 적용
        if(defType == DEFENCETYPE_SMALL) {
            if(atkType == ATTACKTYPE_THICK_BULLET ) { damage = (int) Math.round( damage / 2.0 ); correctRate = correctRate * 0.75; }
            if(atkType == ATTACKTYPE_THICK_MISSILE) { damage = (int) Math.round( damage / 2.0 ); }
            if(atkType == ATTACKTYPE_THICK_RAY    ) { correctRate = correctRate * 0.75; }
            if(atkType == ATTACKTYPE_THICK_ENERGY ) { correctRate = correctRate * 0.25; }
        }
        
        if(defType == DEFENCETYPE_BUILDING) {
            if(atkType >= 1 && atkType <= 9) damage = (int) Math.round( damage / 2.0 );
        }
        
        // 랜덤성 적용
        //    랜덤값 생성
        double naturalRandom = Math.round(damage * 0.2);
        naturalRandom = ((naturalRandom / 2.0) * Math.random()) * 2.0;
        naturalRandom = naturalRandom - (naturalRandom / 2.0); // 반수는 음수로 가도록
        
        //    속성에 따라 랜덤값 추가 변동
        if(atkType == ATTACKTYPE_THICK_MISSILE) naturalRandom = naturalRandom * 2;
        if(atkType == ATTACKTYPE_THICK_ENERGY && defType == DEFENCETYPE_SMALL) naturalRandom = naturalRandom * 4;
        if(atkType == ATTACKTYPE_THIN_ENERGY  && defType == DEFENCETYPE_SMALL) naturalRandom = naturalRandom * 2;
        
        //     랜덤값 대미지에 적용
        damage = damage + (int) Math.round(naturalRandom);
        
        // 방어력 적용
        damage = damage - target.getDefencePoint();
        if(damage < 1) damage = 1;
        
        // 명중률 적용
        if(Math.random() > correctRate) return 0; // 명중률이므로, 명중률에 벗어나야 0 리턴, 부등호 방향 주의 !
        return damage;
    }
    
    public static final short ATTACKTYPE_NORMAL = 0;
    public static final short ATTACKTYPE_THIN_BULLET = 1;
    public static final short ATTACKTYPE_THIN_RAY    = 2;
    public static final short ATTACKTYPE_THIN_ENERGY = 3;
    public static final short ATTACKTYPE_THICK_BULLET  = 11;
    public static final short ATTACKTYPE_THICK_RAY     = 12;
    public static final short ATTACKTYPE_THICK_ENERGY  = 13;
    public static final short ATTACKTYPE_THICK_MISSILE = 14;
    public static final short DEFENCETYPE_NORMAL   = 0;
    public static final short DEFENCETYPE_SMALL    = 1;
    public static final short DEFENCETYPE_BUILDING = 9;
    
}