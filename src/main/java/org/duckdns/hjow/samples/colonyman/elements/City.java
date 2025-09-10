package org.duckdns.hjow.samples.colonyman.elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
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
}
