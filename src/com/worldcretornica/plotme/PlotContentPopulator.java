package com.worldcretornica.plotme;

import org.bukkit.Chunk;
import java.util.Random;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.BlockPopulator;

public class PlotContentPopulator extends BlockPopulator {
    private double plotsize;
    private double pathsize;
    private BlockData plotfloor;
    private BlockData filling;
    private int roadheight;
    
    public PlotContentPopulator() {
        this.plotsize = 32.0;
        this.pathsize = 7.0;
        this.roadheight = 64;
        this.plotfloor = Bukkit.getServer().createBlockData(Material.GRASS_BLOCK);
        this.filling = Bukkit.getServer().createBlockData(Material.DIRT);
    }
    
    public PlotContentPopulator(final PlotMapInfo pmi) {
        this.plotsize = pmi.PlotSize;
        this.pathsize = pmi.PathWidth;
        this.roadheight = pmi.RoadHeight;
        this.plotfloor = pmi.PlotFloorBlockValue;
        this.filling = pmi.PlotFillingBlockValue;
    }
    
    public void populate(final World w, final Random rand, final Chunk chunk) {
        final double size = this.plotsize + this.pathsize;
        final int cx = chunk.getX();
        final int cz = chunk.getZ();
        final int xx = cx << 4;
        final int zz = cz << 4;
        for (int x = 0; x < 16; ++x) {
            int valx = x + xx;
            valx -= (int)Math.ceil(this.pathsize / 2.0);
            valx %= (int)size;
            if (valx < 0) {
                valx += (int)size;
            }
            final boolean modX = valx < this.plotsize;
            for (int z = 0; z < 16; ++z) {
                int valz = z + zz;
                valz -= (int)Math.ceil(this.pathsize / 2.0);
                valz %= (int)size;
                if (valz < 0) {
                    valz += (int)size;
                }
                final boolean modZ = valz < this.plotsize;
                for (int y = 0; y <= this.roadheight; ++y) {
                    if (y < this.roadheight) {
                        this.setData(w, x + xx, y, z + zz, this.filling);
                    }
                    else if (modX && modZ) {
                        this.setData(w, x + xx, y, z + zz, this.plotfloor);
                    }
                }
            }
        }
    }
    
    private void setData(final World w, final int x, final int y, final int z, final BlockData val) {
        w.getBlockAt(x, y, z).setBlockData(val, false);
    }
}
