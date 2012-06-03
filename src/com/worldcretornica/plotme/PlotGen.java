package com.worldcretornica.plotme;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class PlotGen extends ChunkGenerator {
	
	public byte[] generate(World world, Random random, int cx, int cz) {
		byte[] result = new byte[32768];
		
		double plotsize = PlotMe.plotmaps.get(world.getName()).PlotSize;
		double pathsize = PlotMe.plotmaps.get(world.getName()).PathWidth;
		double size = plotsize + pathsize;
		int valx;
		int valz;
		
		byte bottom = PlotMe.plotmaps.get(world.getName()).BottomBlockId; //bedrock
		byte wall = PlotMe.plotmaps.get(world.getName()).WallBlockId; //step
		byte plotfloor = PlotMe.plotmaps.get(world.getName()).PlotFloorBlockId; //grass
		byte filling = PlotMe.plotmaps.get(world.getName()).PlotFillingBlockId; //dirt
		byte floor1 = (byte)Material.WOOL.getId(); //wool
		byte floor2 = (byte)Material.WOOD.getId(); //wood
		byte air = (byte)Material.AIR.getId(); //air
		
		double n1;
		double n2;
		double n3;
		int mod2 = 0;
		int mod1 = 1;
		
		if(pathsize % 2 == 1)
		{
			n1 = Math.ceil(((double)pathsize)/2) - 2; //1 7
			n2 = Math.ceil(((double)pathsize)/2) - 1; //2 7
			n3 = Math.ceil(((double)pathsize)/2); //3 7
		}else{
			n1 = Math.floor(((double)pathsize)/2) - 2; //1 7
			n2 = Math.floor(((double)pathsize)/2) - 1; //2 7
			n3 = Math.floor(((double)pathsize)/2); //3 7
		}
		
		if(pathsize % 2 == 1)
		{
			mod2 = -1;
		}
		
		/*
		if(plotsize % 2 == 1 && pathsize % 2 == 1)
		{
			mod1 = 1;
			mod2 = -1;
		}
		
		if(plotsize % 2 == 0 && pathsize % 2 == 1)
		{
			mod1 = 1;
			mod2 = -1;
		}*/
		
		
		
		
		//mod2 = -1;
					
		//with ^
		//path 7 plot11 -> path 9 plot 9 mod2=-1
		//path 7 plot10 -> path 8 plot 9 mod2=-1
		//path 6 plot10 -> path 7 plot 9
		//path 6 plot11 -> path 6 plot 11  mod2=-1
					
		//path 7 plot11 -> path  plot 
		//path 7 plot10 -> path  plot 
		//path 6 plot10 -> path  plot 
		//path 6 plot11 -> path 5 plot 12 
		
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int height = 66;
                for (int y = 0; y < height; y++) {
                	valx = (cx * 16 + x);
            		valz = (cz * 16 + z);
                	
            		switch(y)
                	{
                	case 0:
                		result[(x * 16 + z) * 128 + y] = bottom;
                		break;
                	case 64:
                		if ((valx - n3 + mod1) % size == 0 || (valx + n3 + mod2) % size == 0) //middle+3
                		{
                			boolean found = false;
                			for(double i = n2; i >= 0; i--)
                			{
	                			if((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0)
	                			{
	                				found = true;
	                				break;
	                			}	                			
                			}

                			if(found)
                				result[(x * 16 + z) * 128 + y] = floor1; //floor1
                			else
                				result[(x * 16 + z) * 128 + y] = filling; //filling
                		}else if ((valx - n2 + mod1) % size == 0 || (valx + n2 + mod2) % size == 0) //middle+2
                		{
                			if((valz - n3 + mod1) % size == 0 || (valz + n3 + mod2) % size == 0
                					|| (valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0)
                				result[(x * 16 + z) * 128 + y] = floor1; //floor1
                			else
                				result[(x * 16 + z) * 128 + y] = floor2; //floor2
                		}else if ((valx - n1 + mod1) % size == 0 || (valx + n1 + mod2) % size == 0) //middle+2
                		{
                			if((valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0 
                				|| (valz - n1 + mod1) % size == 0 || (valz + n1 + mod2) % size == 0)
                				result[(x * 16 + z) * 128 + y] = floor2; //floor2
                			else
                				result[(x * 16 + z) * 128 + y] = floor1; //floor1
                		}else
                		{
                			boolean found = false;
                			for(double i = n1; i >= 0; i--)
                			{
	                			if((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0)
	                			{
	                				found = true;
	                				break;
	                			}	                			
                			}

                			if(found)
                			{
	                			result[(x * 16 + z) * 128 + y] = floor1; //floor1
                			}else{
	                			if((valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0)
	                				result[(x * 16 + z) * 128 + y] = floor2; //floor2
	                			else
	                			{
	                				boolean found2 = false;
	                    			for(double i = n1; i >= 0; i--)
	                    			{
	    	                			if((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0)
	    	                			{
	    	                				found2 = true;
	    	                				break;
	    	                			}	                			
	                    			}
	                				
	                				if(found2)
		                				result[(x * 16 + z) * 128 + y] = floor1; //floor1
		                			else
		                			{
		                				boolean found3 = false;
		                    			for(double i = n3; i >= 0; i--)
		                    			{
		    	                			if((valx - i + mod1) % size == 0 || (valx + i + mod2) % size == 0)
		    	                			{
		    	                				found3 = true;
		    	                				break;
		    	                			}	                			
		                    			}
		                				
		                				if(found3)
		                					result[(x * 16 + z) * 128 + y] = floor1; //floor1
		                				else
		                					result[(x * 16 + z) * 128 + y] = plotfloor; //plotfloor
		                			}
	                			}
	                		}
                		}
                		break;
                	case 65:
                		
                		if ((valx - n3 + mod1) % size == 0 || (valx + n3 + mod2) % size == 0) //middle+3
                		{
                			boolean found = false;
                			for(double i = n2; i >= 0; i--)
                			{
	                			if((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0)
	                			{
	                				found = true;
	                				break;
	                			}	                			
                			}
                			
                			if(found)
                				result[(x * 16 + z) * 128 + y] = air;
                			else
                				result[(x * 16 + z) * 128 + y] = wall;
                			
                		}
                		else
                		{
                			boolean found = false;
                			for(double i = n2; i >= 0; i--)
                			{
	                			if((valx - i + mod1) % size == 0 || (valx + i + mod2) % size == 0)
	                			{
	                				found = true;
	                				break;
	                			}	                			
                			}
                			
                			if(found)
                			{
                				result[(x * 16 + z) * 128 + y] = air;
                			}else{
                				if((valz - n3 + mod1) % size == 0 || (valz + n3 + mod2) % size == 0)
	                				result[(x * 16 + z) * 128 + y] = wall;
	                			else
	                				result[(x * 16 + z) * 128 + y] = air;
                			}
                		}
                		break;
                	default:
                		result[(x * 16 + z) * 128 + y] = filling;
                		break;
                	}
            		
                	/*switch(y)
                	{
                	case 0:
                		result[(x * 16 + z) * 128 + y] = bottom;
                		break;
                	case 64:
                		if ((valx - 3) % size == 0 || (valx + 3) % size == 0) //middle+3
                		{
                			if(valz % size == 0 
                					|| (valz - 1) % size == 0 || (valz + 1) % size == 0
                					|| (valz - 2) % size == 0 || (valz + 2) % size == 0)
                				result[(x * 16 + z) * 128 + y] = floor1;
                			else
                				result[(x * 16 + z) * 128 + y] = filling;
                		}else if ((valx - 2) % size == 0 || (valx + 2) % size == 0) //middle+2
                		{
                			if((valz - 3) % size == 0 || (valz + 3) % size == 0
                					|| (valz - 2) % size == 0 || (valz + 2) % size == 0)
                				result[(x * 16 + z) * 128 + y] = floor1;
                			else
                				result[(x * 16 + z) * 128 + y] = floor2;
                		}else if ((valx - 1) % size == 0 || (valx + 1 ) % size == 0) //middle+1
                		{
                			if((valz - 2) % size == 0 || (valz + 2) % size == 0
                					|| (valz - 1) % size == 0 || (valz + 1) % size == 0)
                				result[(x * 16 + z) * 128 + y] = floor2;
                			else
                				result[(x * 16 + z) * 128 + y] = floor1;
                		}else if (valx % size == 0) //middle
                		{
                			if((valz - 2) % size == 0 || (valz + 2) % size == 0)
                				result[(x * 16 + z) * 128 + y] = floor2;
                			else
                				result[(x * 16 + z) * 128 + y] = floor1;
                		}else{
                			if((valz - 2) % size == 0 || (valz + 2) % size == 0)
                				result[(x * 16 + z) * 128 + y] = floor2;
                			else if((valz - 1) % size == 0 || (valz + 1) % size == 0 || valz % size == 0)
                				result[(x * 16 + z) * 128 + y] = floor1;
                			else
                				result[(x * 16 + z) * 128 + y] = plotfloor;
                		}
                		break;
                	case 65:
                		if ((valx - 3) % size == 0 || (valx + 3) % size == 0) //middle+3
                		{
                			if((valz - 2) % size == 0 || (valz + 2) % size == 0
                					|| (valz - 1) % size == 0 || (valz + 1) % size == 0
                					|| valz % size == 0)
                				result[(x * 16 + z) * 128 + y] = air;
                			else
                				result[(x * 16 + z) * 128 + y] = wall;
                		}else if ((valx - 2) % size == 0 || (valx + 2) % size == 0
                				|| (valx - 1) % size == 0 || (valx + 1) % size == 0
                				|| valx % size == 0) //middle
                		{
                			result[(x * 16 + z) * 128 + y] = air;
                		}else{
                			if((valz - 3) % size == 0 || (valz + 3) % size == 0)
                				result[(x * 16 + z) * 128 + y] = wall;
                			else
                				result[(x * 16 + z) * 128 + y] = air;
                		}
                		break;
                	default:
                		result[(x * 16 + z) * 128 + y] = filling;
                		break;
                	}*/
                }
            }
        }

        return result;
	}
	
	public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList((BlockPopulator)new PlotPopulator());
    }

	public Location getFixedSpawnLocation(World world, Random random) {
		return new Location(world, 0, 66, 0);
	}
}
