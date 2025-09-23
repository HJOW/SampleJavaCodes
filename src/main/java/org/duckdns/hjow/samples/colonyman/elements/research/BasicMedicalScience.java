package org.duckdns.hjow.samples.colonyman.elements.research;

import java.util.List;

import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class BasicMedicalScience extends Research {
    private static final long serialVersionUID = 6925527948145856095L;

    public BasicMedicalScience() { super(); }

    @Override
    public String getName() {
        return "BasicMedicalScience";
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
            
            // 기초과학 레벨이 이 의학 레벨의 3배가 되어야 연구가능
            if(one instanceof BasicScience) {
                if(one.getLevel() >= (int)(chooseMaxInt(getLevel(), 1) * 3)) cond1 = true;
            }
            
            // 기초생물학 레벨이 이 의학 레벨의 2배가 되어야 연구가능
            if(one instanceof BasicScience) {
                if(one.getLevel() >= (int)(chooseMaxInt(getLevel(), 1) * 2)) cond2 = true;
            }
            
            // 기초인문학 레벨이 이 의학 레벨만큼 되어야 연구가능
            if(one instanceof BasicScience) {
                if(one.getLevel() >= chooseMaxInt(getLevel(), 1)) cond3 = true;
            }
        }
        
        return cond1 && cond2 && cond3;
    }

    @Override
    public String getTitle() {
        return "기초의학";
    }
}
