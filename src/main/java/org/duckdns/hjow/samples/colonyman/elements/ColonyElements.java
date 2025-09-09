package org.duckdns.hjow.samples.colonyman.elements;

import java.io.Serializable;

import org.duckdns.hjow.commons.json.JsonObject;

public interface ColonyElements extends Serializable {
    public long getKey();
    public String getName();
    public int getHp();
    public int getMaxHp();
    public void setHp(int hp);
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100);
    public void fromJson(JsonObject json);
    public JsonObject toJson();
}
