package org.duckdns.hjow.samples.console;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStreamReader;

import org.duckdns.hjow.commons.core.Disposeable;

/** 콘솔 스트림. 콘솔 모드로 실행 시 이 객체는 최초 초기화 후, 프로그램 종료될 때까지 유지되어야 함. */
public class ConsoleStream implements Closeable, Disposeable {
    protected BufferedReader inp;
    
    public ConsoleStream() {
        inp = new BufferedReader(new InputStreamReader(System.in));
    }
    
    public ConsoleStream(String charset) {
        try {
        inp = new BufferedReader(new InputStreamReader(System.in, charset));
        } catch(Exception e) { throw new RuntimeException(e.getMessage(), e); }
    }
    
    public void close() {
        dispose();
    }

    @Override
    public void dispose() {
        if(inp != null) {
            try { inp.close(); } catch(Exception ex) { ex.printStackTrace(); }; inp = null;
        }
    }
    
    public String ask() {
        print(">> ");
        try { return inp.readLine(); } catch(Exception ex) { throw new RuntimeException(ex.getMessage(), ex); }
    }
    
    public void print(Object obj) {
        System.out.print(obj);
    }
    
    public void println(Object obj) {
        System.out.println(obj);
    }
}
