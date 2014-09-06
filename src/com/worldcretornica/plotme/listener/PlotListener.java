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
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;
import com.worldcretornica.plotme.PlotMapInfo;
import com.worldcretornica.plotme.PlotMe;

public class PlotListener implements Listener 
{
	
	@EventHandler(priority = EventPriority.HIGH) //, ignoreCancelled = true
	public void onBlockBreak(final BlockBreakEvent event) 
	{	
		Block b = event.getBlock();
		
		if(PlotManager.isPlotWorld(b))
		{
			Player p = event.getPlayer();
			boolean canbuild = PlotMe.cPerms(event.getPlayer(), "plotme.admin.buildanywhere");
			String id = PlotManager.getPlotId(b.getLocation());
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
			}
			else
			{
				Plot plot = PlotManager.getMap(p).plots.get(id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else if(!plot.isAllowed(p.getUniqueId()))
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else
				{
					plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
				}
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGH) //, ignoreCancelled = true
	public void onBlockPlace(final BlockPlaceEvent event)
	{
		Block b = event.getBlock();
		
		if(PlotManager.isPlotWorld(b))
		{
			Player p = event.getPlayer();
			boolean canbuild = PlotMe.cPerms(p, "plotme.admin.buildanywhere");
			String id = PlotManager.getPlotId(b.getLocation());
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
			}
			else
			{
				Plot plot = PlotManager.getPlotById(p,id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else if(!plot.isAllowed(p.getUniqueId()))
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else
				{
					plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityChangeBlock(final EntityChangeBlockEvent event)
    {
	    Block b = event.getBlock();
	    Entity e = event.getEntity();
        
        if(PlotManager.isPlotWorld(b))
        {
            if(!(e instanceof Player)) {
                if(!(e instanceof org.bukkit.entity.FallingBlock)) {
                    event.setCancelled(true);
                }
            } else {
                Player p = (Player) e;
                boolean canbuild = PlotMe.cPerms(p, "plotme.admin.buildanywhere");
                String id = PlotManager.getPlotId(b.getLocation());
                
                if(id.equalsIgnoreCase(""))
                {
                    if(!canbuild)
                    {
                        event.setCancelled(true);
                    }
                }
                else
                {
                    Plot plot = PlotManager.getPlotById(p,id);
                    
                    if (plot == null)
                    {
                        if(!canbuild)
                        {
                            event.setCancelled(true);
                        }
                    }
                    else if(!plot.isAllowed(p.getUniqueId()))
                    {
                        if(!canbuild)
                        {
                            event.setCancelled(true);
                        }
                    }
                    else
                    {
                        plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
                    }
                }
            }
        }
    }
	
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityBlockForm(final EntityBlockFormEvent event) {
        Block b = event.getBlock();
        Entity e = event.getEntity();

        if (PlotManager.isPlotWorld(b)) {
            if (!(e instanceof Player)) {
                event.setCancelled(true);
            }
        }
    }
	
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event)
	{
		if(!PlotMe.cPerms(event.getPlayer(), "plotme.admin.buildanywhere"))
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
				}
				else
				{
					Plot plot = PlotManager.getPlotById(p,id);
					
					if (plot == null)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
					else if(!plot.isAllowed(p.getUniqueId()))
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerBucketFill(final PlayerBucketFillEvent event)
	{
		if(!PlotMe.cPerms(event.getPlayer(), "plotme.admin.buildanywhere"))
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
				}
				else
				{
					Plot plot = PlotManager.getPlotById(p,id);
					
					if (plot == null)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
					else if(!plot.isAllowed(p.getUniqueId()))
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		Block b = event.getClickedBlock();

		if(PlotManager.isPlotWorld(b))
		{
			PlotMapInfo pmi = PlotManager.getMap(b);
			boolean blocked = false;
			Player player = event.getPlayer();
			boolean canbuild = PlotMe.cPerms(player, "plotme.admin.buildanywhere");
			
			if(event.isBlockInHand() && event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
			    ItemStack is = player.getItemInHand();
			    
			    if(event.getClickedBlock() != null && is != null) 
			    {
			        Material matClicked = event.getClickedBlock().getType();
			        Material matHeld = is.getType();
			        
			        if(matClicked == matHeld) 
			        {
        				BlockFace face = event.getBlockFace();
        				Block builtblock = b.getWorld().getBlockAt(b.getX() + face.getModX(), b.getY() + face.getModY(), b.getZ() + face.getModZ());
        				
        				String id = PlotManager.getPlotId(builtblock.getLocation());
        				
        				Player p = event.getPlayer();
        				
        				if(id.equalsIgnoreCase(""))
        				{
        					if(!canbuild)
        					{
        						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
        						event.setCancelled(true);
        					}
        				}
        				else
        				{
        					Plot plot = PlotManager.getPlotById(p,id);
        					
        					if (plot == null)
        					{
        						if(!canbuild)
        						{
        							p.sendMessage(PlotMe.caption("ErrCannotBuild"));
        							event.setCancelled(true);
        						}
        					}
        					else
        					{
        						if(!plot.isAllowed(p.getName()))
        						{
        							if(!canbuild)
        							{
        								p.sendMessage(PlotMe.caption("ErrCannotBuild"));
        								event.setCancelled(true);
        							}
        						}
        						else
        						{
        							plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
        						}
        					}
        				}
    				}
			    }
			}
			else
			{				
				if(pmi.ProtectedBlocks.contains(b.getTypeId()))
				{
					if(!PlotMe.cPerms(player, "plotme.unblock." + b.getTypeId()))
						blocked = true;
				}
						
				ItemStack is = event.getItem();
				
				if(is != null && event.getAction() == Action.RIGHT_CLICK_BLOCK)
				{
					int itemid = is.getType().getId();
					byte itemdata = is.getData().getData();
					
					if(pmi.PreventedItems.contains("" + itemid) 
							|| pmi.PreventedItems.contains("" + itemid + ":" + itemdata))
					{
						if(!PlotMe.cPerms(player, "plotme.unblock." + itemid))
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
					}
					else
					{
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
						}
						else if(!plot.isAllowed(p.getName()))
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
	public void onBlockPistonExtend(final BlockPistonExtendEvent event)
	{
		if(PlotManager.isPlotWorld(event.getBlock()))
		{
			BlockFace face = event.getDirection();
			
			for(Block b : event.getBlocks())
			{
				String id = PlotManager.getPlotId(b.getLocation().add(face.getModX(), face.getModY(), face.getModZ()));
										
				if(id.equalsIgnoreCase(""))
				{
					event.setCancelled(true);
					return;
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
	public void onEntityExplode(final EntityExplodeEvent event)
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
	public void onBlockIgnite(final BlockIgniteEvent event)
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
					}
					else
					{
						Plot plot = PlotManager.getPlotById(b,id);
						
						if (plot == null)
						{
							event.setCancelled(true);
						}
						else if(!plot.isAllowed(p.getUniqueId()))
						{
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHangingPlace(final HangingPlaceEvent event)
	{
		Block b = event.getBlock();
		
		if(PlotManager.isPlotWorld(b))
		{
			String id = PlotManager.getPlotId(b.getLocation());
			Player p = event.getPlayer();
			boolean canbuild = PlotMe.cPerms(event.getPlayer(), "plotme.admin.buildanywhere");
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
			}
			else
			{
				Plot plot = PlotManager.getPlotById(p,id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else if(!plot.isAllowed(p.getUniqueId()))
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else
				{
					plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHangingBreakByEntity(final HangingBreakByEntityEvent event)
	{
		Entity entity = event.getRemover();

		if(entity instanceof Player)
		{
			Player p = (Player)entity;
			
			boolean canbuild = PlotMe.cPerms(p, "plotme.admin.buildanywhere");
	
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
				}
				else
				{
					Plot plot = PlotManager.getPlotById(p,id);
					
					if (plot == null)
					{
						if(!canbuild)
						{
							p.sendMessage(PlotMe.caption("ErrCannotBuild"));
							event.setCancelled(true);
						}
					}
					else if(!plot.isAllowed(p.getUniqueId()))
					{
						if(!canbuild)
						{
							p.sendMessage(PlotMe.caption("ErrCannotBuild"));
							event.setCancelled(true);
						}
					}
					else
					{
						plot.resetExpire(PlotManager.getMap(l).DaysToExpiration);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteractEntity(final PlayerInteractEntityEvent event)
	{
		Location l = event.getRightClicked().getLocation();

		if(PlotManager.isPlotWorld(l))
		{
			Player p = event.getPlayer();
			boolean canbuild = PlotMe.cPerms(p, "plotme.admin.buildanywhere");
			String id = PlotManager.getPlotId(l);
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
			}
			else
			{
				Plot plot = PlotManager.getPlotById(p,id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else if(!plot.isAllowed(p.getUniqueId()))
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else
				{
					plot.resetExpire(PlotManager.getMap(l).DaysToExpiration);
				}
			}
		}
	}	
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event)
    {
        Location l = event.getEntity().getLocation();
        Entity e = event.getDamager();
        //TODO config to allow attacking others
        if(PlotManager.isPlotWorld(l))
        {
            if(!(e instanceof Player)){
                event.setCancelled(true);
            } else {
                Player p = (Player) e;
                boolean canbuild = PlotMe.cPerms(p, "plotme.admin.buildanywhere");
                String id = PlotManager.getPlotId(l);
                
                if(id.equalsIgnoreCase(""))
                {
                    if(!canbuild)
                    {
                        p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
                else
                {
                    Plot plot = PlotManager.getPlotById(p,id);
                    
                    if (plot == null)
                    {
                        if(!canbuild)
                        {
                            p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                            event.setCancelled(true);
                        }
                    }
                    else if(!plot.isAllowed(p.getUniqueId()))
                    {
                        if(!canbuild)
                        {
                            p.sendMessage(PlotMe.caption("ErrCannotBuild"));
                            event.setCancelled(true);
                        }
                    }
                    else
                    {
                        plot.resetExpire(PlotManager.getMap(l).DaysToExpiration);
                    }
                }
            }
        }
    }   
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerEggThrow(final PlayerEggThrowEvent event)
	{
		Location l = event.getEgg().getLocation();
		
		if(PlotManager.isPlotWorld(l))
		{
			Player p = event.getPlayer();
			boolean canbuild = PlotMe.cPerms(p, "plotme.admin.buildanywhere");
			String id = PlotManager.getPlotId(l);
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe.caption("ErrCannotUseEggs"));
					event.setHatching(false);
				}
			}
			else
			{
				Plot plot = PlotManager.getPlotById(p,id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotUseEggs"));
						event.setHatching(false);
					}
				}
				else if(!plot.isAllowed(p.getUniqueId()))
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
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event) {
	    Player p = event.getPlayer();
	    
	    if(p != null) {
	        PlotManager.UpdatePlayerNameFromId(p.getUniqueId(), p.getName());
	    }
	}
}
