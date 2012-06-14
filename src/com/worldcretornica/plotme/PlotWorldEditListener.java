package com.worldcretornica.plotme;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlotWorldEditListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerMove(final PlayerMoveEvent event)
	{	
		Player p = event.getPlayer();
		if(PlotManager.isPlotWorld(p) && !PlotMe.isIgnoringWELimit(p))
		{
			PlotWorldEdit.setMask(p);
		}else{
			PlotWorldEdit.removeMask(p);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event)
	{		
		Player p = event.getPlayer();
		if(PlotManager.isPlotWorld(p) && !PlotMe.isIgnoringWELimit(p))
		{
			if(event.getMessage().startsWith("//gmask") || event.getMessage().startsWith("//up"))
			{
				event.setCancelled(true);
			}
		}
	}	
}
