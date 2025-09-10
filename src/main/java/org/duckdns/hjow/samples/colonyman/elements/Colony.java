package org.duckdns.hjow.samples.colonyman.elements;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.samples.colonyman.elements.enemies.Enemy;

public class Colony implements ColonyElements {
    private static final long serialVersionUID = -3144963237818493111L;
    protected transient volatile long key = new Random().nextLong();
    
    protected List<City>  cities  = new Vector<City>();
    protected List<Enemy> enemies = new Vector<Enemy>();
    protected String name = "정착지_" + Math.abs(new Random().nextInt());
    protected int  hp    = getMaxHp();
    protected long money = 1000000L;
    protected long tech  = 0L;
    
    protected volatile BigInteger time = new BigInteger("0");
    
    protected transient String originalFileName;
    
    public Colony() {
        
    }
    
    public Colony(File f) throws Exception {
        this();
        String strJson = FileUtil.readString(f, "UTF-8");
        JsonObject objJson = (JsonObject) JsonObject.parseJson(strJson);
        fromJson(objJson);
    }

    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    @Override
    public long getKey() {
        return key;
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
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public long getTech() {
        return tech;
    }

    public void setTech(long tech) {
        this.tech = tech;
    }

    public BigInteger getTime() {
        return time;
    }
    
    public String getDateString() {
        BigInteger originals = new BigInteger(getTime().toByteArray());
        BigInteger minutes, seconds, hour, date, month, year;
        minutes = new BigInteger(BigInteger.ZERO.toByteArray());
        seconds = new BigInteger(originals.toByteArray());
        hour    = new BigInteger(BigInteger.ZERO.toByteArray());
        date    = new BigInteger(BigInteger.ONE.toByteArray());
        month   = new BigInteger(BigInteger.ONE.toByteArray());
        year    = new BigInteger("3000");
        
        BigInteger std60, std30, std24, std12;
        std60 = new BigInteger("60");
        std30 = new BigInteger("30");
        std24 = new BigInteger("24");
        std12 = new BigInteger("12");
        
        // DIVIDE - MOD Calculation
        // Seconds
        if(seconds.compareTo(std60) >= 0) {
            minutes = minutes.add(new BigInteger(seconds.toByteArray()).divide(std60));
            seconds = seconds.mod(std60);
        }
        
        // Minutes (Once again)
        if(minutes.compareTo(std60) >= 0) {
            hour = hour.add(new BigInteger(minutes.toByteArray()).divide(std60));
            minutes = minutes.mod(std60);
        }
        
        // Hour
        if(hour.compareTo(std24) >= 0) {
            date = date.add(new BigInteger(hour.toByteArray()).divide(std24));
            hour = hour.mod(std24);
        }
        
        // DIVIDE - Loop Calculation
        // Seconds
        while(seconds.compareTo(std60) >= 0) {
            seconds = seconds.subtract(std60);
            minutes = minutes.add(BigInteger.ONE);
        }
        
        // Minutes (Once again)
        while(minutes.compareTo(std60) >= 0) {
            minutes = minutes.subtract(std60);
            hour = hour.add(BigInteger.ONE);
        }
        
        // Hour
        while(hour.compareTo(std24) >= 0) {
            hour = hour.subtract(std24);
            date = date.add(BigInteger.ONE);
        }
        
        // Date
        while(date.compareTo(std30) > 0) {
            date = date.subtract(std30);
            month = month.add(BigInteger.ONE);
        }
        
        // Month
        while(month.compareTo(std12) > 0) {
            month = month.subtract(std12);
            year = year.add(BigInteger.ONE);
        }
        
        // Create String
        StringBuilder res = new StringBuilder("");
        res = res.append(year.toString()).append("-").append(String.format("%02d", month.intValue())).append("-").append(String.format("%02d", date.intValue()));
        res = res.append(" ");
        res = res.append(String.format("%02d", hour.intValue())).append(":").append(String.format("%02d", minutes.intValue())).append(":").append(String.format("%02d", seconds.intValue()));
        
        return res.toString().trim();
    }

    public void setTime(BigInteger time) {
        this.time = time;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100) { // parameters are null
        for(City c : cities) {
            c.oneSecond(cycle, c, this, 100);
        }
        for(Enemy e : enemies) {
            e.oneSecond(cycle, city, colony, efficiency100);
        }
        time = time.add(BigInteger.ONE);
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", "Colony");
        json.put("name", getName());
        json.put("key", new Long(getKey()));
        json.put("hp", new Long(getHp()));
        json.put("money", new Long(getMoney()));
        json.put("tech", new Long(getTech()));
        json.put("time", time.toString());
        
        JsonArray list = new JsonArray();
        for(City c : cities) { list.add(c.toJson()); }
        json.put("cities", list);
        return json;
    }
    
    public void fromJson(JsonObject json) {
        if(! "Colony".equals(json.get("type"))) throw new RuntimeException("This object is not Colony type.");
        setName(json.get("name").toString());
        key = Long.parseLong(json.get("key").toString());
        try { setHp(Integer.parseInt(json.get("hp").toString()));     } catch(Exception ex) { ex.printStackTrace(); hp    = 10; }
        try { setMoney(Long.parseLong(json.get("money").toString())); } catch(Exception ex) { ex.printStackTrace(); money = 0; }
        try { setTech(Long.parseLong(json.get("tech").toString())); } catch(Exception ex) { ex.printStackTrace(); tech = 0; }
        try { setTime(new BigInteger(json.get("time").toString()));   } catch(Exception ex) { ex.printStackTrace(); time  = BigInteger.ZERO; }
        
        JsonArray list = (JsonArray) json.get("cities");
        cities.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        City city = new City((JsonObject) o);
                        cities.add(city);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public int getMaxHp() {
        return 1000000;
    }
}
