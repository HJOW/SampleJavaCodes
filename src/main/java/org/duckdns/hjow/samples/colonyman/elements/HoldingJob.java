package org.duckdns.hjow.samples.colonyman.elements;

import java.io.Serializable;

import org.duckdns.hjow.commons.json.JsonObject;
public class HoldingJob implements Serializable {
    private static final long serialVersionUID = -8030473577462698183L;
    protected int    cycleLeft = 0;
    protected String command   = null;
    protected String parameter = null;
    
    public HoldingJob() {
        
    }
    
    public HoldingJob(int cycleLeft, String command, String parameter) {
        this();
        this.cycleLeft = cycleLeft;
        this.command = command;
        this.parameter = parameter;
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
        json.put("cycleLeft", new Integer(getCycleLeft()));
        json.put("command"  , getCommand());
        json.put("parameter", getParameter());
        return json;
    }
    
    public void fromJson(JsonObject json) {
        if(! "HoldingJob".equals(json.get("type"))) throw new RuntimeException("This object is not HoldingJob type.");
        
        setCycleLeft(Integer.parseInt(json.get("cycleLeft").toString()));
        setCommand(json.get("command").toString());
        setParameter(json.get("parameter").toString());
    }
}
