package org.duckdns.hjow.samples.colonyman.elements.research;

import java.util.List;

import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class MilitaryTech extends Research {
    private static final long serialVersionUID = -6913431604370242959L;

    public MilitaryTech() { super(); }
    
    @Override
    public String getName() {
        return "MilitaryTech";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 800L; }
    public double getMaxProgressIncreaseRate() { return 1.5;  }

    @Override
    public boolean isResearchAvail(Colony col) {
        boolean cond1 = false;
        boolean cond2 = false;
        
        List<Research> researches = col.getResearches();
        for(Research one : researches) {
            
            // 기초과학 레벨이 이 군사학 레벨의 2배가 되어야 연구가능
            if(one instanceof BasicScience) {
                if(one.getLevel() >= getLevel() * 2) cond1 = true;
            }
            
            if(getLevel() >= 20) { // 레벨 20부터
                // 기초인문학 레벨이 이 군사학 레벨보다 높아야 연구가능
                if(one instanceof BasicHumanities) {
                    if(one.getLevel() > getLevel()) cond2 = true;
                }
            }
        }
        
        return cond1 && cond2;
    }

    @Override
    public String getTitle() {
        return "군사학";
    }
}
