package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.util.List;

import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.research.BasicBuildingTech;
import org.duckdns.hjow.samples.colonyman.elements.research.BasicScience;
import org.duckdns.hjow.samples.colonyman.elements.research.Research;

public class BusStation extends TransportStation {
	private static final long serialVersionUID = 7222508474329385493L;
    
    protected String getDefaultName() {
    	return "버스정류장_" + ColonyManager.generateNaturalNumber();
    }

    @Override
    public String getType() {
        return "BusStation";
    }

    @Override
    public String getStatusDescription(City city, Colony colony) {
        return "";
    }

    @Override
    public int getPowerConsume() {
        return 1;
    }

    @Override
    public int getCapacity() {
        return 100;
    }

    /** 수익 발생 주기 */
    protected int getProfitCycle() {
        return 0;
    }
    
    @Override
    public long usingFee() {
        return 0L;
    }
    
    public static String getFacilityName() {
        return "버스 정류장";
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return "기본적인 교통 수단입니다.\n2곳 이상을 건설해야 동작합니다.\n교통 한도를 증가시킵니다.\n교통 한도가 부족하면, 일부 시설에 직원이 통근할 수 없게 됩니다.";
    }
    
    public static Long getFacilityPrice() {
        return new Long(1000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(120);
    }
    
    public static Long getTechNeeded() {
        return new Long(0);
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) { 
        boolean cond1 = false;
        boolean cond2 = false;
        List<Research> researches = col.getResearches();
        for(Research r : researches) {
            if(r instanceof BasicScience) {
                if(r.getLevel() >= 1) cond1 = true;
            }
            if(r instanceof BasicBuildingTech) {
                if(r.getLevel() >= 1) cond2 = true;
            }
        }
        
        if(! cond1) return "기초과학 연구가 부족합니다.";
        if(! cond2) return "기초건축학 연구가 부족합니다.";
        return null;
    }
}
