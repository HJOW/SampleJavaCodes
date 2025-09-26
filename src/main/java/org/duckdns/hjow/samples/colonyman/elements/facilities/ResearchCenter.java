package org.duckdns.hjow.samples.colonyman.elements.facilities;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.Citizen;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyPanel;
import org.duckdns.hjow.samples.colonyman.elements.research.Research;

public class ResearchCenter extends DefaultFacility {
    private static final long serialVersionUID = 9084689175126703785L;
    protected String name = "연구소_" + ColonyManager.generateNaturalNumber();
    protected long researchKey = 0L;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return "ResearchCenter";
    }

    @Override
    public String getStatusDescription(City city, Colony colony) {
        return "";
    }

    @Override
    public int getPowerConsume() {
        return 15;
    }

    @Override
    public int getWorkerSuitability(Citizen citizen) {
        int point = 0;
        if(citizen.getCarisma()     >= 3) point += 2;
        if(citizen.getAgility()     >= 4) point += 2;
        if(citizen.getStrength()    >= 4) point += 2;
        if(citizen.getIntelligent() >= 7) point += 4;
        
        return point;
    }
    
    @Override
    public int getWorkerNeeded() {
        return 3;
    }
    @Override
    public int getWorkerCapacity() {
        return 5;
    }

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public long getResearchKey() {
        return researchKey;
    }

    public void setResearchKey(long researchKey) {
        this.researchKey = researchKey;
    }
    
    /** 진행 중인 연구 반환 */
    public Research getResearch(Colony colony) {
        if(getResearchKey() == 0L) return null;
        for(Research r : colony.getResearches()) {
            if(r.getKey() == getResearchKey()) {
                return r;
            }
        }
        return null;
    }
    
    /** 테크 포인트 증가 사이클 */
    protected int getTechPointIncreaseCycle() {
        return 100;
    }
    
    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) {
        super.oneSecond(cycle, city, colony, efficiency100, colPanel);
        
        // 업무 처리
        int increases = 10;
        increases = (int) (increases * ( efficiency100 / 100.0 ));
        
        // 테크 수치 올리기
        if(cycle % getTechPointIncreaseCycle() == 0) colony.setTech(colony.getTech() + increases);
        
        // 진행 중인 연구 처리
        if(getResearchKey() != 0L) {
            Research research = getResearch(colony);
            
            if(research != null) {
                research.oneSecond(cycle, city, colony, efficiency100, colPanel);
                
                increases = 1;
                double incFloat = (increases * (efficiency100 / 100.0));
                
                boolean lvUp = false;
                if(incFloat < 1) {
                    increases = 1;
                    if(Math.random() >= incFloat) {
                        lvUp = research.increaseProgress(increases);
                    }
                } else {
                    increases = (int) Math.round(incFloat);
                    lvUp = research.increaseProgress(increases);
                }
                if(lvUp) colPanel.reserveRefresh();
            }
        }
    }
    
    @Override
    public void fromJson(JsonObject json) {
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", getType());
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        
        return json;
    }
    
    public static String getFacilityName() {
        return "연구 모듈";
    }
    
    public static String getFacilityTitle() {
        return getFacilityName();
    }
    
    public static String getFacilityDescription() {
        return "기술 개발 시설입니다.";
    }
    
    public static Long getFacilityPrice() {
        return new Long(30000L);
    }
    
    public static Integer getFacilityBuildingCycle() {
        return new Integer(1200);
    }
    
    public static Long getTechNeeded() {
        return new Long(0);
    }
    
    /** 건설 가능여부 체크. 단, 도시 내 건설가능 구역 수와 건설인력은 이 메소드에서 체크하지 않는다. 건설 불가능 사유 발생 시 그 메시지 반환, 건설 가능 시 null 반환. */
    public static String isBuildAvail(Colony col, City city) { return null; }
}
