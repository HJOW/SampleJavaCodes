package org.duckdns.hjow.samples.charsetconv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Common implementation of FileConverter.
 */
public abstract class CommonFileConverter implements FileConverter {
    /** configurations */
    protected Properties property = new Properties();

    @Override
    public void convert(File file) {
        FileInputStream   in1  = null;
        FileOutputStream  out1 = null;
        
        Throwable exc = null;
        
        try {
            // 한줄씩 읽어, 바로바로 임시파일에 저장
            File temp = null;
            String tempSuffix = "";
            int tempIndex = 0;
            
            tempSuffix = String.valueOf(tempIndex) + ".TEMP";
            temp = new File(file.getAbsolutePath() + tempSuffix);
            
            while(temp.exists()) {
                tempIndex++;
                tempSuffix = String.valueOf(tempIndex) + ".TEMP";
                temp = new File(file.getAbsolutePath() + tempSuffix);
            }
            
            in1 = new FileInputStream(file);
            out1 = new FileOutputStream(temp);
            
            convert(in1, out1);
            
            in1.close();  in1  = null;
            out1.close(); out1 = null;
            
            // 원래 파일을 지우고 임시파일을 원래파일로 이름 변경
            String fullName = file.getAbsolutePath();
            file.delete();
            temp.renameTo(new File(fullName));
        } catch(Throwable t) {
            exc = t;
        } finally {
            if(in1  != null) { try { in1.close();   } catch(Throwable tx) {} }
            if(out1 != null) { try { out1.close();  } catch(Throwable tx) {} }
        }
        
        if(exc != null) throw new RuntimeException(exc.getMessage(), exc);
    }
    
    @Override
    public String getProperty(String key) {
        return property.getProperty(key);
    }
    
    @Override
    public void setProperty(String key, String value) {
        property.setProperty(key, value);
    }

    /** Default getter */
    public Properties getProperty() {
        return property;
    }

    /** Default setter */
    public void setProperty(Properties property) {
        this.property = property;
    }
}
