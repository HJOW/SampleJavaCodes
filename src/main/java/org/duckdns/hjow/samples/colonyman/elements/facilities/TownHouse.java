package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.util.List;

import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.research.BasicBuildingTech;
import org.duckdns.hjow.samples.colonyman.elements.research.BasicScience;
import org.duckdns.hjow.samples.colonyman.elements.research.Research;

public class TownHouse extends Residence {
    private static final long serialVersionUID = 6619376624827956402L;
    
    public TownHouse() {}
    
    protected String defaultName() {
        return "타운하우스_" + ColonyManager.generateNaturalNumber();
    }

    @Override
    public int increasingCityMaxHP() {
        return 2;
    }

    @Override
    public String getStatusDescription(City city, Colony colony) {
        return "";
    }
    
    @Override
    public int getMaxHp() {
        return 2000;
    }
    
    @Override
    public int getPowerConsume() {
        return 2;
    }

    @Override
    public int getCapacity() {
        return 12;
    }
    
    @Override
    public String getType() {
        return "TownHouse";
    }

    @Override
    public double additionalComportGradeRate(City city, Colony colony) {
        return 0.0;
    }

    public static String getFacilityName() {
        return "타운하우스";
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return "규모가 큰 주거 모듈로, 더 많은 시민이 거주할 수 있습니다.";
    }
    
    public static Long getFacilityPrice() {
        return new Long(20000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(360);
    }
    
    public static Long getTechNeeded() {
        return new Long(20);
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) { 
        boolean cond1 = false;
        boolean cond2 = false;
        List<Research> researches = col.getResearches();
        for(Research r : researches) {
            if(r instanceof BasicScience) {
                if(r.getLevel() >= 5) cond1 = true;
            }
            if(r instanceof BasicBuildingTech) {
                if(r.getLevel() >= 2) cond2 = true;
            }
        }
        
        if(! cond1) return "기초과학 연구가 부족합니다.";
        if(! cond2) return "기초건축학 연구가 부족합니다.";
        return null;
    }
}
