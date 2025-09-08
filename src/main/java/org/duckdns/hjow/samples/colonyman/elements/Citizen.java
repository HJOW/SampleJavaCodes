package org.duckdns.hjow.samples.colonyman.elements;

import java.util.Random;

import org.duckdns.hjow.commons.json.JsonObject;

public class Citizen implements ColonyElements {
    private static final long serialVersionUID = -6856576686789163067L;
    protected transient volatile long key = new Random().nextLong();
    protected String name = "시민_" + new Random().nextInt();
    
    protected int hp = 100;
    protected int hunger  =  0;
    protected int stamina =  0;
    protected int happy   = 50;
    
    protected int strength    = (int) (Math.random() * 4) + 5;
    protected int agility     = (int) (Math.random() * 4) + 5;
    protected int carisma     = (int) (Math.random() * 4) + 5;
    protected int intelligent = (int) (Math.random() * 4) + 5;
    
    protected long money           = 100L;
    protected long experience      =   0L;
    
    protected long workingFacility = -1L;
    protected long workingCity     = -1L;
    protected long livingHome      = -1L;

    @Override
    public long getKey() {
        return key;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void oneSecond(int cycle, City city, Colony colony) {
        if(hp < 0) hp = 0;
        if(hp <= 0) return;
        
        if(cycle % 10 == 0) {
            if(livingHome < 0L) { // 집이 없으면
                happy--;
            }
        }
        if(cycle % 100 == 0) {
            hunger--;
        }
        if(hunger < 0) {
            hunger = 0;
            hp--;
        }
    }

    @Override
    public int getHp() {
        return hp;
    }

    public int getHunger() {
        return hunger;
    }

    public int getStamina() {
        return stamina;
    }

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public int getIntelligent() {
        return intelligent;
    }

    public long getMoney() {
        return money;
    }

    public long getWorkingFacility() {
        return workingFacility;
    }

    public long getWorkingCity() {
        return workingCity;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public void setIntelligent(int intelligent) {
        this.intelligent = intelligent;
    }

    public int getCarisma() {
        return carisma;
    }

    public void setCarisma(int carisma) {
        this.carisma = carisma;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public void setWorkingFacility(long workingFacility) {
        this.workingFacility = workingFacility;
    }

    public void setWorkingCity(long workingCity) {
        this.workingCity = workingCity;
    }

    public int getHappy() {
        return happy;
    }

    public void setHappy(int happy) {
        this.happy = happy;
    }

    public long getLivingHome() {
        return livingHome;
    }

    public void setLivingHome(long livingHome) {
        this.livingHome = livingHome;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }
    
    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", "Citizen");
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        
        json.put("hp"                , new Integer(getHp()));
        json.put("hunger"            , new Integer(getHunger()));
        json.put("stamina"           , new Integer(getStamina()));
        json.put("happy"             , new Integer(getHappy()));
        json.put("strength"          , new Integer(getStrength()));
        json.put("agility"           , new Integer(getAgility()));
        json.put("carisma"           , new Integer(getCarisma()));
        json.put("intelligent"       , new Integer(getIntelligent()));
        json.put("money"             , new Long(getMoney()));
        json.put("experience"        , new Long(getExperience()));
        json.put("workingFacility"   , new Long(getWorkingFacility()));
        json.put("workingCity"       , new Long(getWorkingCity()));
        json.put("livingHome"        , new Long(getLivingHome()));
        
        return json;
    }
}
