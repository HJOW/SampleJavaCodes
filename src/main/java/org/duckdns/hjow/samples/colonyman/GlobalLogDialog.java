package org.duckdns.hjow.samples.colonyman;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import org.duckdns.hjow.commons.ui.JLogArea;
import org.duckdns.hjow.samples.util.UIUtil;

/** 전역 로그 출력 대화상자 */
public class GlobalLogDialog {
    protected JLogArea taLog;
    protected JDialog dialog;
    protected boolean threadSwitch = true;
    
    public GlobalLogDialog(ColonyManager superInstance) {
        init(superInstance);
    }

    protected void init(ColonyManager superInstance) {
        if(superInstance instanceof GUIColonyManager) dialog = new JDialog(((GUIColonyManager) superInstance).getDialog());
        else dialog = new JDialog();
        dialog.setSize(600, 400);
        dialog.setTitle("로그");
        UIUtil.center(dialog);

        dialog.setLayout(new BorderLayout());

        taLog = new JLogArea();
        taLog.setLineWrap(true);
        
        dialog.add(new JScrollPane(taLog), BorderLayout.CENTER);
        new Thread(new Runnable() {
			@Override
			public void run() {
				while(threadSwitch) {
					oneCycle();
					try { Thread.sleep(250L); } catch(InterruptedException ex) { threadSwitch = false; break; }
				}
			}
		}).start();
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
    
    public void oneCycle() {
    	GlobalLogs inst = GlobalLogs.getInstance();
    	while(! inst.isEmpty()) {
    		log(inst.poll());
    	}
    }
    
    public void dispose() {
    	threadSwitch = false;
        close();
        dialog.removeAll();
        dialog = null;
    }
}
