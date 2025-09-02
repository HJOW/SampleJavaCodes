package org.duckdns.hjow.samples.base;

import javax.swing.Icon;
import javax.swing.JDialog;

public interface GUIProgram extends Program {
    public void alert(String msg);
    public Icon getIcon();
    public JDialog getDialog();
}
