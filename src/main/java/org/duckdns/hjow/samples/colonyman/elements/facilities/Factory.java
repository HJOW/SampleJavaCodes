package org.duckdns.hjow.samples.colonyman.elements.facilities;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class Factory extends DefaultFacility {
    private static final long serialVersionUID = 8465140770981665970L;
    protected String name = "공장_" + ColonyManager.generateNaturalNumber();
    protected int capacity = 10;
    
    public Factory() {
        
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return "Factory";
    }

    @Override
    public String getStatusDescription(City city, Colony colony) {
        
        return "";
    }

    @Override
    public int getPowerConsume() {
        return 10;
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 0;
        if(citizen.getCarisma()     >= 1) point += 1;
        if(citizen.getAgility()     >= 5) point += 4;
        if(citizen.getStrength()    >= 6) point += 4;
        if(citizen.getIntelligent() >= 4) point += 2;
        
        return point;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int c) {
        capacity = c;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100) {
        super.oneSecond(cycle, city, colony, efficiency100);
        
        // 공장 업무 처리
        int increases = getCapacity();
        increases = (int) (increases * ( efficiency100 / 100.0 ));
        
        colony.modifyingMoney(increases, city, this, "work");
    }
    
    @Override
    public void fromJson(JsonObject json) {
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setCapacity(Integer.parseInt(json.get("capacity").toString()));
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        json.put("capacity", new Integer(getCapacity()));
        
        return json;
    }
    
    public static String getFacilityName() {
        return "생산 시설";
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return "기본적인 생산 시설입니다. 이 곳에서 생산된 물품으로 정착지의 재정에 수익이 발생합니다.";
    }
    
    public static Long getFacilityPrice() {
        return new Long(20000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(1200);
    }
    
    public static Long getTechNeeded() {
        return new Long(0);
    }
}
