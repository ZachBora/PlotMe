package com.worldcretornica.plotme.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;
import com.worldcretornica.plotme.PlotMe;
import com.worldcretornica.plotme.PlotWorldEdit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PlotWorldEditListener implements Listener {
	
    public Location[] GetSelection(Player pl){
 	   
    	WorldEdit we=((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")).getWorldEdit();
        Location[] rst = new Location[2];
        LocalWorld lw = BukkitUtil.getLocalWorld(pl.getWorld());
        LocalSession ls = we.getSession(pl.getName());
        Region rs;
        try {
            rs = ls.getSelection(lw);
            rst[0]=new Location(pl.getWorld(), rs.getMinimumPoint().getX(), rs.getMinimumPoint().getY(), rs.getMinimumPoint().getZ());
            rst[1]=new Location(pl.getWorld(), rs.getMaximumPoint().getX(), rs.getMaximumPoint().getY(), rs.getMaximumPoint().getZ());
        } catch (Exception e) {
            rst[0]=null;
            rst[1]=null;
        }
        return rst;
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
			} else if(event.getMessage().startsWith("//sphere")
					|| event.getMessage().startsWith("//hsphere")
					|| event.getMessage().startsWith("//pyramid")
					|| event.getMessage().startsWith("//hpyramid")
					|| event.getMessage().startsWith("//cylinder")
					|| event.getMessage().startsWith("//hcylinder")
					|| event.getMessage().startsWith("/brush")) 
			{
				Plot plot = PlotManager.getPlotById(p);
				
				if(plot == null || !plot.isAllowed(p.getName()))
				{
					event.setCancelled(true);
				} else {
					PlotWorldEdit.setMask(p);
				}
			}else if(event.getMessage().indexOf("//") == 0)
			{
				Location pos1 = GetSelection(p) [0];
				Location pos2 = GetSelection(p) [1];
				
				Plot plot0 = PlotManager.getPlotById(p);
				Plot plot1 = PlotManager.getPlotById(pos1);
				Plot plot2 = PlotManager.getPlotById(pos2);

				if(plot1 == null || !plot1.isAllowed(p.getName()) || plot2 == null || !plot2.isAllowed(p.getName()) || plot0 != plot1 || plot0 != plot2)
				{
					event.setCancelled(true);
				} else {
					PlotWorldEdit.setMask(p, pos1);
				}
			}
		}
	}
}
