package org.duckdns.hjow.samples.colonyman.elements.facilities;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyPanel;

public abstract class TransportStation extends DefaultFacility {
	private static final long serialVersionUID = 7222508474329385493L;
	protected String name = getDefaultName();
    protected int comportGrade = 0;
    
    protected String getDefaultName() {
    	return "";
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getPowerConsume() {
        return 1;
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        return 5;
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
    public String getName() {
        return name;
    }
    
    @Override
    public int getComportGrade() {
        return comportGrade;
    }
    
    public void setComportGrade(int g) {
        comportGrade = g;
    }
    
    /** 수익 발생 주기 */
    protected int getProfitCycle() {
        return 0;
    }
    
    @Override
    public long usingFee() {
        return 0L;
    }
    
    @Override
    public void oneCycle(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        super.oneCycle(cycle, city, colony, efficiency100, colPanel);
    }
}
