package org.duckdns.hjow.samples.colonyman.elements.facilities;

import java.util.List;

import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.Facility;

public interface Home extends Facility {
    public int getCapacity();
    public List<Citizen> getCitizens(City city, Colony colony);
    public boolean isFull(City city, Colony colony);
}
