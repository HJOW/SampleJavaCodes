package org.duckdns.hjow.samples.colonyman.elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.enemies.Enemy;
import org.duckdns.hjow.samples.colonyman.elements.facilities.FacilityManager;
import org.duckdns.hjow.samples.colonyman.elements.facilities.Home;
import org.duckdns.hjow.samples.colonyman.elements.facilities.PowerStation;
import org.duckdns.hjow.samples.colonyman.events.TimeEvent;

/** 도시 구현 클래스 */
public class City implements ColonyElements {
    private static final long serialVersionUID = -8442328554683565064L;
    protected volatile long key = ColonyManager.generateKey();
    
    protected String name = "도시_" + ColonyManager.generateNaturalNumber();
    protected List<Facility>   facility = new Vector<Facility>();
    protected List<Citizen>    citizens = new Vector<Citizen>();
    protected List<Enemy>      enemies  = new Vector<Enemy>();
    protected List<HoldingJob> holdings = new Vector<HoldingJob>();
    protected int hp = getMaxHp();
    protected int spaces = 30 + ((int) ( 30 * Math.random() ));
    
    public City() {
        
    }
    
    public City(JsonObject json) throws IOException {
        this();
        fromJson(json);
    }
    
    @Override
    public long getKey() {
        return key;
    }
    public void setKey(long key) {
        this.key = key;
    }

    @Override
    public String getName() {
        return name;
    }
    public List<Facility> getFacility() {
        return facility;
    }
    public Facility getFacility(long facKey) {
        for(Facility f : getFacility()) {
            if(f.getKey() == facKey) return f;
        }
        return null;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setFacility(List<Facility> facility) {
        this.facility = facility;
    }
    public List<Citizen> getCitizens() {
        return citizens;
    }

    public void setCitizens(List<Citizen> citizens) {
        this.citizens = citizens;
    }
    
    public Citizen getCitizen(long citizenKey) {
        for(Citizen c : getCitizens()) {
            if(c.getKey() == citizenKey) return c;
        }
        return null;
    }
    
    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    public List<HoldingJob> getHoldings() {
        return holdings;
    }
    
    public HoldingJob getHoldingJobOne(long key) {
        for(HoldingJob j : getHoldings()) {
            if(j.getKey() == key) return j;
        }
        return null;
    }

    public void setHoldings(List<HoldingJob> holdings) {
        this.holdings = holdings;
    }

    @Override
    public void addHp(int amount) {
        hp += amount;
        int mx = getMaxHp();
        if(hp >= mx) hp = mx;
        if(hp <   0) hp = 0;
    }

    @Override
    public int getHp() {
        return hp;
    }
    
    @Override
    public void setHp(int hp) {
        this.hp = hp;
        int mx = getMaxHp();
        if(hp >= mx) hp = mx;
        if(hp <   0) hp = 0;
    }
    
    @Override
    public short getDefenceType() {
        return ColonyManager.DEFENCETYPE_BUILDING;
    }

    @Override
    public int getDefencePoint() {
        return 9;
    }
    
    public int getSpaces() {
        return spaces;
    }

    public void setSpaces(int spaces) {
        this.spaces = spaces;
    }

    @Override
    public int getMaxHp() {
        int max = 100000;
        for(Facility f : facility) {
            max += f.increasingCityMaxHP();
        }
        return max;
    }
    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100) { // city should be a self
        int idx;
        
        // 출산율 및 이주 계산
        processBornChance(cycle, colony, efficiency100);
        processMoveInChance(cycle, colony, efficiency100);
        processMoveOutChance(cycle, colony, efficiency100);
        
        // 전력 생산량 계산
        long power = getPowerGenerate(colony);
        
        // 시설 파워 및 효율성 계산, 효과 처리
        for(Facility f : getFacility()) {
            int efficiency = efficiency100;
            
            // Calculates power supply
            int efficiencyPow = efficiency100;
            int consume = f.getPowerConsume();
            
            if(consume == 0 || power >= consume) {
                efficiencyPow = efficiency100;
                power -= consume;
            } else if(consume >= 1 && power >= 1 && power < consume) {
                efficiencyPow = (int) (power * efficiency100) / consume;
                power = 0L;
            } else {
                efficiencyPow = 0;
                power = 0L;
            }
            
            // Calculates worker
            int efficiencyWorker = efficiency100;
            if(f.getWorkerNeeded() >= 1) {
                int working = f.getWorkingCitizensCount(city, colony);
                if(f.getWorkerNeeded() > working) {
                    efficiencyWorker = efficiencyWorker / 2;
                }
            }
            
            efficiency = (int) Math.round(   ((efficiencyPow / 100.0) * (efficiencyWorker / 100.0)) * 100  );
            
            if(cycle % (colony.getAccountingPeriod()) == 0) {
                // 시설의 비용 처리
                processFacilityFees(f, colony);
            }
            
            // 시설 효과 처리
            f.oneSecond(cycle, this, colony, efficiency);
        }
        
        // 적 사이클 처리
        for(Enemy e : getEnemies()) {
            e.oneSecond(cycle, city, colony, efficiency100);
        }
        
        // 사망 개체 제거
        removeDeads(colony);
        
        // 거주자 및 일자리 할당
        allocateHome(colony);
        allocateWorkers(colony);
        
        // 예약 작업 처리
        for(HoldingJob h : getHoldings()) {
            int lefts = h.getCycleLeft();
            
            if("NewFacility".equalsIgnoreCase(h.getCommand())) {
                if(h.getWorkingCitizens(this).isEmpty()) continue; // 시설 건설의 경우, 건설에 시민이 필요함
            }
            
            h.decreaseCycle();
            lefts = h.getCycleLeft();
            if(lefts >= 1) continue; // 아직 사이클이 남아있으면 execute 하지 않고 건너뜀
            
            executeHoldJob(h);
        }
        
        // 완료된 예약 작업 삭제
        idx = 0;
        while(idx < getHoldings().size()) {
            HoldingJob j = getHoldings().get(idx);
            if(j.getCycleLeft() <= 0) {
                // 건설하고 있는 시민들 노숙자로 변경
                for(Citizen c : j.getWorkingCitizens(this)) {
                    if(c.getBuildingFacility() == j.getKey()) c.setBuildingFacility(0L);
                }
                
                // 삭제
                getHoldings().remove(idx);
                continue;
            }
            idx++;
        }
        
        // 이벤트 처리
        for(TimeEvent ev : colony.getEvents()) {
            if(colony.getTime().compareTo(new BigInteger("" + ev.getOccurMinimumTime(colony))) < 0) continue;
            
            if(ev.getEventSize() == TimeEvent.EVENTSIZE_CITY) {
                if(cycle % ev.getOccurCycle(colony, this) == 0) {
                    if(Math.random() <= ev.getOccurRate(this, colony, this)) ev.onEventOccured(this, colony, this);
                }
            } else if(ev.getEventSize() == TimeEvent.EVENTSIZE_FACILITY) {
                for(Facility fac : getFacility()) {
                    if(cycle % ev.getOccurCycle(colony, this) == 0) {
                        if(Math.random() <= ev.getOccurRate(fac, colony, this)) ev.onEventOccured(fac, colony, this);
                    }
                }
            }
        }
    }
    
    /** 시설의 비용 처리 */
    protected void processFacilityFees(Facility f, Colony colony) {
        // 임금 처리
        for(Citizen c : f.getWorkingCitizens(this, colony)) {
            long sal = f.getSalary(this, colony);
            colony.modifyingMoney( sal * (-1), this, f, "salary" );
            c.setMoney(c.getMoney() + sal);
        }
        // 유지비 처리
        colony.modifyingMoney( f.getMaintainFee(this, colony) * (-1), this, f, "maintain" );
    }
    
    /** 예약 작업 처리 */
    protected void executeHoldJob(HoldingJob j) {
        String command, params;
        command = j.getCommand();
        params  = j.getParameter();
        
        try {
            if(command.equalsIgnoreCase("NewCitizen")) {
                createNewCitizen();
                return;
            }
            
            if(command.equalsIgnoreCase("NewFacility")) {
                if(params == null) return;
                if(params.equals("")) return;
                
                Class<?> facilityClass = FacilityManager.getFacilityClass(params);
                if(facilityClass == null) return;
                Object newOne = facilityClass.newInstance();
                getFacility().add((Facility) newOne);
            }
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        
    }
    /** 건설 중인 시설 수 반환 */
    public int getHoldingBuildFacility() {
        int res = 0;
        for(HoldingJob j : getHoldings()) {
            if("NewFacility".equalsIgnoreCase(j.getCommand())) res++; 
        }
        return res;
    }
    
    /** 총 전력 생산량 반환 (사용량 미반영) */
    public long getPowerGenerate(Colony col) {
        long power = 0L;
        
        for(Facility f : facility) {
            int working = f.getWorkingCitizensCount(this, col);
            if(working >= 1) {
                if(f instanceof PowerStation) {
                    if(working >= f.getWorkerNeeded()) power += ((PowerStation) f).getPowerGenerate(col, this);
                    else                               power += (((PowerStation) f).getPowerGenerate(col, this)) / 2;
                }
            }
        }
        
        return power;
    }
    
    /** HP가 0인 시민과 시설, 적 제거 */
    public void removeDeads(Colony col) {
        int idx = 0;
        
        // 시민
        while(idx < getCitizens().size()) {
            Citizen c = getCitizens().get(idx);
            if(c.getHp() <= 0) {
                getCitizens().remove(idx);
                continue;
            }
            idx++;
        }
        
        // 시설
        idx = 0;
        while(idx < getFacility().size()) {
            Facility f = getFacility().get(idx);
            if(f.getHp() <= 0) {
                // 일하는 중인 시민 구직자 만들기
                long facKey = f.getKey();
                for(Citizen c : getCitizens()) {
                    if(c.getWorkingFacility() == facKey) c.setWorkingFacility(0L);
                }
                
                if(f instanceof Home) {
                    // 살던 시민 노숙자 만들기
                    for(Citizen c : getCitizens()) {
                        if(c.getLivingHome() == facKey) c.setLivingHome(0L);
                    }
                }
                
                // 시설 제거
                getFacility().remove(idx);
                continue;
            }
            idx++;
        }
        
        // 적
        idx = 0;
        while(idx < getEnemies().size()) {
            Enemy en = getEnemies().get(idx);
            if(en.getHp() <= 0) {
                getEnemies().remove(idx);
                continue;
            }
            idx++;
        }
    }
    
    /** 노숙자 수 계산 */
    public int getHomelesses() {
        int counts = 0;
        for(Citizen c : citizens) {
            if(c.isHomeless()) counts++;
        }
        return counts;
    }
    
    /** 백수의 수 계산 */
    public int getJobSeekers() {
        int counts = 0;
        for(Citizen c : citizens) {
            if(c.isJobSeeker()) counts++;
        }
        return counts;
    }
    
    /** 노숙자를 주거 모듈에 할당 */
    protected void allocateHome(Colony col) {
        // 시민들 확인해서, 존재하지 않는 주거 모듈에 산다고 되어 있으면 리셋
        for(Citizen c : citizens) {
            // 거주민 여부 확인
            if(c.isHomeless()) continue;
            
            if(c.getLivingHome() != 0L) {
                Home h = c.getLivingHome(this);
                if(h == null) c.setLivingHome(0L);
            }
        }
        
        // 시민들 별로 주거 할당
        for(Citizen c : citizens) {
            // 노숙자 여부 판단
            if(! c.isHomeless()) continue;
            
            // 비어있는 주거 모듈 찾기
            for(Facility f : facility) {
                if(f instanceof Home) {
                    Home home = (Home) f;
                    if(! home.isFull(this, col)) {
                        c.setLivingHome(home.getKey());
                        break;
                    }
                }
            }
        }
    }
    
    /** 백수에게 새 직장 할당 */
    protected void allocateWorkers(Colony col) {
        List<Facility> list = new ArrayList<Facility>();
        // 시민들 확인해서, 존재하지 않는 직장에 있는지 확인
        for(final Citizen c : citizens) {
            // 직장인 여부 판단
            if(c.isJobSeeker()) continue;
            
            // 존재하지 않는 직장인 경우 (직장 시설이 없어졌거나 등등) 리셋
            if(c.getWorkingFacility() != 0L) {
                Facility f = c.getWorkingFacility(this);
                if(f == null) c.setWorkingFacility(0L);
            }
            
            // 존재하지 않는 건설 현장인 경우 (완공되었거나 등등) 리셋
            if(c.getBuildingFacility() != 0L) {
                HoldingJob j = c.getBuildingFacility(this);
                if(j == null) c.setBuildingFacility(0L);
            }
        }
        
        // 시민별로 일자리 할당
        for(final Citizen c : citizens) {
            // 백수 여부 판단
            if(! c.isJobSeeker()) continue;
            
            // 건설 일자리들 확인
            list.clear();
            HoldingJob building = null;
            for(HoldingJob j : getHoldings()) {
                if(! "NewFacility".equalsIgnoreCase(j.getCommand())) continue;
                int working = j.getWorkingCitizens(this).size();
                if(working <= 0) {
                    building = j;
                    break;
                }
            }
            if(building != null) {
                c.setBuildingFacility(building.getKey());
                continue;
            }
            
            // 일자리가 당장 필요한 직장들 찾기 - 정렬 고민해야...
            list.clear();
            for(Facility f : facility) {
                if(f.getWorkerNeeded() > f.getWorkingCitizensCount(this, col)) {
                    list.add(f);
                }
            }
            
            if(! list.isEmpty()) {
                // 이 시민이 일하기에 적합한 정도 순으로 정렬
                Collections.sort(list, new Comparator<Facility>() {
                    @Override
                    public int compare(Facility o1, Facility o2) {
                        int res = o1.getWorkerSuitability(c) - o2.getWorkerSuitability(c);
                        if(res <  0) return -1;
                        if(res == 0) return  0;
                        return 1;
                    }
                });
                
                c.setWorkingFacility(list.get(0).getKey());
                continue;
            }
            
            // 일자리를 더 제공할 수 있는 직장들 찾기 - 정렬 고민해야...
            list.clear();
            for(Facility f : facility) {
                if(f.getWorkerCapacity() > f.getWorkingCitizensCount(this, col)) {
                    list.add(f);
                }
            }
            
            if(! list.isEmpty()) {
                // 이 시민이 일하기에 적합한 정도 순으로 정렬
                Collections.sort(list, new Comparator<Facility>() {
                    @Override
                    public int compare(Facility o1, Facility o2) {
                        int res = o1.getWorkerSuitability(c) - o2.getWorkerSuitability(c);
                        if(res <  0) return -1;
                        if(res == 0) return  0;
                        return 1;
                    }
                });
                
                c.setWorkingFacility(list.get(0).getKey());
            }
        }
    }
    
    /** 새 시민 생성 */
    public Citizen createNewCitizen() {
        Citizen c = new Citizen();
        getCitizens().add(c);
        return c;
    }
    
    /**  시민 수 반환 */
    public int getCitizenCount() {
        return citizens.size();
    }
    
    /** 이 도시 내 거주 시설 수용 인원 반환 (이미 거주 중인 자리도 포함) */
    public long getHomeCapacity() {
        long res = 0L;
        for(Facility f : getFacility()) {
            if(f instanceof Home) {
                res += f.getCapacity();
            }
        }
        return res;
    }
    
    /** 이 도시 내 잔여 거주 시설 수용 인원 반환 */
    public long getHomeCapacityLeft() {
        long res = 0L;
        for(Facility f : getFacility()) {
            if(f instanceof Home) {
                res += f.getCapacity();
                for(Citizen c : getCitizens()) { if(c.getLivingHome() == f.getKey()) res--; }
            }
        }
        return res;
    }
    
    /** 이 도시 내 직장 자리 수 반환 (이미 일하고 있는 자리 수도 포함) */
    public long getJobsCount() {
        long res = 0L;
        for(Facility f : getFacility()) {
            res += f.getWorkerCapacity();
        }
        return res;
    }
    
    /** 이 도시 내 잔여 직장 자리 수 반환 */
    public long getLeftJobsCount() {
        long res = 0L;
        for(Facility f : getFacility()) {
            res += f.getWorkerCapacity();
            for(Citizen c : getCitizens()) { if(c.getWorkingFacility() == f.getKey()) res--; }
        }
        return res;
    }
    
    
    /** 출산률 계산 */
    public double getBornChanceRate(Colony col, int efficiency100) {
        double res = efficiency100 / 100.0;
        if(res > 50.0) res = 50.0;
        return res;
    }
    
    /** 출산률 적용 */
    public void processBornChance(int cycle, Colony col, int efficiency100) {
        if(cycle % 600 == 0) {
            if(getHomeCapacity() > getCitizenCount()) {
                if(Math.random() < ( getBornChanceRate(col, efficiency100))) {
                    createNewCitizen();
                }
            }
        }
    }
    
    /** 이주율 계산 (이주해 들어올 확률만 계산) */
    public double getMoveChangeRate(Colony col, int efficiency100) {
        // 거주지가 부족하면 이주 0
        if(getHomeCapacity() <= getCitizenCount()) return 0.0;
        
        // 행복도 체크
        boolean happinessAccepts = false;
        double rate = getAverageHappiness();
        if(getCitizenCount() <= 0) {
            happinessAccepts = true;
            rate = 10.0; // 시민이 아예 없으면 10 적용
        } else {
            if(rate >= 50.0) happinessAccepts = true;
            rate = rate - 50.0;
        }
        
        if(! happinessAccepts) return 0.0;
        if(rate <= 0.0) return 0.0;
        
        // 효율 적용
        rate = rate * (efficiency100 / 100.0);
        
        // 백분율 to 0~1
        return rate / 100.0;
    }
    
    /** 평균행복도 계산 (참고 - 시민의 행복도는 최소 0, 최초값은 50) */
    public double getAverageHappiness() {
        BigDecimal happiness = new BigDecimal("0");
        if(getCitizenCount() >= 1) {
            for(Citizen c : getCitizens()) {
                happiness = happiness.add(new BigDecimal(String.valueOf(c.getHappy())));
            }
            happiness = happiness.divide(new BigDecimal(String.valueOf(getCitizenCount())), 5, BigDecimal.ROUND_DOWN);
            return happiness.doubleValue();
        }
        return 0.0;
    }
    
    /** 이주율 (입주) 적용 */
    public void processMoveInChance(int cycle, Colony col, int efficiency100) {
        double moveRate = getMoveChangeRate(col, efficiency100);
        if(cycle % 600 == 0) {
            if(Math.random() < moveRate) {
                createNewCitizen();
            }
        }
    }
    
    /** 이주율 (탈출) 적용 */
    public void processMoveOutChance(int cycle, Colony col, int efficiency100) {
        if(cycle % 60 == 0) {
            if(getCitizenCount() >= 1) {
                int idx = 0;
                List<Citizen> citizens = getCitizens();
                while(idx < citizens.size()) {
                    int happy = citizens.get(idx).getHappy();
                    if(happy < 10) {
                        double rates = (happy / 100.0);
                        if(Math.random() < rates) {
                            citizens.remove(idx);
                            continue;
                        }
                    }
                    
                    idx++;
                }
            }
        }
    }
    
    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", "City");
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        json.put("spaces", new Integer(getSpaces()));
        
        JsonArray list = new JsonArray();
        for(Facility f : getFacility()) { list.add(f.toJson()); }
        json.put("facilities", list);
        
        list = new JsonArray();
        for(Citizen c : getCitizens()) { list.add(c.toJson()); }
        json.put("citizens", list);
        
        list = new JsonArray();
        for(HoldingJob h : holdings) { list.add(h.toJson()); }
        json.put("holdings", list);
        
        list = new JsonArray();
        for(Enemy h : enemies) { list.add(h.toJson()); }
        json.put("enemies", list);
        
        return json;
    }
    
    @Override
    public void fromJson(JsonObject json) {
        if(! "City".equals(json.get("type"))) throw new RuntimeException("This object is not City type.");
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        setSpaces(Integer.parseInt(json.get("spaces").toString()));
        
        JsonArray list = (JsonArray) json.get("facilities");
        facility.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Facility fac = FacilityManager.fromJson((JsonObject) o);
                        if(fac == null) throw new NullPointerException("Cannot found these facility type " + o);
                        facility.add(fac);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        
        list = (JsonArray) json.get("citizens");
        citizens.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Citizen cit = new Citizen((JsonObject) o);
                        citizens.add(cit);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        
        list = (JsonArray) json.get("holdings");
        holdings.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        HoldingJob h = new HoldingJob();
                        h.fromJson((JsonObject) o);
                        holdings.add(h);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        
        list = (JsonArray) json.get("enemies");
        enemies.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Enemy en = Enemy.createEnemyFromJson((JsonObject) o);
                        enemies.add(en);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    
    /** 상태 메시지 생성 (UI 내 JTextArea 에 출력됨) */
    public String getStatusString(Colony col, ColonyManager superInstance) {
        StringBuilder desc = new StringBuilder("");
        
        DecimalFormat formatterInt  = new DecimalFormat("#,###,###,###,###,##0");
        DecimalFormat formatterRate = new DecimalFormat("##0.00");
        
        long powerConsume = 0L;
        for(Facility f : getFacility()) {
            powerConsume += f.getPowerConsume();
        }
        
        desc = desc.append("\n").append("HP : ").append(formatterInt.format(getHp())).append(" / ").append(formatterInt.format(getMaxHp()));
        desc = desc.append("\n").append("인구 : ").append(formatterInt.format(getCitizenCount()));
        desc = desc.append("\n").append("전력 : ").append(formatterInt.format(powerConsume)).append(" / ").append(formatterInt.format(getPowerGenerate(getColony(superInstance))));
        desc = desc.append("\n").append("시설 수 : ").append(formatterInt.format(facility.size())).append(" / ").append(formatterInt.format(getSpaces()));
        desc = desc.append("\n").append("평균 행복도 : ").append(formatterRate.format(getAverageHappiness()));
        desc = desc.append("\n").append("노숙자 : ").append(formatterInt.format(getHomelesses()));
        desc = desc.append("\n").append("거주 한도 : ").append(formatterInt.format(getHomeCapacity()));
        desc = desc.append("\n").append("백수 : ").append(formatterInt.format(getJobSeekers()));
        desc = desc.append("\n").append("직장 한도 : ").append(formatterInt.format(getJobsCount()));
        
        return desc.toString().trim();
    }
    
    /** 소속 정착지 찾기 */
    public Colony getColony(ColonyManager man) {
        return man.getColonyFrom(this);
    }
    
    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = new BigInteger(String.valueOf(getKey()));
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
        res = res.add(new BigInteger(String.valueOf(getHp())));
        for(Facility   f : getFacility()) { res = res.add(f.getCheckerValue()); }
        for(Citizen    c : getCitizens()) { res = res.add(c.getCheckerValue()); }
        for(Enemy      e : getEnemies())  { res = res.add(e.getCheckerValue()); }
        for(HoldingJob h : getHoldings()) { res = res.add(h.getCheckerValue()); }
        
        return res;
    }
}
