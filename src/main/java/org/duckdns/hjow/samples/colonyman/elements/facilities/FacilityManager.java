package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.elements.Facility;

public class FacilityManager {
    protected static List<Class<?>> facilityClasses = new Vector<Class<?>>();
    
    static {
        register(Residence.class);
        register(PowerStation.class);
        register(Restaurant.class);
        register(Arcade.class);
    }
    
    public static Facility fromJson(JsonObject json) {
        String type = String.valueOf(json.get("type"));
        if(type.equals("City") || type.equals("Colony")) throw new RuntimeException("This object is not a Facility type.");
        
        Facility fac = null;
        Class<?> classes = null;
        
        for(Class<?> classOne : facilityClasses) {
            if(classOne.getName().endsWith("." + json.get("type"))) {
                classes = classOne;
                break;
            }
        }
        if(classes == null) return null;
        
        try {
            fac = (Facility) classes.newInstance();
            fac.fromJson(json);
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        
        return fac;
    }
    
    public static void register(Class<?> facilityClass) {
        facilityClasses.add(facilityClass);
    }
    
    public static Class<?> getFacilityClass(String name) {
        for(Class<?> classes : facilityClasses) {
            if(classes.getName().equals(name)) return classes;
        }
        for(Class<?> classes : facilityClasses) {
            if(classes.getSimpleName().equals(name)) return classes;
        }
        return null;
    }
}
