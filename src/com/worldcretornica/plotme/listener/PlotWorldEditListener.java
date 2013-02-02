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
import org.bukkit.event.player.PlayerJoinEvent;
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
		Location from = event.getFrom();
		Location to = event.getTo();
		boolean changemask = false;
		
		if(!from.getWorld().getName().equalsIgnoreCase(to.getWorld().getName()))
		{
			changemask = true;
		}
		else if(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ())
		{
			String idFrom = PlotManager.getPlotId(from);
			String idTo = PlotManager.getPlotId(to);
			
			if(!idFrom.equalsIgnoreCase(idTo))
			{
				changemask = true;
			}
		}
		
		if(changemask)
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
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event)
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
	public void onPlayerTeleport(final PlayerTeleportEvent event)
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

		if(!PlotMe.cPerms(p, "plotme.admin.buildanywhere") && PlotManager.isPlotWorld(p) && !PlotMe.isIgnoringWELimit(p))
		{
			if((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) 
					&& p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR)
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

/*
public class PlotWorldEditListener implements Listener 
{
	
    public Location[] GetSelection(Player pl)
    {
 	   
    	WorldEdit we = PlotMe.we.getWorldEdit(); //((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")).getWorldEdit();
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
			if(event.getMessage().startsWith("//wand")
					|| event.getMessage().startsWith("//undo")
					|| event.getMessage().startsWith("//redo"))
			{
				return;
			}
			if(event.getMessage().startsWith("//gmask"))
			{
				event.setCancelled(true);
			} 
			else if(event.getMessage().startsWith("//sphere")
					|| event.getMessage().startsWith("//hsphere")
					|| event.getMessage().startsWith("//pyramid")
					|| event.getMessage().startsWith("//hpyramid")
					|| event.getMessage().startsWith("//cylinder")
					|| event.getMessage().startsWith("//hcylinder")
					|| event.getMessage().startsWith("/brush")
					|| event.getMessage().startsWith("//tree")) 
			{
				Plot plot = PlotManager.getPlotById(p);
				
				if(plot == null || !plot.isAllowed(p.getName()))
				{
					event.setCancelled(true);
				} 
				else 
				{
					PlotWorldEdit.setMask(p);
				}
			}
			else if(event.getMessage().indexOf("//") == 0)
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
}*/