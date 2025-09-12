package org.duckdns.hjow.samples.colonyman.elements;

import java.awt.Component;

import org.duckdns.hjow.samples.colonyman.ColonyManager;

public interface ColonyElementPanel {
    public void setEditable(boolean editable);
    public void refresh(int cycle, City city, Colony colony, ColonyManager superInstance);
    public void dispose();
    public String getTargetName();
    public Component getComponent();
}
