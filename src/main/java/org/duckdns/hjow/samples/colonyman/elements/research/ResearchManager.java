package org.duckdns.hjow.samples.colonyman.elements.research;

import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyClassLoader;
import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class ResearchManager {
    protected static List<Class<?>> classes = ColonyClassLoader.researchClasses();
    
    public static Research createResearchInstance(String type) {
        for(Class<?> classOne : classes) {
            if(type.equalsIgnoreCase(classOne.getName()) || type.equalsIgnoreCase(classOne.getSimpleName())) {
                try { return (Research) classOne.newInstance(); } catch(Exception ex) { ex.printStackTrace(); }
            }
        }
        return null;
    }
    
    public static Research fromJson(String json) {
        JsonObject jsonObj = (JsonObject) JsonObject.parseJson(json);
        return fromJson(jsonObj);
    }
    
    public static Research fromJson(JsonObject json) {
        Research   instances = createResearchInstance(json.get("type").toString());
        instances.fromJson(json);
        return instances;
    }
    
    public static List<Research> initList(Colony col) {
        List<Research> list = new ArrayList<Research>();
        for(Class<?> classOne : classes) {
            try { Research newInst = (Research) classOne.newInstance(); list.add(newInst); } catch(Exception ex) { ex.printStackTrace(); };
        }
        return list;
    }
    
}
