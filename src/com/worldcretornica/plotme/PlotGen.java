package com.worldcretornica.plotme;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlotGen extends ChunkGenerator {

    private double plotsize;
    private double pathsize;
    private short bottom;
    private short wall;
    private short plotfloor;
    private short filling;
    private short floor1;
    private short floor2;
    private int roadheight;
    private PlotMapInfo temppmi;

    public PlotGen() {
        plotsize = 32;
        pathsize = 7;
        bottom = 7;
        wall = 44;
        plotfloor = 2;
        filling = 3;
        roadheight = 64;
        floor1 = 5;
        floor2 = 5;
        temppmi = null;
        PlotMe.logger.warning("Unable to find configuration, using defaults");
    }

    public PlotGen(PlotMapInfo pmi) {
        plotsize = pmi.PlotSize;
        pathsize = pmi.PathWidth;
        bottom = pmi.BottomBlockId;
        wall = pmi.WallBlockId;
        plotfloor = pmi.PlotFloorBlockId;
        filling = pmi.PlotFillingBlockId;
        roadheight = pmi.RoadHeight;
        floor1 = pmi.RoadMainBlockId;
        floor2 = pmi.RoadStripeBlockId;
        temppmi = pmi;
    }

    @Override
    public short[][] generateExtBlockSections(World world, Random random, int cx, int cz, BiomeGrid biomes) {
        int maxY = world.getMaxHeight();

        short[][] result = new short[maxY / 16][];

        double size = plotsize + pathsize;
        int valx;
        int valz;

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

                biomes.setBiome(x, z, Biome.PLAINS);

                for (int y = 0; y < height; y++) {
                    if (y == 0) {
                        setBlock(result, x, y, z, bottom);

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

                            if (found) {
                                setBlock(result, x, y, z, floor1);
                            } else {
                                setBlock(result, x, y, z, filling);
                            }
                        } else if ((valx - n2 + mod1) % size == 0 || (valx + n2 + mod2) % size == 0) // middle+2
                        {
                            if ((valz - n3 + mod1) % size == 0 || (valz + n3 + mod2) % size == 0 || (valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0) {
                                setBlock(result, x, y, z, floor1);
                            } else {
                                setBlock(result, x, y, z, floor2);
                            }
                        } else if ((valx - n1 + mod1) % size == 0 || (valx + n1 + mod2) % size == 0) // middle+2
                        {
                            if ((valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0 || (valz - n1 + mod1) % size == 0 || (valz + n1 + mod2) % size == 0) {
                                setBlock(result, x, y, z, floor2);
                            } else {
                                setBlock(result, x, y, z, floor1);
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
                                setBlock(result, x, y, z, floor1);
                            } else {
                                if ((valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0) {
                                    setBlock(result, x, y, z, floor2);
                                } else {
                                    boolean found2 = false;
                                    for (double i = n1; i >= 0; i--) {
                                        if ((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0) {
                                            found2 = true;
                                            break;
                                        }
                                    }

                                    if (found2) {
                                        setBlock(result, x, y, z, floor1);
                                    } else {
                                        boolean found3 = false;
                                        for (double i = n3; i >= 0; i--) {
                                            if ((valx - i + mod1) % size == 0 || (valx + i + mod2) % size == 0) {
                                                found3 = true;
                                                break;
                                            }
                                        }

                                        if (found3) {
                                            setBlock(result, x, y, z, floor1);
                                        } else {
                                            setBlock(result, x, y, z, plotfloor);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (y == (roadheight + 1)) {

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
                            } else {
                                setBlock(result, x, y, z, wall);
                            }
                        } else {
                            boolean found = false;
                            for (double i = n2; i >= 0; i--) {
                                if ((valx - i + mod1) % size == 0 || (valx + i + mod2) % size == 0) {
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                if ((valz - n3 + mod1) % size == 0 || (valz + n3 + mod2) % size == 0) {
                                    setBlock(result, x, y, z, wall);
                                } else {
                                }
                            }
                        }
                    } else {
                        setBlock(result, x, y, z, filling);
                    }
                }
            }
        }

        return result;
    }

    private void setBlock(short[][] result, int x, int y, int z, short blkid) {
        if (result[y >> 4] == null) {
            result[y >> 4] = new short[4096];
        }
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
    }

    public List<BlockPopulator> getDefaultPopulators(World world) {
        if (temppmi == null) {
            return Arrays.asList((BlockPopulator) new PlotRoadPopulator(), (BlockPopulator) new PlotContentPopulator());
        } else {
            return Arrays.asList((BlockPopulator) new PlotRoadPopulator(temppmi), (BlockPopulator) new PlotContentPopulator(temppmi));
        }
    }

    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, roadheight + 2, 0);
    }
}
