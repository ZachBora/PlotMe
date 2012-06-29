package com.worldcretornica.plotme;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class PlotPopulator extends BlockPopulator {

	private int roadheight;
	
	public PlotPopulator()
	{
		roadheight = 64;
	}
	
	public PlotPopulator(PlotMapInfo pmi)
	{
		roadheight = pmi.RoadHeight;
	}
	
	public void populate(World world, Random rand, Chunk chunk) 
	{
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				Block block = chunk.getBlock(x, roadheight, z);
				if(block.getType() == Material.WOOL)
				{
					block.setType(Material.WOOD);
					block.setData((byte) 2);
				}
				block.setBiome(Biome.PLAINS);
			}
		}
		
	}

}
