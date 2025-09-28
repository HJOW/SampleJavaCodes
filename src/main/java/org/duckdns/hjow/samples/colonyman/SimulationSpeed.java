package org.duckdns.hjow.samples.colonyman;

import java.io.Serializable;

/** 시뮬레이션 속도 */
public class SimulationSpeed implements Serializable {
	private static final long serialVersionUID = -2925999670049087061L;
	protected int speed = 1;
	
	public SimulationSpeed() { }
	public SimulationSpeed(int speed) {
		this();
		this.speed = speed;
	}
    
	@Override
    public String toString() {
    	return "×" + speed;
    }
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
		if(this.speed < 0) this.speed = 0;
	}

	/** 실제 속도를 좌우하는 값. 작을 수록 빨라지나 너무 작으면 렉이 체감될 수 있음. 밀리초 단위 */
	public long getThreadGap() {
		if(speed <= 0) return 0;
		return 99L / speed;
	}
}
