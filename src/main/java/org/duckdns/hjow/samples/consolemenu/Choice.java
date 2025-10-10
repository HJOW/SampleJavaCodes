package org.duckdns.hjow.samples.consolemenu;

import java.io.Serializable;

/** 메뉴 선택지 */
public interface Choice extends Serializable {
    /** 메뉴 목록에 표시될 텍스트 */
    public String getText();
    
    /** 메뉴 선택 시 호출될 동작 */
    public void action() throws Throwable;
}
