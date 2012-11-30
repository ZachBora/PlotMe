package com.worldcretornica.plotme.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.sk89q.worldedit.LocalSession;
import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;
import com.worldcretornica.plotme.PlotMe;
import com.worldcretornica.plotme.PlotWorldEdit;

public class PlotWorldEditListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event)
	{		
		Player p = event.getPlayer();
		String cmd = event.getMessage();

		if(PlotManager.isPlotWorld(p) && !PlotMe.isIgnoringWELimit(p))
		{
			if(cmd.startsWith("//gmask") || cmd.startsWith("//redo") || cmd.startsWith("//repl"))
			{
				event.setCancelled(true);
			}else if(cmd.indexOf("//") == 0
				  || cmd.startsWith("/up")
				  || cmd.startsWith("/brush")
				  || cmd.startsWith("/pumpkins")
				  || cmd.startsWith("/forestgen")
				  || cmd.startsWith("/tree"))
			{
				String id = PlotManager.getPlotId(p.getLocation());
				Plot plot = PlotManager.getPlotById(p);
				LocalSession session = PlotMe.we.getSession(p);
				
				if(plot != null && plot.isAllowed(p.getName())) {
					PlotWorldEdit.setMask(p, id);
				} else {
					if(PlotManager.getPlots(p).values().size() > 0) {
						if(session.getMask() == null) {
							String id1 = PlotManager.getPlots(p).values().iterator().next().id;
							PlotWorldEdit.setMask(p, id1);
						}
					} else {
						event.setCancelled(true);
						p.sendMessage("WorldEdit disabled until you have at least one plot");
					}
				}
			}
		}
	}
}