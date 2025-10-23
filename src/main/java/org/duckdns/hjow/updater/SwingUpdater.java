package org.duckdns.hjow.updater;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.commons.util.GUIUtil;
import org.duckdns.hjow.commons.util.NetUtil;

public class SwingUpdater {
    protected Target target;
    protected File installPath;
    protected int[] installedVersion;
    protected String strUrlUpdates;
    protected File fileToRun;
    protected boolean needDownload = false;
    
    protected Properties prop;
    protected JFrame frame;
    protected JEditorPane taNotice;
    protected JProgressBar prog;
    protected JButton btnRun, btnExit;
    
    public SwingUpdater() {
        init();
    }
    
    protected File getInstallPath() {
        String path = target.getInstallPath();
        path = path.replace("[USERHOME]", System.getProperty("user.home"));
        path = path.replace("[TEMP]"    , System.getProperty("java.io.tmpdir"));
        return new File(path);
    }
    
    /** 사전 준비, 업데이트 대상 소프트웨어 정보를 찾고, 이미 설치된 여부도 확인 */
    protected void init() {
        // 업데이트 대상 소프트웨어 정보를 정의한 클래스 찾기
        try {
            Class<?> targetClass = Class.forName("org.duckdns.hjow.updater.TargetImpl");
            target = (Target) targetClass.newInstance();
        } catch(Throwable tx) {
            tx.printStackTrace();
            System.exit(0);
            return;
        }
        
        if(target == null) { System.out.println("Cannot find the target."); return; }
        
        // 설치경로 먼저 스캔해서, 현재 설치된 버전 정보 찾기
        installPath = null;
        installedVersion = null;
        try {
            installPath = getInstallPath();
            if(! installPath.exists()) {
                installPath.mkdirs();
                installedVersion = null;
            } else {
                File fileFlag = new File(installPath.getAbsolutePath() + File.separator + "installed.xml");
                if(! fileFlag.exists()) {
                    installedVersion = null;
                } else {
                    Properties propFlag = FileUtil.loadProperties(fileFlag, false);
                    if(propFlag == null) {
                        installedVersion = null;
                    } else {
                        try {
                            String sTitleFlag = propFlag.getProperty("configuration");
                            if(sTitleFlag.equals("updaterInstallation")) {
                                String strVer = propFlag.getProperty("version");
                                installedVersion = parseVersionString(strVer);
                                if(installedVersion.length < 4) {
                                    installedVersion = null;
                                } else {
                                    installedVersion = null;
                                }
                            }
                        } catch(Throwable txIn) {
                            txIn.printStackTrace();
                            installedVersion = null;
                        }
                    }
                }
            }
        } catch(Throwable tx) {
            tx.printStackTrace();
            System.exit(0);
            return;
        }
        
        prepareUI();
    }
    
    /** UI 세팅 */
    protected void prepareUI() {
        GUIUtil.setLookAndFeel("Nimbus");
        
        frame = new JFrame();
        frame.setTitle(target.getTitle());
        frame.setSize(400, 300);
        GUIUtil.centerWindow(frame);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel pnMain, pnUp, pnCenter, pnDown, pnCtrl;
        
        pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        frame.add(pnMain, BorderLayout.CENTER);
        
        pnUp     = new JPanel();
        pnCenter = new JPanel();
        pnDown   = new JPanel();
        
        pnUp.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        pnDown.setLayout(new BorderLayout());
        
        pnMain.add(pnUp    , BorderLayout.NORTH);
        pnMain.add(pnCenter, BorderLayout.CENTER);
        pnMain.add(pnDown  , BorderLayout.SOUTH);
        
        taNotice = new JEditorPane();
        taNotice.setEditable(false);
        taNotice.setContentType("text/html; charset=UTF-8");
        pnCenter.add(new JScrollPane(taNotice), BorderLayout.CENTER);
        
        prog = new JProgressBar();
        pnDown.add(prog, BorderLayout.NORTH);
        
        pnCtrl = new JPanel();
        pnCtrl.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnDown.add(pnCtrl, BorderLayout.CENTER);
        
        btnRun  = new JButton("실행");
        btnExit = new JButton("종료");
        
        pnCtrl.add(btnRun);
        pnCtrl.add(btnExit);
        
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                System.exit(0);
            }
        });
        
        btnRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onProcessRequested();
            }
        });
    }
    
    public void open() {
        btnRun.setEnabled(false);
        prog.setIndeterminate(true);
        frame.setVisible(true);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                readMainContent();
            }
        }).start();
    }
    
    /** 최초 실행 시, 메인URL로부터 설정과 버전 정보들을 읽어옴 */
    protected void readMainContent() {
        try {
            String resp = NetUtil.sendPost(new URL(target.getMainUrl()), new HashMap<>(), "application/json", "UTF-8");
            JsonObject json = (JsonObject) JsonObject.parseJson(resp.trim());
            json = (JsonObject) json.get(target.getSubType()); // Sub Type 꺼내기
            
            if(json == null) throw new NullPointerException("해당 소프트웨어의 타입을 확인할 수 없습니다.");
            
            String urlNotice = json.get("noticeKo").toString().trim();
            if(! urlNotice.startsWith("http")) urlNotice = target.getMainUrl() + "/" + urlNotice;
            taNotice.setPage(urlNotice);
            
            JsonArray builds = (JsonArray) json.get("builds");
            if(builds == null || builds.isEmpty()) throw new NullPointerException("해당 소프트웨어의 준비된 버전이 없습니다.");
            
            File fileInstalled = null;
            if(installedVersion != null) {
                fileInstalled = new File(getInstallPath().getAbsolutePath() + File.separator + target.getFileName());
                if(! fileInstalled.exists()) { fileInstalled = null; installedVersion = null; }
            }
            
            int[] versionMax = null;
            JsonObject buildSelected = null;
            if(installedVersion != null) versionMax = installedVersion;
            
            for(Object oBuildOne : builds) {
                JsonObject buildOne = (JsonObject) oBuildOne;
                String strVer = buildOne.get("version").toString().trim();
                int[] ver = parseVersionString(strVer);
                
                if(versionMax == null)     { versionMax = ver; buildSelected = buildOne; continue; }
                if(ver[0] > versionMax[0]) { versionMax = ver; buildSelected = buildOne; continue; }
                else if(ver[0] == versionMax[0] && ver[1] >  versionMax[1]) { versionMax = ver; buildSelected = buildOne; continue; }
                else if(ver[0] == versionMax[0] && ver[1] == versionMax[1] && ver[2] >  versionMax[2]) { versionMax = ver; buildSelected = buildOne; continue; }
                else if(ver[0] == versionMax[0] && ver[1] == versionMax[1] && ver[2] == versionMax[2] && ver[3] == versionMax[3]) { versionMax = ver; buildSelected = buildOne; continue; }
                else continue;
            }
            
            needDownload = true;
            if(buildSelected == null) {
                needDownload = false;
                if(installedVersion != null) {
                    // 이미 설치된 버전이 최신 버전인 케이스
                    fileToRun = fileInstalled;
                } else {
                    throw new NullPointerException("해당 소프트웨어의 준비된 버전이 없습니다.");
                }
            }
            
            if(buildSelected != null && needDownload) {
                // 업데이트를 해야하는 케이스
                strUrlUpdates = buildSelected.get("url").toString(); 
                if(! strUrlUpdates.startsWith("http")) strUrlUpdates = target.getMainUrl() + "/" + strUrlUpdates;
            }
        } catch(Throwable txc) {
            txc.printStackTrace();
            JOptionPane.showMessageDialog(frame, "오류 : " + txc.getMessage());
            frame.setVisible(false);
            System.exit(0);
        }
    }
    
    protected void onProcessRequested() {
        btnRun.setEnabled(false);
        prog.setIndeterminate(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                process();
            }
        }).start();
    }
    
    /** 소프트웨어 업데이트 및 실행 */
    public void process() {
        Runtime rt = Runtime.getRuntime();
        InputStream inp1 = null;
        FileOutputStream out1 = null;
        byte[] buffer = new byte[2048];
        try {
            if(! needDownload) {
                rt.exec("java -jar " + fileToRun.getAbsolutePath());
                System.exit(0);
                return;
            }
            
            File fileToDownload = new File(getInstallPath().getAbsolutePath() + File.separator + target.getFileName());
            out1 = new FileOutputStream(fileToDownload);
            
            int r;
            inp1 = new URL(strUrlUpdates).openStream();
            while(true) {
                r = inp1.read(buffer, 0, buffer.length);
                if(r < 0) break;
                out1.write(buffer, 0, r);
            }
            
            ClassUtil.closeAll(inp1, out1);
            inp1 = null; out1 = null;
            
            rt.exec("java -jar " + fileToDownload.getAbsolutePath());
            System.exit(0);
        } catch(Throwable txc) {
            txc.printStackTrace();
            JOptionPane.showMessageDialog(frame, "오류 : " + txc.getMessage());
        } finally {
            ClassUtil.closeAll(inp1, out1);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    btnRun.setEnabled(true);
                    prog.setIndeterminate(false);
                }
            });
        }
    }
    
    public static void main(String[] args) {
        SwingUpdater updater = new SwingUpdater();
        updater.open();
    }
    
    /** 버전 문자열을 버전 배열로 반환 */
    public static int[] parseVersionString(String versionString) {
        String str = versionString.toLowerCase().replace("version", "").replace("ver", "").replace("v", "").trim();
        int[] arr = new int[4];
        
        StringTokenizer dotTokenizer = new StringTokenizer(str, ".");
        for(int idx=0; idx<arr.length; idx++) {
            arr[idx] = Integer.parseInt(dotTokenizer.nextToken().trim());
        }
        return arr;
    }
    
    /** 버전 배열을 문자열로 변환 */
    public static String getVersionString(int[] num) {
        return num[0] + "." + num[1] + "." + num[2] + "." + num[3];
    }
}
