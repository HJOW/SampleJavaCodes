package org.duckdns.hjow.samples.colonyman.elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyMan;
import org.duckdns.hjow.samples.colonyman.elements.enemies.Enemy;
import org.duckdns.hjow.samples.colonyman.elements.facilities.FacilityManager;
import org.duckdns.hjow.samples.colonyman.elements.facilities.Home;
import org.duckdns.hjow.samples.colonyman.elements.facilities.PowerStation;

public class City implements ColonyElements {
    private static final long serialVersionUID = -8442328554683565064L;
    protected transient volatile long key = new Random().nextLong();
    
    protected String name = "도시_" + Math.abs(new Random().nextInt());
    protected List<Facility> facility = new Vector<Facility>();
    protected List<Citizen>  citizens = new Vector<Citizen>();
    protected List<Enemy>    enemies  = new Vector<Enemy>();
    protected int hp = getMaxHp();
    
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
    @Override
    public String getName() {
        return name;
    }
    public List<Facility> getFacility() {
        return facility;
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
    
    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    @Override
    public void addHp(int amount) {
        hp += amount;
        int mx = getMaxHp();
        if(hp >= mx) hp = mx;
        if(hp <   0) hp = 0;
    }

    public int getHp() {
        return hp;
    }
    public void setHp(int hp) {
        this.hp = hp;
        if(this.hp > getMaxHp()) this.hp = getMaxHp();
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
        // Apply Born and move Chance
        processBornChance(cycle, colony, efficiency100);
        processMoveInChance(cycle, colony, efficiency100);
        processMoveOutChance(cycle, colony, efficiency100);
        
        // Calculate power generation
        long power = getPowerAvail(colony);
        
        // Apply Facilities
        for(Facility f : facility) {
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
            
            // Facilities affect
            f.oneSecond(cycle, this, colony, efficiency);
        }
        
        // Remove dead objects
        removeDeads(colony);
        
        // Allocates
        allocateHome(colony);
        allocateWorkers(colony);
        
        // Process Enemies
        for(Enemy e : enemies) {
            e.oneSecond(cycle, city, colony, efficiency100);
        }
    }
    
    /** Calculate total power generating */
    protected long getPowerAvail(Colony col) {
        long power = 0L;
        
        // Calculation power
        for(Facility f : facility) {
            int working = f.getWorkingCitizensCount(this, col);
            if(working >= 1) {
                if(f instanceof PowerStation) {
                    if(working >= f.getWorkerNeeded()) power += ((PowerStation) f).getPowerGenerate();
                    else                               power += (((PowerStation) f).getPowerGenerate()) / 2;
                }
            }
        }
        
        return power;
    }
    
    /** Remove dead citizens and facilities */
    protected void removeDeads(Colony col) {
        int idx = 0;
        
        // Citizens
        while(idx < citizens.size()) {
            Citizen c = citizens.get(idx);
            if(c.getHp() <= 0) {
                citizens.remove(idx);
                continue;
            }
            idx++;
        }
        
        // Facilities
        idx = 0;
        while(idx < facility.size()) {
            Facility f = facility.get(idx);
            if(f.getHp() <= 0) {
                // Make working citizens to job seekers
                long facKey = f.getKey();
                for(Citizen c : citizens) {
                    if(c.getWorkingFacility() == facKey) c.setWorkingFacility(-1L);
                }
                
                if(f instanceof Home) {
                    // Make living citizens to homeless
                    for(Citizen c : citizens) {
                        if(c.getLivingHome() == facKey) c.setLivingHome(-1L);
                    }
                }
                
                // Remove facility
                facility.remove(idx);
                continue;
            }
            idx++;
        }
    }
    
    /** Allocate homeless to a new home */
    protected void allocateHome(Colony col) {
        for(Citizen c : citizens) {
            // Check this citizen is homeless
            if(c.getLivingHome() >= 0) continue;
            
            // Find homes
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
    
    /** Allocate job seekers to a new job */
    protected void allocateWorkers(Colony col) {
        for(Citizen c : citizens) {
            // Check this citizen is job seeker
            if(c.getWorkingFacility() >= 0) continue;
            
            // Find jobs for needed facilities - need to align
            List<Facility> list = new ArrayList<Facility>();
            for(Facility f : facility) {
                if(f.getWorkerNeeded() > f.getWorkingCitizensCount(this, col)) {
                    list.add(f);
                }
            }
            
            if(! list.isEmpty()) {
                final Citizen ct = c;
                Collections.sort(list, new Comparator<Facility>() {
                    @Override
                    public int compare(Facility o1, Facility o2) {
                        int res = o1.getWorkerSuitability(ct) - o2.getWorkerSuitability(ct);
                        if(res <  0) return -1;
                        if(res == 0) return  0;
                        return 1;
                    }
                });
                
                c.setWorkingFacility(list.get(0).getKey());
                continue;
            }
            
            // Find jobs for left facilities - need to align
            list.clear();
            for(Facility f : facility) {
                if(f.getWorkerCapacity() > f.getWorkingCitizensCount(this, col)) {
                    list.add(f);
                }
            }
            
            if(! list.isEmpty()) {
                final Citizen ct = c;
                Collections.sort(list, new Comparator<Facility>() {
                    @Override
                    public int compare(Facility o1, Facility o2) {
                        int res = o1.getWorkerSuitability(ct) - o2.getWorkerSuitability(ct);
                        if(res <  0) return -1;
                        if(res == 0) return  0;
                        return 1;
                    }
                });
                
                c.setWorkingFacility(list.get(0).getKey());
            }
        }
    }
    
    public int getCitizenCount() {
        return citizens.size();
    }
    
    public long getHomeCapacity() {
        long res = 0L;
        for(Facility f : getFacility()) {
            if(f instanceof Home) {
                res += f.getCapacity();
            }
        }
        return res;
    }
    
    public void newCitizen() {
        Citizen c = new Citizen();
        getCitizens().add(c);
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
                    newCitizen();
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
                newCitizen();
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
        
        JsonArray list = new JsonArray();
        for(Facility f : facility) { list.add(f.toJson()); }
        json.put("facilities", list);
        
        list = new JsonArray();
        for(Citizen c : citizens) { list.add(c.toJson()); }
        json.put("citizens", list);
        return json;
    }
    
    @Override
    public void fromJson(JsonObject json) {
        if(! "City".equals(json.get("type"))) throw new RuntimeException("This object is not City type.");
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        setHp(Integer.parseInt(json.get("hp").toString()));
        
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
    }
    
    public String getStatusString(ColonyMan superInstance) {
        StringBuilder desc = new StringBuilder("");
        
        DecimalFormat formatterInt  = new DecimalFormat("#,###,###,###,###,##0");
        DecimalFormat formatterRate = new DecimalFormat("##0.00");
        
        desc = desc.append("\n").append("HP : ").append(formatterInt.format(getHp())).append(" / ").append(formatterInt.format(getMaxHp()));
        desc = desc.append("\n").append("인구 : ").append(formatterInt.format(getCitizenCount()));
        desc = desc.append("\n").append("평균 행복도 : ").append(formatterRate.format(getAverageHappiness()));
        
        return desc.toString().trim();
    }
}
