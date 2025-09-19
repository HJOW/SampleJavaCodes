package org.duckdns.hjow.samples.colonyman.elements.research;

import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;

public abstract class Research implements ColonyElements {
    private static final long serialVersionUID = -3391024381630960804L;
    protected volatile long    key = ColonyManager.generateKey();
    protected volatile boolean active = false;
    protected volatile int     progress = 0;

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

    public boolean isActive() {
        return active;
    }

    public int getProgress() {
        return progress;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if(this.progress < 0) this.progress = 0;
        if(this.progress > getMaxProgress()) this.progress = getMaxProgress();
    }
    
    public void increaseProgress(int adds) {
        this.progress += adds;
        if(this.progress < 0) this.progress = 0;
        if(this.progress > getMaxProgress()) this.progress = getMaxProgress();
        if(this.progress >= getMaxProgress()) setActive(true);
    }
    
    public abstract int getMaxProgress();

    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100) { }
}
