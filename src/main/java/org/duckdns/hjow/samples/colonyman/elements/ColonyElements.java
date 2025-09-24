package org.duckdns.hjow.samples.colonyman.elements;

import java.io.Serializable;
import java.math.BigInteger;

import org.duckdns.hjow.commons.json.JsonObject;

public interface ColonyElements extends Serializable {
    /** 이 객체의 고유 ID 값 반환. 0이 될 수 없음. */
    public long getKey();
    
    /** 이 객체의 이름 반환. */
    public String getName();
    
    /** 현재의 HP 반환 */
    public int getHp();
    
    /** HP 최대값 반환 */
    public int getMaxHp();
    
    /** HP 값 설정, 전체 회복이거나 객체를 불러오는 경우를 제외하면 호출 비권장 */
    public void setHp(int hp);
    
    /** HP 추가 (음수 사용 가능) HP 변경 이슈 발생 시 되도록이면 이 함수 사용 권장 */
    public void addHp(int amount);
    
    /** 방어 속성 */
    public short getDefenceType();
    
    /** 방어력, 이 값 만큼 대미지에서 깎인다. 단, 대미지가 1 이하로는 떨어지지 않는다. */
    public int getDefencePoint();
    
    /** 쓰레드 1 사이클 당 1회 호출됨 */
    public void oneSecond(int cycle, City city, Colony colony, int efficiency100, ColonyPanel colPanel);
    
    /** JSON 데이터로부터 객체 데이터를 불러옮 */
    public void fromJson(JsonObject json);
    
    /** 이 객체를 JSON 형태로 출력 */
    public JsonObject toJson();
    
    /** 변조방지값 계산 */
    public BigInteger getCheckerValue();
}
