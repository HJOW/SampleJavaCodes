package org.duckdns.hjow.subprogram;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import org.duckdns.hjow.commons.util.ClassUtil;

public class DBConsole extends org.duckdns.hjow.commons.console.DBConsole {
    public DBConsole() {}
    
    @Override
    public void run(Map<String, String> argMap) {
        BufferedReader reader = null;
        String charset = null;
        
        if(argMap.containsKey("charset")) charset = argMap.get("charset");
        
        try {
            // 표준 입력 준비
            if(charset == null) reader = new BufferedReader(new InputStreamReader(System.in));
            else                reader = new BufferedReader(new InputStreamReader(System.in, charset));
            
            String line;
            int sel;
            boolean readOnly = false;
            
            while(true) {
                try {
                    System.out.println();
                    
                    // 타이틀 출력
                    System.out.println("DB Console");
                    System.out.println();
                    
                    // 메뉴 출력
                    System.out.println("1. Connect to DB");
                    if(readOnly) System.out.println("2. Disable Read-Only mode");
                    else         System.out.println("2. Enable Read-Only mode");
                    System.out.println("3. Use embedded DB (HyperSQL DB)");
                    System.out.println("4. EXIT");
                    
                    System.out.print(">> ");
                    line = reader.readLine();
                    if(line == null) continue;
                    if(line.equals("exit")) break;
                    
                    sel = Integer.parseInt(line.trim());
                    
                    if(sel == 4) break;
                    else if(sel == 3) onEmbeddedDB(reader);
                    else if(sel == 2) { readOnly = (! readOnly); System.out.println(readOnly ? "Read-Only mode enabled." : "Read-Only mode disabled."); }
                    else if(sel == 1) onConnectToDB(reader, readOnly);
                    else throw new NumberFormatException("Wrong number !");
                } catch(NumberFormatException ex) {
                    System.out.println("Wrong number !");
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
            
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            ClassUtil.closeAll(reader);
            System.out.println("Bye");
        }
    }
    
    protected void onEmbeddedDB(BufferedReader reader) {
        Connection conn = null;
        boolean classLoadFin = false;
        
        try {
            if(! classLoadFin) {
                Class.forName("org.hsqldb.jdbc.JDBCDriver");
            }
            
            String jdbcUrl = "jdbc:hsqldb:mem:testdb";
            
            conn = DriverManager.getConnection(jdbcUrl);
            System.out.println("Opened !");
            
            onDBMenu(reader, conn, false);
        } catch(ClassNotFoundException ex) {
            System.out.println("Wrong JDBC Driver class !");
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            ClassUtil.closeAll(conn);
        }
    }
}
