package org.duckdns.hjow.samples.colonyman.events;

import java.util.ArrayList;
import java.util.List;

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
        return 1000L * 60 * 60 * 24;
    }

    @Override
    public double getOccurRate(ColonyElements target, Colony col, City city) {
        return 0.1;
    }

    @Override
    public void onEventOccured(ColonyElements target, Colony col, City city, ColonyPanel colPanel) {
        List<Citizen> all     = city.getCitizens();
        List<Citizen> already = new ArrayList<Citizen>();
        
        if(all.isEmpty()) return;
        
        // 랜덤 시민에게 발생
        int infected = 0;  // 최초 발생 완료된 시민 수
        int starts   = 5;  // 최초 발생 목표 수
        double rate = 0.1; // 해당 시민 선정 확률 (getOccurRate 하고는 다름 ! 이미 발생은 확정된 상황에서 그중 첫타자로 당첨될 확률) 
        
        if(starts >= all.size()) starts = all.size() - 1;
        if(starts < 1) starts = 1;
        
        while(infected < starts) {
            for(Citizen ct : all) {
                if(already.contains(ct)) continue;
                
                Influenza vs = new Influenza();
                if(Math.random() >= (1.0 - rate)) { 
                    ct.getStates().add(vs);
                    already.add(ct);
                    infected++; 
                    
                    if(infected >= starts) break;
                }
            }
        }
        
        if(infected >= 1) {
            colPanel.log("전염병 발생이 감지되었습니다.");
        }
    }

    @Override
    public String getTitle() {
        return "전염병 - 독감";
    }
    
}
