package com.worldcretornica.plotme.listener;

import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerInteractEvent;
import com.worldcretornica.plotme.Plot;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import com.worldcretornica.plotme.PlotManager;
import com.worldcretornica.plotme.PlotMe;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.Listener;

public class PlotWorldEditListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        boolean changemask = false;
        final Player p = event.getPlayer();
        if (to == null) {
            PlotMe.plotworldedit.removeMask(p);
        }
        else {
            if (from != null) {
                if (!from.getWorld().getName().equalsIgnoreCase(to.getWorld().getName())) {
                    changemask = true;
                }
                else if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                    final String idFrom = PlotManager.getPlotId(from);
                    final String idTo = PlotManager.getPlotId(to);
                    if (!idFrom.equalsIgnoreCase(idTo)) {
                        changemask = true;
                    }
                }
            }
            if (changemask && PlotManager.isPlotWorld(to.getWorld())) {
                if (!PlotMe.isIgnoringWELimit(p)) {
                    PlotMe.plotworldedit.setMask(p);
                }
                else {
                    PlotMe.plotworldedit.removeMask(p);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        if (PlotManager.isPlotWorld(p)) {
            if (!PlotMe.isIgnoringWELimit(p)) {
                PlotMe.plotworldedit.setMask(p);
            }
            else {
                PlotMe.plotworldedit.removeMask(p);
            }
        }
        else {
            PlotMe.plotworldedit.removeMask(p);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player p = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (to == null) {
            PlotMe.plotworldedit.removeMask(p);
        }
        else if (from != null && PlotManager.isPlotWorld(from) && !PlotManager.isPlotWorld(to)) {
            PlotMe.plotworldedit.removeMask(p);
        }
        else if (PlotManager.isPlotWorld(to)) {
            PlotMe.plotworldedit.setMask(p);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        final Player p = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (to == null) {
            PlotMe.plotworldedit.removeMask(p);
        }
        else if (from != null && PlotManager.isPlotWorld(from) && !PlotManager.isPlotWorld(to)) {
            PlotMe.plotworldedit.removeMask(p);
        }
        else if (PlotManager.isPlotWorld(to)) {
            PlotMe.plotworldedit.setMask(p);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        final Player p = event.getPlayer();
        if (PlotManager.isPlotWorld(p) && !PlotMe.isIgnoringWELimit(p)) {
            final String lowerCaseMessage = event.getMessage().toLowerCase();
            if (lowerCaseMessage.startsWith("/gmask") || lowerCaseMessage.startsWith("//gmask") || lowerCaseMessage.startsWith("/worldedit:gmask") || lowerCaseMessage.startsWith("/worldedit:/gmask")) {
                event.setCancelled(true);
            }
            else if (lowerCaseMessage.startsWith("/up") || lowerCaseMessage.startsWith("//up") || lowerCaseMessage.startsWith("/worldedit:up") || lowerCaseMessage.startsWith("/worldedit:/up")) {
                final Plot plot = PlotManager.getPlotById(p);
                if (plot == null || !plot.isAllowed(p.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (!PlotMe.cPerms((CommandSender)p, "plotme.admin.buildanywhere") && PlotManager.isPlotWorld(p) && !PlotMe.isIgnoringWELimit(p) && (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) && p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() != Material.AIR && p.getInventory().getItemInOffHand() != null && p.getInventory().getItemInOffHand().getType() != Material.AIR) {
            final Block b = event.getClickedBlock();
            final Plot plot = PlotManager.getPlotById(b);
            if (plot != null && plot.isAllowed(p.getUniqueId())) {
                PlotMe.plotworldedit.setMask(p, b.getLocation());
            }
            else {
                event.setCancelled(true);
            }
        }
    }
}
