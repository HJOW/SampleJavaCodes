package org.duckdns.hjow.samples.img2base64;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileFilter;

/** GUI 기반 Image2Base64 툴 */
public class GUIImage2Base64Converter extends Image2Base64Converter implements Runnable {
    
    public static void main(String[] args) {
        new GUIImage2Base64Converter().open();
    }
    
    protected SimpleDateFormat formatter16;

    protected JFrame frame;
    protected JTextField tfPath;
    protected JTextArea taRes, taLog;
    protected JCheckBox chkPrefix;
    protected JFileChooser fileChooser;
    protected JButton btnFile, btnRun;
    protected JProgressBar prog;
    protected JSplitPane splits;
    protected JMenuItem mnFileRun;
    
    /** 객체 생성 및 UI 세팅 */
    public GUIImage2Base64Converter() {
        // LookAndFeel 설정
        try {
            LookAndFeelInfo[] looAndFeels = UIManager.getInstalledLookAndFeels();
            for(LookAndFeelInfo lookAndFeelOne : looAndFeels) {
                if(lookAndFeelOne.getName().equalsIgnoreCase("Nimbus")) {
                    UIManager.setLookAndFeel(lookAndFeelOne.getClassName());
                }
            }
        } catch(Exception ex) { ex.printStackTrace(); }
        
        // JFrame 설정
        frame = new JFrame();
        frame.setSize(600, 400);
        frame.setTitle("Image 2 Base64 Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(iconToImage(UIManager.getIcon("OptionPane.informationIcon")));
        frame.setLayout(new BorderLayout());
        
        // 기본 날짜 포맷 (로그 출력 시 사용)
        formatter16 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // UI 요소 구성
        JPanel pnMain, pnTop, pnCenter, pnDown, pnCtrl;
        
        pnMain = new JPanel();
        frame.add(pnMain, BorderLayout.CENTER);
        
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
                        int res = fileChooser.showOpenDialog(frame);
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
        
        taLog = new JTextArea();
        taLog.setEditable(false);
        splits.setBottomComponent(new JScrollPane(taLog));
        
        prog = new JProgressBar();
        prog.setMaximum(100);
        prog.setValue(0);
        pnDown.add(prog, BorderLayout.NORTH);
        
        // 메뉴 구성
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
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
                frame.setVisible(false);
                System.exit(0);
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
        DropTarget dndTargetTf = new DropTarget(frame, new DropTargetListener() {
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
        
        DropTarget dndTargetTa = new DropTarget(frame, new DropTargetListener() {
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
    
    /** frame 창을 호출 */
    public void open() {
        frame.setVisible(true);
        taLog.setText("이미지 파일을 선택하신 후 실행해 주세요.");
        taRes.setText("이 곳에 Base64 텍스트가 출력됩니다.");
        splits.setDividerLocation(0.7);
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
        taLog.setText(taLog.getText() + "\n" + formatter16.format(new Date(System.currentTimeMillis())) + " " + msg);
        taLog.setCaretPosition(taLog.getDocument().getLength() - 1);
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
    
    /** javax.swing.Icon 객체를 java.awt.Image 로 변환 */
    public static Image iconToImage(Icon icon) {
        if(icon instanceof ImageIcon) return ((ImageIcon) icon).getImage();
        
        BufferedImage buffImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),BufferedImage.TYPE_INT_ARGB);
        Graphics g = buffImage.createGraphics();
        
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        
        return buffImage;
    }
}
