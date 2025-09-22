package org.duckdns.hjow.samples.colonyman.elements;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
public class HoldingJob implements Serializable {
    private static final long serialVersionUID = -8030473577462698183L;
    protected long   key       = ColonyManager.generateKey();
    protected int    cycleMax  = 0;
    protected int    cycleLeft = 0;
    protected String command   = null;
    protected String parameter = null;
    
    public HoldingJob() {
        
    }
    
    public HoldingJob(int cycleLeft, int cycleMax, String command, String parameter) {
        this();
        this.cycleLeft = cycleLeft;
        this.cycleMax = cycleMax;
        this.command = command;
        this.parameter = parameter;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public int getCycleMax() {
        return cycleMax;
    }

    public void setCycleMax(int cycleMax) {
        this.cycleMax = cycleMax;
        if(this.cycleMax < 0) this.cycleMax = 0;
    }

    public int getCycleLeft() {
        return cycleLeft;
    }

    public String getCommand() {
        return command;
    }
    
    public String getCommandTitle() {
        if("NewFacility".equals(getCommand())) return "건설";
        if("NewCitizen".equals(getCommand())) return "이주";
        return "작업";
    }

    public void setCycleLeft(int cycleLeft) {
        this.cycleLeft = cycleLeft;
        if(this.cycleLeft < 0) this.cycleLeft = 0;
    }
    
    public void decreaseCycle() {
        this.cycleLeft--;
        if(this.cycleLeft < 0) this.cycleLeft = 0;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    
    public List<Citizen> getWorkingCitizens(City city) {
        List<Citizen> citizens = new ArrayList<Citizen>();
        for(Citizen c : city.getCitizens()) {
            if(c.getBuildingFacility() == getKey()) citizens.add(c);
        }
        return citizens;
    }
    
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", "HoldingJob");
        json.put("key", new Long(getKey()));
        json.put("cycleMax", new Integer(getCycleMax()));
        json.put("cycleLeft", new Integer(getCycleLeft()));
        json.put("command"  , getCommand());
        json.put("parameter", getParameter());
        return json;
    }
    
    public void fromJson(JsonObject json) {
        if(! "HoldingJob".equals(json.get("type"))) throw new RuntimeException("This object is not HoldingJob type.");
        key = Long.parseLong(json.get("key").toString());
        setCycleMax(Integer.parseInt(json.get("cycleMax").toString()));
        setCycleLeft(Integer.parseInt(json.get("cycleLeft").toString()));
        setCommand(json.get("command").toString());
        setParameter(json.get("parameter").toString());
    }
    
    public BigInteger getCheckerValue() {
    	BigInteger res = new BigInteger(String.valueOf(getKey()));
    	res = res.add(new BigInteger(String.valueOf(getCycleLeft())));
    	for(int idx=0; idx<getCommand().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getCommand().charAt(idx)))); }
    	for(int idx=0; idx<getCommandTitle().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getCommandTitle().charAt(idx)))); }
    	for(int idx=0; idx<getParameter().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getParameter().charAt(idx)))); }
    	return res;
    }
}
