package com.worldcretornica.plotme.listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import com.worldcretornica.plotme.Plot;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import com.worldcretornica.plotme.PlotMe;
import com.worldcretornica.plotme.PlotManager;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.Listener;

public class PlotDenyListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player p = event.getPlayer();
        if (PlotManager.isPlotWorld(p) && !PlotMe.cPerms((CommandSender)p, "plotme.admin.bypassdeny")) {
            final Location to = event.getTo();
            final String idTo = PlotManager.getPlotId(to);
            if (!idTo.equalsIgnoreCase("")) {
                final Plot plot = PlotManager.getPlotById(p, idTo);
                if (plot != null && plot.isDenied(p.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player p = event.getPlayer();
        if (PlotManager.isPlotWorld(p) && !PlotMe.cPerms((CommandSender)p, "plotme.admin.bypassdeny")) {
            final Location to = event.getTo();
            final String idTo = PlotManager.getPlotId(to);
            if (!idTo.equalsIgnoreCase("")) {
                final Plot plot = PlotManager.getPlotById(p, idTo);
                if (plot != null && plot.isDenied(p.getUniqueId())) {
                    event.setTo(PlotManager.getPlotHome(p.getWorld(), plot));
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        if (PlotManager.isPlotWorld(p) && !PlotMe.cPerms((CommandSender)p, "plotme.admin.bypassdeny")) {
            final String id = PlotManager.getPlotId(p);
            if (!id.equalsIgnoreCase("")) {
                final Plot plot = PlotManager.getPlotById(p, id);
                if (plot != null && plot.isDenied(p.getUniqueId())) {
                    p.teleport(PlotManager.getPlotHome(p.getWorld(), plot));
                }
            }
        }
    }
}
