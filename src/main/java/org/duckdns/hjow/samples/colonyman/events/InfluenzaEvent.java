package org.duckdns.hjow.samples.colonyman.events;

import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;
import org.duckdns.hjow.samples.colonyman.elements.ColonyPanel;
import org.duckdns.hjow.samples.colonyman.elements.states.Influenza;

public class InfluenzaEvent extends TimeEvent {
    private static final long serialVersionUID = -5377286479366944281L;

    @Override
    public short getEventSize() {
        return TimeEvent.EVENTSIZE_CITY;
    }

    @Override
    public int getOccurCycle(Colony col, City city) {
        return 100;
    }

    @Override
    public long getOccurMinimumTime(Colony col) {
        return 100000;
    }

    @Override
    public double getOccurRate(ColonyElements target, Colony col, City city) {
        return 0.01;
    }

    @Override
    public void onEventOccured(ColonyElements target, Colony col, City city, ColonyPanel colPanel) {
        colPanel.log("전염병이 창궐이 감지되었습니다.");
        for(Citizen ct : city.getCitizens()) {
            Influenza vs = new Influenza();
            ct.getStates().add(vs);
        }
    }

    @Override
    public String getTitle() {
        return "전염병 - 독감";
    }
    
}
