package com.worldcretornica.plotme.listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityExplodeEvent;
import java.util.List;
import org.bukkit.block.BlockState;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.worldcretornica.plotme.PlotMapInfo;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import com.worldcretornica.plotme.Plot;
import org.bukkit.command.CommandSender;
import com.worldcretornica.plotme.PlotMe;
import com.worldcretornica.plotme.PlotManager;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;

public class PlotListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block b = event.getBlock();
        if (PlotManager.isPlotWorld(b)) {
            final Player p = event.getPlayer();
            final boolean canbuild = PlotMe.cPerms((CommandSender)event.getPlayer(), "plotme.admin.buildanywhere");
            final String id = PlotManager.getPlotId(b.getLocation());
            if (id.equalsIgnoreCase("")) {
                if (!canbuild) {
                    p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                    event.setCancelled(true);
                }
            }
            else {
                final Plot plot = (Plot)PlotManager.getMap(p).plots.get(id);
                if (plot == null) {
                    if (!canbuild) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
                else if (!plot.isAllowed(p.getUniqueId())) {
                    if (!canbuild) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
                else {
                    plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block b = event.getBlock();
        if (PlotManager.isPlotWorld(b)) {
            final Player p = event.getPlayer();
            final boolean canbuild = PlotMe.cPerms((CommandSender)p, "plotme.admin.buildanywhere");
            final String id = PlotManager.getPlotId(b.getLocation());
            if (id.equalsIgnoreCase("")) {
                if (!canbuild) {
                    p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                    event.setCancelled(true);
                }
            }
            else {
                final Plot plot = PlotManager.getPlotById(p, id);
                if (plot == null) {
                    if (!canbuild) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
                else if (!plot.isAllowed(p.getUniqueId())) {
                    if (!canbuild) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
                else {
                    plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        final Block b = event.getBlock();
        final Entity e = event.getEntity();
        if (PlotManager.isPlotWorld(b)) {
            if (!(e instanceof Player)) {
                if (!(e instanceof FallingBlock)) {
                    event.setCancelled(true);
                }
            }
            else {
                final Player p = (Player)e;
                final boolean canbuild = PlotMe.cPerms((CommandSender)p, "plotme.admin.buildanywhere");
                final String id = PlotManager.getPlotId(b.getLocation());
                if (id.equalsIgnoreCase("")) {
                    if (!canbuild) {
                        event.setCancelled(true);
                    }
                }
                else {
                    final Plot plot = PlotManager.getPlotById(p, id);
                    if (plot == null) {
                        if (!canbuild) {
                            event.setCancelled(true);
                        }
                    }
                    else if (!plot.isAllowed(p.getUniqueId())) {
                        if (!canbuild) {
                            event.setCancelled(true);
                        }
                    }
                    else {
                        plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityBlockForm(final EntityBlockFormEvent event) {
        final Block b = event.getBlock();
        final Entity e = event.getEntity();
        if (PlotManager.isPlotWorld(b) && !(e instanceof Player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (!PlotMe.cPerms((CommandSender)event.getPlayer(), "plotme.admin.buildanywhere")) {
            final BlockFace bf = event.getBlockFace();
            final Block b = event.getBlockClicked().getLocation().add((double)bf.getModX(), (double)bf.getModY(), (double)bf.getModZ()).getBlock();
            if (PlotManager.isPlotWorld(b)) {
                final String id = PlotManager.getPlotId(b.getLocation());
                final Player p = event.getPlayer();
                if (id.equalsIgnoreCase("")) {
                    p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                    event.setCancelled(true);
                }
                else {
                    final Plot plot = PlotManager.getPlotById(p, id);
                    if (plot == null) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                    else if (!plot.isAllowed(p.getUniqueId())) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerBucketFill(final PlayerBucketFillEvent event) {
        if (!PlotMe.cPerms((CommandSender)event.getPlayer(), "plotme.admin.buildanywhere")) {
            final Block b = event.getBlockClicked();
            if (PlotManager.isPlotWorld(b)) {
                final String id = PlotManager.getPlotId(b.getLocation());
                final Player p = event.getPlayer();
                if (id.equalsIgnoreCase("")) {
                    p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                    event.setCancelled(true);
                }
                else {
                    final Plot plot = PlotManager.getPlotById(p, id);
                    if (plot == null) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                    else if (!plot.isAllowed(p.getUniqueId())) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Block b = event.getClickedBlock();
        if (PlotManager.isPlotWorld(b)) {
            final PlotMapInfo pmi = PlotManager.getMap(b);
            boolean blocked = false;
            final Player player = event.getPlayer();
            final boolean canbuild = PlotMe.cPerms((CommandSender)player, "plotme.admin.buildanywhere");
            if (event.isBlockInHand() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                final ItemStack is = player.getInventory().getItemInMainHand();
                final ItemStack is2 = player.getInventory().getItemInOffHand();
                if (event.getClickedBlock() != null) {
                    final Material matClicked = event.getClickedBlock().getType();
                    final Material matHeld;
                    if(is != null) {
                        matHeld = is.getType();
                    } else {
                        matHeld = null;
                    }
                    final Material matHeld2;
                    if(is2 != null) {
                        matHeld2 = is2.getType();
                    } else {
                        matHeld2 = null;
                    }
                    if (matClicked == matHeld || matClicked == matHeld2) {
                        final BlockFace face = event.getBlockFace();
                        final Block builtblock = b.getWorld().getBlockAt(b.getX() + face.getModX(), b.getY() + face.getModY(), b.getZ() + face.getModZ());
                        final String id = PlotManager.getPlotId(builtblock.getLocation());
                        final Player p = event.getPlayer();
                        if (id.equalsIgnoreCase("")) {
                            if (!canbuild) {
                                p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                                event.setCancelled(true);
                            }
                        }
                        else {
                            final Plot plot = PlotManager.getPlotById(p, id);
                            if (plot == null) {
                                if (!canbuild) {
                                    p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                                    event.setCancelled(true);
                                }
                            }
                            else if (!plot.isAllowed(p.getName())) {
                                if (!canbuild) {
                                    p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                                    event.setCancelled(true);
                                }
                            }
                            else {
                                plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
                            }
                        }
                    }
                }
            }
            else {
                if (pmi.ProtectedBlocks.contains(b.getType()) && !PlotMe.cPerms((CommandSender)player, "plotme.unblock." + b.getType().name().toLowerCase())) {
                    blocked = true;
                }
                final ItemStack is = event.getItem();
                if (is != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    final Material itemid = is.getType();
                    if (pmi.PreventedItems.contains(itemid) && !PlotMe.cPerms((CommandSender)player, new StringBuilder().append("plotme.unblock.").append(itemid).toString())) {
                        blocked = true;
                    }
                }
                if (blocked) {
                    final String id2 = PlotManager.getPlotId(b.getLocation());
                    final Player p2 = event.getPlayer();
                    if (id2.equalsIgnoreCase("")) {
                        if (!canbuild) {
                            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                p2.sendMessage(PlotMe.caption("ErrCannotUse"));
                            }
                            event.setCancelled(true);
                        }
                    }
                    else {
                        final Plot plot2 = PlotManager.getPlotById(p2, id2);
                        if (plot2 == null) {
                            if (!canbuild) {
                                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                    p2.sendMessage(PlotMe.caption("ErrCannotUse"));
                                }
                                event.setCancelled(true);
                            }
                        }
                        else if (!plot2.isAllowed(p2.getName()) && !canbuild) {
                            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                p2.sendMessage(PlotMe.caption("ErrCannotUse"));
                            }
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockSpread(final BlockSpreadEvent event) {
        final Block b = event.getBlock();
        if (PlotManager.isPlotWorld(b)) {
            final String id = PlotManager.getPlotId(b.getLocation());
            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockForm(final BlockFormEvent event) {
        final Block b = event.getBlock();
        if (PlotManager.isPlotWorld(b)) {
            final String id = PlotManager.getPlotId(b.getLocation());
            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDamage(final BlockDamageEvent event) {
        final Block b = event.getBlock();
        if (PlotManager.isPlotWorld(b)) {
            final String id = PlotManager.getPlotId(b.getLocation());
            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFade(final BlockFadeEvent event) {
        final Block b = event.getBlock();
        if (PlotManager.isPlotWorld(b)) {
            final String id = PlotManager.getPlotId(b.getLocation());
            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFromTo(final BlockFromToEvent event) {
        final Block b = event.getToBlock();
        if (PlotManager.isPlotWorld(b)) {
            final String id = PlotManager.getPlotId(b.getLocation());
            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockGrow(final BlockGrowEvent event) {
        final Block b = event.getBlock();
        if (PlotManager.isPlotWorld(b)) {
            final String id = PlotManager.getPlotId(b.getLocation());
            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
        if (PlotManager.isPlotWorld(event.getBlock())) {
            final BlockFace face = event.getDirection();
            for (final Block b : event.getBlocks()) {
                final String id = PlotManager.getPlotId(b.getLocation().add((double)face.getModX(), (double)face.getModY(), (double)face.getModZ()));
                if (id.equalsIgnoreCase("")) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
        final Block b = event.getBlock();
        if (PlotManager.isPlotWorld(b) && event.getBlock().getType() == Material.STICKY_PISTON) {
            final String id = PlotManager.getPlotId(b.getLocation());
            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onStructureGrow(final StructureGrowEvent event) {
        final List<BlockState> blocks = (List<BlockState>)event.getBlocks();
        boolean found = false;
        for (int i = 0; i < blocks.size(); ++i) {
            if (found || PlotManager.isPlotWorld((BlockState)blocks.get(i))) {
                found = true;
                final String id = PlotManager.getPlotId(((BlockState)blocks.get(i)).getLocation());
                if (id.equalsIgnoreCase("")) {
                    event.getBlocks().remove(i);
                    --i;
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event) {
        final Location l = event.getLocation();
        if (l != null) {
            final PlotMapInfo pmi = PlotManager.getMap(l);
            if (pmi != null && pmi.DisableExplosion) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        final Block b = event.getBlock();
        if (b != null) {
            final PlotMapInfo pmi = PlotManager.getMap(b);
            if (pmi != null) {
                if (pmi.DisableIgnition) {
                    event.setCancelled(true);
                }
                else {
                    final String id = PlotManager.getPlotId(b.getLocation());
                    final Player p = event.getPlayer();
                    if (id.equalsIgnoreCase("") || p == null) {
                        event.setCancelled(true);
                    }
                    else {
                        final Plot plot = PlotManager.getPlotById(b, id);
                        if (plot == null) {
                            event.setCancelled(true);
                        }
                        else if (!plot.isAllowed(p.getUniqueId())) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingPlace(final HangingPlaceEvent event) {
        final Block b = event.getBlock();
        if (PlotManager.isPlotWorld(b)) {
            final String id = PlotManager.getPlotId(b.getLocation());
            final Player p = event.getPlayer();
            final boolean canbuild = PlotMe.cPerms((CommandSender)event.getPlayer(), "plotme.admin.buildanywhere");
            if (id.equalsIgnoreCase("")) {
                if (!canbuild) {
                    p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                    event.setCancelled(true);
                }
            }
            else {
                final Plot plot = PlotManager.getPlotById(p, id);
                if (plot == null) {
                    if (!canbuild) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
                else if (!plot.isAllowed(p.getUniqueId())) {
                    if (!canbuild) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
                else {
                    plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingBreakByEntity(final HangingBreakByEntityEvent event) {
        final Entity entity = event.getRemover();
        if (entity instanceof Player) {
            final Player p = (Player)entity;
            final boolean canbuild = PlotMe.cPerms((CommandSender)p, "plotme.admin.buildanywhere");
            final Location l = event.getEntity().getLocation();
            if (PlotManager.isPlotWorld(l)) {
                final String id = PlotManager.getPlotId(l);
                if (id.equalsIgnoreCase("")) {
                    if (!canbuild) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
                else {
                    final Plot plot = PlotManager.getPlotById(p, id);
                    if (plot == null) {
                        if (!canbuild) {
                            p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                            event.setCancelled(true);
                        }
                    }
                    else if (!plot.isAllowed(p.getUniqueId())) {
                        if (!canbuild) {
                            p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                            event.setCancelled(true);
                        }
                    }
                    else {
                        plot.resetExpire(PlotManager.getMap(l).DaysToExpiration);
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        final Location l = event.getRightClicked().getLocation();
        if (PlotManager.isPlotWorld(l)) {
            final Player p = event.getPlayer();
            final boolean canbuild = PlotMe.cPerms((CommandSender)p, "plotme.admin.buildanywhere");
            final String id = PlotManager.getPlotId(l);
            if (id.equalsIgnoreCase("")) {
                if (!canbuild) {
                    p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                    event.setCancelled(true);
                }
            }
            else {
                final Plot plot = PlotManager.getPlotById(p, id);
                if (plot == null) {
                    if (!canbuild) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
                else if (!plot.isAllowed(p.getUniqueId())) {
                    if (!canbuild) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
                else {
                    plot.resetExpire(PlotManager.getMap(l).DaysToExpiration);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Location l = event.getEntity().getLocation();
        final Entity e = event.getDamager();
        if (PlotManager.isPlotWorld(l)) {
            if (!(e instanceof Player)) {
                event.setCancelled(true);
            }
            else {
                final Player p = (Player)e;
                final boolean canbuild = PlotMe.cPerms((CommandSender)p, "plotme.admin.buildanywhere");
                final String id = PlotManager.getPlotId(l);
                if (id.equalsIgnoreCase("")) {
                    if (!canbuild) {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
                else {
                    final Plot plot = PlotManager.getPlotById(p, id);
                    if (plot == null) {
                        if (!canbuild) {
                            p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                            event.setCancelled(true);
                        }
                    }
                    else if (!plot.isAllowed(p.getUniqueId())) {
                        if (!canbuild) {
                            p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                            event.setCancelled(true);
                        }
                    }
                    else {
                        plot.resetExpire(PlotManager.getMap(l).DaysToExpiration);
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerEggThrow(final PlayerEggThrowEvent event) {
        final Location l = event.getEgg().getLocation();
        if (PlotManager.isPlotWorld(l)) {
            final Player p = event.getPlayer();
            final boolean canbuild = PlotMe.cPerms((CommandSender)p, "plotme.admin.buildanywhere");
            final String id = PlotManager.getPlotId(l);
            if (id.equalsIgnoreCase("")) {
                if (!canbuild) {
                    p.sendMessage(PlotMe.caption("ErrCannotUseEggs"));
                    event.setHatching(false);
                }
            }
            else {
                final Plot plot = PlotManager.getPlotById(p, id);
                if (plot == null) {
                    if (!canbuild) {
                        p.sendMessage(PlotMe.caption("ErrCannotUseEggs"));
                        event.setHatching(false);
                    }
                }
                else if (!plot.isAllowed(p.getUniqueId()) && !canbuild) {
                    p.sendMessage(PlotMe.caption("ErrCannotUseEggs"));
                    event.setHatching(false);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        if (p != null) {
            PlotManager.UpdatePlayerNameFromId(p.getUniqueId(), p.getName());
        }
    }
}
