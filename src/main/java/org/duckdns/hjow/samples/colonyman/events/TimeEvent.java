package org.duckdns.hjow.samples.colonyman.events;

import java.io.Serializable;

import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;
import org.duckdns.hjow.samples.colonyman.ui.ColonyPanel;

/** 랜덤 발생 이벤트 공통 부분을 정의하는 상위 클래스 */
public abstract class TimeEvent implements Serializable {
    private static final long serialVersionUID = 3397834730618049049L;
    
    /** 이벤트 규모 */
    public abstract short  getEventSize();
    
    /** 이벤트 발생 타이밍 사이클 (이 때 이벤트 발생률에 따라 발생) */
    public abstract int    getOccurCycle(Colony col, City city);
    
    /** 이벤트가 발생할 최소 타이밍 (이 이상 시간이 지나야 발생) */
    public abstract long   getOccurMinimumTime(Colony col);
    
    /** 이벤트 발생률 */
    public abstract double getOccurRate(ColonyElements target, Colony col, City city);
    
    /** 이벤트 처리 */
    public void onEventOccured(ColonyElements target, Colony col, City city, ColonyPanel colPanel) {
        ColonyManager.logGlobals(getTitle() + " 발생 !");
    }
    
    /** 이벤트 명칭 반환 */
    public abstract String getTitle();
    
    public static final short EVENTSIZE_COLONY   = 9;
    public static final short EVENTSIZE_CITY     = 8;
    public static final short EVENTSIZE_FACILITY = 7;
}
