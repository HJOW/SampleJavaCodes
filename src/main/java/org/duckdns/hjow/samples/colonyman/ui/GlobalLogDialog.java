package org.duckdns.hjow.samples.colonyman.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.duckdns.hjow.commons.ui.JLogArea;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.GlobalLogs;
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
        
        JPanel pnMain, pnDown;
        pnMain = new JPanel();
        pnDown = new JPanel();
        pnMain.setLayout(new BorderLayout());
        pnDown.setLayout(new BorderLayout());
        dialog.add(pnMain, BorderLayout.CENTER);
        dialog.add(pnDown, BorderLayout.SOUTH);

        taLog = new JLogArea();
        taLog.setLineWrap(true);
        pnMain.add(new JScrollPane(taLog), BorderLayout.CENTER);
        
        JToolBar toolbar = new JToolBar();
        pnDown.add(toolbar, BorderLayout.CENTER);
        
        JButton btnClear = new JButton("비우기");
        toolbar.add(btnClear);
        
        btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalLogs.getInstance().clear();
				taLog.clear();
			}
		});
        
        threadSwitch = true;
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
    
    public void setSize(int w, int h) {
    	dialog.setSize(w, h);
    }
    
    public Dimension getSize() {
    	return dialog.getSize();
    }
    
    public void setLocationBottom(JDialog superDialog) {
    	Point p = superDialog.getLocation();
    	dialog.setSize(superDialog.getWidth(), dialog.getHeight());
    	dialog.setLocation((int) p.getX(), (int) (p.getY() + superDialog.getHeight()));
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
