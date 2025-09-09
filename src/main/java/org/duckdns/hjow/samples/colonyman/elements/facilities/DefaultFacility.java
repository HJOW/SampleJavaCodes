package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.Facility;

public abstract class DefaultFacility implements Facility {
    private static final long serialVersionUID = 8012568139388326869L;
    protected transient volatile long key = new Random().nextLong();
    protected int hp = getMaxHp();
    
    @Override
    public int getComportGrade() {
        return 0;
    }
    
    @Override
    public int getHp() {
        return hp;
    }
    
    @Override
    public void setHp(int hp) {
        this.hp = hp;
    }
    
    @Override
    public int getMaxHp() {
        return 1000;
    }
    
    @Override
    public long getKey() {
        return key;
    }
    
    @Override
    public int getWorkingCitizensCount(City city, Colony colony) {
        int count = 0;
        
        for(Citizen c : city.getCitizens()) {
            if(getKey() == c.getWorkingFacility()) {
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    public List<Citizen> getWorkingCitizens(City city, Colony colony) {
        List<Citizen> list = new ArrayList<Citizen>();
        
        for(Citizen c : city.getCitizens()) {
            if(getKey() == c.getWorkingFacility()) {
                list.add(c);
            }
        }
        
        return list;
    }
    
    @Override
    public int getWorkerNeeded() {
        return 0;
    }
    @Override
    public int getWorkerCapacity() {
        return 0;
    }
    @Override
    public int increasingCityMaxHP() {
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
}
