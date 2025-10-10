package org.duckdns.hjow.samples.consolemenu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.duckdns.hjow.commons.util.ClassUtil;

/** 콘솔 메뉴 동작 구현 */
public class ConsoleMenu {

    public static void main(String[] args) {
        ConsoleMenu menu = new ConsoleMenu();
        menu.addChoice(new Choice() {
            private static final long serialVersionUID = 7256035422086481330L;

            @Override
            public String getText() {
                return "Hello";
            }
            
            @Override
            public void action() throws Throwable {
                System.out.println("WOW");
            }
        });
        menu.setEndOperationExit(true);
        menu.launch();
    }

    protected String title = "Main Menu";
    protected List<Choice> menuList = new ArrayList<Choice>();
    protected Properties stringTable = new Properties();
    protected String charset = null;
    
    protected transient BufferedReader reader = null;
    protected transient boolean switchThread  = false;
    protected transient boolean flagExitOnEnd = false;
    
    public ConsoleMenu() { }
    public ConsoleMenu(List<Choice> menuList) {
        this();
        this.menuList.addAll(menuList);
    }
    
    /** 메뉴 호출 */
    public void launch() {
        switchThread = true;
        
        try {
            if(charset == null) reader = new BufferedReader(new InputStreamReader(System.in));
            else                reader = new BufferedReader(new InputStreamReader(System.in, charset));
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        while(switchThread) {
            try {
                if(menuList.isEmpty()) { switchThread = false; break; }
                
                int idx=0;
                for(idx=0; idx<3; idx++) {
                    logLn();
                }
                
                logLn(t(getTitle()));
                
                List<Choice> displayList = new ArrayList<Choice>();
                displayList.addAll(menuList);
                
                if(flagExitOnEnd) {
                    displayList.add(new Choice() {
                        private static final long serialVersionUID = -1005617447569063744L;
                        @Override
                        public String getText() { return t("Exit"); }
                        
                        @Override
                        public void action() throws Throwable { closeMenu(); }
                    });
                } else {
                    displayList.add(new Choice() {
                        private static final long serialVersionUID = -1005617447569063744L;
                        @Override
                        public String getText() { return t("Back"); }
                        
                        @Override
                        public void action() throws Throwable { closeMenu(); }
                    });
                }
                
                for(idx=0; idx<displayList.size(); idx++) {
                    Choice c = displayList.get(idx);
                    logLn((idx+1) + ". " + t(c.getText()));
                }
                
                log(t("Choice") + " >> ");
                
                String line = reader.readLine();
                if(line == null) line = "";
                
                if(! switchThread) break;
                
                int sel = -1;
                try { sel = Integer.parseInt(line.trim()); } catch(NumberFormatException e) { logLn(t("Please input the correct number !")); continue; }
                
                if(sel <= 0 || sel > displayList.size()) {
                    logLn(t("Please input the correct number !")); continue;
                }
                
                Choice selected = displayList.get(sel-1);
                selected.action();
                
            } catch(Throwable tx) {
                String msg = tx.getMessage();
                if(msg == null) msg = "";
                logLn("Error : (" + tx.getClass().getSimpleName() + ") " + t(msg));
                tx.printStackTrace();
            }
        }
        
        if(flagExitOnEnd) {
            if(reader != null) { ClassUtil.closeAll(reader); reader = null; }
            System.exit(0);
        }
    }
    
    /** 로그 출력 (줄을 띄우지 않음) */
    public void log(Object obj) {
        System.out.print(obj);
    }
    
    /** 로그 출력 */
    public void logLn(Object obj) {
        System.out.println(obj);
    }
    
    /** 로그 출력 (줄바꿈) */
    public void logLn() {
        System.out.println();
    }
    
    /** 메뉴 선택지 목록 반환 */
    public List<Choice> getMenuList() {
        return menuList;
    }
    
    /** 메뉴 선택지 목록 설정 */
    public void setMenuList(List<Choice> menuList) {
        this.menuList = menuList;
    }
    
    /** 메뉴 선택지 추가 */
    public void addChoice(Choice c) {
        menuList.add(c);
    }
    
    /** 메뉴 선택지 갯수 반환 (마지막 항목 제외) */
    public int getChoiceCount() {
        return menuList.size();
    }
    
    /** 메뉴 제목 반환 */
    public String getTitle() {
        return title;
    }
    
    /** 스트링 테이블 (언어 설정) 반환 */
    public Properties getStringTable() {
        return stringTable;
    }
    
    /** 메뉴 제목 설정 */
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getCharset() {
        return charset;
    }
    public void setCharset(String charset) {
        this.charset = charset;
    }
    /** 스트링 테이블 (언어 설정) 설정 */
    public void setStringTable(Properties stringTable) {
        this.stringTable = stringTable;
    }
    
    /** String Table 적용, String Table 에 없는 텍스트이면 원본 그대로 반환 */
    public String t(String originalString) {
        String res = getStringTable().getProperty(originalString);
        if(res == null) return originalString;
        return res;
    }
    /** 메뉴 호출 종료, 단, 이미 메뉴 입력값을 받고 있는 상황이라면 아무 값이나 입력 받아야 종료될 수 있음. */
    public void closeMenu() {
        switchThread = false;
        if(flagExitOnEnd) {
            if(reader != null) { ClassUtil.closeAll(reader); reader = null; }
            System.exit(0);
        }
    }
    /** 이 메뉴의 동작 종료 시 프로그램을 종료할 지 여부를 지정 */
    public void setEndOperationExit(boolean flags) {
        this.flagExitOnEnd = flags;
    } 
}
