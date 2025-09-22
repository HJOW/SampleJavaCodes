package org.duckdns.hjow.samples.colonyman.elements.research;

import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class BasicScience extends Research {
    private static final long serialVersionUID = -6067100861366848018L;
    public BasicScience() { super(); }

    @Override
    public String getName() {
        return "BasicScience";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 600L; }
    public double getMaxProgressIncreaseRate() { return 1.5;  }

    @Override
    public boolean isResearchAvail(Colony col) {
        return true;
    }

    @Override
    public String getTitle() {
        return "기초과학";
    }

}
