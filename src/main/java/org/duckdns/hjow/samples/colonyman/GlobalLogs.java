package org.duckdns.hjow.samples.colonyman;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/** 전역 로그 관리용 클래스, 별도의 UI 클래스와 함께 동작해야 함. Queue 안에 로그를 쌓아두고, UI 클래스에서 이를 꺼내 출력 */
public class GlobalLogs implements Serializable {
	private static final long serialVersionUID = 3967001988050207241L;
	protected Queue<String> logs = new LinkedList<String>();
    
    /** 로그 추가 */
    public void add(String msg) {
    	logs.add(msg);
    } 
    /** 로그 꺼내기 */
    public String poll() {
    	return logs.poll();
    }
    /** 출력할 로그가 없는지 확인 */
    public boolean isEmpty() {
    	return logs.isEmpty();
    }
    /** 로그들이 포함된 Queue 객체 자체를 반환 */
	public Queue<String> getLogs() {
		return logs;
	}
    /** Queue 객체 자체를 교체 */
	public void setLogs(Queue<String> logs) {
		this.logs = logs;
	}
    /** 로그 모두 삭제 */
	public void clear() {
		this.logs.clear();
	}
	
	protected static GlobalLogs instances = new GlobalLogs();
	public static GlobalLogs getInstance() { return instances; }
	
	/** 로그 출력 */
	public static void log(String msg) { System.out.println(msg); getInstance().add(msg); }
	
	/** 오류 처리 (Colony 이내 쪽에서 발생한 예외는 각 패널에서 처리할 것 !) */
    public static void processExceptionOccured(Throwable tx, boolean isSerious) {
        if(tx instanceof RuntimeException) {
            Throwable caused = tx.getCause();
            if(caused != null) tx = caused;
        }
        
        tx.printStackTrace();
        
        String msg = "오류 - " + tx.getMessage();
        ByteArrayOutputStream byteCollector = new ByteArrayOutputStream();
        if(isSerious) {
            PrintStream ps = new PrintStream(byteCollector);
            tx.printStackTrace(ps);
            ps.close();

            msg = msg + "\n" + new String(byteCollector.toByteArray());
        }
        log(msg);
    }
}
