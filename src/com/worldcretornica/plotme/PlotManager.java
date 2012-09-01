package com.worldcretornica.plotme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class PlotManager {
				
	public static String getPlotId(Location loc)
	{
		if(isPlotWorld(loc))
		{
			int valx = loc.getBlockX();
			int valz = loc.getBlockZ();
			PlotMapInfo pmi = getMap(loc);
			int size = pmi.PlotSize + pmi.PathWidth;
			int pathsize = pmi.PathWidth;
			
			double n3;
			int mod2 = 0;
			int mod1 = 1;
			
			if(pathsize % 2 == 1)
			{
				n3 = Math.ceil(((double)pathsize)/2); //3 7
				mod2 = -1;
			}
			else
			{
				n3 = Math.floor(((double)pathsize)/2); //3 7
			}
						
			for(double i = n3; i >= 0; i--)
			{
				if((valx - i + mod1) % size == 0 || 
				   (valx + i + mod2) % size == 0 || 
				   (valz - i + mod1) % size == 0 || 
				   (valz + i + mod2) % size == 0)
				{
					return "";
				}	                			
			}
			
			int x = (int) Math.ceil((double)valx / size);
			int z = (int) Math.ceil((double)valz / size);
			
			return "" + x + ";" + z;
		}
		else
		{
			return "";
		}
	}
	
	public static boolean isPlotAvailable(String id, World world)
	{
		return isPlotAvailable(id, world.getName().toLowerCase());
	}
	
	public static boolean isPlotAvailable(String id, Player p)
	{
		return isPlotAvailable(id, p.getWorld().getName().toLowerCase());
	}
	
	public static boolean isPlotAvailable(String id, String world)
	{
		if(isPlotWorld(world))
		{
			return !getPlots(world).containsKey(id);
		}
		else
		{
			return false;
		}
	}
	
	public static Plot createPlot(World world, String id, String owner)
	{
		if(isPlotAvailable(id, world) && id != "")
		{
			Plot plot = new Plot(owner, getPlotTopLoc(world, id), getPlotBottomLoc(world, id), id, getMap(world).DaysToExpiration);
			
			setOwnerSign(world, plot);
			
			getPlots(world).put(id, plot);
			SqlManager.addPlot(plot, getIdX(id), getIdZ(id), world);
			return plot;
		}
		else
		{
			return null;
		}
	}
	
	public static void setOwnerSign(World world, Plot plot)
	{	
		Location pillar = new Location(world, bottomX(plot.id, world) - 1, getMap(world).RoadHeight + 1, bottomZ(plot.id, world) - 1);
						
		Block bsign = pillar.add(0, 0, -1).getBlock();
		bsign.setType(Material.AIR);
		bsign.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 2, false);
		
		String id = getPlotId(new Location(world, bottomX(plot.id, world), 0, bottomZ(plot.id, world)));
		
		Sign sign = (Sign) bsign.getState();
		if(("ID:" + id).length() > 15)
		{
			sign.setLine(0, ("ID:" + id).substring(0, 15));
			if(("ID:" + id).length() > 30)
			{
				sign.setLine(1, ("ID:" + id).substring(15, 30));
			}
			else
			{
				sign.setLine(1, ("ID:" + id).substring(15));
			}
		}
		else
		{
			sign.setLine(0, "ID:" + id);
		}
		if(("Owner:" + plot.owner).length() > 15)
		{
			sign.setLine(2, ("Owner:" + plot.owner).substring(0, 15));
			if(("Owner: " + plot.owner).length() > 30)
			{
				sign.setLine(3, ("Owner:" + plot.owner).substring(15, 30));
			}
			else
			{
				sign.setLine(3, ("Owner:" + plot.owner).substring(15));
			}
		}else{
			sign.setLine(2, "Owner:" + plot.owner);
			sign.setLine(3, "");
		}
		sign.update(true);
	}
	
	public static void setSellSign(World world, Plot plot)
	{
		removeSellSign(world, plot.id);
		
		if(plot.forsale || plot.auctionned)
		{
			Location pillar = new Location(world, bottomX(plot.id, world) - 1, getMap(world).RoadHeight + 1, bottomZ(plot.id, world) - 1);
							
			Block bsign = pillar.clone().add(-1, 0, 0).getBlock();
			bsign.setType(Material.AIR);
			bsign.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);
			
			Sign sign = (Sign) bsign.getState();
			
			if(plot.forsale)
			{
				sign.setLine(0, "" + ChatColor.BLUE + ChatColor.BOLD + "FOR SALE");
				sign.setLine(1, "Price :");
				if(plot.customprice % 1 == 0)
					sign.setLine(2, "" + ChatColor.BLUE + Math.round(plot.customprice));
				else
					sign.setLine(2, "" + ChatColor.BLUE + plot.customprice);
				sign.setLine(3, "/plotme buy");
				
				sign.update(true);
			}
			
			if(plot.auctionned)
			{				
				if(plot.forsale)
				{
					bsign = pillar.clone().add(-1, 0, 1).getBlock();
					bsign.setType(Material.AIR);
					bsign.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);
					
					sign = (Sign) bsign.getState();
				}
				
				sign.setLine(0, "" + ChatColor.BLUE + ChatColor.BOLD + "ON AUCTION");
				if(plot.currentbidder.equals(""))
					sign.setLine(1, "Minimum bid :");
				else
					sign.setLine(1, "Current bid :");
				if(plot.currentbid % 1 == 0)
					sign.setLine(2, "" + ChatColor.BLUE + Math.round(plot.currentbid));
				else
					sign.setLine(2, "" + ChatColor.BLUE + plot.currentbid);
				sign.setLine(3, "/plotme bid <x>");
				
				sign.update(true);
			}
		}
	}
	
	public static void removeOwnerSign(World world, String id)
	{
		Location bottom = getPlotBottomLoc(world, id);
		
		Location pillar = new Location(world, bottom.getX() - 1, getMap(world).RoadHeight + 1, bottom.getZ() - 1);
		
		Block bsign = pillar.add(0, 0, -1).getBlock();
		bsign.setType(Material.AIR);
	}
	
	public static void removeSellSign(World world, String id)
	{
		Location bottom = getPlotBottomLoc(world, id);
		
		Location pillar = new Location(world, bottom.getX() - 1, getMap(world).RoadHeight + 1, bottom.getZ() - 1);
		
		Block bsign = pillar.clone().add(-1, 0, 0).getBlock();
		bsign.setType(Material.AIR);
						
		bsign = pillar.clone().add(-1, 0, 1).getBlock();
		bsign.setType(Material.AIR);
	}
	
	public static int getIdX(String id)
	{
		return Integer.parseInt(id.substring(0, id.indexOf(";")));
	}
	
	public static int getIdZ(String id)
	{
		return Integer.parseInt(id.substring(id.indexOf(";") + 1));
	}
	
	public static Location getPlotBottomLoc(World world, String id)
	{
		int px = getIdX(id);
		int pz = getIdZ(id);
		
		PlotMapInfo pmi = getMap(world);
		
		int x = px * (pmi.PlotSize + pmi.PathWidth) - (pmi.PlotSize) - ((int)Math.floor(pmi.PathWidth/2));
		int z = pz * (pmi.PlotSize + pmi.PathWidth) - (pmi.PlotSize) - ((int)Math.floor(pmi.PathWidth/2));
		
		return new Location(world, x, 1, z);
	}
	
	public static Location getPlotTopLoc(World world, String id)
	{
		int px = getIdX(id);
		int pz = getIdZ(id);
		
		PlotMapInfo pmi = getMap(world);
		
		int x = px * (pmi.PlotSize + pmi.PathWidth) - ((int)Math.floor(pmi.PathWidth/2)) - 1;
		int z = pz * (pmi.PlotSize + pmi.PathWidth) - ((int)Math.floor(pmi.PathWidth/2)) - 1;
		
		return new Location(world, x, 255, z);
	}
	
	public static void setBiome(World w, String id, Plot plot, Biome b)
	{
		for(int x = PlotManager.bottomX(plot.id, w); x <= PlotManager.topX(plot.id, w); x++)
		{
			for(int z = PlotManager.bottomZ(plot.id, w); z <= PlotManager.topZ(plot.id, w); z++)
			{
				w.getBlockAt(x, 0, z).setBiome(b);
			}
		}
		
		plot.biome = b;
		SqlManager.updatePlot(getIdX(id), getIdZ(id), plot.world, "biome", b.name());
	}
	
	public static Location getTop(World w, Plot plot)
	{
		return new Location(w, PlotManager.topX(plot.id, w), 256, PlotManager.topZ(plot.id, w));
	}
	
	public static void clear(World w, Plot plot)
	{
		clear(new Location(w, PlotManager.bottomX(plot.id, w), 0, PlotManager.bottomZ(plot.id, w)), new Location(w, PlotManager.topX(plot.id, w), 256, PlotManager.topZ(plot.id, w)));
	}
	
	public static void clear(Location bottom, Location top)
	{
		PlotMapInfo pmi = getMap(bottom);
		
		for(int x = bottom.getBlockX() - 1; x <= top.getBlockX() + 1; x++)
		{
			for(int z = bottom.getBlockZ() - 1; z <= top.getBlockZ() + 1; z++)
			{
				Block block = new Location(bottom.getWorld(), x, 0, z).getBlock();
				
				block.setBiome(Biome.PLAINS);
				
				for(int y = 0; y < block.getWorld().getMaxHeight(); y++)
				{
					block = new Location(bottom.getWorld(), x, y, z).getBlock();
					
					if(y == 0)
						block.setTypeId(pmi.BottomBlockId);
					else if(y < pmi.RoadHeight)
						block.setTypeId(pmi.PlotFillingBlockId);
					else if(y == pmi.RoadHeight)
						block.setTypeId(pmi.PlotFloorBlockId);
					else
					{
						if(y == (pmi.RoadHeight + 1) && 
								(x == bottom.getBlockX() - 1 || 
								 x == top.getBlockX() + 1 ||
								 z == bottom.getBlockZ() - 1 || 
								 z == top.getBlockZ() + 1))
						{
							//block.setTypeId(pmi.WallBlockId);
						}
						else
						{
							block.setType(Material.AIR);
						}
					}
				}
			}
		}
		
		adjustWall(bottom);
	}
		
	public static void adjustWall(Location l)
	{
		Plot plot = getPlotById(l);
		World w = l.getWorld();
		PlotMapInfo pmi = getMap(w);
		
		List<String> wallids = new ArrayList<String>();
		
		String auctionwallid = pmi.AuctionWallBlockId;
		String forsalewallid = pmi.ForSaleWallBlockId;
		
		if(plot.protect) wallids.add(pmi.ProtectedWallBlockId);
		if(plot.auctionned && !wallids.contains(auctionwallid)) wallids.add(auctionwallid);
		if(plot.forsale && !wallids.contains(forsalewallid)) wallids.add(forsalewallid);
		
		if(wallids.size() == 0) wallids.add("" + pmi.WallBlockId);
		
		int ctr = 0;
			
		Location bottom = getPlotBottomLoc(w, plot.id);
		Location top = getPlotTopLoc(w, plot.id);
		
		int x;
		int z;
		
		String currentblockid;
		Block block;
		
		for(x = bottom.getBlockX() - 1; x < top.getBlockX() + 1; x++)
		{
			z = bottom.getBlockZ() - 1;
			currentblockid = wallids.get(ctr);
			ctr = (ctr == wallids.size()-1)? 0 : ctr + 1;
			block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
			setWall(block, currentblockid);
		}
		
		for(z = bottom.getBlockZ() - 1; z < top.getBlockZ() + 1; z++)
		{
			x = top.getBlockX() + 1;
			currentblockid = wallids.get(ctr);
			ctr = (ctr == wallids.size()-1)? 0 : ctr + 1;
			block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
			setWall(block, currentblockid);
		}
		
		for(x = top.getBlockX() + 1; x > bottom.getBlockX() - 1; x--)
		{
			z = top.getBlockZ() + 1;
			currentblockid = wallids.get(ctr);
			ctr = (ctr == wallids.size()-1)? 0 : ctr + 1;
			block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
			setWall(block, currentblockid);
		}
		
		for(z = top.getBlockZ() + 1; z > bottom.getBlockZ() - 1; z--)
		{
			x = bottom.getBlockX() - 1;
			currentblockid = wallids.get(ctr);
			ctr = (ctr == wallids.size()-1)? 0 : ctr + 1;
			block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
			setWall(block, currentblockid);
		}
	}
	
	
	private static void setWall(Block block, String currentblockid)
	{
		
		int blockId;
		byte blockData = 0;
		
		if(currentblockid.contains(":"))
		{
			try
			{
				blockId = Integer.parseInt(currentblockid.substring(0, currentblockid.indexOf(":")));
				blockData = Byte.parseByte(currentblockid.substring(currentblockid.indexOf(":") + 1));
			}
			catch(NumberFormatException e)
			{
				blockId = 44;
				blockData = 0;
			}
		}
		else
		{
			try
			{
				blockId = Integer.parseInt(currentblockid);
			}
			catch(NumberFormatException e)
			{
				blockId = 44;
			}
		}
		
		block.setTypeIdAndData(blockId, blockData, true);
	}
	
	
	public static boolean isBlockInPlot(Plot plot, Location blocklocation)
	{
		World w = blocklocation.getWorld();
		int lowestX = Math.min(PlotManager.bottomX(plot.id, w), PlotManager.topX(plot.id, w));
		int highestX = Math.max(PlotManager.bottomX(plot.id, w), PlotManager.topX(plot.id, w));
		int lowestZ = Math.min(PlotManager.bottomZ(plot.id, w), PlotManager.topZ(plot.id, w));
		int highestZ = Math.max(PlotManager.bottomZ(plot.id, w), PlotManager.topZ(plot.id, w));
		
		return blocklocation.getBlockX() >= lowestX && blocklocation.getBlockX() <= highestX
				&& blocklocation.getBlockZ() >= lowestZ && blocklocation.getBlockZ() <= highestZ;
	}
	
	public static boolean movePlot(World w, String idFrom, String idTo)
	{
		Location plot1Bottom = getPlotBottomLoc(w, idFrom);
		Location plot2Bottom = getPlotBottomLoc(w, idTo);
		Location plot1Top = getPlotTopLoc(w, idFrom);
		
		int distanceX = plot1Bottom.getBlockX() - plot2Bottom.getBlockX();
		int distanceZ = plot1Bottom.getBlockZ() - plot2Bottom.getBlockZ();
		
		for(int x = plot1Bottom.getBlockX(); x <= plot1Top.getBlockX(); x++)
		{
			for(int z = plot1Bottom.getBlockZ(); z <= plot1Top.getBlockZ(); z++)
			{
				Block plot1Block = w.getBlockAt(new Location(w, x, 0, z));
				Block plot2Block = w.getBlockAt(new Location(w, x - distanceX, 0, z - distanceZ));
				
				String plot1Biome = plot1Block.getBiome().name();
				String plot2Biome = plot2Block.getBiome().name();
				
				plot1Block.setBiome(Biome.valueOf(plot2Biome));
				plot2Block.setBiome(Biome.valueOf(plot1Biome));
				
				for(int y = 0; y < 256 ; y++)
				{
					plot1Block = w.getBlockAt(new Location(w, x, y, z));
					int plot1Type = plot1Block.getTypeId();
					byte plot1Data = plot1Block.getData();
					
					plot2Block = w.getBlockAt(new Location(w, x - distanceX, y, z - distanceZ));
					int plot2Type = plot2Block.getTypeId();
					byte plot2Data = plot2Block.getData();
					
					//plot1Block.setTypeId(plot2Type);
					plot1Block.setTypeIdAndData(plot2Type, plot2Data, false);
					plot1Block.setData(plot2Data);
					
					//net.minecraft.server.World world = ((org.bukkit.craftbukkit.CraftWorld) w).getHandle();
					//world.setRawTypeIdAndData(plot1Block.getX(), plot1Block.getY(), plot1Block.getZ(), plot2Type, plot2Data);
					
					
					
					//plot2Block.setTypeId(plot1Type);
					plot2Block.setTypeIdAndData(plot1Type, plot1Data, false);
					plot2Block.setData(plot1Data);
					//world.setRawTypeIdAndData(plot2Block.getX(), plot2Block.getY(), plot2Block.getZ(), plot1Type, plot1Data);
				}
			}
		}
		
		HashMap<String, Plot> plots = getPlots(w);
		
		if(plots.containsKey(idFrom))
		{
			if(plots.containsKey(idTo))
			{
				Plot plot1 = plots.get(idFrom);
				Plot plot2 = plots.get(idTo);
								
				int idX = getIdX(idTo);
				int idZ = getIdZ(idTo);
				SqlManager.deletePlot(idX, idZ, plot2.world);
				plots.remove(idFrom);
				plots.remove(idTo);
				idX = getIdX(idFrom);
				idZ = getIdZ(idFrom);
				SqlManager.deletePlot(idX, idZ, plot1.world);
				
				plot2.id = "" + idX + ";" + idZ;
				SqlManager.addPlot(plot2, idX, idZ, w);
				plots.put(idFrom, plot2);
				
				for(int i = 0 ; i < plot2.comments.size() ; i++)
				{
					SqlManager.addPlotComment(plot2.comments.get(i), i, idX, idZ, plot2.world);
				}
				
				for(String player : plot2.allowed())
				{
					SqlManager.addPlotAllowed(player, idX, idZ, plot2.world);
				}
				
				idX = getIdX(idTo);
				idZ = getIdZ(idTo);
				plot1.id = "" + idX + ";" + idZ;
				SqlManager.addPlot(plot1, idX, idZ, w);
				plots.put(idTo, plot1);
				
				for(int i = 0 ; i < plot1.comments.size() ; i++)
				{
					SqlManager.addPlotComment(plot1.comments.get(i), i, idX, idZ, plot1.world);
				}
				
				for(String player : plot1.allowed())
				{
					SqlManager.addPlotAllowed(player, idX, idZ, plot1.world);
				}
				
				setOwnerSign(w, plot1);
				setSellSign(w, plot1);
				setOwnerSign(w, plot2);
				setSellSign(w, plot2);
				
			}
			else
			{
				Plot plot = plots.get(idFrom);
				
				int idX = getIdX(idFrom);
				int idZ = getIdZ(idFrom);
				SqlManager.deletePlot(idX, idZ, plot.world);
				plots.remove(idFrom);
				idX = getIdX(idTo);
				idZ = getIdZ(idTo);
				plot.id = "" + idX + ";" + idZ;
				SqlManager.addPlot(plot, idX, idZ, w);
				plots.put(idTo, plot);
				
				for(int i = 0 ; i < plot.comments.size() ; i++)
				{
					SqlManager.addPlotComment(plot.comments.get(i), i, idX, idZ, plot.world);
				}
				
				for(String player : plot.allowed())
				{
					SqlManager.addPlotAllowed(player, idX, idZ, plot.world);
				}
				
				setOwnerSign(w, plot);
				setSellSign(w, plot);
				removeOwnerSign(w, idFrom);
				removeSellSign(w, idFrom);
				
			}
		}else{
			if(plots.containsKey(idTo))
			{
				Plot plot = plots.get(idTo);
				
				int idX = getIdX(idTo);
				int idZ = getIdZ(idTo);
				SqlManager.deletePlot(idX, idZ, plot.world);
				plots.remove(idTo);
				
				idX = getIdX(idFrom);
				idZ = getIdZ(idFrom);
				plot.id = "" + idX + ";" + idZ;
				SqlManager.addPlot(plot, idX, idZ, w);
				plots.put(idFrom, plot);
				
				for(int i = 0 ; i < plot.comments.size() ; i++)
				{
					SqlManager.addPlotComment(plot.comments.get(i), i, idX, idZ, plot.world);
				}
				
				for(String player : plot.allowed())
				{
					SqlManager.addPlotAllowed(player, idX, idZ, plot.world);
				}
				
				setOwnerSign(w, plot);
				setSellSign(w, plot);
				removeOwnerSign(w, idTo);
				removeSellSign(w, idTo);
			}
		}
		
		return true;
	}
	
	public static int getNbOwnedPlot(Player p)
	{
		return getNbOwnedPlot(p.getName(), p.getWorld());
	}
	
	public static int getNbOwnedPlot(String name, World w)
	{
		int nbfound = 0;
		for(Plot plot : PlotManager.getPlots(w).values())
		{
			if(plot.owner.equalsIgnoreCase(name))
			{
				nbfound++;
			}
		}
		
		return nbfound;
	}
		
	public static int bottomX(String id, World w)
	{
		return getPlotBottomLoc(w, id).getBlockX();
	}
	
	public static int bottomZ(String id, World w)
	{
		return getPlotBottomLoc(w, id).getBlockZ();
	}

	public static int topX(String id, World w)
	{
		return getPlotTopLoc(w, id).getBlockX();
	}
	
	public static int topZ(String id, World w)
	{
		return getPlotTopLoc(w, id).getBlockZ();
	}
	
	public static boolean isPlotWorld(World w)
	{
		if(w == null)
			return false;
		else
			return PlotMe.plotmaps.containsKey(w.getName().toLowerCase());
	}
	
	public static boolean isPlotWorld(String name)
	{
		return PlotMe.plotmaps.containsKey(name.toLowerCase());
	}
	
	public static boolean isPlotWorld(Location l)
	{
		if(l == null)
			return false;
		else
			return PlotMe.plotmaps.containsKey(l.getWorld().getName().toLowerCase());
	}
	
	public static boolean isPlotWorld(Player p)
	{
		if(p == null)
			return false;
		else
			return PlotMe.plotmaps.containsKey(p.getWorld().getName().toLowerCase());
	}
	
	public static boolean isPlotWorld(Block b)
	{
		if(b == null)
			return false;
		else
			return PlotMe.plotmaps.containsKey(b.getWorld().getName().toLowerCase());
	}
	
	public static boolean isPlotWorld(BlockState b)
	{
		if(b == null)
			return false;
		else
			return PlotMe.plotmaps.containsKey(b.getWorld().getName().toLowerCase());
	}
	
	public static boolean isEconomyEnabled(World w)
	{
		PlotMapInfo pmi = getMap(w);
		
		if(pmi == null)
			return false;
		else
			return pmi.UseEconomy && PlotMe.globalUseEconomy;
	}
	
	public static boolean isEconomyEnabled(String name)
	{
		PlotMapInfo pmi = getMap(name);
		
		if(pmi == null)
			return false;
		else
			return pmi.UseEconomy && PlotMe.globalUseEconomy;
	}
	
	public static boolean isEconomyEnabled(Player p)
	{
		if(PlotMe.economy == null) return false;
		
		PlotMapInfo pmi = getMap(p);
		
		if(pmi == null)
			return false;
		else
			return pmi.UseEconomy && PlotMe.globalUseEconomy;
	}
	
	public static boolean isEconomyEnabled(Block b)
	{
		PlotMapInfo pmi = getMap(b);
		
		if(pmi == null)
			return false;
		else
			return pmi.UseEconomy && PlotMe.globalUseEconomy;
	}
	
	public static PlotMapInfo getMap(World w)
	{
		if(w == null)
			return null;
		else
		{			
			String worldname = w.getName().toLowerCase();
			
			if(PlotMe.plotmaps.containsKey(worldname))
				return PlotMe.plotmaps.get(worldname);
			else
				return null;
		}
	}
	
	public static PlotMapInfo getMap(String name)
	{
		String worldname = name.toLowerCase();
		
		if(PlotMe.plotmaps.containsKey(worldname))
			return PlotMe.plotmaps.get(worldname);
		else
			return null;
	}
	
	public static PlotMapInfo getMap(Location l)
	{
		if(l == null)
			return null;
		else
		{
			String worldname = l.getWorld().getName().toLowerCase();
			
			if(PlotMe.plotmaps.containsKey(worldname))
				return PlotMe.plotmaps.get(worldname);
			else
				return null;
		}
	}
	
	public static PlotMapInfo getMap(Player p)
	{
		if(p == null)
			return null;
		else
		{
			String worldname = p.getWorld().getName().toLowerCase();
			
			if(PlotMe.plotmaps.containsKey(worldname))
				return PlotMe.plotmaps.get(worldname);
			else
				return null;
		}
	}
	
	public static PlotMapInfo getMap(Block b)
	{
		if(b == null)
			return null;
		else
		{
			String worldname = b.getWorld().getName().toLowerCase();
			
			if(PlotMe.plotmaps.containsKey(worldname))
				return PlotMe.plotmaps.get(worldname);
			else
				return null;
		}
	}
	
	public static HashMap<String, Plot> getPlots(World w)
	{
		PlotMapInfo pmi = getMap(w);
		
		if(pmi == null)
			return null;
		else
			return pmi.plots;
	}
	
	public static HashMap<String, Plot> getPlots(String name)
	{		
		PlotMapInfo pmi = getMap(name);
		
		if(pmi == null)
			return null;
		else
			return pmi.plots;
	}
	
	public static HashMap<String, Plot> getPlots(Player p)
	{		
		PlotMapInfo pmi = getMap(p);
		
		if(pmi == null)
			return null;
		else
			return pmi.plots;
	}
	
	public static HashMap<String, Plot> getPlots(Block b)
	{	
		PlotMapInfo pmi = getMap(b);
		
		if(pmi == null)
			return null;
		else
			return pmi.plots;
	}
	
	public static HashMap<String, Plot> getPlots(Location l)
	{
		PlotMapInfo pmi = getMap(l);
		
		if(pmi == null)
			return null;
		else
			return pmi.plots;
	}
	
	public static Plot getPlotById(World w, String id)
	{
		HashMap<String, Plot> plots = getPlots(w);
		
		if(plots == null)
			return null;
		else
			return plots.get(id);
	}
	
	public static Plot getPlotById(String name, String id)
	{
		HashMap<String, Plot> plots = getPlots(name);
		
		if(plots == null)
			return null;
		else
			return plots.get(id);
	}
	
	public static Plot getPlotById(Player p, String id)
	{
		HashMap<String, Plot> plots = getPlots(p);
		
		if(plots == null)
			return null;
		else
			return plots.get(id);
	}
	
	public static Plot getPlotById(Player p)
	{
		HashMap<String, Plot> plots = getPlots(p);
		String plotid = getPlotId(p.getLocation());
		
		if(plots == null || plotid == "")
			return null;
		else
			return plots.get(plotid);
	}
	
	public static Plot getPlotById(Location l)
	{
		HashMap<String, Plot> plots = getPlots(l);
		String plotid = getPlotId(l);
		
		if(plots == null || plotid == "")
			return null;
		else
			return plots.get(plotid);
	}
	
	public static Plot getPlotById(Block b, String id)
	{
		HashMap<String, Plot> plots = getPlots(b);
		
		if(plots == null)
			return null;
		else
			return plots.get(id);
	}
	
	public static Plot getPlotById(Block b)
	{
		HashMap<String, Plot> plots = getPlots(b);
		String plotid = getPlotId(b.getLocation());
		
		if(plots == null || plotid == "")
			return null;
		else
			return plots.get(plotid);
	}
}
