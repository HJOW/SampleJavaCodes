package org.duckdns.hjow.samples.colonyman.elements;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

public class Colony implements ColonyElements {
    private static final long serialVersionUID = -3144963237818493111L;
    protected transient volatile long key = new Random().nextLong();
    
    protected List<City> cities = new Vector<City>();
    protected String name = "정착지_" + new Random().nextInt();
    protected int  hp    = 1000000;
    protected long money = 1000000L;
    
    public Colony() {
        
    }
    
    public Colony(File f) {
        
    }

    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    @Override
    public long getKey() {
        return key;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    @Override
    public void oneSecond(int cycle, City city, Colony colony) { // parameters are null
        for(City c : cities) {
            c.oneSecond(cycle, c, this);
        }
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", "Colony");
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        json.put("money", new Long(getMoney()));
        
        JsonArray list = new JsonArray();
        for(City c : cities) { list.add(c.toJson()); }
        json.put("cities", list);
        return json;
    }
}
