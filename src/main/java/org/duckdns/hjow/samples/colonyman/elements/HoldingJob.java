package org.duckdns.hjow.samples.colonyman.elements;

import java.io.Serializable;

import org.duckdns.hjow.commons.json.JsonObject;
public class HoldingJob implements Serializable {
    private static final long serialVersionUID = -8030473577462698183L;
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
    
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", "HoldingJob");
        json.put("cycleMax", new Integer(getCycleMax()));
        json.put("cycleLeft", new Integer(getCycleLeft()));
        json.put("command"  , getCommand());
        json.put("parameter", getParameter());
        return json;
    }
    
    public void fromJson(JsonObject json) {
        if(! "HoldingJob".equals(json.get("type"))) throw new RuntimeException("This object is not HoldingJob type.");
        
        setCycleMax(Integer.parseInt(json.get("cycleMax").toString()));
        setCycleLeft(Integer.parseInt(json.get("cycleLeft").toString()));
        setCommand(json.get("command").toString());
        setParameter(json.get("parameter").toString());
    }
}
