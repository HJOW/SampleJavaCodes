package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.lang.reflect.Method;
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
        register(Factory.class);
        register(ResearchCenter.class);
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
    
    /** 시설 클래스 등록 */
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
    
    /** 시설 클래스 목록 반환 */
    public static List<Class<?>> getFacilityClasses() {
        List<Class<?>> newList = new Vector<Class<?>>();
        newList.addAll(facilityClasses);
        return newList;
    }
    
    /** 해당 시설 클래스로부터 이름 추출 */
    public static String getFacilityName(Class<?> facilityClass) {
        for(Class<?> classes : facilityClasses) {
            if(classes == facilityClass) {
                try {
                    Method method = facilityClass.getMethod("getFacilityName");
                    return (String) method.invoke(null);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return null;
    }
    
    /** 해당 시설 클래스로부터 설명 추출 */
    public static String getFacilityDescription(Class<?> facilityClass) {
        for(Class<?> classes : facilityClasses) {
            if(classes == facilityClass) {
                try {
                    Method method = facilityClass.getMethod("getFacilityDescription");
                    return (String) method.invoke(null);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return null;
    }
    
    /** 해당 시설 클래스로부터 설치 가격 추출 */
    public static long getFacilityPrice(Class<?> facilityClass) {
        for(Class<?> classes : facilityClasses) {
            if(classes == facilityClass) {
                try {
                    Method method = facilityClass.getMethod("getFacilityPrice");
                    return ((Long) method.invoke(null)).longValue();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return 0L;
    }
    
    /** 해당 시설 클래스로부터 설치 시간 추출 */
    public static int getFacilityBuildingCycle(Class<?> facilityClass) {
        for(Class<?> classes : facilityClasses) {
            if(classes == facilityClass) {
                try {
                    Method method = facilityClass.getMethod("getFacilityBuildingCycle");
                    return ((Integer) method.invoke(null)).intValue();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return 0;
    }
}
