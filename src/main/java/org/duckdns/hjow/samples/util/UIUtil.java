package org.duckdns.hjow.samples.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/** Swing Utils */
public class UIUtil {
    public static void applyLookAndFeel(Properties prop) {
    	try {
    		String theme = "Nimbus";
    		if(prop.getProperty("look.and.feel") != null) theme = prop.getProperty("look.and.feel");
    		
    		if(theme.equalsIgnoreCase("default")) theme = "Nimbus";
    		if(theme.equalsIgnoreCase("metal"  )) theme = "Metal";
    		
    		if(theme.equalsIgnoreCase("system")) {
    			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    			return;
    		}
    		
            boolean found = false;
            for(LookAndFeelInfo infos : UIManager.getInstalledLookAndFeels()) {
                if(theme.equalsIgnoreCase(infos.getName())) {
                    UIManager.setLookAndFeel(infos.getClassName());
                    found = true;
                }
            }
            if(!found) UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex) { ex.printStackTrace(); }
    }
    
    public static void center(Window win) {
    	Dimension sc = Toolkit.getDefaultToolkit().getScreenSize();
    	win.setLocation((int)((sc.getWidth() / 2) - (win.getWidth() / 2)), (int)((sc.getHeight() / 2) - (win.getHeight() / 2)));
    }
}
