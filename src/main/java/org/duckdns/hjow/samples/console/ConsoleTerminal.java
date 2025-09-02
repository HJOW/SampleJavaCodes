package org.duckdns.hjow.samples.console;

import java.util.List;

import org.duckdns.hjow.samples.base.SampleJavaCodes;
import org.duckdns.hjow.samples.interfaces.Disposeable;

/** 콘솔 모드로 메뉴를 띄우고 입력을 받는 도구 */
public class ConsoleTerminal implements Disposeable {
    protected SampleJavaCodes superInst;
    protected ConsoleStream   stream;
    
    public ConsoleTerminal(SampleJavaCodes superInst, ConsoleStream stream) {
        this.superInst = superInst;
        this.stream = stream;
    }
    
    public int askMenu(String title, List<String> menu) {
        stream.println(title);
        for(int idx=0; idx<menu.size(); idx++) {
            stream.println((idx + 1) + ". " + menu.get(idx));
        }
        
        String resp = stream.ask();
        if(resp == null) return -1;
        
        resp = resp.trim();
        return Integer.parseInt(resp) - 1;
    }

    /** 순환 참조만 제거, 스트림을 닫지 않음 */
    @Override
    public void dispose() {
        this.superInst = null;
        this.stream = null;
    }
}
