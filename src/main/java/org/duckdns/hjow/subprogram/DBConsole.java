package org.duckdns.hjow.subprogram;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.commons.util.DataUtil;

public class DBConsole {
    public DBConsole() {}
    
    protected static final DecimalFormat FORMATTER_INT   = new DecimalFormat("#,###");
    protected static final DecimalFormat FORMATTER_FLOAT = new DecimalFormat("#,##0.00");
    
    /** 1단계 - 메인 메뉴 */
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
                    System.out.println("3. EXIT");
                    
                    System.out.print(">> ");
                    line = reader.readLine();
                    if(line == null) continue;
                    if(line.equals("exit")) break;
                    
                    sel = Integer.parseInt(line.trim());
                    
                    if(sel == 3) break;
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
    
    /** 2단계 - JDBC 정보 입력 단계 */
    protected void onConnectToDB(BufferedReader reader, boolean readOnly) {
        String line;
        String jdbcUrl  = null;
        String jdbcId   = null;
        String jdbcPw   = null;
        Connection conn = null;
        boolean classLoadFin = false;
        
        while(true) {
            try {
                if(! classLoadFin) {
                    System.out.println("Please input the full name of JDBC Driver class\n(Cancelled when you press enter without input anything)");
                    System.out.print(">> ");
                    line = reader.readLine();
                    if(line == null) continue;
                    if(DataUtil.isEmpty(line)) break;
                    
                    Class.forName(line.trim());
                    classLoadFin = true;
                }
                
                System.out.println("Please input the JDBC URL.\n(Cancelled when you press enter without input anything)");
                System.out.print(">> ");
                line = reader.readLine();
                if(line == null) continue;
                if(DataUtil.isEmpty(line)) break;
                
                jdbcUrl = line.trim();
                
                System.out.println("Please input the username.\n(Cancelled when you press enter without input anything)");
                System.out.print(">> ");
                line = reader.readLine();
                if(line == null) continue;
                if(DataUtil.isEmpty(line)) break;
                
                jdbcId = line.trim();
                
                System.out.println("Please input the password.\n(Cancelled when you press enter without input anything)");
                System.out.print(">> ");
                line = reader.readLine();
                if(line == null) continue;
                if(DataUtil.isEmpty(line)) break;
                
                jdbcPw = line.trim();
                
                conn = DriverManager.getConnection(jdbcUrl, jdbcId, jdbcPw);
                conn.setReadOnly(readOnly);
                System.out.println("Connected !");
                
                onDBMenu(reader, conn, readOnly);
                break;
            } catch(ClassNotFoundException ex) {
                System.out.println("Wrong JDBC Driver class !");
            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
                if(! readOnly) { try { conn.rollback(); } catch(Exception ignores) {} }
                ClassUtil.closeAll(conn);
            }
        }
    }
    
    /** 3단계 - DB 접속 완료 후 메뉴 */
    protected void onDBMenu(BufferedReader reader, Connection conn, boolean readOnly) {
        String line;
        int sel;
        while(true) {
            try {
                System.out.println();
                
                System.out.println("On DB, what do you want to do?" + (readOnly ? " (Read-Only mode)"  : ""));
                System.out.println("1. Run SQL");
                System.out.println("2. Commit");
                System.out.println("3. Rollback");
                System.out.println("4. Disconnect");
                
                System.out.print(">> ");
                line = reader.readLine();
                if(line == null) continue;
                
                sel = Integer.parseInt(line.trim());
                
                if(sel == 4) break;
                else if(sel == 3) { conn.rollback(); System.out.println("Rollbacked."); continue; }
                else if(sel == 2) { conn.commit(); System.out.println("Committed."); continue; }
                else if(sel == 1) onQuerying(reader, conn);
                else throw new NumberFormatException("Wrong number !");
            } catch(NumberFormatException ex) {
                System.out.println("Wrong number !");
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /** 4단계 - SQL 실행 */
    protected void onQuerying(BufferedReader reader, Connection conn) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String line;
        StringBuilder sqls = new StringBuilder("");
        boolean cancelled = false;
        
        while(true) {
            try {
                System.out.println();
                
                System.out.println("Input your SQL. \n    Put ';' at end to complete SQL.\n    If you want to cancel, just input 'exit'.");
                
                System.out.print(">> ");
                line = reader.readLine();
                if(line == null) continue;
                line = line.trim();
                
                if(line.equals("exit")) break;
                
                sqls.setLength(0);
                sqls = sqls.append(line);
                
                while(! sqls.toString().trim().endsWith(";")) {
                    System.out.print(">> ");
                    line = reader.readLine();
                    if(line == null) line = "";
                    line = line.trim();
                    
                    if(line.equals("exit")) { cancelled = true; break; }
                    
                    sqls = sqls.append("\n").append(line);
                }
                if(cancelled) break;
                
                System.out.println();
                System.out.println();
                System.out.println();
                
                String sql = sqls.toString().trim();
                sqls = null;
                
                System.out.println(sql);
                System.out.println(" * Do you want to run this? (Y/N)");
                
                System.out.print(">> ");
                line = reader.readLine();
                if(line == null) continue;
                line = line.trim();
                
                if(line.equals("exit")) break;
                if(! DataUtil.parseBoolean(line)) break;
                
                line = null;
                
                pstmt = conn.prepareStatement(sql);
                sql = null;
                
                boolean rsExists = pstmt.execute();
                if(rsExists) {
                    rs = pstmt.getResultSet();
                    ResultSetMetaData meta = rs.getMetaData();
                    
                    int colCnt = meta.getColumnCount();
                    List<String> columnNames = new ArrayList<String>();
                    List<Object> columnTypes = new ArrayList<Object>();
                    
                    for(int idx=0; idx<colCnt; idx++) {
                        String colName = meta.getColumnName(idx+1);
                        columnNames.add(colName);
                        columnTypes.add(new Integer(meta.getColumnType(idx+1)));
                    }
                    
                    // Print Headers
                    System.out.print("NO");
                    System.out.print("\t|\t");
                    for(String h : columnNames) {
                        System.out.print(h);
                        System.out.print("\t|\t");
                    }
                    System.out.println();
                    
                    // Print Rows
                    int rowNo = 0;
                    while(rs.next()) {
                        rowNo++;
                        
                        System.out.print(rowNo);
                        System.out.print("\t|\t");
                        for(int idx=0; idx<colCnt; idx++) {
                            int type = ((Integer) columnTypes.get(idx)).intValue();
                            String val = "";
                            
                            if(     type == Types.VARCHAR ) val = rs.getString(idx+1);
                            else if(type == Types.CHAR    ) val = rs.getString(idx+1);
                            else if(type == Types.NVARCHAR) val = rs.getString(idx+1);
                            else if(type == Types.NUMERIC ) val = FORMATTER_FLOAT.format(rs.getDouble(idx+1));
                            else if(type == Types.FLOAT   ) val = FORMATTER_FLOAT.format(rs.getDouble(idx+1));
                            else if(type == Types.DECIMAL ) val = FORMATTER_FLOAT.format(rs.getBigDecimal(idx+1));
                            else if(type == Types.INTEGER ) val = FORMATTER_INT.format(rs.getInt(idx+1));
                            else if(type == Types.BIGINT  ) val = FORMATTER_INT.format(rs.getLong(idx+1));
                            else if(type == Types.BOOLEAN ) val = rs.getBoolean(idx + 1) ? "Y" : "N";
                            else if(type == Types.NULL    ) val = "[NULL]";
                            else if(type == Types.BLOB    ) val = "[BLOB]";
                            else if(type == Types.CLOB    ) val = "[CLOB]";
                            else val = String.valueOf(rs.getObject(idx+1));
                            
                            System.out.print(val);
                            System.out.print("\t|\t");
                        }
                        
                        System.out.println();
                    }
                    System.out.println("Total " + rowNo + "rows.");
                } else {
                    System.out.println("SQL execution complete.");
                    System.out.println("    Affected rows : " + pstmt.getUpdateCount());
                }
                break;
            } catch(SQLException ex) {
                System.out.println("SQL Error (" + ex.getErrorCode() + ") " + ex.getMessage());
                SQLException nexts;
                nexts = ex.getNextException();
                while(nexts != null) {
                    System.out.println("Caused SQL Error (" + nexts.getErrorCode() + ") " + nexts.getMessage());
                    nexts = nexts.getNextException();
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
                ClassUtil.closeAll(rs, pstmt);
            }
        }
    }
    
    public static void main(String[] args) {
        // 매개변수 수집
        StringBuilder params = new StringBuilder("");
        if(args != null) {
            for(String a : args) {
                params = params.append(" ").append(a);
            }
        }
        
        String paramStr = params.toString().trim();
        params = null;
        
        // Map 으로 변환
        Map<String, String> argMap = new HashMap<String, String>();
        if(DataUtil.isNotEmpty(paramStr)) argMap.putAll(DataUtil.parseParameter(paramStr));
        params = null;
        
        // 실행
        new org.duckdns.hjow.subprogram.DBConsole().run(argMap);
    }
}
