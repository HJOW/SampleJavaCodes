package org.duckdns.hjow.samples.colonyman.elements;

import org.duckdns.hjow.samples.colonyman.ColonyMan;

public interface ColonyElementPanel {
    public void setEditable(boolean editable);
    public void refresh(int cycle, City city, Colony colony, ColonyMan superInstance);
    public void dispose();
}
