package org.duckdns.hjow.samples.colonyman.elements;

public class NormalColony extends Colony {
    private static final long serialVersionUID = -5381698598742715021L;
    
    public NormalColony() {
        super();
    }
    
    public String getType() {
        return NormalColony.getColonyClassName();
    }
    
    public static String getColonyClassName() {
        return "NormalColony";
    }
    
    public static String getColonyClassTitle() {
        return "일반 정착지 시나리오";
    }
    
    public static String getColonyClassDescription() {
        return "일반 정착지 시나리오";
    }
}
