package com.worldcretornica.plotme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.worldcretornica.plotme.Metrics.Graph;
import com.worldcretornica.plotme.listener.PlotListener;
import com.worldcretornica.plotme.listener.PlotWorldEditListener;

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
    public static String configpath;
    public static int AutoPlotLimit;
    
    public static Map<String, PlotMapInfo> plotmaps;
    
    public static WorldEditPlugin we;
    
    public static boolean usingPEX = false;
    
    private static HashSet<String> playersignoringwelimit = null;
	
	public void onDisable()
	{	
		SqlManager.closeConnection();
		NAME = null;
		PREFIX = null;
		VERSION = null;
		mySQLuname = null;
		mySQLpass = null;
		mySQLconn = null;
		plotmaps = null;
		configpath = null;
		we = null;
	}
	
	public void onEnable()
	{
		initialize();
		
		try {
		    Metrics metrics = new Metrics(this);
		    
		    Graph graphNbWorlds = metrics.createGraph("Plot worlds");
		    
		    graphNbWorlds.addPlotter(new Metrics.Plotter("Number of plot worlds") {
				@Override
				public int getValue() {
					return plotmaps.size();
				}
			});
		    	    
		    graphNbWorlds.addPlotter(new Metrics.Plotter("Average Plot size") {
				@Override
				public int getValue() {
					
					if(plotmaps.size() > 0)
					{
						int totalplotsize = 0;
						
						for(PlotMapInfo p : plotmaps.values())
						{
							totalplotsize += p.PlotSize;
						}
						
						
						return totalplotsize / plotmaps.size();
					}
					else
					{
						return 0;
					}
				}
			});
		    
		    graphNbWorlds.addPlotter(new Metrics.Plotter("Number of plots") {
				@Override
				public int getValue() {
					int nbplot = 0;
					
					for(PlotMapInfo p : plotmaps.values())
					{
						nbplot += p.plots.size();
					}
					
					return nbplot;
				}
			});
		    		    
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
		}
		
		PluginManager pm = getServer().getPluginManager();
				
		pm.registerEvents(new PlotListener(), this);
		
		if(pm.getPlugin("WorldEdit") != null)
		{
			we = (WorldEditPlugin) pm.getPlugin("WorldEdit");
			pm.registerEvents(new PlotWorldEditListener(), this);			
		}
		
		usingPEX = (pm.getPlugin("PermissionsEx") != null);
			
		getCommand("plotme").setExecutor(new PMCommand(this));
	}
	
	public ChunkGenerator getDefaultWorldGenerator(String worldname, String id)
	{
		initialize();
		
		if(PlotManager.isPlotWorld(worldname))
		{
			return new PlotGen(PlotManager.getMap(worldname));
		}else{
			logger.warning(PREFIX + " Configuration not found for PlotMe world '" + worldname + "' Using defaults");
			return new PlotGen();
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
		PREFIX = ChatColor.BLUE + "[" + NAME + "]";
		VERSION = pdfFile.getVersion();
		configpath = getDataFolder().getAbsolutePath();
		playersignoringwelimit = new HashSet<String>();

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
			plotworld.set("RoadHeight", 64);
			plotworld.set("DaysToExpiration", 7);
			
			worlds.set("plotworld", plotworld);
			config.set("worlds", worlds);
		}
		else
		{
			worlds = config.getConfigurationSection("worlds");
		}
		
		plotmaps = new HashMap<String, PlotMapInfo>();
		
		for(String worldname : worlds.getKeys(false))
		{
			PlotMapInfo tempPlotInfo = new PlotMapInfo();
			ConfigurationSection currworld = worlds.getConfigurationSection(worldname);
			
			tempPlotInfo.PlotAutoLimit = currworld.getInt("PlotAutoLimit", 100);
			tempPlotInfo.PathWidth = currworld.getInt("PathWidth", 7);
			tempPlotInfo.PlotSize = currworld.getInt("PlotSize", 32);
			tempPlotInfo.BottomBlockId = (byte) currworld.getInt("BottomBlockId", 7);
			tempPlotInfo.WallBlockId = (byte) currworld.getInt("WallBlockId", 44);
			tempPlotInfo.PlotFloorBlockId = (byte) currworld.getInt("PlotFloorBlockId", 2);
			tempPlotInfo.PlotFillingBlockId = (byte) currworld.getInt("PlotFillingBlockId", 3);
			tempPlotInfo.RoadHeight = currworld.getInt("RoadHeight", currworld.getInt("WorldHeight", 64));
			tempPlotInfo.DaysToExpiration = currworld.getInt("DaysToExpiration", 7);
			
			if(tempPlotInfo.RoadHeight > 250)
			{
				logger.severe(PREFIX + " RoadHeight above 250 is unsafe. This is the height at which your road is located. Setting it to 64.");
				tempPlotInfo.RoadHeight = 64;
			}
			
			currworld.set("PlotAutoLimit", tempPlotInfo.PlotAutoLimit);
			currworld.set("PathWidth", tempPlotInfo.PathWidth);
			currworld.set("PlotSize", tempPlotInfo.PlotSize);
			currworld.set("BottomBlockId", tempPlotInfo.BottomBlockId);
			currworld.set("WallBlockId", tempPlotInfo.WallBlockId);
			currworld.set("PlotFloorBlockId", tempPlotInfo.PlotFloorBlockId);
			currworld.set("PlotFillingBlockId", tempPlotInfo.PlotFillingBlockId);
			currworld.set("RoadHeight", tempPlotInfo.RoadHeight);
			currworld.set("WorldHeight", null);
			currworld.set("DaysToExpiration", tempPlotInfo.DaysToExpiration);
			
			tempPlotInfo.plots = SqlManager.getPlots(worldname.toLowerCase());
			
			plotmaps.put(worldname.toLowerCase(), tempPlotInfo);
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
	
	public static void addIgnoreWELimit(Player p)
	{
		if(!isIgnoringWELimit(p))
		{
			playersignoringwelimit.add(p.getName());
			if(we != null)
				PlotWorldEdit.removeMask(p);
		}
	}
	
	public static void removeIgnoreWELimit(Player p)
	{
		if(isIgnoringWELimit(p))
		{
			playersignoringwelimit.remove(p.getName());
			if(we != null)
				PlotWorldEdit.setMask(p);
		}
	}
	
	public static boolean isIgnoringWELimit(Player p)
	{
		return playersignoringwelimit.contains(p.getName());
	}
	
	public static int getPlotLimit(Player p)
	{
		int max = 0;
		
		int maxlimit = 255;
		
		for(int ctr = 0; ctr < maxlimit; ctr++)
		{
			if(p.hasPermission("plotme.limit." + ctr))
			{
				max = ctr;
			}
		}
		
		//This is solution 1, but I don't like it, instead we'll use above solution
		/*
		String limit = "";
		
		if(usingPEX)
		{
			PermissionUser user = PermissionsEx.getUser(p);
			
			for(String perm : user.getPermissions(p.getWorld().getName()))
			{
				//logger.info("PlotMe: " + perm);
				
				if(perm.startsWith("plotme.limit."))
				{			
					limit = perm.substring(perm.lastIndexOf(".") + 1);
									
					int tempmax = 0;
					
					if(limit.equals("*"))
					{
						return -1;
					}else{
						try
						{
							tempmax = Integer.parseInt(limit);
						}catch(NumberFormatException ex)
						{
							tempmax = 1;
						}
						
						if(tempmax > max)
							max = tempmax;
					}
				}
			}
			
		}
		else
		{
			Set<PermissionAttachmentInfo> perms = p.getEffectivePermissions();
			
			for(PermissionAttachmentInfo pai : perms)
			{
				if(pai.getValue())
				{
					String perm = pai.getPermission();
					
					//logger.info("PLotMe: " + perm);
								
					if(perm.startsWith("plotme.limit."))
					{			
						limit = perm.substring(perm.lastIndexOf(".") + 1);
										
						int tempmax = 0;
						
						if(limit.equals("*"))
						{
							return -1;
						}else{
							try
							{
								tempmax = Integer.parseInt(limit);
							}catch(NumberFormatException ex)
							{
								tempmax = 1;
							}
							
							if(tempmax > max)
								max = tempmax;
						}
					}
				}
			}
		}
		*/
		if(max == 0)
		{
			if(cPerms(p, "PlotMe.admin", false))
				max = -1;
			else
				max = 1;
		}
		
		return max;
	}
	
	public static String getDate()
	{
		return getDate(Calendar.getInstance());
	}
	
	public static String getDate(Calendar cal)
	{
		int imonth = cal.get(Calendar.MONTH) + 1;
        int iday = cal.get(Calendar.DAY_OF_MONTH) + 1;
        String month = "";
        String day = "";
        
        if(imonth < 10)
        	month = "0" + imonth;
        else
        	month = "" + imonth;
        
        if(iday < 10)
        	day = "0" + iday;
        else
        	day = "" + iday;
        		
		return "" + cal.get(Calendar.YEAR) + "-" + month + "-" + day;
	}

	public static String getDate(java.sql.Date expireddate)
	{		
		return expireddate.toString();
	}
}
