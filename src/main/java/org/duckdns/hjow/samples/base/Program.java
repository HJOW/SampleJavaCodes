package org.duckdns.hjow.samples.base;

import java.io.Serializable;

import javax.swing.JDialog;

import org.duckdns.hjow.samples.interfaces.Disposeable;

public interface Program extends Serializable, Disposeable {
    public void init(SampleJavaCodes superInstance);
    public void onBeforeOpened(SampleJavaCodes superInstance);
    public void onAfterOpened(SampleJavaCodes superInstance);
    public String getTitle();
    public void log(String msg);
    public void alert(String msg);
    public String getName();
    public JDialog getDialog();
    public void open(SampleJavaCodes superInstance);
}
