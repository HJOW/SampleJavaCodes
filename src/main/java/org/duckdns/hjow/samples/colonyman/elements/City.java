package org.duckdns.hjow.samples.colonyman.elements;

import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

public class City implements ColonyElements {
    private static final long serialVersionUID = -8442328554683565064L;
    protected transient volatile long key = new Random().nextLong();
    
    protected String name = "도시_" + new Random().nextInt();
    protected List<Facility> facility = new Vector<Facility>();
    protected List<Citizen>  citizens = new Vector<Citizen>();
    protected int hp = getMaxHp();
    
    @Override
    public long getKey() {
        return key;
    }
    @Override
    public String getName() {
        return name;
    }
    public List<Facility> getFacility() {
        return facility;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setFacility(List<Facility> facility) {
        this.facility = facility;
    }
    public int getHp() {
        return hp;
    }
    public void setHp(int hp) {
        this.hp = hp;
        if(this.hp > getMaxHp()) this.hp = getMaxHp();
    }
    public int getMaxHp() {
        int max = 100000;
        for(Facility f : facility) {
            max += f.increasingCityMaxHP();
        }
        return max;
    }
    @Override
    public void oneSecond(int cycle, City city, Colony colony) { // city should be a self
        for(Facility f : facility) {
            f.oneSecond(cycle, this, colony);
        }
    }
    
    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", "City");
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        
        JsonArray list = new JsonArray();
        for(Facility f : facility) { list.add(f.toJson()); }
        json.put("facilities", list);
        
        list = new JsonArray();
        for(Citizen c : citizens) { list.add(c.toJson()); }
        json.put("citizens", list);
        return json;
    }
}
