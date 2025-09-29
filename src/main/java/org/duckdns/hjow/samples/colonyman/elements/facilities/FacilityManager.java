package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.elements.Facility;

public class FacilityManager {
    protected static List<FacilityInformation> facilities = new Vector<FacilityInformation>();
    
    static {
        register(ResidenceModule.class);
        register(PowerStation.class);
        register(Restaurant.class);
        register(Arcade.class);
        register(Factory.class);
        register(ResearchCenter.class);
        register(ArchitectOffice.class);
        register(BusStation.class);
        register(Turret.class);
        register(TownHouse.class);
    }
    
    public static Facility fromJson(JsonObject json) {
        String type = String.valueOf(json.get("type"));
        if(type.equals("City") || type.equals("Colony")) throw new RuntimeException("This object is not a Facility type.");
        
        Facility fac = null;
        
        Class<?> classes = getFacilityClass(json.get("type").toString());
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
        FacilityInformation info = new FacilityInformation();
        info.setFacilityClass(facilityClass);
        try {
            Method method = facilityClass.getMethod("getFacilityName");
            info.setName((String) method.invoke(null));
            
            method = facilityClass.getMethod("getFacilityTitle");
            info.setTitle((String) method.invoke(null));
            
            method = facilityClass.getMethod("getFacilityDescription");
            info.setDescription((String) method.invoke(null));
            
            method = facilityClass.getMethod("getFacilityPrice");
            info.setPrice((Long) method.invoke(null));
            
            method = facilityClass.getMethod("getTechNeeded");
            info.setTech((Long) method.invoke(null));
            
            method = facilityClass.getMethod("getFacilityBuildingCycle");
            info.setBuildingCycle((Integer) method.invoke(null));
            
            if(! facilities.contains(info)) facilities.add(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** 시설 이름으로 시설 클래스 찾기 */
    public static Class<?> getFacilityClass(String name) {
        for(FacilityInformation info : facilities) {
            if(info.getName().equals(name)) return info.getFacilityClass();
        }
        for(FacilityInformation info : facilities) {
            if(info.getFacilityClass().getName().equals(name)) return info.getFacilityClass();
        }
        for(FacilityInformation info : facilities) {
            if(info.getFacilityClass().getSimpleName().equals(name)) return info.getFacilityClass();
        }
        return null;
    }
    
    /** Facility 객체 생성 */
    public static Facility newFacilityObject(String name) {
        Class<?> facClass = getFacilityClass(name);
        if(facClass == null) return null;
        
        return newFacilityObject(facClass);
    }
    
    /** Facility 객체 생성 */
    public static Facility newFacilityObject(Class<?> facClass) {
        if(facClass == null) return null;
        
        try {
            Facility fac = (Facility) facClass.newInstance();
            return fac;
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /** 시설 정보 목록 반환 */
    public static List<FacilityInformation> getFacilityInformations() {
        List<FacilityInformation> newList = new Vector<FacilityInformation>();
        newList.addAll(facilities);
        return newList;
    }
    
    /** 시설 클래스 목록 반환 */
    public static List<Class<?>> getFacilityClasses() {
        List<Class<?>> newList = new Vector<Class<?>>();
        for(FacilityInformation info : facilities) {
            newList.add(info.getFacilityClass());
        }
        return newList;
    }
    
    /** 해당 시설 클래스로부터 이름 추출 */
    public static String getFacilityName(Class<?> facilityClass) {
        for(FacilityInformation info : facilities) {
            if(info.getFacilityClass() == facilityClass) {
                return info.getName();
            }
        }
        return null;
    }
    
    /** 해당 시설 클래스로부터 표시 이름 추출 */
    public static String getFacilityTitle(Class<?> facilityClass) {
        for(FacilityInformation info : facilities) {
            if(info.getFacilityClass() == facilityClass) {
                return info.getTitle();
            }
        }
        return null;
    }
    
    /** 해당 시설 클래스로부터 설명 추출 */
    public static String getFacilityDescription(Class<?> facilityClass) {
        for(FacilityInformation info : facilities) {
            if(info.getFacilityClass() == facilityClass) {
                return info.getDescription();
            }
        }
        return null;
    }
    
    /** 해당 시설 클래스로부터 설치 가격 추출 */
    public static long getFacilityPrice(Class<?> facilityClass) {
        for(FacilityInformation info : facilities) {
            if(info.getFacilityClass() == facilityClass) {
                return info.getPrice().longValue();
            }
        }
        return 0L;
    }
    
    /** 해당 시설 클래스로부터 설치 시간 추출 */
    public static int getFacilityBuildingCycle(Class<?> facilityClass) {
        for(FacilityInformation info : facilities) {
            if(info.getFacilityClass() == facilityClass) {
                return info.getBuildingCycle();
            }
        }
        return 0;
    }
}
