package com.worldcretornica.plotme;

import com.worldcretornica.plotme.listener.PlotDenyListener;
import com.worldcretornica.plotme.listener.PlotListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class PlotMe extends JavaPlugin {
	public static final String PREFIX = "[PlotMe] ";

	public static Logger logger;

	public static String configpath;
	public static Boolean globalUseEconomy;
	public static Boolean allowWorldTeleport;
	public static Boolean allowToDeny;
	public static ConcurrentHashMap<String, PlotMapInfo> plotmaps = null;
	public static Economy economy = null;
	public static World worldcurrentlyprocessingexpired;
	public static CommandSender cscurrentlyprocessingexpired;
	public static Integer counterexpired;
	public static Integer nbperdeletionprocessingexpired;
	protected static PlotMe self;
	Boolean initialized = false;
	private File captionFile = new File(getDataFolder(), "captions.yml");
	private FileConfiguration captionsConfig;

	public static boolean cPerms(CommandSender sender, String node) {
		return sender.hasPermission(node);
	}

	public static int getPlotLimit(Player p) {
		int max = -2;

		int maxlimit = 255;

		if (p.hasPermission("plotme.limit.*")) {
			return -1;
		} else {
			for (int ctr = 0; ctr < maxlimit; ctr++) {
				if (p.hasPermission("plotme.limit." + ctr)) {
					max = ctr;
				}
			}

		}

		if (max == -2) {
			if (cPerms(p, "plotme.admin")) {
				return -1;
			} else if (cPerms(p, "plotme.use")) {
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

	public static String caption(String s) {
		return self.getCaptionFile().getString(s);
	}

	public FileConfiguration getCaptionFile() {
		if (captionsConfig == null) {
			reloadCaptionFile();
		}
		return captionsConfig;
	}

	private void reloadCaptionFile() {
		if (captionFile == null) {
			captionFile = new File(getDataFolder(), "captions.yml");
		}
		captionsConfig = YamlConfiguration.loadConfiguration(captionFile);

		// Look for defaults in the jar
		InputStream defConfigStream = this.getResource("captions.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			captionsConfig.setDefaults(defConfig);
		}
	}

	public void onDisable() {
		SqlManager.closeConnection();

		globalUseEconomy = null;
		allowWorldTeleport = null;
		plotmaps = null;
		configpath = null;
		economy = null;
		worldcurrentlyprocessingexpired = null;
		cscurrentlyprocessingexpired = null;
		counterexpired = null;
		nbperdeletionprocessingexpired = null;
		self = null;
		allowToDeny = null;
		initialized = null;
	}

	public void onEnable() {
		self = this;
		logger = getLogger();
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

	public ChunkGenerator getDefaultWorldGenerator(String worldname, String id) {
		if (PlotManager.isPlotWorld(worldname)) {
			return new PlotGen(PlotManager.getMap(worldname));
		} else {
			logger.warning("Configuration not found for PlotMe world '" + worldname + "' Using defaults");
			return new PlotGen();
		}
	}

	public void initialize() {
		configpath = getDataFolder().getAbsolutePath();

		if (!this.getDataFolder().exists()) {
			//noinspection ResultOfMethodCallIgnored
			this.getDataFolder().mkdirs();
		}
		saveDefaultConfig();
		FileConfiguration config = getConfig();

		globalUseEconomy = config.getBoolean("globalUseEconomy", false);
		allowWorldTeleport = config.getBoolean("allowWorldTeleport", true);
		allowToDeny = config.getBoolean("allowToDeny", true);

		ConfigurationSection plotworld = config.getConfigurationSection("plotworld");

		plotmaps = new ConcurrentHashMap<>();

		PlotMapInfo tempPlotInfo = new PlotMapInfo();

		tempPlotInfo.PlotAutoLimit = plotworld.getInt("PlotAutoLimit", 100);
		tempPlotInfo.PathWidth = plotworld.getInt("PathWidth", 7);
		tempPlotInfo.PlotSize = plotworld.getInt("PlotSize", 32);

		tempPlotInfo.BottomBlockId = getBlockId(plotworld, "BottomBlockId", "7:0");
		tempPlotInfo.BottomBlockValue = getBlockValue(plotworld, "BottomBlockId", "7:0");
		tempPlotInfo.WallBlockId = getBlockId(plotworld, "WallBlockId", "44:0");
		tempPlotInfo.WallBlockValue = getBlockValue(plotworld, "WallBlockId", "44:0");
		tempPlotInfo.PlotFloorBlockId = getBlockId(plotworld, "PlotFloorBlockId", "2:0");
		tempPlotInfo.PlotFloorBlockValue = getBlockValue(plotworld, "PlotFloorBlockId", "2:0");
		tempPlotInfo.PlotFillingBlockId = getBlockId(plotworld, "PlotFillingBlockId", "3:0");
		tempPlotInfo.PlotFillingBlockValue = getBlockValue(plotworld, "PlotFillingBlockId", "3:0");
		tempPlotInfo.RoadMainBlockId = getBlockId(plotworld, "RoadMainBlockId", "5:0");
		tempPlotInfo.RoadMainBlockValue = getBlockValue(plotworld, "RoadMainBlockId", "5:0");
		tempPlotInfo.RoadStripeBlockId = getBlockId(plotworld, "RoadStripeBlockId", "5:2");
		tempPlotInfo.RoadStripeBlockValue = getBlockValue(plotworld, "RoadStripeBlockId", "5:2");

		tempPlotInfo.RoadHeight = plotworld.getInt("RoadHeight", 64);
		if (tempPlotInfo.RoadHeight > 250) {
			logger.severe("RoadHeight above 250 is unsafe. This is the height at which your road is located. Setting it to 64.");
			tempPlotInfo.RoadHeight = 64;
		}
		tempPlotInfo.DaysToExpiration = plotworld.getInt("DaysToExpiration", 7);

		if (plotworld.contains("ProtectedBlocks")) {
			tempPlotInfo.ProtectedBlocks = plotworld.getIntegerList("ProtectedBlocks");
		} else {
			tempPlotInfo.ProtectedBlocks = getDefaultProtectedBlocks();
		}

		if (plotworld.contains("PreventedItems")) {
			tempPlotInfo.PreventedItems = plotworld.getStringList("PreventedItems");
		} else {
			tempPlotInfo.PreventedItems = getDefaultPreventedItems();
		}

		tempPlotInfo.ProtectedWallBlockId = plotworld.getString("ProtectedWallBlockId", "44:4");
		tempPlotInfo.ForSaleWallBlockId = plotworld.getString("ForSaleWallBlockId", "44:1");
		tempPlotInfo.AuctionWallBlockId = plotworld.getString("AuctionWallBlockId", "44:1");
		tempPlotInfo.AutoLinkPlots = plotworld.getBoolean("AutoLinkPlots", true);
		tempPlotInfo.DisableExplosion = plotworld.getBoolean("DisableExplosion", true);
		tempPlotInfo.DisableIgnition = plotworld.getBoolean("DisableIgnition", true);

		ConfigurationSection economysection = config.getConfigurationSection("economy");

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

		tempPlotInfo.plots = SqlManager.getPlots();

		plotmaps.put("plotworld", tempPlotInfo);

		loadCaptions();
	}

	private void setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
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
		PlotMe.cscurrentlyprocessingexpired.sendMessage(PlotMe.PREFIX + caption("MsgStartDeleteSession"));

		for (int ctr = 0; ctr < (50 / nbperdeletionprocessingexpired); ctr++) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, task, ctr * 100);
		}
	}

	private void loadCaptions() {
		createCapFile();
		getCaptionFile();
	}

	private void createCapFile() {
		if (!captionFile.exists()) {
			this.saveResource("captions.yml", false);
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

}
