package org.duckdns.hjow.samples.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.duckdns.hjow.commons.util.ClassUtil;

/** 언어 지원을 위한 String Table */
public class StringTable implements Serializable {
    private static final long serialVersionUID = 7324274539053757150L;
    protected String     name = "Unnamed";
    protected Properties data = new Properties();
    
    public StringTable() { }
    public StringTable(File file) {
        String nameLower = file.getName().toLowerCase().trim();
        
        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(file);
            data.clear();
            load(nameLower.endsWith(".xml"), fileIn);
            setNameFrom(file.getName());
            fileIn.close();
            fileIn = null;
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            ClassUtil.closeAll(fileIn);
        }
    }
    
    public StringTable(String resources, Class<?> sameResourcePathClass) {
        InputStream stream = null;
        try {
            Class<?> roots = sameResourcePathClass;
            if(roots == null) roots = StringTable.class;
            stream = roots.getResourceAsStream(resources);
            
            data.clear();
            load(resources.toLowerCase().trim().endsWith(".xml"), stream);
            setNameFrom(resources);
            
            stream.close();
            stream = null;
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            ClassUtil.closeAll(stream);
        }
    }
    
    /** 스트림으로부터 데이터 불러오기 (스트림을 닫지 않음 !) */
    public void load(boolean xml, InputStream stream) {
        try {
            data.clear();
            if(xml) {
                data.loadFromXML(stream);
            } else {
                data.load(stream);
            }
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /** 파일 이름으로부터 스트링 테이블 이름 구하기 (확장자 제외) */
    protected void setNameFrom(String fileName) {
        StringBuilder names = new StringBuilder("");
        
        String[] splits = fileName.split(".");
        if(splits.length == 1) {
            names = names.append(splits[0]);
        } else {
            for(int idx=0; idx<splits.length - 1; idx++) {
                if(idx > 0) names = names.append(".");
                names = names.append(splits[idx]);
            }
        }
        
        setName(names.toString().trim());
    }
    
    /** 번역된 값을 반환, 단 스트링테이블에 없는 경우 원래 값 그대로 반환 */
    public String t(String originals) {
        String res = getData().getProperty(originals);
        if(res == null) return originals;
        return res;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Properties getData() {
        return data;
    }
    public void setData(Properties data) {
        this.data = data;
    }
    
    protected static Map<String, StringTable> stringTables = new HashMap<String, StringTable>();
    /** 등록된 스트링 테이블들 모두 비우기 */
    public static void removeAllStringTables() { stringTables.clear(); }
    /** 등록된 스트링 테이블 이름들 반환 */
    public static Set<String> getStringTableNames() { return stringTables.keySet(); }
    /** 해당 이름의 스트링 테이블 하나를 반환 */
    public static StringTable get(String name) { return stringTables.get(name); }
    /** 스트링 테이블 등록 */
    public static StringTable register(StringTable table) { stringTables.put(table.getName(), table); return table; }
    /** 파일에서 스트링 테이블 불러와 등록 */
    public static StringTable register(File file) {
        StringTable table = new StringTable(file);
        stringTables.put(table.getName(), table);
        return table;
    }
    /** 디렉토리에서 스트링 테이블 모두 불러와 등록, 확장자가 xml 또는 properties 여야 함 */
    public static void registerDirectory(File dir) {
        File[] lists = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.isDirectory()) return false;
                String name = pathname.getName().toLowerCase().trim();
                return name.endsWith(".xml") || name.endsWith(".properties");
            }
        });
        
        for(File f : lists) {
            register(f);
        }
    }
    /** 리소스에서 스트링 테이블 불러와 등록 */
    public static StringTable registerResource(String resourceName, Class<?> sameResourcePathClass) {
        StringTable table = new StringTable(resourceName, sameResourcePathClass);
        stringTables.put(table.getName(), table);
        return table;
    }
    /** 스트링 테이블 이름 지정해서 바로 번역 */
    public static String t(String name, String originals) {
        StringTable obj = get(name);
        if(obj == null) return originals;
        return obj.t(originals);
    }
}
