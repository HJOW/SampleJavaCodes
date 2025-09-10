package org.duckdns.hjow.samples.colonyman.elements;

import java.util.List;

public interface Facility extends ColonyElements {
    public void setName(String name);
    public String getType();
    public int increasingCityMaxHP();
    public String getStatusDescription(City city, Colony colony);
    public int getPowerConsume();
    public int getComportGrade();
    public int getWorkingCitizensCount(City city, Colony colony);
    public List<Citizen> getWorkingCitizens(City city, Colony colony);
    public int getWorkerNeeded();
    public int getWorkerCapacity();
    public int getWorkerSuitability(Citizen citizen);
    public int getCapacity();
}
