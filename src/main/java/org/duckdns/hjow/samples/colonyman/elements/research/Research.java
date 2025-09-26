package org.duckdns.hjow.samples.colonyman.elements.research;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;
import org.duckdns.hjow.samples.colonyman.elements.ColonyPanel;

public abstract class Research implements ColonyElements {
    private static final long serialVersionUID = -3391024381630960804L;
    protected volatile long    key = ColonyManager.generateKey();
    protected volatile long    progress = 0;
    protected volatile int     level    = 0;

    @Override
    public long getKey() {
        return key;
    }
    
    public void setKey(long key) {
        this.key = key;
    }
    
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public int getHp() {
        return 100;
    }

    @Override
    public int getMaxHp() {
        return 100;
    }

    @Override
    public void setHp(int hp) { }

    @Override
    public void addHp(int amount) { }
    
    @Override
    public short getDefenceType() {
        return 0;
    }

    @Override
    public int getDefencePoint() {
        return 0;
    }
    
    public double getProgressPercents() {
        return getProgressPercents(true);
    }
    
    public double getProgressPercents(boolean left2FloatPoint) {
        BigDecimal r = new BigDecimal(String.valueOf(getMaxProgress()));
        if(r.compareTo(BigDecimal.ZERO) <= 0) return 0.0;
        
        BigDecimal p = new BigDecimal(String.valueOf(getProgress()));
        p = p.multiply(new BigDecimal("100"));
        r = p.divide(r, 50, BigDecimal.ROUND_HALF_UP);
        if(left2FloatPoint) r = r.setScale(2, RoundingMode.DOWN);
        return r.doubleValue();
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
        if(this.progress < 0) this.progress = 0;
        if(this.progress > getMaxProgress()) this.progress = getMaxProgress();
    }
    
    /** 진행 상태 증가 (레벨업 로직 포함 - adds는 반드시 양수로 입력해야 함) 레벨 변동 시 true 리턴 */
    public boolean increaseProgress(int adds) {
        if(adds < 0) adds = 0;
        boolean increased = false;
        
        this.progress += adds;
        if(this.progress < 0) this.progress = 0;
        while(this.progress >= getMaxProgress()) {
            increased = true;
            if(getLevel() < getMaxLevel()) {
                this.progress -= getMaxProgress();
                if(this.progress < 0) this.progress = 0;
                setLevel(getLevel() + 1);
            } else {
                this.progress = 0L;
                setLevel(getMaxLevel());
                break;
            }
        }
        if(this.progress > getMaxProgress()) this.progress = getMaxProgress();
        return increased;
    }
    
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    
    /** 도달할 수 있는 최대 레벨 */
    public int getMaxLevel() {
        return Integer.MAX_VALUE / 10;
    }
    
    /** 다음 레벨까지 도달하기에 필요한 진행상태(cycle) 필요 요구량 계산 */
    public long getMaxProgress() {
        long res = getMaxProgressStarts();
        int nowLevelLefts = getLevel() - 1;
        if(nowLevelLefts < 0) nowLevelLefts = 0;
        
        while(nowLevelLefts >= 1) {
            if(res >= Long.MAX_VALUE / 2) return res;
            res = (long) Math.round(res * getMaxProgressIncreaseRate());
            nowLevelLefts--;
        }
        return res;
    }
    
    public long   getMaxProgressStarts()       { return 600L; }
    public double getMaxProgressIncreaseRate() { return 1.5;  }
    
    @Override
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel) { }
    
    /** 연구 시작 가능여부 반환 (선행 연구 완료여부만 체크) */
    public abstract boolean isResearchAvail(Colony col);
    /** 연구 이름 반환 */
    public abstract String  getTitle();
    
    /** 이 연구의 설명 반환 */
    public String getDescription() {
        return getTitle();
    }
    
    @Override
    public void fromJson(JsonObject json) {
        key = Long.parseLong(json.get("key").toString());
        setLevel(Integer.parseInt(json.get("level").toString()));
        setProgress(Long.parseLong(json.get("progress").toString()));
    }
    
    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type"    , getClass().getSimpleName());
        json.put("key"     , new Long(getKey()));
        json.put("level"   , new Integer(getLevel()));
        json.put("progress", new Long(getProgress()));
        return json;
    }
    
    @Override
    public BigInteger getCheckerValue() {
        BigInteger res = new BigInteger(String.valueOf(getKey()));
        String type = getClass().getSimpleName();
        for(int idx=0; idx<type.length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) type.charAt(idx)))); }
        for(int idx=0; idx<getName().length(); idx++) { res = res.add(new BigInteger(String.valueOf((int) getName().charAt(idx)))); }
        res = res.add(new BigInteger(String.valueOf(getLevel())));
        res = res.add(new BigInteger(String.valueOf(getProgress())));
        
        return res;
    }
    
    @Override
    public String toString() {
        return getTitle() + " (Lv " + (getLevel() + 1) + ")"; // 콤보박스 출력용이므로...
    }
    
    /** 정수 둘 중 큰 값을 반환 (레벨 비교에 사용) */
    protected int chooseMaxInt(int a, int b) {
        if(a >= b) return a;
        return b;
    }
}
