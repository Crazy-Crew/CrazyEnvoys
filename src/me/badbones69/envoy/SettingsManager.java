package me.badbones69.envoy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class SettingsManager {

	static SettingsManager instance = new SettingsManager();

	public static SettingsManager getInstance() {
		return instance;
	}

	Plugin p;

	FileConfiguration config;
	File cfile;

	FileConfiguration msg;
	File mfile;

	FileConfiguration data;
	File dfile;

	File tierfolder;

	public void setup(Plugin p) {
		this.p = p;
		if(!p.getDataFolder().exists()) {
			p.getDataFolder().mkdir();
		}

		tierfolder = new File(p.getDataFolder() + "/Tiers");
		if(!tierfolder.exists()) {
			tierfolder.mkdir();
			try {
				File basic = new File(p.getDataFolder() + "/Tiers/", "Basic.yml");
				File lucky = new File(p.getDataFolder() + "/Tiers/", "Lucky.yml");
				File titan = new File(p.getDataFolder() + "/Tiers/", "Titan.yml");
				InputStream B = getClass().getResourceAsStream("/Tiers/Basic.yml");
				InputStream L = getClass().getResourceAsStream("/Tiers/Lucky.yml");
				InputStream T = getClass().getResourceAsStream("/Tiers/Titan.yml");
				copyFile(B, basic);
				copyFile(L, lucky);
				copyFile(T, titan);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}

		cfile = new File(p.getDataFolder(), "Config.yml");
		if(!cfile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/Config.yml");
				InputStream E = getClass().getResourceAsStream("/Config.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(cfile);

		mfile = new File(p.getDataFolder(), "Messages.yml");
		if(!mfile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/Messages.yml");
				InputStream E = getClass().getResourceAsStream("/Messages.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		msg = YamlConfiguration.loadConfiguration(mfile);

		dfile = new File(p.getDataFolder(), "Data.yml");
		if(!dfile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/Data.yml");
				InputStream E = getClass().getResourceAsStream("/Data.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		data = YamlConfiguration.loadConfiguration(dfile);

	}

	public ArrayList<File> getAllTiers() {
		ArrayList<File> files = new ArrayList<File>();
		for(String name : tierfolder.list()) {
			if(!name.equalsIgnoreCase(".DS_Store")) {
				files.add(new File(tierfolder, name));
			}
		}
		return files;
	}

	public ArrayList<String> getAllTierNames() {
		ArrayList<String> files = new ArrayList<String>();
		for(String name : tierfolder.list()) {
			if(!name.equalsIgnoreCase(".DS_Store")) {
				File f = new File(tierfolder, name);
				files.add(f.getName().replaceAll(".yml", ""));
			}
		}
		return files;
	}

	public FileConfiguration getFile(String tier) {
		File file = new File(p.getDataFolder() + "/Tiers/", tier + ".yml");
		return YamlConfiguration.loadConfiguration(file);
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public FileConfiguration getMessages() {
		return msg;
	}

	public FileConfiguration getData() {
		return data;
	}

	public void saveConfig() {
		try {
			config.save(cfile);
		}catch(IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save Config.yml!");
		}
	}

	public void saveMessages() {
		try {
			msg.save(mfile);
		}catch(IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save Messages.yml!");
		}
	}

	public void saveData() {
		try {
			data.save(dfile);
		}catch(IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save Data.yml!");
		}
	}

	public void reloadTiers() {
		config = YamlConfiguration.loadConfiguration(cfile);
		data = YamlConfiguration.loadConfiguration(dfile);
		for(File c : getAllTiers()) {
			YamlConfiguration.loadConfiguration(c);
		}
	}

	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(cfile);
	}

	public void reloadMessages() {
		msg = YamlConfiguration.loadConfiguration(mfile);
	}

	public void reloadData() {
		data = YamlConfiguration.loadConfiguration(dfile);
	}

	public PluginDescriptionFile getDesc() {
		return p.getDescription();
	}

	public static void copyFile(InputStream in, File out) throws Exception { // https://bukkit.org/threads/extracting-file-from-jar.16962/
		InputStream fis = in;
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		}catch(Exception e) {
			throw e;
		}finally {
			if(fis != null) {
				fis.close();
			}
			if(fos != null) {
				fos.close();
			}
		}
	}

}
