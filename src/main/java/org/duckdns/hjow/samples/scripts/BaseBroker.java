package org.duckdns.hjow.samples.scripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.duckdns.hjow.samples.base.GUIProgram;
import org.duckdns.hjow.samples.base.SampleJavaCodes;

public class BaseBroker implements ScriptObject {
    private static final long serialVersionUID = -8650341902220364844L;
    protected SampleJavaCodes superInstance;
    
    public BaseBroker(SampleJavaCodes superInstance) {
        this.superInstance = superInstance;
    }
    
    @Override
    public void dispose() {
        superInstance = null;
    }

    @Override
    public String getName() {
        return "base";
    }
    
    public void log(Object msg) {
        superInstance.log(String.valueOf(msg));
    }
    
    public Object create(Object className) throws Exception {
        Class<?> classObj = Class.forName(className.toString());
        return classObj.newInstance();
    }
    
    public Object clone(Object befores) {
        if(befores == null) return null;
        if(befores instanceof List<?>) { 
            List<Object> list = new ArrayList<Object>();
            for(Object obj : (List<?>) befores) {
                list.add(obj);
            }
            return list;
        } else if(befores instanceof Map<?, ?>) {
            Map<?, ?> mapObj = (Map<?, ?>) befores;
            Set<?> keys = mapObj.keySet();
            Map<Object, Object> newMap = new HashMap<Object, Object>();
            for(Object obj : keys) {
                newMap.put(obj, mapObj.get(keys));
            }
            
            return newMap;
        } else if(befores instanceof String) {
            return new String((String) befores);
        } else if(befores instanceof Integer) {
            return new Integer( ((Integer) befores).intValue() );
        } else if(befores instanceof Double) {
            return new Double( ((Double) befores).doubleValue() );
        }
                
        
        throw new RuntimeException("This object does not support 'clone' !");
    }
    
    public GUIProgram getProgram(Object name) {
        return superInstance.getProgram(name.toString());
    }
    
    public void open(Object name) {
        GUIProgram progrm = superInstance.getProgram(name.toString());
        if(progrm == null) throw new NullPointerException("There is no program named '" + name + "'.");
        progrm.open(superInstance);
    }
    
    public List<String> list() {
        return superInstance.getProgramNames();
    }
    
    public void exit() {
        superInstance.exit();
    }
}
