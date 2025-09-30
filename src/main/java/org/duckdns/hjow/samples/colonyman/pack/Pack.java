package org.duckdns.hjow.samples.colonyman.pack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.duckdns.hjow.samples.colonyman.ColonyManager;

/** 여러 클래스 정보들을 담은 객채를 위한 클래스 */
public class Pack implements Serializable {
    private static final long serialVersionUID = -8964086871404887882L;
    protected String name = "Pack_" + ColonyManager.generateKey();
    protected String desc = "";
    protected boolean enabled = true;
    
    protected List<Class<?>> colonyClasses   = new ArrayList<Class<?>>();
    protected List<Class<?>> facilityClasses = new ArrayList<Class<?>>();
    protected List<Class<?>> researchClasses = new ArrayList<Class<?>>();
    protected List<Class<?>> enemyClasses    = new ArrayList<Class<?>>();
    protected List<Class<?>> stateClasses    = new ArrayList<Class<?>>();
    
    public Pack() { init(); }
    protected void init() {}
    
    public List<Class<?>> getColonyClasses() {
        return colonyClasses;
    }
    public List<Class<?>> getFacilityClasses() {
        return facilityClasses;
    }
    public List<Class<?>> getResearchClasses() {
        return researchClasses;
    }
    public List<Class<?>> getEnemyClasses() {
        return enemyClasses;
    }
    public List<Class<?>> getStateClasses() {
        return stateClasses;
    }
    public void setColonyClasses(List<Class<?>> colonyClasses) {
        this.colonyClasses = colonyClasses;
    }
    public void setFacilityClasses(List<Class<?>> facilityClasses) {
        this.facilityClasses = facilityClasses;
    }
    public void setResearchClasses(List<Class<?>> researchClasses) {
        this.researchClasses = researchClasses;
    }
    public void setEnemyClasses(List<Class<?>> enemyClasses) {
        this.enemyClasses = enemyClasses;
    }
    public void setStateClasses(List<Class<?>> stateClasses) {
        this.stateClasses = stateClasses;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    @Override
    public boolean equals(Object oth) {
        if(oth == null) return false;
        if(! (oth instanceof Pack)) return false;
        Pack otherPack = (Pack) oth;
        return otherPack.getName().equals(getName());
    }
}
