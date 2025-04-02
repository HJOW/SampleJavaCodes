package com.hjow.textconvert;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

public class GUITextConverter {
    protected JFrame frame;
    
    protected JToolBar    toolbar;
    protected JSplitPane  jsplitText;
    protected JTextArea   taBefore, taAfter, taLog;
    protected JTextField  tfParam;
    protected JButton     btnRun;
    protected JComboBox<TextConverter> cbConverter;
    
    protected Vector<TextConverter> converters = new Vector<TextConverter>();
    
    public GUITextConverter() {
        frame = new JFrame();
        frame.setSize(790, 590);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("텍스트 변환 도구");
        
        JPanel pnMain, pnLog;
        
        pnMain = new JPanel();
        frame.add(pnMain, BorderLayout.CENTER);
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
        
        loadConverter();
        
        btnRun = new JButton("▶");
        tfParam = new JTextField(20);
        cbConverter = new JComboBox<TextConverter>();
        
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
    
    public void loadConverter() {
        DefaultComboBoxModel<TextConverter> cbModel = new DefaultComboBoxModel<TextConverter>(converters);
        cbConverter.setModel(cbModel);
    }
    
    public void open() {
        frame.setVisible(true);
        jsplitText.setDividerLocation(0.6);
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
    
    public void log(String content) {
        taLog.setText(taLog.getText() + "\n" + content);
        taLog.setCaretPosition(taLog.getDocument().getLength() - 1);
    }
    
    public static void main(String[] args) {
        new GUITextConverter().open();
    }
}
