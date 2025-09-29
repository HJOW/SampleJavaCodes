package org.duckdns.hjow.samples.colonyman;

import java.util.List;
import java.util.Vector;

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
}
