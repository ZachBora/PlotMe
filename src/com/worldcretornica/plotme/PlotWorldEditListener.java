package com.worldcretornica.plotme;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlotWorldEditListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerMove(final PlayerMoveEvent event)
	{	
		Player p = event.getPlayer();
		if(PlotManager.isPlotWorld(p))
		{
			if(!PlotMe.isIgnoringWELimit(p))
				PlotWorldEdit.setMask(p);
			else
				PlotWorldEdit.removeMask(p);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerWorldChange(final PlayerTeleportEvent event)
	{
		Player p = event.getPlayer();
		Location from = event.getFrom();
		Location to = event.getTo();
		if(PlotManager.isPlotWorld(from) && !PlotManager.isPlotWorld(to))
		{
			PlotWorldEdit.removeMask(p);
		}else if(!PlotManager.isPlotWorld(from) && PlotManager.isPlotWorld(to))
		{
			PlotWorldEdit.setMask(p);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event)
	{		
		Player p = event.getPlayer();
		
		if(PlotManager.isPlotWorld(p) && !PlotMe.isIgnoringWELimit(p))
		{
			if(event.getMessage().startsWith("//gmask"))
			{
				event.setCancelled(true);
			}else if(event.getMessage().startsWith("//up"))
			{
				Plot plot = PlotManager.getPlotById(p);
				
				if(plot == null || !plot.isAllowed(p.getName()))
				{
					event.setCancelled(true);
				}
			}
		}
	}
}
