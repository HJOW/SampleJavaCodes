package org.duckdns.hjow.samples.colonyman.elements.facilities;

import org.duckdns.hjow.samples.colonyman.elements.Facility;

public interface SupportGUIFacility extends Facility {
    public FacilityPanel createPanel();
    public boolean checkPanelAccept(FacilityPanel pn);
}
