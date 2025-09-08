package org.duckdns.hjow.samples.colonyman.elements;

import java.io.Serializable;

import org.duckdns.hjow.commons.json.JsonObject;

public interface ColonyElements extends Serializable {
    public long getKey();
    public String getName();
    public int getHp();
    public void oneSecond(int cycle, City city, Colony colony);
    public JsonObject toJson();
}
