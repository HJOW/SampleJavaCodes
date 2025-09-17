package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class Residence extends DefaultFacility implements Home {
    private static final long serialVersionUID = -2930901725309688206L;
    
    protected String name = "보급형_주거모듈_" + ColonyManager.generateNaturalNumber();
    protected int comportGrade = 0;

    public Residence() {
        
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int increasingCityMaxHP() {
        return 1;
    }

    @Override
    public String getStatusDescription(City city, Colony colony) {
        return "";
    }
    
    @Override
    public int getMaxHp() {
        return 1000;
    }
    
    @Override
    public int getPowerConsume() {
        return 1;
    }

    @Override
    public int getCapacity() {
        return 5;
    }
    
    @Override
    public String getType() {
        return "Residence";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setComportGrade(int comportGrade) {
        this.comportGrade = comportGrade;
    }
    
    @Override
    public int getComportGrade() {
        return comportGrade;
    }

    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100) {
        super.oneSecond(cycle, city, colony, efficiency100);
        
        int cycleComport = 20;
        int compGrade = getComportGrade();
        
        if(efficiency100 < 10) {
            if(cycle % 600 == 0) {
                for(Citizen c : getCitizens(city, colony)) {
                    c.addHp(-1);
                }
            }
        } else {
            compGrade = (int) Math.round( compGrade * (efficiency100 / 100.0) );
            if(compGrade >= 10) cycleComport -= 10;
            else                cycleComport -= compGrade;
            
            if(cycle % (cycleComport * 60) == 0) {
                for(Citizen c : getCitizens(city, colony)) {
                    c.addHappy(1);
                }
            }
        }
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

    @Override
    public List<Citizen> getCitizens(City city, Colony colony) {
        List<Citizen> lists = new Vector<Citizen>();
        for(Citizen c : city.getCitizens()) {
            if(c.getLivingHome() == getKey()) {
                lists.add(c);
            }
        }
        return lists;
    }
    
    @Override
    public boolean isFull(City city, Colony colony) {
        if(getCapacity() <= getCitizens(city, colony).size()) return true;
        return false;
    }

    @Override
    public int getWorkingCitizensCount(City city, Colony colony) {
        return 0;
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        return 0;
    }

    @Override
    public List<Citizen> getWorkingCitizens(City city, Colony colony) {
        return new ArrayList<Citizen>();
    }
    
    public static String getFacilityName() {
        return "주거 모듈";
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return "기본적인 주거 모듈로 시민이 거주하는 데 필요한 기본적인 시설이 포함됩니다.";
    }
    
    public static Long getFacilityPrice() {
        return new Long(10000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(180);
    }
    
    public static Long getTechNeeded() {
        return new Long(0);
    }
}
