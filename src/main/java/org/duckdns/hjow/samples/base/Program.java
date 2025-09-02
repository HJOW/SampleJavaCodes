package org.duckdns.hjow.samples.base;

import org.duckdns.hjow.samples.scripts.ScriptObject;

public interface Program extends ScriptObject {
    public void init(SampleJavaCodes superInstance);
    public void onBeforeOpened(SampleJavaCodes superInstance);
    public void onAfterOpened(SampleJavaCodes superInstance);
    public String getTitle();
    public String getName();
    public void log(String msg);
    public void open(SampleJavaCodes superInstance);
    public boolean isHidden();
}
