package com.worldcretornica.plotme;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class PlotContentPopulator extends BlockPopulator {
    private double plotsize;
    private double pathsize;

    private byte plotfloor;
    private byte filling;

    private int roadheight;

    public PlotContentPopulator() {
        plotsize = 32;
        pathsize = 7;
        roadheight = 64;
        plotfloor = 0;
        filling = 0;
    }

    public PlotContentPopulator(PlotMapInfo pmi) {
        plotsize = pmi.PlotSize;
        pathsize = pmi.PathWidth;
        roadheight = pmi.RoadHeight;
        plotfloor = pmi.PlotFloorBlockValue;
        filling = pmi.PlotFillingBlockValue;
    }

    @Override
	public void populate(World w, Random rand, Chunk chunk) {
        double size = plotsize + pathsize;

        int cx = chunk.getX();
        int cz = chunk.getZ();
        
        int xx = cx << 4;
		int zz = cz << 4;
		
		int valx;
        int valz;
        
        double pathmod1;
        double pathmod2 = 0;
        
        if (pathsize % 2 == 1) {
            pathmod1 = Math.ceil(((double) pathsize) / 2);
        } else {
            pathmod1 = Math.floor(((double) pathsize) / 2);
            pathmod2 = 1;
        }
        
        for (int x = 0; x < 16; x++) {
            valx = x + xx;

            boolean modX;
            if ((valx - pathmod1) < 0) {
                modX = Math.abs(valx) >= (pathmod1 + pathmod2) && Math.abs((Math.abs(valx) - pathmod1 - pathmod2) % size) < plotsize; 
            } else {
                modX = valx >= pathmod1 && ((valx - pathmod1) % size) < plotsize;
            }
            
            for (int z = 0; z < 16; z++) {
                valz = z + zz;

                boolean modZ;
                if ((valz - pathmod1) < 0) {
                    modZ = Math.abs(valz) >= (pathmod1 + pathmod2) && Math.abs((Math.abs(valz) - pathmod1 - pathmod2) % size) < plotsize;
                } else {
                    modZ = valz >= pathmod1 && ((valz - pathmod1) % size) < plotsize;
                }
                
                for (int y = 0; y <= roadheight; y++) {
                    if (y < roadheight) {
                        setData(w, valx, y, valz, filling);
                    } else {
                        if (modX && modZ) {
                            setData(w, valx, y, valz, plotfloor);
                        }
                    }
                }
            }
        }
	}

    @SuppressWarnings("deprecation")
    private void setData(World w, int x, int y, int z, byte val) {
        w.getBlockAt(x, y, z).setData(val, false);
    }
}
