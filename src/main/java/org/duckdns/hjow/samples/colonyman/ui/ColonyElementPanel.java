package org.duckdns.hjow.samples.colonyman.ui;

import java.awt.Component;

import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;

public interface ColonyElementPanel {
    public void setEditable(boolean editable);
    public void refresh(int cycle, City city, Colony colony, ColonyManager superInstance);
    public void dispose();
    public String getTargetName();
    public Component getComponent();
}
