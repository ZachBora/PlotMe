package com.worldcretornica.plotme;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class PlotPopulator extends BlockPopulator {

	public void populate(World world, Random rand, Chunk chunk) {
		
		int y = PlotManager.getMap(world).WorldHeight;
		
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				Block block = chunk.getBlock(x, y, z);
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
