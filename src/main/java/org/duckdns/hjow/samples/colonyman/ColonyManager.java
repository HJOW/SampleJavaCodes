package org.duckdns.hjow.samples.colonyman;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.samples.base.SampleJavaCodes;
import org.duckdns.hjow.samples.colonyman.elements.AttackableObject;
import org.duckdns.hjow.samples.colonyman.elements.City;
import org.duckdns.hjow.samples.colonyman.elements.CityPanel;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyElements;
import org.duckdns.hjow.samples.colonyman.elements.ColonyPanel;
import org.duckdns.hjow.samples.util.ResourceUtil;

/** Colonization 프로그램 */
public class ColonyManager implements ColonyManagerUI, Disposeable, Serializable {
    private static final long serialVersionUID = -5740844908011980260L;
    protected transient SampleJavaCodes superInstance;
    protected transient Thread thread;
    protected transient volatile boolean threadSwitch, threadPaused, threadShutdown, reserveSaving, reserveRefresh;
    protected transient volatile boolean bCheckerPauseCompleted = false;
    
    protected transient volatile Vector<Colony> colonies = new Vector<Colony>();
    protected transient volatile int  selectedColony = -1;
    protected transient volatile int  cycle = 0;
    protected transient volatile long cycleGap = 99L;
    protected transient volatile long cycleRunningTime = 0L;
    
    protected transient volatile boolean flagSaveBeforeClose = true; // 종료 시 저장 플래그
    protected transient volatile boolean flagAlreadyDisposed = false;
    
    public ColonyManager(SampleJavaCodes superInstance) {
        super();
        this.superInstance = superInstance;
        
        threadSwitch = false;
        threadPaused = true;
        threadShutdown = true;
        reserveSaving = false;
        reserveRefresh = false;
        flagAlreadyDisposed = false;
        
        init(superInstance);
    }

    public void init(SampleJavaCodes superInstance) {
        refreshColonyContent();
    }

    public ColonyManager getSelf() { return this; }

    /** 메인 쓰레드 구동 중인지 확인하여, 미구동 중인 경우 구동 시작 */
    public void assureMainThreadRunning() {
        if(thread == null || (! threadSwitch)) turnOnMainThread();
    }
    
    /** 메인 쓰레드 실행 */
    protected void turnOnMainThread() {
        if(thread != null) {
            thread.interrupt();
            try { Thread.sleep(1000L); } catch(InterruptedException ex) { GlobalLogs.processExceptionOccured(ex, false); }
        }
        thread = new Thread(new Runnable() {    
            @Override
            public void run() {
                while(threadSwitch) {
                    if(! onMainThread()) break;
                }
                threadShutdown = true;
            }
        });
        threadSwitch   = true;
        threadShutdown = false;
        flagSaveBeforeClose = true;
        thread.start();
    }
    
    /** 메인 쓰레드 동작 */
    protected boolean onMainThread() {
        threadShutdown = false;
        long elapsed = System.currentTimeMillis();
        long gap = cycleGap;
        
        // 쓰레드에서 수행할 실질 작업 수행
        try { if(! threadPaused) { bCheckerPauseCompleted = false; oneCycle(); } } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        
        // 저장 요청 수행
        if(reserveSaving) { try { saveColonies(); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); } reserveSaving = false; }
        
        // 일시정지 후 쓰레드가 실제 정지 중인지 판단하는 플래그
        if(threadPaused) bCheckerPauseCompleted = true;
        else bCheckerPauseCompleted = false;
        
        // 쓰레드 Sleep
        try { Thread.sleep(gap); } catch(InterruptedException e) { threadSwitch = false; return false; }

        cycleRunningTime = System.currentTimeMillis() - elapsed - gap;
        threadShutdown = false;
        
        return true;
    }
    
    /** 정착지 세이브 파일 필터 생성 */
    public FileFilter getColonyFileFilter() {
        return new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.isDirectory()) return false;
                String nameLower = pathname.getName().toLowerCase();
                return nameLower.endsWith(".colony") || nameLower.endsWith(".colgz");
            }
        };
    }
    
    /** 정착지 세이브 기본 경로 반환 */
    public File getColonySaveRootDirectory() {
        return ResourceUtil.getHomeDir("samplejavacodes", "colony");
    }
    
    /** 정착지들을 기본 경로에서 불러오기 */
    public void loadColonies() {
        File root = getColonySaveRootDirectory();
        File[] lists = root.listFiles(getColonyFileFilter());
        
        colonies.clear();
        for(File f : lists) {
            if(getColonyFileFilter().accept(f)) loadColony(f, false);
        }
        
        if(colonies.isEmpty()) {
            newColony();
        } else {
            refreshColonyList();
        }
    }
    
    /** 정착지 모두 포기, 초기화 */
    protected void resetAllColony() {
        colonies.clear();
        File root = getColonySaveRootDirectory();
        File[] lists = root.listFiles(getColonyFileFilter());
        for(File f : lists) {
            f.delete();
        }
        newColony();
    }
    
    /** 정착지를 별도 파일에서 불러오기 (화면에는 반영하지 않으므로, 사용 후 refreshColonyList 호출 필요) */
    public void loadColony(File f, boolean alert) {
        boolean exists = false;
        try { 
            Colony c = new Colony(f);
            exists = false;
            for(Colony cx : colonies) { if(c.getName().equals(cx.getName())) exists = true; break; }
            if(exists) return;
            
            c.setOriginalFileName(f.getName());
            colonies.add(c); 
        } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); if(alert) alert("오류 : " + ex.getMessage()); }
    }
    
    /** 정착지들을 기본 경로에 저장 */
    public void saveColonies() {
        File root = ResourceUtil.getHomeDir("samplejavacodes", "colony");
        
        // 백업 준비
        SimpleDateFormat format8 = new SimpleDateFormat("yyyyMMdd");
        int no = 1;
        String date8 = format8.format(new Date(System.currentTimeMillis()));
        String dirName = "backup" + date8 + "_" + no;
        
        // 백업 디렉토리 생성
        File dir = new File(root.getAbsolutePath() + File.separator + dirName);
        while(dir.exists()) {
            no++;
            dirName = "backup" + date8 + "_" + no;
            dir = new File(root.getAbsolutePath() + File.separator + dirName);
        }
        dir.mkdirs();
        
        // 백업
        File[] lists = root.listFiles(getColonyFileFilter());
        for(File f : lists) {
            File newPath = new File(dir.getAbsolutePath() + File.separator + f.getName());
            f.renameTo(newPath); // 파일 이동
        }
        
        // 저장
        for(Colony c : colonies) {
            String name = c.getOriginalFileName();
            if(name == null) name = "col_" + c.getKey() + ".colony";
            
            File colFile = new File(root.getAbsolutePath() + File.separator + name);
            saveColony(c, colFile, false);
        }
        
        // 임시 백업 삭제
        if(dir.exists()) {
            lists = dir.listFiles();
            for(File f : lists) {
                if(f.isDirectory()) continue;
                f.delete();
            }
            if(dir.listFiles().length <= 0) dir.delete();
        }
    }
    
    /** 해당 정착지를 별도 파일로 저장 */
    public void saveColony(Colony c, File f, boolean alert) {
        try { 
            String nameLower = f.getName().toLowerCase().trim();
            if(! ( nameLower.endsWith(".colony") || nameLower.endsWith(".colgz") )) f = new File(f.getAbsolutePath() + ".colony");
            
            c.save(f); 
        } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, true); if(alert) { alert("오류 : " + ex.getMessage()); } else { throw new RuntimeException(ex.getMessage()); } }
    }
    
    /** 새 정착지 생성 */
    public Colony newColony() {
        Colony newCol = new Colony();
        newCol.newCity();
        
        colonies.add(newCol);
        refreshColonyList();
        
        return newCol;
    }
    
    /** 정착지 추가 */
    public void addColony(Colony col) {
        for(Colony c : colonies) {
            if(c.getKey() == col.getKey()) return;
        }
        colonies.add(col);
        refreshColonyList();
    }

    public void log(String msg) {
        System.out.println(msg);
        GlobalLogs.log(msg);
    }
    
    protected void waitThreadShutdown() {
        threadSwitch = false;
        int prevInfinites = 0;
        while(true) {
            if(threadShutdown) break;
            try { Thread.sleep(100L); } catch(Exception ex) {  }
            
            prevInfinites++;
            if(prevInfinites >= 100000) break;
        }
    }

    @Override
    public void dispose() {
        dispose(true);
        flagAlreadyDisposed = true;
    }
    
    public void dispose(boolean closeDialog) {
        disposeContents();
        
        colonies.clear();
        
        if(dialogGlobalLog != null) {
            dialogGlobalLog.dispose();
            dialogGlobalLog = null;
        }
    }
    
    public void disposeContents() {
        threadSwitch = false;
        waitThreadShutdown();
        if((! flagAlreadyDisposed) && (! colonies.isEmpty())) saveColonies();
    }
    
    public void alert(String msg) {
        GlobalLogs.log(msg);
    }
    
    /** 현재 선택된 정착지 반환 */
    public Colony getSelectedColony() {
        if(selectedColony < 0) return null;
        if(selectedColony >= colonies.size()) { selectedColony = 0; return null; }
        return colonies.get(selectedColony);
    }
    
    /** 현재 선택된 정착지 반환 */
    public Colony getColony() {
        return getSelectedColony();
    }
    
    /** 해당 키를 갖는 정착지 찾아 반환 (목록에 없으면 null 반환) */
    @Override
    public Colony getColony(long colonyKey) {
        for(Colony c : colonies) {
            if(c.getKey() == colonyKey) return c;
        }
        return null;
    }
    
    /** 시뮬레이션 시작/정지 토글 */
    public void toggleSimulationRunning() {
        threadPaused = (! threadPaused);
        if(threadPaused) {
            pauseSimulation();
        } else {
            resumeSimulation();
        }
    }
    
    public void pauseSimulation() {
        threadPaused = true;
    }
    
    public void resumeSimulation() {
        threadPaused = false;
        reserveSaving = true;
        
        // 쓰레드가 완전히 종료될 때까지 대기
        try {
            int prefInfLoop = 10;
            while(! bCheckerPauseCompleted) {
                Thread.sleep(1000L);
                prefInfLoop--;
                if(prefInfLoop <= 0) break;
            }
        } catch(InterruptedException ex) { GlobalLogs.processExceptionOccured(ex, false); }
    }
    
    /** 쓰레드에서 1 사이클 당 1회 호출됨 */
    public void oneCycle() {
        Colony col = getSelectedColony();
        if(col == null) return;
        
        try { col.oneCycle(cycle, null, col, 100, getColonyPanel(col)); } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        try {
        	refreshArenaPanel(cycle);
        } catch(Exception ex) { GlobalLogs.processExceptionOccured(ex, false); }
        
        cycle++;
        if(cycle >= Integer.MAX_VALUE - 10) cycle = 0;
    }
    
    /** 화면 새로고침 예약 */
    public void reserveRefresh() {
        reserveRefresh = true;
    }
    
    /** 정착지 목록과 화면 내용 갱신 */
    public void refreshColonyList() {
    	if(colonies.size() >= 1 && selectedColony < 0) selectedColony = 0;
        refreshColonyContent();
    }
    
    /** 정착지 화면 내용 갱신 */
    public void refreshColonyContent() {
        assureMainThreadRunning();
        refreshArenaPanel(0);
    }
    
    /** 사이클 진행에 따른 정착지 화면 내용 갱신 (성능을 위해 항상 전체를 새로고침하지는 않음. 확실히 새로고침하려면 refreshColonyContent 메소드 사용) */
    public void refreshArenaPanel(int cycle) { }
    
    /** 백업 복원 받기 */
    public void applyRestore(List<Colony> colonies, BackupManager backupMan, boolean concat) {
        // 파일 다 지워야 함
        File root = getColonySaveRootDirectory();
        File[] lists = root.listFiles(getColonyFileFilter());
        for(File f : lists) {
            f.delete();
        }
        
        // 복원 처리
        if(concat) { // 병합
            List<Colony> temp = new ArrayList<Colony>();
            temp.addAll(this.colonies);
            
            this.colonies.clear();
            
            for(Colony c : temp) {
                boolean dupl = false;
                for(Colony alreadyIn : this.colonies) {
                    if(c.getKey() == alreadyIn.getKey()) { dupl = true; break; }
                }
                if(dupl) continue;
                this.colonies.add(c);
            }
            
            for(Colony c : colonies) {
                boolean dupl = false;
                for(Colony alreadyIn : this.colonies) {
                    if(c.getKey() == alreadyIn.getKey()) { dupl = true; break; }
                }
                if(dupl) continue;
                this.colonies.add(c);
            }
        } else { // 대체
            this.colonies.clear();
            this.colonies.addAll(colonies);
        }
        
        reserveSaving = true; // 저장 예약
        refreshColonyList();  // 목록 갱신
    }
    
    /** 해당 정착지를 출력하는 영역 반환 */
    public ColonyPanel getColonyPanel(Colony col) {
        return null;
    }
    
    /** 해당 도시를 출력하는 도시 영역 반환 */
    public CityPanel getCityPanel(City city) {
        Colony col = getSelectedColony();
        ColonyPanel colPn = getColonyPanel(col);
        if(colPn == null) return null;
        return colPn.getCityPanel(city);
    }
    
    /** 도시가 속한 정착지 찾기 */
    @Override
    public Colony getColonyFrom(City city) {
        for(Colony c : colonies) {
            for(City ct : c.getCities()) {
                if(ct.getKey() == city.getKey()) return c;
            }
        }
        return null;
    }
    
    /** 각 요소들을 위한 고유키 생성 (절대 0이 나오지 않음) */
    public static long generateKey() {
        Random rd = new Random();
        long key = rd.nextLong();
        while(key == 0L) { key = rd.nextLong(); }
        return key;
    }
    
    /** 각 요소들의 고유 이름을 위한 자연수 반환 */
    public static int generateNaturalNumber() {
        return Math.abs(new Random().nextInt());
    }

    public static boolean isDebugModeEnabled() {
        return flagDebugMode;
    }

    /** 전역 로그 출력 */
    public static void logGlobals(String msg) {
        System.err.println(msg);
        if(dialogGlobalLog != null) dialogGlobalLog.log(msg);
    }
    
    /** 공격자의 대미지에 추가 연산 (랜덤성 부여, 속성 및 방어력, 상태 적용) */
    public static int naturalizeDamage(AttackableObject attacker, ColonyElements target, int damage) {
        if(damage < 0) return damage; // 대미지가 음수인 경우 그대로 반환
        double correctRate = 1.0; // 명중률
        int    defType = target.getDefenceType();
        int    atkType = attacker.getAttackType();
        
        // 속성 적용
        if(defType == DEFENCETYPE_SMALL) {
            if(atkType == ATTACKTYPE_THICK_BULLET ) { damage = (int) Math.round( damage / 2.0 ); correctRate = correctRate * 0.75; }
            if(atkType == ATTACKTYPE_THICK_MISSILE) { damage = (int) Math.round( damage / 2.0 ); }
            if(atkType == ATTACKTYPE_THICK_RAY    ) { correctRate = correctRate * 0.75; }
            if(atkType == ATTACKTYPE_THICK_ENERGY ) { correctRate = correctRate * 0.25; }
        }
        
        if(defType == DEFENCETYPE_BUILDING) {
            if(atkType >= 1 && atkType <= 9) damage = (int) Math.round( damage / 2.0 );
        }
        
        // 랜덤성 적용
        //    랜덤값 생성
        double naturalRandom = Math.round(damage * 0.2);
        naturalRandom = ((naturalRandom / 2.0) * Math.random()) * 2.0;
        naturalRandom = naturalRandom - (naturalRandom / 2.0); // 반수는 음수로 가도록
        
        //    속성에 따라 랜덤값 추가 변동
        if(atkType == ATTACKTYPE_THICK_MISSILE) naturalRandom = naturalRandom * 2;
        if(atkType == ATTACKTYPE_THICK_ENERGY && defType == DEFENCETYPE_SMALL) naturalRandom = naturalRandom * 4;
        if(atkType == ATTACKTYPE_THIN_ENERGY  && defType == DEFENCETYPE_SMALL) naturalRandom = naturalRandom * 2;
        
        //     랜덤값 대미지에 적용
        damage = damage + (int) Math.round(naturalRandom);
        
        // 방어력 적용
        damage = damage - target.getDefencePoint();
        if(damage < 1) damage = 1;
        
        // 명중률 적용
        if(Math.random() > correctRate) return 0; // 명중률이므로, 명중률에 벗어나야 0 리턴, 부등호 방향 주의 !
        return damage;
    }
    
    public static final short ATTACKTYPE_NORMAL = 0;
    public static final short ATTACKTYPE_THIN_BULLET = 1;
    public static final short ATTACKTYPE_THIN_RAY    = 2;
    public static final short ATTACKTYPE_THIN_ENERGY = 3;
    public static final short ATTACKTYPE_THICK_BULLET  = 11;
    public static final short ATTACKTYPE_THICK_RAY     = 12;
    public static final short ATTACKTYPE_THICK_ENERGY  = 13;
    public static final short ATTACKTYPE_THICK_MISSILE = 14;
    public static final short DEFENCETYPE_NORMAL   = 0;
    public static final short DEFENCETYPE_SMALL    = 1;
    public static final short DEFENCETYPE_BUILDING = 9;
    
    protected static transient GlobalLogDialog dialogGlobalLog;
    protected static transient boolean flagDebugMode = false; // 실행 시간 표시 플래그
}