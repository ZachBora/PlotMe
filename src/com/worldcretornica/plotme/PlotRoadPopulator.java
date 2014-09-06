package com.worldcretornica.plotme;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class PlotRoadPopulator extends BlockPopulator {
    private double plotsize;
    private double pathsize;

    private byte wall;
    private byte floor1;
    private byte floor2;

    private byte pillarh1;
    private byte pillarh2;

    private int roadheight;

    public PlotRoadPopulator() {
        plotsize = 32;
        pathsize = 7;
        wall = 0;
        floor2 = 2;
        floor1 = 0;
        pillarh1 = 4;
        pillarh2 = 8;
        roadheight = 64;
    }

    public PlotRoadPopulator(PlotMapInfo pmi) {
        plotsize = pmi.PlotSize;
        pathsize = pmi.PathWidth;
        wall = pmi.WallBlockValue;
        floor1 = pmi.RoadMainBlockValue;
        floor2 = pmi.RoadStripeBlockValue;
        roadheight = pmi.RoadHeight;
        pillarh1 = pmi.RoadMainBlockValue;
        pillarh2 = pmi.RoadMainBlockValue;
    }

    @Override
    public void populate(World w, Random rand, Chunk chunk) {
        int cx = chunk.getX();
        int cz = chunk.getZ();

        int xx = cx << 4;
        int zz = cz << 4;

        double size = plotsize + pathsize;

        double n1;
        double n2;
        double n3;
        int mod2 = 0;
        int mod1 = 1;

        if (pathsize % 2 == 1) {
            n1 = Math.ceil(((double) pathsize) / 2) - 2;
            n2 = Math.ceil(((double) pathsize) / 2) - 1;
            n3 = Math.ceil(((double) pathsize) / 2);
            mod2 = -1;
        } else {
            n1 = Math.floor(((double) pathsize) / 2) - 2;
            n2 = Math.floor(((double) pathsize) / 2) - 1;
            n3 = Math.floor(((double) pathsize) / 2);
        }

        for (int x = 0; x < 16; x++) {
            int valx = (xx + x);

            for (int z = 0; z < 16; z++) {
                int valz = (zz + z);

                int y = roadheight;

                if ((valx - n3 + mod1) % size == 0 || (valx + n3 + mod2) % size == 0) // middle+3
                {
                    boolean found = false;
                    for (double i = n2; i >= 0; i--) {
                        if ((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        // setBlock(w, valx, y, valz, floor1, floor1id);
                        setData(w, valx, y, valz, floor1);
                    } else {
                        // setBlock(w, valx, y, valz, pillarh2, pillarh2id);
                        setData(w, valx, y, valz, pillarh2);
                        // setBlock(w, valx, y + 1, valz, wall, wallid);
                        setData(w, valx, y + 1, valz, wall);
                    }
                } else {
                    boolean found5 = false;
                    for (double i = n2; i >= 0; i--) {
                        if ((valx - i + mod1) % size == 0 || (valx + i + mod2) % size == 0) {
                            found5 = true;
                            break;
                        }
                    }

                    if (!found5) {
                        if ((valz - n3 + mod1) % size == 0 || (valz + n3 + mod2) % size == 0) {
                            // setBlock(w, valx, y, valz, pillarh1, pillarh1id);
                            setData(w, valx, y, valz, pillarh1);
                            // setBlock(w, valx, y + 1, valz, wall, wallid);
                            setData(w, valx, y + 1, valz, wall);
                        }
                    }

                    if ((valx - n2 + mod1) % size == 0 || (valx + n2 + mod2) % size == 0) // middle+2
                    {
                        if ((valz - n3 + mod1) % size == 0 || (valz + n3 + mod2) % size == 0 || (valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0) {
                            // setBlock(w, valx, y, valz, floor1, floor1id);
                            setData(w, valx, y, valz, floor1);
                        } else {
                            // setBlock(w, valx, y, valz, floor2, floor2id);
                            setData(w, valx, y, valz, floor2);
                        }
                    } else if ((valx - n1 + mod1) % size == 0 || (valx + n1 + mod2) % size == 0) // middle+2
                    {
                        if ((valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0 || (valz - n1 + mod1) % size == 0 || (valz + n1 + mod2) % size == 0) {
                            // setBlock(w, valx, y, valz, floor2, floor2id);
                            setData(w, valx, y, valz, floor2);
                        } else {
                            // setBlock(w, valx, y, valz, floor1, floor1id);
                            setData(w, valx, y, valz, floor1);
                        }
                    } else {
                        boolean found = false;
                        for (double i = n1; i >= 0; i--) {
                            if ((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0) {
                                found = true;
                                break;
                            }
                        }

                        if (found) {
                            // setBlock(w, valx, y, valz, floor1, floor1id);
                            setData(w, valx, y, valz, floor1);
                        } else {
                            if ((valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0) {
                                // setBlock(w, valx, y, valz, floor2, floor2id);
                                setData(w, valx, y, valz, floor2);
                            } else {
                                boolean found2 = false;
                                for (double i = n1; i >= 0; i--) {
                                    if ((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0) {
                                        found2 = true;
                                        break;
                                    }
                                }

                                if (found2) {
                                    // setBlock(w, valx, y, valz, floor1,
                                    // floor1id);
                                    setData(w, valx, y, valz, floor1);
                                } else {
                                    boolean found3 = false;
                                    for (double i = n3; i >= 0; i--) {
                                        if ((valx - i + mod1) % size == 0 || (valx + i + mod2) % size == 0) {
                                            found3 = true;
                                            break;
                                        }
                                    }

                                    if (found3) {
                                        // setBlock(w, valx, y, valz, floor1,
                                        // floor1id);
                                        setData(w, valx, y, valz, floor1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * @SuppressWarnings("deprecation") private void setBlock(World w, int x,
     * int y, int z, byte val, short id) { if (val != 0) { w.getBlockAt(x, y,
     * z).setTypeIdAndData(id, val, false); } else { w.getBlockAt(x, y,
     * z).setTypeId(id); } }
     */

    @SuppressWarnings("deprecation")
    private void setData(World w, int x, int y, int z, byte val) {
        w.getBlockAt(x, y, z).setData(val, false);
    }
}
