package org.duckdns.hjow.samples.colonyman.elements.states;
import java.math.BigInteger;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;

/** 시민, 혹은 시설의 상태 하나를 지칭하는 객체를 위한 클래스 */
public abstract class State implements ColonyElements {
    private static final long serialVersionUID = -8452951686397752158L;
    protected volatile long key = ColonyManager.generateKey();
    protected volatile int  hp = 1;
    protected volatile long lefts = Long.MAX_VALUE;

    @Override
    public long getKey() {
        return key;
    }
    
    public void setKey(long key) {
        this.key = key;
    }

    @Override
    public int getHp() {
        return hp;
    }
    
    @Override
    public void setHp(int h) {
        this.hp = h;
    }

    @Override
    public void addHp(int amount) {
        setHp(getHp() + amount);
        if(getHp() < 0) setHp(0);
        if(getHp() > getMaxHp()) setHp(getMaxHp());
    }

    public long getLefts() {
        return lefts;
    }

    public void setLefts(long lefts) {
        this.lefts = lefts;
    }

    @Override
    public final void oneSecond(int cycle, City city, Colony colony, int efficiency100) {
        // 이 메소드는 수정하거나 오버라이드하지 말 것 !
        
        if(lefts < Long.MAX_VALUE) lefts--;
        if(lefts < 0) lefts = 0L;
        if(lefts == 0) setHp(0);
    }
    
    public abstract String getTitle();
    
    /** 1 사이클마다 쓰레드에서 호출됨. */
    public abstract void oneSecond(int cycle, ColonyElements hosts, City city, Colony colony);

    @Override
    public void fromJson(JsonObject json) {
        String clsName = getClass().getSimpleName();
        if(! clsName.equals(json.get("type"))) throw new RuntimeException("This object is not " + clsName + " type.");
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setLefts(Long.parseLong(json.get("lefts").toString()));
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", getClass().getSimpleName());
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Integer(getHp()));
        json.put("lefts", new Long(getLefts()));
        return json;
    }

    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = new BigInteger(String.valueOf(getKey()));
        res = res.add(new BigInteger(String.valueOf(getHp())));
        res = res.add(new BigInteger(String.valueOf(getLefts())));
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
        return res;
    }

    protected static List<Class<?>> stateClasses = new Vector<Class<?>>();
    public static State createStateInstance(String type) {
        Class<?> stateClass = null;
        
        for(Class<?> classOne : stateClasses) {
            if(classOne.getName().equals(type)) { stateClass = classOne; break; }
        }
        
        if(stateClass == null) {
            for(Class<?> classOne : stateClasses) {
                if(classOne.getSimpleName().equals(type)) { stateClass = classOne; break; }
            }
        }
        
        if(stateClass != null) {
            try { return (State) stateClass.newInstance(); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
        }
        return null;
    }
}
