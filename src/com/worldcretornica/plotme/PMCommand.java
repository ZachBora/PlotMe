package com.worldcretornica.plotme;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.milkbowl.vault.economy.EconomyResponse;

import org.angelsl.minecraft.randomshit.fontwidth.MinecraftFontWidthCalculator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PMCommand implements CommandExecutor
{
	private PlotMe plugin;
	private final ChatColor BLUE = ChatColor.BLUE;
	private final ChatColor RED = ChatColor.RED;
	private final ChatColor RESET = ChatColor.RESET;
	private final ChatColor AQUA = ChatColor.AQUA;
	private final ChatColor GREEN = ChatColor.GREEN;
	private final ChatColor ITALIC = ChatColor.ITALIC;
	private final String PREFIX = PlotMe.PREFIX;
	
	public PMCommand(PlotMe instance)
	{
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender s, Command c, String l, String[] args)
	{
		if(l.equalsIgnoreCase("plotme") || l.equalsIgnoreCase("plot") || l.equalsIgnoreCase("p"))
		{
			if(!(s instanceof Player))
			{
				if(args.length == 0 || args[0].equalsIgnoreCase("1"))
				{
					s.sendMessage(" ---==PlotMe v" + PlotMe.VERSION + " Console Help Page==--- "); 
					s.sendMessage(" - /plotme reload");
					s.sendMessage(" - Reloads the plugin and its configuration files");
					return true;
				}
				else
				{
					String a0 = args[0].toString();
					if(!(s instanceof Player))
					{
						if (a0.equalsIgnoreCase("reload")) { return reload(s, args);}
					}
				}
			}
			else
			{
				Player p = (Player)s;
				
				if(args.length == 0)
				{
					return showhelp(p, 1);
				}
				else
				{
					String a0 = args[0].toString();
					int ipage = -1;
					
					try  
					{  
						if(args.length > 0)
							ipage = Integer.parseInt( a0 );  
					}  
					catch( Exception e) {}
									
					if(ipage != -1)
					{
						return showhelp(p, ipage);
					}
					else
					{
						if (a0.equalsIgnoreCase("claim")) { return claim(p, args);}
						if (a0.equalsIgnoreCase("auto")) { return auto(p, args);}
						if (a0.startsWith("home") || a0.startsWith("h")) { return home(p, args);}
						if (a0.equalsIgnoreCase("info") || a0.equalsIgnoreCase("i")) { return info(p, args);}
						if (a0.equalsIgnoreCase("comment")) { return comment(p, args);}
						if (a0.equalsIgnoreCase("comments") || a0.equalsIgnoreCase("c")) { return comments(p, args);}
						if (a0.equalsIgnoreCase("biome") || a0.equalsIgnoreCase("b")) { return biome(p, args);}
						if (a0.equalsIgnoreCase("biomelist")) { return biomelist(p, args);}
						if (a0.equalsIgnoreCase("id")) { return id(p, args);}
						if (a0.equalsIgnoreCase("tp")) { return tp(p, args);}
						if (a0.equalsIgnoreCase("clear")) { return clear(p, args);}
						if (a0.equalsIgnoreCase("reset")) { return reset(p, args);}
						if (a0.equalsIgnoreCase("add") || a0.equalsIgnoreCase("+")) { return add(p, args);}
						if (a0.equalsIgnoreCase("remove") || a0.equalsIgnoreCase("-")) { return remove(p, args);}
						if (a0.equalsIgnoreCase("setowner") || a0.equalsIgnoreCase("o")) { return setowner(p, args);}
						if (a0.equalsIgnoreCase("move") || a0.equalsIgnoreCase("m")) { return move(p, args);}
						if (a0.equalsIgnoreCase("reload")) { return reload(s, args);}
						if (a0.equalsIgnoreCase("weanywhere")) { return weanywhere(p, args);}
						if (a0.equalsIgnoreCase("list")) { return plotlist(p, args);}
						if (a0.equalsIgnoreCase("expired")) { return expired(p, args);}
						if (a0.equalsIgnoreCase("addtime")) { return addtime(p, args);}
						if (a0.equalsIgnoreCase("done")) { return done(p, args);}
						if (a0.equalsIgnoreCase("donelist")) { return donelist(p, args);}
						if (a0.equalsIgnoreCase("protect")) { return protect(p, args);}
						
						if (a0.equalsIgnoreCase("sell")) { return sell(p, args);}
						if (a0.equalsIgnoreCase("dispose")) { return dispose(p, args);}
						if (a0.equalsIgnoreCase("auction")) { return auction(p, args);}
						if (a0.equalsIgnoreCase("buy")) { return buy(p, args);}
						if (a0.equalsIgnoreCase("bid")) { return bid(p, args);}
					}
				}
			}
		}
		return false;
	}
	
	private boolean bid(Player p, String[] args) 
	{	
		if(PlotManager.isEconomyEnabled(p))
		{
			if(PlotMe.cPerms(p, "PlotMe.use.bid"))
			{
				String id = PlotManager.getPlotId(p.getLocation());
				
				if(id.equals(""))
				{
					p.sendMessage(PREFIX + RED + " No plot found");
				}
				else
				{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						Plot plot = PlotManager.getPlotById(p,id);
						
						if(plot.auctionned)
						{
							String bidder = p.getName();
							
							if(plot.owner.equalsIgnoreCase(bidder))
							{
								p.sendMessage(PREFIX + RED + " You cannot bid on your own plot.");
							}
							else
							{
								if(args.length == 2)
								{
									double bid = 0;
									double currentbid = plot.currentbid;
									String currentbidder = plot.currentbidder;
									
									try  
									{  
										bid = Double.parseDouble(args[1]);  
									}  
									catch( Exception e){}
																		
									if(bid < currentbid || (bid == currentbid && !currentbidder.equals("")))
									{
										p.sendMessage(PREFIX + RED + " Invalid bid. Must be above " + RESET + round(plot.currentbid) + RED + " " + PlotMe.economy.currencyNamePlural() + ".");
									}
									else
									{
										double balance = PlotMe.economy.getBalance(bidder);
										
										if(bid > balance && !currentbidder.equals(bidder) ||
											currentbidder.equals(bidder) && bid > (balance + currentbid))
										{
											p.sendMessage(PREFIX + RED + " You do not have enough to bid this much.");
										}
										else
										{
											if(!currentbidder.equals(""))
											{
												EconomyResponse er = PlotMe.economy.depositPlayer(currentbidder, currentbid);
												
												if(!er.transactionSuccess())
												{
													p.sendMessage(PREFIX + RESET + " " + er.errorMessage);
												}
											}
											
											EconomyResponse er = PlotMe.economy.withdrawPlayer(bidder, bid);
											
											if(er.transactionSuccess())
											{
												plot.currentbidder = bidder;
												plot.currentbid = bid;
												
												plot.updateField("currentbidder", bidder);
												plot.updateField("currentbid", bid);
												
												PlotManager.setSellSign(p.getWorld(), plot);
												
												p.sendMessage(PREFIX + RESET + " Your bid has been placed");
											}
											else
											{
												p.sendMessage(PREFIX + RESET + " " + er.errorMessage);
											}
										}
									}
								}
								else
								{
									p.sendMessage(PREFIX + RESET + " Usage: " + RED + "/plotme bid <amount> " + RESET + "Example: " + RED + "/plotme bid 100");
								}
							}
						}
						else
						{
							p.sendMessage(PREFIX + RED + " This plot isn't being auctionned.");
						}
					}
					else
					{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
			else
			{
				p.sendMessage(PREFIX + RED + " Permission denied");
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Economy is disabled for this world.");
		}
		return true;
	}

	private boolean buy(Player p, String[] args) 
	{
		if(PlotManager.isEconomyEnabled(p))
		{
			if(PlotMe.cPerms(p, "PlotMe.use.buy") || PlotMe.cPerms(p, "PlotMe.admin.buy"))
			{
				Location l = p.getLocation();
				String id = PlotManager.getPlotId(l);
				
				if(id.equals(""))
				{
					p.sendMessage(PREFIX + RED + " No plot found");
				}
				else
				{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						Plot plot = PlotManager.getPlotById(p,id);
						
						if(!plot.forsale)
						{
							p.sendMessage(PREFIX + RED + " Plot isn't for sale.");
						}
						else
						{
							if(plot.owner.equalsIgnoreCase(p.getName()))
							{
								p.sendMessage(PREFIX + RED + " You can't buy your own plot.");
							}
							else
							{
								World w = p.getWorld();
								
								double cost = plot.customprice;
								String buyer = p.getName();
								
								if(PlotMe.economy.getBalance(buyer) < cost)
								{
									p.sendMessage(PREFIX + RED + " You do not have enough money to buy this plot.");
								}
								else
								{
									EconomyResponse er = PlotMe.economy.withdrawPlayer(buyer, cost);
									
									if(er.transactionSuccess())
									{
										String oldowner = plot.owner;
										
										if(!oldowner.equalsIgnoreCase("$Bank$"))
										{
											EconomyResponse er2 = PlotMe.economy.depositPlayer(oldowner, cost);
											
											if(!er2.transactionSuccess())
											{
												p.sendMessage(PREFIX + RED + " " + er2.errorMessage);
											}
										}
										
										plot.owner = buyer;
										plot.customprice = 0;
										plot.forsale = false;
										
										plot.updateField("owner", buyer);
										plot.updateField("customprice", 0);
										plot.updateField("forsale", false);
										
										PlotManager.adjustWall(l);
										PlotManager.setSellSign(w, plot);
										PlotManager.setOwnerSign(w, plot);
										
										p.sendMessage(PREFIX + RESET + " Plot is now yours.");
									}
									else
									{
										p.sendMessage(PREFIX + RED + " " + er.errorMessage);
									}
								}
							}
						}
					}
					else
					{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
			else
			{
				p.sendMessage(PREFIX + RED + " Permission denied");
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Economy is disabled for this world.");
		}
		return true;
	}

	private boolean auction(Player p, String[] args) 
	{
		if(PlotManager.isEconomyEnabled(p))
		{
			PlotMapInfo pmi = PlotManager.getMap(p);
			
			if(pmi.CanPutOnSale)
			{
				if(PlotMe.cPerms(p, "PlotMe.use.auction") || PlotMe.cPerms(p, "PlotMe.admin.auction"))
				{
					String id = PlotManager.getPlotId(p.getLocation());
					
					if(id.equals(""))
					{
						p.sendMessage(PREFIX + RED + " No plot found");
					}
					else
					{
						if(!PlotManager.isPlotAvailable(id, p))
						{
							Plot plot = PlotManager.getPlotById(p,id);
							
							if(plot.owner.equalsIgnoreCase(p.getName()) || PlotMe.cPerms(p, "PlotMe.admin.auction"))
							{
								World w = p.getWorld();
								
								if(plot.auctionned)
								{
									if(!PlotMe.cPerms(p, "PlotMe.admin.auction"))
									{
										p.sendMessage(PREFIX + RED + " Plot is already being auctionned. Ask an admin to cancel it.");
									}
									else
									{
										if(!plot.currentbidder.equalsIgnoreCase(""))
										{
											PlotMe.economy.depositPlayer(plot.currentbidder, plot.currentbid);
										}
										
										plot.auctionned = false;
										PlotManager.adjustWall(p.getLocation());
										PlotManager.setSellSign(w, plot);
										plot.currentbid = 0;
										plot.currentbidder = "";
										
										plot.updateField("currentbid", 0);
										plot.updateField("currentbidder", "");
										plot.updateField("auctionned", false);
										
										
										
										p.sendMessage(PREFIX + RESET + " Auction cancelled.");
									}
								}
								else
								{									
									double bid = 1;
									
									if(args.length == 2)
									{
										try  
										{  
											bid = Double.parseDouble(args[1]);  
										}  
										catch( Exception e){}
									}
									
									if(bid < 0)
									{
										p.sendMessage(PREFIX + RED + " Invalid auction amount. Must be above or equal to 0.");
									}
									else
									{
										plot.currentbid = bid;
										plot.auctionned = true;
										plot.forsale = false;
										PlotManager.adjustWall(p.getLocation());
										PlotManager.setSellSign(w, plot);
										
										plot.updateField("currentbid", bid);
										plot.updateField("auctionned", true);
										plot.updateField("forsale", true);
										
										p.sendMessage(PREFIX + RESET + " Auction started.");
									}
								}
							}
							else
							{
								p.sendMessage(PREFIX + RED + " You do not own this plot.");
							}
						}
						else
						{
							p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
						}
					}
				}
				else
				{
					p.sendMessage(PREFIX + RED + " Permission denied");
				}
			}
			else
			{
				p.sendMessage(PREFIX + RED + " Selling plots is disabled in this world.");
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Economy is disabled for this world.");
		}
		return true;
	}

	private boolean dispose(Player p, String[] args) 
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.dispose") || PlotMe.cPerms(p, "PlotMe.use.dispose"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				String id = PlotManager.getPlotId(p.getLocation());
				if(id.equals(""))
				{
					p.sendMessage(PREFIX + RED + " No plot found");
				}
				else
				{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						Plot plot = PlotManager.getPlotById(p,id);
						
						if(plot.protect)
						{
							p.sendMessage(PREFIX + RED + " Plot is protected and cannot be disposed.");
						}
						else
						{
							if(plot.owner.equalsIgnoreCase(p.getName()) || PlotMe.cPerms(p, "PlotMe.admin.clear"))
							{
								if(plot.auctionned)
								{
									String currentbidder = plot.currentbidder;
									
									if(!currentbidder.equals(""))
									{
										EconomyResponse er = PlotMe.economy.depositPlayer(currentbidder, plot.currentbid);
										
										if(!er.transactionSuccess())
										{
											p.sendMessage(PREFIX + RESET + " " + er.errorMessage);
										}
									}
								}
								
								World w = p.getWorld();
								
								if(!PlotManager.isPlotAvailable(id, p))
								{
									PlotManager.getPlots(p).remove(id);
								}
								
								PlotManager.removeOwnerSign(w, id);
								PlotManager.removeSellSign(w, id);
								
								SqlManager.deletePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), p.getWorld().getName().toLowerCase());
								
								p.sendMessage(PREFIX + RESET + " Plot disposed. Anyone can claim it.");
							}
							else
							{
								p.sendMessage(PREFIX + RED + " This plot(" + id + ") is not yours. You are not allowed to dispose it.");
							}
						}
					}
					else
					{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}

	private boolean sell(Player p, String[] args) 
	{
		if(PlotManager.isEconomyEnabled(p))
		{
			PlotMapInfo pmi = PlotManager.getMap(p);
			
			if(pmi.CanSellToBank || pmi.CanPutOnSale)
			{
				if(PlotMe.cPerms(p, "PlotMe.use.sell") || PlotMe.cPerms(p, "PlotMe.admin.sell"))
				{
					Location l = p.getLocation();
					String id = PlotManager.getPlotId(l);
					
					if(id.equals(""))
					{
						p.sendMessage(PREFIX + RED + " No plot found");
					}
					else
					{
						if(!PlotManager.isPlotAvailable(id, p))
						{
							Plot plot = PlotManager.getPlotById(p,id);
							
							if(plot.owner.equalsIgnoreCase(p.getName()) || PlotMe.cPerms(p, "PlotMe.admin.sell"))
							{
								if(plot.auctionned && !plot.currentbidder.equals(""))
								{
									p.sendMessage(PREFIX + RED + " You can't sell a plot with bids on it. Ask an admin to cancel the auction.");
								}
								else
								{
									World w = p.getWorld();
									
									if(plot.forsale)
									{
										plot.customprice = 0;
										plot.forsale = false;
										
										plot.updateField("customprice", 0);
										plot.updateField("forsale", false);
										
										PlotManager.adjustWall(l);
										PlotManager.setSellSign(w, plot);
										
										p.sendMessage(PREFIX + RESET + " Plot no longer for sale.");
									}
									else
									{
										double price = pmi.SellToPlayerPrice;
										boolean bank = false;
										
										if(args.length == 2)
										{
											if(args[1].equalsIgnoreCase("bank"))
											{
												bank = true;
											}
											else
											{
												if(pmi.CanCustomizeSellPrice)
												{
													try  
													{  
														price = Double.parseDouble(args[1]);  
													}  
													catch( Exception e)
													{
														if(pmi.CanSellToBank)
														{
															p.sendMessage(PREFIX + RESET + " Usage : " + RED + " /plotme sell bank|<amount>");
															p.sendMessage("  Example : " + RED + "/plotme sell bank " + RESET + " or " + RED + " /plotme sell 200");
														}
														else
														{
															p.sendMessage(PREFIX + RESET + " Usage : " + RED + " /plotme sell <amount>" + RESET + " Example : " + RED + "/plotme sell 200");
														}
													}
												}
												else
												{
													p.sendMessage(PREFIX + RED + " You cannot customize the price. Default price is : " + price);
													return true;
												}
											}
										}
										
										if(bank)
										{
											if(!pmi.CanSellToBank)
											{
												p.sendMessage(PREFIX + RED + " Plots cannot be sold to the bank in this world.");
											}
											else
											{
												double sellprice = pmi.SellToBankPrice;
												
												PlotMe.economy.depositPlayer(p.getName(), sellprice);
												plot.owner = "$Bank$";
												plot.forsale = true;
												plot.customprice = pmi.BuyFromBankPrice;
												
												for(String name : plot.allowed())
												{
													plot.removeAllowed(name);
												}
												
												PlotManager.setOwnerSign(w, plot);
												PlotManager.setSellSign(w, plot);
												
												plot.updateField("owner", plot.owner);
												plot.updateField("forsale", plot.forsale);
												plot.updateField("customprice", plot.customprice);
												
												p.sendMessage(PREFIX + RESET + " Plot sold to the bank for " + round(sellprice) + " " + PlotMe.economy.currencyNamePlural() + ".");
											}
										}
										else
										{
											if(price < 0)
											{
												p.sendMessage(PREFIX + RED + " Invalid amount. Must be above or equal to 0.");
											}
											else
											{
												plot.customprice = price;
												plot.forsale = true;
												
												plot.updateField("customprice", price);
												plot.updateField("forsale", true);
												
												PlotManager.adjustWall(l);
												PlotManager.setSellSign(w, plot);
												
												p.sendMessage(PREFIX + RESET + " Plot now for sale.");
											}
										}
									}
								}
							}
							else
							{
								p.sendMessage(PREFIX + RED + " You do not own this plot.");
							}
						}
						else
						{
							p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
						}
					}
				}
				else
				{
					p.sendMessage(PREFIX + RED + " Permission denied");
				}
			}
			else
			{
				p.sendMessage(PREFIX + RED + " Selling plots is disabled in this world.");
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Economy is disabled for this world.");
		}
		return true;
	}

	private boolean protect(Player p, String[] args) 
	{
		if(PlotMe.cPerms(p, "PlotMe.admin.protect"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
				return true;
			}
			else
			{
				String id = PlotManager.getPlotId(p.getLocation());
				
				if(id.equals(""))
				{
					p.sendMessage(PREFIX + RED + " No plot found");
				}
				else
				{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						Plot plot = PlotManager.getPlotById(p,id);
						
						if(plot.protect)
						{
							plot.protect = false;
							PlotManager.adjustWall(p.getLocation());
							p.sendMessage(PREFIX + RESET + " Plot is no longer protected. It is now possible to Clear or Reset it.");
						}
						else
						{
							plot.protect = true;
							PlotManager.adjustWall(p.getLocation());
							p.sendMessage(PREFIX + RESET + " Plot is now protected. It won't be possible to Clear or Reset it.");
						}
					}
					else
					{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}

	private boolean donelist(Player p, String[] args) 
	{
		if(PlotMe.cPerms(p, "PlotMe.admin.done"))
		{
			PlotMapInfo pmi = PlotManager.getMap(p);
			
			if(pmi == null)
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
				return true;
			}
			else
			{
				
				HashMap<String, Plot> plots = pmi.plots;
				List<Plot> finishedplots = new ArrayList<Plot>();
				int nbfinished = 0;
				int maxpage = 0;
				int pagesize = 8;
				int page = 1;
				
				if(args.length == 2)
				{
					try
					{
						page = Integer.parseInt(args[1]);
					}catch(NumberFormatException ex){}
				}
				
				for(String id : plots.keySet())
				{
					Plot plot = plots.get(id);
					
					if(plot.finished)
					{
						finishedplots.add(plot);
						nbfinished++;
					}
				}
				
				Collections.sort(finishedplots, new PlotFinishedComparator());
				
				maxpage = (int) Math.ceil(((double)nbfinished/(double)pagesize));
				
				if(finishedplots.size() == 0)
				{
					p.sendMessage(PREFIX + RESET + " No plots are finished");
				}
				else
				{
					p.sendMessage(PREFIX + RESET + " Finished plots page " + page + "/" + maxpage);
					
					for(int i = (page-1) * pagesize; i < finishedplots.size() && i < (page * pagesize); i++)
					{	
						Plot plot = finishedplots.get(i);
						
						String starttext = "  " + BLUE + plot.id + RESET + " -> " + plot.owner;
						
						int textLength = MinecraftFontWidthCalculator.getStringWidth(starttext);						
						
						String line = starttext + whitespace(550 - textLength) + "@" + plot.finisheddate;

						p.sendMessage(line);
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}

	private boolean done(Player p, String[] args)
	{
		if(PlotMe.cPerms(p, "PlotMe.use.done") || PlotMe.cPerms(p, "PlotMe.admin.done"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
				return true;
			}
			else
			{
				String id = PlotManager.getPlotId(p.getLocation());
				
				if(id.equals(""))
				{
					p.sendMessage(PREFIX + RED + " No plot found");
				}
				else
				{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						Plot plot = PlotManager.getPlotById(p,id);
						
						if(plot.owner.equalsIgnoreCase(p.getName()) || PlotMe.cPerms(p, "PlotMe.admin.done"))
						{							
							if(plot.finished)
							{
								plot.setUnfinished();
								p.sendMessage(PREFIX + RESET + " Plot is no longer marked finished.");
							}
							else
							{
								plot.setFinished();
								p.sendMessage(PREFIX + RESET + " Plot is now marked finished.");
							}
						}
					}
					else
					{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean addtime(Player p, String[] args)
	{
		if(PlotMe.cPerms(p, "PlotMe.admin.addtime"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
				return true;
			}
			else
			{
				String id = PlotManager.getPlotId(p.getLocation());
				
				if(id.equals(""))
				{
					p.sendMessage(PREFIX + RED + " No plot found");
				}
				else
				{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						Plot plot = PlotManager.getPlotById(p,id);
						
						if(plot != null)
						{
							plot.resetExpire(PlotManager.getMap(p).DaysToExpiration);
							p.sendMessage(PREFIX + RESET + " Plot expiration reset");
						}
					}
					else
					{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}

	private boolean expired(Player p, String[] args)
	{
		if(PlotMe.cPerms(p, "PlotMe.admin.expired"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
				return true;
			}
			else
			{
				int pagesize = 8;
				int page = 1;
				int maxpage = 0;
				int nbexpiredplots = 0; 
				World w = p.getWorld();
				List<Plot> expiredplots = new ArrayList<Plot>();
				HashMap<String, Plot> plots = PlotManager.getPlots(w);
				String date = PlotMe.getDate();
				
				if(args.length == 2)
				{
					try
					{
						page = Integer.parseInt(args[1]);
					}catch(NumberFormatException ex){}
				}
				
				for(String id : plots.keySet())
				{
					Plot plot = plots.get(id);
					
					if(plot.expireddate != null && PlotMe.getDate(plot.expireddate).compareTo(date.toString()) < 0)
					{
						nbexpiredplots++;
						expiredplots.add(plot);
					}
				}
				
				Collections.sort(expiredplots);
								
				maxpage = (int) Math.ceil(((double)nbexpiredplots/(double)pagesize));
				
				if(expiredplots.size() == 0)
				{
					p.sendMessage(PREFIX + RESET + " No plots are expired");
				}
				else
				{
					p.sendMessage(PREFIX + RESET + " Expired plots page " + page + "/" + maxpage);
					
					for(int i = (page-1) * pagesize; i < expiredplots.size() && i < (page * pagesize); i++)
					{	
						Plot plot = expiredplots.get(i);
						
						String starttext = "  " + BLUE + plot.id + RESET + " -> " + plot.owner;
						
						int textLength = MinecraftFontWidthCalculator.getStringWidth(starttext);						
						
						String line = starttext + whitespace(550 - textLength) + "@" + plot.expireddate.toString();

						p.sendMessage(line);
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}

	private boolean plotlist(Player p, String[] args)
	{
		if(PlotMe.cPerms(p, "PlotMe.use.list"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
				return true;
			}
			else
			{
				String name;
				
				if(PlotMe.cPerms(p, "PlotMe.admin.list") && args.length == 2)
				{
					name = args[1];
					p.sendMessage(PREFIX + RESET + " List of plots where " + BLUE + name + RESET + " can build:");
				}
				else
				{
					name = p.getName();
					p.sendMessage(PREFIX + RESET + " List of plots where you can build:");
				}
								
				for(Plot plot : PlotManager.getPlots(p).values())
				{
					String expiration = "";
						
					if(plot.expireddate != null)
					{
						java.util.Date tempdate = plot.expireddate; 
						
						if(tempdate.compareTo(Calendar.getInstance().getTime()) < 0)
						{
							expiration = RED + " @" + plot.expireddate.toString() + RESET;
						}else{
							expiration = " @" + plot.expireddate.toString();
						}
					}
						
					if(plot.owner.equalsIgnoreCase(name))
					{
						if(plot.allowedcount() == 0)
						{
							if(name.equalsIgnoreCase(p.getName()))
								p.sendMessage("  " + plot.id + " -> " + BLUE + ITALIC + "Yours" + RESET + expiration);
							else
								p.sendMessage("  " + plot.id + " -> " + BLUE + ITALIC + plot.owner + RESET + expiration);
						}
						else
						{
							StringBuilder helpers = new StringBuilder();
							for(int i = 0 ; i < plot.allowedcount(); i++)
							{
								helpers.append(BLUE).append(plot.allowed().toArray()[i]).append(RESET).append(", ");
							}
							if(helpers.length() > 2)
								helpers.delete(helpers.length() - 2, helpers.length());
							
							if(name.equalsIgnoreCase(p.getName()))
								p.sendMessage("  " + plot.id + " -> " + BLUE + ITALIC + "Yours" + RESET + expiration + ", Helpers: " + helpers);
							else
								p.sendMessage("  " + plot.id + " -> " + BLUE + ITALIC + plot.owner + RESET + expiration + ", Helpers: " + helpers);
						}
					}
					else if(plot.isAllowed(name))
					{
						StringBuilder helpers = new StringBuilder();
						for(int i = 0 ; i < plot.allowedcount(); i++)
						{
							if(p.getName().equalsIgnoreCase((String) plot.allowed().toArray()[i]))
								if(name.equalsIgnoreCase(p.getName()))
									helpers.append(BLUE).append(ITALIC).append("You").append(RESET).append(", ");
								else
									helpers.append(BLUE).append(ITALIC).append(name).append(RESET).append(", ");
							else
								helpers.append(BLUE).append(plot.allowed().toArray()[i]).append(RESET).append(", ");
						}
						if(helpers.length() > 2)
							helpers.delete(helpers.length() - 2, helpers.length());
						
						if(plot.owner.equalsIgnoreCase(p.getName()))
							p.sendMessage("  " + plot.id + " -> " + BLUE + "Yours" + RESET + expiration + ", Helpers: " + helpers);
						else
							p.sendMessage("  " + plot.id + " -> " + BLUE + plot.owner + "'s" + RESET + expiration + ", Helpers: " + helpers);
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}

	private boolean weanywhere(Player p, String[] args)
	{
		if(PlotMe.cPerms(p, "PlotMe.admin.weanywhere"))
		{
			if(PlotMe.isIgnoringWELimit(p))
			{
				PlotMe.removeIgnoreWELimit(p);
				p.sendMessage(PREFIX + RESET + " You can now only WorldEdit in your plots");
			}
			else
			{
				PlotMe.addIgnoreWELimit(p);
				p.sendMessage(PREFIX + RESET + " You can now WorldEdit anywhere");
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	
	private boolean showhelp(Player p, int page)
	{
		int max = 4;
		int maxpage = 0;
		boolean ecoon = PlotManager.isEconomyEnabled(p);
		
		List<String> allowed_commands = new ArrayList<String>();
		
		allowed_commands.add("limit");
		if(PlotMe.cPerms(p, "PlotMe.use.claim")) allowed_commands.add("claim");
		if(PlotMe.cPerms(p, "PlotMe.use.claim.other")) allowed_commands.add("claim.other");
		if(PlotMe.cPerms(p, "PlotMe.use.auto")) allowed_commands.add("auto");
		if(PlotMe.cPerms(p, "PlotMe.use.home")) allowed_commands.add("home");
		if(PlotMe.cPerms(p, "PlotMe.use.home.other")) allowed_commands.add("home.other");
		if(PlotMe.cPerms(p, "PlotMe.use.info"))
		{
			allowed_commands.add("info");
			allowed_commands.add("biomeinfo");
		}
		if(PlotMe.cPerms(p, "PlotMe.use.comment")) allowed_commands.add("comment");
		if(PlotMe.cPerms(p, "PlotMe.use.comments")) allowed_commands.add("comments");
		if(PlotMe.cPerms(p, "PlotMe.use.list")) allowed_commands.add("list");
		if(PlotMe.cPerms(p, "PlotMe.use.biome"))
		{
			allowed_commands.add("biome");
			allowed_commands.add("biomelist");
		}
		if(PlotMe.cPerms(p, "PlotMe.use.done") || PlotMe.cPerms(p, "PlotMe.admin.done")) allowed_commands.add("done");
		if(PlotMe.cPerms(p, "PlotMe.admin.done")) allowed_commands.add("donelist");
		if(PlotMe.cPerms(p, "PlotMe.admin.tp")) allowed_commands.add("tp");
		if(PlotMe.cPerms(p, "PlotMe.admin.id")) allowed_commands.add("id");
		if(PlotMe.cPerms(p, "PlotMe.use.clear") || PlotMe.cPerms(p, "PlotMe.admin.clear")) allowed_commands.add("clear");
		if(PlotMe.cPerms(p, "PlotMe.admin.dispose") || PlotMe.cPerms(p, "PlotMe.use.dispose")) allowed_commands.add("dispose");
		if(PlotMe.cPerms(p, "PlotMe.admin.reset")) allowed_commands.add("reset");
		if(PlotMe.cPerms(p, "PlotMe.use.add") || PlotMe.cPerms(p, "PlotMe.admin.add")) allowed_commands.add("add");
		if(PlotMe.cPerms(p, "PlotMe.use.remove") || PlotMe.cPerms(p, "PlotMe.admin.remove")) allowed_commands.add("remove");
		if(PlotMe.cPerms(p, "PlotMe.admin.setowner")) allowed_commands.add("setowner");
		if(PlotMe.cPerms(p, "PlotMe.admin.move")) allowed_commands.add("move");
		if(PlotMe.cPerms(p, "PlotMe.admin.weanywhere")) allowed_commands.add("weanywhere");
		if(PlotMe.cPerms(p, "PlotMe.admin.reload")) allowed_commands.add("reload");
		if(PlotMe.cPerms(p, "PlotMe.admin.list")) allowed_commands.add("listother");
		if(PlotMe.cPerms(p, "PlotMe.admin.expired")) allowed_commands.add("expired");
		if(PlotMe.cPerms(p, "PlotMe.admin.addtime")) allowed_commands.add("addtime");
		
		PlotMapInfo pmi = PlotManager.getMap(p);
		
		if(PlotManager.isPlotWorld(p) && ecoon)
		{
			if(PlotMe.cPerms(p, "PlotMe.use.buy")) allowed_commands.add("buy");
			if(PlotMe.cPerms(p, "PlotMe.use.sell")) 
			{
				allowed_commands.add("sell");
				if(pmi.CanSellToBank)
				{
					allowed_commands.add("sellbank");
				}
			}
			if(PlotMe.cPerms(p, "PlotMe.use.auction")) allowed_commands.add("auction");
		}
		
		maxpage = (int) Math.floor((double) allowed_commands.size() / max);
		
		if (page > maxpage)
			page = 1;
		
		p.sendMessage(RED + " ---==" + BLUE + "PlotMe Help Page " + page + "/" + maxpage + RED + "==--- ");
		
		for(int ctr = (page - 1) * max; ctr < (page * max) && ctr < allowed_commands.size(); ctr++)
		{
			String allowedcmd = allowed_commands.get(ctr);
			
			if(allowedcmd.equalsIgnoreCase("limit"))
			{
				if(PlotManager.isPlotWorld(p))
				{
					int maxplots = PlotMe.getPlotLimit(p);
					int ownedplots = PlotManager.getNbOwnedPlot(p);
					
					if(maxplots == -1)
						p.sendMessage(GREEN + "Your plot limit in this world : " + AQUA + ownedplots + GREEN + " used of " + AQUA + "Infinite");
					else
						p.sendMessage(GREEN + "Your plot limit in this world : " + AQUA + ownedplots + GREEN + " used of " + AQUA + maxplots);
				}
				else
				{
					p.sendMessage(GREEN + "Your plot limit in this world : " + AQUA + "Not a plot world");
				}
			}
			else if(allowedcmd.equalsIgnoreCase("claim"))
			{
				p.sendMessage(GREEN + " /plotme claim");
				if(ecoon && pmi != null && pmi.ClaimPrice != 0)
					p.sendMessage(AQUA + " Claim the current plot you are standing on. Price : " + RESET + pmi.ClaimPrice);
				else
					p.sendMessage(AQUA + " Claim the current plot you are standing on.");
			}
			else if(allowedcmd.equalsIgnoreCase("claim.other"))
			{
				p.sendMessage(GREEN + " /plotme claim <player>");
				if(ecoon && pmi != null && pmi.ClaimPrice != 0)
					p.sendMessage(AQUA + " Claim the current plot you are standing on for another player. Price : " + RESET + pmi.ClaimPrice);
				else
					p.sendMessage(AQUA + " Claim the current plot you are standing on for another player.");
			}
			else if(allowedcmd.equalsIgnoreCase("auto"))
			{
				p.sendMessage(GREEN + " /plotme auto");
				if(ecoon && pmi != null && pmi.ClaimPrice != 0)
					p.sendMessage(AQUA + " Claim the next available free plot. Price : " + RESET + pmi.ClaimPrice);
				else
					p.sendMessage(AQUA + " Claim the next available free plot.");
			}
			else if(allowedcmd.equalsIgnoreCase("home"))
			{
				p.sendMessage(GREEN + " /plotme home[:#]");
				if(ecoon && pmi != null && pmi.PlotHomePrice != 0)
					p.sendMessage(AQUA + " Teleports you to your own plot, :# if you own multiple plots. Price : " + RESET + pmi.PlotHomePrice);
				else
					p.sendMessage(AQUA + " Teleports you to your own plot, :# if you own multiple plots.");
			}
			else if(allowedcmd.equalsIgnoreCase("home.other"))
			{
				p.sendMessage(GREEN + " /plotme home[:#] <player>");
				if(ecoon && pmi != null && pmi.PlotHomePrice != 0)
					p.sendMessage(AQUA + " Teleports you to other plots, :# if other people own multiple plots. Price : " + RESET + pmi.PlotHomePrice);
				else
					p.sendMessage(AQUA + " Teleports you to other plots, :# if other people own multiple plots.");
			}
			else if(allowedcmd.equalsIgnoreCase("info"))
			{
				p.sendMessage(GREEN + " /plotme info");
				p.sendMessage(AQUA + " Displays information about the plot you're standing on");
			}
			else if(allowedcmd.equalsIgnoreCase("comment"))
			{
				p.sendMessage(GREEN + " /plotme comment <comment>");
				if(ecoon && pmi != null && pmi.AddCommentPrice != 0)
					p.sendMessage(AQUA + " Leave comment on the current plot. Price : " + RESET + pmi.AddCommentPrice);
				else
					p.sendMessage(AQUA + " Leave comment on the current plot.");
			}
			else if(allowedcmd.equalsIgnoreCase("comments"))
			{
				p.sendMessage(GREEN + " /plotme comments");
				p.sendMessage(AQUA + " Lists all comments users have said about your plot.");
			}
			else if(allowedcmd.equalsIgnoreCase("list"))
			{
				p.sendMessage(GREEN + " /plotme list");
				p.sendMessage(AQUA + " Lists every plot you can build on.");
			}
			else if(allowedcmd.equalsIgnoreCase("listother"))
			{
				p.sendMessage(GREEN + " /plotme list <player>");
				p.sendMessage(AQUA + " Lists every plot <player> can build on.");
			}
			else if(allowedcmd.equalsIgnoreCase("biomeinfo"))
			{
				p.sendMessage(GREEN + " /plotme biome");
				p.sendMessage(AQUA + " Shows the current biome in the plot.");
			}
			else if(allowedcmd.equalsIgnoreCase("biome"))
			{
				p.sendMessage(GREEN + " /plotme biome <biome>");
				if(ecoon && pmi != null && pmi.BiomeChangePrice != 0)
					p.sendMessage(AQUA + " Changes the plots biome to the one specified. Price : " + RESET + pmi.BiomeChangePrice);
				else
					p.sendMessage(AQUA + " Changes the plots biome to the one specified.");
			}
			else if(allowedcmd.equalsIgnoreCase("biomelist"))
			{
				p.sendMessage(GREEN + " /plotme biomelist");
				p.sendMessage(AQUA + " List all possible biomes.");
			}
			else if(allowedcmd.equalsIgnoreCase("done"))
			{
				p.sendMessage(GREEN + " /plotme done");
				p.sendMessage(AQUA + " Toggles a plot done or not done.");
			}
			else if(allowedcmd.equalsIgnoreCase("tp"))
			{
				p.sendMessage(GREEN + " /plotme tp <id>");
				p.sendMessage(AQUA + " Teleports to a plot in the current world.");
			}
			else if(allowedcmd.equalsIgnoreCase("id"))
			{
				p.sendMessage(GREEN + " /plotme id");
				p.sendMessage(AQUA + " Gets plot id and coordinates of the current plot your standing on.");
			}
			else if(allowedcmd.equalsIgnoreCase("clear"))
			{
				p.sendMessage(GREEN + " /plotme clear");
				if(ecoon && pmi != null && pmi.ClearPrice != 0)
					p.sendMessage(AQUA + " Clear the plot to its original flat state. Price : " + RESET + pmi.ClearPrice);
				else
					p.sendMessage(AQUA + " Clear the plot to its original flat state.");
			}
			else if(allowedcmd.equalsIgnoreCase("reset"))
			{
				p.sendMessage(GREEN + " /plotme reset");
				p.sendMessage(AQUA + " Reset the plot to its original flat state AND remove its owner");
			}
			else if(allowedcmd.equalsIgnoreCase("add"))
			{
				p.sendMessage(GREEN + " /plotme add <player>");
				if(ecoon && pmi != null && pmi.AddPlayerPrice != 0)
					p.sendMessage(AQUA + " Allows a player to have full access to the plot(This is your responsibility!) Price : " + RESET + pmi.AddPlayerPrice);
				else
					p.sendMessage(AQUA + " Allows a player to have full access to the plot(This is your responsibility!)");
			}
			else if(allowedcmd.equalsIgnoreCase("remove")){
				p.sendMessage(GREEN + " /plotme remove <player>");
				if(ecoon && pmi != null && pmi.RemovePlayerPrice != 0)
					p.sendMessage(AQUA + " Revoke a players access to the plot. Price : " + RESET + pmi.RemovePlayerPrice);
				else
					p.sendMessage(AQUA + " Revoke a players access to the plot.");
			}
			else if(allowedcmd.equalsIgnoreCase("setowner"))
			{
				p.sendMessage(GREEN + " /plotme setowner <player>");
				p.sendMessage(AQUA + " Sets the player provided as the owner of the plot your currently on");
			}
			else if(allowedcmd.equalsIgnoreCase("move"))
			{
				p.sendMessage(GREEN + " /plotme move <id-from> <id-to>");
				p.sendMessage(AQUA + " Swaps the plots blocks(highly experimental for now, use at your own risk)");
			}
			else if(allowedcmd.equalsIgnoreCase("weanywhere"))
			{
				p.sendMessage(GREEN + " /plotme weanywhere");
				p.sendMessage(AQUA + " Toggles using worldedit anywhere");
			}
			else if(allowedcmd.equalsIgnoreCase("expired"))
			{
				p.sendMessage(GREEN + " /plotme expired [page]");
				p.sendMessage(AQUA + " Lists expired plots");
			}
			else if(allowedcmd.equalsIgnoreCase("donelist"))
			{
				p.sendMessage(GREEN + " /plotme donelist [page]");
				p.sendMessage(AQUA + " Lists finished plots");
			}
			else if(allowedcmd.equalsIgnoreCase("addtime"))
			{
				p.sendMessage(GREEN + " /plotme addtime");
				int days = (pmi == null) ? 0 : pmi.DaysToExpiration;
				if(days == 0)
					p.sendMessage(AQUA + " Resets the expiration date to " + RESET + "Never");
				else
					p.sendMessage(AQUA + " Resets the expiration date to " + RESET + days + AQUA + " days from now");
			}
			else if(allowedcmd.equalsIgnoreCase("reload"))
			{
				p.sendMessage(GREEN + " /plotme reload");
				p.sendMessage(AQUA + " Reloads the plugin and its configuration files");
			}
			else if(allowedcmd.equalsIgnoreCase("dispose"))
			{
				p.sendMessage(GREEN + " /plotme dispose");
				p.sendMessage(AQUA + " You will no longer own the plot but it will not get cleared");
			}
			else if(allowedcmd.equalsIgnoreCase("buy"))
			{
				p.sendMessage(GREEN + " /plotme buy");
				p.sendMessage(AQUA + " Buy a plot at the price listed");
			}
			else if(allowedcmd.equalsIgnoreCase("sell"))
			{				
				p.sendMessage(GREEN + " /plotme sell [amount]");
				p.sendMessage(AQUA + " Put your plot for sale. Default : " + RESET + round(pmi.SellToPlayerPrice));
			}
			else if(allowedcmd.equalsIgnoreCase("sellbank"))
			{				
				p.sendMessage(GREEN + " /plotme sell bank");
				p.sendMessage(AQUA + " Sell your plot to the bank for " + RESET + round(pmi.SellToBankPrice));
			}
		}
		
		return true;
	}
	
	private boolean tp(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.tp"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				if(args.length == 2)
				{
					String id = args[1];
					
					Location bottom = PlotManager.getPlotBottomLoc(p.getWorld(), id);
					Location top = PlotManager.getPlotTopLoc(p.getWorld(), id);
					
					p.teleport(new Location(p.getWorld(), bottom.getX() + (top.getBlockX() - bottom.getBlockX())/2, PlotManager.getMap(p).RoadHeight + 2, bottom.getZ() - 2));
				}
				else
				{
					p.sendMessage(PREFIX + RESET + " Usage: " + RED + "/plotme tp <id> " + RESET + "Example: " + RED + "/plotme tp 5;-1 ");
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}

	private boolean auto(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.auto"))
		{			
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{				
				if(PlotManager.getNbOwnedPlot(p) >= PlotMe.getPlotLimit(p) && !PlotMe.cPerms(p, "PlotMe.admin"))
					p.sendMessage(PREFIX + RED + " You have already reached your maximum amount of plots (" + 
							PlotManager.getNbOwnedPlot(p) + "/" + PlotMe.getPlotLimit(p) + "). Use " + RED + "/plotme home" + RESET + " to get to them.");
				else
				{
					PlotMapInfo pmi = PlotManager.getMap(p);
					int limit = pmi.PlotAutoLimit;
					
					for(int i = 0; i < limit; i++)
					{
						for(int x = -i; x <= i; x++)
						{
							for(int z = -i; z <= i; z++)
							{
								String id = "" + x + ";" + z;
								
								if(PlotManager.isPlotAvailable(id, p))
								{
									World w = p.getWorld();
									
									String name = p.getName();
									
									if(PlotManager.isEconomyEnabled(w))
									{
										double price = pmi.ClaimPrice;
										double balance = PlotMe.economy.getBalance(name);
										
										if(balance > price)
										{
											EconomyResponse er = PlotMe.economy.withdrawPlayer(name, price);
											
											if(!er.transactionSuccess())
											{
												p.sendMessage(PREFIX + RED + " " + er.errorMessage);
												return true;
											}
										}
										else
										{
											p.sendMessage(PREFIX + RED + " You do not have enough to buy a plot. Missing " + RESET + (price - balance) + RED + " " + PlotMe.economy.currencyNamePlural());
											return true;
										}
									}
									
									Plot plot = PlotManager.createPlot(w, id, name);
									
									p.teleport(new Location(p.getWorld(), PlotManager.bottomX(plot.id, w) + (PlotManager.topX(plot.id, w) - 
											PlotManager.bottomX(plot.id, w))/2, pmi.RoadHeight + 2, PlotManager.bottomZ(plot.id, w) - 2));
		
									p.sendMessage(PREFIX + RESET + " This plot is now yours. Use " + RED + "/plotme home" + RESET + " to get back to them.");
									return true;
								}
							}
						}
					}
				
					p.sendMessage(PREFIX + RED + " No plot found within " + (limit^2) + " plots. Contact an admin.");
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}

	private boolean claim(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.claim") || PlotMe.cPerms(p, "PlotMe.admin.claim.other"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{		
				String id = PlotManager.getPlotId(p.getLocation());
				
				if(id.equals(""))
				{
					p.sendMessage(PREFIX + RED + " You cannot claim the road");
				}
				else if(!PlotManager.isPlotAvailable(id, p))
				{
					p.sendMessage(PREFIX + RED + " This plot is already owned");
				}
				else
				{
					String playername = p.getName();
					
					if(args.length == 2)
					{
						if(PlotMe.cPerms(p, "PlotMe.admin.claim.other"))
						{
							playername = args[1];
						}
					}
					
					int plotlimit = PlotMe.getPlotLimit(p);
					
					if(playername == p.getName() && plotlimit != -1 && PlotManager.getNbOwnedPlot(p) >= plotlimit)
					{
						p.sendMessage(PREFIX + RESET + " You have already reached your maximum amount of plots (" + 
								PlotManager.getNbOwnedPlot(p) + "/" + PlotMe.getPlotLimit(p) + "). Use " + RED + "/plotme home" + RESET + " to get to it");
					}
					else
					{
						
						World w = p.getWorld();
						PlotMapInfo pmi = PlotManager.getMap(w);
												
						if(PlotManager.isEconomyEnabled(w))
						{
							double price = pmi.ClaimPrice;
							double balance = PlotMe.economy.getBalance(playername);
							
							if(balance > price)
							{
								EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);
								
								if(!er.transactionSuccess())
								{
									p.sendMessage(PREFIX + RED + " " + er.errorMessage);
									return true;
								}
							}
							else
							{
								p.sendMessage(PREFIX + RED + " You do not have enough to buy a plot. Missing " + RESET + (price - balance) + RED + " " + PlotMe.economy.currencyNamePlural());
								return true;
							}
						}
						
						Plot plot = PlotManager.createPlot(w, id, playername);
		
						if(plot == null)
							p.sendMessage(PREFIX + RED + " An error occured while creating the plot at " + id);
						else
							p.sendMessage(PREFIX + RESET + " This plot is now yours. Use " + RED + "/plotme home" + RESET + " to get back to it.");
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean home(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.home") || PlotMe.cPerms(p, "PlotMe.admin.home.other"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				boolean found = false;
				String playername = p.getName();
				int nb = 1;
				
				if(args[0].contains(":"))
				{
					try{
						nb = Integer.parseInt(args[0].split(":")[1]);
					}catch(NumberFormatException ex)
					{
						p.sendMessage(PREFIX + RESET + " Format is: " + RED + "/plot home:# " + 
								RESET + "As in : " + RED + "/plot home:1");
						return true;
					}
				}
				
				if(args.length == 2)
				{					
					if(PlotMe.cPerms(p, "PlotMe.admin.home.other"))
					{
						playername = args[1];
					}
				}
				
				int i = nb - 1;
						
				for(Plot plot : PlotManager.getPlots(p).values())
				{
					if(plot.owner.equalsIgnoreCase(playername))
					{
						if(i == 0)
						{
							World w = p.getWorld();
							
							PlotMapInfo pmi = PlotManager.getMap(w);
													
							if(PlotManager.isEconomyEnabled(w))
							{
								double price = pmi.PlotHomePrice;
								double balance = PlotMe.economy.getBalance(playername);
								
								if(balance > price)
								{
									EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);
									
									if(!er.transactionSuccess())
									{
										p.sendMessage(PREFIX + RED + " " + er.errorMessage);
										return true;
									}
								}
								else
								{
									p.sendMessage(PREFIX + RED + " You do not have enough to teleport home. Missing " + RESET + (price - balance) + RED + " " + PlotMe.economy.currencyNamePlural());
									return true;
								}
							}
							
							p.teleport(new Location(w, PlotManager.bottomX(plot.id, w) + (PlotManager.topX(plot.id, w) - 
									PlotManager.bottomX(plot.id, w))/2, pmi.RoadHeight + 2, PlotManager.bottomZ(plot.id, w) - 2));
							return true;
						}else{
							i--;
						}
					}
				}
				
				if(!found)
				{
					if(nb > 0)
					{
						if(!playername.equalsIgnoreCase(p.getName()))
						{
							p.sendMessage(PREFIX + RED + " " + playername + " does not have a plot #" + nb);
						}else{
							p.sendMessage(PREFIX + RED + " Could not find plot #" + nb);
						}
					}
					else if(!playername.equalsIgnoreCase(p.getName()))
					{
						p.sendMessage(PREFIX + RED + " " + playername + " does not have a plot");
					}
					else
					{
						p.sendMessage(PREFIX + RED + " You don't have a plot");
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean info(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.info"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				String id = PlotManager.getPlotId(p.getLocation());
				
				if(id.equals(""))
				{
					p.sendMessage(PREFIX + RED + " No plot found");
				}
				else
				{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						Plot plot = PlotManager.getPlotById(p,id);
						
						p.sendMessage(GREEN +"ID: " + AQUA + id + 
								GREEN + " Owner: " + AQUA + plot.owner + 
								GREEN + " Biome: " + AQUA + plot.biome);
						
						p.sendMessage(GREEN +"Expire date: " + AQUA + ((plot.expireddate == null) ? "Never" : plot.expireddate.toString()) +
								GREEN + " Finished: " + AQUA + ((plot.finished) ? "Yes" : "No") +
								GREEN + " Protected: " + AQUA + ((plot.protect) ? "Yes" : "No"));
						
						if(plot.allowedcount() > 0)
						{
							p.sendMessage(GREEN +"Helpers: " + AQUA + plot.getAllowed());
						}
						
						if(PlotManager.isEconomyEnabled(p))
						{
							if(plot.auctionned)
							{
								if(plot.currentbidder.equalsIgnoreCase(""))
								{
									p.sendMessage(GREEN +"Auctionned: " + AQUA + "Yes" + GREEN + " Minimum bid: " + AQUA + round(plot.currentbid));
								}
								else
								{
									p.sendMessage(GREEN + "Auctionned: " + AQUA + "Yes" + 
											GREEN + " Bidder: " + AQUA + plot.currentbidder + 
											GREEN + " Bid: " + AQUA + round(plot.currentbid));
								}
							}
							else
							{								
								p.sendMessage(GREEN +"For sale: " + AQUA + ((plot.forsale) ? "Yes" + GREEN + " Price: " + 
										AQUA + round(plot.customprice) : "No"));
							}
						}
					}
					else
					{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean comment(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.comment"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				if(args.length < 2)
				{
					p.sendMessage(PREFIX + RESET + " Usage: " + RED + "/plotme comment <text>");
				}
				else
				{
					String id = PlotManager.getPlotId(p.getLocation());
					
					if(id.equals(""))
					{
						p.sendMessage(PREFIX + RED + " No plot found");
					}
					else
					{
						if(!PlotManager.isPlotAvailable(id, p))
						{
							World w = p.getWorld();
							PlotMapInfo pmi = PlotManager.getMap(w);
							String playername = p.getName();
													
							if(PlotManager.isEconomyEnabled(w))
							{
								double price = pmi.AddCommentPrice;
								double balance = PlotMe.economy.getBalance(playername);
								
								if(balance > price)
								{
									EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);
									
									if(!er.transactionSuccess())
									{
										p.sendMessage(PREFIX + RED + " " + er.errorMessage);
										return true;
									}
								}
								else
								{
									p.sendMessage(PREFIX + RED + " You do not have enough to comment on a plot. Missing " + RESET + (price - balance) + RED + " " + PlotMe.economy.currencyNamePlural());
									return true;
								}
							}
							
							Plot plot = PlotManager.getPlotById(p, id);
							
							String text = StringUtils.join(args," ");
							text = text.substring(text.indexOf(" "));
							
							String[] comment = new String[2];
							comment[0] = playername;
							comment[1] = text;
							
							plot.comments.add(comment);
							SqlManager.addPlotComment(comment, plot.comments.size(), PlotManager.getIdX(id), PlotManager.getIdZ(id), plot.world);
							
							p.sendMessage(PREFIX + RESET + " Comment added");
						}
						else
						{
							p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
						}
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean comments(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.comments"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				if(args.length < 2)
				{
					String id = PlotManager.getPlotId(p.getLocation());
					
					if(id.equals(""))
					{
						p.sendMessage(PREFIX + RED + " No plot found");
					}
					else
					{
						if(!PlotManager.isPlotAvailable(id, p))
						{
							Plot plot = PlotManager.getPlotById(p,id);
							
							if(plot.owner.equalsIgnoreCase(p.getName()) || plot.isAllowed(p.getName()) || PlotMe.cPerms(p, "PlotMe.admin"))
							{
								if(plot.comments.size() == 0)
								{
									p.sendMessage(PREFIX + RESET + " No comments");
								}
								else
								{
									p.sendMessage(PREFIX + RESET + " You have " + BLUE + plot.comments.size() + RESET + " comments.");
									
									for(String[] comment : plot.comments)
									{
										p.sendMessage(ChatColor.BLUE + "From : " + RED + comment[0]);
										p.sendMessage("" + RESET + ChatColor.ITALIC + comment[1]);
									}
									
								}
							}
							else
							{
								p.sendMessage(PREFIX + RED + " This plot(" + id + ") is not yours. You are not allowed to view the comments.");
							}
						}
						else
						{
							p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
						}
					}
				}
			}
		}
		else
		{
			p.sendMessage(BLUE + PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean biome(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.biome"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				String id = PlotManager.getPlotId(p.getLocation());
				if(id.equals(""))
				{
					p.sendMessage(BLUE + PREFIX + RED + " No plot found");
				}
				else
				{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						World w = p.getWorld();
						
						if(args.length == 2)
						{
							Biome biome = null;
							
							for(Biome bio : Biome.values())
							{
								if(bio.name().equalsIgnoreCase(args[1]))
								{
									biome = bio;
								}
							}
							
							if(biome == null)
							{
								p.sendMessage(PREFIX + RED + " " + args[1] + RESET + " is not a valid biome.");
							}
							else
							{
								Plot plot = PlotManager.getPlotById(p,id);
								String playername = p.getName();
								
								if(plot.owner.equalsIgnoreCase(playername) || PlotMe.cPerms(p, "PlotMe.admin"))
								{
									PlotMapInfo pmi = PlotManager.getMap(w);
															
									if(PlotManager.isEconomyEnabled(w))
									{
										double price = pmi.BiomeChangePrice;
										double balance = PlotMe.economy.getBalance(playername);
										
										if(balance > price)
										{
											EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);
											
											if(!er.transactionSuccess())
											{
												p.sendMessage(PREFIX + RED + " " + er.errorMessage);
												return true;
											}
										}
										else
										{
											p.sendMessage(PREFIX + RED + " You do not have enough to change the biome. Missing " + RESET + (price - balance) + RED + " " + PlotMe.economy.currencyNamePlural());
											return true;
										}
									}
									
									PlotManager.setBiome(w, id, plot, biome);
								
									p.sendMessage(PREFIX + RESET + " Biome set to " + ChatColor.BLUE + biome.name());
								}
								else
								{
									p.sendMessage(PREFIX + RED + " This plot(" + id + ") is not yours. You are not allowed to change it's biome.");
								}
							}
						}
						else
						{
							Plot plot = PlotMe.plotmaps.get(w.getName().toLowerCase()).plots.get(id);
							
							p.sendMessage(PREFIX + RESET + " This plot is using the biome " + ChatColor.BLUE + plot.biome.name());
						}
					}
					else
					{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean biomelist(CommandSender s, String[] args)
	{
		if (!(s instanceof Player) || PlotMe.cPerms((Player) s, "PlotMe.use.biome"))
		{
			s.sendMessage(PREFIX + RESET + " Biomes : ");
					
			int i = 0;
			StringBuilder line = new StringBuilder();
			List<String> biomes = new ArrayList<String>();
			
			for(Biome b : Biome.values())
			{
				biomes.add(b.name());
			}
			
			Collections.sort(biomes);
				
			for(String b : biomes)
			{
				int nameLength = MinecraftFontWidthCalculator.getStringWidth(b);
				
				i += 1;
				if(i == 3)
				{
					line.append(b);
					s.sendMessage("" + BLUE + line);
					i = 0;
					line = new StringBuilder();
				}
				else
				{
					line.append(b).append(whitespace(363 - nameLength));
				}
			}
		}
		else
		{
			s.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean reset(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.reset"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				Plot plot = PlotManager.getPlotById(p.getLocation());
				
				if(plot == null)
				{
					p.sendMessage(PREFIX + RED + " No plot found");
				}
				else
				{
					if(plot.protect)
					{
						p.sendMessage(PREFIX + RED + " Plot is protected and cannot be reset.");
					}
					else
					{
						String id = plot.id;
						
						Location bottom = PlotManager.getPlotBottomLoc(p.getWorld(), id);
						Location top = PlotManager.getPlotTopLoc(p.getWorld(), id);
						
						PlotManager.clear(bottom, top);
						
						if(plot.auctionned)
						{
							String currentbidder = plot.currentbidder;
							
							if(!currentbidder.equals(""))
							{
								EconomyResponse er = PlotMe.economy.depositPlayer(currentbidder, plot.currentbid);
								
								if(!er.transactionSuccess())
								{
									p.sendMessage(PREFIX + RESET + " " + er.errorMessage);
								}
							}
						}
						
						PlotMapInfo pmi = PlotManager.getMap(p);
						
						if(pmi.RefundClaimPriceOnReset)
						{
							EconomyResponse er = PlotMe.economy.depositPlayer(plot.owner, pmi.ClaimPrice);
							
							if(!er.transactionSuccess())
							{
								p.sendMessage(PREFIX + RED + " " + er.errorMessage);
								return true;
							}
						}
						
						if(!PlotManager.isPlotAvailable(id, p))
						{
							PlotManager.getPlots(p).remove(id);
						}
						
						World w = p.getWorld();
						
						PlotManager.removeOwnerSign(w, id);
						PlotManager.removeSellSign(w, id);
						
						SqlManager.deletePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), p.getWorld().getName().toLowerCase());
						
						p.sendMessage(PREFIX + RESET + " Plot has been reset.");
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean clear(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.clear") || PlotMe.cPerms(p, "PlotMe.use.clear"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				String id = PlotManager.getPlotId(p.getLocation());
				if(id.equals(""))
				{
					p.sendMessage(PREFIX + RED + " No plot found");
				}
				else
				{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						Plot plot = PlotManager.getPlotById(p,id);
						
						if(plot.protect)
						{
							p.sendMessage(PREFIX + RED + " Plot is protected and cannot be cleared.");
						}
						else
						{
							String playername = p.getName();
							
							if(plot.owner.equalsIgnoreCase(playername) || PlotMe.cPerms(p, "PlotMe.admin.clear"))
							{
								World w = p.getWorld();
								
								PlotMapInfo pmi = PlotManager.getMap(w);
								
								if(PlotManager.isEconomyEnabled(w))
								{
									double price = pmi.ClearPrice;
									double balance = PlotMe.economy.getBalance(playername);
									
									if(balance > price)
									{
										EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);
										
										if(!er.transactionSuccess())
										{
											p.sendMessage(PREFIX + RED + " " + er.errorMessage);
											return true;
										}
									}
									else
									{
										p.sendMessage(PREFIX + RED + " You do not have enough to clear the plot. Missing " + RESET + (price - balance) + RED + " " + PlotMe.economy.currencyNamePlural());
										return true;
									}
								}						
								
								PlotManager.clear(w, plot);
								
								p.sendMessage(PREFIX + RESET + " Plot cleared");
							}
							else
							{
								p.sendMessage(PREFIX + RED + " This plot(" + id + ") is not yours. You are not allowed to clear it.");
							}
						}
					}
					else
					{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean add(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.add") || PlotMe.cPerms(p, "PlotMe.use.add"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				String id = PlotManager.getPlotId(p.getLocation());
				if(id.equals(""))
				{
					p.sendMessage(PREFIX + RED + " No plot found");
				}
				else
				{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						if(args.length < 2 || args[1].equalsIgnoreCase(""))
						{
							p.sendMessage(PREFIX + RESET + " Usage " + RED + "/plotme allow <player>");
						}
						else
						{
						
							Plot plot = PlotManager.getPlotById(p,id);
							String playername = p.getName();
							
							if(plot.owner.equalsIgnoreCase(playername) || PlotMe.cPerms(p, "PlotMe.admin.add"))
							{
								if(plot.isAllowed(args[1]))
								{
									p.sendMessage(PREFIX + RESET + " Player " + RED + args[1] + RESET + " was already allowed");
								}
								else
								{
									
									World w = p.getWorld();
									
									PlotMapInfo pmi = PlotManager.getMap(w);
									
									if(PlotManager.isEconomyEnabled(w))
									{
										double price = pmi.AddPlayerPrice;
										double balance = PlotMe.economy.getBalance(playername);
										
										if(balance > price)
										{
											EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);
											
											if(!er.transactionSuccess())
											{
												p.sendMessage(PREFIX + RED + " " + er.errorMessage);
												return true;
											}
										}
										else
										{
											p.sendMessage(PREFIX + RED + " You do not have enough to add a player. Missing " + RESET + (price - balance) + RED + " " + PlotMe.economy.currencyNamePlural());
											return true;
										}
									}
									
									plot.addAllowed(args[1]);
									
									p.sendMessage(PREFIX + RESET + " Player " + RED + args[1] + RESET + " now allowed");
								}
							}
							else
							{
								p.sendMessage(PREFIX + RED + " This plot(" + id + ") is not yours. You are not allowed to add someone to it.");
							}
						}
					}
					else
					{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean remove(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.remove") || PlotMe.cPerms(p, "PlotMe.use.remove"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				String id = PlotManager.getPlotId(p.getLocation());
				if(id.equals(""))
				{
					p.sendMessage(PREFIX + RED + " No plot found");
				}
				else
				{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						if(args.length < 2 || args[1].equalsIgnoreCase(""))
						{
							p.sendMessage(PREFIX + RESET + " Usage " + RED + "/plotme remove <player>");
						}
						else
						{
							Plot plot = PlotManager.getPlotById(p,id);
							String playername = p.getName();
							
							if(plot.owner.equalsIgnoreCase(playername) || PlotMe.cPerms(p, "PlotMe.admin.remove"))
							{
								if(plot.isAllowed(args[1]))
								{
									
									World w = p.getWorld();
									
									PlotMapInfo pmi = PlotManager.getMap(w);
									
									if(PlotManager.isEconomyEnabled(w))
									{
										double price = pmi.AddPlayerPrice;
										double balance = PlotMe.economy.getBalance(playername);
										
										if(balance > price)
										{
											EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);
											
											if(!er.transactionSuccess())
											{
												p.sendMessage(PREFIX + RED + " " + er.errorMessage);
												return true;
											}
										}
										else
										{
											p.sendMessage(PREFIX + RED + " You do not have enough to remove a player. Missing " + RESET + (price - balance) + RED + " " + PlotMe.economy.currencyNamePlural());
											return true;
										}
									}
									
									plot.removeAllowed(args[1]);
																	
									p.sendMessage(PREFIX + RESET + "Player " + RED + args[1] + RESET + " removed");
								}
								else
								{
									p.sendMessage(PREFIX + RESET + "Player " + RED + args[1] + RESET + " wasn't allowed");
								}
							}
							else
							{
								p.sendMessage(PREFIX + RED + " This plot(" + id + ") is not yours. You are not allowed to remove someone from it.");
							}
						}
					}
					else
					{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean setowner(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.setowner"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				String id = PlotManager.getPlotId(p.getLocation());
				if(id.equals(""))
				{
					p.sendMessage(PREFIX + RED + " No plot found");
				}
				else
				{
					if(args.length < 2 || args[1].equals(""))
					{
						p.sendMessage(PREFIX + RESET + " Usage " + RED + "/plotme owner <player>");
					}
					else
					{
						String newowner = args[1];
						
						if(!PlotManager.isPlotAvailable(id, p))
						{								
								Plot plot = PlotManager.getPlotById(p,id);
								
								PlotMapInfo pmi = PlotManager.getMap(p);
								String owner = plot.owner;
								
								if(pmi.RefundClaimPriceOnSetOwner && newowner != owner)
								{
									EconomyResponse er = PlotMe.economy.depositPlayer(owner, pmi.ClaimPrice);
									
									if(!er.transactionSuccess())
									{
										p.sendMessage(PREFIX + RED + " " + er.errorMessage);
										return true;
									}
								}
								
								if(plot.currentbidder.equals(newowner))
								{
									EconomyResponse er = PlotMe.economy.depositPlayer(newowner, plot.currentbid);
									
									if(!er.transactionSuccess())
									{
										p.sendMessage(PREFIX + RESET + " " + er.errorMessage);
									}
									
									plot.currentbidder = "";
									plot.currentbid = 0;
									plot.auctionned = false;
									
									PlotManager.setSellSign(p.getWorld(), plot);
									
									plot.updateField("currentbidder", "");
									plot.updateField("currentbid", 0);
									plot.updateField("auctionned", false);
								}
						
								plot.owner = newowner;
								
								PlotManager.setOwnerSign(p.getWorld(), plot);
								
								plot.updateField("owner", newowner);
							
						}
						else
						{
							PlotManager.createPlot(p.getWorld(), id,newowner);
						}
						
						p.sendMessage(PREFIX + RESET + " Plot Owner has been set to " + RED + newowner);
					}
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean id(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.id"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				String plotid = PlotManager.getPlotId(p.getLocation());
				
				if(plotid.equals(""))
				{
					p.sendMessage(PREFIX + RED + " No plot found");
				}
				else
				{
					p.sendMessage(BLUE + "Plot Id: " + RESET + plotid);
					
					Location bottom = PlotManager.getPlotBottomLoc(p.getWorld(), plotid);
					Location top = PlotManager.getPlotTopLoc(p.getWorld(), plotid);
					
					p.sendMessage(BLUE + "Bottom: " + RESET + bottom.getBlockX() + ChatColor.BLUE + "," + RESET + bottom.getBlockZ());
					p.sendMessage(BLUE + "Top: " + RESET + top.getBlockX() + ChatColor.BLUE + "," + RESET + top.getBlockZ());
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean move(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.move"))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{
				if(args.length < 3 || args[1].equalsIgnoreCase("") || args[2].equalsIgnoreCase(""))
				{
					p.sendMessage(PREFIX + RESET + " Usage " + RED + "/plotme move <idFrom> <idTo>");
				}
				else
				{
					if(PlotManager.movePlot(p.getWorld(), args[1], args[2]))
						p.sendMessage(PREFIX + RESET + " Plot moved successfully");
					else
						p.sendMessage(PREFIX + RED + " Error moving plot");
				}
			}
		}
		else
		{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean reload(CommandSender s, String[] args)
	{
		if (!(s instanceof Player) || PlotMe.cPerms((Player) s, "PlotMe.admin.reload"))
		{
			plugin.initialize();
			s.sendMessage(PREFIX + RESET + " reloaded successfully");
		}
		else
		{
			s.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private StringBuilder whitespace(int length) {
		int spaceWidth = MinecraftFontWidthCalculator.getStringWidth(" ");
		
		StringBuilder ret = new StringBuilder();
		
		for(int i = 0; i < length; i+=spaceWidth) {
			ret.append(" ");
		}
		
		return ret;
	}
	
	private String round(double money)
	{
		return (money % 1 == 0) ? "" + Math.round(money) : "" + money;
	}
}
