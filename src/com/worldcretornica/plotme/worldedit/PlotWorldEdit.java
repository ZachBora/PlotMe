package com.worldcretornica.plotme.worldedit;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface PlotWorldEdit {
	
	public void setMask(Player p);
	
	public void setMask(Player p, Location l);

	public void removeMask(Player p);
}
