package org.duckdns.hjow.samples.colonyman.elements.research;

import java.util.List;

import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class BasicEngineering extends Research {
    private static final long serialVersionUID = -2727850120481565932L;

    public BasicEngineering() { super(); }

    @Override
    public String getName() {
        return "BasicEngineering";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 900L; }
    public double getMaxProgressIncreaseRate() { return 1.5;  }

    @Override
    public boolean isResearchAvail(Colony col) {
        boolean cond1 = false;
        boolean cond2 = false;
        boolean cond3 = false;
        
        List<Research> researches = col.getResearches();
        for(Research one : researches) {
            
            // 기초과학 레벨이 이 공학 레벨의 2배가 되어야 연구가능
            if(one instanceof BasicScience) {
                if(one.getLevel() >= (int)(getLevel() * 2)) cond1 = true;
            }
            
            if(getLevel() >= 30) { // 레벨 30부터
                // 기초인문학 레벨이 이 공학 레벨만큼 되어야 연구가능
                if(one instanceof BasicHumanities) {
                    if(one.getLevel() >= getLevel()) cond3 = true;
                }
            }
        }
        
        return cond1 && cond2 && cond3;
    }

    @Override
    public String getTitle() {
        return "공학기초";
    }
}
