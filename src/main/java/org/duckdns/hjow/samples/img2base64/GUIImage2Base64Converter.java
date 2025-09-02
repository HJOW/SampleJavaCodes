package org.duckdns.hjow.samples.img2base64;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.duckdns.hjow.samples.base.GUIProgram;
import org.duckdns.hjow.samples.base.SampleJavaCodes;
import org.duckdns.hjow.samples.uicomponent.JLogArea;
import org.duckdns.hjow.samples.util.UIUtil;

/** GUI 기반 Image2Base64 툴 */
public class GUIImage2Base64Converter extends Image2Base64Converter implements GUIProgram, Runnable {
    private static final long serialVersionUID = 5119253138076253761L;
    protected SimpleDateFormat formatter16;

    protected JDialog dialog;
    protected JTextField tfPath;
    protected JTextArea taRes;
    protected JLogArea taLog;
    protected JCheckBox chkPrefix;
    protected JFileChooser fileChooser;
    protected JButton btnFile, btnRun;
    protected JProgressBar prog;
    protected JSplitPane splits;
    protected JMenuItem mnFileRun;
    
    public GUIImage2Base64Converter(SampleJavaCodes superInstance) {
        super();
        init(superInstance);
    }
    
    /** DnD 발생 시 호출되는 메소드 */
    @SuppressWarnings("unchecked")
    protected String getFromDnd(DropTargetDropEvent dtde) {
        try {
            String one = null;
            List<File> files = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            for(File f : files) {
                if(! f.exists()) return null;
                if(f.isDirectory()) return null;
                String name = f.getName().toLowerCase();
                if(! (name.endsWith(".jpg") || name.endsWith(".png"))) return null;
                
                one = f.getAbsolutePath();
            }
            
            return one;
        } catch(Exception ex) {
            ex.printStackTrace();
            log("오류 : " + ex.getMessage());
            return null;
        }
    }
    
    /** 작업 시작 메소드 */
    public void processStart() {
        taRes.setText("...");
        prog.setIndeterminate(true);
        log("작업이 시작되었습니다.");
        tfPath.setEditable(false);
        btnRun.setEnabled(false);
        btnFile.setEnabled(false);
        
        new Thread(this).start(); // 별도 쓰레드에서 run 메소드 수행
    }
    
    /** 로그 출력 */
    public void log(String msg) {
        System.out.println(msg);
        if(taLog != null) taLog.log(msg);
    }
    
    /** 작업 시작 시, 별도 쓰레드에서 이 메소드가 호출됨 */
    @Override
    public void run() {
        try {
            boolean usingPrefix = chkPrefix.isSelected();
            chkPrefix.setEnabled(false);
            
            String path = tfPath.getText();
            File   file = new File(path);
            
            String ext  = getExt(file);
            String res  = convert(file);
            
            if(usingPrefix) {
                String prefix = "data:image/";
                if(ext.equalsIgnoreCase("jpg")) prefix += "jpeg";
                else prefix += ext;
                
                res = prefix + ";base64, " + res;
            }
            
            taRes.setText(res);
        } catch(Exception ex) {
            taRes.setText("Error : " + ex.getMessage());
            log("Error : " + ex.getMessage());
        } finally {
            tfPath.setEditable(true);
            btnRun.setEnabled(true);
            btnFile.setEnabled(true);
            chkPrefix.setEnabled(true);
            prog.setIndeterminate(false);
            log("작업이 종료되었습니다.");
        }
    }
    
    @Override
    public void init(SampleJavaCodes superInstance) {
        Window superDialog = superInstance.getWindow();
        
        // JDialog 설정
        if(     superDialog instanceof Frame ) dialog = new JDialog((Frame) superDialog);
        else if(superDialog instanceof Dialog) dialog = new JDialog((Dialog)superDialog);
        else dialog = new JDialog();
        
        dialog.setSize(600, 400);
        dialog.setTitle("Image 2 Base64 Converter");
        dialog.setIconImage(UIUtil.iconToImage(getIcon()));
        dialog.setLayout(new BorderLayout());
        
        UIUtil.center(dialog);
        
        // 기본 날짜 포맷 (로그 출력 시 사용)
        formatter16 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // UI 요소 구성
        JPanel pnMain, pnTop, pnCenter, pnDown, pnCtrl;
        
        pnMain = new JPanel();
        dialog.add(pnMain, BorderLayout.CENTER);
        
        pnMain.setLayout(new BorderLayout());
        
        pnTop    = new JPanel();
        pnCenter = new JPanel();
        pnDown   = new JPanel();
        pnCtrl   = new JPanel();
        pnMain.add(pnTop   , BorderLayout.NORTH);
        pnMain.add(pnCenter, BorderLayout.CENTER);
        pnMain.add(pnDown  , BorderLayout.SOUTH);
        
        pnTop.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new BorderLayout());
        pnCtrl.setLayout(new BorderLayout());
        
        splits = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        pnCenter.add(splits, BorderLayout.CENTER);
        
        taRes = new JTextArea();
        taRes.setEditable(false);
        taRes.setLineWrap(true);
        splits.setTopComponent(new JScrollPane(taRes));
        
        JToolBar toolbar = new JToolBar();
        pnCenter.add(toolbar, BorderLayout.NORTH);
        
        chkPrefix = new JCheckBox("접두어 포함");
        chkPrefix.setSelected(true);
        toolbar.add(chkPrefix);
        
        pnTop.add(pnCtrl, BorderLayout.EAST);
        
        tfPath = new JTextField();
        pnTop.add(tfPath, BorderLayout.CENTER);
        
        btnFile = new JButton("...");
        pnCtrl.add(btnFile, BorderLayout.WEST);
        
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "Image files (*.png)";
            }
            
            @Override
            public boolean accept(File f) {
                if(f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".png");
            }
        });
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "Image files (*.jpg)";
            }
            
            @Override
            public boolean accept(File f) {
                if(f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg");
            }
        });
        
        btnFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {   
                    @Override
                    public void run() {
                        int res = fileChooser.showOpenDialog(dialog);
                        if(res == JFileChooser.APPROVE_OPTION) tfPath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    }
                });
            }
        });
        
        btnRun = new JButton("실행");
        btnRun.setIcon(UIManager.getIcon("FileChooser.detailsViewIcon"));
        // pnCtrl.add(btnRun, BorderLayout.CENTER);
        toolbar.add(btnRun);
        
        ActionListener listenerRun = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {   
                    @Override
                    public void run() {
                        processStart();
                    }
                });
            }
        };
        
        btnRun.addActionListener(listenerRun);
        
        taLog = new JLogArea();
        splits.setBottomComponent(taLog);
        
        prog = new JProgressBar();
        prog.setMaximum(100);
        prog.setValue(0);
        pnDown.add(prog, BorderLayout.NORTH);
        
        // 메뉴 구성
        JMenuBar menuBar = new JMenuBar();
        dialog.setJMenuBar(menuBar);
        
        JMenu menu = new JMenu("파일");
        menuBar.add(menu);
        
        mnFileRun = new JMenuItem("실행");
        menu.add(mnFileRun);
        mnFileRun.addActionListener(listenerRun);
        
        menu.addSeparator();
        
        JMenuItem mnItem = new JMenuItem("종료");
        menu.add(mnItem);
        
        mnItem.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });
        
        menu = new JMenu("기능");
        menuBar.add(menu);
        
        mnItem = new JMenuItem("로그 및 결과 비우기");
        menu.add(mnItem);
        
        mnItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                taLog.setText("");
                taRes.setText("");
            }
        });
        
        // DnD 구성
        DropTarget dndTargetTf = new DropTarget(dialog, new DropTargetListener() {
            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) { }            
            @Override
            public void dragOver(DropTargetDragEvent dtde) { }
            @Override
            public void dragExit(DropTargetEvent dte) { }
            @Override
            public void dragEnter(DropTargetDragEvent dtde) { }
            @Override
            public void drop(DropTargetDropEvent dtde) {
                dtde.acceptDrop(DnDConstants.ACTION_LINK);
                String res = getFromDnd(dtde);
                if(res == null) res = "";
                tfPath.setText(res);
            }
        });
        tfPath.setDropTarget(dndTargetTf);
        
        DropTarget dndTargetTa = new DropTarget(dialog, new DropTargetListener() {
            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) { }            
            @Override
            public void dragOver(DropTargetDragEvent dtde) { }
            @Override
            public void dragExit(DropTargetEvent dte) { }
            @Override
            public void dragEnter(DropTargetDragEvent dtde) { }
            @Override
            public void drop(DropTargetDropEvent dtde) {
                dtde.acceptDrop(DnDConstants.ACTION_LINK);
                String res = getFromDnd(dtde);
                if(res == null) res = "";
                
                tfPath.setText(res);
                if(! res.equals("")) processStart();
            }
        });
        taRes.setDropTarget(dndTargetTa);
        
        taRes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StringSelection data = new StringSelection(taRes.getText());
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                clip.setContents(data, data);
                taRes.selectAll();
            }
        });
    }

    @Override
    public void onBeforeOpened(SampleJavaCodes superInstance) {
        
    }

    @Override
    public void onAfterOpened(SampleJavaCodes superInstance) {
        taLog.setText("이미지 파일을 선택하신 후 실행해 주세요.");
        taRes.setText("이 곳에 Base64 텍스트가 출력됩니다.");
        splits.setDividerLocation(0.7);
    }

    @Override
    public String getTitle() {
        return "Image 2 BASE64 Converter";
    }

    @Override
    public void alert(String msg) {
        JOptionPane.showMessageDialog(getDialog(), msg);
    }

    @Override
    public String getName() {
        return "img2base64";
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

    @Override
    public void dispose() {
        if(dialog != null) dialog.setVisible(false);
        dialog = null;
    }

    @Override
    public void open(SampleJavaCodes superInstance) {
        if(dialog == null) init(superInstance);
        
        onBeforeOpened(superInstance);
        dialog.setVisible(true);
        onAfterOpened(superInstance);
    }
    
    @Override
    public boolean isHidden() {return false;}
}
