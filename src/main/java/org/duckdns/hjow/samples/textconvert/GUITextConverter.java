package org.duckdns.hjow.samples.textconvert;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.duckdns.hjow.samples.base.GUIProgram;
import org.duckdns.hjow.samples.base.SampleJavaCodes;
import org.duckdns.hjow.samples.util.UIUtil;

public class GUITextConverter implements GUIProgram {
    private static final long serialVersionUID = -3245930991246791318L;

    protected JDialog dialog;
    
    protected JToolBar    toolbar;
    protected JSplitPane  jsplitText;
    protected JTextArea   taBefore, taAfter, taLog;
    protected JTextField  tfParam;
    protected JButton     btnRun;
    protected JComboBox<TextConverter> cbConverter;
    
    protected Vector<TextConverter> converters = new Vector<TextConverter>();
    
    public GUITextConverter(SampleJavaCodes superInstance) {
        super();
        init(superInstance);
    }
    
    public void loadConverter() {
        DefaultComboBoxModel<TextConverter> cbModel = new DefaultComboBoxModel<TextConverter>(converters);
        cbConverter.setModel(cbModel);
    }
    
    @Override
    public void open(SampleJavaCodes superInstance) {
        if(dialog == null) init(superInstance);
        
        onBeforeOpened(superInstance);
        dialog.setVisible(true);
        onAfterOpened(superInstance);
    }
    
    protected void invokeConvert() {
        btnRun.setEnabled(false);
        taAfter.setText("");
        try {
            TextConverter converter = (TextConverter) cbConverter.getSelectedItem();
            if(converter != null) taAfter.setText(converter.convert(taBefore.getText()));
        } catch(Exception ex) {
            ex.printStackTrace();
            log("Error : " + ex.getMessage());
        }
        
        btnRun.setEnabled(true);
    }
    
    public String convert(TextConverter converter, String inputs, Map<String, String> param) throws Exception {
        return converter.convert(inputs, param);
    }
    
    @Override
    public void log(String content) {
        System.out.println(content);
        taLog.setText(taLog.getText() + "\n" + content);
        taLog.setCaretPosition(taLog.getDocument().getLength() - 1);
    }
    
    @Override
    public void init(SampleJavaCodes superInstance) {
        if(dialog != null) dispose();
        
        Window superDialog = superInstance.getWindow();
        if(     superDialog instanceof Frame ) dialog = new JDialog((Frame) superDialog);
        else if(superDialog instanceof Dialog) dialog = new JDialog((Dialog)superDialog);
        else dialog = new JDialog();
        
        dialog.setSize(790, 590);
        dialog.setLayout(new BorderLayout());
        dialog.setTitle("텍스트 변환 도구");
        
        UIUtil.center(dialog);
        
        JPanel pnMain, pnLog;
        
        pnMain = new JPanel();
        dialog.add(pnMain, BorderLayout.CENTER);
        pnMain.setLayout(new BorderLayout());
        
        toolbar = new JToolBar();
        pnMain.add(toolbar, BorderLayout.NORTH);
        
        jsplitText = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        pnMain.add(jsplitText, BorderLayout.CENTER);
        
        taBefore = new JTextArea();
        taAfter  = new JTextArea();
        taAfter.setEditable(false);
        taLog    = new JTextArea();
        taLog.setEditable(false);
        
        pnLog = new JPanel();
        pnLog.setLayout(new BorderLayout());
        
        pnMain.add(pnLog, BorderLayout.SOUTH);
        pnLog.add(new JScrollPane(taLog), BorderLayout.CENTER);
        
        jsplitText.setTopComponent(taBefore);
        jsplitText.setBottomComponent(taAfter);
        
        btnRun = new JButton("▶");
        tfParam = new JTextField(20);
        cbConverter = new JComboBox<TextConverter>();
        
        loadConverter();
        
        toolbar.add(cbConverter);
        toolbar.add(tfParam);
        toolbar.add(btnRun);
        
        btnRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        invokeConvert();
                    }
                });
            }
        });
    }

    @Override
    public void onBeforeOpened(SampleJavaCodes superInstance) {
        
    }

    @Override
    public void onAfterOpened(SampleJavaCodes superInstance) {
        jsplitText.setDividerLocation(0.6);
    }

    @Override
    public String getTitle() {
        return "텍스트 변환 도구";
    }

    @Override
    public void alert(String msg) {
        JOptionPane.showMessageDialog(dialog, msg);
    }

    @Override
    public String getName() {
        return "textconv";
    }
    
    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public JDialog getDialog() {
        return dialog;
    }

    @Override
    public void dispose() {
        if(dialog != null) dialog.setVisible(false);
        dialog = null;
    }
    
    @Override
    public boolean isHidden() {return true;}
}
