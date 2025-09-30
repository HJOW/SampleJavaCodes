package org.duckdns.hjow.samples.colonyman;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class GlobalLogs implements Serializable {
	private static final long serialVersionUID = 3967001988050207241L;
	protected Queue<String> logs = new LinkedList<String>();
    
    public void add(String msg) {
    	logs.add(msg);
    } 
    public String poll() {
    	return logs.poll();
    }
    public boolean isEmpty() {
    	return logs.isEmpty();
    }
	public Queue<String> getLogs() {
		return logs;
	}
	public void setLogs(Queue<String> logs) {
		this.logs = logs;
	}
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
