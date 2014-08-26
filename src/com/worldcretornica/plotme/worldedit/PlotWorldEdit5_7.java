package com.worldcretornica.plotme.worldedit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.masks.RegionMask;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;
import com.worldcretornica.plotme.PlotMe;
import com.sk89q.worldedit.masks.Mask;

@SuppressWarnings({ "deprecation", "unused" })
public class PlotWorldEdit5_7 implements PlotWorldEdit {
	
	public void setMask(Player p)
	{
		setMask(p, p.getLocation());
	}
	
	public void setMask(Player p, Location l)
	{
		World w = p.getWorld();
		
		String id = PlotManager.getPlotId(l);
				
		Location bottom = null;
		Location top = null;
		
		LocalSession session = PlotMe.worldeditplugin.getSession(p);
				
		if(!id.equalsIgnoreCase(""))
		{		
			Plot plot = PlotManager.getPlotById(p, id);
			
			if(plot != null && plot.isAllowed(p.getUniqueId()))
			{			
				bottom = PlotManager.getPlotBottomLoc(w, id);
				top = PlotManager.getPlotTopLoc(w, id);
				
				BukkitPlayer player = PlotMe.worldeditplugin.wrapPlayer(p);
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
		
		
		Object result = null;
		
		try {
			Class<? extends LocalSession> csession = session.getClass();
			Method method = csession.getMethod("getMask", (Class<?>[]) null);
			result = method.invoke(session, (Object[]) null);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		if(result == null)
		{
			BukkitPlayer player = PlotMe.worldeditplugin.wrapPlayer(p);
			LocalWorld world = player.getWorld();
					
			Vector pos1 = new Vector(bottom.getBlockX(), bottom.getBlockY(), bottom.getBlockZ());
			Vector pos2 = new Vector(top.getBlockX(), top.getBlockY(), top.getBlockZ());
					
			CuboidRegion cr = new CuboidRegion(world, pos1, pos2);
			
			RegionMask rm = new RegionMask(cr);
			
			session.setMask(rm);
		}
	}

	public void removeMask(Player p)
	{
		LocalSession session = PlotMe.worldeditplugin.getSession(p);
		RegionMask mask = null;
		session.setMask(mask);
	}
}
