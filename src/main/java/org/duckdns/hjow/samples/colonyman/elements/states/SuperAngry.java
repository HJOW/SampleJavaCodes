package org.duckdns.hjow.samples.colonyman.elements.states;

import java.util.List;

import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;
import org.duckdns.hjow.samples.colonyman.elements.ColonyPanel;
import org.duckdns.hjow.samples.colonyman.elements.Facility;

public class SuperAngry extends State {
    private static final long serialVersionUID = -1359679198711687635L;

    @Override
    public String getName() {
        return "SuperAngry";
    }

    @Override
    public int getMaxHp() {
        return 10;
    }

    @Override
    public String getTitle() {
        return "Super Angry";
    }
    
    @Override
    public long getDefaultLefts() {
        return 120;
    }
    
    @Override
    public void oneSecond(int cycle, ColonyElements hosts, City city, Colony colony, ColonyPanel colPanel) {
        if(! (hosts instanceof Citizen)) return;
        Citizen ct = (Citizen) hosts;
        
        if(cycle % 10 == 0) {
            // 도시 내 랜덤한 시설에 대미지
            List<Facility> facList = city.getFacility();
            int selection = (int) (Math.random() * facList.size());
            
            Facility fac = facList.get(selection);
            fac.addHp(ct.getStrength());
            
            // 본인에게도 랜덤하게 피해
            if(Math.random() >= 0.3) {
                ct.addHp(-1);
            }
            
            // 행복도 증가 (스트레스 해소로 인함)
            if(Math.random() >= 0.3) {
                ct.addHappy(1);
                if(ct.getHappy() >= 30) { setHp(0); setLefts(0); } // 행복도 30 이상이면 즉시 중단
            }
        }
    }
}
