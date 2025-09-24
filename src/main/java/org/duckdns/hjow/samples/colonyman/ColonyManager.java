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
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.CityPanel;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyPanel;
import org.duckdns.hjow.samples.util.ResourceUtil;
import org.duckdns.hjow.samples.util.UIUtil;

public class ColonyManager implements GUIProgram {
    private static final long serialVersionUID = -5740844908011980260L;
    protected transient SampleJavaCodes superInstance;
    protected transient Thread thread;
    protected transient volatile boolean threadSwitch, threadPaused, threadShutdown, reserveSaving;
    
    protected transient JDialog dialog;
    protected transient JPanel pnMain;
    protected transient CardLayout cardMain;
    protected transient JButton btnSaveAs, btnLoadAs, btnThrPlay;
    
    protected transient Vector<Colony> colonies = new Vector<Colony>();
    protected transient volatile int selectedColony = -1;
    protected transient volatile int cycle = 0;
    protected transient volatile long cycleGap = 990L;
    
    protected transient JPanel pnCols;
    protected transient ColonyPanel cpNow;
    protected transient JComboBox<Colony> cbxColony;
    protected transient List<ColonyPanel> pnColonies = new Vector<ColonyPanel>();
    
    protected transient JProgressBar progThreadStatus;
    protected transient JFileChooser fileChooser;
    protected transient javax.swing.filechooser.FileFilter filterCol, filterColGz;
    
    protected transient JMenuBar menuBar;
    protected transient JMenu menuFile, menuAction;
    protected transient JMenuItem menuActionThrPlay;
    
    protected transient boolean flagSaveBeforeClose = true;
    
    public ColonyManager(SampleJavaCodes superInstance) {
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

        Vector<String> strSpeeds = new Vector<String>();
        strSpeeds.add("×1");
        strSpeeds.add("×2");
        strSpeeds.add("×4");
        JComboBox<String> cbxSpeed = new JComboBox<String>(strSpeeds);
        cbxSpeed.setSelectedIndex(0);
        toolbarNorth.add(cbxSpeed);

        cbxSpeed.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int sel = cbxSpeed.getSelectedIndex();
                if(sel < 0) sel = 0;
                if(sel > 2) sel = 2;
                
                if(sel == 0) cycleGap = 990L;
                else if(sel == 1) cycleGap = 490L;
                else if(sel == 2) cycleGap = 240L;
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
        
        pnCols  = new JPanel();
        JPanel pnArena = new JPanel();
        JPanel pnCtrl  = new JPanel();
        
        pnArena.setLayout( new BorderLayout());
        pnCtrl.setLayout( new BorderLayout());
        pnCols.setLayout( new BorderLayout());
        pnCenter.add(pnArena, BorderLayout.CENTER);
        
        pnArena.add(pnCols, BorderLayout.CENTER);
        pnArena.add(pnCtrl , BorderLayout.NORTH);
        
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
        
        menuBar = new JMenuBar();
        dialog.setJMenuBar(menuBar);
        
        JMenuItem menuItem;
        
        menuFile = new JMenu("파일");
        menuBar.add(menuFile);
        
        menuItem = new JMenuItem("다른 이름으로 이 정착지 저장");
        menuFile.add(menuItem);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveRequested();
            }
        });
        
        menuItem = new JMenuItem("외부 정착지 파일 불러오기");
        menuFile.add(menuItem);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLoadRequested();
            }
        });
        
        menuFile.addSeparator();
        
        menuItem = new JMenuItem("정착지 모두 포기");
        menuFile.add(menuItem);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, KeyEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
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
        thread = new Thread(new Runnable() {    
            @Override
            public void run() {
                while(threadSwitch) {
                    threadShutdown = false;
                    
                    try { if(! threadPaused) oneSecond(); } catch(Exception ex) { ex.printStackTrace(); }
                    try { Thread.sleep(cycleGap); } catch(InterruptedException e) { threadSwitch = false; break; }
                    if(reserveSaving) { try { saveColonies(); } catch(Exception ex) { ex.printStackTrace(); } reserveSaving = false; }
                    
                    threadShutdown = false;
                    progThreadStatus.setIndeterminate(! threadPaused);
                }
                threadShutdown = true;
                progThreadStatus.setIndeterminate(false);
                btnThrPlay.setEnabled(false);
                menuActionThrPlay.setEnabled(false);
            }
        });
        threadSwitch   = true;
        threadPaused   = true;
        threadShutdown = false;
        reserveSaving  = false;
        flagSaveBeforeClose = true;
        btnThrPlay.setText("시뮬레이션 시작");
        btnThrPlay.setEnabled(true);
        menuActionThrPlay.setEnabled(true);
        thread.start();
        setEditable(true);
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
        for(Colony c : colonies) {
            String name = c.getOriginalFileName();
            if(name == null) name = "col_" + c.getKey() + ".colony";
            
            File colFile = new File(root.getAbsolutePath() + File.separator + name);
            saveColony(c, colFile, false);
        }
    }
    
    /** 해당 정착지를 별도 파일로 저장 */
    public void saveColony(Colony c, File f, boolean alert) {
        try { c.save(f); } catch(Exception ex) { ex.printStackTrace(); if(alert) alert("오류 : " + ex.getMessage()); }
    }
    
    /** 새 정착지 생성 */
    public void newColony() {
        Colony newCol = new Colony();
        newCol.newCity();
        
        colonies.add(newCol);
        refreshColonyList();
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
        btnThrPlay.setText("시뮬레이션 시작");
        menuActionThrPlay.setText("시뮬레이션 시작");
        btnSaveAs.setEnabled(true);
        btnLoadAs.setEnabled(true);
        cbxColony.setEnabled(true);
        for(ColonyPanel c : pnColonies) {
            Colony col = c.getColony();
            if(col == null) return;
            if(col.getHp() <= 0) return;
            c.setEditable(true); 
        }
    }
    
    public void resumeSimulation() {
        threadPaused = false;
        reserveSaving = true;
        btnThrPlay.setText("시뮬레이션 정지");
        menuActionThrPlay.setText("시뮬레이션 정지");
        btnSaveAs.setEnabled(false);
        btnLoadAs.setEnabled(false);
        cbxColony.setEnabled(false);
        for(ColonyPanel c : pnColonies) { c.setEditable(false); }
    }
    
    /** 쓰레드에서 1 사이클 당 1회 호출됨 */
    public void oneSecond() {
        Colony col = getSelectedColony();
        if(col == null) return;
        
        try { col.oneSecond(cycle, null, col, 100); } catch(Exception ex) { ex.printStackTrace(); }
        try {
            SwingUtilities.invokeLater(new Runnable() { 
                @Override
                public void run() {
                    refreshArenaPanel(cycle);
                }
            });
        } catch(Exception ex) { ex.printStackTrace(); }
        
        cycle++;
        if(cycle >= 10000000) cycle = 0;
    }
    
    /** 정착지 목록과 화면 내용 갱신 */
    public void refreshColonyList() {
        cbxColony.setModel(new DefaultComboBoxModel<Colony>(colonies));
        selectedColony = cbxColony.getSelectedIndex();
        refreshColonyContent();
    }
    
    /** 정착지 화면 내용 갱신 */
    public void refreshColonyContent() {
        refreshArenaPanel(0);
    }
    
    /** 사이클 진행에 따른 정착지 화면 내용 갱신 (성능을 위해 항상 전체를 새로고침하지는 않음. 확실히 새로고침하려면 refreshColonyContent 메소드 사용) */
    public synchronized void refreshArenaPanel(int cycle) {
        Colony col = getSelectedColony();
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
        
        colPn.refresh(cycle, null, col, this);
    }
    
    /** 해당 도시를 출력하는 도시 영역 반환 */
    public CityPanel getCityPanel(City city) {
        Colony col = getSelectedColony();
        ColonyPanel colPn = null;
        for(ColonyPanel cp : pnColonies) {
            if(cp.getColony().getKey() == col.getKey()) {
                colPn = cp; break;
            }
        }
        if(colPn == null) return null;
        return colPn.getCityPanel(city);
    }
    
    /** 도시가 속한 정착지 찾기 */
    public Colony getColonyFrom(City city) {
        for(Colony c : colonies) {
            for(City ct : c.getCities()) {
                if(ct.getKey() == city.getKey()) return c;
            }
        }
        return null;
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