package com.worldcretornica.plotme.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;
import com.worldcretornica.plotme.PlotMe;

public class PlotDenyListener implements Listener
{

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMove(final PlayerMoveEvent event)
	{
		Player p = event.getPlayer();
		
		if(PlotManager.isPlotWorld(p) && !PlotMe.cPerms(p, "plotme.admin.bypassdeny"))
		{
			Location to = event.getTo();
			
			String idTo = PlotManager.getPlotId(to);
			
			if(!idTo.equalsIgnoreCase(""))
			{
				Plot plot = PlotManager.getPlotById(p, idTo);
				
				if(plot != null && plot.isDenied(p.getName()))
				{
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerTeleport(final PlayerTeleportEvent event)
	{
		Player p = event.getPlayer();
		
		if(PlotManager.isPlotWorld(p) && !PlotMe.cPerms(p, "plotme.admin.bypassdeny"))
		{
			Location to = event.getTo();
			
			String idTo = PlotManager.getPlotId(to);
			
			if(!idTo.equalsIgnoreCase(""))
			{
				Plot plot = PlotManager.getPlotById(p, idTo);
				
				if(plot != null && plot.isDenied(p.getName()))
				{
					event.setTo(PlotManager.getPlotHome(p.getWorld(), plot));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		Player p = event.getPlayer();
		
		if(PlotManager.isPlotWorld(p) && !PlotMe.cPerms(p, "plotme.admin.bypassdeny"))
		{
			String id = PlotManager.getPlotId(p);
			
			if(!id.equalsIgnoreCase(""))
			{
				Plot plot = PlotManager.getPlotById(p, id);
				
				if(plot != null && plot.isDenied(p.getName()))
				{
					p.teleport(PlotManager.getPlotHome(p.getWorld(), plot));
				}
			}
		}
	}
}
