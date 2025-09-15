package org.duckdns.hjow.samples.colonyman.elements.facilities;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class Arcade extends DefaultFacility {
    private static final long serialVersionUID = 6472512678804457223L;
    protected String name = "아케이드_" + ColonyManager.generateNaturalNumber();
    protected int comportGrade = 0;
    protected int capacity = 100;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return "Arcade";
    }

    @Override
    public String getStatusDescription(City city, Colony colony) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public int getPowerConsume() {
        return 10;
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 1;
        if(citizen.getCarisma()     >= 3) point += 1;
        if(citizen.getAgility()     >= 6) point += 3;
        if(citizen.getStrength()    >= 6) point += 1;
        if(citizen.getIntelligent() >= 6) point += 2;
        
        return point;
    }
    
    @Override
    public void fromJson(JsonObject json) {
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setCapacity(Integer.parseInt(json.get("capacity").toString()));
        setComportGrade(Integer.parseInt(json.get("comportGrade").toString()));
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        json.put("capacity", new Integer(getCapacity()));
        json.put("comportGrade", new Integer(getComportGrade()));
        
        return json;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    @Override
    public int getComportGrade() {
        return comportGrade;
    }
    
    public void setComportGrade(int g) {
        comportGrade = g;
    }

    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100) {
        super.oneSecond(cycle, city, colony, efficiency100);
        
        int servicingCount = 0;
        for(Citizen c : city.getCitizens()) {
            if(c.getHappy() >= 100) continue;
            if(c.getMoney() < 2) continue;
            
            servicingCount++;
            c.setHappy(c.getHappy() + 5 + (getComportGrade() / 2));
            c.setMoney(c.getMoney() - 2);
            
            if(servicingCount >= getCapacity()) break;
        }
    }

    public static String getFacilityName() {
        return "아케이드";
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return "시민들의 행복을 위한 기본적인 오락 시설입니다.";
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
}
