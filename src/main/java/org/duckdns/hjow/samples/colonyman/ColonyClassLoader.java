package org.duckdns.hjow.samples.colonyman;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.ClassUtil;
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

public class ColonyClassLoader {
	
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
	
	public static List<Class<?>> enemyClasses() {
        List<Class<?>> classes = new Vector<Class<?>>();
		return classes;
	}
	
	public static List<Class<?>> stateClasses() {
		List<Class<?>> classes = new Vector<Class<?>>();
		classes.add(Influenza.class);
        classes.add(ImmuneInfluenza.class);
        classes.add(SuperAngry.class);
		return classes;
	}
	
	public static String htmlNoticeEmpty() {
		StringBuilder res = new StringBuilder("");
		
		res = res.append("\n").append("<html>                                                                                                                                                                            ");
		res = res.append("\n").append("<head>                                                                                                                                                                            ");
		res = res.append("\n").append("<title>Notice</title>                                                                                                                                                             ");
		res = res.append("\n").append("<style type=\"text/css\">                                                                                                                                                         ");
		res = res.append("\n").append("body { margin: 0; padding: 0; background-color: #424242; font-family: NanumGothic, \"나눔고딕\", \"Nanum Gothic\", NanumGothicCoding, \"나눔고딕코딩\", \"Nanum Gothic Coding\" } ");
		res = res.append("\n").append("</style>                                                                                                                                                                          ");
		res = res.append("\n").append("</head>                                                                                                                                                                           ");
		res = res.append("\n").append("<body>                                                                                                                                                                            ");
		res = res.append("\n").append("        123                                                                                                                                                                       ");
		res = res.append("\n").append("</body>                                                                                                                                                                           ");
		res = res.append("\n").append("</html>                                                                                                                                                                           ");
		
		return res.toString().trim();
	}
	
	public static String htmlNoticeUrl() {
		return "http://hjow.duckdns.org/colonization/notice_ko.html";
	}
	
	public static String htmlConfigJsonUrl() {
		return "http://hjow.duckdns.org/colonization/content.json";
	}
	
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
	
	protected static void applyWebConfigs(JsonObject json, ColonyManager man) throws Exception {
		// TODO
	}
}
