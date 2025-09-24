package org.duckdns.hjow.samples.colonyman.elements;

public interface AttackableObject extends ColonyElements {
    /** 공격이 시행되는 사이클 주기 */
    public int getAttackCycle();
    /** 한 타이밍 당 공격 횟수 */
    public int getAttackCount();
    /** 기본 공격력 */
    public int getDamage();
    /** 공격의 속성 */
    public short getAttackType();
}
