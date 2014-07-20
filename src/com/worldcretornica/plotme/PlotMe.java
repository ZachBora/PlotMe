package com.worldcretornica.plotme;

import com.worldcretornica.plotme.listener.PlotDenyListener;
import com.worldcretornica.plotme.listener.PlotListener;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class PlotMe extends JavaPlugin {
	public static String NAME;
	public static String PREFIX;

	public static Logger logger = Logger.getLogger("Minecraft");

	public static boolean usemySQL;
	public static String mySQLuname;
	public static String mySQLpass;
	public static String mySQLconn;
	public static String configpath;
	public static boolean globalUseEconomy;
	public static String language;
	public static boolean allowWorldTeleport;
	public static boolean allowToDeny;

	public static ConcurrentHashMap<String, PlotMapInfo> plotmaps;

	public static Economy economy;

	public static World worldcurrentlyprocessingexpired;
	public static CommandSender cscurrentlyprocessingexpired;
	public static Integer counterexpired;
	public static Integer nbperdeletionprocessingexpired;
	private File captionFile = new File(getDataFolder(), "captions.yml");
	private FileConfiguration captionsConfig;
	protected static PlotMe self;

	Boolean initialized = false;

	@Override
	public void onDisable() {
		SqlManager.closeConnection();
		NAME = null;
		PREFIX = null;

		logger = null;

		mySQLuname = null;
		mySQLpass = null;
		mySQLconn = null;
		language = null;
		configpath = null;
		economy = null;
		worldcurrentlyprocessingexpired = null;
		cscurrentlyprocessingexpired = null;
		counterexpired = null;
		nbperdeletionprocessingexpired = null;
		self = null;
		initialized = null;
	}

	@Override
	public void onEnable() {
		self = this;

		initialize();

		PluginManager pm = getServer().getPluginManager();

		pm.registerEvents(new PlotListener(), this);

		if (pm.getPlugin("Vault") != null) {
			setupEconomy();
		}

		if (allowToDeny) {
			pm.registerEvents(new PlotDenyListener(), this);
		}

		getCommand("plotme").setExecutor(new PMCommand(this));

		initialized = true;

		SqlManager.plotConvertToUUIDAsynchronously();
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldname, String id) {
		if (PlotManager.isPlotWorld(worldname)) {
			return new PlotGen(PlotManager.getMap(worldname));
		} else {
			logger.warning(PREFIX + "Configuration not found for PlotMe world '" + worldname + "' Using defaults");
			return new PlotGen();
		}
	}

	public static String caption(String s) {
		return self.getCaptionFile().getString(s);
	}

	public ConfigurationSection getCaptionFile() {
		if (captionsConfig == null) {
			reloadCaptionFile();
		}
		return captionsConfig;
	}

	private void loadCaptions() {
		createCapFile();
		getCaptionFile();
	}

	private void createCapFile() {
		if (!captionFile.exists()) {
			saveResource("captions.yml", false);
		}
	}

	@SuppressWarnings("deprecation")
	private void reloadCaptionFile() {
		if (captionFile == null) {
			captionFile = new File(getDataFolder(), "captions.yml");
		}
		captionsConfig = YamlConfiguration.loadConfiguration(captionFile);

		// Look for defaults in the jar
		InputStream defConfigStream = getResource("captions.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			captionsConfig.setDefaults(defConfig);
		}
	}

	public void initialize() {
		PluginDescriptionFile pdfFile = getDescription();
		NAME = pdfFile.getName();
		PREFIX = ChatColor.BLUE + "[" + NAME + "] " + ChatColor.RESET;
		configpath = getDataFolder().getAbsolutePath();

		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}

		File configfile = new File(configpath, "config.yml");
		FileConfiguration config = new YamlConfiguration();

		try {
			config.load(configfile);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			logger.severe(PREFIX + "can't read configuration file");
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			logger.severe(PREFIX + "invalid configuration format");
			e.printStackTrace();
		}

		usemySQL = config.getBoolean("usemySQL", false);
		mySQLconn = config.getString("mySQLconn", "jdbc:mysql://localhost:3306/minecraft");
		mySQLuname = config.getString("mySQLuname", "root");
		mySQLpass = config.getString("mySQLpass", "password");
		globalUseEconomy = config.getBoolean("globalUseEconomy", false);
		language = config.getString("Language", "english");
		allowWorldTeleport = config.getBoolean("allowWorldTeleport", true);
		allowToDeny = config.getBoolean("allowToDeny", true);

		ConfigurationSection worlds;

		if (!config.contains("worlds")) {
			worlds = config.createSection("worlds");

			ConfigurationSection plotworld = worlds.createSection("plotworld");

			plotworld.set("PlotAutoLimit", 1000);
			plotworld.set("PathWidth", 7);
			plotworld.set("PlotSize", 32);

			plotworld.set("WallBlockId", "44");
			plotworld.set("PlotFloorBlockId", "2");
			plotworld.set("PlotFillingBlockId", "3");
			plotworld.set("RoadMainBlockId", "5");
			plotworld.set("RoadStripeBlockId", "5:2");

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
			economysection.set("DenyPlayerPrice", 0);
			economysection.set("RemovePlayerPrice", 0);
			economysection.set("UndenyPlayerPrice", 0);
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
		} else {
			worlds = config.getConfigurationSection("worlds");
		}

		plotmaps = new ConcurrentHashMap<>();

		for (String worldname : worlds.getKeys(false)) {
			PlotMapInfo tempPlotInfo = new PlotMapInfo();
			ConfigurationSection currworld = worlds.getConfigurationSection(worldname);

			tempPlotInfo.PlotAutoLimit = currworld.getInt("PlotAutoLimit", 100);
			tempPlotInfo.PathWidth = currworld.getInt("PathWidth", 7);
			tempPlotInfo.PlotSize = currworld.getInt("PlotSize", 32);

			tempPlotInfo.WallBlockId = getBlockId(currworld, "WallBlockId", "44:0");
			tempPlotInfo.WallBlockValue = getBlockValue(currworld, "WallBlockId", "44:0");
			tempPlotInfo.PlotFloorBlockId = getBlockId(currworld, "PlotFloorBlockId", "2:0");
			tempPlotInfo.PlotFloorBlockValue = getBlockValue(currworld, "PlotFloorBlockId", "2:0");
			tempPlotInfo.PlotFillingBlockId = getBlockId(currworld, "PlotFillingBlockId", "3:0");
			tempPlotInfo.PlotFillingBlockValue = getBlockValue(currworld, "PlotFillingBlockId", "3:0");
			tempPlotInfo.RoadMainBlockId = getBlockId(currworld, "RoadMainBlockId", "5:0");
			tempPlotInfo.RoadMainBlockValue = getBlockValue(currworld, "RoadMainBlockId", "5:0");
			tempPlotInfo.RoadStripeBlockId = getBlockId(currworld, "RoadStripeBlockId", "5:2");
			tempPlotInfo.RoadStripeBlockValue = getBlockValue(currworld, "RoadStripeBlockId", "5:2");

			tempPlotInfo.RoadHeight = currworld.getInt("RoadHeight", currworld.getInt("WorldHeight", 64));
			if (tempPlotInfo.RoadHeight > 250) {
				logger.severe(PREFIX + "RoadHeight above 250 is unsafe. This is the height at which your road is located. Setting it to 64.");
				tempPlotInfo.RoadHeight = 64;
			}
			tempPlotInfo.DaysToExpiration = currworld.getInt("DaysToExpiration", 7);

			if (currworld.contains("ProtectedBlocks")) {
				tempPlotInfo.ProtectedBlocks = currworld.getIntegerList("ProtectedBlocks");
			} else {
				tempPlotInfo.ProtectedBlocks = getDefaultProtectedBlocks();
			}

			if (currworld.contains("PreventedItems")) {
				tempPlotInfo.PreventedItems = currworld.getStringList("PreventedItems");
			} else {
				tempPlotInfo.PreventedItems = getDefaultPreventedItems();
			}

			tempPlotInfo.ProtectedWallBlockId = currworld.getString("ProtectedWallBlockId", "44:4");
			tempPlotInfo.ForSaleWallBlockId = currworld.getString("ForSaleWallBlockId", "44:1");
			tempPlotInfo.AuctionWallBlockId = currworld.getString("AuctionWallBlockId", "44:1");
			tempPlotInfo.AutoLinkPlots = currworld.getBoolean("AutoLinkPlots", true);
			tempPlotInfo.DisableExplosion = currworld.getBoolean("DisableExplosion", true);
			tempPlotInfo.DisableIgnition = currworld.getBoolean("DisableIgnition", true);

			ConfigurationSection economysection;

			if (currworld.getConfigurationSection("economy") == null) {
				economysection = currworld.createSection("economy");
			} else {
				economysection = currworld.getConfigurationSection("economy");
			}

			tempPlotInfo.UseEconomy = economysection.getBoolean("UseEconomy", false);
			tempPlotInfo.CanPutOnSale = economysection.getBoolean("CanPutOnSale", false);
			tempPlotInfo.CanSellToBank = economysection.getBoolean("CanSellToBank", false);
			tempPlotInfo.RefundClaimPriceOnReset = economysection.getBoolean("RefundClaimPriceOnReset", false);
			tempPlotInfo.RefundClaimPriceOnSetOwner = economysection.getBoolean("RefundClaimPriceOnSetOwner", false);
			tempPlotInfo.ClaimPrice = economysection.getDouble("ClaimPrice", 0);
			tempPlotInfo.ClearPrice = economysection.getDouble("ClearPrice", 0);
			tempPlotInfo.AddPlayerPrice = economysection.getDouble("AddPlayerPrice", 0);
			tempPlotInfo.DenyPlayerPrice = economysection.getDouble("DenyPlayerPrice", 0);
			tempPlotInfo.RemovePlayerPrice = economysection.getDouble("RemovePlayerPrice", 0);
			tempPlotInfo.UndenyPlayerPrice = economysection.getDouble("UndenyPlayerPrice", 0);
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

			currworld.set("WallBlockId", getBlockValueId(tempPlotInfo.WallBlockId, tempPlotInfo.WallBlockValue));
			currworld.set("PlotFloorBlockId", getBlockValueId(tempPlotInfo.PlotFloorBlockId, tempPlotInfo.PlotFloorBlockValue));
			currworld.set("PlotFillingBlockId", getBlockValueId(tempPlotInfo.PlotFillingBlockId, tempPlotInfo.PlotFillingBlockValue));
			currworld.set("RoadMainBlockId", getBlockValueId(tempPlotInfo.RoadMainBlockId, tempPlotInfo.RoadMainBlockValue));
			currworld.set("RoadStripeBlockId", getBlockValueId(tempPlotInfo.RoadStripeBlockId, tempPlotInfo.RoadStripeBlockValue));

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
			economysection.set("DenyPlayerPrice", tempPlotInfo.DenyPlayerPrice);
			economysection.set("RemovePlayerPrice", tempPlotInfo.RemovePlayerPrice);
			economysection.set("UndenyPlayerPrice", tempPlotInfo.UndenyPlayerPrice);
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
		config.set("globalUseEconomy", globalUseEconomy);
		config.set("Language", language);
		config.set("allowWorldTeleport", allowWorldTeleport);
		config.set("allowToDeny", allowToDeny);

		try {
			config.save(configfile);
		} catch (IOException e) {
			logger.severe(PREFIX + "error writting configurations");
			e.printStackTrace();
		}

		loadCaptions();
	}

	private void setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
	}

	public static int getPlotLimit(Player p) {
		int max = -2;

		int maxlimit = 255;

		if (p.hasPermission("plotme.limit.*")) {
			return -1;
		}
		for (int ctr = 0; ctr < maxlimit; ctr++) {
			if (p.hasPermission("plotme.limit." + ctr)) {
				max = ctr;
			}
		}

		if (max == -2) {
			if (p.hasPermission("plotme.admin")) {
				return -1;
			} else if (p.hasPermission("plotme.use")) {
				return 1;
			} else {
				return 0;
			}
		}

		return max;
	}

	public static String getDate() {
		return getDate(Calendar.getInstance());
	}

	private static String getDate(Calendar cal) {
		int imonth = cal.get(Calendar.MONTH) + 1;
		int iday = cal.get(Calendar.DAY_OF_MONTH) + 1;
		String month;
		String day;

		if (imonth < 10) {
			month = "0" + imonth;
		} else {
			month = String.valueOf(imonth);
		}

		if (iday < 10) {
			day = "0" + iday;
		} else {
			day = String.valueOf(iday);
		}

		return cal.get(Calendar.YEAR) + "-" + month + "-" + day;
	}

	public static String getDate(java.sql.Date expireddate) {
		return expireddate.toString();
	}

	@SuppressWarnings("deprecation")
	private List<Integer> getDefaultProtectedBlocks() {
		List<Integer> protections = new ArrayList<>();

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
		protections.add(Material.DISPENSER.getId());
		protections.add(Material.DROPPER.getId());
		protections.add(Material.HOPPER.getId());

		return protections;
	}

	@SuppressWarnings("deprecation")
	private List<String> getDefaultPreventedItems() {
		List<String> preventeditems = new ArrayList<>();

		preventeditems.add("" + Material.INK_SACK.getId() + ":15");
		preventeditems.add("" + Material.FLINT_AND_STEEL.getId());
		preventeditems.add("" + Material.MINECART.getId());
		preventeditems.add("" + Material.POWERED_MINECART.getId());
		preventeditems.add("" + Material.STORAGE_MINECART.getId());
		preventeditems.add("" + Material.HOPPER_MINECART.getId());
		preventeditems.add("" + Material.BOAT.getId());

		return preventeditems;
	}

	public void scheduleTask(Runnable task) {
		PlotMe.cscurrentlyprocessingexpired.sendMessage(PlotMe.PREFIX + ChatColor.RESET + caption("MsgStartDeleteSession"));

		for (int ctr = 0; ctr < (50 / nbperdeletionprocessingexpired); ctr++) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, task, ctr * 5 * 20);
		}
	}

	private short getBlockId(ConfigurationSection cs, String section, String def) {
		String idvalue = cs.getString(section, def);
		if (idvalue.indexOf(":") > 0) {
			return Short.parseShort(idvalue.split(":")[0]);
		} else {
			return Short.parseShort(idvalue);
		}
	}

	private byte getBlockValue(ConfigurationSection cs, String section, String def) {
		String idvalue = cs.getString(section, def);
		if (idvalue.indexOf(":") > 0) {
			return Byte.parseByte(idvalue.split(":")[1]);
		} else {
			return 0;
		}
	}

	private String getBlockValueId(Short id, Byte value) {
		return (value == 0) ? id.toString() : id + ":" + value;
	}
}
