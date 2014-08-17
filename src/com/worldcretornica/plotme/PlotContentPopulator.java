package com.worldcretornica.plotme;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class PlotContentPopulator extends BlockPopulator {
	private double plotsize;
	private double pathsize;
	
	private short plotfloorid;
	private byte plotfloor;
    private short fillingid;
    private byte filling;

	private int roadheight;

	public PlotContentPopulator() {
		plotsize = 32;
		pathsize = 7;
		roadheight = 64;
		plotfloorid = 2;
		plotfloor = 0;
		fillingid = 3;
		filling = 0;
	}

	public PlotContentPopulator(PlotMapInfo pmi) {
		plotsize = pmi.PlotSize;
		pathsize = pmi.PathWidth;
		roadheight = pmi.RoadHeight;
		plotfloorid = pmi.PlotFloorBlockId;
		plotfloor = pmi.PlotFloorBlockValue;
		fillingid = pmi.PlotFillingBlockId;
		filling = pmi.PlotFillingBlockValue;
	}

	@Override
	public void populate(World w, Random rand, Chunk chunk) {
        double size = plotsize + pathsize;
        int valx;
        int valz;

        int cx = chunk.getX();
        int cz = chunk.getZ();
        
        int xx = cx << 4;
		int zz = cz << 4;

        double n1;
        double n2;
        double n3;
        int mod2 = 0;
        int mod1 = 1;

        if (pathsize % 2 == 1) {
            n1 = Math.ceil(((double) pathsize) / 2) - 2;
            n2 = Math.ceil(((double) pathsize) / 2) - 1;
            n3 = Math.ceil(((double) pathsize) / 2);
        } else {
            n1 = Math.floor(((double) pathsize) / 2) - 2;
            n2 = Math.floor(((double) pathsize) / 2) - 1;
            n3 = Math.floor(((double) pathsize) / 2);
        }

        if (pathsize % 2 == 1) {
            mod2 = -1;
        }

        for (int x = 0; x < 16; x++) {
            valx = (cx * 16 + x);

            for (int z = 0; z < 16; z++) {
                int height = roadheight + 2;
                valz = (cz * 16 + z);

                for (int y = 0; y < height; y++) {
                    if (y == 0) {
                    } else if (y == roadheight) {
                        if ((valx - n3 + mod1) % size == 0 || (valx + n3 + mod2) % size == 0) // middle+3
                        {
                            boolean found = false;
                            for (double i = n2; i >= 0; i--) {
                                if ((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0) {
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                setBlock(w, x + xx, y, z + zz, filling, fillingid);
                            }
                        } else if ((valx - n2 + mod1) % size == 0 || (valx + n2 + mod2) % size == 0) // middle+2
                        {
                        } else if ((valx - n1 + mod1) % size == 0 || (valx + n1 + mod2) % size == 0) // middle+2
                        {
                        } else {
                            boolean found = false;
                            for (double i = n1; i >= 0; i--) {
                                if ((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0) {
                                    found = true;
                                    break;
                                }
                            }

                            if (found) {
                            } else {
                                if ((valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0) {
                                } else {
                                    boolean found2 = false;
                                    for (double i = n1; i >= 0; i--) {
                                        if ((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0) {
                                            found2 = true;
                                            break;
                                        }
                                    }

                                    if (found2) {
                                    } else {
                                        boolean found3 = false;
                                        for (double i = n3; i >= 0; i--) {
                                            if ((valx - i + mod1) % size == 0 || (valx + i + mod2) % size == 0) {
                                                found3 = true;
                                                break;
                                            }
                                        }

                                        if (!found3) {
                                            setBlock(w, x + xx, y, z + zz, plotfloor, plotfloorid);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (y == (roadheight + 1)) {
                    } else {
                        setBlock(w, x + xx, y, z + zz, filling, fillingid);
                    }
                }
            }
        }
	}

	@SuppressWarnings("deprecation")
	private void setBlock(World w, int x, int y, int z, byte val, short id) {
		if (val != 0) {
			w.getBlockAt(x, y, z).setTypeIdAndData(id, val, false);
		} else {
			w.getBlockAt(x, y, z).setTypeId(id);
		}
	}
}
