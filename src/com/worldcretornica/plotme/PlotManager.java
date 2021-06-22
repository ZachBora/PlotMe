package com.worldcretornica.plotme;

import org.bukkit.plugin.Plugin;
import com.griefcraft.model.Protection;
import com.griefcraft.lwc.LWC;
import java.util.Set;
import java.util.Collections;
import org.bukkit.command.CommandSender;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.Chunk;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.Bukkit;
import java.util.UUID;
import org.bukkit.block.data.BlockData;
import org.bukkit.Material;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class PlotManager {
    public static String getPlotId(final Location loc) {
        final PlotMapInfo pmi = getMap(loc);
        if (pmi == null) {
            return "";
        }
        final int valx = loc.getBlockX();
        final int valz = loc.getBlockZ();
        final int size = pmi.PlotSize + pmi.PathWidth;
        final int pathsize = pmi.PathWidth;
        boolean road = false;
        int mod2 = 0;
        final int mod3 = 1;
        int x = (int)Math.ceil(valx / (double)size);
        int z = (int)Math.ceil(valz / (double)size);
        double n3;
        if (pathsize % 2 == 1) {
            n3 = Math.ceil(pathsize / 2.0);
            mod2 = -1;
        }
        else {
            n3 = Math.floor(pathsize / 2.0);
        }
        for (double i = n3; i >= 0.0; --i) {
            if ((valx - i + mod3) % size == 0.0 || (valx + i + mod2) % size == 0.0) {
                road = true;
                x = (int)Math.ceil((valx - n3) / size);
            }
            if ((valz - i + mod3) % size == 0.0 || (valz + i + mod2) % size == 0.0) {
                road = true;
                z = (int)Math.ceil((valz - n3) / size);
            }
        }
        if (road) {
            return "";
        }
        return new StringBuilder().append("").append(x).append(";").append(z).toString();
    }
    
    public static String getPlotId(final Player player) {
        return getPlotId(player.getLocation());
    }
    
    public static List<Player> getPlayersInPlot(final World w, final String id) {
        final List<Player> playersInPlot = new ArrayList<Player>();
        for (final Player p : w.getPlayers()) {
            if (getPlotId(p).equals(id)) {
                playersInPlot.add(p);
            }
        }
        return playersInPlot;
    }
    
    public static void adjustLinkedPlots(final String id, final World world) {
        final PlotMapInfo pmi = getMap(world);
        if (pmi != null) {
            final HashMap<String, Plot> plots = pmi.plots;
            final int x = getIdX(id);
            final int z = getIdZ(id);
            final Plot p11 = (Plot)plots.get(id);
            if (p11 != null) {
                final Plot p12 = (Plot)plots.get(new StringBuilder().append(x - 1).append(";").append(z).toString());
                final Plot p13 = (Plot)plots.get(new StringBuilder().append(x).append(";").append(z - 1).toString());
                final Plot p14 = (Plot)plots.get(new StringBuilder().append(x).append(";").append(z + 1).toString());
                final Plot p15 = (Plot)plots.get(new StringBuilder().append(x + 1).append(";").append(z).toString());
                final Plot p16 = (Plot)plots.get(new StringBuilder().append(x - 1).append(";").append(z - 1).toString());
                final Plot p17 = (Plot)plots.get(new StringBuilder().append(x - 1).append(";").append(z + 1).toString());
                final Plot p18 = (Plot)plots.get(new StringBuilder().append(x + 1).append(";").append(z - 1).toString());
                final Plot p19 = (Plot)plots.get(new StringBuilder().append(x + 1).append(";").append(z + 1).toString());
                if (p12 != null && p12.owner.equalsIgnoreCase(p11.owner)) {
                    fillroad(p12, p11, world);
                }
                if (p13 != null && p13.owner.equalsIgnoreCase(p11.owner)) {
                    fillroad(p13, p11, world);
                }
                if (p14 != null && p14.owner.equalsIgnoreCase(p11.owner)) {
                    fillroad(p14, p11, world);
                }
                if (p15 != null && p15.owner.equalsIgnoreCase(p11.owner)) {
                    fillroad(p15, p11, world);
                }
                if (p16 != null && p13 != null && p12 != null && p16.owner.equalsIgnoreCase(p11.owner) && p11.owner.equalsIgnoreCase(p13.owner) && p13.owner.equalsIgnoreCase(p12.owner)) {
                    fillmiddleroad(p16, p11, world);
                }
                if (p13 != null && p18 != null && p15 != null && p13.owner.equalsIgnoreCase(p11.owner) && p11.owner.equalsIgnoreCase(p18.owner) && p18.owner.equalsIgnoreCase(p15.owner)) {
                    fillmiddleroad(p18, p11, world);
                }
                if (p12 != null && p17 != null && p14 != null && p12.owner.equalsIgnoreCase(p11.owner) && p11.owner.equalsIgnoreCase(p17.owner) && p17.owner.equalsIgnoreCase(p14.owner)) {
                    fillmiddleroad(p17, p11, world);
                }
                if (p14 != null && p15 != null && p19 != null && p14.owner.equalsIgnoreCase(p11.owner) && p11.owner.equalsIgnoreCase(p15.owner) && p15.owner.equalsIgnoreCase(p19.owner)) {
                    fillmiddleroad(p19, p11, world);
                }
            }
        }
    }
    
    private static void fillroad(final Plot plot1, final Plot plot2, final World w) {
        final Location bottomPlot1 = getPlotBottomLoc(w, plot1.id);
        final Location topPlot1 = getPlotTopLoc(w, plot1.id);
        final Location bottomPlot2 = getPlotBottomLoc(w, plot2.id);
        final Location topPlot2 = getPlotTopLoc(w, plot2.id);
        final PlotMapInfo pmi = getMap(w);
        final int h = pmi.RoadHeight;
        final Material wallId = pmi.WallBlockId;
        final BlockData wallValue = pmi.WallBlockValue;
        final Material fillId = pmi.PlotFloorBlockId;
        final BlockData fillValue = pmi.PlotFloorBlockValue;
        int minX;
        int maxX;
        int minZ;
        int maxZ;
        if (bottomPlot1.getBlockX() == bottomPlot2.getBlockX()) {
            minX = bottomPlot1.getBlockX();
            maxX = topPlot1.getBlockX();
            minZ = Math.min(bottomPlot1.getBlockZ(), bottomPlot2.getBlockZ()) + pmi.PlotSize;
            maxZ = Math.max(topPlot1.getBlockZ(), topPlot2.getBlockZ()) - pmi.PlotSize;
        }
        else {
            minZ = bottomPlot1.getBlockZ();
            maxZ = topPlot1.getBlockZ();
            minX = Math.min(bottomPlot1.getBlockX(), bottomPlot2.getBlockX()) + pmi.PlotSize;
            maxX = Math.max(topPlot1.getBlockX(), topPlot2.getBlockX()) - pmi.PlotSize;
        }
        final boolean isWallX = maxX - minX > maxZ - minZ;
        if (isWallX) {
            --minX;
            ++maxX;
        }
        else {
            --minZ;
            ++maxZ;
        }
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int y = h; y < w.getMaxHeight(); ++y) {
                    if (y >= h + 2) {
                        w.getBlockAt(x, y, z).setType(Material.AIR);
                    }
                    else if (y == h + 1) {
                        if (isWallX && (x == minX || x == maxX)) {
                            w.getBlockAt(x, y, z).setType(wallId, true);
                            w.getBlockAt(x, y, z).setBlockData(wallValue, true);
                        }
                        else if (!isWallX && (z == minZ || z == maxZ)) {
                            w.getBlockAt(x, y, z).setType(wallId, true);
                            w.getBlockAt(x, y, z).setBlockData(wallValue, true);
                        }
                        else {
                            w.getBlockAt(x, y, z).setType(Material.AIR);
                        }
                    }
                    else {
                        w.getBlockAt(x, y, z).setType(fillId, true);
                        w.getBlockAt(x, y, z).setBlockData(fillValue, true);
                    }
                }
            }
        }
    }
    
    private static void fillmiddleroad(final Plot plot1, final Plot plot2, final World w) {
        final Location bottomPlot1 = getPlotBottomLoc(w, plot1.id);
        final Location topPlot1 = getPlotTopLoc(w, plot1.id);
        final Location bottomPlot2 = getPlotBottomLoc(w, plot2.id);
        final Location topPlot2 = getPlotTopLoc(w, plot2.id);
        final PlotMapInfo pmi = getMap(w);
        final int h = pmi.RoadHeight;
        final Material fillId = pmi.PlotFloorBlockId;
        final int minX = Math.min(topPlot1.getBlockX(), topPlot2.getBlockX());
        final int maxX = Math.max(bottomPlot1.getBlockX(), bottomPlot2.getBlockX());
        final int minZ = Math.min(topPlot1.getBlockZ(), topPlot2.getBlockZ());
        final int maxZ = Math.max(bottomPlot1.getBlockZ(), bottomPlot2.getBlockZ());
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int y = h; y < w.getMaxHeight(); ++y) {
                    if (y >= h + 1) {
                        w.getBlockAt(x, y, z).setType(Material.AIR);
                    }
                    else {
                        w.getBlockAt(x, y, z).setType(fillId);
                    }
                }
            }
        }
    }
    
    public static boolean isPlotAvailable(final String id, final World world) {
        return isPlotAvailable(id, world.getName().toLowerCase());
    }
    
    public static boolean isPlotAvailable(final String id, final Player p) {
        return isPlotAvailable(id, p.getWorld().getName().toLowerCase());
    }
    
    public static boolean isPlotAvailable(final String id, final String world) {
        return isPlotWorld(world) && !getPlots(world).containsKey(id);
    }
    
    @Deprecated
    public static Plot createPlot(final World world, final String id, final String owner) {
        if (isPlotAvailable(id, world) && id != "") {
            final Plot plot = new Plot(owner, getPlotTopLoc(world, id), getPlotBottomLoc(world, id), id, getMap(world).DaysToExpiration);
            setOwnerSign(world, plot);
            getPlots(world).put(id, plot);
            SqlManager.addPlot(plot, getIdX(id), getIdZ(id), world);
            return plot;
        }
        return null;
    }
    
    public static Plot createPlot(final World world, final String id, final String owner, final UUID uuid) {
        if (isPlotAvailable(id, world) && id != "") {
            final Plot plot = new Plot(owner, uuid, getPlotTopLoc(world, id), getPlotBottomLoc(world, id), id, getMap(world).DaysToExpiration);
            setOwnerSign(world, plot);
            getPlots(world).put(id, plot);
            SqlManager.addPlot(plot, getIdX(id), getIdZ(id), world);
            return plot;
        }
        return null;
    }
    
    public static void setOwnerSign(final World world, final Plot plot) {
        final Location pillar = new Location(world, (double)(bottomX(plot.id, world) - 1), (double)(getMap(world).RoadHeight + 1), (double)(bottomZ(plot.id, world) - 1));
        final Block bsign = pillar.add(0.0, 0.0, -1.0).getBlock();
        bsign.setType(Material.AIR);
        bsign.setType(Material.OAK_WALL_SIGN, false);
        final BlockData data = Bukkit.getServer().createBlockData(Material.OAK_WALL_SIGN);
        ((Directional)data).setFacing(BlockFace.NORTH);
        bsign.setBlockData(data, false);
        final String id = getPlotId(new Location(world, (double)bottomX(plot.id, world), 0.0, (double)bottomZ(plot.id, world)));
        final Sign sign = (Sign)bsign.getState();
        if ((PlotMe.caption("SignId") + id).length() > 16) {
            sign.setLine(0, (PlotMe.caption("SignId") + id).substring(0, 16));
            if ((PlotMe.caption("SignId") + id).length() > 32) {
                sign.setLine(1, (PlotMe.caption("SignId") + id).substring(16, 32));
            }
            else {
                sign.setLine(1, (PlotMe.caption("SignId") + id).substring(16));
            }
        }
        else {
            sign.setLine(0, PlotMe.caption("SignId") + id);
        }
        if ((PlotMe.caption("SignOwner") + plot.owner).length() > 16) {
            sign.setLine(2, (PlotMe.caption("SignOwner") + plot.owner).substring(0, 16));
            if ((PlotMe.caption("SignOwner") + plot.owner).length() > 32) {
                sign.setLine(3, (PlotMe.caption("SignOwner") + plot.owner).substring(16, 32));
            }
            else {
                sign.setLine(3, (PlotMe.caption("SignOwner") + plot.owner).substring(16));
            }
        }
        else {
            sign.setLine(2, PlotMe.caption("SignOwner") + plot.owner);
            sign.setLine(3, "");
        }
        sign.update(true);
    }
    
    public static void setSellSign(final World world, final Plot plot) {
        removeSellSign(world, plot.id);
        if (plot.forsale || plot.auctionned) {
            final Location pillar = new Location(world, (double)(bottomX(plot.id, world) - 1), (double)(getMap(world).RoadHeight + 1), (double)(bottomZ(plot.id, world) - 1));
            Block bsign = pillar.clone().add(-1.0, 0.0, 0.0).getBlock();
            bsign.setType(Material.AIR);
            Directional data = (Directional)Bukkit.getServer().createBlockData(Material.OAK_WALL_SIGN);
            data.setFacing(BlockFace.EAST);
            bsign.setType(Material.OAK_WALL_SIGN, false);
            bsign.setBlockData((BlockData)data, false);
            Sign sign = (Sign)bsign.getState();
            if (plot.forsale) {
                sign.setLine(0, PlotMe.caption("SignForSale"));
                sign.setLine(1, PlotMe.caption("SignPrice"));
                if (plot.customprice % 1.0 == 0.0) {
                    sign.setLine(2, PlotMe.caption("SignPriceColor") + Math.round(plot.customprice));
                }
                else {
                    sign.setLine(2, PlotMe.caption("SignPriceColor") + plot.customprice);
                }
                sign.setLine(3, "/plotme " + PlotMe.caption("CommandBuy"));
                sign.update(true);
            }
            if (plot.auctionned) {
                if (plot.forsale) {
                    bsign = pillar.clone().add(-1.0, 0.0, 1.0).getBlock();
                    bsign.setType(Material.AIR);
                    data = (Directional)Bukkit.getServer().createBlockData(Material.OAK_WALL_SIGN);
                    data.setFacing(BlockFace.EAST);
                    bsign.setType(Material.OAK_WALL_SIGN, false);
                    bsign.setBlockData((BlockData)data, false);
                    sign = (Sign)bsign.getState();
                }
                sign.setLine(0, "" + PlotMe.caption("SignOnAuction"));
                if (plot.currentbidder.equals("")) {
                    sign.setLine(1, PlotMe.caption("SignMinimumBid"));
                }
                else {
                    sign.setLine(1, PlotMe.caption("SignCurrentBid"));
                }
                if (plot.currentbid % 1.0 == 0.0) {
                    sign.setLine(2, PlotMe.caption("SignCurrentBidColor") + Math.round(plot.currentbid));
                }
                else {
                    sign.setLine(2, PlotMe.caption("SignCurrentBidColor") + plot.currentbid);
                }
                sign.setLine(3, "/plotme " + PlotMe.caption("CommandBid") + " <x>");
                sign.update(true);
            }
        }
    }
    
    public static void removeOwnerSign(final World world, final String id) {
        final Location bottom = getPlotBottomLoc(world, id);
        final Location pillar = new Location(world, bottom.getX() - 1.0, (double)(getMap(world).RoadHeight + 1), bottom.getZ() - 1.0);
        final Block bsign = pillar.add(0.0, 0.0, -1.0).getBlock();
        bsign.setType(Material.AIR);
    }
    
    public static void removeSellSign(final World world, final String id) {
        final Location bottom = getPlotBottomLoc(world, id);
        final Location pillar = new Location(world, bottom.getX() - 1.0, (double)(getMap(world).RoadHeight + 1), bottom.getZ() - 1.0);
        Block bsign = pillar.clone().add(-1.0, 0.0, 0.0).getBlock();
        bsign.setType(Material.AIR);
        bsign = pillar.clone().add(-1.0, 0.0, 1.0).getBlock();
        bsign.setType(Material.AIR);
    }
    
    public static int getIdX(final String id) {
        return Integer.parseInt(id.substring(0, id.indexOf(";")));
    }
    
    public static int getIdZ(final String id) {
        return Integer.parseInt(id.substring(id.indexOf(";") + 1));
    }
    
    public static Location getPlotBottomLoc(final World world, final String id) {
        final int px = getIdX(id);
        final int pz = getIdZ(id);
        final PlotMapInfo pmi = getMap(world);
        final int x = px * (pmi.PlotSize + pmi.PathWidth) - pmi.PlotSize - (int)Math.floor((double)(pmi.PathWidth / 2));
        final int z = pz * (pmi.PlotSize + pmi.PathWidth) - pmi.PlotSize - (int)Math.floor((double)(pmi.PathWidth / 2));
        return new Location(world, (double)x, 1.0, (double)z);
    }
    
    public static Location getPlotTopLoc(final World world, final String id) {
        final int px = getIdX(id);
        final int pz = getIdZ(id);
        final PlotMapInfo pmi = getMap(world);
        final int x = px * (pmi.PlotSize + pmi.PathWidth) - (int)Math.floor((double)(pmi.PathWidth / 2)) - 1;
        final int z = pz * (pmi.PlotSize + pmi.PathWidth) - (int)Math.floor((double)(pmi.PathWidth / 2)) - 1;
        return new Location(world, (double)x, 255.0, (double)z);
    }
    
    public static void setBiome(final World w, final String id, final Plot plot, final Biome b) {
        final int bottomX = bottomX(plot.id, w) - 1;
        final int topX = topX(plot.id, w) + 1;
        final int bottomZ = bottomZ(plot.id, w) - 1;
        final int topZ = topZ(plot.id, w) + 1;
        for (int x = bottomX; x <= topX; ++x) {
            for (int z = bottomZ; z <= topZ; ++z) {
                w.getBlockAt(x, 0, z).setBiome(b);
            }
        }
        plot.biome = b;
        SqlManager.updatePlot(getIdX(id), getIdZ(id), plot.world, "biome", b.name());
    }
    
    public static Location getTop(final World w, final Plot plot) {
        return new Location(w, (double)topX(plot.id, w), (double)w.getMaxHeight(), (double)topZ(plot.id, w));
    }
    
    public static Location getBottom(final World w, final Plot plot) {
        return new Location(w, (double)bottomX(plot.id, w), 0.0, (double)bottomZ(plot.id, w));
    }
    
    public static void clear(final World w, final Plot plot) {
        clear(getBottom(w, plot), getTop(w, plot));
        RemoveLWC(w, plot);
    }
    
    public static void clear(final Location bottom, final Location top) {
        final PlotMapInfo pmi = getMap(bottom);
        final int bottomX = bottom.getBlockX();
        final int topX = top.getBlockX();
        final int bottomZ = bottom.getBlockZ();
        final int topZ = top.getBlockZ();
        final int minChunkX = (int)Math.floor(bottomX / 16.0);
        final int maxChunkX = (int)Math.floor(topX / 16.0);
        final int minChunkZ = (int)Math.floor(bottomZ / 16.0);
        final int maxChunkZ = (int)Math.floor(topZ / 16.0);
        final World w = bottom.getWorld();
        for (int cx = minChunkX; cx <= maxChunkX; ++cx) {
            for (int cz = minChunkZ; cz <= maxChunkZ; ++cz) {
                final Chunk chunk = w.getChunkAt(cx, cz);
                for (final Entity e : chunk.getEntities()) {
                    final Location eloc = e.getLocation();
                    if (!(e instanceof Player) && eloc.getBlockX() >= bottom.getBlockX() && eloc.getBlockX() <= top.getBlockX() && eloc.getBlockZ() >= bottom.getBlockZ() && eloc.getBlockZ() <= top.getBlockZ()) {
                        e.remove();
                    }
                }
            }
        }
        for (int x = bottomX; x <= topX; ++x) {
            for (int z = bottomZ; z <= topZ; ++z) {
                Block block = new Location(w, (double)x, 0.0, (double)z).getBlock();
                block.setBiome(Biome.PLAINS);
                for (int y = w.getMaxHeight(); y >= 0; --y) {
                    block = new Location(w, (double)x, (double)y, (double)z).getBlock();
                    final BlockState state = block.getState();
                    if (state instanceof InventoryHolder) {
                        final InventoryHolder holder = (InventoryHolder)state;
                        holder.getInventory().clear();
                    }
                    if (state instanceof Jukebox) {
                        final Jukebox jukebox = (Jukebox)state;
                        try {
                            jukebox.setPlaying(Material.AIR);
                        }
                        catch (Exception ex) {}
                    }
                    if (y == 0) {
                        block.setType(pmi.BottomBlockId);
                    }
                    else if (y < pmi.RoadHeight) {
                        block.setType(pmi.PlotFillingBlockId);
                    }
                    else if (y == pmi.RoadHeight) {
                        block.setType(pmi.PlotFloorBlockId);
                    }
                    else {
                        if (y == pmi.RoadHeight + 1) {
                            if (x == bottomX - 1 || x == topX + 1 || z == bottomZ - 1) {
                                continue;
                            }
                            if (z == topZ + 1) {
                                continue;
                            }
                        }
                        block.setType(Material.AIR, false);
                    }
                }
            }
        }
        adjustWall(bottom);
    }
    
    public static void adjustWall(final Location l) {
        final Plot plot = getPlotById(l);
        final World w = l.getWorld();
        final PlotMapInfo pmi = getMap(w);
        final List<MatAndData> wallids = new ArrayList<MatAndData>();
        final MatAndData auctionwallid = new MatAndData(pmi.AuctionWallBlockId, null);
        final MatAndData forsalewallid = new MatAndData(pmi.ForSaleWallBlockId, null);
        if (plot.protect) {
            wallids.add(new MatAndData(pmi.ProtectedWallBlockId, null));
        }
        if (plot.auctionned && !wallids.contains(auctionwallid)) {
            wallids.add(auctionwallid);
        }
        if (plot.forsale && !wallids.contains(forsalewallid)) {
            wallids.add(forsalewallid);
        }
        if (wallids.size() == 0) {
            wallids.add(new MatAndData(pmi.WallBlockId, pmi.WallBlockValue));
        }
        int ctr = 0;
        final Location bottom = getPlotBottomLoc(w, plot.id);
        final Location top = getPlotTopLoc(w, plot.id);
        for (int x = bottom.getBlockX() - 1; x < top.getBlockX() + 1; ++x) {
            final int z = bottom.getBlockZ() - 1;
            final MatAndData currentblockid = (MatAndData)wallids.get(ctr);
            ctr = ((ctr == wallids.size() - 1) ? 0 : (ctr + 1));
            final Block block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
            setWall(block, currentblockid.mat, currentblockid.data);
        }
        for (int z = bottom.getBlockZ() - 1; z < top.getBlockZ() + 1; ++z) {
            final int x = top.getBlockX() + 1;
            final MatAndData currentblockid = (MatAndData)wallids.get(ctr);
            ctr = ((ctr == wallids.size() - 1) ? 0 : (ctr + 1));
            final Block block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
            setWall(block, currentblockid.mat, currentblockid.data);
        }
        for (int x = top.getBlockX() + 1; x > bottom.getBlockX() - 1; --x) {
            final int z = top.getBlockZ() + 1;
            final MatAndData currentblockid = (MatAndData)wallids.get(ctr);
            ctr = ((ctr == wallids.size() - 1) ? 0 : (ctr + 1));
            final Block block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
            setWall(block, currentblockid.mat, currentblockid.data);
        }
        for (int z = top.getBlockZ() + 1; z > bottom.getBlockZ() - 1; --z) {
            final int x = bottom.getBlockX() - 1;
            final MatAndData currentblockid = (MatAndData)wallids.get(ctr);
            ctr = ((ctr == wallids.size() - 1) ? 0 : (ctr + 1));
            final Block block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
            setWall(block, currentblockid.mat, currentblockid.data);
        }
    }
    
    private static void setWall(final Block block, final Material blockId, final BlockData blockData) {
        block.setType(blockId, true);
        block.setBlockData(blockData, true);
    }
    
    public static boolean isBlockInPlot(final Plot plot, final Location blocklocation) {
        final World w = blocklocation.getWorld();
        final int lowestX = Math.min(bottomX(plot.id, w), topX(plot.id, w));
        final int highestX = Math.max(bottomX(plot.id, w), topX(plot.id, w));
        final int lowestZ = Math.min(bottomZ(plot.id, w), topZ(plot.id, w));
        final int highestZ = Math.max(bottomZ(plot.id, w), topZ(plot.id, w));
        return blocklocation.getBlockX() >= lowestX && blocklocation.getBlockX() <= highestX && blocklocation.getBlockZ() >= lowestZ && blocklocation.getBlockZ() <= highestZ;
    }
    
    public static boolean movePlot(final World w, final String idFrom, final String idTo) {
        final Location plot1Bottom = getPlotBottomLoc(w, idFrom);
        final Location plot2Bottom = getPlotBottomLoc(w, idTo);
        final Location plot1Top = getPlotTopLoc(w, idFrom);
        final int distanceX = plot1Bottom.getBlockX() - plot2Bottom.getBlockX();
        final int distanceZ = plot1Bottom.getBlockZ() - plot2Bottom.getBlockZ();
        for (int x = plot1Bottom.getBlockX(); x <= plot1Top.getBlockX(); ++x) {
            for (int z = plot1Bottom.getBlockZ(); z <= plot1Top.getBlockZ(); ++z) {
                Block plot1Block = w.getBlockAt(new Location(w, (double)x, 0.0, (double)z));
                Block plot2Block = w.getBlockAt(new Location(w, (double)(x - distanceX), 0.0, (double)(z - distanceZ)));
                final String plot1Biome = plot1Block.getBiome().name();
                final String plot2Biome = plot2Block.getBiome().name();
                plot1Block.setBiome(Biome.valueOf(plot2Biome));
                plot2Block.setBiome(Biome.valueOf(plot1Biome));
                for (int y = 0; y < w.getMaxHeight(); ++y) {
                    plot1Block = w.getBlockAt(new Location(w, (double)x, (double)y, (double)z));
                    final Material plot1Type = plot1Block.getType();
                    final BlockData plot1Data = plot1Block.getBlockData();
                    plot2Block = w.getBlockAt(new Location(w, (double)(x - distanceX), (double)y, (double)(z - distanceZ)));
                    final Material plot2Type = plot2Block.getType();
                    final BlockData plot2Data = plot2Block.getBlockData();
                    plot1Block.setType(plot2Type, false);
                    plot1Block.setBlockData(plot2Data, false);
                    plot2Block.setType(plot1Type, false);
                    plot2Block.setBlockData(plot1Data);
                }
            }
        }
        final HashMap<String, Plot> plots = getPlots(w);
        if (plots.containsKey(idFrom)) {
            if (plots.containsKey(idTo)) {
                final Plot plot1 = (Plot)plots.get(idFrom);
                final Plot plot2 = (Plot)plots.get(idTo);
                int idX = getIdX(idTo);
                int idZ = getIdZ(idTo);
                SqlManager.deletePlot(idX, idZ, plot2.world);
                plots.remove(idFrom);
                plots.remove(idTo);
                idX = getIdX(idFrom);
                idZ = getIdZ(idFrom);
                SqlManager.deletePlot(idX, idZ, plot1.world);
                plot2.id = new StringBuilder().append("").append(idX).append(";").append(idZ).toString();
                SqlManager.addPlot(plot2, idX, idZ, w);
                plots.put(idFrom, plot2);
                for (int i = 0; i < plot2.comments.size(); ++i) {
                    String strUUID = "";
                    UUID uuid = null;
                    if (((String[])plot2.comments.get(i)).length >= 3) {
                        strUUID = ((String[])plot2.comments.get(i))[2];
                        try {
                            uuid = UUID.fromString(strUUID);
                        }
                        catch (Exception ex) {}
                    }
                    SqlManager.addPlotComment((String[])plot2.comments.get(i), i, idX, idZ, plot2.world, uuid);
                }
                for (final String player : plot2.allowed()) {
                    SqlManager.addPlotAllowed(player, idX, idZ, plot2.world);
                }
                idX = getIdX(idTo);
                idZ = getIdZ(idTo);
                plot1.id = new StringBuilder().append("").append(idX).append(";").append(idZ).toString();
                SqlManager.addPlot(plot1, idX, idZ, w);
                plots.put(idTo, plot1);
                for (int i = 0; i < plot1.comments.size(); ++i) {
                    String strUUID = "";
                    UUID uuid = null;
                    if (((String[])plot1.comments.get(i)).length >= 3) {
                        strUUID = ((String[])plot1.comments.get(i))[2];
                        try {
                            uuid = UUID.fromString(strUUID);
                        }
                        catch (Exception ex2) {}
                    }
                    SqlManager.addPlotComment((String[])plot1.comments.get(i), i, idX, idZ, plot1.world, uuid);
                }
                for (final String player : plot1.allowed()) {
                    SqlManager.addPlotAllowed(player, idX, idZ, plot1.world);
                }
                setOwnerSign(w, plot1);
                setSellSign(w, plot1);
                setOwnerSign(w, plot2);
                setSellSign(w, plot2);
            }
            else {
                final Plot plot3 = (Plot)plots.get(idFrom);
                int idX2 = getIdX(idFrom);
                int idZ2 = getIdZ(idFrom);
                SqlManager.deletePlot(idX2, idZ2, plot3.world);
                plots.remove(idFrom);
                idX2 = getIdX(idTo);
                idZ2 = getIdZ(idTo);
                plot3.id = new StringBuilder().append("").append(idX2).append(";").append(idZ2).toString();
                SqlManager.addPlot(plot3, idX2, idZ2, w);
                plots.put(idTo, plot3);
                for (int j = 0; j < plot3.comments.size(); ++j) {
                    String strUUID2 = "";
                    UUID uuid2 = null;
                    if (((String[])plot3.comments.get(j)).length >= 3) {
                        strUUID2 = ((String[])plot3.comments.get(j))[2];
                        try {
                            uuid2 = UUID.fromString(strUUID2);
                        }
                        catch (Exception ex3) {}
                    }
                    SqlManager.addPlotComment((String[])plot3.comments.get(j), j, idX2, idZ2, plot3.world, uuid2);
                }
                for (final String player2 : plot3.allowed()) {
                    SqlManager.addPlotAllowed(player2, idX2, idZ2, plot3.world);
                }
                setOwnerSign(w, plot3);
                setSellSign(w, plot3);
                removeOwnerSign(w, idFrom);
                removeSellSign(w, idFrom);
            }
        }
        else if (plots.containsKey(idTo)) {
            final Plot plot3 = (Plot)plots.get(idTo);
            int idX2 = getIdX(idTo);
            int idZ2 = getIdZ(idTo);
            SqlManager.deletePlot(idX2, idZ2, plot3.world);
            plots.remove(idTo);
            idX2 = getIdX(idFrom);
            idZ2 = getIdZ(idFrom);
            plot3.id = new StringBuilder().append("").append(idX2).append(";").append(idZ2).toString();
            SqlManager.addPlot(plot3, idX2, idZ2, w);
            plots.put(idFrom, plot3);
            for (int j = 0; j < plot3.comments.size(); ++j) {
                String strUUID2 = "";
                UUID uuid2 = null;
                if (((String[])plot3.comments.get(j)).length >= 3) {
                    strUUID2 = ((String[])plot3.comments.get(j))[2];
                    try {
                        uuid2 = UUID.fromString(strUUID2);
                    }
                    catch (Exception ex4) {}
                }
                SqlManager.addPlotComment((String[])plot3.comments.get(j), j, idX2, idZ2, plot3.world, uuid2);
            }
            for (final String player2 : plot3.allowed()) {
                SqlManager.addPlotAllowed(player2, idX2, idZ2, plot3.world);
            }
            setOwnerSign(w, plot3);
            setSellSign(w, plot3);
            removeOwnerSign(w, idTo);
            removeSellSign(w, idTo);
        }
        return true;
    }
    
    public static int getNbOwnedPlot(final Player p) {
        return getNbOwnedPlot(p.getUniqueId(), p.getWorld());
    }
    
    public static int getNbOwnedPlot(final Player p, final World w) {
        return getNbOwnedPlot(p.getUniqueId(), w);
    }
    
    public static int getNbOwnedPlot(final UUID uuid, final World w) {
        int nbfound = 0;
        if (getPlots(w) != null) {
            for (final Plot plot : getPlots(w).values()) {
                if (plot.ownerId != null && plot.ownerId.equals(uuid)) {
                    ++nbfound;
                }
            }
        }
        return nbfound;
    }
    
    public static int bottomX(final String id, final World w) {
        return getPlotBottomLoc(w, id).getBlockX();
    }
    
    public static int bottomZ(final String id, final World w) {
        return getPlotBottomLoc(w, id).getBlockZ();
    }
    
    public static int topX(final String id, final World w) {
        return getPlotTopLoc(w, id).getBlockX();
    }
    
    public static int topZ(final String id, final World w) {
        return getPlotTopLoc(w, id).getBlockZ();
    }
    
    public static boolean isPlotWorld(final World w) {
        return w != null && PlotMe.plotmaps.containsKey(w.getName().toLowerCase());
    }
    
    public static boolean isPlotWorld(final String name) {
        return PlotMe.plotmaps.containsKey(name.toLowerCase());
    }
    
    public static boolean isPlotWorld(final Location l) {
        return l != null && PlotMe.plotmaps.containsKey(l.getWorld().getName().toLowerCase());
    }
    
    public static boolean isPlotWorld(final Player p) {
        return p != null && PlotMe.plotmaps.containsKey(p.getWorld().getName().toLowerCase());
    }
    
    public static boolean isPlotWorld(final Block b) {
        return b != null && PlotMe.plotmaps.containsKey(b.getWorld().getName().toLowerCase());
    }
    
    public static boolean isPlotWorld(final BlockState b) {
        return b != null && PlotMe.plotmaps.containsKey(b.getWorld().getName().toLowerCase());
    }
    
    public static boolean isEconomyEnabled(final World w) {
        final PlotMapInfo pmi = getMap(w);
        return pmi != null && pmi.UseEconomy && PlotMe.globalUseEconomy && PlotMe.economy != null;
    }
    
    public static boolean isEconomyEnabled(final String name) {
        final PlotMapInfo pmi = getMap(name);
        return pmi != null && pmi.UseEconomy && PlotMe.globalUseEconomy;
    }
    
    public static boolean isEconomyEnabled(final Player p) {
        if (PlotMe.economy == null) {
            return false;
        }
        final PlotMapInfo pmi = getMap(p);
        return pmi != null && pmi.UseEconomy && PlotMe.globalUseEconomy;
    }
    
    public static boolean isEconomyEnabled(final Block b) {
        final PlotMapInfo pmi = getMap(b);
        return pmi != null && pmi.UseEconomy && PlotMe.globalUseEconomy;
    }
    
    public static PlotMapInfo getMap(final World w) {
        if (w == null) {
            return null;
        }
        final String worldname = w.getName().toLowerCase();
        if (PlotMe.plotmaps.containsKey(worldname)) {
            return (PlotMapInfo)PlotMe.plotmaps.get(worldname);
        }
        return null;
    }
    
    public static PlotMapInfo getMap(final String name) {
        final String worldname = name.toLowerCase();
        if (PlotMe.plotmaps.containsKey(worldname)) {
            return (PlotMapInfo)PlotMe.plotmaps.get(worldname);
        }
        return null;
    }
    
    public static PlotMapInfo getMap(final Location l) {
        if (l == null) {
            return null;
        }
        final String worldname = l.getWorld().getName().toLowerCase();
        if (PlotMe.plotmaps.containsKey(worldname)) {
            return (PlotMapInfo)PlotMe.plotmaps.get(worldname);
        }
        return null;
    }
    
    public static PlotMapInfo getMap(final Player p) {
        if (p == null) {
            return null;
        }
        final String worldname = p.getWorld().getName().toLowerCase();
        if (PlotMe.plotmaps.containsKey(worldname)) {
            return (PlotMapInfo)PlotMe.plotmaps.get(worldname);
        }
        return null;
    }
    
    public static PlotMapInfo getMap(final Block b) {
        if (b == null) {
            return null;
        }
        final String worldname = b.getWorld().getName().toLowerCase();
        if (PlotMe.plotmaps.containsKey(worldname)) {
            return (PlotMapInfo)PlotMe.plotmaps.get(worldname);
        }
        return null;
    }
    
    public static HashMap<String, Plot> getPlots(final World w) {
        final PlotMapInfo pmi = getMap(w);
        if (pmi == null) {
            return null;
        }
        return pmi.plots;
    }
    
    public static HashMap<String, Plot> getPlots(final String name) {
        final PlotMapInfo pmi = getMap(name);
        if (pmi == null) {
            return null;
        }
        return pmi.plots;
    }
    
    public static HashMap<String, Plot> getPlots(final Player p) {
        final PlotMapInfo pmi = getMap(p);
        if (pmi == null) {
            return null;
        }
        return pmi.plots;
    }
    
    public static HashMap<String, Plot> getPlots(final Block b) {
        final PlotMapInfo pmi = getMap(b);
        if (pmi == null) {
            return null;
        }
        return pmi.plots;
    }
    
    public static HashMap<String, Plot> getPlots(final Location l) {
        final PlotMapInfo pmi = getMap(l);
        if (pmi == null) {
            return null;
        }
        return pmi.plots;
    }
    
    public static Plot getPlotById(final World w, final String id) {
        final HashMap<String, Plot> plots = getPlots(w);
        if (plots == null) {
            return null;
        }
        return (Plot)plots.get(id);
    }
    
    public static Plot getPlotById(final String name, final String id) {
        final HashMap<String, Plot> plots = getPlots(name);
        if (plots == null) {
            return null;
        }
        return (Plot)plots.get(id);
    }
    
    public static Plot getPlotById(final Player p, final String id) {
        final HashMap<String, Plot> plots = getPlots(p);
        if (plots == null) {
            return null;
        }
        return (Plot)plots.get(id);
    }
    
    public static Plot getPlotById(final Player p) {
        final HashMap<String, Plot> plots = getPlots(p);
        final String plotid = getPlotId(p.getLocation());
        if (plots == null || plotid == "") {
            return null;
        }
        return (Plot)plots.get(plotid);
    }
    
    public static Plot getPlotById(final Location l) {
        final HashMap<String, Plot> plots = getPlots(l);
        final String plotid = getPlotId(l);
        if (plots == null || plotid == "") {
            return null;
        }
        return (Plot)plots.get(plotid);
    }
    
    public static Plot getPlotById(final Block b, final String id) {
        final HashMap<String, Plot> plots = getPlots(b);
        if (plots == null) {
            return null;
        }
        return (Plot)plots.get(id);
    }
    
    public static Plot getPlotById(final Block b) {
        final HashMap<String, Plot> plots = getPlots(b);
        final String plotid = getPlotId(b.getLocation());
        if (plots == null || plotid == "") {
            return null;
        }
        return (Plot)plots.get(plotid);
    }
    
    public static void deleteNextExpired(final World w, final CommandSender sender) {
        List<Plot> expiredplots = new ArrayList<Plot>();
        HashMap<String, Plot> plots = getPlots(w);
        final String date = PlotMe.getDate();
        for (final String id : plots.keySet()) {
            final Plot plot = (Plot)plots.get(id);
            if (!plot.protect && !plot.finished && plot.expireddate != null && PlotMe.getDate(plot.expireddate).compareTo(date.toString()) < 0) {
                expiredplots.add(plot);
            }
        }
        plots = null;
        Collections.sort(expiredplots);
        final Plot expiredplot = (Plot)expiredplots.get(0);
        expiredplots = null;
        clear(w, expiredplot);
        final String id2 = expiredplot.id;
        getPlots(w).remove(id2);
        removeOwnerSign(w, id2);
        removeSellSign(w, id2);
        SqlManager.deletePlot(getIdX(id2), getIdZ(id2), w.getName().toLowerCase());
    }
    
    public static World getFirstWorld() {
        if (PlotMe.plotmaps != null) {
            final Set<String> set = PlotMe.plotmaps.keySet();
            if (set != null && set.toArray().length > 0) {
                return Bukkit.getWorld((String)set.toArray()[0]);
            }
        }
        return null;
    }
    
    public static World getFirstWorld(final UUID uuid) {
        if (PlotMe.plotmaps != null) {
            final Set<String> set = PlotMe.plotmaps.keySet();
            if (set != null && set.toArray().length > 0) {
                for (final String mapkey : set) {
                    for (final String id : ((PlotMapInfo)PlotMe.plotmaps.get(mapkey)).plots.keySet()) {
                        if (((Plot)((PlotMapInfo)PlotMe.plotmaps.get(mapkey)).plots.get(id)).ownerId.equals(uuid)) {
                            return Bukkit.getWorld(mapkey);
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static Plot getFirstPlot(final UUID uuid) {
        if (PlotMe.plotmaps != null) {
            final Set<String> set = PlotMe.plotmaps.keySet();
            if (set != null && set.toArray().length > 0) {
                for (final String mapkey : set) {
                    for (final String id : ((PlotMapInfo)PlotMe.plotmaps.get(mapkey)).plots.keySet()) {
                        if (((Plot)((PlotMapInfo)PlotMe.plotmaps.get(mapkey)).plots.get(id)).ownerId.equals(uuid)) {
                            return (Plot)((PlotMapInfo)PlotMe.plotmaps.get(mapkey)).plots.get(id);
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static boolean isValidId(final String id) {
        final String[] coords = id.split(";");
        if (coords.length != 2) {
            return false;
        }
        try {
            Integer.parseInt(coords[0]);
            Integer.parseInt(coords[1]);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public static void regen(final World w, final Plot plot, final CommandSender sender) {
        final int bottomX = bottomX(plot.id, w);
        final int topX = topX(plot.id, w);
        final int bottomZ = bottomZ(plot.id, w);
        final int topZ = topZ(plot.id, w);
        final int minChunkX = (int)Math.floor(bottomX / 16.0);
        final int maxChunkX = (int)Math.floor(topX / 16.0);
        final int minChunkZ = (int)Math.floor(bottomZ / 16.0);
        final int maxChunkZ = (int)Math.floor(topZ / 16.0);
        final HashMap<Location, Biome> biomes = new HashMap<Location, Biome>();
        for (int cx = minChunkX; cx <= maxChunkX; ++cx) {
            final int xx = cx << 4;
            for (int cz = minChunkZ; cz <= maxChunkZ; ++cz) {
                final int zz = cz << 4;
                final BlockState[][][] blocks = new BlockState[16][16][w.getMaxHeight()];
                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        for (int y = 0; y < w.getMaxHeight(); ++y) {
                            biomes.put(new Location(w, (double)(x + xx), 0.0, (double)(z + zz)), w.getBiome(x + xx, y, z + zz));
                            final Block block = w.getBlockAt(x + xx, y, z + zz);
                            blocks[x][z][y] = block.getState();
                            if (PlotMe.usinglwc) {
                                final LWC lwc = LWC.getInstance();
                                final boolean ignoreBlockDestruction = Boolean.parseBoolean(lwc.resolveProtectionConfiguration(block, "ignoreBlockDestruction"));
                                if (!ignoreBlockDestruction) {
                                    final Protection protection = lwc.findProtection(block);
                                    if (protection != null) {
                                        protection.remove();
                                    }
                                }
                            }
                        }
                    }
                }
                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        for (int y = 0; y < w.getMaxHeight(); ++y) {
                            if (x + xx < bottomX || x + xx > topX || z + zz < bottomZ || z + zz > topZ) {
                                final Block newblock = w.getBlockAt(x + xx, y, z + zz);
                                final BlockState oldblock = blocks[x][z][y];
                                newblock.setType(oldblock.getType(), false);
                                newblock.setBlockData(oldblock.getBlockData(), false);
                                oldblock.update();
                            }
                        }
                    }
                }
            }
        }
        for (final Location loc : biomes.keySet()) {
            final int x2 = loc.getBlockX();
            final int y2 = loc.getBlockX();
            final int z2 = loc.getBlockX();
            w.setBiome(x2, y2, z2, (Biome)biomes.get(loc));
        }
    }
    
    public static Location getPlotHome(final World w, final Plot plot) {
        final PlotMapInfo pmi = getMap(w);
        if (pmi != null) {
            return new Location(w, (double)(bottomX(plot.id, w) + (topX(plot.id, w) - bottomX(plot.id, w)) / 2), (double)(pmi.RoadHeight + 2), (double)(bottomZ(plot.id, w) - 2));
        }
        return w.getSpawnLocation();
    }
    
    public static void RemoveLWC(final World w, final Plot plot) {
        if (PlotMe.usinglwc) {
            final Location bottom = getBottom(w, plot);
            final Location top = getTop(w, plot);
            final int x1 = bottom.getBlockX();
            final int y1 = bottom.getBlockY();
            final int z1 = bottom.getBlockZ();
            final int x2 = top.getBlockX();
            final int y2 = top.getBlockY();
            final int z2 = top.getBlockZ();
            final String wname = w.getName();
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)PlotMe.self, (Runnable)new Runnable() {
                public void run() {
                    final LWC lwc = LWC.getInstance();
                    final List<Protection> protections = (List<Protection>)lwc.getPhysicalDatabase().loadProtections(wname, x1, x2, y1, y2, z1, z2);
                    for (final Protection protection : protections) {
                        protection.remove();
                    }
                }
            });
        }
    }
    
    public static void UpdatePlayerNameFromId(final UUID uuid, final String name) {
        SqlManager.updatePlotsNewUUID(uuid, name);
        Bukkit.getServer().getScheduler().runTaskAsynchronously((Plugin)PlotMe.self, (Runnable)new Runnable() {
            public void run() {
                for (final PlotMapInfo pmi : PlotMe.plotmaps.values()) {
                    for (final Plot plot : pmi.plots.values()) {
                        if (plot.ownerId != null && plot.ownerId.equals(uuid)) {
                            plot.owner = name;
                        }
                        if (plot.currentbidderId != null && plot.currentbidderId.equals(uuid)) {
                            plot.currentbidder = name;
                        }
                        plot.allowed.replace(uuid, name);
                        plot.denied.replace(uuid, name);
                        for (final String[] comment : plot.comments) {
                            if (comment.length > 2 && comment[2] != null && comment[2].equalsIgnoreCase(uuid.toString())) {
                                comment[0] = name;
                            }
                        }
                    }
                }
            }
        });
    }
}
