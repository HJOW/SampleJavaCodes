package org.duckdns.hjow.samples.colonyman.elements.facilities;

import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;

public interface ServiceFacility {
    public double additionalComportGradeRate(City city, Colony colony);
    public int getComportGrade();
}
