package org.duckdns.hjow.samples.colonyman.elements.states;

import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;
import org.duckdns.hjow.samples.colonyman.elements.ColonyPanel;
import org.duckdns.hjow.samples.colonyman.elements.Facility;
import org.duckdns.hjow.samples.colonyman.elements.facilities.Home;

public class Influenza extends State {
    private static final long serialVersionUID = -4399295592737336716L;

    @Override
    public String getName() {
        return "Influenza";
    }

    @Override
    public int getMaxHp() {
        return 10;
    }

    @Override
    public String getTitle() {
        return "Influenza";
    }
    
    @Override
    public long getDefaultLefts() {
        return 1800;
    }
    
    /** 전염률 (직장 내 다른 시민과 거주 모듈 내 다른 시민은 매 사이클마다 적용, 그외 도시 내 시민에게는 10 사이클마다 적용) */
    public double contageousRate() {
        return 0.1;
    }
    
    /** 피해량 (10 사이클마다 적용) */
    public int damageRate() {
        return 1;
    }
    
    /** 숙주 체력 1 to 0 순간에는 이 확률이 적용 (이 확률에 당첨되어야 체력이 0으로 떨어짐) */
    public double deathRate() {
        return 0.1;
    }
    
    /** 자연치유 확률, 10 사이클마다 적용되며, 당첨 시 이 전염병의 HP가 1 소모 */
    public double naturalCuredRate() {
        return 0.1;
    }

    @Override
    public void oneCycle(int cycle, ColonyElements hosts, City city, Colony colony, ColonyPanel colPanel) {
        if(! (hosts instanceof Citizen)) return;
        Citizen ct = (Citizen) hosts;
        
        // 전염 구현
        Home     home    = ct.getLivingHome(city);
        Facility working = ct.getWorkingFacility(city);
        
        //    가정
        if(home != null) {
            for(Citizen others : home.getCitizens(city, colony)) {
                if(hosts.getKey() == others.getKey()) continue; // 나 자신은 제외
                
                // 전염률
                if(Math.random() >= contageousRate()) {
                    // 전염
                    others.getStates().add(new Influenza());
                }
            }
        }
        
        //    직장
        if(working != null) {
            for(Citizen others : working.getWorkingCitizens(city, colony)) {
                if(hosts.getKey() == others.getKey()) continue; // 나 자신은 제외
                
                // 전염률
                if(Math.random() >= contageousRate()) {
                    // 전염
                    others.getStates().add(new Influenza());
                }
            }
        }
        
        //    도시 내 시민
        if(cycle % 100 == 0) {
            for(Citizen others : city.getCitizens()) {
                if(hosts.getKey() == others.getKey()) continue; // 나 자신은 제외
                if(others.getWorkingFacility() == ct.getWorkingFacility()) continue; // 동일직장 제외 (위에서 이미 했으므로)
                if(others.getLivingHome()      == ct.getLivingHome()     ) continue; // 동일거주 제외 (위에서 이미 했으므로)
                
                // 전염률
                if(Math.random() >= contageousRate()) {
                    // 전염
                    others.getStates().add(new Influenza());
                }
            }
        }
        
        // 이 전염병이 끝나갈 때, 면역 상태 부여
        if(getHp() <= 1 || getLefts() <= 1) {
            ct.getStates().add(new ImmuneInfluenza());
            return;
        }
        
        // 자연치유 확률 적용
        if(cycle % 100 == 0) {
            if(Math.random() >= naturalCuredRate()) addHp(-1);
        }
        
        // 이 전염병의 증상 구현
        if(cycle % 100 == 0) {
            if(ct.getHp() > 1                   ) ct.addHp(-1);
            else if(Math.random() >= deathRate()) ct.addHp(-1);
        }
        
        if(ct.getHappy() > 20) ct.addHappy(-1);
    }
    
}
