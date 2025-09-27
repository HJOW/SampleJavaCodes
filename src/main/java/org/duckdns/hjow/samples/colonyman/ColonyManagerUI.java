package org.duckdns.hjow.samples.colonyman;

import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.Colony;

public interface ColonyManagerUI {
	/** 해당 키를 갖는 정착지 찾아 반환 (목록에 없으면 null 반환) */
	public Colony getColony(long colonyKey);
	
	/** 도시가 속한 정착지 찾기 */
	public Colony getColonyFrom(City city);
}
