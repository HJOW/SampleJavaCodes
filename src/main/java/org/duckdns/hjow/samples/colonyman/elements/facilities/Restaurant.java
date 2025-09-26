package org.duckdns.hjow.samples.colonyman.elements.facilities;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyPanel;

public class Restaurant extends DefaultFacility implements ServiceFacility {
    private static final long serialVersionUID = -7371044845340026748L;
    
    protected String name = "식당_" + ColonyManager.generateNaturalNumber();
    protected int comportGrade = 0;
    protected int capacity = 30;

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
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int c) {
        capacity = c;
    }
    
    @Override
    public double additionalComportGradeRate(City city, Colony colony) {
        return 0.0;
    }
    
    /** 행사 주기 */
    protected int getProfitCycle() {
        return 60;
    }
    
    @Override
    public long usingFee() {
        return 5L;
    }

    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        super.oneSecond(cycle, city, colony, efficiency100, colPanel);
        
        if(cycle % getProfitCycle() == 0) {
            double efficiencyRate = efficiency100 / 100.0;
            double additionalRate = additionalComportGradeRate(city, colony);
            if(additionalRate < 0.0) additionalRate = 0.0;
            if(additionalRate != 0.0) {
                efficiencyRate = efficiencyRate + ((1.0 - efficiencyRate) * additionalRate);
            }
            if(efficiencyRate > 1.0) efficiencyRate = 1.0;
            
            int solvingHunger = 50;
            solvingHunger = (int) Math.round(solvingHunger * efficiencyRate);
            
            int compGrade = getComportGrade();
            compGrade = (int) Math.round(compGrade * efficiencyRate);
            
            int servicingCount = 0;
            for(Citizen c : city.getCitizens()) {
                if(c.getHunger() >= 80) continue;
                if(c.getMoney() < usingFee()) continue;
                
                long fee = usingFee();
                long tax = getTax(city, colony);
                
                servicingCount++;
                c.setMoney(c.getMoney() - fee - tax);
                c.setHunger(c.getHunger() + solvingHunger);
                colony.modifyingMoney(tax, city, colony, "세금 - " + getFacilityTitle());
                
                if(compGrade >= 2) {
                    c.setHappy(c.getHappy() + (compGrade / 2));
                }
                
                if(servicingCount >= getCapacity()) break;
            }
        }
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
        setCapacity(Integer.parseInt(json.get("capacity").toString()));
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        json.put("comportGrade", new Integer(getComportGrade()));
        json.put("capacity", new Integer(getCapacity()));
        
        return json;
    }
    
    public static String getFacilityName() {
        return "식당";
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return "식당으로, 시민들에게 유상으로 음식을 제공합니다.";
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
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) { return null; }
}
