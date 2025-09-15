package org.duckdns.hjow.samples.colonyman;

import java.io.Serializable;
import java.math.BigInteger;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;

/** 돈의 수입, 지출 이력 하나를 담는 VO */
public class AccountingData implements Serializable {
    private static final long serialVersionUID = 6059734786112483575L;
    protected BigInteger time = BigInteger.ZERO;
    protected long amount = 0L;
    protected String reason = "";
    protected long cityKey;
    protected long sourceKey;
    
    protected transient boolean disposed = false;
    
    public AccountingData() {}
    public AccountingData(BigInteger time, long amount, String reason, City city, ColonyElements sources) {
        super();
        this.time = time;
        this.amount = amount;
        this.reason = reason;
        this.cityKey = city.getKey();
        this.sourceKey = sources.getKey();
    }
    
    public BigInteger getTime() {
        return time;
    }
    public void setTime(BigInteger time) {
        this.time = time;
    }

    public long getCityKey() {
        return cityKey;
    }
    public void setCityKey(long cityKey) {
        this.cityKey = cityKey;
    }
    public long getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public long getSourceKey() {
        return sourceKey;
    }
    public void setSourceKey(long sourceKey) {
        this.sourceKey = sourceKey;
    }
    public boolean isDisposed() {
        return disposed;
    }
    public void dispose() {
        this.disposed = true;
    }
    
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", "AccountingHistory");
        json.put("time", getTime().toString());
        json.put("reason", getReason());
        json.put("city", new Long(getCityKey()));
        json.put("source", new Long(getSourceKey()));
        
        return json;
    }
    
    public void fromJson(JsonObject json) {
        if(! "AccountingHistory".equals(json.get("type"))) throw new RuntimeException("This object is not AccountingHistory type.");
        
        setTime(new BigInteger(json.get("time").toString()));
        setReason(json.get("reason").toString());
        setCityKey(Long.parseLong(json.get("city").toString()));
        setSourceKey(Long.parseLong(json.get("source").toString()));
    }
}
