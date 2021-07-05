package com.worldcretornica.plotme;

import org.bukkit.Location;
import java.util.Arrays;
import org.bukkit.generator.BlockPopulator;
import java.util.List;
import org.bukkit.block.Biome;
import java.util.Random;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;

public class PlotGen extends ChunkGenerator {
    private double plotsize;
    private double pathsize;
    private Material bottom;
    private Material wall;
    private Material plotfloor;
    private Material filling;
    private Material floor1;
    private Material floor2;
    private int roadheight;
    private PlotMapInfo temppmi;
    
    public PlotGen() {
        this.plotsize = 32.0;
        this.pathsize = 7.0;
        this.bottom = Material.BEDROCK;
        this.wall = Material.SMOOTH_STONE_SLAB;
        this.plotfloor = Material.GRASS_BLOCK;
        this.filling = Material.DIRT;
        this.roadheight = 64;
        this.floor1 = Material.OAK_PLANKS;
        this.floor2 = Material.OAK_PLANKS;
        this.temppmi = null;
        PlotMe.logger.warning("Unable to find configuration, using defaults");
    }
    
    public PlotGen(final PlotMapInfo pmi) {
        this.plotsize = pmi.PlotSize;
        this.pathsize = pmi.PathWidth;
        this.bottom = pmi.BottomBlockId;
        this.wall = pmi.WallBlockId;
        this.plotfloor = pmi.PlotFloorBlockId;
        this.filling = pmi.PlotFillingBlockId;
        this.roadheight = pmi.RoadHeight;
        this.floor1 = pmi.RoadMainBlockId;
        this.floor2 = pmi.RoadStripeBlockId;
        this.temppmi = pmi;
    }
    
    public ChunkData generateChunkData(final World world, final Random random, final int cx, final int cz, final ChunkGenerator.BiomeGrid biomes) {
        final ChunkData result = this.createChunkData(world);
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
        }
        else {
            n1 = Math.floor(this.pathsize / 2.0) - 2.0;
            n2 = Math.floor(this.pathsize / 2.0) - 1.0;
            n3 = Math.floor(this.pathsize / 2.0);
        }
        if (this.pathsize % 2.0 == 1.0) {
            mod2 = -1;
        }
        for (int x = 0; x < 16; ++x) {
            final int valx = cx * 16 + x;
            for (int z = 0; z < 16; ++z) {
                final int height = this.roadheight + 2;
                final int valz = cz * 16 + z;
                for (int y = 0; y < height; ++y) {
                    biomes.setBiome(x, y, z, Biome.PLAINS);
                    if (y == 0) {
                        this.setBlock(result, x, y, z, this.bottom);
                    }
                    else if (y == this.roadheight) {
                        if ((valx - n3 + mod3) % size == 0.0 || (valx + n3 + mod2) % size == 0.0) {
                            boolean found = false;
                            for (double i = n2; i >= 0.0; --i) {
                                if ((valz - i + mod3) % size == 0.0 || (valz + i + mod2) % size == 0.0) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {
                                this.setBlock(result, x, y, z, this.floor1);
                            }
                            else {
                                this.setBlock(result, x, y, z, this.filling);
                            }
                        }
                        else if ((valx - n2 + mod3) % size == 0.0 || (valx + n2 + mod2) % size == 0.0) {
                            if ((valz - n3 + mod3) % size == 0.0 || (valz + n3 + mod2) % size == 0.0 || (valz - n2 + mod3) % size == 0.0 || (valz + n2 + mod2) % size == 0.0) {
                                this.setBlock(result, x, y, z, this.floor1);
                            }
                            else {
                                this.setBlock(result, x, y, z, this.floor2);
                            }
                        }
                        else if ((valx - n1 + mod3) % size == 0.0 || (valx + n1 + mod2) % size == 0.0) {
                            if ((valz - n2 + mod3) % size == 0.0 || (valz + n2 + mod2) % size == 0.0 || (valz - n1 + mod3) % size == 0.0 || (valz + n1 + mod2) % size == 0.0) {
                                this.setBlock(result, x, y, z, this.floor2);
                            }
                            else {
                                this.setBlock(result, x, y, z, this.floor1);
                            }
                        }
                        else {
                            boolean found = false;
                            for (double i = n1; i >= 0.0; --i) {
                                if ((valz - i + mod3) % size == 0.0 || (valz + i + mod2) % size == 0.0) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {
                                this.setBlock(result, x, y, z, this.floor1);
                            }
                            else if ((valz - n2 + mod3) % size == 0.0 || (valz + n2 + mod2) % size == 0.0) {
                                this.setBlock(result, x, y, z, this.floor2);
                            }
                            else {
                                boolean found2 = false;
                                for (double j = n1; j >= 0.0; --j) {
                                    if ((valz - j + mod3) % size == 0.0 || (valz + j + mod2) % size == 0.0) {
                                        found2 = true;
                                        break;
                                    }
                                }
                                if (found2) {
                                    this.setBlock(result, x, y, z, this.floor1);
                                }
                                else {
                                    boolean found3 = false;
                                    for (double k = n3; k >= 0.0; --k) {
                                        if ((valx - k + mod3) % size == 0.0 || (valx + k + mod2) % size == 0.0) {
                                            found3 = true;
                                            break;
                                        }
                                    }
                                    if (found3) {
                                        this.setBlock(result, x, y, z, this.floor1);
                                    }
                                    else {
                                        this.setBlock(result, x, y, z, this.plotfloor);
                                    }
                                }
                            }
                        }
                    }
                    else if (y == this.roadheight + 1) {
                        if ((valx - n3 + mod3) % size == 0.0 || (valx + n3 + mod2) % size == 0.0) {
                            boolean found = false;
                            for (double i = n2; i >= 0.0; --i) {
                                if ((valz - i + mod3) % size == 0.0 || (valz + i + mod2) % size == 0.0) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                this.setBlock(result, x, y, z, this.wall);
                            }
                        }
                        else {
                            boolean found = false;
                            for (double i = n2; i >= 0.0; --i) {
                                if ((valx - i + mod3) % size == 0.0 || (valx + i + mod2) % size == 0.0) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found && ((valz - n3 + mod3) % size == 0.0 || (valz + n3 + mod2) % size == 0.0)) {
                                this.setBlock(result, x, y, z, this.wall);
                            }
                        }
                    }
                    else {
                        this.setBlock(result, x, y, z, this.filling);
                    }
                }
            }
        }
        return result;
    }
    
    private void setBlock(final ChunkGenerator.ChunkData result, final int x, final int y, final int z, final Material blk) {
        result.setBlock(x, y, z, blk);
    }
    
    public List<BlockPopulator> getDefaultPopulators(final World world) {
        if (this.temppmi == null) {
            return Arrays.asList(new BlockPopulator[] { new PlotRoadPopulator(), new PlotContentPopulator() });
        }
        return Arrays.asList(new BlockPopulator[] { new PlotRoadPopulator(this.temppmi), new PlotContentPopulator(this.temppmi) });
    }
    
    public Location getFixedSpawnLocation(final World world, final Random random) {
        return new Location(world, 0.0, (double)(this.roadheight + 2), 0.0);
    }
}
