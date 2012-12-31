package com.worldcretornica.plotme.listener;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;
import com.worldcretornica.plotme.PlotMapInfo;
import com.worldcretornica.plotme.PlotMe;

public class PlotListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH) //, ignoreCancelled = true
	public void onBlockBreak(final BlockBreakEvent event) 
	{
		Player p = event.getPlayer();

		boolean canbuild = PlotMe.cPerms(event.getPlayer(), "PlotMe.admin");
		
		Block b = event.getBlock();
		
		if(PlotManager.isPlotWorld(b))
		{
			String id = PlotManager.getPlotId(b.getLocation());
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
			}else{
				Plot plot = PlotManager.getMap(p).plots.get(id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}else{
					
					if(!plot.isAllowed(p.getName()))
					{
						if(!canbuild)
						{
							p.sendMessage(PlotMe.caption("ErrCannotBuild"));
							event.setCancelled(true);
						}
					}else{
						plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH) //, ignoreCancelled = true
	public void onBlockPlace(final BlockPlaceEvent event)
	{
		Player p = event.getPlayer();

		boolean canbuild = PlotMe.cPerms(event.getPlayer(), "PlotMe.admin");

		Block b = event.getBlock();
		
		if(PlotManager.isPlotWorld(b))
		{
			String id = PlotManager.getPlotId(b.getLocation());
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
			}else{
				Plot plot = PlotManager.getPlotById(p,id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}else{
					if(!plot.isAllowed(p.getName()))
					{
						if(!canbuild)
						{
							p.sendMessage(PlotMe.caption("ErrCannotBuild"));
							event.setCancelled(true);
						}
					}else{
						plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event)
	{
		if(!PlotMe.cPerms(event.getPlayer(), "PlotMe.admin"))
		{
			BlockFace bf = event.getBlockFace();
			Block b = event.getBlockClicked().getLocation().add(bf.getModX(), bf.getModY(), bf.getModZ()).getBlock();
			if(PlotManager.isPlotWorld(b))
			{
				String id = PlotManager.getPlotId(b.getLocation());
				Player p = event.getPlayer();
				
				if(id.equalsIgnoreCase(""))
				{
					p.sendMessage(PlotMe.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}else{
					Plot plot = PlotManager.getPlotById(p,id);
					
					if (plot == null)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}else{
						if(!plot.isAllowed(p.getName()))
						{
							p.sendMessage(PlotMe.caption("ErrCannotBuild"));
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerBucketFill(final PlayerBucketFillEvent event)
	{
		if(!PlotMe.cPerms(event.getPlayer(), "PlotMe.admin"))
		{
			Block b = event.getBlockClicked();
			if(PlotManager.isPlotWorld(b))
			{
				String id = PlotManager.getPlotId(b.getLocation());
				Player p = event.getPlayer();
				
				if(id.equalsIgnoreCase(""))
				{
					p.sendMessage(PlotMe.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}else{
					Plot plot = PlotManager.getPlotById(p,id);
					
					if (plot == null)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}else{
						if(!plot.isAllowed(p.getName()))
						{
							p.sendMessage(PlotMe.caption("ErrCannotBuild"));
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		boolean canbuild = PlotMe.cPerms(event.getPlayer(), "PlotMe.admin");
		
		Block b = event.getClickedBlock();
		
		if(PlotManager.isPlotWorld(b))
		{
			PlotMapInfo pmi = PlotManager.getMap(b);
			boolean blocked = false;
			
			
			for(int blockid : pmi.ProtectedBlocks)
			{
				if(blockid == b.getTypeId())
				{
					blocked = true;
				}
			}
						
			ItemStack is = event.getItem();
			
			if(is != null && event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				int itemid = is.getType().getId();
				byte itemdata = is.getData().getData();
				
				if(pmi.PreventedItems.contains("" + itemid) 
						|| pmi.PreventedItems.contains("" + itemid + ":" + itemdata))
				{
					blocked = true;
				}
			}
			
			if(blocked)
			{
				String id = PlotManager.getPlotId(b.getLocation());
				
				Player p = event.getPlayer();
				
				if(id.equalsIgnoreCase(""))
				{
					if(!canbuild)
					{
						if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
						{
							p.sendMessage(PlotMe.caption("ErrCannotUse"));
						}
						event.setCancelled(true);
					}
				}else{
					Plot plot = PlotManager.getPlotById(p,id);
					
					if (plot == null)
					{
						if(!canbuild)
						{
							if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
							{
								p.sendMessage(PlotMe.caption("ErrCannotUse"));
							}
							event.setCancelled(true);
						}
					}else{
						if(!plot.isAllowed(p.getName()))
						{
							if(!canbuild)
							{
								if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
								{
									p.sendMessage(PlotMe.caption("ErrCannotUse"));
								}
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockSpread(final BlockSpreadEvent event)
	{
		Block b = event.getBlock();
		if(PlotManager.isPlotWorld(b))
		{
			String id = PlotManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockForm(final BlockFormEvent event)
	{
		Block b = event.getBlock();
		if(PlotManager.isPlotWorld(b))
		{
			String id = PlotManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}
	

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockDamage(final BlockDamageEvent event)
	{
		Block b = event.getBlock();
		if(PlotManager.isPlotWorld(b))
		{
			String id = PlotManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFade(final BlockFadeEvent event)
	{
		Block b = event.getBlock();
		if(PlotManager.isPlotWorld(b))
		{
			String id = PlotManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFromTo(final BlockFromToEvent event)
	{
		Block b = event.getToBlock();
		if(PlotManager.isPlotWorld(b))
		{
			String id = PlotManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockGrow(final BlockGrowEvent event)
	{
		Block b = event.getBlock();
		if(PlotManager.isPlotWorld(b))
		{
			String id = PlotManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockIgnite(final BlockIgniteEvent event)
	{
		Block b = event.getBlock();
		if(PlotManager.isPlotWorld(b))
		{
			String id = PlotManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPistonExtend(final BlockPistonExtendEvent event)
	{
		boolean found = false;
		
		for(Block b : event.getBlocks())
		{
			if(found || PlotManager.isPlotWorld(b))
			{
				found = true;
				String id = PlotManager.getPlotId(b.getLocation().add(event.getDirection().getModX(), event.getDirection().getModY(), event.getDirection().getModZ()));
										
				if(id.equalsIgnoreCase(""))
				{
					event.setCancelled(true);
					break;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPistonRetract(final BlockPistonRetractEvent event)
	{
		Block b = event.getRetractLocation().getBlock();
		
		if(PlotManager.isPlotWorld(b) && event.getBlock().getType() == Material.PISTON_STICKY_BASE)
		{
			String id = PlotManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onStructureGrow(final StructureGrowEvent event)
	{
		List<BlockState> blocks = event.getBlocks();
		boolean found = false;

		for(int i = 0 ; i < blocks.size(); i++)
		{
			if(found || PlotManager.isPlotWorld(blocks.get(i)))
			{
				found = true;
				String id = PlotManager.getPlotId(blocks.get(i).getLocation());
										
				if(id.equalsIgnoreCase(""))
				{
					event.getBlocks().remove(i);
					i--;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplodeEvent(final EntityExplodeEvent event)
	{	
		Location l = event.getLocation();
		
		if(l != null)
		{
			PlotMapInfo pmi = PlotManager.getMap(l);
			
			if(pmi != null && pmi.DisableExplosion)
				event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockIgniteEvent(final BlockIgniteEvent event)
	{
		Block b = event.getBlock();
		
		if(b != null)
		{
			PlotMapInfo pmi = PlotManager.getMap(b);
			
			if(pmi != null)
			{
				if(pmi.DisableIgnition)
				{
					event.setCancelled(true);
				}
				else
				{
					String id = PlotManager.getPlotId(b.getLocation());
					Player p = event.getPlayer();
					
					if(id.equalsIgnoreCase("") || p == null)
					{
						event.setCancelled(true);
					}else{
						Plot plot = PlotManager.getPlotById(b,id);
						
						if (plot == null)
						{
							event.setCancelled(true);
						}else{
							if(!plot.isAllowed(p.getName()))
							{
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHangingBreakEvent(final HangingPlaceEvent event)
	{
		boolean canbuild = PlotMe.cPerms(event.getPlayer(), "PlotMe.admin");
		
		Block b = event.getBlock();
		
		if(PlotManager.isPlotWorld(b))
		{
			String id = PlotManager.getPlotId(b.getLocation());
			Player p = event.getPlayer();
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
			}else{
				Plot plot = PlotManager.getPlotById(p,id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}else{
					if(!plot.isAllowed(p.getName()))
					{
						if(!canbuild)
						{
							p.sendMessage(PlotMe.caption("ErrCannotBuild"));
							event.setCancelled(true);
						}
					}else{
						plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHangingBreakEvent(final HangingBreakByEntityEvent event)
	{
		Entity entity = event.getRemover();
		
		if(entity instanceof Player)
		{
			Player p = (Player)entity;
			
			boolean canbuild = PlotMe.cPerms(p, "PlotMe.admin");
	
			Location l = event.getEntity().getLocation();
			
			if(PlotManager.isPlotWorld(l))
			{
				String id = PlotManager.getPlotId(l);
				
				if(id.equalsIgnoreCase(""))
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}else{
					Plot plot = PlotManager.getPlotById(p,id);
					
					if (plot == null)
					{
						if(!canbuild)
						{
							p.sendMessage(PlotMe.caption("ErrCannotBuild"));
							event.setCancelled(true);
						}
					}else{
						if(!plot.isAllowed(p.getName()))
						{
							if(!canbuild)
							{
								p.sendMessage(PlotMe.caption("ErrCannotBuild"));
								event.setCancelled(true);
							}
						}else{
							plot.resetExpire(PlotManager.getMap(l).DaysToExpiration);
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteractEntityEvent(final PlayerInteractEntityEvent event)
	{
		Player p = event.getPlayer();
		
		boolean canbuild = PlotMe.cPerms(p, "PlotMe.admin");

		Location l = event.getRightClicked().getLocation();
		
		if(PlotManager.isPlotWorld(l))
		{
			String id = PlotManager.getPlotId(l);
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
			}else{
				Plot plot = PlotManager.getPlotById(p,id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}else{
					if(!plot.isAllowed(p.getName()))
					{
						if(!canbuild)
						{
							p.sendMessage(PlotMe.caption("ErrCannotBuild"));
							event.setCancelled(true);
						}
					}else{
						plot.resetExpire(PlotManager.getMap(l).DaysToExpiration);
					}
				}
			}
		}
	}	
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerEggThrowEvent(final PlayerEggThrowEvent event)
	{
		Player p = event.getPlayer();
			
		boolean canbuild = PlotMe.cPerms(p, "PlotMe.admin");

		Location l = event.getEgg().getLocation();
		
		if(PlotManager.isPlotWorld(l))
		{
			String id = PlotManager.getPlotId(l);
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe.caption("ErrCannotUseEggs"));
					event.setHatching(false);
				}
			}else{
				Plot plot = PlotManager.getPlotById(p,id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotUseEggs"));
						event.setHatching(false);
					}
				}else{
					if(!plot.isAllowed(p.getName()))
					{
						if(!canbuild)
						{
							p.sendMessage(PlotMe.caption("ErrCannotUseEggs"));
							event.setHatching(false);
						}
					}
				}
			}
		}
	}
}
