package org.duckdns.hjow.samples.colonyman;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

/** Colonization 설정 관리 클래스 */
public class ColonyManagerConfig implements Serializable {
    private static final long serialVersionUID = -3527963151987002687L;
    protected Map<String, Object> roots = new HashMap<String, Object>();
    public ColonyManagerConfig() { }
    
    /** 설정 값 반환, 해당 키가 없으면 null 이 리턴 */
    public Object get(String key) {
        return roots.get(key);
    }
    
    /** 설정 변경 */
    public void set(String key, Object obj) {
        roots.put(key, obj);
    }
    
    /** 설정 값 반환, 값이 Map 이어야 예외가 발생하지 않음. 해당 키가 없으면 null 이 리턴 */
    public ColonyManagerConfig getMap(String key) {
        Object obj = get(key);
        if(obj == null) return null;
        
        if(obj instanceof JsonObject) obj = toMap((JsonObject) obj);
        if(obj instanceof Map<?, ?>) {
            ColonyManagerConfig child = new ColonyManagerConfig();
            
            Map<?, ?> map = (Map<?, ?>) obj;
            Set<?> keys = map.keySet();
            for(Object k : keys) {
                Object val = map.get(k);
                
                child.getRoots().put(k.toString(), val);
            }
            return child;
        } else {
            throw new RuntimeException("This child " + key + " is not a map !");
        }
    }
    
    /** 설정 값 반환, 값이 List 이어야 예외가 발생하지 않음. 해당 키가 없으면 null 이 리턴 */
    public List<Object> getList(String key) {
        Object obj = get(key);
        if(obj == null) return null;
        
        if(obj instanceof JsonArray) obj = toList((JsonArray) obj);
        if(obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            List<Object> newList = new ArrayList<Object>();
            for(Object o : list) {
                
                // 객체가 Map 인 경우 ColonyManagerConfig 으로 변환해 탑재
                if(o instanceof Map<?, ?>) {
                    Map<?, ?> map = (Map<?, ?>) o;
                    Set<?> keys = map.keySet();
                    
                    ColonyManagerConfig child = new ColonyManagerConfig();
                    for(Object k : keys) {
                        child.set(k.toString(), map.get(k));
                    }
                    o = child;
                }
                
                newList.add(o);
            }
            return newList;
        } else {
            throw new RuntimeException("This child " + key + " is not a list !");
        }
    }
    
    /** 설정 값을 문자열로 취급하여 문자열 반환, 강제 형변환될 수 있음. null 일 경우 공란 반환 */
    public String getString(String key) {
        Object obj = roots.get(key);
        if(obj == null) return "";
        return obj.toString().trim();
    }
    
    /** 설정 값을 boolean 으로 취급하여 반환, 변환이 불가능한 값의 경우 예외가 발생함. 숫자 타입의 경우 0인 경우만 false, 그외에는 true 리턴 */
    public boolean getBool(String key) {
        Object obj = roots.get(key);
        if(obj == null) return false;
        if(obj instanceof Boolean) return ((Boolean) obj).booleanValue();
        if(obj instanceof Number ) {
            BigDecimal d = new BigDecimal(String.valueOf(obj));
            if(d.equals(BigDecimal.ZERO)) return false;
            return true;
        }
        
        String str = obj.toString().trim().toLowerCase();
        if(str.equals("y") || str.equals("yes") || str.equals("t") || str.equals("true" )) return true;
        if(str.equals("n") || str.equals("no" ) || str.equals("f") || str.equals("false")) return false;
        
        throw new RuntimeException("This value " + obj + " is not a boolean !");
    }
    
    /** 설정 값을 int 로 취급하여 반환, 변환 불가능한 경우 예외가 발생함 */
    public int getInt(String key) {
        Object obj = roots.get(key);
        if(obj == null) return 0;
        if(obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        String str = obj.toString().trim();
        return Integer.parseInt(str);
    }
    
    /** 설정 값을 double 로 취급하여 반환, 변환 불가능한 경우 예외가 발생함 */
    public double getDouble(String key) {
        Object obj = roots.get(key);
        if(obj == null) return 0;
        if(obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        String str = obj.toString().trim();
        return Double.parseDouble(str);
    }
    
    public Map<String, Object> getRoots() {
        return roots;
    }
    public void setRoots(Map<String, Object> roots) {
        this.roots = roots;
    }
    
    /** 설정 모두 삭제 */
    public void clear() {
        roots.clear();
    }
    
    /** JSON 에서 불러오기 (기존 데이터를 비우지 않고 덮어 씌움, 즉 키가 중복되지 않은 값은 남아있을 수 있음.) */
    public void fromJson(JsonObject json) {
        roots.putAll(toMap(json));
    }
    
    /** JSON 으로 변환 */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        Set<String> keys = roots.keySet();
        for(String k : keys) {
            json.put(k, roots.get(k));
        }
        return json;
    }
    
    /** JSON 객체를 Map으로 변환 */
    private Map<String, Object> toMap(JsonObject json) {
        Map<String, Object> maps = new HashMap<String, Object>();
        Set<String> keys = json.keySet();
        for(String k : keys) {
            Object val = json.get(k);
            
            if(val instanceof JsonObject) val = toMap((JsonObject) val);
            if(val instanceof JsonArray ) val = toList((JsonArray) val);
            maps.put(k, val);
        }
        
        return maps;
    }
    
    /** JSON 배열을 List으로 변환 */
    private List<Object> toList(JsonArray json) {
        List<Object> list = new ArrayList<Object>();
        for(Object val : json) {
            if(val instanceof JsonObject) val = toMap((JsonObject) val);
            if(val instanceof JsonArray ) val = toList((JsonArray) val);
            list.add(val);
        }
        return list;
    }
    
    /** 이 객체를 복제 */
    public ColonyManagerConfig cloneSelf() {
        ColonyManagerConfig newInst = new ColonyManagerConfig();
        newInst.getRoots().putAll(getRoots());
        return newInst;
    }
}
