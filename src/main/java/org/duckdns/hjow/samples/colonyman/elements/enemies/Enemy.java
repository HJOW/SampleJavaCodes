package org.duckdns.hjow.samples.colonyman.elements.enemies;

import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.AttackableObject;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;
import org.duckdns.hjow.samples.colonyman.elements.ColonyPanel;
import org.duckdns.hjow.samples.colonyman.elements.Facility;
import org.duckdns.hjow.samples.colonyman.elements.states.State;

/** 적 개체 */
public abstract class Enemy implements ColonyElements, AttackableObject {
    private static final long serialVersionUID = 8827673273232204593L;
    protected volatile long key = ColonyManager.generateKey();
    protected volatile int  hp  = getMaxHp();
    protected List<State> states = new Vector<State>();
    
    public Enemy() {}

    public long getKey() {
        return key;
    }

    public int getHp() {
        return hp;
    }
    
    public int getMaxHp() {
        return 100;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public void setHp(int hp) {
        this.hp = hp;
        int mx = getMaxHp();
        if(hp >  mx) hp = mx;
        if(hp <   0) hp = 0;
    }
    
    @Override
    public int getAttackCount() {
        return 1;
    }
    
    @Override
    public int getDamage() {
        return 1;
    }
    
    @Override
    public int getAttackCycle() {
        return 10;
    }
    
    @Override
    public short getAttackType() {
        return 0;
    }
    
    @Override
    public void addHp(int amount) {
        hp += amount;
        int mx = getMaxHp();
        if(hp >  mx) hp = mx;
        if(hp <   0) hp = 0;
    }
    
    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }
    
    /** 대미지 처리 후 추가 작업 (상태를 부여한다거나 등등) 이 메소드에서 구현 */
    protected void processAfterAttack(int cycle, ColonyElements element, int finalDamage) { }

    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        
        // 공격 처리
        int castLeft    = getAttackCount();
        int damages     = getDamage();
        int naturalized = damages;
        
        if(cycle % getAttackCycle() == 0) {
            // 시설 먼저 처리
            List<Facility> facs = city.getFacility();
            for(Facility fac : facs) {
                if(fac.getHp() >= 1) {
                    naturalized = ColonyManager.naturalizeDamage(this, fac, damages);
                    fac.addHp(naturalized * (-1));
                    processAfterAttack(cycle, fac, naturalized);
                    castLeft--;
                    if(castLeft <= 0) break;
                }
                
            }
            
            // 시설이 없으면 시민
            if(castLeft >= 1) {
                List<Citizen> citizens = city.getCitizens();
                for(Citizen ct : citizens) {
                    if(ct.getHp() >= 1) {
                        naturalized = ColonyManager.naturalizeDamage(this, ct, damages);
                        ct.addHp(naturalized * (-1));
                        processAfterAttack(cycle, ct, naturalized);
                        castLeft--;
                        if(castLeft <= 0) break;
                    }
                }
            }
            
            // 시설, 시민 모두 없으면 도시 자체
            if(castLeft >= 1) {
                if(city.getHp() >= 1) {
                    naturalized = ColonyManager.naturalizeDamage(this, city, damages);
                    processAfterAttack(cycle, city, naturalized);
                    city.addHp(naturalized * (-1));
                    castLeft--;
                }
            }
        }
    }
    
    @Override
    public void fromJson(JsonObject json) {
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
        json.put("type", getClass().getSimpleName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        
        JsonArray list = new JsonArray();
        for(State s : getStates()) { list.add(s.toJson()); }
        json.put("states", list);
        
        return json;
    }
    
    
    protected static List<Class<?>> enemyClasses = new Vector<Class<?>>();
    static {}
    
    /** type 를 읽어, 기본 Enemy 객체 생성 */
    public static Enemy createEnemyObject(String type) {
        Class<?> enemyClass = null;
        for(Class<?> classOne : enemyClasses) {
            if(classOne.getSimpleName().equals(type) || classOne.getName().equals(type)) {
                enemyClass = classOne;
                break;
            }
        }
        if(enemyClass == null) return null;
        try { return (Enemy) enemyClass.newInstance(); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
    }
    
    /** JSON을 읽어 Enemy 객체 생성 */
    public static Enemy createEnemyFromJson(JsonObject json) {
        String type = json.get("type").toString();
        
        Enemy en = createEnemyObject(type);
        if(en == null) return null;
        
        en.fromJson(json);
        return en;
    }
}
