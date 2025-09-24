package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.Facility;
import org.duckdns.hjow.samples.colonyman.elements.states.State;

public abstract class DefaultFacility implements Facility {
    private static final long serialVersionUID = 8012568139388326869L;
    protected volatile long key = ColonyManager.generateKey();
    protected int hp = getMaxHp();
    
    protected List<State> states = new Vector<State>();
    
    @Override
    public int getComportGrade() {
        return 0;
    }
    
    @Override
    public int getHp() {
        return hp;
    }
    
    @Override
    public void setHp(int hp) {
        this.hp = hp;
    }
    
    @Override
    public void addHp(int amount) {
        hp += amount;
        int mx = getMaxHp();
        if(hp >= mx) hp = mx;
        if(hp <   0) hp = 0;
    }
    
    @Override
    public int getMaxHp() {
        return 1000;
    }
    
    @Override
    public short getDefenceType() {
        return ColonyManager.DEFENCETYPE_BUILDING;
    }

    @Override
    public int getDefencePoint() {
        return 1;
    }
    
    @Override
    public long getKey() {
        return key;
    }
    
    public void setKey(long key) {
        this.key = key;
    }

    @Override
    public int getWorkingCitizensCount(City city, Colony colony) {
        int count = 0;
        
        for(Citizen c : city.getCitizens()) {
            if(getKey() == c.getWorkingFacility()) {
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    public List<Citizen> getWorkingCitizens(City city, Colony colony) {
        List<Citizen> list = new ArrayList<Citizen>();
        
        for(Citizen c : city.getCitizens()) {
            if(getKey() == c.getWorkingFacility()) {
                list.add(c);
            }
        }
        
        return list;
    }
    
    @Override
    public int getWorkerNeeded() {
        return 0;
    }
    @Override
    public int getWorkerCapacity() {
        return 0;
    }
    @Override
    public int increasingCityMaxHP() {
        return 0;
    }
    
    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100) {
        // State 영향력 동작
        for(State st : getStates()) {
            st.oneSecond(cycle, this, city, colony);
        }
        
        // State 수명 동작
        for(State st : getStates()) {
            st.oneSecond(cycle, city, colony, efficiency100);
        }
        
        // 수명 다된 state 제거
        int std = 0;
        while(std < getStates().size()) {
            State st = getStates().get(std);
            if(st.getHp() <= 0 || st.getLefts() <= 0) {
                getStates().remove(std);
                continue;
            }
            std++;
        }
    }
    
    @Override
    public long getSalary(City city, Colony colony) {
        return 1000L;
    }
    
    @Override
    public long getMaintainFee(City city, Colony colony) {
        return 1000L;
    }
    
    @Override
    public void fromJson(JsonObject json) {
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        
        JsonArray list = (JsonArray) json.get("states");
        states.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        JsonObject jsonObj = (JsonObject) o;
                        State stateOne = State.createStateInstance(jsonObj.get("type").toString());
                        if(stateOne == null) throw new NullPointerException("Cannot found these state type " + jsonObj);
                        
                        stateOne.fromJson(jsonObj);
                        states.add(stateOne);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        
        JsonArray list = new JsonArray();
        for(State s : getStates()) { list.add(s.toJson()); }
        json.put("states", list);
        
        return json;
    }
    
    public static String getFacilityName() {
        return "";
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return "";
    }
    
    public static Long getFacilityPrice() {
        return new Long(10000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(1200);
    }
    
    public static Long getTechNeeded() {
        return new Long(0);
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) { return null; }
    
    @Override
    public List<State> getStates() {
        return states;
    }
    
    public void setStates(List<State> states) {
        this.states = states;
    }

    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = new BigInteger(String.valueOf(getKey()));
        for(int idx=0; idx<getType().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getType().charAt(idx)))); }
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
        res = res.add(new BigInteger(String.valueOf(getHp())));
        for(State st : getStates()) { res = res.add(st.getCheckerValue()); }
        return res;
    }
}
