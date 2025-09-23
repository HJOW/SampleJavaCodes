package org.duckdns.hjow.samples.colonyman.elements.states;

import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;

public class ImmuneInfluenza extends State {
    private static final long serialVersionUID = 8482202600166530470L;

    @Override
    public String getName() {
        return "[Immune]Influenza";
    }

    @Override
    public int getMaxHp() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getTitle() {
        return "[Immune]Influenza";
    }
    
    @Override
    public long getDefaultLefts() {
        return 3600;
    }

    @Override
    public void oneSecond(int cycle, ColonyElements hosts, City city, Colony colony) {
        if(! (hosts instanceof Citizen)) { setHp(0); return; }
        Citizen ct = (Citizen) hosts;
        
        // Influenza 제거
        for(State st : ct.getStates()) {
            if(st instanceof Influenza) st.setHp(0);
        }
    }
}
