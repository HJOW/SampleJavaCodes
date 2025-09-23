package org.duckdns.hjow.samples.colonyman.elements.facilities;

import org.duckdns.hjow.samples.colonyman.ColonyManager;

public class ResidenceModule extends Residence {
    private static final long serialVersionUID = -4063295537669464654L;
    public ResidenceModule() { }
    
    protected String defaultName() {
        return "보급형_주거모듈_" + ColonyManager.generateNaturalNumber();
    }
    
    @Override
    public String getType() {
        return "ResidenceModule";
    }
}
