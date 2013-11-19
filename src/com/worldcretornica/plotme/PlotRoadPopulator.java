package com.worldcretornica.plotme;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class PlotRoadPopulator extends BlockPopulator
{
	private double plotsize;
	private double pathsize;

	private byte wall;
	private short wallid;
	private byte floor1;
	private short floor1id;
	private byte floor2;
	private short floor2id;
	
	private byte pillar;
	private short pillarid;
	
	private byte pillarh1;
	private short pillarh1id;
	private byte pillarh2;
	private short pillarh2id;
	
	private int roadheight;
	
	public PlotRoadPopulator()
	{
		plotsize = 32;
		pathsize = 7;
		wall = 0;
		wallid = 44;
		floor2 = 0;
		floor2id = 5;
		floor1 = 2;
		floor1id = 5;
		
		pillar = 0;
		pillarid = 17;
		
		pillarh1 = 4;
		pillarh1id = 17;
		
		pillarh2 = 8;
		pillarh2id = 17;
		
		roadheight = 64;
	}
	
	public PlotRoadPopulator(PlotMapInfo pmi)
	{
		plotsize = pmi.PlotSize;
		pathsize = pmi.PathWidth;
		wall = pmi.WallBlockValue;
		wallid = pmi.WallBlockId;
		floor2 = pmi.RoadMainBlockValue;
		floor2id = pmi.RoadMainBlockId;
		floor1 = pmi.RoadStripeBlockValue;
		floor1id = pmi.RoadStripeBlockId;
		roadheight = pmi.RoadHeight;
		
		pillar = 0;
		pillarid = 17;
	}

	@SuppressWarnings("deprecation")
    @Override
	public void populate(World w, Random rand, Chunk chunk) 
	{
		int cx = chunk.getX();
		int cz = chunk.getZ();
		
		int xx = cx << 4;
		int zz = cz << 4;
		
		double size = plotsize + pathsize;
		int valx;
		int valz;
		
		double n1;
		double n2;
		double n3;
		int mod2 = 0;
		int mod1 = 1;
		
		if(pathsize % 2 == 1)
		{
			n1 = Math.ceil(((double)pathsize)/2) - 2;
			n2 = Math.ceil(((double)pathsize)/2) - 1;
			n3 = Math.ceil(((double)pathsize)/2);
		}
		else
		{
			n1 = Math.floor(((double)pathsize)/2) - 2;
			n2 = Math.floor(((double)pathsize)/2) - 1;
			n3 = Math.floor(((double)pathsize)/2);
		}
		
		if(pathsize % 2 == 1)
		{
			mod2 = -1;
		}
		
		for (int x = 0; x < 16; x++) 
        {
			valx = (cx * 16 + x);
			
            for (int z = 0; z < 16; z++) 
            {
            	valz = (cz * 16 + z);
                
        		int y = roadheight;
        		            		
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
        			{
        				setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
        				setBlock(w, x + xx, y, z + zz, floor1, floor1id);
        				setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
        				setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
        			}else{
        				setBlock(w, x + xx, y, z + zz, pillarh2, pillarh2id);
    					setBlock(w, x + xx, y+1, z + zz, wall, wallid);
        			}
        		}
        		else
        		{
        			boolean found5 = false;
        			for(double i = n2; i >= 0; i--)
        			{
            			if((valx - i + mod1) % size == 0 || (valx + i + mod2) % size == 0)
            			{
            				found5 = true;
            				break;
            			}	                			
        			}
        			
        			if(!found5)
        			{
        				if((valz - n3 + mod1) % size == 0 || (valz + n3 + mod2) % size == 0)
        				{
        					setBlock(w, x + xx, y, z + zz, pillarh1, pillarh1id);
        					setBlock(w, x + xx, y+1, z + zz, wall, wallid);
        				}
        			}
        			
        			
        			if ((valx - n2 + mod1) % size == 0 || (valx + n2 + mod2) % size == 0) //middle+2
	        		{
	        			if((valz - n3 + mod1) % size == 0 || (valz + n3 + mod2) % size == 0
	        					|| (valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0)
	        			{
	        				setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
	        				setBlock(w, x + xx, y, z + zz, floor1, floor1id);
	        				setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
	        				setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
	        			}
	        			else
	        			{
	        				setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
	        				setBlock(w, x + xx, y, z + zz, floor2, floor2id);
	        				setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
	        				setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
	        			}
	        		}else if ((valx - n1 + mod1) % size == 0 || (valx + n1 + mod2) % size == 0) //middle+2
	        		{
	        			if((valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0 
	        				|| (valz - n1 + mod1) % size == 0 || (valz + n1 + mod2) % size == 0)
	        			{
	        				setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
	        				setBlock(w, x + xx, y, z + zz, floor2, floor2id);
	        				setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
	        				setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
	        			}
	        			else
	        			{
	        				setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
	        				setBlock(w, x + xx, y, z + zz, floor1, floor1id);
	        				setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
	        				setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
	        			}
	        		}
	        		else
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
	        				setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
	        				setBlock(w, x + xx, y, z + zz, floor1, floor1id);
	        				setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
	        				setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
	        			}else{
	            			if((valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0)
	            			{
	            				setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
	            				setBlock(w, x + xx, y, z + zz, floor2, floor2id);
	            				setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
	            				setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
	            			}
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
	            				{
	            					setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
	            					setBlock(w, x + xx, y, z + zz, floor1, floor1id);
	            					setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
	                				setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
	            				}
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
	                				{
	                					setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
	                					setBlock(w, x + xx, y, z + zz, floor1, floor1id);
	                					setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
	                    				setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
	                				}
	                			}
	            			}
	            		}
	        		}
        		}
            }
        }
	}

	@SuppressWarnings("deprecation")
    private void setBlock(World w, int x, int y, int z, byte val, short id)
	{
		if(val != 0)
		{
			w.getBlockAt(x, y, z).setTypeIdAndData(id, val, false);
		}else{
			w.getBlockAt(x, y, z).setTypeId(id);
		}
	}
}
