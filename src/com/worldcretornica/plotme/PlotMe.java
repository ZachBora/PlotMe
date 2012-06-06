package com.worldcretornica.plotme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PlotMe extends JavaPlugin
{
	public static String NAME;
	public static String PREFIX;
	public static String VERSION;
	
	public static final Logger logger = Logger.getLogger("Minecraft");
		
	public static boolean usemySQL;
    public static String mySQLuname;
    public static String mySQLpass;
    public static String mySQLconn;
    
    public static int AutoPlotLimit;
    
    public static Map<String, PlotMapInfo> plotmaps;
    
    public static String configpath;
	
	public void onDisable()
	{		
		SqlManager.closeConnection();
	}
	
	public void onEnable()
	{
		initialize();
				
		getServer().getPluginManager().registerEvents(new PlotListener(), this);
		
		getCommand("plotme").setExecutor(new PMCommand(this));
	}
	
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
	{
		if(plotmaps.containsKey(worldName))
		{
			return new PlotGen();
		}else{
			return super.getDefaultWorldGenerator(worldName, id);
		}
	}
	
	public static boolean checkPerms(Player player, String node)
	{
		return player.hasPermission(node) || player.hasPermission(node.toLowerCase()) || player.hasPermission(NAME + ".*") || player.hasPermission("*");
	}
	
	public static boolean cPerms(Player player, String node, Boolean basic)
	{
		return checkPerms(player, node) || (basic && checkPerms(player, "PlotMe.use")) || checkPerms(player, "PlotMe.admin");
	}
	
	public void initialize()
	{
		PluginDescriptionFile pdfFile = this.getDescription();
		NAME = pdfFile.getName();
		PREFIX = "[" + NAME + "]";
		VERSION = pdfFile.getVersion();
		configpath = getDataFolder().getAbsolutePath();

		if(!this.getDataFolder().exists()) {
        	this.getDataFolder().mkdirs();
        }
		
		File configfile = new File(configpath, "config.yml");
		FileConfiguration config = new YamlConfiguration();
		
		try {
			config.load(configfile);
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			logger.severe(PREFIX + " can't read configuration file");
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			logger.severe(PREFIX + " invalid configuration format");
			e.printStackTrace();
		}
        
        usemySQL = config.getBoolean("usemySQL", false);
		mySQLconn = config.getString("mySQLconn", "jdbc:mysql://localhost:3306/minecraft");
		mySQLuname = config.getString("mySQLuname", "root");
		mySQLpass = config.getString("mySQLpass", "password");
		AutoPlotLimit = config.getInt("AutoPlotLimit", 100);
		
		ConfigurationSection worlds;
		
		if(!config.contains("worlds"))
		{
			worlds = config.createSection("worlds");
			
			ConfigurationSection plotworld = worlds.createSection("plotworld");
			
			plotworld.set("PlotAutoLimit", 100);
			plotworld.set("PathWidth", 7);
			plotworld.set("PlotSize", 32);
			plotworld.set("BottomBlockId", 7);
			plotworld.set("WallBlockId", 44);
			plotworld.set("PlotFloorBlockId", 2);
			plotworld.set("PlotFillingBlockId", 3);
			
			worlds.set("plotworld", plotworld);
			config.set("worlds", worlds);
		}
		else
		{
			worlds = config.getConfigurationSection("worlds");
		}
		
		plotmaps = new HashMap<String, PlotMapInfo>();
		
		for(String worldname :  worlds.getKeys(false))
		{
			PlotMapInfo tempPlotInfo = new PlotMapInfo();
			ConfigurationSection currworld = worlds.getConfigurationSection(worldname);
			
			tempPlotInfo.PlotAutoLimit = currworld.getInt("PlotAutoLimit");
			tempPlotInfo.PathWidth = currworld.getInt("PathWidth");
			tempPlotInfo.PlotSize = currworld.getInt("PlotSize");
			tempPlotInfo.BottomBlockId = (byte) currworld.getInt("BottomBlockId");
			tempPlotInfo.WallBlockId = (byte) currworld.getInt("WallBlockId");
			tempPlotInfo.PlotFloorBlockId = (byte) currworld.getInt("PlotFloorBlockId");
			tempPlotInfo.PlotFillingBlockId = (byte) currworld.getInt("PlotFillingBlockId");
			
			logger.info("plot size: " + tempPlotInfo.PlotSize);
			
			tempPlotInfo.plots = SqlManager.getPlots(worldname);
			
			plotmaps.put(worldname, tempPlotInfo);
		}
		
		config.set("usemySQL", usemySQL);
		config.set("mySQLconn", mySQLconn);
		config.set("mySQLuname", mySQLuname);
		config.set("mySQLpass", mySQLpass);
		
		try {
			config.save(configfile);
		} catch (IOException e) {
			logger.severe(PREFIX + " error writting configurations");
			e.printStackTrace();
		}
    }
}
