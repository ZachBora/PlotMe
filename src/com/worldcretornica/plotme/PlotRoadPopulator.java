package com.worldcretornica.plotme;

import org.bukkit.Chunk;
import java.util.Random;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.BlockPopulator;

public class PlotRoadPopulator extends BlockPopulator {
    private double plotsize;
    private double pathsize;
    private BlockData wall;
    private BlockData floor1;
    private BlockData floor2;
    private BlockData pillarh1;
    private BlockData pillarh2;
    private int roadheight;
    
    public PlotRoadPopulator() {
        this.plotsize = 32.0;
        this.pathsize = 7.0;
        this.wall = Bukkit.getServer().createBlockData(Material.SMOOTH_STONE_SLAB);
        this.floor2 = Bukkit.getServer().createBlockData(Material.BIRCH_PLANKS);
        this.floor1 = Bukkit.getServer().createBlockData(Material.OAK_PLANKS);
        this.pillarh1 = Bukkit.getServer().createBlockData(Material.OAK_PLANKS);
        this.pillarh2 = Bukkit.getServer().createBlockData(Material.OAK_PLANKS);
        this.roadheight = 64;
    }
    
    public PlotRoadPopulator(final PlotMapInfo pmi) {
        this.plotsize = pmi.PlotSize;
        this.pathsize = pmi.PathWidth;
        this.wall = pmi.WallBlockValue;
        this.floor1 = pmi.RoadMainBlockValue;
        this.floor2 = pmi.RoadStripeBlockValue;
        this.roadheight = pmi.RoadHeight;
        this.pillarh1 = pmi.RoadMainBlockValue;
        this.pillarh2 = pmi.RoadMainBlockValue;
    }
    
    public void populate(final World w, final Random rand, final Chunk chunk) {
        final int cx = chunk.getX();
        final int cz = chunk.getZ();
        final int xx = cx << 4;
        final int zz = cz << 4;
        final double size = this.plotsize + this.pathsize;
        int mod2 = 0;
        final int mod3 = 1;
        double n1;
        double n2;
        double n3;
        if (this.pathsize % 2.0 == 1.0) {
            n1 = Math.ceil(this.pathsize / 2.0) - 2.0;
            n2 = Math.ceil(this.pathsize / 2.0) - 1.0;
            n3 = Math.ceil(this.pathsize / 2.0);
            mod2 = -1;
        }
        else {
            n1 = Math.floor(this.pathsize / 2.0) - 2.0;
            n2 = Math.floor(this.pathsize / 2.0) - 1.0;
            n3 = Math.floor(this.pathsize / 2.0);
        }
        for (int x = 0; x < 16; ++x) {
            final int valx = xx + x;
            for (int z = 0; z < 16; ++z) {
                final int valz = zz + z;
                final int y = this.roadheight;
                if ((valx - n3 + mod3) % size == 0.0 || (valx + n3 + mod2) % size == 0.0) {
                    boolean found = false;
                    for (double i = n2; i >= 0.0; --i) {
                        if ((valz - i + mod3) % size == 0.0 || (valz + i + mod2) % size == 0.0) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        this.setData(w, valx, y, valz, this.floor1);
                    }
                    else {
                        this.setData(w, valx, y, valz, this.pillarh2);
                        this.setData(w, valx, y + 1, valz, this.wall);
                    }
                }
                else {
                    boolean found2 = false;
                    for (double i = n2; i >= 0.0; --i) {
                        if ((valx - i + mod3) % size == 0.0 || (valx + i + mod2) % size == 0.0) {
                            found2 = true;
                            break;
                        }
                    }
                    if (!found2 && ((valz - n3 + mod3) % size == 0.0 || (valz + n3 + mod2) % size == 0.0)) {
                        this.setData(w, valx, y, valz, this.pillarh1);
                        this.setData(w, valx, y + 1, valz, this.wall);
                    }
                    if ((valx - n2 + mod3) % size == 0.0 || (valx + n2 + mod2) % size == 0.0) {
                        if ((valz - n3 + mod3) % size == 0.0 || (valz + n3 + mod2) % size == 0.0 || (valz - n2 + mod3) % size == 0.0 || (valz + n2 + mod2) % size == 0.0) {
                            this.setData(w, valx, y, valz, this.floor1);
                        }
                        else {
                            this.setData(w, valx, y, valz, this.floor2);
                        }
                    }
                    else if ((valx - n1 + mod3) % size == 0.0 || (valx + n1 + mod2) % size == 0.0) {
                        if ((valz - n2 + mod3) % size == 0.0 || (valz + n2 + mod2) % size == 0.0 || (valz - n1 + mod3) % size == 0.0 || (valz + n1 + mod2) % size == 0.0) {
                            this.setData(w, valx, y, valz, this.floor2);
                        }
                        else {
                            this.setData(w, valx, y, valz, this.floor1);
                        }
                    }
                    else {
                        boolean found3 = false;
                        for (double j = n1; j >= 0.0; --j) {
                            if ((valz - j + mod3) % size == 0.0 || (valz + j + mod2) % size == 0.0) {
                                found3 = true;
                                break;
                            }
                        }
                        if (found3) {
                            this.setData(w, valx, y, valz, this.floor1);
                        }
                        else if ((valz - n2 + mod3) % size == 0.0 || (valz + n2 + mod2) % size == 0.0) {
                            this.setData(w, valx, y, valz, this.floor2);
                        }
                        else {
                            boolean found4 = false;
                            for (double k = n1; k >= 0.0; --k) {
                                if ((valz - k + mod3) % size == 0.0 || (valz + k + mod2) % size == 0.0) {
                                    found4 = true;
                                    break;
                                }
                            }
                            if (found4) {
                                this.setData(w, valx, y, valz, this.floor1);
                            }
                            else {
                                boolean found5 = false;
                                for (double l = n3; l >= 0.0; --l) {
                                    if ((valx - l + mod3) % size == 0.0 || (valx + l + mod2) % size == 0.0) {
                                        found5 = true;
                                        break;
                                    }
                                }
                                if (found5) {
                                    this.setData(w, valx, y, valz, this.floor1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void setData(final World w, final int x, final int y, final int z, final BlockData val) {
        w.getBlockAt(x, y, z).setBlockData(val, false);
    }
}
