package com.worldcretornica.plotme.worldedit;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface PlotWorldEdit {
    void setMask(final Player player);
    
    void setMask(final Player player, final Location location);
    
    void removeMask(final Player player);
}
