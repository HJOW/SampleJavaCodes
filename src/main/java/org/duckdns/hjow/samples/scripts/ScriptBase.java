package org.duckdns.hjow.samples.scripts;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.duckdns.hjow.samples.base.SampleJavaCodes;

/** 스크립트 엔진 관리 */
public class ScriptBase {
    protected static ScriptEngineManager manager = new ScriptEngineManager();
    protected static ScriptEngine rootEngine = null;
    
    /** 새 엔진 생성 */
    public static ScriptEngine newEngine() {
        ScriptEngine eng = manager.getEngineByName("JavaScript");
        eval(eng, basicScripts());
        return eng;
    }
    
    /** 초기 설정 */
    public static synchronized void init(SampleJavaCodes superInstance, List<ScriptObject> globalObjects) {
        if(globalObjects != null) {
            for(ScriptObject obj : globalObjects) { manager.put(obj.getName(), obj); }
        }
        BaseBroker bases = new BaseBroker(superInstance);
        manager.put("__base", bases);
        rootEngine = newEngine();
    }
    
    protected static String basicScripts() {
        StringBuilder res = new StringBuilder("");
        
        res = res.append("  var clone      = function(obj) { return __base.clone(obj); };           ").append("\n");
        res = res.append("  var create     = function(obj) { return __base.create(obj); };          ").append("\n");
        res = res.append("  var log        = function(obj) { __base.log(obj); };                    ").append("\n");
        res = res.append("  var list       = function(obj) { return __base.list(); };               ").append("\n");
        res = res.append("  var open       = function(obj) { __base.open(obj); };                   ").append("\n");
        res = res.append("  var getProgram = function(obj) { return __base.getProgram(obj); };      ").append("\n");
        res = res.append("  var exit       = function(obj) { __base.exit(); };                      ").append("\n");
        
        return res.toString().trim();
    }
    
    public static Object eval(ScriptEngine engine, String scripts) {
        if(scripts == null) return null;
        
        scripts = scripts.trim();
        if(scripts.equals("")) return null;
        
        try {
            if(scripts.equals("list")) scripts = "list()";
            if(scripts.equals("exit")) scripts = "exit()";
            
            if(engine == null) return rootEngine.eval(scripts);
            return engine.eval(scripts);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
