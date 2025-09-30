package org.duckdns.hjow.samples.colonyman.events;

import java.util.List;

import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;
import org.duckdns.hjow.samples.colonyman.elements.states.SuperAngry;
import org.duckdns.hjow.samples.colonyman.ui.ColonyPanel;

public class Riot extends TimeEvent {
    private static final long serialVersionUID = 3877755419740712733L;

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
        return 1000L * 60 * 60 * 24;
    }

    @Override
    public double getOccurRate(ColonyElements target, Colony col, City city) {
        double av = city.getAverageHappiness();
        if(av <= 20) return 0.01;
        if(av <= 10) return 0.1;
        if(av <=  5) return 0.2;
        if(av <=  2) return 0.5;
        return 0;
    }
    
    @Override
    public void onEventOccured(ColonyElements target, Colony col, City city, ColonyPanel colPanel) {
        List<Citizen> all = city.getCitizens();
        
        if(all.isEmpty()) return;
        if(all.size() <= 2) return;
        double eachRate;
        int targetted = 0;
        
        for(Citizen ct : all) {
            int happy = ct.getHappy();
            if(happy >= 30) continue;
            
            eachRate = 0.0;
            if(happy <=  2) eachRate = 0.9;
            if(happy <=  5) eachRate = 0.8;
            if(happy <= 10) eachRate = 0.7;
            if(happy <= 15) eachRate = 0.5;
            if(happy <= 20) eachRate = 0.3;
            
            if(Math.random() <= eachRate) {
                SuperAngry angry = new SuperAngry();
                ct.getStates().add(angry);
                targetted++; 
            }
        }
        
        if(targetted >= 1) {
            ColonyManager.logGlobals("폭동이 일어났습니다.");
        }
    }

    @Override
    public String getTitle() {
        return "폭동";
    }

}
