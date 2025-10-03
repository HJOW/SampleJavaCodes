package org.duckdns.hjow.samples.base;

import javax.swing.JOptionPane;

public abstract class DefaultProgram implements GUIProgram {
    private static final long serialVersionUID = -8751781027069806392L;
    
    protected SampleJavaCodes superInstance;
    public DefaultProgram(SampleJavaCodes superInstance) {
        this.superInstance = superInstance;
        init(superInstance);
    }
    
    @Override
    public void init(SampleJavaCodes superDialog) { }
    
    @Override
    public void onBeforeOpened(SampleJavaCodes superInstance) { }
    
    @Override
    public void onAfterOpened(SampleJavaCodes superInstance) { }
    
    @Override
    public String getTitle() { return getName(); }
    
    @Override
    public void log(String msg) { System.out.println(msg); };
    
    @Override
    public void alert(String msg) { System.out.println(msg); if(getDialog() != null) JOptionPane.showMessageDialog(getDialog(), msg); };
    
    @Override
    public void open(SampleJavaCodes superInstance) {
        onBeforeOpened(superInstance);
        getDialog().setVisible(true);
    }
    
    @Override
    public void dispose() {}
    
    @Override
    public boolean isHidden() {return false;}
}
