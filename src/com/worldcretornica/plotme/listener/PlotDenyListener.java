package com.worldcretornica.plotme.listener;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;
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
	public void onPlayerMove(final PlayerMoveEvent event) {
		Player p = event.getPlayer();

		if (PlotManager.isPlotWorld(p) && !p.hasPermission("plotme.admin.bypassdeny")) {
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
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		Player p = event.getPlayer();

		if (PlotManager.isPlotWorld(p) && !p.hasPermission("plotme.admin.bypassdeny")) {
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
	public void onPlayerJoin(final PlayerJoinEvent event) {
		Player p = event.getPlayer();

		if (PlotManager.isPlotWorld(p) && !p.hasPermission("plotme.admin.bypassdeny")) {
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
