package org.duckdns.hjow.samples.colonyman.elements.facilities;

import org.duckdns.hjow.samples.colonyman.ColonyMan;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.Facility;

public interface SupportGUIFacility extends Facility {
    public FacilityPanel createPanel(City city, Colony colony, ColonyMan superInstance);
    public boolean checkPanelAccept(FacilityPanel pn);
}
