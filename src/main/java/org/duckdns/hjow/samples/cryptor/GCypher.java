package org.duckdns.hjow.samples.cryptor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.duckdns.hjow.samples.base.Program;
import org.duckdns.hjow.samples.base.SampleJavaCodes;
import org.duckdns.hjow.samples.cryptor.modules.CypherModule;
import org.duckdns.hjow.samples.cryptor.modules.ModuleLoader;
import org.duckdns.hjow.samples.interfaces.Disposeable;
import org.duckdns.hjow.samples.util.ResourceUtil;
import org.duckdns.hjow.samples.util.UIUtil;

/**
 * 암/복호화 기능을 제공하는 UI 클래스입니다.
 * 
 * @author HJOW
 *
 */
public class GCypher implements Program {
    private static final long serialVersionUID = 1987839294285455468L;
    protected JDialog           dialog;
    protected JSplitPane        splitPane;
    protected JToolBar          toolbar;
    protected JTextArea         before, after;
    protected JComboBox<String> cbModule;
    protected JPasswordField    pwField;
    protected JButton           btnAct;
    protected JMenuItem         menuAct;
    protected GFileHash         fileHash;
    protected GFileCypher       fileCypher;
    
    protected transient Vector<Disposeable> disposeables = new Vector<Disposeable>();
    protected transient Properties properties = new Properties();
    
    /** GCypher 기본 생성자이자 유일한 생성자입니다. */
    public GCypher(SampleJavaCodes superInstance) {
        super();
        init(superInstance);
    }
    /** UI를 초기화합니다. */
    @Override
    public void init(SampleJavaCodes superInstance) {
        if(dialog != null) dispose();
        
    	properties.putAll(ResourceUtil.loadPropResource("/bundled.properties"));
    	properties.putAll(ResourceUtil.loadPropResource("/config.properties"));
    	
    	UIUtil.applyLookAndFeel(properties);
        
        dialog = new JDialog();
        dialog.setSize(500, 300);
        dialog.setTitle("GCypher");
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		exit();
        	}
		});
        UIUtil.center(dialog);
        
        dialog.setLayout(new BorderLayout());
        
        JPanel pnMain = new JPanel();
        dialog.add(pnMain, BorderLayout.CENTER);
        
        pnMain.setLayout(new BorderLayout());
        
        JPanel pnCenter = new JPanel();
        pnMain.add(pnCenter, BorderLayout.CENTER);
        
        pnCenter.setLayout(new BorderLayout());
        
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        pnCenter.add(splitPane, BorderLayout.CENTER);
        
        before = new JTextArea();
        after  = new JTextArea();
        after.setEditable(false);
        
        splitPane.setTopComponent(new JScrollPane(before));
        splitPane.setBottomComponent(new JScrollPane(after));
        
        toolbar = new JToolBar();
        pnCenter.add(toolbar, BorderLayout.NORTH);
        
        Vector<String> moduleNames = new Vector<String>();
        moduleNames.addAll(ModuleLoader.getNames());
        cbModule = new JComboBox<String>(moduleNames);
        toolbar.add(cbModule);
        
        pwField = new JPasswordField(20);
        toolbar.add(pwField);
        
        btnAct = new JButton("Convert");
        toolbar.add(btnAct);
        
        ActionListener eventAct = new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {   
                    @Override
                    public void run() {
                        act();
                    }
                });
            }
        };
        
        btnAct.addActionListener(eventAct);
        
        fileCypher = new GFileCypher(this);
        disposeables.add(fileCypher);
        
        fileHash = new GFileHash(this);
        disposeables.add(fileHash);
        
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuFile = new JMenu("File");
        
        menuAct = new JMenuItem("Convert");
        menuAct.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
        menuAct.addActionListener(eventAct);
        menuFile.add(menuAct);
        
        menuFile.addSeparator();
        
        JMenuItem menuFileConv = new JMenuItem("GCypher File Converter");
        menuFileConv.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK));
        menuFile.add(menuFileConv);
        menuFileConv.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						fileCypher.open();
					}
				});
			}
		});
        
        JMenuItem menuFileHash = new JMenuItem("GCypher File Hash");
        menuFileHash.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_MASK));
        menuFile.add(menuFileHash);
        menuFileHash.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						fileHash.open();
					}
				});
			}
		});
        
        menuFile.addSeparator();
        
        JMenuItem menuExit = new JMenuItem("Exit");
        menuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK));
        menuFile.add(menuExit);
        menuExit.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        
        menuBar.add(menuFile);
        dialog.setJMenuBar(menuBar);
    }
    /** UI 상에서 동작 버튼이 클릭되었을 때 호출됩니다. */
    protected void act() {
    	after.setText("");
        String moduleName = (String) cbModule.getSelectedItem();
        CypherModule module = ModuleLoader.get(moduleName);
        
        String password;
        char[] pwInput = pwField.getPassword();
        password = new String(pwInput);
        
        btnAct.setEnabled(false);
        menuAct.setEnabled(false);
        before.setEditable(false);
        try {
            after.setText(module.convert(before.getText(), password, properties));
        } catch(Throwable t) {
            after.setText("[ERROR]\n" + t.getMessage());
        }
        before.setEditable(true);
        btnAct.setEnabled(true);
        menuAct.setEnabled(true);
    }
    /** UI를 열어 본격적으로 프로그램 사용을 시작합니다. */
    @Override
    public void open(SampleJavaCodes superInstance) {
        if(dialog == null) init(superInstance);
    	dialog.setVisible(true);
        splitPane.setDividerLocation(0.5);
    }
    /**  프로그램 종료 */
    public void exit() {
        dispose();
    }
    
    @Override
    public void dispose() {
        dialog.setVisible(false);
        dialog = null;
    }
    @Override
    public void onBeforeOpened(SampleJavaCodes superInstance) {
        
    }
    @Override
    public void onAfterOpened(SampleJavaCodes superInstance) {
        
    }
    @Override
    public String getTitle() {
        return "GCypher";
    }
    @Override
    public void log(String msg) {
        System.out.println(msg);
    }
    @Override
    public void alert(String msg) {
        JOptionPane.showMessageDialog(dialog, msg);
    }
    @Override
    public String getName() {
        return "gcypher";
    }
    @Override
    public JDialog getDialog() {
        return dialog;
    }
}
