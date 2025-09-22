package org.duckdns.hjow.samples.colonyman.elements.research;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;

public abstract class Research implements ColonyElements {
    private static final long serialVersionUID = -3391024381630960804L;
    protected volatile long    key = ColonyManager.generateKey();
    protected volatile long    progress = 0;
    protected volatile int     level    = 0;

    @Override
    public long getKey() {
        return key;
    }
    
    public void setKey(long key) {
        this.key = key;
    }

    @Override
    public int getHp() {
        return 100;
    }

    @Override
    public int getMaxHp() {
        return 100;
    }

    @Override
    public void setHp(int hp) { }

    @Override
    public void addHp(int amount) { }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
        if(this.progress < 0) this.progress = 0;
        if(this.progress > getMaxProgress()) this.progress = getMaxProgress();
    }
    
    public void increaseProgress(int adds) {
        this.progress += adds;
        if(this.progress < 0) this.progress = 0;
        if(this.progress > getMaxProgress()) this.progress = getMaxProgress();
        while(this.progress >= getMaxProgress()) {
            if(getLevel() < getMaxLevel()) {
                this.progress -= getMaxProgress();
                if(this.progress < 0) this.progress = 0;
                setLevel(getLevel() + 1);
            } else {
                this.progress = 0L;
                setLevel(getMaxLevel());
                break;
            }
        }
    }
    
    public abstract long getMaxProgress();

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    
    public int getMaxLevel() {
        return 1;
    }
    
    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100) { }
    
    public abstract boolean isResearchAvail(Colony col);
    public abstract String  getTitle();
    
    @Override
    public void fromJson(JsonObject json) {
        key = Long.parseLong(json.get("key").toString());
        setLevel(Integer.parseInt(json.get("level").toString()));
        setProgress(Long.parseLong(json.get("progress").toString()));
    }
    
    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type"    , getClass().getSimpleName());
        json.put("key"     , new Long(getKey()));
        json.put("level"   , new Integer(getLevel()));
        json.put("progress", new Long(getProgress()));
        return json;
    }
}
