package org.duckdns.hjow.samples.base;

import javax.swing.Icon;
import javax.swing.JDialog;

import org.duckdns.hjow.samples.scripts.ScriptObject;

public interface Program extends ScriptObject {
    public void init(SampleJavaCodes superInstance);
    public void onBeforeOpened(SampleJavaCodes superInstance);
    public void onAfterOpened(SampleJavaCodes superInstance);
    public String getTitle();
    public void log(String msg);
    public void alert(String msg);
    public String getName();
    public Icon getIcon();
    public JDialog getDialog();
    public void open(SampleJavaCodes superInstance);
    public boolean isHidden();
}
