package org.duckdns.hjow.samples.charsetconv.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * The main class of this program. Run this program as GUI or console mode.
 */
public class ConvertManager implements UI {
    @Override
    public void logObj(Object content) {
        if(content instanceof Throwable) content = convertExceptionString((Throwable) content);
        System.out.println(content);
    }

    @Override
    public void alert(Object content) {
        logObj(content);
    }
    
    /** Convert exception messages and stack traces into readable string. */
    protected String convertExceptionString(Throwable t) {
        ByteArrayOutputStream coll = new ByteArrayOutputStream();
        PrintStream st = null;
        try { st = new PrintStream(coll); t.printStackTrace(st); } catch(Exception ex) { ex.printStackTrace(); } finally { if(st != null) st.close(); }
        return new String(coll.toByteArray());
    }

    @Override
    public void open() {
        // TODO Auto-generated method stub
        
    }
}
