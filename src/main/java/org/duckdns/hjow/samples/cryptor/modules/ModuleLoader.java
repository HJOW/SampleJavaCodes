package org.duckdns.hjow.samples.cryptor.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.samples.interfaces.LineListener;
import org.duckdns.hjow.samples.util.ResourceUtil;

/** 암/복호화 모듈을 관리하는 클래스입니다. */
public class ModuleLoader {
    private static final List<CypherModule> modules = new Vector<CypherModule>();
    /** 모듈을 등록합니다. */
    @SuppressWarnings("unchecked")
    public static void register(String moduleClass) {
        try {
            Class<? extends CypherModule> moduleClassObj = (Class<? extends CypherModule>) Class.forName(moduleClass);
            register(moduleClassObj.newInstance());
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
    /** 모듈을 등록합니다. */
    public static void register(CypherModule m) {
        modules.add(m);
    }
    /** 모듈 이름들을 반환합니다. */
    public static List<String> getNames() {
        List<String> names = new ArrayList<String>();
        for(CypherModule m : modules) {
            names.add(m.name());
        }
        return names;
    }
    /** 모듈 이름으로 모듈을 찾습니다. 찾지 못하면 null 이 반환됩니다. */
    public static CypherModule get(String name) {
        for(CypherModule m : modules) {
            if(m.name().equals(name)) return m;
        }
        return null;
    }
    
    static {
    	loadResource("/cypher/bundled.txt");
    	loadResource("/cypher/list.txt");
    }
    
    private static void loadResource(String resourceName) {
        try {
            ResourceUtil.loadResource(resourceName, '#', new LineListener() {
                @Override
                public void onEachLine(String line) {
                    register(line);
                }
            });
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
