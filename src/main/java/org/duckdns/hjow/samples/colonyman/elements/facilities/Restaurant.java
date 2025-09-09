package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.util.Random;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class Restaurant extends DefaultFacility {
    private static final long serialVersionUID = -7371044845340026748L;
    
    protected String name = "식당_" + new Random().nextInt();
    protected int comportGrade = 0;

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getMaxHp() {
        return 1000;
    }

    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100) {
        
    }

    @Override
    public String getType() {
        return "Restaurant";
    }

    @Override
    public int increasingCityMaxHP() {
        return 1;
    }

    @Override
    public String getStatusDescription(City city, Colony colony) {
        // TODO
        return "";
    }

    @Override
    public int getPowerConsume() {
        return 1;
    }

    @Override
    public int getComportGrade() {
        return comportGrade;
    }
    
    public void setComportGrade(int g) {
        comportGrade = g;
    }

    @Override
    public int getWorkerNeeded() {
        return 3;
    }

    @Override
    public int getWorkerCapacity() {
        return 5;
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 5;
        if(citizen.getCarisma()     >= 7) point += 2;
        if(citizen.getAgility()     >= 6) point += 1;
        if(citizen.getStrength()    >= 6) point += 1;
        if(citizen.getIntelligent() >= 4) point += 1;
        
        return point;
    }

    @Override
    public void fromJson(JsonObject json) {
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setComportGrade(Integer.parseInt(json.get("comportGrade").toString()));
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        json.put("comportGrade", new Integer(getComportGrade()));
        
        return json;
    }
}
