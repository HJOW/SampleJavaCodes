package com.hjow.img2base64;

import java.awt.BorderLayout;
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
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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
    protected JFileChooser fileChooser;
    protected JButton btnFile, btnRun;
    protected JProgressBar prog;
    protected JSplitPane splits;
    
    public GUIImage2Base64Converter() {
        try {
            LookAndFeelInfo[] looAndFeels = UIManager.getInstalledLookAndFeels();
            for(LookAndFeelInfo lookAndFeelOne : looAndFeels) {
                if(lookAndFeelOne.getName().equalsIgnoreCase("Nimbus")) {
                    UIManager.setLookAndFeel(lookAndFeelOne.getClassName());
                }
            }
        } catch(Exception ex) { ex.printStackTrace(); }
        
        frame = new JFrame();
        frame.setSize(600, 400);
        frame.setTitle("Image 2 Base64 Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        formatter16 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
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
        pnCtrl.add(btnRun, BorderLayout.CENTER);
        
        btnRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {   
                    @Override
                    public void run() {
                        processStart();
                    }
                });
            }
        });
        
        taLog = new JTextArea();
        taLog.setEditable(false);
        splits.setBottomComponent(new JScrollPane(taLog));
        
        prog = new JProgressBar();
        prog.setMaximum(100);
        prog.setValue(0);
        pnDown.add(prog, BorderLayout.NORTH);
        
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
    
    public void open() {
        frame.setVisible(true);
        taLog.setText("이미지 파일을 선택하신 후 실행해 주세요.");
        taRes.setText("이 곳에 Base64 텍스트가 출력됩니다.");
        splits.setDividerLocation(0.7);
    }
    
    public void processStart() {
        taRes.setText("...");
        prog.setIndeterminate(true);
        log("작업이 시작되었습니다.");
        tfPath.setEditable(false);
        btnRun.setEnabled(false);
        btnFile.setEnabled(false);
        
        new Thread(this).start();
    }
    
    public void log(String msg) {
        taLog.setText(taLog.getText() + "\n" + formatter16.format(new Date(System.currentTimeMillis())) + " " + msg);
        taLog.setCaretPosition(taLog.getDocument().getLength() - 1);
    }
    
    @Override
    public void run() {
        try {
            String path = tfPath.getText();
            String res  = convert(new File(path));
            
            taRes.setText(res);
        } catch(Exception ex) {
            taRes.setText("Error : " + ex.getMessage());
            log("Error : " + ex.getMessage());
        } finally {
            tfPath.setEditable(true);
            btnRun.setEnabled(true);
            btnFile.setEnabled(true);
            prog.setIndeterminate(false);
            log("작업이 종료되었습니다.");
        }
    }
}
