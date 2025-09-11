package org.duckdns.hjow.samples.charsetconv.charset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

import org.duckdns.hjow.samples.charsetconv.CommonFileConverter;

/**
 * Convert charset of file from someone to another.
 */
public class CharsetConverter extends CommonFileConverter {
    public CharsetConverter() {
        setProperty("CHARSET_BEFORE", "UTF-16");
        setProperty("CHARSET_AFTER" , "UTF-8");
    }

    @Override
    public void convert(InputStream read, OutputStream output) {
        InputStreamReader in2 = null;
        BufferedReader    in3 = null;
        
        OutputStreamWriter out2 = null;
        BufferedWriter     out3 = null;
        
        Throwable exc = null;
        
        String startsWith = getProperty("SKIP_PREFIX");
        String endsWith   = getProperty("SKIP_SUFFIX");
        
        String fullAccess = getProperty("FULL_WORK");
        
        if(startsWith != null) startsWith = startsWith.trim();
        if(endsWith   != null) endsWith   = endsWith.trim();
        
        if("".equals(startsWith)) startsWith = null;
        if("".equals(endsWith  )) endsWith   = null;
        
        if(fullAccess == null) fullAccess = "false";
        fullAccess = fullAccess.toLowerCase();
        
        try {
            if("false".equals(fullAccess)) {
                in2 = new InputStreamReader(read, getProperty("CHARSET_BEFORE"));
                in3 = new BufferedReader(in2);
                
                out2 = new OutputStreamWriter(output, getProperty("CHARSET_AFTER"));
                out3 = new BufferedWriter(out2);
                
                String line;
                boolean firsts = true;
                while(true) {
                    line = in3.readLine();
                    if(line == null) break;
                    
                    if(startsWith != null) {
                        if(line.trim().startsWith(startsWith)) continue;
                    }
                    if(endsWith != null) {
                        if(line.trim().endsWith(endsWith)) continue;
                    }
                    
                    if(! firsts) out3.newLine();
                    out3.write(line);
                    
                    firsts = false;
                }
                
                in3.close();  in3  = null;
                out3.close(); out3 = null;
                
                in2.close();  in2  = null;
                out2.close(); out2 = null;
            } else {
                StringBuilder coll = new StringBuilder();
                in2 = new InputStreamReader(read, getProperty("CHARSET_BEFORE"));
                in3 = new BufferedReader(in2);
                
                String line;
                while(true) {
                    line = in3.readLine();
                    if(line == null) break;
                    
                    if(startsWith != null) {
                        if(line.trim().startsWith(startsWith)) continue;
                    }
                    if(endsWith != null) {
                        if(line.trim().endsWith(endsWith)) continue;
                    }
                    
                    coll = coll.append(line).append("\n");
                }
                
                in3.close();  in3  = null;
                in2.close();  in2  = null;
                
                out2 = new OutputStreamWriter(output, getProperty("CHARSET_AFTER"));
                out3 = new BufferedWriter(out2);
                
                StringTokenizer lineTokenizer = new StringTokenizer(coll.toString().trim(), "\n");
                coll.setLength(0);
                coll = null;
                
                boolean firsts = true;
                while(lineTokenizer.hasMoreTokens()) {
                    if(!firsts) out3.newLine();
                    out3.write(lineTokenizer.nextToken());
                    firsts = false;
                }
                
                out3.close(); out3 = null;
                out2.close(); out2 = null;
            }
        } catch(Throwable t) {
            exc = t;
        } finally {
            if(in3  != null) { try { in3.close();   } catch(Throwable tx) {} }
            if(in2  != null) { try { in2.close();   } catch(Throwable tx) {} }
            if(out3 != null) { try { out3.close();  } catch(Throwable tx) {} }
            if(out2 != null) { try { out2.close();  } catch(Throwable tx) {} }
        }
        
        if(exc != null) throw new RuntimeException(exc.getMessage(), exc);
    }
}
