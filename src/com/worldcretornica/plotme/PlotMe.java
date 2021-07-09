package com.worldcretornica.plotme;

import javax.annotation.Nonnull;
import org.bukkit.block.data.BlockData;
import com.griefcraft.model.Protection;
import com.griefcraft.lwc.LWC;
import org.bukkit.block.Block;
import org.bukkit.Location;
import java.util.Map;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.util.TreeMap;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Calendar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import java.util.stream.Collectors;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import java.io.FileNotFoundException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.generator.ChunkGenerator;
import java.io.IOException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.command.CommandExecutor;
import com.worldcretornica.plotme.listener.PlotDenyListener;
import com.worldcretornica.plotme.listener.PlotWorldEditListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import com.worldcretornica.plotme.listener.PlotListener;
import org.bukkit.command.CommandSender;
import org.bukkit.World;
import java.util.HashMap;
import java.util.HashSet;
import net.milkbowl.vault.economy.Economy;
import com.worldcretornica.plotme.worldedit.PlotWorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class PlotMe extends JavaPlugin {
    public static String NAME;
    public static String PREFIX;
    public static Logger logger;
    public static Boolean usemySQL;
    public static String mySQLuname;
    public static String mySQLpass;
    public static String mySQLconn;
    public static String configpath;
    public static Boolean globalUseEconomy;
    public static Boolean advancedlogging;
    public static String language;
    public static Boolean allowWorldTeleport;
    public static Boolean autoUpdate;
    public static Boolean allowToDeny;
    public static ConcurrentHashMap<String, PlotMapInfo> plotmaps;
    public static WorldEditPlugin worldeditplugin;
    public static PlotWorldEdit plotworldedit;
    public static Economy economy;
    public static Boolean usinglwc;
    private static HashSet<String> playersignoringwelimit;
    private static HashMap<String, String> captions;
    public static World worldcurrentlyprocessingexpired;
    public static CommandSender cscurrentlyprocessingexpired;
    public static Integer counterexpired;
    public static Integer nbperdeletionprocessingexpired;
    public static Boolean defaultWEAnywhere;
    protected static PlotMe self;
    Boolean initialized;
    
    public PlotMe() {
        this.initialized = false;
    }
    
    public void onDisable() {
        SqlManager.closeConnection();
        PlotMe.NAME = null;
        PlotMe.PREFIX = null;
        PlotMe.logger = null;
        PlotMe.usemySQL = null;
        PlotMe.mySQLuname = null;
        PlotMe.mySQLpass = null;
        PlotMe.mySQLconn = null;
        PlotMe.globalUseEconomy = null;
        PlotMe.advancedlogging = null;
        PlotMe.language = null;
        PlotMe.allowWorldTeleport = null;
        PlotMe.autoUpdate = null;
        PlotMe.plotmaps = null;
        PlotMe.configpath = null;
        PlotMe.worldeditplugin = null;
        PlotMe.economy = null;
        PlotMe.usinglwc = null;
        PlotMe.playersignoringwelimit = null;
        PlotMe.captions = null;
        PlotMe.worldcurrentlyprocessingexpired = null;
        PlotMe.cscurrentlyprocessingexpired = null;
        PlotMe.counterexpired = null;
        PlotMe.nbperdeletionprocessingexpired = null;
        PlotMe.defaultWEAnywhere = null;
        PlotMe.self = null;
        PlotMe.allowToDeny = null;
        this.initialized = null;
    }
    
    public void onEnable() {
        PlotMe.self = this;
        PlotMe.logger = this.getLogger();
        this.initialize();
        this.doMetric();
        final PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents((Listener)new PlotListener(), (Plugin)this);
        if (pm.getPlugin("Vault") != null) {
            this.setupEconomy();
        }
        if (pm.getPlugin("WorldEdit") != null) {
            PlotMe.worldeditplugin = (WorldEditPlugin)pm.getPlugin("WorldEdit");
            try {
                Class.forName("com.sk89q.worldedit.function.mask.Mask");
                PlotMe.plotworldedit = (PlotWorldEdit)Class.forName("com.worldcretornica.plotme.worldedit.PlotWorldEdit6_0_0").getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception e) {
                try {
                    PlotMe.plotworldedit = (PlotWorldEdit)Class.forName("com.worldcretornica.plotme.worldedit.PlotWorldEdit5_7").getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                catch (Exception e2) {
                    PlotMe.logger.warning("Unable to hook to WorldEdit properly, please contact the developper of plotme with your WorldEdit version.");
                    PlotMe.plotworldedit = null;
                }
            }
            pm.registerEvents((Listener)new PlotWorldEditListener(), (Plugin)this);
        }
        if (pm.getPlugin("LWC") != null) {
            PlotMe.usinglwc = true;
        }
        if (PlotMe.allowToDeny) {
            pm.registerEvents((Listener)new PlotDenyListener(), (Plugin)this);
        }
        this.getCommand("plotme").setExecutor((CommandExecutor)new PMCommand(this));
        this.initialized = true;
        SqlManager.plotConvertToUUIDAsynchronously();
    }
    
    private void doMetric() {
        try {
            final Metrics metrics = new Metrics((Plugin)this);
            final Metrics.Graph graphNbWorlds = metrics.createGraph("Plot worlds");
            graphNbWorlds.addPlotter(new Metrics.Plotter("Number of plot worlds") {
                @Override
                public int getValue() {
                    return PlotMe.plotmaps.size();
                }
            });
            graphNbWorlds.addPlotter(new Metrics.Plotter("Average Plot size") {
                @Override
                public int getValue() {
                    if (PlotMe.plotmaps.size() > 0) {
                        int totalplotsize = 0;
                        for (final PlotMapInfo p : PlotMe.plotmaps.values()) {
                            totalplotsize += p.PlotSize;
                        }
                        return totalplotsize / PlotMe.plotmaps.size();
                    }
                    return 0;
                }
            });
            graphNbWorlds.addPlotter(new Metrics.Plotter("Number of plots") {
                @Override
                public int getValue() {
                    int nbplot = 0;
                    for (final PlotMapInfo p : PlotMe.plotmaps.values()) {
                        nbplot += p.plots.size();
                    }
                    return nbplot;
                }
            });
            metrics.start();
        }
        catch (IOException ex) {}
    }
    
    public ChunkGenerator getDefaultWorldGenerator(final String worldname, final String id) {
        if (PlotManager.isPlotWorld(worldname)) {
            return new PlotGen(PlotManager.getMap(worldname));
        }
        PlotMe.logger.warning("Configuration not found for PlotMe world '" + worldname + "' Using defaults");
        return new PlotGen();
    }
    
    public static boolean cPerms(final CommandSender sender, final String node) {
        return sender.hasPermission(node);
    }
    
    public void initialize() {
        final PluginDescriptionFile pdfFile = this.getDescription();
        PlotMe.NAME = pdfFile.getName();
        PlotMe.PREFIX = new StringBuilder().append(ChatColor.BLUE).append("[").append(PlotMe.NAME).append("] ").append(ChatColor.RESET).toString();
        PlotMe.configpath = this.getDataFolder().getAbsolutePath();
        PlotMe.playersignoringwelimit = new HashSet<String>();
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
        final File configfile = new File(PlotMe.configpath, "config.yml");
        final FileConfiguration config = (FileConfiguration)new YamlConfiguration();
        try {
            config.load(configfile);
        }
        catch (FileNotFoundException ex) {}
        catch (IOException e) {
            PlotMe.logger.severe("Can't read configuration file");
            e.printStackTrace();
        }
        catch (InvalidConfigurationException e2) {
            PlotMe.logger.severe("Invalid configuration format");
            e2.printStackTrace();
        }
        PlotMe.usemySQL = config.getBoolean("usemySQL", false);
        PlotMe.mySQLconn = config.getString("mySQLconn", "jdbc:mysql://localhost:3306/minecraft");
        PlotMe.mySQLuname = config.getString("mySQLuname", "root");
        PlotMe.mySQLpass = config.getString("mySQLpass", "password");
        PlotMe.globalUseEconomy = config.getBoolean("globalUseEconomy", false);
        PlotMe.advancedlogging = config.getBoolean("AdvancedLogging", false);
        PlotMe.language = config.getString("Language", "english");
        PlotMe.allowWorldTeleport = config.getBoolean("allowWorldTeleport", true);
        PlotMe.defaultWEAnywhere = config.getBoolean("defaultWEAnywhere", false);
        PlotMe.autoUpdate = config.getBoolean("auto-update", false);
        PlotMe.allowToDeny = config.getBoolean("allowToDeny", true);
        ConfigurationSection worlds;
        if (!config.contains("worlds")) {
            worlds = config.createSection("worlds");
            final ConfigurationSection plotworld = worlds.createSection("plotworld");
            plotworld.set("PlotAutoLimit", 1000);
            plotworld.set("PathWidth", 7);
            plotworld.set("PlotSize", 32);
            plotworld.set("BottomBlockId", this.matToString(Material.BEDROCK));
            plotworld.set("WallBlockId", this.matToString(Material.SMOOTH_STONE_SLAB));
            plotworld.set("PlotFloorBlockId", this.matToString(Material.GRASS_BLOCK));
            plotworld.set("PlotFillingBlockId", this.matToString(Material.DIRT));
            plotworld.set("RoadMainBlockId", this.matToString(Material.OAK_PLANKS));
            plotworld.set("RoadStripeBlockId", this.matToString(Material.BIRCH_PLANKS));
            plotworld.set("RoadHeight", 64);
            plotworld.set("DaysToExpiration", 7);
            plotworld.set("ProtectedBlocks", this.getDefaultProtectedBlocks());
            plotworld.set("PreventedItems", this.getDefaultPreventedItems());
            plotworld.set("ProtectedWallBlockId", this.matToString(Material.STONE_BRICKS));
            plotworld.set("ForSaleWallBlockId", this.matToString(Material.SANDSTONE_SLAB));
            plotworld.set("AuctionWallBlockId", this.matToString(Material.SANDSTONE_SLAB));
            plotworld.set("AutoLinkPlots", true);
            plotworld.set("DisableExplosion", true);
            plotworld.set("DisableIgnition", true);
            final ConfigurationSection economysection = plotworld.createSection("economy");
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
        }
        else {
            worlds = config.getConfigurationSection("worlds");
        }
        PlotMe.plotmaps = new ConcurrentHashMap<String, PlotMapInfo>();
        for (final String worldname : worlds.getKeys(false)) {
            final PlotMapInfo tempPlotInfo = new PlotMapInfo();
            final ConfigurationSection currworld = worlds.getConfigurationSection(worldname);
            tempPlotInfo.PlotAutoLimit = currworld.getInt("PlotAutoLimit", 100);
            tempPlotInfo.PathWidth = currworld.getInt("PathWidth", 7);
            tempPlotInfo.PlotSize = currworld.getInt("PlotSize", 32);
            tempPlotInfo.BottomBlockId = this.getBlockId(currworld, "BottomBlockId", Material.BEDROCK);
            tempPlotInfo.BottomBlockValue = this.getBlockValue(currworld, "BottomBlockId", Bukkit.getServer().createBlockData(Material.BEDROCK));
            tempPlotInfo.WallBlockId = this.getBlockId(currworld, "WallBlockId", Material.SMOOTH_STONE_SLAB);
            tempPlotInfo.WallBlockValue = this.getBlockValue(currworld, "WallBlockValue", Bukkit.getServer().createBlockData(Material.SMOOTH_STONE_SLAB));
            tempPlotInfo.PlotFloorBlockId = this.getBlockId(currworld, "PlotFloorBlockId", Material.GRASS_BLOCK);
            tempPlotInfo.PlotFloorBlockValue = this.getBlockValue(currworld, "PlotFloorBlockId", Bukkit.getServer().createBlockData(Material.GRASS_BLOCK));
            tempPlotInfo.PlotFillingBlockId = this.getBlockId(currworld, "PlotFillingBlockId", Material.DIRT);
            tempPlotInfo.PlotFillingBlockValue = this.getBlockValue(currworld, "PlotFillingBlockId", Bukkit.getServer().createBlockData(Material.DIRT));
            tempPlotInfo.RoadMainBlockId = this.getBlockId(currworld, "RoadMainBlockId", Material.OAK_PLANKS);
            tempPlotInfo.RoadMainBlockValue = this.getBlockValue(currworld, "RoadMainBlockId", Bukkit.getServer().createBlockData(Material.OAK_PLANKS));
            tempPlotInfo.RoadStripeBlockId = this.getBlockId(currworld, "RoadStripeBlockId", Material.BIRCH_PLANKS);
            tempPlotInfo.RoadStripeBlockValue = this.getBlockValue(currworld, "RoadStripeBlockId", Bukkit.getServer().createBlockData(Material.BIRCH_PLANKS));
            tempPlotInfo.RoadHeight = currworld.getInt("RoadHeight", currworld.getInt("WorldHeight", 64));
            if (tempPlotInfo.RoadHeight > 250) {
                PlotMe.logger.severe("RoadHeight above 250 is unsafe. This is the height at which your road is located. Setting it to 64.");
                tempPlotInfo.RoadHeight = 64;
            }
            tempPlotInfo.DaysToExpiration = currworld.getInt("DaysToExpiration", 7);
            if (currworld.contains("ProtectedBlocks")) {
                tempPlotInfo.ProtectedBlocks = this.stringToMaterial((List<String>)currworld.getStringList("ProtectedBlocks"));
            }
            else {
                tempPlotInfo.ProtectedBlocks = this.getDefaultProtectedBlocks();
            }
            if (currworld.contains("PreventedItems")) {
                tempPlotInfo.PreventedItems = this.stringToMaterial((List<String>)currworld.getStringList("PreventedItems"));
            }
            else {
                tempPlotInfo.PreventedItems = this.getDefaultPreventedItems();
            }
            tempPlotInfo.ProtectedWallBlockId = this.stringToMaterial(currworld.getString("ProtectedWallBlockId", this.matToString(Material.STONE_BRICKS)));
            tempPlotInfo.ForSaleWallBlockId = this.stringToMaterial(currworld.getString("ForSaleWallBlockId", this.matToString(Material.OAK_PLANKS)));
            tempPlotInfo.AuctionWallBlockId = this.stringToMaterial(currworld.getString("AuctionWallBlockId", this.matToString(Material.OAK_PLANKS)));
            tempPlotInfo.AutoLinkPlots = currworld.getBoolean("AutoLinkPlots", true);
            tempPlotInfo.DisableExplosion = currworld.getBoolean("DisableExplosion", true);
            tempPlotInfo.DisableIgnition = currworld.getBoolean("DisableIgnition", true);
            ConfigurationSection economysection2;
            if (currworld.getConfigurationSection("economy") == null) {
                economysection2 = currworld.createSection("economy");
            }
            else {
                economysection2 = currworld.getConfigurationSection("economy");
            }
            tempPlotInfo.UseEconomy = economysection2.getBoolean("UseEconomy", false);
            tempPlotInfo.CanPutOnSale = economysection2.getBoolean("CanPutOnSale", false);
            tempPlotInfo.CanSellToBank = economysection2.getBoolean("CanSellToBank", false);
            tempPlotInfo.RefundClaimPriceOnReset = economysection2.getBoolean("RefundClaimPriceOnReset", false);
            tempPlotInfo.RefundClaimPriceOnSetOwner = economysection2.getBoolean("RefundClaimPriceOnSetOwner", false);
            tempPlotInfo.ClaimPrice = economysection2.getDouble("ClaimPrice", 0.0);
            tempPlotInfo.ClearPrice = economysection2.getDouble("ClearPrice", 0.0);
            tempPlotInfo.AddPlayerPrice = economysection2.getDouble("AddPlayerPrice", 0.0);
            tempPlotInfo.DenyPlayerPrice = economysection2.getDouble("DenyPlayerPrice", 0.0);
            tempPlotInfo.RemovePlayerPrice = economysection2.getDouble("RemovePlayerPrice", 0.0);
            tempPlotInfo.UndenyPlayerPrice = economysection2.getDouble("UndenyPlayerPrice", 0.0);
            tempPlotInfo.PlotHomePrice = economysection2.getDouble("PlotHomePrice", 0.0);
            tempPlotInfo.CanCustomizeSellPrice = economysection2.getBoolean("CanCustomizeSellPrice", false);
            tempPlotInfo.SellToPlayerPrice = economysection2.getDouble("SellToPlayerPrice", 0.0);
            tempPlotInfo.SellToBankPrice = economysection2.getDouble("SellToBankPrice", 0.0);
            tempPlotInfo.BuyFromBankPrice = economysection2.getDouble("BuyFromBankPrice", 0.0);
            tempPlotInfo.AddCommentPrice = economysection2.getDouble("AddCommentPrice", 0.0);
            tempPlotInfo.BiomeChangePrice = economysection2.getDouble("BiomeChangePrice", 0.0);
            tempPlotInfo.ProtectPrice = economysection2.getDouble("ProtectPrice", 0.0);
            tempPlotInfo.DisposePrice = economysection2.getDouble("DisposePrice", 0.0);
            currworld.set("PlotAutoLimit", tempPlotInfo.PlotAutoLimit);
            currworld.set("PathWidth", tempPlotInfo.PathWidth);
            currworld.set("PlotSize", tempPlotInfo.PlotSize);
            currworld.set("BottomBlockId", this.getBlockValueId(tempPlotInfo.BottomBlockId, tempPlotInfo.BottomBlockValue));
            currworld.set("WallBlockId", this.getBlockValueId(tempPlotInfo.WallBlockId, tempPlotInfo.WallBlockValue));
            currworld.set("PlotFloorBlockId", this.getBlockValueId(tempPlotInfo.PlotFloorBlockId, tempPlotInfo.PlotFloorBlockValue));
            currworld.set("PlotFillingBlockId", this.getBlockValueId(tempPlotInfo.PlotFillingBlockId, tempPlotInfo.PlotFillingBlockValue));
            currworld.set("RoadMainBlockId", this.getBlockValueId(tempPlotInfo.RoadMainBlockId, tempPlotInfo.RoadMainBlockValue));
            currworld.set("RoadStripeBlockId", this.getBlockValueId(tempPlotInfo.RoadStripeBlockId, tempPlotInfo.RoadStripeBlockValue));
            currworld.set("RoadHeight", tempPlotInfo.RoadHeight);
            currworld.set("WorldHeight", null);
            currworld.set("DaysToExpiration", tempPlotInfo.DaysToExpiration);
            currworld.set("ProtectedBlocks", this.matToString(tempPlotInfo.ProtectedBlocks));
            currworld.set("PreventedItems", this.matToString(tempPlotInfo.PreventedItems));
            currworld.set("ProtectedWallBlockId", this.matToString(tempPlotInfo.ProtectedWallBlockId));
            currworld.set("ForSaleWallBlockId", this.matToString(tempPlotInfo.ForSaleWallBlockId));
            currworld.set("AuctionWallBlockId", this.matToString(tempPlotInfo.AuctionWallBlockId));
            currworld.set("AutoLinkPlots", tempPlotInfo.AutoLinkPlots);
            currworld.set("DisableExplosion", tempPlotInfo.DisableExplosion);
            currworld.set("DisableIgnition", tempPlotInfo.DisableIgnition);
            economysection2 = currworld.createSection("economy");
            economysection2.set("UseEconomy", tempPlotInfo.UseEconomy);
            economysection2.set("CanPutOnSale", tempPlotInfo.CanPutOnSale);
            economysection2.set("CanSellToBank", tempPlotInfo.CanSellToBank);
            economysection2.set("RefundClaimPriceOnReset", tempPlotInfo.RefundClaimPriceOnReset);
            economysection2.set("RefundClaimPriceOnSetOwner", tempPlotInfo.RefundClaimPriceOnSetOwner);
            economysection2.set("ClaimPrice", tempPlotInfo.ClaimPrice);
            economysection2.set("ClearPrice", tempPlotInfo.ClearPrice);
            economysection2.set("AddPlayerPrice", tempPlotInfo.AddPlayerPrice);
            economysection2.set("DenyPlayerPrice", tempPlotInfo.DenyPlayerPrice);
            economysection2.set("RemovePlayerPrice", tempPlotInfo.RemovePlayerPrice);
            economysection2.set("UndenyPlayerPrice", tempPlotInfo.UndenyPlayerPrice);
            economysection2.set("PlotHomePrice", tempPlotInfo.PlotHomePrice);
            economysection2.set("CanCustomizeSellPrice", tempPlotInfo.CanCustomizeSellPrice);
            economysection2.set("SellToPlayerPrice", tempPlotInfo.SellToPlayerPrice);
            economysection2.set("SellToBankPrice", tempPlotInfo.SellToBankPrice);
            economysection2.set("BuyFromBankPrice", tempPlotInfo.BuyFromBankPrice);
            economysection2.set("AddCommentPrice", tempPlotInfo.AddCommentPrice);
            economysection2.set("BiomeChangePrice", tempPlotInfo.BiomeChangePrice);
            economysection2.set("ProtectPrice", tempPlotInfo.ProtectPrice);
            economysection2.set("DisposePrice", tempPlotInfo.DisposePrice);
            currworld.set("economy", economysection2);
            worlds.set(worldname, currworld);
            tempPlotInfo.plots = SqlManager.getPlots(worldname.toLowerCase());
            PlotMe.plotmaps.put(worldname.toLowerCase(), tempPlotInfo);
        }
        config.set("usemySQL", PlotMe.usemySQL);
        config.set("mySQLconn", PlotMe.mySQLconn);
        config.set("mySQLuname", PlotMe.mySQLuname);
        config.set("mySQLpass", PlotMe.mySQLpass);
        config.set("globalUseEconomy", PlotMe.globalUseEconomy);
        config.set("AdvancedLogging", PlotMe.advancedlogging);
        config.set("Language", PlotMe.language);
        config.set("allowWorldTeleport", PlotMe.allowWorldTeleport);
        config.set("defaultWEAnywhere", PlotMe.defaultWEAnywhere);
        config.set("auto-update", PlotMe.autoUpdate);
        config.set("allowToDeny", PlotMe.allowToDeny);
        try {
            config.save(configfile);
        }
        catch (IOException e3) {
            PlotMe.logger.severe("Error writing configurations");
            e3.printStackTrace();
        }
        this.loadCaptions();
    }
    
    private List<Material> stringToMaterial(final List<String> stringList) {
        return (List<Material>)stringList.stream().map(string -> Material.getMaterial(string.toUpperCase())).collect(Collectors.toList());
    }
    
    private List<String> matToString(final List<Material> matList) {
        return (List<String>)matList.stream().map(mat -> mat.getKey().getKey()).collect(Collectors.toList());
    }
    
    private Material stringToMaterial(final String string) {
        return Material.getMaterial(string.toUpperCase());
    }
    
    private String matToString(final Material mat) {
        return mat.getKey().getKey();
    }
    
    private void setupEconomy() {
        final RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            PlotMe.economy = (Economy)economyProvider.getProvider();
        }
    }
    
    public static void addIgnoreWELimit(final Player p) {
        if (!PlotMe.playersignoringwelimit.contains(p.getName())) {
            PlotMe.playersignoringwelimit.add(p.getName());
            if (PlotMe.worldeditplugin != null) {
                PlotMe.plotworldedit.removeMask(p);
            }
        }
    }
    
    public static void removeIgnoreWELimit(final Player p) {
        if (PlotMe.playersignoringwelimit.contains(p.getName())) {
            PlotMe.playersignoringwelimit.remove(p.getName());
            if (PlotMe.worldeditplugin != null) {
                PlotMe.plotworldedit.setMask(p);
            }
        }
    }
    
    public static boolean isIgnoringWELimit(final Player p) {
        if (PlotMe.defaultWEAnywhere && cPerms((CommandSender)p, "PlotMe.admin.weanywhere")) {
            return !PlotMe.playersignoringwelimit.contains(p.getName());
        }
        return PlotMe.playersignoringwelimit.contains(p.getName());
    }
    
    public static int getPlotLimit(final Player p) {
        int max = -2;
        final int maxlimit = 255;
        if (p.hasPermission("plotme.limit.*")) {
            return -1;
        }
        for (int ctr = 0; ctr < maxlimit; ++ctr) {
            if (p.hasPermission(new StringBuilder().append("plotme.limit.").append(ctr).toString())) {
                max = ctr;
            }
        }
        if (max != -2) {
            return max;
        }
        if (cPerms((CommandSender)p, "plotme.admin")) {
            return -1;
        }
        if (cPerms((CommandSender)p, "plotme.use")) {
            return 1;
        }
        return 0;
    }
    
    public static String getDate() {
        return getDate(Calendar.getInstance());
    }
    
    private static String getDate(final Calendar cal) {
        final int imonth = cal.get(2) + 1;
        final int iday = cal.get(5) + 1;
        String month = "";
        String day = "";
        if (imonth < 10) {
            month = new StringBuilder().append("0").append(imonth).toString();
        }
        else {
            month = new StringBuilder().append("").append(imonth).toString();
        }
        if (iday < 10) {
            day = new StringBuilder().append("0").append(iday).toString();
        }
        else {
            day = new StringBuilder().append("").append(iday).toString();
        }
        return new StringBuilder().append("").append(cal.get(1)).append("-").append(month).append("-").append(day).toString();
    }
    
    public static String getDate(final Date expireddate) {
        return expireddate.toString();
    }
    
    private List<Material> getDefaultProtectedBlocks() {
        final List<Material> protections = new ArrayList<Material>();
        protections.add(Material.CHEST);
        protections.add(Material.FURNACE);
        protections.add(Material.END_PORTAL_FRAME);
        protections.add(Material.JUKEBOX);
        protections.add(Material.NOTE_BLOCK);
        protections.add(Material.BLUE_BED);
        protections.add(Material.BLACK_BED);
        protections.add(Material.RED_BED);
        protections.add(Material.GREEN_BED);
        protections.add(Material.PURPLE_BED);
        protections.add(Material.WHITE_BED);
        protections.add(Material.BROWN_BED);
        protections.add(Material.PINK_BED);
        protections.add(Material.CYAN_BED);
        protections.add(Material.LIGHT_BLUE_BED);
        protections.add(Material.LIGHT_GRAY_BED);
        protections.add(Material.LIME_BED);
        protections.add(Material.YELLOW_BED);
        protections.add(Material.ORANGE_BED);
        protections.add(Material.GRAY_BED);
        protections.add(Material.MAGENTA_BED);
        protections.add(Material.CAULDRON);
        protections.add(Material.BREWING_STAND);
        protections.add(Material.BEACON);
        protections.add(Material.FLOWER_POT);
        protections.add(Material.ANVIL);
        protections.add(Material.DISPENSER);
        protections.add(Material.DROPPER);
        protections.add(Material.HOPPER);
        return protections;
    }
    
    private List<Material> getDefaultPreventedItems() {
        final List<Material> preventeditems = new ArrayList<Material>();
        preventeditems.add(Material.INK_SAC);
        preventeditems.add(Material.FLINT_AND_STEEL);
        preventeditems.add(Material.MINECART);
        preventeditems.add(Material.FURNACE_MINECART);
        preventeditems.add(Material.CHEST_MINECART);
        preventeditems.add(Material.HOPPER_MINECART);
        preventeditems.add(Material.OAK_BOAT);
        preventeditems.add(Material.DARK_OAK_BOAT);
        preventeditems.add(Material.BIRCH_BOAT);
        preventeditems.add(Material.SPRUCE_BOAT);
        preventeditems.add(Material.JUNGLE_BOAT);
        preventeditems.add(Material.ACACIA_BOAT);
        return preventeditems;
    }
    
    public void scheduleTask(final Runnable task, final int eachseconds, final int howmanytimes) {
        PlotMe.cscurrentlyprocessingexpired.sendMessage("" + PlotMe.PREFIX + ChatColor.RESET + caption("MsgStartDeleteSession"));
        for (int ctr = 0; ctr < howmanytimes / PlotMe.nbperdeletionprocessingexpired; ++ctr) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this, task, (long)(ctr * eachseconds * 20));
        }
    }
    
    private void loadCaptions() {
        File filelang = new File(this.getDataFolder(), "caption-english.yml");
        final TreeMap<String, String> properties = new TreeMap<String, String>();
        properties.put("MsgStartDeleteSession", "Starting delete session");
        properties.put("MsgDeletedExpiredPlots", "Deleted expired plot");
        properties.put("MsgDeleteSessionFinished", "Deletion session finished, rerun to reset more plots");
        properties.put("MsgAlreadyProcessingPlots", "is already processing expired plots");
        properties.put("MsgDoesNotExistOrNotLoaded", "does not exist or is not loaded.");
        properties.put("MsgNotPlotWorld", "This is not a plot world.");
        properties.put("MsgPermissionDenied", "Permission denied");
        properties.put("MsgNoPlotFound", "No plot found");
        properties.put("MsgCannotBidOwnPlot", "You cannot bid on your own plot.");
        properties.put("MsgCannotBuyOwnPlot", "You cannot buy your own plot.");
        properties.put("MsgCannotClaimRoad", "You cannot claim the road.");
        properties.put("MsgInvalidBidMustBeAbove", "Invalid bid. Must be above");
        properties.put("MsgOutbidOnPlot", "Outbid on plot");
        properties.put("MsgOwnedBy", "owned by");
        properties.put("MsgBidAccepted", "Bid accepted.");
        properties.put("MsgPlotNotAuctionned", "This plot isn't being auctionned.");
        properties.put("MsgThisPlot", "This plot");
        properties.put("MsgThisPlotYours", "This plot is now yours.");
        properties.put("MsgThisPlotIsNow", "This plot is now ");
        properties.put("MsgThisPlotOwned", "This plot is already owned.");
        properties.put("MsgHasNoOwner", "has no owners.");
        properties.put("MsgEconomyDisabledWorld", "Economy is disabled for this world.");
        properties.put("MsgPlotNotForSale", "Plot isn't for sale.");
        properties.put("MsgAlreadyReachedMaxPlots", "You have already reached your maximum amount of plots");
        properties.put("MsgToGetToIt", "to get to it");
        properties.put("MsgNotEnoughBid", "You do not have enough to bid this much.");
        properties.put("MsgNotEnoughBuy", "You do not have enough to buy this plot.");
        properties.put("MsgNotEnoughAuto", "You do not have enough to buy a plot.");
        properties.put("MsgNotEnoughComment", "You do not have enough to comment on a plot.");
        properties.put("MsgNotEnoughBiome", "You do not have enough to change the biome.");
        properties.put("MsgNotEnoughClear", "You do not have enough to clear the plot.");
        properties.put("MsgNotEnoughDispose", "You do not have enough to dispose of this plot.");
        properties.put("MsgNotEnoughProtectPlot", "You do not have enough to protect this plot.");
        properties.put("MsgNotEnoughTp", "You do not have enough to teleport home.");
        properties.put("MsgNotEnoughAdd", "You do not have enough to add a player.");
        properties.put("MsgNotEnoughDeny", "You do not have enough to deny a player.");
        properties.put("MsgNotEnoughRemove", "You do not have enough to remove a player.");
        properties.put("MsgNotEnoughUndeny", "You do not have enough to undeny a player.");
        properties.put("MsgSoldTo", "sold to");
        properties.put("MsgPlotBought", "Plot bought.");
        properties.put("MsgBoughtPlot", "bought plot");
        properties.put("MsgClaimedPlot", "claimed plot");
        properties.put("MsgPlotHasBidsAskAdmin", "Plot is being auctionned and has bids. Ask an admin to cancel it.");
        properties.put("MsgAuctionCancelledOnPlot", "Auction cancelled on plot");
        properties.put("MsgAuctionCancelled", "Auction cancelled.");
        properties.put("MsgStoppedTheAuctionOnPlot", "stopped the auction on plot");
        properties.put("MsgInvalidAmount", "Invalid amount. Must be above or equal to 0.");
        properties.put("MsgAuctionStarted", "Auction started.");
        properties.put("MsgStartedAuctionOnPlot", "started an auction on plot");
        properties.put("MsgDoNotOwnPlot", "You do not own this plot.");
        properties.put("MsgSellingPlotsIsDisabledWorld", "Selling plots is disabled in this world.");
        properties.put("MsgPlotProtectedNotDisposed", "Plot is protected and cannot be disposed.");
        properties.put("MsgWasDisposed", "was disposed.");
        properties.put("MsgPlotDisposedAnyoneClaim", "Plot disposed. Anyone can claim it.");
        properties.put("MsgDisposedPlot", "disposed of plot");
        properties.put("MsgNotYoursCannotDispose", "is not yours. You are not allowed to dispose it.");
        properties.put("MsgPlotNoLongerSale", "Plot no longer for sale.");
        properties.put("MsgRemovedPlot", "removed the plot");
        properties.put("MsgFromBeingSold", "from being sold");
        properties.put("MsgCannotCustomPriceDefault", "You cannot customize the price. Default price is :");
        properties.put("MsgCannotSellToBank", "Plots cannot be sold to the bank in this world.");
        properties.put("MsgSoldToBank", "sold to bank.");
        properties.put("MsgPlotSold", "Plot sold.");
        properties.put("MsgSoldToBankPlot", "sold to bank plot");
        properties.put("MsgPlotForSale", "Plot now for sale.");
        properties.put("MsgPutOnSalePlot", "put on sale plot");
        properties.put("MsgPlotNoLongerProtected", "Plot is no longer protected. It is now possible to Clear or Reset it.");
        properties.put("MsgUnprotectedPlot", "unprotected plot");
        properties.put("MsgPlotNowProtected", "Plot is now protected. It won't be possible to Clear or Reset it.");
        properties.put("MsgProtectedPlot", "protected plot");
        properties.put("MsgNoPlotsFinished", "No plots are finished");
        properties.put("MsgFinishedPlotsPage", "Finished plots page");
        properties.put("MsgUnmarkFinished", "Plot is no longer marked finished.");
        properties.put("MsgMarkFinished", "Plot is now marked finished.");
        properties.put("MsgPlotExpirationReset", "Plot expiration reset");
        properties.put("MsgNoPlotExpired", "No plots are expired");
        properties.put("MsgExpiredPlotsPage", "Expired plots page");
        properties.put("MsgListOfPlotsWhere", "List of plots where");
        properties.put("MsgCanBuild", "can build:");
        properties.put("MsgListOfPlotsWhereYou", "List of plots where you can build:");
        properties.put("MsgWorldEditInYourPlots", "You can now only WorldEdit in your plots");
        properties.put("MsgWorldEditAnywhere", "You can now WorldEdit anywhere");
        properties.put("MsgNoPlotFound1", "No plot found within");
        properties.put("MsgNoPlotFound2", "plots. Contact an admin.");
        properties.put("MsgDoesNotHavePlot", "does not have a plot");
        properties.put("MsgPlotNotFound", "Could not find plot");
        properties.put("MsgYouHaveNoPlot", "You don't have a plot.");
        properties.put("MsgCommentAdded", "Comment added.");
        properties.put("MsgCommentedPlot", "commented on plot");
        properties.put("MsgNoComments", "No comments");
        properties.put("MsgYouHave", "You have");
        properties.put("MsgComments", "comments.");
        properties.put("MsgNotYoursNotAllowedViewComments", "is not yours. You are not allowed to view the comments.");
        properties.put("MsgIsInvalidBiome", "is not a valid biome.");
        properties.put("MsgBiomeSet", "Biome set to");
        properties.put("MsgChangedBiome", "changed the biome of plot");
        properties.put("MsgNotYoursNotAllowedBiome", "is not yours. You are not allowed to change it's biome.");
        properties.put("MsgPlotUsingBiome", "This plot is using the biome");
        properties.put("MsgPlotProtectedCannotReset", "Plot is protected and cannot be reset.");
        properties.put("MsgPlotProtectedCannotClear", "Plot is protected and cannot be cleared.");
        properties.put("MsgOwnedBy", "owned by");
        properties.put("MsgWasReset", "was reset.");
        properties.put("MsgPlotReset", "Plot has been reset.");
        properties.put("MsgResetPlot", "reset plot");
        properties.put("MsgPlotCleared", "Plot cleared.");
        properties.put("MsgClearedPlot", "cleared plot");
        properties.put("MsgNotYoursNotAllowedClear", "is not yours. You are not allowed to clear it.");
        properties.put("MsgAlreadyAllowed", "was already allowed");
        properties.put("MsgAlreadyDenied", "was already denied");
        properties.put("MsgWasNotAllowed", "was not allowed");
        properties.put("MsgWasNotDenied", "was not denied");
        properties.put("MsgNowUndenied", "now undenied.");
        properties.put("MsgNowDenied", "now denied.");
        properties.put("MsgNowAllowed", "now allowed.");
        properties.put("MsgAddedPlayer", "added player");
        properties.put("MsgDeniedPlayer", "denied player");
        properties.put("MsgRemovedPlayer", "removed player");
        properties.put("MsgUndeniedPlayer", "undenied player");
        properties.put("MsgToPlot", "to plot");
        properties.put("MsgFromPlot", "from plot");
        properties.put("MsgNotYoursNotAllowedAdd", "is not yours. You are not allowed to add someone to it.");
        properties.put("MsgNotYoursNotAllowedDeny", "is not yours. You are not allowed to deny someone from it.");
        properties.put("MsgNotYoursNotAllowedRemove", "is not yours. You are not allowed to remove someone from it.");
        properties.put("MsgNotYoursNotAllowedUndeny", "is not yours. You are not allowed to undeny someone to it.");
        properties.put("MsgNowOwnedBy", "is now owned by");
        properties.put("MsgChangedOwnerFrom", "changed owner from");
        properties.put("MsgChangedOwnerOf", "changed owner of");
        properties.put("MsgOwnerChangedTo", "Plot Owner has been set to");
        properties.put("MsgPlotMovedSuccess", "Plot moved successfully");
        properties.put("MsgExchangedPlot", "exchanged plot");
        properties.put("MsgAndPlot", "and plot");
        properties.put("MsgReloadedSuccess", "reloaded successfully");
        properties.put("MsgReloadedConfigurations", "reloaded configurations");
        properties.put("MsgNoPlotworldFound", "No Plot world found.");
        properties.put("MsgWorldNotPlot", "does not exist or is not a plot world.");
        properties.put("ConsoleHelpMain", " ---==PlotMe Console Help Page==---");
        properties.put("ConsoleHelpReload", " - Reloads the plugin and its configuration files");
        properties.put("HelpTitle", "PlotMe Help Page");
        properties.put("HelpYourPlotLimitWorld", "Your plot limit in this world");
        properties.put("HelpUsedOf", "used of");
        properties.put("HelpClaim", "Claims the current plot you are standing on.");
        properties.put("HelpClaimOther", "Claims the current plot you are standing on for another player.");
        properties.put("HelpAuto", "Claims the next available free plot.");
        properties.put("HelpHome", "Teleports you to your plot, :# if you own multiple plots.");
        properties.put("HelpHomeOther", "Teleports you to other plots, :# if other people own multiple plots.");
        properties.put("HelpInfo", "Displays information about the plot you're standing on.");
        properties.put("HelpComment", "Leave comment on the current plot.");
        properties.put("HelpComments", "Lists all comments users have said about your plot.");
        properties.put("HelpList", "Lists every plot you can build on.");
        properties.put("HelpListOther", "Lists every plot <player> can build on.");
        properties.put("HelpBiomeInfo", "Shows the current biome in the plot.");
        properties.put("HelpBiome", "Changes the plots biome to the one specified.");
        properties.put("HelpBiomeList", "Lists all possible biomes.");
        properties.put("HelpDone", "Toggles a plot done or not done.");
        properties.put("HelpTp", "Teleports to a plot in the current world.");
        properties.put("HelpId", "Gets plot id and coordinates of the current plot your standing on.");
        properties.put("HelpClear", "Clears the plot to its original flat state.");
        properties.put("HelpReset", "Resets the plot to its original flat state AND remove its owner.");
        properties.put("HelpAdd", "Allows a player to have full access to the plot(This is your responsibility!)");
        properties.put("HelpDeny", "Prevents a player from moving onto your plot.");
        properties.put("HelpRemove", "Revokes a players access to the plot.");
        properties.put("HelpUndeny", "Allows a previously denied player to move onto your plot.");
        properties.put("HelpSetowner", "Sets the player provided as the owner of the plot your currently on.");
        properties.put("HelpMove", "Swaps the plots blocks(highly experimental for now, use at your own risk).");
        properties.put("HelpWEAnywhere", "Toggles using worldedit anywhere.");
        properties.put("HelpExpired", "Lists expired plots.");
        properties.put("HelpDoneList", "Lists finished plots.");
        properties.put("HelpAddTime1", "Resets the expiration date to");
        properties.put("HelpAddTime2", "days from now.");
        properties.put("HelpReload", "Reloads the plugin and its configuration files.");
        properties.put("HelpDispose", "You will no longer own the plot but it will not get cleared.");
        properties.put("HelpBuy", "Buys a plot at the price listed.");
        properties.put("HelpSell", "Puts your plot for sale.");
        properties.put("HelpSellBank", "Sells your plot to the bank for");
        properties.put("HelpAuction", "Puts your plot for auction.");
        properties.put("HelpResetExpired", "Resets the 50 oldest plots on that world.");
        properties.put("HelpBid", "Places a bid on the current plot.");
        properties.put("WordWorld", "World");
        properties.put("WordUsage", "Usage");
        properties.put("WordExample", "Example");
        properties.put("WordAmount", "amount");
        properties.put("WordUse", "Use");
        properties.put("WordPlot", "Plot");
        properties.put("WordFor", "for");
        properties.put("WordAt", "at");
        properties.put("WordMarked", "marked");
        properties.put("WordFinished", "finished");
        properties.put("WordUnfinished", "unfinished");
        properties.put("WordAuction", "Auction");
        properties.put("WordSell", "Sell");
        properties.put("WordYours", "Yours");
        properties.put("WordHelpers", "Helpers");
        properties.put("WordInfinite", "Infinite");
        properties.put("WordPrice", "Price");
        properties.put("WordPlayer", "Player");
        properties.put("WordComment", "comment");
        properties.put("WordBiome", "biome");
        properties.put("WordId", "id");
        properties.put("WordIdFrom", "id-from");
        properties.put("WordIdTo", "id-to");
        properties.put("WordNever", "Never");
        properties.put("WordDefault", "Default");
        properties.put("WordMissing", "Missing");
        properties.put("WordYes", "Yes");
        properties.put("WordNo", "No");
        properties.put("WordText", "text");
        properties.put("WordFrom", "From");
        properties.put("WordTo", "to");
        properties.put("WordBiomes", "Biomes");
        properties.put("WordNotApplicable", "N/A");
        properties.put("WordBottom", "Bottom");
        properties.put("WordTop", "Top");
        properties.put("WordPossessive", "'s");
        properties.put("WordRemoved", "removed");
        properties.put("SignOwner", "Owner:");
        properties.put("SignId", "ID:");
        properties.put("SignForSale", "&9&lFOR SALE");
        properties.put("SignPrice", "Price :");
        properties.put("SignPriceColor", "&9");
        properties.put("SignOnAuction", "&9&lON AUCTION");
        properties.put("SignMinimumBid", "Minimum bid :");
        properties.put("SignCurrentBid", "Current bid :");
        properties.put("SignCurrentBidColor", "&9");
        properties.put("InfoId", "ID");
        properties.put("InfoOwner", "Owner");
        properties.put("InfoBiome", "Biome");
        properties.put("InfoExpire", "Expire date");
        properties.put("InfoFinished", "Finished");
        properties.put("InfoProtected", "Protected");
        properties.put("InfoHelpers", "Helpers");
        properties.put("InfoDenied", "Denied");
        properties.put("InfoAuctionned", "Auctionned");
        properties.put("InfoBidder", "Bidder");
        properties.put("InfoBid", "Bid");
        properties.put("InfoForSale", "For sale");
        properties.put("InfoMinimumBid", "Minimum bid");
        properties.put("CommandBuy", "buy");
        properties.put("CommandBid", "bid");
        properties.put("CommandResetExpired", "resetexpired");
        properties.put("CommandHelp", "help");
        properties.put("CommandClaim", "claim");
        properties.put("CommandAuto", "auto");
        properties.put("CommandInfo", "info");
        properties.put("CommandComment", "comment");
        properties.put("CommandComments", "comments");
        properties.put("CommandBiome", "biome");
        properties.put("CommandBiomelist", "biomelist");
        properties.put("CommandId", "id");
        properties.put("CommandTp", "tp");
        properties.put("CommandClear", "clear");
        properties.put("CommandReset", "reset");
        properties.put("CommandAdd", "add");
        properties.put("CommandDeny", "deny");
        properties.put("CommandRemove", "remove");
        properties.put("CommandUndeny", "undeny");
        properties.put("CommandSetowner", "setowner");
        properties.put("CommandMove", "move");
        properties.put("CommandWEAnywhere", "weanywhere");
        properties.put("CommandList", "list");
        properties.put("CommandExpired", "expired");
        properties.put("CommandAddtime", "addtime");
        properties.put("CommandDone", "done");
        properties.put("CommandDoneList", "donelist");
        properties.put("CommandProtect", "protect");
        properties.put("CommandSell", "sell");
        properties.put("CommandSellBank", "sell bank");
        properties.put("CommandDispose", "dispose");
        properties.put("CommandAuction", "auction");
        properties.put("CommandHome", "home");
        properties.put("ErrCannotBuild", "You cannot build here.");
        properties.put("ErrCannotUseEggs", "You cannot use eggs here.");
        properties.put("ErrCannotUse", "You cannot use that.");
        properties.put("ErrCreatingPlotAt", "An error occured while creating the plot at");
        properties.put("ErrMovingPlot", "Error moving plot");
        this.CreateConfig(filelang, properties, "PlotMe Caption configuration \u03b1\u03c9");
        if (PlotMe.language != "english") {
            filelang = new File(this.getDataFolder(), "caption-" + PlotMe.language + ".yml");
            this.CreateConfig(filelang, properties, "PlotMe Caption configuration");
        }
        InputStream input = null;
        try {
            input = (InputStream)new FileInputStream(filelang);
            final Yaml yaml = new Yaml();
            final LinkedHashMap<String, String> obj = yaml.load(input);
            if (obj instanceof LinkedHashMap) {
                final LinkedHashMap<String, String> data = obj;
                PlotMe.captions = new HashMap<String, String>();
                for (final String key : data.keySet()) {
                    PlotMe.captions.put(key, data.get(key));
                }
            }
        }
        catch (FileNotFoundException e) {
            PlotMe.logger.severe("File not found: " + e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e2) {
            PlotMe.logger.severe("Error with configuration: " + e2.getMessage());
            e2.printStackTrace();
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    private void CreateConfig(final File file, final TreeMap<String, String> properties, final String Title) {
        if (!file.exists()) {
            BufferedWriter writer = null;
            try {
                final File dir = new File(this.getDataFolder(), "");
                dir.mkdirs();
                writer = new BufferedWriter((Writer)new OutputStreamWriter((OutputStream)new FileOutputStream(file, true), "UTF-8"));
                writer.write("# " + Title + "\n");
                for (final Map.Entry<String, String> e : properties.entrySet()) {
                    writer.write((String)e.getKey() + ": '" + ((String)e.getValue()).replace("'", "''") + "'\n");
                }
                writer.close();
            }
            catch (IOException e2) {
                PlotMe.logger.severe("Unable to create config file : " + Title + "!");
                PlotMe.logger.severe(e2.getMessage());
            }
            finally {
                if (writer != null) {
                    try {
                        writer.close();
                    }
                    catch (IOException ex) {}
                }
            }
        }
        else {
            OutputStreamWriter writer2 = null;
            InputStream input = null;
            try {
                input = (InputStream)new FileInputStream(file);
                final Yaml yaml = new Yaml();
                final LinkedHashMap<String, String> obj = yaml.load(input);
                if (obj instanceof LinkedHashMap) {
                    final LinkedHashMap<String, String> data = obj;
                    writer2 = new OutputStreamWriter((OutputStream)new FileOutputStream(file, true), "UTF-8");
                    for (final Map.Entry<String, String> e3 : properties.entrySet()) {
                        if (!data.containsKey(e3.getKey())) {
                            writer2.write("\n" + (String)e3.getKey() + ": '" + ((String)e3.getValue()).replace("'", "''") + "'");
                        }
                    }
                    writer2.close();
                    input.close();
                }
            }
            catch (FileNotFoundException e4) {
                PlotMe.logger.severe("File not found: " + e4.getMessage());
                e4.printStackTrace();
            }
            catch (Exception e5) {
                PlotMe.logger.severe("Error with configuration: " + e5.getMessage());
                e5.printStackTrace();
            }
            finally {
                if (writer2 != null) {
                    try {
                        writer2.close();
                    }
                    catch (IOException ex2) {}
                }
                if (input != null) {
                    try {
                        input.close();
                    }
                    catch (IOException ex3) {}
                }
            }
        }
    }
    
    public static String caption(final String s) {
        if (PlotMe.captions.containsKey(s)) {
            return addColor((String)PlotMe.captions.get(s));
        }
        PlotMe.logger.warning("Missing caption: " + s);
        return "ERROR:Missing caption '" + s + "'";
    }
    
    public static String addColor(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
    
    public void scheduleProtectionRemoval(final Location bottom, final Location top) {
        final int x1 = bottom.getBlockX();
        final int y1 = bottom.getBlockY();
        final int z1 = bottom.getBlockZ();
        final int x2 = top.getBlockX();
        final int y2 = top.getBlockY();
        final int z2 = top.getBlockZ();
        final World w = bottom.getWorld();
        for (int x3 = x1; x3 <= x2; ++x3) {
            for (int z3 = z1; z3 <= z2; ++z3) {
                for (int y3 = y1; y3 <= y2; ++y3) {
                    final Block block = w.getBlockAt(x3, y3, z3);
                    Bukkit.getScheduler().runTask((Plugin)this, (Runnable)new Runnable() {
                        public void run() {
                            final Protection protection = LWC.getInstance().findProtection(block);
                            if (protection != null) {
                                protection.remove();
                            }
                        }
                    });
                }
            }
        }
    }
    
    private Material getBlockId(final ConfigurationSection cs, final String section, final Material def) {
        String id = cs.getString(section, this.matToString(def));
        if (id.contains(";")) {
            id = id.split(";")[0];
        }
        return this.stringToMaterial(id);
    }
    
    private BlockData getBlockValue(final ConfigurationSection cs, final String section, final BlockData def) {
        String id = cs.getString(section, this.datToString(def));
        if (id.contains(";")) {
            id = id.split(";", 2)[1];
            return this.stringToBlockData(id);
        }
        return def;
    }
    
    private BlockData stringToBlockData(final String str) {
        return Bukkit.getServer().createBlockData(str);
    }
    
    private String datToString(final BlockData data) {
        return data.getAsString();
    }
    
    private String getBlockValueId(@Nonnull final Material id, final BlockData value) {
        if (value != null) {
            return this.matToString(id) + ";" + this.datToString(value);
        }
        return this.matToString(id);
    }
    
    static {
        PlotMe.logger = null;
        PlotMe.plotmaps = null;
        PlotMe.worldeditplugin = null;
        PlotMe.plotworldedit = null;
        PlotMe.economy = null;
        PlotMe.usinglwc = false;
        PlotMe.playersignoringwelimit = null;
        PlotMe.self = null;
    }
}
