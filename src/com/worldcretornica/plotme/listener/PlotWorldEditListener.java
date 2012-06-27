package com.worldcretornica.plotme.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;
import com.worldcretornica.plotme.PlotMe;
import com.worldcretornica.plotme.PlotWorldEdit;

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
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		Player p = event.getPlayer();

		if(!PlotMe.cPerms(p, "PlotMe.admin") && PlotManager.isPlotWorld(p) && !PlotMe.isIgnoringWELimit(p))
		{
			if(event.getAction() == Action.LEFT_CLICK_BLOCK && p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR)
			{
				Block b = event.getClickedBlock();
				Plot plot = PlotManager.getPlotById(b);
				
				if(plot != null && plot.isAllowed(p.getName()))
					PlotWorldEdit.setMask(p, b.getLocation());
				else
					event.setCancelled(true);
			}
		}
	}
}
