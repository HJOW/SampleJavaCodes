package org.duckdns.hjow.samples.colonyman;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import org.duckdns.hjow.commons.ui.JLogArea;
import org.duckdns.hjow.samples.util.UIUtil;

/** 전역 로그 출력 대화상자 */
public class GlobalLogDialog {
    protected static JLogArea taLog;
    protected JDialog dialog;
    
    public GlobalLogDialog(ColonyManager superInstance) {
        init(superInstance);
    }

    protected void init(ColonyManager superInstance) {
        dialog = new JDialog(superInstance.getDialog());
        dialog.setSize(600, 400);
        dialog.setTitle("로그");
        UIUtil.center(dialog);

        dialog.setLayout(new BorderLayout());

        if(taLog == null) {
            taLog = new JLogArea();
            taLog.setLineWrap(true);
        }
        
        dialog.add(new JScrollPane(taLog), BorderLayout.CENTER);
    }

    public void log(String msg) {
        taLog.log(msg);
    }

    public void clear() {
        taLog.clear();
    }

    public void open(ColonyManager superInstance) {
        if(dialog == null) {
            init(superInstance);
        }
        dialog.setVisible(true);
    }

    public void close() {
        dialog.setVisible(false);
    }
    
    public void dispose() {
        close();
        dialog.removeAll();
        dialog = null;
    }
}
