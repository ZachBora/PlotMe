package com.worldcretornica.plotme;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.masks.RegionMask;
import com.sk89q.worldedit.regions.CuboidRegion;

public class PlotWorldEdit {
	
	public static void setMask(Player p)
	{
		setMask(p, p.getLocation());
	}
	
	public static void setMask(Player p, Location l)
	{
		World w = p.getWorld();
		
		String id = PlotManager.getPlotId(l);
				
		Location bottom = null;
		Location top = null;
		
		LocalSession session = PlotMe.we.getSession(p);
				
		if(!id.equalsIgnoreCase(""))
		{		
			Plot plot = PlotManager.getPlotById(p, id);
			
			if(plot != null && plot.isAllowed(p.getName()))
			{			
				bottom = PlotManager.getPlotBottomLoc(w, id);
				top = PlotManager.getPlotTopLoc(w, id);
				
				BukkitPlayer player = PlotMe.we.wrapPlayer(p);
				LocalWorld world = player.getWorld();
						
				Vector pos1 = new Vector(bottom.getBlockX(), bottom.getBlockY(), bottom.getBlockZ());
				Vector pos2 = new Vector(top.getBlockX(), top.getBlockY(), top.getBlockZ());
						
				CuboidRegion cr = new CuboidRegion(world, pos1, pos2);
				
				RegionMask rm = new RegionMask(cr);
				
				session.setMask(rm);
				return;
			}
		}
		
		if(bottom == null || top == null){
			bottom = new Location(w, 0, 0, 0);
			top = new Location(w, 0, 0, 0);
		}
		
		if(session.getMask() == null)
		{
			BukkitPlayer player = PlotMe.we.wrapPlayer(p);
			LocalWorld world = player.getWorld();
					
			Vector pos1 = new Vector(bottom.getBlockX(), bottom.getBlockY(), bottom.getBlockZ());
			Vector pos2 = new Vector(top.getBlockX(), top.getBlockY(), top.getBlockZ());
					
			CuboidRegion cr = new CuboidRegion(world, pos1, pos2);
			
			RegionMask rm = new RegionMask(cr);
			
			session.setMask(rm);
		}
	}

	public static void removeMask(Player p)
	{
		LocalSession session = PlotMe.we.getSession(p);
		session.setMask(null);
	}	
}
