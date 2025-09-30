package org.duckdns.hjow.samples.colonyman;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;
import org.duckdns.hjow.commons.util.FileUtil;
import org.duckdns.hjow.samples.colonyman.elements.Colony;
import org.duckdns.hjow.samples.colonyman.elements.ColonyInformation;
import org.duckdns.hjow.samples.colonyman.elements.NormalColony;
import org.duckdns.hjow.samples.colonyman.elements.facilities.Arcade;
import org.duckdns.hjow.samples.colonyman.elements.facilities.ArchitectOffice;
import org.duckdns.hjow.samples.colonyman.elements.facilities.BusStation;
import org.duckdns.hjow.samples.colonyman.elements.facilities.Factory;
import org.duckdns.hjow.samples.colonyman.elements.facilities.PowerStation;
import org.duckdns.hjow.samples.colonyman.elements.facilities.ResearchCenter;
import org.duckdns.hjow.samples.colonyman.elements.facilities.ResidenceModule;
import org.duckdns.hjow.samples.colonyman.elements.facilities.Restaurant;
import org.duckdns.hjow.samples.colonyman.elements.facilities.TownHouse;
import org.duckdns.hjow.samples.colonyman.elements.facilities.Turret;
import org.duckdns.hjow.samples.colonyman.elements.research.BasicBiology;
import org.duckdns.hjow.samples.colonyman.elements.research.BasicBuildingTech;
import org.duckdns.hjow.samples.colonyman.elements.research.BasicEngineering;
import org.duckdns.hjow.samples.colonyman.elements.research.BasicHumanities;
import org.duckdns.hjow.samples.colonyman.elements.research.BasicMedicalScience;
import org.duckdns.hjow.samples.colonyman.elements.research.BasicScience;
import org.duckdns.hjow.samples.colonyman.elements.research.MilitaryTech;
import org.duckdns.hjow.samples.colonyman.elements.states.ImmuneInfluenza;
import org.duckdns.hjow.samples.colonyman.elements.states.Influenza;
import org.duckdns.hjow.samples.colonyman.elements.states.SuperAngry;

/** 정착지 시나리오, 시설, 연구, 시설과 시민의 상태 타입 등 클래스들과 타입 리스트를 관리하는 클래스 */
public class ColonyClassLoader {
    /** 정착지 시나리오 정보들을 반환, 타입명과 제목, 설명, 클래스가 포함 */
    public static List<ColonyInformation> colonyInfos() {
        List<ColonyInformation> infos = new Vector<ColonyInformation>();
        
        for(Class<?> classOne : colonyClasses()) {
            ColonyInformation info = new ColonyInformation();
            
            try {
                Method method = classOne.getMethod("getColonyClassName");
                info.setName((String) method.invoke(null));
                
                method = classOne.getMethod("getColonyClassTitle");
                info.setTitle((String) method.invoke(null));
                
                method = classOne.getMethod("getColonyClassDescription");
                info.setDescription((String) method.invoke(null));
                
                info.setColonyClass(classOne);
                if(! infos.contains(info)) infos.add(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
        
        return infos;
    }
    
    /** 정착지 시나리오 클래스들을 반환 */
    public static List<Class<?>> colonyClasses() {
        List<Class<?>> classes = new Vector<Class<?>>();
        classes.add(NormalColony.class);
        return classes;
    }
    
    /** 타입을 받아 그에 맞는 새 정착지 객체 생성, 해당 클래스 정보가 없으면 null 반환 */
    public static Colony newColonyInstance(String typeOrClass) {
        try {
            for(ColonyInformation info : colonyInfos()) {
                if(info.getName().equals(typeOrClass)) {
                    Colony col = (Colony) info.getColonyClass().newInstance();
                    return col;
                }
            }
            for(ColonyInformation info : colonyInfos()) {
                if(info.getColonyClass().getName().equals(typeOrClass) || info.getColonyClass().getSimpleName().equals(typeOrClass)) {
                    Colony col = (Colony) info.getColonyClass().newInstance();
                    return col;
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    /** 파일로부터 정착지 객체 읽어 반환 */
    public static Colony loadColony(File f) throws Exception {
        String fileName = f.getName().toLowerCase();
        String strJson;
        
        if(fileName.endsWith(".colgz")) {
            strJson = FileUtil.readString(f, "UTF-8", GZIPInputStream.class);
        } else {
            strJson = FileUtil.readString(f, "UTF-8");
        }
        
        JsonObject json = (JsonObject) JsonObject.parseJson(strJson);
        return loadColony(json);
    }
    
    /**  JSON 으로부터 정착지 객체 읽어 반환 */
    public static Colony loadColony(JsonObject json) throws Exception {
        String type = json.get("type").toString();
        
        for(ColonyInformation info : colonyInfos()) {
            if(info.getName().equals(type)) {
                Colony col = (Colony) info.getColonyClass().newInstance();
                if(col == null) continue;
                
                col.fromJson(json);
                return col;
            }
        }
        return null;
    }
    
    /** 시설 클래스 목록 반환 */
	public static List<Class<?>> facilityClasses() {
		List<Class<?>> classes = new Vector<Class<?>>();
		classes.add(ResidenceModule.class);
        classes.add(PowerStation.class);
        classes.add(Restaurant.class);
        classes.add(Arcade.class);
        classes.add(Factory.class);
        classes.add(ResearchCenter.class);
        classes.add(ArchitectOffice.class);
        classes.add(BusStation.class);
        classes.add(Turret.class);
        classes.add(TownHouse.class);
		return classes;
	}
	
    /** 연구 클래스 목록 반환 */
	public static List<Class<?>> researchClasses() {
		List<Class<?>> classes = new Vector<Class<?>>();
		classes.add(BasicScience.class);
        classes.add(BasicHumanities.class);
        classes.add(MilitaryTech.class);
        classes.add(BasicBuildingTech.class);
        classes.add(BasicBiology.class);
        classes.add(BasicMedicalScience.class);
        classes.add(BasicEngineering.class);
        return classes;
	}
	
    /** 적 클래스 목록 반환 */
	public static List<Class<?>> enemyClasses() {
        List<Class<?>> classes = new Vector<Class<?>>();
		return classes;
	}
	
    /** 상태 클래스 목록 반환 */
	public static List<Class<?>> stateClasses() {
		List<Class<?>> classes = new Vector<Class<?>>();
		classes.add(Influenza.class);
        classes.add(ImmuneInfluenza.class);
        classes.add(SuperAngry.class);
		return classes;
	}
	
    /** 기본 공지사항 컨텐츠 html 반환 (웹 접근 못했을 시 이 내용 출력) */
	public static String htmlNoticeEmpty() {
		StringBuilder res = new StringBuilder("");
		
		res = res.append("\n").append("<html>                                                                                                                                                                            ");
		res = res.append("\n").append("<head>                                                                                                                                                                            ");
		res = res.append("\n").append("<title>Notice</title>                                                                                                                                                             ");
		res = res.append("\n").append("</head>                                                                                                                                                                           ");
		res = res.append("\n").append("<body style='margin-left: 0; margin-right: 0; margin-top: 0; margin-bottom: 0; background-color: #EEEEEE;'>                                                                       ");
		res = res.append("\n").append("    <div style='padding-left: 30px; padding-top: 30px; font-size: 30px; font-family: NanumGothic, \"나눔고딕\", \"Nanum Gothic\", NanumGothicCoding, \"나눔고딕코딩\", \"Nanum Gothic Coding\", Arial, Consolas, \"돋움체\";'></div>");
		res = res.append("\n").append("</body>                                                                                                                                                                           ");
		res = res.append("\n").append("</html>                                                                                                                                                                           ");
		
		return res.toString().trim();
	}
	
    /** 공지사항 웹 URL 반환 */
	public static String htmlNoticeUrl() {
		return "http://hjow.duckdns.org/colonization/notice_ko.html";
	}
	
    /** 공통 설정 URL 반환 (이 안에서 최신 버전 코드와 추가 컨텐츠 정보 등을 얻게 됨) */
	public static String htmlConfigJsonUrl() {
		return "http://hjow.duckdns.org/colonization/content.json";
	}
	
    /** 공통 설정 정보 조회 */
	public static void loadWebConfigs(ColonyManager man) {
		InputStream       inp1 = null;
		InputStreamReader inp2 = null;
		BufferedReader    inp3 = null;
		try {
			StringBuilder str = new StringBuilder("");
			String line;
			
			URL url = new URL(htmlConfigJsonUrl());
			inp1 = url.openStream();
			inp2 = new InputStreamReader(inp1, "UTF-8");
			inp3 = new BufferedReader(inp2);
			
			while(true) {
				line = inp3.readLine();
				if(line == null) break;
				str = str.append("\n").append(line);
			}
			
			inp3.close(); inp3 = null;
			inp2.close(); inp2 = null;
			inp1.close(); inp1 = null;
			
			JsonObject json = (JsonObject) JsonObject.parseJson(str.toString().trim());
			str = null;
			
			json = (JsonObject) json.get("swing");
			
			applyWebConfigs(json, man);
		} catch(Exception ex) {
			GlobalLogs.processExceptionOccured(ex, true);
		} finally {
			ClassUtil.closeAll(inp3, inp2, inp1);
		}
	}
	
    /** 공통 설정 정보 적용 */
	protected static void applyWebConfigs(JsonObject json, ColonyManager man) throws Exception {
		// TODO
	}
}
