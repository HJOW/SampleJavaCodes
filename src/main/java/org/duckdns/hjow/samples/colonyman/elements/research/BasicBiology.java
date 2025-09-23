package org.duckdns.hjow.samples.colonyman.elements.research;

import java.util.List;

import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class BasicBiology extends Research {
    private static final long serialVersionUID = -231922131243240067L;

    public BasicBiology() { super(); }

    @Override
    public String getName() {
        return "BasicBiology";
    }
    
    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    public long   getMaxProgressStarts()       { return 600L; }
    public double getMaxProgressIncreaseRate() { return 1.5;  }

    @Override
    public boolean isResearchAvail(Colony col) {
        boolean cond1 = false;
        boolean cond2 = false;
        
        List<Research> researches = col.getResearches();
        for(Research one : researches) {
            
            // 기초과학 레벨이 이 생물학 레벨의 1.2배가 되어야 연구가능
            if(one instanceof BasicScience) {
                if(one.getLevel() >= (int)(chooseMaxInt(getLevel(), 1) * 1.2)) cond1 = true;
            }
            
            // 기초인문학 레벨이 이 생물학 레벨만큼 되어야 연구가능
            if(one instanceof BasicScience) {
                if(one.getLevel() >= chooseMaxInt(getLevel(), 1)) cond2 = true;
            }
        }
        
        return cond1 && cond2;
    }

    @Override
    public String getTitle() {
        return "기초생물학";
    }
}
