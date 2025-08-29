package org.duckdns.hjow.samples.base;

import java.awt.Window;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.duckdns.hjow.samples.cryptor.GCypher;
import org.duckdns.hjow.samples.img2base64.GUIImage2Base64Converter;
import org.duckdns.hjow.samples.interfaces.LineListener;
import org.duckdns.hjow.samples.util.ResourceUtil;

public class SampleJavaCodes {
    public static void main(String[] args) {
        SampleJavaCodes obj;
        
        obj = new GUISampleJavaCodes();
        obj.init(obj);
        obj.loadSamples(obj);
        obj.applySampleList(obj);
        obj.run(new Properties());
    }
    
    protected List<Program> programs = new Vector<Program>();
    public SampleJavaCodes() {
        
    }
    
    /** 프로그램들을 다시 불러옵니다. */
    public void loadSamples(final SampleJavaCodes superInstance) {
        disposeAllPrograms();
        
        addProgram(superInstance, new GUIImage2Base64Converter(superInstance));
        addProgram(superInstance, new GCypher(superInstance));
        // addProgram(new GUITextConverter(superInstance));
        
        ResourceUtil.loadResource("/program.txt", '#', new LineListener() {   
            @SuppressWarnings("unchecked")
            @Override
            public void onEachLine(String line) {
                try { addProgram(superInstance, (Class<Program>) Class.forName(line)); } catch(Throwable t) { log("Failed to load program " + line); log("    Error : " + t.getMessage()); }
            }
        });
    }
    
    public synchronized void addProgram(SampleJavaCodes superInst, Program prgm) {
        boolean dupl = false;
        for(Program pg : programs) {
            if(! prgm.getClass().equals(pg.getClass())) continue;
            if(! prgm.getName().equals(pg.getName())) continue;
            dupl = true; break;
        }
        if(dupl) return;
        
        programs.add(prgm);
    }
    
    public synchronized void addProgram(SampleJavaCodes superInst, Class<Program> prgmClass) {
        try {
            Constructor<Program> cons = prgmClass.getConstructor(SampleJavaCodes.class);
            Program prgm = cons.newInstance(superInst);
            addProgram(superInst, prgm);
        } catch(NoSuchMethodException ex) { log("Failed to load program " + prgmClass); log("    Error : " + ex.getMessage()); } catch(Exception ex) { ex.printStackTrace(); log("Failed to load program " + prgmClass); log("    Error : " + ex.getMessage()); }
    }
    
    public void init(SampleJavaCodes superInstance) { }
    public void applySampleList(SampleJavaCodes superInstance) { }
    public void run(Properties args) { }
    
    public Window getWindow() { return null; }
    public void log(String msg) { System.out.println(msg); }
    
    public synchronized void disposeAllPrograms() {
        for(Program pg : programs) { try { pg.dispose(); } catch(Throwable t) { log("Error : " + t.getMessage()); } }
        programs.clear();
    }
}
