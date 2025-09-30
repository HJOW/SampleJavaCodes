package org.duckdns.hjow.samples.colonyman.elements.facilities;

import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.Facility;
import org.duckdns.hjow.samples.colonyman.ui.FacilityPanel;

public interface SupportGUIFacility extends Facility {
    public FacilityPanel createPanel(City city, Colony colony, ColonyManager superInstance);
    public boolean checkPanelAccept(FacilityPanel pn);
}
