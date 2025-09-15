package org.duckdns.hjow.samples.colonyman;

import java.io.Serializable;

import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;

/** 돈의 수입, 지출 이력 하나를 담는 VO */
public class AccountingData implements Serializable {
    private static final long serialVersionUID = 6059734786112483575L;
    protected long amount = 0L;
    protected String reason = "";
    protected City city;
    protected ColonyElements sources;
    
    public AccountingData() {}
    public AccountingData(long amount, String reason, City city, ColonyElements sources) {
        super();
        this.amount = amount;
        this.reason = reason;
        this.city = city;
        this.sources = sources;
    }

    public long getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public City getCity() {
        return city;
    }

    public ColonyElements getSources() {
        return sources;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void setSources(ColonyElements sources) {
        this.sources = sources;
    }
}
