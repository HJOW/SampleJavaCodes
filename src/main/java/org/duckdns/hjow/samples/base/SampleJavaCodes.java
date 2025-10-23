package org.duckdns.hjow.samples.base;

import java.awt.Window;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.duckdns.hjow.commons.util.DataUtil;
import org.duckdns.hjow.samples.FileUniqueNumberChecker;
import org.duckdns.hjow.samples.charsetconv.ui.GUIConverterManager;
import org.duckdns.hjow.samples.console.ConsoleStream;
import org.duckdns.hjow.samples.console.ConsoleTerminal;
import org.duckdns.hjow.samples.cryptor.GCypher;
import org.duckdns.hjow.samples.img2base64.GUIImage2Base64Converter;
import org.duckdns.hjow.samples.img2base64.GUIImage2HexConverter;
import org.duckdns.hjow.samples.interfaces.LineListener;
import org.duckdns.hjow.samples.scripts.ScriptBase;
import org.duckdns.hjow.samples.svndirdel.SVNDirDelete;
import org.duckdns.hjow.samples.textconvert.GUITextConverter;
import org.duckdns.hjow.samples.util.ResourceUtil;
import org.duckdns.hjow.subprogram.DBConsole;

public class SampleJavaCodes {
    public static void main(String[] args) {
        // 매개변수 수집
        StringBuilder params = new StringBuilder("");
        if(args != null) {
            for(String a : args) {
                params = params.append(" ").append(a);
            }
        }
        
        String paramStr = params.toString().trim();
        params = null;
        
        // Map 으로 변환
        Map<String, String> argMap = new HashMap<String, String>();
        if(DataUtil.isNotEmpty(paramStr)) argMap.putAll(DataUtil.parseParameter(paramStr));
        params = null;
        
        // 실행
        SampleJavaCodes obj;
        
        if(argMap.get("dbconsole") != null) {
            if(DataUtil.parseBoolean(argMap.get("dbconsole"))) {
                DBConsole.main(args);
                return;
            }
        }
        
        boolean gui = true;
        if(argMap.get("GUI") != null) gui = Boolean.parseBoolean(argMap.get("GUI"));
        
        if(gui) obj = new GUISampleJavaCodes();
        else    obj = new SampleJavaCodes();
        obj.init(obj);
        obj.loadSamples(obj);
        obj.applySampleList(obj);
        
        Properties prop = new Properties();
        Set<String> keys = argMap.keySet();
        for(String k : keys) { String val = argMap.get(k); if(val != null) prop.setProperty(k, val); }
        
        obj.run(prop);
    }
    
    protected List<GUIProgram> programs = new Vector<GUIProgram>();
    protected ScriptBase scriptBase = new ScriptBase();
    protected ConsoleStream mainStream = null;
    
    public SampleJavaCodes() {
        
    }
    
    /** 프로그램들을 다시 불러옵니다. */
    public void loadSamples(final SampleJavaCodes superInstance) {
        disposeAllPrograms();
        
        addProgram(superInstance, new GUIImage2Base64Converter(superInstance));
        addProgram(superInstance, new GUIImage2HexConverter(superInstance));
        addProgram(superInstance, new GCypher(superInstance));
        addProgram(superInstance, new GUITextConverter(superInstance));
        addProgram(superInstance, new SVNDirDelete(superInstance));
        addProgram(superInstance, new GUIConverterManager(superInstance));
        addProgram(superInstance, new FileUniqueNumberChecker(superInstance));
        
        ResourceUtil.loadResource("/program.txt", '#', new LineListener() {   
            @SuppressWarnings("unchecked")
            @Override
            public void onEachLine(String line) {
                try { addProgram(superInstance, (Class<GUIProgram>) Class.forName(line)); } catch(Throwable t) { log("Failed to load program " + line); log("    Error : " + t.getMessage()); }
            }
        });
    }
    
    public synchronized void addProgram(SampleJavaCodes superInst, GUIProgram prgm) {
        boolean dupl = false;
        for(GUIProgram pg : programs) {
            if(! prgm.getClass().equals(pg.getClass())) continue;
            if(! prgm.getName().equals(pg.getName())) continue;
            dupl = true; break;
        }
        if(dupl) return;
        
        programs.add(prgm);
    }
    
    public synchronized void addProgram(SampleJavaCodes superInst, Class<GUIProgram> prgmClass) {
        try {
            Constructor<GUIProgram> cons = prgmClass.getConstructor(SampleJavaCodes.class);
            GUIProgram prgm = cons.newInstance(superInst);
            addProgram(superInst, prgm);
        } catch(NoSuchMethodException ex) { log("Failed to load program " + prgmClass); log("    Error : " + ex.getMessage()); } catch(Exception ex) { ex.printStackTrace(); log("Failed to load program " + prgmClass); log("    Error : " + ex.getMessage()); }
    }
    
    public GUIProgram getProgram(String name) {
        for(GUIProgram pg : programs) {
            if(name.equals(pg.getName())) return pg;
        }
        return null;
    }
    
    public List<String> getProgramNames() {
        List<String> names = new ArrayList<String>();
        for(GUIProgram pg : programs) { names.add(pg.getName()); }
        return names;
    }
    
    public void init(SampleJavaCodes superInstance) { }
    public void applySampleList(SampleJavaCodes superInstance) {}
    
    /** 콘솔 표준 입출력 객체 반환. GUI 모드인 경우 null 을 반환. */
    public ConsoleStream getMainStream() {
        return mainStream;
    }
    
    /** 콘솔 모드 구동 (GUI 모드는 GUISampleJavaCodes 클래스에서 오버라이딩하며, 이 경우 mainStream 필드는 사용하지 않음) */
    public void run(Properties args) {
        mainStream = new ConsoleStream();
        
        ConsoleTerminal terminal = new ConsoleTerminal(this, mainStream);
        List<String> listMenu = new ArrayList<String>();
        while(true) {
            try {
                listMenu.clear();
                listMenu.add("샘플 실행");
                listMenu.add("종료");
                int r = terminal.askMenu("메인 메뉴", listMenu);
                
                if(r == 0) menuProgramList(args);
                if(r == 1) break; // 종료
            } catch(Exception ex) {
                ex.printStackTrace();
                break;
            }
        }
        
        if(mainStream != null) mainStream.println("END");
        
        if(terminal   != null) terminal.dispose();   terminal   = null;
        if(mainStream != null) mainStream.dispose(); mainStream = null;
        exit();
    }
    
    public void menuProgramList(Properties args) {
        ConsoleTerminal terminal = new ConsoleTerminal(this, mainStream);
        List<String> listMenu = new ArrayList<String>();
        while(true) {
            try {
                listMenu.clear();
                listMenu.add("[뒤로]");
                for(int idx=0; idx<programs.size(); idx++) {
                    listMenu.add(programs.get(idx).getTitle());
                }
                
                int r = terminal.askMenu("샘플 선택", listMenu);
                if(r == 0) break;
                else programs.get(r).open(this);
                
            } catch(Exception ex) {
                ex.printStackTrace();
                break;
            }
        }
        
        terminal.dispose();
    }
    
    public void exit() {
        disposeAllPrograms();
        if(mainStream != null) { try { mainStream.close(); mainStream = null; } catch(Exception ex) { ex.printStackTrace(); } }
        System.exit(0);
    }
    
    public Window getWindow() { return null; }
    public void log(String msg) { System.out.println(msg); }
    
    public synchronized void disposeAllPrograms() {
        for(GUIProgram pg : programs) { try { pg.dispose(); } catch(Throwable t) { log("Error : " + t.getMessage()); } }
        programs.clear();
    }
}
