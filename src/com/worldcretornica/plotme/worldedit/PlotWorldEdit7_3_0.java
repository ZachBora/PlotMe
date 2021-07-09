package com.worldcretornica.plotme.worldedit;

import com.worldcretornica.plotme.Plot;
import com.sk89q.worldedit.LocalSession;
import org.bukkit.World;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.function.mask.RegionMask;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.math.BlockVector3;
import com.worldcretornica.plotme.PlotMe;
import com.worldcretornica.plotme.PlotManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlotWorldEdit7_3_0 implements PlotWorldEdit {
    public void setMask(final Player p) {
        this.setMask(p, p.getLocation());
    }
    
    public void setMask(final Player p, final Location l) {
        final World w = p.getWorld();
        final String id = PlotManager.getPlotId(l);
        Location bottom = null;
        Location top = null;
        final LocalSession session = PlotMe.worldeditplugin.getSession(p);
        if (!id.equalsIgnoreCase("")) {
            final Plot plot = PlotManager.getPlotById(p, id);
            if (plot != null && plot.isAllowed(p.getUniqueId())) {
                bottom = PlotManager.getPlotBottomLoc(w, id);
                top = PlotManager.getPlotTopLoc(w, id);
                final LocalSession localsession = PlotMe.worldeditplugin.getSession(p);
                final com.sk89q.worldedit.world.World world = localsession.getSelectionWorld();
                final BlockVector3 pos1 = BlockVector3.at(bottom.getBlockX(), bottom.getBlockY(), bottom.getBlockZ());
                final BlockVector3 pos2 = BlockVector3.at(top.getBlockX(), top.getBlockY(), top.getBlockZ());
                final CuboidRegion cr = new CuboidRegion(world, pos1, pos2);
                final RegionMask rm = new RegionMask((Region)cr);
                session.setMask(rm);
                return;
            }
        }
        if (bottom == null || top == null) {
            bottom = new Location(w, 0.0, 0.0, 0.0);
            top = new Location(w, 0.0, 0.0, 0.0);
        }
        if (session.getMask() == null) {
            final LocalSession localsession2 = PlotMe.worldeditplugin.getSession(p);
            final com.sk89q.worldedit.world.World world2 = localsession2.getSelectionWorld();
            final BlockVector3 pos3 = BlockVector3.at(bottom.getBlockX(), bottom.getBlockY(), bottom.getBlockZ());
            final BlockVector3 pos4 = BlockVector3.at(top.getBlockX(), top.getBlockY(), top.getBlockZ());
            final CuboidRegion cr2 = new CuboidRegion(world2, pos3, pos4);
            final RegionMask rm2 = new RegionMask((Region)cr2);
            session.setMask((Mask)rm2);
        }
    }
    
    public void removeMask(final Player p) {
        final LocalSession session = PlotMe.worldeditplugin.getSession(p);
        final Mask mask = null;
        session.setMask(mask);
    }
}
