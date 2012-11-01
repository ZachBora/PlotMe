package com.worldcretornica.plotme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
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
    public static boolean globalUseEconomy;
    public static boolean advancedlogging;
    //public static boolean showmoneychanges;
    
    public static Map<String, PlotMapInfo> plotmaps = null;
    
    public static WorldEditPlugin we = null;
    public static Economy economy = null;
    
    private static HashSet<String> playersignoringwelimit = null;
    
    public static World worldcurrentlyprocessingexpired;
    public static CommandSender cscurrentlyprocessingexpired;
    public static int counterexpired;
    public static int nbperdeletionprocessingexpired;
	
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
		economy = null;
	}
	
	public void onEnable()
	{
		initialize();
		
		doMetric();
		
		PluginManager pm = getServer().getPluginManager();
				
		pm.registerEvents(new PlotListener(), this);
		
		if(pm.getPlugin("Vault") != null)
		{
			setupEconomy();
		}
		
		if(pm.getPlugin("WorldEdit") != null)
		{
			we = (WorldEditPlugin) pm.getPlugin("WorldEdit");
			pm.registerEvents(new PlotWorldEditListener(), this);			
		}
				
		getCommand("plotme").setExecutor(new PMCommand(this));
	}
	
	private void doMetric()
	{
		try
		{
		    Metrics metrics = new Metrics(this);
		    
		    Graph graphNbWorlds = metrics.createGraph("Plot worlds");
		    
		    graphNbWorlds.addPlotter(new Metrics.Plotter("Number of plot worlds")
		    {
				@Override
				public int getValue() {
					return plotmaps.size();
				}
			});
		    	    
		    graphNbWorlds.addPlotter(new Metrics.Plotter("Average Plot size")
		    {
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
		    
		    graphNbWorlds.addPlotter(new Metrics.Plotter("Number of plots")
		    {
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
	}
	
	public ChunkGenerator getDefaultWorldGenerator(String worldname, String id)
	{		
		if(PlotManager.isPlotWorld(worldname))
		{
			return new PlotGen(PlotManager.getMap(worldname));
		}else{
			logger.warning(PREFIX + " Configuration not found for PlotMe world '" + worldname + "' Using defaults");
			return new PlotGen();
		}
	}
	
	public static boolean cPerms(CommandSender sender, String node)
	{
		return sender.hasPermission(node);
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
		globalUseEconomy = config.getBoolean("globalUseEconomy", false);
		advancedlogging = config.getBoolean("AdvancedLogging", false);
		//showmoneychanges = config.getBoolean("ShowMoneyChanges", true);
		
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
			plotworld.set("ProtectedBlocks", getDefaultProtectedBlocks());
			plotworld.set("PreventedItems", getDefaultPreventedItems());
			plotworld.set("ProtectedWallBlockId", "44:4");
			plotworld.set("ForSaleWallBlockId", "44:1");
			plotworld.set("AuctionWallBlockId", "44:1");
			plotworld.set("AutoLinkPlots", true);
			plotworld.set("DisableExplosion", true);
			plotworld.set("DisableIgnition", true);
			
			ConfigurationSection economysection = plotworld.createSection("economy");
			
			economysection.set("UseEconomy", false);
			economysection.set("CanPutOnSale", false);
			economysection.set("CanSellToBank", false);
			economysection.set("RefundClaimPriceOnReset", false);
			economysection.set("RefundClaimPriceOnSetOwner", false);
			economysection.set("ClaimPrice", 0);
			economysection.set("ClearPrice", 0);
			economysection.set("AddPlayerPrice", 0);
			economysection.set("RemovePlayerPrice", 0);
			economysection.set("PlotHomePrice", 0);
			economysection.set("CanCustomizeSellPrice", false);
			economysection.set("SellToPlayerPrice", 0);
			economysection.set("SellToBankPrice", 0);
			economysection.set("BuyFromBankPrice", 0);
			economysection.set("AddCommentPrice", 0);
			economysection.set("BiomeChangePrice", 0);
			economysection.set("ProtectPrice", 0);
			economysection.set("DisposePrice", 0);
			
			plotworld.set("economy", economysection);
			
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
			if(tempPlotInfo.RoadHeight > 250)
			{
				logger.severe(PREFIX + " RoadHeight above 250 is unsafe. This is the height at which your road is located. Setting it to 64.");
				tempPlotInfo.RoadHeight = 64;
			}
			tempPlotInfo.DaysToExpiration = currworld.getInt("DaysToExpiration", 7);
			
			if(currworld.contains("ProtectedBlocks"))
			{
				tempPlotInfo.ProtectedBlocks = currworld.getIntegerList("ProtectedBlocks");
			}else{
				tempPlotInfo.ProtectedBlocks = getDefaultProtectedBlocks();
			}
			
			if(currworld.contains("PreventedItems"))
			{
				tempPlotInfo.PreventedItems = currworld.getStringList("PreventedItems");
			}else{
				tempPlotInfo.PreventedItems = getDefaultPreventedItems();
			}
			
			tempPlotInfo.ProtectedWallBlockId = currworld.getString("ProtectedWallBlockId", "44:4");
			tempPlotInfo.ForSaleWallBlockId = currworld.getString("ForSaleWallBlockId", "44:1");
			tempPlotInfo.AuctionWallBlockId = currworld.getString("AuctionWallBlockId", "44:1");
			tempPlotInfo.AutoLinkPlots = currworld.getBoolean("AutoLinkPlots", true);
			tempPlotInfo.DisableExplosion = currworld.getBoolean("DisableExplosion", true);
			tempPlotInfo.DisableIgnition = currworld.getBoolean("DisableIgnition", true);
			
			ConfigurationSection economysection;
			
			if(currworld.getConfigurationSection("economy") == null)
				economysection = currworld.createSection("economy");
			else
				economysection = currworld.getConfigurationSection("economy");
			
			tempPlotInfo.UseEconomy = economysection.getBoolean("UseEconomy", false);
			tempPlotInfo.CanPutOnSale = economysection.getBoolean("CanPutOnSale", false);
			tempPlotInfo.CanSellToBank = economysection.getBoolean("CanSellToBank", false);
			tempPlotInfo.RefundClaimPriceOnReset = economysection.getBoolean("RefundClaimPriceOnReset", false);
			tempPlotInfo.RefundClaimPriceOnSetOwner = economysection.getBoolean("RefundClaimPriceOnSetOwner", false);
			tempPlotInfo.ClaimPrice = economysection.getDouble("ClaimPrice", 0);
			tempPlotInfo.ClearPrice = economysection.getDouble("ClearPrice", 0);
			tempPlotInfo.AddPlayerPrice = economysection.getDouble("AddPlayerPrice", 0);
			tempPlotInfo.RemovePlayerPrice = economysection.getDouble("RemovePlayerPrice", 0);
			tempPlotInfo.PlotHomePrice = economysection.getDouble("PlotHomePrice", 0);
			tempPlotInfo.CanCustomizeSellPrice = economysection.getBoolean("CanCustomizeSellPrice", false);
			tempPlotInfo.SellToPlayerPrice = economysection.getDouble("SellToPlayerPrice", 0);
			tempPlotInfo.SellToBankPrice = economysection.getDouble("SellToBankPrice", 0);
			tempPlotInfo.BuyFromBankPrice = economysection.getDouble("BuyFromBankPrice", 0);
			tempPlotInfo.AddCommentPrice = economysection.getDouble("AddCommentPrice", 0);
			tempPlotInfo.BiomeChangePrice = economysection.getDouble("BiomeChangePrice", 0);
			tempPlotInfo.ProtectPrice = economysection.getDouble("ProtectPrice", 0);
			tempPlotInfo.DisposePrice = economysection.getDouble("DisposePrice", 0);
			
			
			
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
			currworld.set("ProtectedBlocks", tempPlotInfo.ProtectedBlocks);
			currworld.set("PreventedItems", tempPlotInfo.PreventedItems);
			currworld.set("ProtectedWallBlockId", tempPlotInfo.ProtectedWallBlockId);
			currworld.set("ForSaleWallBlockId", tempPlotInfo.ForSaleWallBlockId);
			currworld.set("AuctionWallBlockId", tempPlotInfo.AuctionWallBlockId);
			currworld.set("AutoLinkPlots", tempPlotInfo.AutoLinkPlots);
			currworld.set("DisableExplosion", tempPlotInfo.DisableExplosion);
			currworld.set("DisableIgnition", tempPlotInfo.DisableIgnition);
			
			economysection = currworld.createSection("economy");
			
			economysection.set("UseEconomy", tempPlotInfo.UseEconomy);
			economysection.set("CanPutOnSale", tempPlotInfo.CanPutOnSale);
			economysection.set("CanSellToBank", tempPlotInfo.CanSellToBank);
			economysection.set("RefundClaimPriceOnReset", tempPlotInfo.RefundClaimPriceOnReset);
			economysection.set("RefundClaimPriceOnSetOwner", tempPlotInfo.RefundClaimPriceOnSetOwner);
			economysection.set("ClaimPrice", tempPlotInfo.ClaimPrice);
			economysection.set("ClearPrice", tempPlotInfo.ClearPrice);
			economysection.set("AddPlayerPrice", tempPlotInfo.AddPlayerPrice);
			economysection.set("RemovePlayerPrice", tempPlotInfo.RemovePlayerPrice);
			economysection.set("PlotHomePrice", tempPlotInfo.PlotHomePrice);
			economysection.set("CanCustomizeSellPrice", tempPlotInfo.CanCustomizeSellPrice);
			economysection.set("SellToPlayerPrice", tempPlotInfo.SellToPlayerPrice);
			economysection.set("SellToBankPrice", tempPlotInfo.SellToBankPrice);
			economysection.set("BuyFromBankPrice", tempPlotInfo.BuyFromBankPrice);
			economysection.set("AddCommentPrice", tempPlotInfo.AddCommentPrice);
			economysection.set("BiomeChangePrice", tempPlotInfo.BiomeChangePrice);
			economysection.set("ProtectPrice", tempPlotInfo.ProtectPrice);
			economysection.set("DisposePrice", tempPlotInfo.DisposePrice);
			
			currworld.set("economy", economysection);
			
			worlds.set(worldname, currworld);
			
			tempPlotInfo.plots = SqlManager.getPlots(worldname.toLowerCase());
			
			plotmaps.put(worldname.toLowerCase(), tempPlotInfo);
		}
		
		config.set("usemySQL", usemySQL);
		config.set("mySQLconn", mySQLconn);
		config.set("mySQLuname", mySQLuname);
		config.set("mySQLpass", mySQLpass);
		config.set("AutoPlotLimit", AutoPlotLimit);
		config.set("globalUseEconomy", globalUseEconomy);
		config.set("AdvancedLogging", advancedlogging);
		//config.set("ShowMoneyChanges", showmoneychanges);
		
		try {
			config.save(configfile);
		} catch (IOException e) {
			logger.severe(PREFIX + " error writting configurations");
			e.printStackTrace();
		}
    }
	
	private void setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
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
		
		if(p.hasPermission("plotme.limit.*"))
		{
			max = -1;
		}else{
			for(int ctr = 0; ctr < maxlimit; ctr++)
			{
				if(p.hasPermission("plotme.limit." + ctr))
				{
					max = ctr;
				}
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
			if(cPerms(p, "PlotMe.admin"))
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
	
	public List<Integer> getDefaultProtectedBlocks()
	{
		List<Integer> protections = new ArrayList<Integer>();
		
		protections.add(Material.CHEST.getId());
		protections.add(Material.FURNACE.getId());
		protections.add(Material.BURNING_FURNACE.getId());
		protections.add(Material.ENDER_PORTAL_FRAME.getId());
		protections.add(Material.DIODE_BLOCK_ON.getId());
		protections.add(Material.DIODE_BLOCK_OFF.getId());
		protections.add(Material.JUKEBOX.getId());
		protections.add(Material.NOTE_BLOCK.getId());
		protections.add(Material.BED.getId());
		protections.add(Material.CAULDRON.getId());
		protections.add(Material.BREWING_STAND.getId());
		protections.add(Material.BEACON.getId());
		protections.add(Material.FLOWER_POT.getId());
		protections.add(Material.ANVIL.getId());
		
		return protections;
	}
	
	public List<String> getDefaultPreventedItems()
	{
		List<String> preventeditems = new ArrayList<String>();

		preventeditems.add("" + Material.INK_SACK.getId() + ":15");
		preventeditems.add("" + Material.FLINT_AND_STEEL.getId());
		preventeditems.add("" + Material.MINECART.getId());
		preventeditems.add("" + Material.POWERED_MINECART.getId());
		preventeditems.add("" + Material.STORAGE_MINECART.getId());
		preventeditems.add("" + Material.BOAT.getId());
		
		return preventeditems;
	}
	
	public void scheduleTask(Runnable task, int eachseconds, int howmanytimes)
	{		 		 
		//return Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, task, eachseconds, howmanytimes * eachseconds);
		
		PlotMe.cscurrentlyprocessingexpired.sendMessage("" + PlotMe.PREFIX + ChatColor.RESET + " Starting delete session");
		
		for(int ctr = 0; ctr < (howmanytimes / nbperdeletionprocessingexpired); ctr++)
		{
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, task, ctr * eachseconds * 20);
		}
	}
}
