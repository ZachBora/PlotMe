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

        for (int x = 0; x < 16; x++) {
            int valx = x + xx;

            valx -= Math.ceil(pathsize / 2);
            valx = (valx % (int) size);
            if (valx < 0) valx += size;

            boolean modX = valx < plotsize;

            for (int z = 0; z < 16; z++) {
                int valz = z + zz;

                valz -= Math.ceil(pathsize / 2);
                valz = (valz % (int) size);
                if (valz < 0) valz += size;

                boolean modZ = valz < plotsize;

                for (int y = 0; y <= roadheight; y++) {
                    if (y < roadheight) {
                        setData(w, x + xx, y, z + zz, filling);
                    } else {
                        if (modX && modZ) {
                            setData(w, x + xx, y, z + zz, plotfloor);
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
