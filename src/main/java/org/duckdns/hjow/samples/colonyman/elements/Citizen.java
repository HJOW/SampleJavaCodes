package org.duckdns.hjow.samples.colonyman.elements;

import java.text.DecimalFormat;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;

public class Citizen implements ColonyElements {
    private static final long serialVersionUID = -6856576686789163067L;
    protected transient volatile long key = ColonyManager.generateKey();
    protected String name = "시민_" + ColonyManager.generateNaturalNumber();
    
    protected int hp = 100;
    protected int hunger  = 50;
    protected int stamina = 50;
    protected int happy   = 50;
    
    protected int strength    = (int) (Math.random() * 5) + 4;
    protected int agility     = (int) (Math.random() * 5) + 4;
    protected int carisma     = (int) (Math.random() * 5) + 4;
    protected int intelligent = (int) (Math.random() * 5) + 4;
    
    protected long money           = 100L;
    protected long experience      =   0L;
    
    protected long workingFacility = 0L;
    protected long workingCity     = 0L;
    protected long livingHome      = 0L;
    
    public Citizen() {
        
    }
    
    public Citizen(JsonObject json) {
        super();
        fromJson(json);
    }

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
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100) {
        if(hp < 0) hp = 0;
        if(hp <= 0) return;
        
        boolean harm = false;
        int std;
        
        std = 10;
        if(cycle % std == 0) {
            if(livingHome < 0L) { // 집이 없으면
                happy--;
                hp--;
                harm = true;
            }
        }
        
        std = 100;
        if(cycle % std == 0) {
            hunger--;
        }
        if(hunger < 0) {
            hunger = 0;
        }
        
        if(hunger <= 0) {
            std = 5;
            if(cycle % std == 0) {
                hp--;
                if(efficiency100 < 50) hp--;
                harm = true;
            }
        } else if(hunger >= 50) {
            std = 50;
            if(cycle % std == 0 && hp < getMaxHp()) hp++;
        }
        
        std = 10000 / ( (101 - efficiency100) < 1 ? 1 : (101 - efficiency100) );
        if(cycle % std == 0) {
            happy--;
            if(happy <    0) happy =   0;
            if(happy >  100) happy = 100;
        }
        
        if(! harm) {
            hp++;
            int mx = getMaxHp();
            if(hp >= mx) hp = mx;
        }
    }
    
    @Override
    public void addHp(int amount) {
        hp += amount;
        int mx = getMaxHp();
        if(hp >  mx) hp = mx;
        if(hp <   0) hp = 0;
    }
    
    public void addHappy(int amount) {
        happy += amount;
        if(happy <    0) happy =   0;
        if(happy >  100) happy = 100;
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
        if(this.hunger <   0) this.hunger =   0;
        if(this.hunger > 100) this.hunger = 100;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
        if(this.stamina < 0) this.stamina = 0;
        if(this.stamina > getMaxStemina()) this.stamina = getMaxStemina();
    }
    
    public int getMaxStemina() {
        return 50 + (getAgility() / 2);
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
        if(this.money < 0) this.money = 0L;
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
        if(this.happy <   0) this.happy =   0;
        if(this.happy > 100) this.happy = 100;
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
    
    public int getMaxHp() {
        return 100 + (getStrength() / 5) + (getAgility() / 10);
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
    
    public void fromJson(JsonObject json) {
        if(! "Citizen".equals(json.get("type"))) throw new RuntimeException("This object is not Citizen type.");
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setMoney(Long.parseLong(json.get("money").toString()));
        
        setHunger(     Integer.parseInt(json.get("hunger"     ).toString()));
        setStamina(    Integer.parseInt(json.get("stamina"    ).toString()));
        setHappy(      Integer.parseInt(json.get("happy"      ).toString()));
        setStrength(   Integer.parseInt(json.get("strength"   ).toString()));
        setAgility(    Integer.parseInt(json.get("agility"    ).toString()));
        setCarisma(    Integer.parseInt(json.get("carisma"    ).toString()));
        setIntelligent(Integer.parseInt(json.get("intelligent").toString()));
        setExperience(     Long.parseLong(json.get("experience"     ).toString()));
        setWorkingFacility(Long.parseLong(json.get("workingFacility").toString()));
        setWorkingCity(    Long.parseLong(json.get("workingCity"    ).toString()));
        setLivingHome(     Long.parseLong(json.get("livingHome"     ).toString()));
    }
    
    public String getStatusString(City city, Colony colony, ColonyManager superInstance) {
        DecimalFormat formatterInt  = new DecimalFormat("#,###,###,###,###,##0");
        
        StringBuilder desc = new StringBuilder("");
        desc = desc.append("\n").append("자금 : ").append(formatterInt.format(getMoney()));
        desc = desc.append("\n").append("행복도 : ").append(formatterInt.format(getHappy())).append(" / ").append("100");
        
        Facility f = null;
        Facility h = null;
        
        if(getWorkingFacility() != 0L) f = city.getFacility(getWorkingFacility());
        if(getLivingHome()      != 0L) h = city.getFacility(getLivingHome());
        
        
        if(h != null) desc = desc.append("\n").append("거주 : ").append(h.getName());
        if(f != null) desc = desc.append("\n").append("직장 : ").append(f.getName());
        
        return desc.toString().trim();
    }
}
