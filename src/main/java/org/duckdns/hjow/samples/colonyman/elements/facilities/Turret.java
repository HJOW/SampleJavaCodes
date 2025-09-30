package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.util.List;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.AttackableObject;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;
import org.duckdns.hjow.samples.colonyman.elements.enemies.Enemy;
import org.duckdns.hjow.samples.colonyman.elements.research.BasicBuildingTech;
import org.duckdns.hjow.samples.colonyman.elements.research.MilitaryTech;
import org.duckdns.hjow.samples.colonyman.elements.research.Research;
import org.duckdns.hjow.samples.colonyman.ui.ColonyPanel;

public class Turret extends DefaultFacility implements AttackableObject {
    private static final long serialVersionUID = -8553101924279880106L;
    protected String name = "터렛_" + ColonyManager.generateNaturalNumber();
    
    @Override
    public String getType() {
        return "Turret";
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
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
        return 100;
    }
    
    /** 대미지 처리 후 추가 작업 (상태를 부여한다거나 등등) 이 메소드에서 구현 */
    protected void processAfterAttack(int cycle, ColonyElements element, int finalDamage) { }
    
    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        super.oneCycle(cycle, city, colony, efficiency100, colPanel);
        
        int castLeft    = getAttackCount();
        int damages     = getDamage();
        int naturalized = damages;
        
        if(cycle % getAttackCycle() == 0) {
            List<Enemy> enemies = city.getEnemies();
            for(Enemy e : enemies) {
                if(e.getHp() >= 1) {
                    naturalized = ColonyManager.naturalizeDamage(this, e, damages);
                    e.addHp(naturalized * (-1));
                    processAfterAttack(cycle, e, naturalized);
                    castLeft--;
                    if(castLeft <= 0) break;
                }
            }
            
            if(castLeft >= 1) {
                enemies = colony.getEnemies();
                for(Enemy e : enemies) {
                    if(e.getHp() >= 1) {
                        naturalized = ColonyManager.naturalizeDamage(this, e, damages);
                        e.addHp(naturalized * (-1));
                        processAfterAttack(cycle, e, naturalized);
                        castLeft--;
                        if(castLeft <= 0) break;
                    }
                }
            }
        }
    }
    
    @Override
    public int increasingCityMaxHP() {
        return 3;
    }
    
    @Override
    public String getStatusDescription(City city, Colony colony) {
        return ""; // TODO
    }
    
    @Override
    public int getPowerConsume() {
        return 20;
    }
    
    @Override
    public int getComportGrade() {
        return 0;
    }
    @Override
    public int getMaxHp() {
        return 500;
    }
    @Override
    public int getWorkerNeeded() {
        return 1;
    }
    @Override
    public int getWorkerCapacity() {
        return 2;
    }
    
    @Override
    public short getAttackType() {
        return 0;
    }
    
    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 3;
        if(citizen.getCarisma()     >= 3) point += 1;
        if(citizen.getAgility()     >= 6) point += 2;
        if(citizen.getStrength()    >= 6) point += 2;
        if(citizen.getIntelligent() >= 6) point += 2;
        
        return point;
    }
    
    @Override
    public int getCapacity() {
        return 0;
    }
    
    @Override
    public void fromJson(JsonObject json) {
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        
        return json;
    }
    
    public static String getFacilityName() {
        return "터렛";
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return "기본적인 방어 시설입니다.";
    }
    
    public static Long getFacilityPrice() {
        return new Long(15000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(400);
    }
    
    public static Long getTechNeeded() {
        return new Long(10);
    }
    
    public static String getImageHex() {
        return null;
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) {
        boolean cond1 = false;
        boolean cond2 = false;
        
        List<Research> researches = col.getResearches();
        for(Research r : researches) {
            if(r instanceof MilitaryTech) {
                if(r.getLevel() >= 1) cond1 = true;
            }
            if(r instanceof BasicBuildingTech) {
                if(r.getLevel() >= 1) cond2 = true;
            }
        }
        
        if(! cond1) return "군사학 연구가 부족합니다.";
        if(! cond2) return "기초건축학 연구가 부족합니다.";
        return null;
    }
}
