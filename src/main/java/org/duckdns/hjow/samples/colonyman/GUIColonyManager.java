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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.duckdns.hjow.samples.base.GUIProgram;
import org.duckdns.hjow.samples.base.SampleJavaCodes;
import org.duckdns.hjow.samples.colonyman.benchmark.BenchmarkManager;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.CityPanel;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyPanel;
import org.duckdns.hjow.samples.util.UIUtil;

/** Colonization 프로그램 */
public class GUIColonyManager extends ColonyManager implements GUIProgram {
	private static final long serialVersionUID = -2483528821790634383L;
	protected transient SampleJavaCodes superInstance;
	
	protected transient JDialog dialog;
    protected transient JPanel pnRoot, pnMain, pnFront;
    protected transient JProgressBar progFront;
    protected transient JTabbedPane tabMain;
    protected transient CardLayout cardMain;
    protected transient JButton btnSaveAs, btnLoadAs, btnThrPlay, btnGotoGame;
    
    protected transient JEditorPane webNotice;
    
    protected transient JPanel pnCols, pnNoColonies;
    protected transient ColonyPanel cpNow;
    protected transient JComboBox<Colony> cbxColony;
    protected transient List<ColonyPanel> pnColonies = new Vector<ColonyPanel>();
    
    protected transient JProgressBar progThreadStatus;
    protected transient JLabel lbRunningTime;
    
    protected transient JFileChooser fileChooser;
    protected transient javax.swing.filechooser.FileFilter filterCol, filterColGz;
    
    protected transient BackupManager backupManager;
    protected transient BenchmarkManager benchManager;
    
    protected transient JMenuBar menuBar;
    protected transient JMenu menuFile, menuAction;
    protected transient JMenuItem menuActionThrPlay, menuFileSave, menuFileLoad, menuFileBackup, menuFileRestore, menuFileReset, menuFileNew, menuFileDel;
    
    public GUIColonyManager(SampleJavaCodes superInstance) {
    	super();
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
        
        Dimension winSize = UIUtil.getScreenSize();
        int w, h;
        w = (int) (winSize.getWidth()  * 0.8);
        h = (int) (winSize.getHeight() * 0.8);
        
        if(w >= winSize.getWidth()  - 50) w = (int) (winSize.getWidth()  - 50);
        if(h >= winSize.getHeight() - 50) h = (int) (winSize.getHeight() - 80);
        
        if(w < 800) w = 800;
        if(h < 600) h = 600;
        int logHeight = 250;
        
        dialog.setSize(w, h);
        UIUtil.center(dialog);
        dialog.setSize(w, h - logHeight); // 로그 대화상자 들어갈 자리 마련
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
        
        if(dialogGlobalLog == null) dialogGlobalLog = new GlobalLogDialog(this);
        dialogGlobalLog.setSize(w, logHeight);
        dialogGlobalLog.setLocationBottom(dialog);
        
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
        
        pnRoot = new JPanel();
        dialog.add(pnRoot, BorderLayout.CENTER);
        
        tabMain = new JTabbedPane();
        pnRoot.setLayout(new BorderLayout());
        pnRoot.add(tabMain, BorderLayout.CENTER);
        
        JPanel pnMainCard1, pnMainCard2;
        pnMain      = new JPanel();
        pnFront     = new JPanel();
        pnMainCard1 = new JPanel();
        pnMainCard2 = new JPanel();
        
        tabMain.add("홈", pnFront);
        tabMain.add("Colonization", pnMain);
        
        pnFront.setLayout(new BorderLayout());
        
        JPanel pnFrontCenter, pnFrontDown;
        pnFrontCenter = new JPanel();
        pnFrontDown   = new JPanel();
        pnFront.add(pnFrontCenter, BorderLayout.CENTER);
        pnFront.add(pnFrontDown  , BorderLayout.SOUTH);
        
        webNotice = new JEditorPane();
        webNotice.setEditable(false);
        webNotice.setContentType("text/html");
        webNotice.setText(ColonyClassLoader.htmlNoticeEmpty());
        
        pnFrontCenter.setLayout(new BorderLayout());
        pnFrontCenter.add(new JScrollPane(webNotice), BorderLayout.CENTER);
        
        JPanel pnFrontDownCenter, pnFrontDownRight, pnFrontDownLeft;
        pnFrontDownLeft   = new JPanel();
        pnFrontDownCenter = new JPanel();
        pnFrontDownRight  = new JPanel();
        pnFrontDown.setLayout(new BorderLayout());
        pnFrontDown.add(pnFrontDownLeft  , BorderLayout.WEST);
        pnFrontDown.add(pnFrontDownCenter, BorderLayout.CENTER);
        pnFrontDown.add(pnFrontDownRight , BorderLayout.EAST);
        
        pnFrontDownLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnFrontDownCenter.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnFrontDownRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        progFront = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        pnFrontDownLeft.add(progFront);
        
        btnGotoGame = new JButton("Colonization");
        pnFrontDownRight.add(btnGotoGame);
        
        btnGotoGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tabMain.setSelectedIndex(1);
				progFront.setVisible(false);
			}
		});
        
        JButton btnExit = new JButton("종료");
        pnFrontDownRight.add(btnExit);
        
        btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				flagSaveBeforeClose = false;
                dispose(true);
			}
		});
        
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

        Vector<SimulationSpeed> strSpeeds = getSpeedList();
        JComboBox<SimulationSpeed> cbxSpeed = new JComboBox<SimulationSpeed>(strSpeeds);
        cbxSpeed.setSelectedIndex(0);
        toolbarNorth.add(cbxSpeed);

        cbxSpeed.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
            	SimulationSpeed spd = (SimulationSpeed) cbxSpeed.getSelectedItem();
            	if(spd == null) cycleGap = 99L;
            	else cycleGap = spd.getThreadGap();
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

        menuAction.addSeparator();

        menuItem = new JCheckBoxMenuItem("디버그 모드");
        menuAction.add(menuItem);
        menuItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                flagDebugMode = ((JCheckBoxMenuItem)e.getSource()).isSelected();
                lbRunningTime.setVisible(isDebugModeEnabled());
            }
        });
        ((JCheckBoxMenuItem)menuItem).setSelected(isDebugModeEnabled());

        menuItem = new JMenuItem("전역 로그 보기");
        menuAction.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogGlobalLog.open(getSelf());
                dialogGlobalLog.setLocationBottom(getDialog());
            }
        });
        
        menuAction.addSeparator();
        
        menuItem = new JMenuItem("Benchmark");
        menuAction.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	benchManager.open();
            }
        });
        
        benchManager  = new BenchmarkManager(dialog);
        backupManager = new BackupManager(this);
        
        refreshColonyContent();
        cardMain.show(pnMain, "C2");
    }

    /** 지원되는 시뮬 속도 목록 반환 */
    protected Vector<SimulationSpeed> getSpeedList() {
    	Vector<SimulationSpeed> strSpeeds = new Vector<SimulationSpeed>();
        strSpeeds.add(new SimulationSpeed(1));
        strSpeeds.add(new SimulationSpeed(2));
        strSpeeds.add(new SimulationSpeed(3));
        return strSpeeds;
	}

	@Override
    public void onBeforeOpened(SampleJavaCodes superInstance) {
        if(thread != null) { try { threadSwitch = false; thread.interrupt(); Thread.sleep(1000L); } catch(Exception exc) {} }
        if(dialog == null) init(superInstance);
        
        loadColonies();
        loadWebConfigs();
    }

    @Override
    public void onAfterOpened(SampleJavaCodes superInstance) {
        btnThrPlay.setEnabled(false);
        menuActionThrPlay.setEnabled(false);
        
        threadPaused = true;
        reserveSaving  = false;
        reserveRefresh = false;
        flagAlreadyDisposed = false;
        
        assureMainThreadRunning();
        
        btnThrPlay.setText("시뮬레이션 시작");
        btnThrPlay.setEnabled(true);
        menuActionThrPlay.setEnabled(true);
        try { webNotice.setPage(ColonyClassLoader.htmlNoticeUrl()); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        
        setEditable(true);
        
        if(dialogGlobalLog == null) dialogGlobalLog = new GlobalLogDialog(this);
    }
    
    /** 별도 쓰레드에서 웹 서버에서 설정 불러오기 */
    protected void loadWebConfigs() {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				progFront.setValue(0);
				progFront.setIndeterminate(true);
				ColonyClassLoader.loadWebConfigs(getSelf());
				
				progFront.setIndeterminate(false);
				if(progFront.isVisible()) {
					int r = 1;
					
					while(r < 100) {
						try { Thread.sleep(12L); } catch(InterruptedException ex) { GlobalLogs.processExceptionOccured(ex, false); break; }
						progFront.setValue(r); r++;
					}
					
					if(tabMain.getSelectedIndex() == 0) tabMain.setSelectedIndex(1);
					progFront.setVisible(false);
				}
			}
		}).start();
    }
    
    /** 메인 쓰레드 실행 */
    protected void turnOnMainThread() {
        if(thread != null) {
            thread.interrupt();
            try { Thread.sleep(1000L); } catch(InterruptedException ex) { GlobalLogs.processExceptionOccured(ex, false); }
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
        long gap = cycleGap;
        
        // 쓰레드에서 수행할 실질 작업 수행
        try { if(! threadPaused) { bCheckerPauseCompleted = false; oneCycle(); } } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        
        // 저장 요청 수행
        if(reserveSaving) { try { saveColonies(); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); } reserveSaving = false; }
        
        // 리프레시 요청 수행
        if(reserveRefresh) {
            try {
                SwingUtilities.invokeLater(new Runnable() {   
                    @Override
                    public void run() {
                        refreshColonyContent();
                    }
                });
            } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
            reserveRefresh = false;
        }
        
        // 일시정지 후 쓰레드가 실제 정지 중인지 판단하는 플래그
        if(threadPaused) bCheckerPauseCompleted = true;
        else bCheckerPauseCompleted = false;
        
        // 쓰레드 Sleep
        try { Thread.sleep(gap); } catch(InterruptedException e) { threadSwitch = false; return false; }

        cycleRunningTime = System.currentTimeMillis() - elapsed - gap;
        if(isDebugModeEnabled()) lbRunningTime.setText("  " + String.valueOf(cycleRunningTime) + " ms");
        
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
        GlobalLogs.log(msg);
    }

    @Override
    public void open(SampleJavaCodes superInstance) {
        onBeforeOpened(superInstance);
        dialog.setVisible(true);
        if(dialogGlobalLog != null) dialogGlobalLog.open(this);
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
        flagAlreadyDisposed = true;
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

        if(dialogGlobalLog != null) {
            dialogGlobalLog.dispose();
            dialogGlobalLog = null;
        }
    }
    
    public void disposeContents() {
        threadSwitch = false;
        waitThreadShutdown();
        if((! flagAlreadyDisposed) && (! colonies.isEmpty())) saveColonies();
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

    public Icon getIcon() {
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        Image img = UIUtil.iconToImage(icon);
        img = img.getScaledInstance(12, 12, Image.SCALE_SMOOTH);
        ImageIcon newIcon = new ImageIcon(img);
        
        return newIcon;
    }

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
                try { Thread.sleep(500L); } catch(InterruptedException ex) { GlobalLogs.processExceptionOccured(ex, false); }
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
        } catch(InterruptedException ex) { GlobalLogs.processExceptionOccured(ex, false); }
        
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
        
        try { col.oneCycle(cycle, null, col, 100, getColonyPanel(col)); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        try {
            SwingUtilities.invokeLater(new Runnable() { 
                @Override
                public void run() {
                    refreshArenaPanel(cycle);
                }
            });
        } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        
        cycle++;
        if(cycle >= Integer.MAX_VALUE - 10) cycle = 0;
    }
    
    /** 정착지 목록과 화면 내용 갱신 */
    @Override
    public void refreshColonyList() {
        cbxColony.setModel(new DefaultComboBoxModel<Colony>(colonies));
        selectedColony = cbxColony.getSelectedIndex();
        refreshColonyContent();
    }
    
    /** 정착지 화면 내용 갱신 */
    @Override
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
}