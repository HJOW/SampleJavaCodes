package org.duckdns.hjow.samples.colonyman.elements;

import java.io.File;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.samples.colonyman.AccountingData;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.enemies.Enemy;
import org.duckdns.hjow.samples.colonyman.elements.facilities.PowerStation;
import org.duckdns.hjow.samples.colonyman.elements.facilities.Residence;
import org.duckdns.hjow.samples.colonyman.elements.facilities.Restaurant;
import org.duckdns.hjow.samples.colonyman.elements.research.Research;

public class Colony implements ColonyElements {
    private static final long serialVersionUID = -3144963237818493111L;
    protected volatile long key = ColonyManager.generateKey();
    
    protected List<City>       cities     = new Vector<City>();
    protected List<Enemy>      enemies    = new Vector<Enemy>();
    protected List<HoldingJob> holdings   = new Vector<HoldingJob>();
    protected List<Research>   researches = new Vector<Research>();
    protected String name = "정착지_" + ColonyManager.generateNaturalNumber();
    protected int  hp    = getMaxHp();
    protected long money = 1000000L;
    protected long tech  = 0L;
    
    protected volatile BigInteger time = new BigInteger("0");
    
    protected transient String originalFileName;
    protected transient List<AccountingData> accountingData = new Vector<AccountingData>();
    
    public Colony() {
        
    }
    
    public Colony(File f) throws Exception {
        this();
        
        String fileName = f.getName().toLowerCase();
        String strJson;
        
        if(fileName.endsWith(".colgz")) {
            strJson = FileUtil.readString(f, "UTF-8", GZIPInputStream.class);
        } else {
            strJson = FileUtil.readString(f, "UTF-8");
        }
        
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
    
    public City getCity(long key) {
        for(City c : getCities()) {
            if(c.getKey() == key) return c;
        }
        return null;
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

    public List<HoldingJob> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<HoldingJob> holdings) {
        this.holdings = holdings;
    }

    public List<Research> getResearches() {
        return researches;
    }

    public void setResearches(List<Research> researches) {
        this.researches = researches;
    }
    
    public void resetResearches() {
        researches.clear();
        // TODO
    }

    @Override
    public long getKey() {
        return key;
    }
    
    public void setKey(long key) {
        this.key = key;
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
    
    public void modifyingMoney(long money, City city, ColonyElements objType, String reason) {
        setMoney(getMoney() + money);
        
        AccountingData data = new AccountingData(getTime(), money, reason, city, objType);
        addAccountingData(data);
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

    public List<AccountingData> getAccountingData() {
        return accountingData;
    }

    public void setAccountingData(List<AccountingData> accountingData) {
        this.accountingData = accountingData;
    }
    
    public void addAccountingData(AccountingData data) {
        if(getAccountingData().size() >= 100000) getAccountingData().remove(0);
        getAccountingData().add(data);
    }

    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100) { // parameters are null
        int idx;
        
        // 도시별 사이클 처리
        for(City c : cities) {
            c.oneSecond(cycle, c, this, 100);
        }
        
        // 적 사이클 처리
        for(Enemy e : enemies) {
            e.oneSecond(cycle, city, colony, efficiency100);
        }
        
        // 예약 작업 처리
        for(HoldingJob h : getHoldings()) {
            int lefts = h.getCycleLeft();
            h.decreaseCycle();
            if(lefts >= 1) continue;
            
            executeHoldJob(h);
        }
        
        // 소모된 예약 작업 삭제
        idx = 0;
        while(idx < getHoldings().size()) {
            if(getHoldings().get(idx).getCycleLeft() <= 0) {
                getHoldings().remove(idx);
                continue;
            }
            idx++;
        }
        
        // 시간 지남
        time = time.add(BigInteger.ONE);
    }
    
    /** 예약 작업 처리 */
    @SuppressWarnings("unused")
    protected void executeHoldJob(HoldingJob j) {
        String command, params;
        command = j.getCommand();
        params  = j.getParameter();
        
        try {
            if(command.equalsIgnoreCase("NewCity")) {
                newCity();
                return;
            }
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /** 새 도시를 생성 */
    public City newCity() {
        City city = new City();
        int idx;
        
        for(idx=0; idx<20; idx++) {
            city.createNewCitizen();
        }
        
        Facility fac;
        
        for(idx=0; idx<6; idx++) {
            fac = new Residence();
            ((Residence) fac).setComportGrade(0);
            city.getFacility().add(fac);
        }
        
        fac = new PowerStation();
        city.getFacility().add(fac);
        
        fac = new Restaurant();
        city.getFacility().add(fac);
        
        getCities().add(city);
        return city;
    }
    
    @Override
    public int getMaxHp() {
        return 1000000;
    }
    
    public int getAccountingPeriod() {
        return 60;
    }
    
    public String getStatusString(ColonyManager superInstance) {
        DecimalFormat formatterInt  = new DecimalFormat("#,###,###,###,###,##0");
        // DecimalFormat formatterRate = new DecimalFormat("##0.00");
        
        StringBuilder desc = new StringBuilder("");
        desc = desc.append("\t").append("HP : ").append(formatterInt.format(getHp())).append(" / ").append(formatterInt.format(getMaxHp()));
        desc = desc.append("\t").append("예산 : ").append(formatterInt.format(getMoney()));
        desc = desc.append("\t").append("기술 : ").append(formatterInt.format(getTech()));
        desc = desc.append("\t").append("도시 수 : ").append(formatterInt.format(getCities().size()));
        
        return desc.toString().trim();
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
        json.put("time", getTime().toString());
        
        JsonArray list = new JsonArray();
        for(City c : getCities()) { list.add(c.toJson()); }
        json.put("cities", list);
        
        list = new JsonArray();
        for(HoldingJob h : getHoldings()) { list.add(h.toJson()); }
        json.put("holdings", list);
        
        list = new JsonArray();
        for(AccountingData d : getAccountingData()) { list.add(d.toJson()); }
        json.put("accountinghis", list);
        
        list = new JsonArray();
        for(Research d : getResearches()) { list.add(d.toJson()); }
        json.put("researches", list);
        
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
        
        list = (JsonArray) json.get("accountinghis");
        accountingData.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        AccountingData d = new AccountingData();
                        d.fromJson((JsonObject) o);
                        accountingData.add(d);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        
        list = (JsonArray) json.get("researches");
        resetResearches();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        long key = Long.parseLong(String.valueOf(((JsonObject) o).get("key")));
                        for(Research r : getResearches()) {
                            if(key == r.getKey()) {
                                r.fromJson((JsonObject) o);
                            }
                        }
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void save(File f) throws Exception {
        String fileName = f.getName().toLowerCase();
        if(fileName.endsWith(".colgz")) {
            FileUtil.writeString(f, "UTF-8", toJson().toJSON(), GZIPOutputStream.class); 
        } else {
            FileUtil.writeString(f, "UTF-8", toJson().toJSON()); 
        }
    }
}
