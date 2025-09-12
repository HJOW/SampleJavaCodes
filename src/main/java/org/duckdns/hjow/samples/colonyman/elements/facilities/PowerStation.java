package org.duckdns.hjow.samples.colonyman.elements.facilities;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class PowerStation extends DefaultFacility {
    private static final long serialVersionUID = 4079646708867981024L;
    protected String name = "발전소_" + ColonyManager.generateNaturalNumber();
    protected int capacity = 100;
    
    @Override
    public String getType() {
        return "PowerStation";
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
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100) {
        super.oneSecond(cycle, city, colony, efficiency100);
        
        // Do nothing on PowerStation (implemented on City class)
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
    @Override
    public int increasingCityMaxHP() {
        return 1;
    }
    @Override
    public String getStatusDescription(City city, Colony colony) {
        return ""; // TODO
    }
    @Override
    public int getPowerConsume() {
        return 0;
    }
    public int getPowerGenerate() {
        return getCapacity();
    }
    @Override
    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int c) {
        this.capacity = c;
    }
    @Override
    public int getComportGrade() {
        return 0;
    }
    @Override
    public int getMaxHp() {
        return 1000;
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
    public int getWorkerSuitability(Citizen citizen) {
        int point = 3;
        if(citizen.getCarisma()     >= 3) point += 1;
        if(citizen.getAgility()     >= 6) point += 2;
        if(citizen.getStrength()    >= 6) point += 2;
        if(citizen.getIntelligent() >= 6) point += 2;
        
        return point;
    }
}
