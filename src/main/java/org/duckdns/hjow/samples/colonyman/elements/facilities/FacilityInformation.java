package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.io.Serializable;

public class FacilityInformation implements Serializable {
    private static final long serialVersionUID = -5378970571423008845L;
    protected String name, description, title;
    protected Long price = new Long(0L);
    protected Long tech = new Long(0L);
    protected int buildingCycle = 1200;
    protected Class<?> facilityClass;
    public String getName() {
        return name;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public Class<?> getFacilityClass() {
        return facilityClass;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setFacilityClass(Class<?> facilityClass) {
        this.facilityClass = facilityClass;
    }
    public Long getPrice() {
        return price;
    }
    public int getBuildingCycle() {
        return buildingCycle;
    }
    public void setPrice(Long price) {
        this.price = price;
    }
    public void setBuildingCycle(int buildingCycle) {
        this.buildingCycle = buildingCycle;
    }
    public Long getTech() {
        return tech;
    }
    public void setTech(Long tech) {
        this.tech = tech;
    }
    @Override
    public String toString() {
        return getTitle();
    }
}
