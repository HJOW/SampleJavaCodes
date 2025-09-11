package org.duckdns.hjow.samples.charsetconv.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.duckdns.hjow.samples.base.GUIProgram;
import org.duckdns.hjow.samples.base.SampleJavaCodes;
import org.duckdns.hjow.samples.charsetconv.charset.CharsetConverter;

/**
 * GUI implements for this program.
 */
public class GUIConverterManager extends ConvertManager implements GUIProgram {
    private static final long serialVersionUID = -4437868935297398137L;

    protected JDialog dialog;
    
    protected JToolBar          toolbar;
    protected JTabbedPane       tab;
    protected JTextArea         taLog;
    
    protected JPanel            pnTabFileConv;
    
    protected JButton           btnConvert;
    protected JProgressBar      progBar;
    
    protected JFileChooser      fileChooserAny, dirChooser;
    
    protected JTextField        tfFileConvTarget, tfFileConvFileExt, tfFileConvStartsWith, tfFileConvEndsWith;
    protected JComboBox<String> cbxFileConvChsetBef, cbxFileConvChsetAft;
    protected JCheckBox         chkFileConvFullAcc;
    
    public GUIConverterManager(SampleJavaCodes superInstance) {
        init(superInstance);
    }
    
    @Override
    public void init(SampleJavaCodes superInstance) {
        Window superDialog = superInstance.getWindow();
        
        // JDialog 설정
        if(     superDialog instanceof Frame ) dialog = new JDialog((Frame) superDialog);
        else if(superDialog instanceof Dialog) dialog = new JDialog((Dialog)superDialog);
        else dialog = new JDialog();
        
        dialog.setSize(500, 300);
        dialog.setLayout(new BorderLayout());
        dialog.setTitle("Charset Converter");
        
        JPanel pnMain, pnLog, pnCenter;
        
        pnMain = new JPanel();
        dialog.add(pnMain, BorderLayout.CENTER);
        pnMain.setLayout(new BorderLayout());
        
        toolbar = new JToolBar();
        pnMain.add(toolbar, BorderLayout.NORTH);
        
        btnConvert = new JButton("▶");
        toolbar.add(btnConvert);
        
        pnCenter = new JPanel();
        pnMain.add(pnCenter, BorderLayout.CENTER);
        
        pnCenter.setLayout(new BorderLayout());
        
        tab = new JTabbedPane();
        pnCenter.add(tab, BorderLayout.NORTH);
        
        pnLog = new JPanel();
        pnLog.setLayout(new BorderLayout());
        taLog = new JTextArea();
        taLog.setEditable(false);
        
        pnCenter.add(pnLog, BorderLayout.CENTER);
        pnLog.add(new JScrollPane(taLog), BorderLayout.CENTER);
        
        progBar = new JProgressBar();
        pnLog.add(progBar, BorderLayout.SOUTH);
        
        FileFilter filterAny = new FileFilter() {
            @Override
            public String getDescription() {
                return "Any files (*)";
            }
            
            @Override
            public boolean accept(File f) {
                if(f.isDirectory()) return false;
                return true;
            }
        };
        
        FileFilter filterDir = new FileFilter() {
            @Override
            public String getDescription() {
                return "Any directories (*)";
            }
            
            @Override
            public boolean accept(File f) {
                if(f.isDirectory()) return true;
                return false;
            }
        };
        
        fileChooserAny = new JFileChooser();
        fileChooserAny.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooserAny.setFileFilter(filterAny);
        
        dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setFileFilter(filterDir);
        
        prepareTab1();
        
        btnConvert.addActionListener(new ActionListener() {   
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {   
                    @Override
                    public void run() {
                        onAction();
                    }
                });
            }
        });
    }
    
    public void prepareTab1() {
        pnTabFileConv = new JPanel();
        tab.add("Charset Converter", pnTabFileConv);
        
        pnTabFileConv.setLayout(new GridBagLayout());
        
        JLabel lb1;
        JButton btn1;
        
        lb1 = new JLabel("Target");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(lb1, gbc);
        
        tfFileConvTarget = new JTextField(30);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth  = 5;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(tfFileConvTarget, gbc);
        
        btn1 = new JButton("...");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(btn1, gbc);
        
        Vector<String> vCharsets = new Vector<String>();
        vCharsets.add("UTF-16");
        vCharsets.add("UTF-8");
        vCharsets.add("EUC-KR");
        
        cbxFileConvChsetBef = new JComboBox<String>(vCharsets);
        cbxFileConvChsetAft = new JComboBox<String>(vCharsets);
        
        lb1 = new JLabel("Before");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(lb1, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(cbxFileConvChsetBef, gbc);
        
        lb1 = new JLabel("After");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(lb1, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(cbxFileConvChsetAft, gbc);
        
        lb1 = new JLabel("Full Work");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(lb1, gbc);
        
        chkFileConvFullAcc = new JCheckBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        gbc.gridwidth  = 2;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(chkFileConvFullAcc, gbc);
        
        lb1 = new JLabel("Ext");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(lb1, gbc);
        
        tfFileConvFileExt = new JTextField(5);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(tfFileConvFileExt, gbc);
        
        lb1 = new JLabel("Skip Prefix");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(lb1, gbc);
        
        tfFileConvStartsWith = new JTextField(5);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(tfFileConvStartsWith, gbc);
        
        lb1 = new JLabel("Skip Suffix");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(lb1, gbc);
        
        tfFileConvEndsWith = new JTextField(5);
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnTabFileConv.add(tfFileConvEndsWith, gbc);
        
        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int sel = dirChooser.showOpenDialog(dialog);
                if(sel == JFileChooser.APPROVE_OPTION) {
                    tfFileConvTarget.setText(dirChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
    }
    
    public void onActionFileConv() throws Exception {
        File dir = new File(tfFileConvTarget.getText());
        if(! dir.exists()) throw new FileNotFoundException("There is no directory at " + tfFileConvTarget.getText());
        if(! dir.isDirectory()) throw new FileNotFoundException("No directory ! " + tfFileConvTarget.getText());
        
        File[] files = dir.listFiles(new java.io.FileFilter() {   
            @Override
            public boolean accept(File pathname) {
                String ext = tfFileConvFileExt.getText().trim();
                if(ext.equals("")) return true;
                return pathname.getName().toLowerCase().trim().endsWith(ext);
            }
        });
        
        CharsetConverter converter = new CharsetConverter();
        converter.setProperty("CHARSET_BEFORE", String.valueOf(cbxFileConvChsetBef.getSelectedItem()));
        converter.setProperty("CHARSET_AFTER" , String.valueOf(cbxFileConvChsetAft.getSelectedItem()));
        converter.setProperty("FULL_WORK", String.valueOf(chkFileConvFullAcc.isSelected()));
        converter.setProperty("SKIP_PREFIX", tfFileConvStartsWith.getText());
        converter.setProperty("SKIP_SUFFIX" , tfFileConvEndsWith.getText());
        
        log("Work starts.");
        progBar.setMaximum(files.length + 1);
        for(int idx=0; idx<files.length; idx++) {
            File current = files[idx];
            progBar.setValue(idx);
            log(current.getName());
            converter.convert(current);
        }
        progBar.setValue(progBar.getMaximum());
        log("Work finish.");
    }
    
    public void onAction() {
        btnConvert.setEnabled(false);
        Thread threadWorks = new Thread(new Runnable() {
            @Override
            public void run() {
                Exception occurs = null;
                try {
                    progBar.setValue(0);
                    
                    Component sels = tab.getSelectedComponent();
                    if(sels == pnTabFileConv) {
                        onActionFileConv();
                    }
                } catch(Exception ex) {
                    occurs = ex;
                } finally {
                    btnConvert.setEnabled(true);
                }
                if(occurs != null) {
                    alert("Error : " + occurs.getMessage());
                    occurs.printStackTrace();
                }
            }
        });
        threadWorks.start();
    }
    
    @Override
    public void open() {
        dialog.setVisible(true);
        
        tfFileConvFileExt.setText(".sql");
        cbxFileConvChsetBef.setSelectedItem("UTF-16");
        cbxFileConvChsetAft.setSelectedItem("UTF-8");
        tfFileConvTarget.setText("D:\\Workspace\\egov\\42\\hms\\DATABASE\\mssql");
        tfFileConvStartsWith.setText("/****** Object:");
        tfFileConvEndsWith.setText("******/");
        chkFileConvFullAcc.setSelected(true);
    }
    
    @Override
    public void logObj(Object content) {
        super.logObj(content);
        if(content instanceof Throwable) content = convertExceptionString((Throwable) content);
        taLog.setText(taLog.getText() + "\n" + String.valueOf(content));
        taLog.setCaretPosition(taLog.getDocument().getLength() - 1);
    }

    @Override
    public void alert(Object content) {
        super.alert(content);
        JOptionPane.showMessageDialog(dialog, String.valueOf(content));
    }

    @Override
    public void onBeforeOpened(SampleJavaCodes superInstance) {
        
    }

    @Override
    public void onAfterOpened(SampleJavaCodes superInstance) {
        
    }

    @Override
    public String getTitle() {
        return "Character Set Converter";
    }

    @Override
    public String getName() {
        return "charsetconv";
    }

    @Override
    public void log(String msg) {
        logObj(msg);
    }

    @Override
    public void open(SampleJavaCodes superInstance) {
        if(dialog == null) init(superInstance);
        onBeforeOpened(superInstance);
        dialog.setVisible(true);
        onAfterOpened(superInstance);
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void dispose() {
        dialog.setVisible(false);
        dialog.removeAll();
        dialog = null;
    }

    @Override
    public void alert(String msg) {
        JOptionPane.showMessageDialog(getDialog(), msg);
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public JDialog getDialog() {
        return dialog;
    }
}
