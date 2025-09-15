package org.duckdns.hjow.samples.colonyman.elements.facilities;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class ResearchCenter extends DefaultFacility {
    private static final long serialVersionUID = 9084689175126703785L;
    protected String name = "연구소_" + ColonyManager.generateNaturalNumber();

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return "ResearchCenter";
    }

    @Override
    public String getStatusDescription(City city, Colony colony) {
        return "";
    }

    @Override
    public int getPowerConsume() {
        return 15;
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 0;
        if(citizen.getCarisma()     >= 3) point += 2;
        if(citizen.getAgility()     >= 4) point += 2;
        if(citizen.getStrength()    >= 4) point += 2;
        if(citizen.getIntelligent() >= 7) point += 4;
        
        return point;
    }

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100) {
        super.oneSecond(cycle, city, colony, efficiency100);
        
        // 업무 처리
        int increases = 10;
        increases = (int) (increases * ( efficiency100 / 100.0 ));
        
        colony.setTech(colony.getTech() + increases);
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
        return "연구 모듈";
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return "기술 개발 시설입니다.";
    }
    
    public static Long getFacilityPrice() {
        return new Long(30000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(1800);
    }
    
    public static Long getTechNeeded() {
        return new Long(0);
    }
}
