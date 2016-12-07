package me.BadBones69.envoy.MultiSupport;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.BadBones69.envoy.Main;
import me.BadBones69.envoy.Methods;

public class HolographicSupport {
	
	private static HashMap<Location, Hologram> holos = new HashMap<Location, Hologram>();
	private static Plugin plugin = Bukkit.getPluginManager().getPlugin("CrazyEnvoy");
	
	public static void createHologram(Location loc){
		Hologram hg = HologramsAPI.createHologram(plugin, loc);
		for(String line : Main.settings.getConfig().getStringList("Settings.Hologram")){
			hg.appendTextLine(Methods.color(line));
		}
		holos.put(loc, hg);
	}
	
	public static void removeHologram(Location loc){
		if(holos.containsKey(loc)){
			Hologram hg = holos.get(loc);
			holos.remove(loc);
			hg.delete();
		}
	}
	
	public static void removeAllHolograms(){
		for(Location loc : holos.keySet()){
			holos.get(loc).delete();
		}
		holos.clear();
	}
	
}