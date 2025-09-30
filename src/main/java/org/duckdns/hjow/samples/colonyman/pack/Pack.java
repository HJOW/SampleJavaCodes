package org.duckdns.hjow.samples.colonyman.pack;

import java.io.Serializable;
import java.util.List;

/** 여러 클래스 정보들을 담은 객체를 Pack 이라 하고, 이 Pack 정의 클래스임을 명시하기 위한 인터페이스 */
public interface Pack extends Serializable {
    public List<Class<?>> getColonyClasses();
    public List<Class<?>> getFacilityClasses();
    public List<Class<?>> getResearchClasses();
    public List<Class<?>> getEnemyClasses();
    public List<Class<?>> getStateClasses();
    public String getName();
    public String getDesc();
    public boolean isEnabled();
    public void setEnabled(boolean enabled);
}
