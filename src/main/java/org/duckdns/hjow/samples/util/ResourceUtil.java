package org.duckdns.hjow.samples.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.duckdns.hjow.samples.cryptor.modules.ModuleLoader;
import org.duckdns.hjow.samples.interfaces.LineListener;

public class ResourceUtil {
    /** 리소스에서 프로그램 설정을 불러옵니다. */
    public static Properties loadPropResource(String resourceName) {
        InputStream inp1 = null;
        Properties  prop = new Properties();
        
        try {
            inp1 = ModuleLoader.class.getResourceAsStream(resourceName);
            if(inp1 != null) {
                if(resourceName.toLowerCase().trim().endsWith(".xml")) prop.loadFromXML(inp1);
                else prop.load(inp1);
                inp1.close(); inp1 = null;
            }
        } catch(Throwable t) {
            t.printStackTrace();
        } finally {
            if(inp1 != null) try { inp1.close();  } catch(Exception ignores) {}
        }
        return prop;
    }
    
    /** 리소스로부터 텍스트 파일을 읽어 한줄 당 하나의 처리를 수행합니다. */
    public static void loadResource(String resourceName, LineListener lineListener) {
        loadResource(resourceName, ' ', lineListener);
    }
    
    /** 리소스로부터 텍스트 파일을 읽어 한줄 당 하나의 처리를 수행합니다. commentKey 는 주석 구분자로, 각 줄에서 이 글자로 시작하는 부분 이후는 주석으로 처리됩니다. 단 commentKey가 공란인 경우 주석을 안쓰는 것으로 간주합니다. */
    public static void loadResource(String resourceName, char commentKey, LineListener lineListener) {
        InputStream       inp1 = null;
        InputStreamReader inp2 = null;
        BufferedReader    inp3 = null;
        Throwable caused = null;
        
        try {
            inp1 = ResourceUtil.class.getResourceAsStream(resourceName);
            if(inp1 != null) {
                inp2 = new InputStreamReader(inp1, "UTF-8");
                inp3 = new BufferedReader(inp2);
                
                String line;
                while(true) {
                    line = inp3.readLine();
                    if(line == null) break;
                    
                    try {
                        lineListener.onEachLine(processComment(line, commentKey).trim());
                    } catch(Exception ex) {
                        caused = ex;
                    }
                }
                
                inp3.close(); inp3 = null;
                inp2.close(); inp2 = null;
                inp1.close(); inp1 = null;
                
                if(caused != null) throw caused;
            }
        } catch(Throwable t) {
            t.printStackTrace();
            caused = t;
        } finally {
            if(inp3 != null) try { inp3.close();  } catch(Exception ignores) {}
            if(inp2 != null) try { inp2.close();  } catch(Exception ignores) {}
            if(inp1 != null) try { inp1.close();  } catch(Exception ignores) {}
        }
        if(caused != null) throw new RuntimeException(caused.getMessage(), caused);
    }
    
    /** 주석 적용 */
    private static String processComment(String line, char comment) {
        if(comment == ' ') return line;
        if(comment == ((char) 0)) return line;
        if(! line.contains(String.valueOf(comment))) return line;
        StringBuilder res = new StringBuilder("");
        
        int totals = line.length();
        char    quotes      = ' ';
        
        for(int idx=0; idx<totals; idx++) {
            char charOne = line.charAt(idx);
            
            if(quotes == ' ') {
                if(charOne == comment) break;
                if(charOne == '\n') break;
                if(charOne == '\'') quotes = '\'';
                if(charOne == '"' ) quotes = '"' ;
            } else {
                if(charOne == quotes) quotes = ' ';
            }
            
            res = res.append(String.valueOf(charOne));
        }
        
        return res.toString();
    }
}
