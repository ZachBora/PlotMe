package com.worldcretornica.plotme.listener;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlotDenyListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();

		if (p.getWorld() == Bukkit.getWorld("plotworld") && !p.hasPermission("plotme.admin.bypassdeny")) {
			Location to = event.getTo();

			String idTo = PlotManager.getPlotId(to);

			if (!idTo.equalsIgnoreCase("")) {
				Plot plot = PlotManager.getPlotById(p, idTo);

				if (plot != null && plot.isDenied(p.getUniqueId())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player p = event.getPlayer();

		if (p.getWorld() == Bukkit.getWorld("plotworld") && !p.hasPermission("plotme.admin.bypassdeny")) {
			Location to = event.getTo();

			String idTo = PlotManager.getPlotId(to);

			if (!idTo.equalsIgnoreCase("")) {
				Plot plot = PlotManager.getPlotById(p, idTo);

				if (plot != null && plot.isDenied(p.getUniqueId())) {
					event.setTo(PlotManager.getPlotHome(p.getWorld(), plot));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();

		if (p.getWorld() == Bukkit.getWorld("plotworld") && !p.hasPermission("plotme.admin.bypassdeny")) {
			String id = PlotManager.getPlotId(p);

			if (!id.equalsIgnoreCase("")) {
				Plot plot = PlotManager.getPlotById(p, id);

				if (plot != null && plot.isDenied(p.getUniqueId())) {
					p.teleport(PlotManager.getPlotHome(p.getWorld(), plot));
				}
			}
		}
	}
}
