package com.worldcretornica.plotme;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

public class PMCommand implements CommandExecutor {

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
	
	public boolean onCommand(CommandSender s, Command c, String l, String[] args){
		if(l.equalsIgnoreCase("plotme") || l.equalsIgnoreCase("plot") || l.equalsIgnoreCase("p")){
			if(!(s instanceof Player)){
				if(args.length == 0 || args[0].equalsIgnoreCase("1")){
					s.sendMessage(" ---==PlotMe v" + PlotMe.VERSION + " Console Help Page==--- "); 
					s.sendMessage(" - /plotme reload");
					s.sendMessage(" - Reloads the plugin and its configuration files");
					return true;
				}else{
					String a0 = args[0].toString();
					if(!(s instanceof Player)){
						if (a0.equalsIgnoreCase("reload")) { return reload(s, args);}
					}
				}
			}else{
				
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
					}else{
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
					}
				}
			}
		}
		return false;
	}
	
	
	private boolean addtime(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.addtime", false))
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
				}else{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						Plot plot = PlotManager.getPlotById(p,id);
						
						if(plot != null)
						{
							plot.resetExpire(PlotManager.getMap(p).DaysToExpiration);
							p.sendMessage(PREFIX + " Plot expiration reset");
						}
					}else{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}
		return true;
	}

	private boolean expired(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.expired", false))
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
				}else{
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
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}

	private boolean plotlist(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.list", true))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
				return true;
			}else{
				
				String name;
				
				if(PlotMe.cPerms(p, "PlotMe.admin.list", false) && args.length == 2)
				{
					name = args[1];
					p.sendMessage(PREFIX + RESET + " List of plots where " + BLUE + name + RESET + " can build:");
				}else{
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
							String helpers = "";
							for(int i = 0 ; i < plot.allowedcount(); i++)
							{
								helpers += "" + BLUE + plot.allowed().toArray()[i] + RESET + ", ";
							}
							if(helpers.length() > 2)
								helpers = helpers.substring(0, helpers.length() - 2);
							
							if(name.equalsIgnoreCase(p.getName()))
								p.sendMessage("  " + plot.id + " -> " + BLUE + ITALIC + "Yours" + RESET + expiration + ", Helpers: " + helpers);
							else
								p.sendMessage("  " + plot.id + " -> " + BLUE + ITALIC + plot.owner + RESET + expiration + ", Helpers: " + helpers);
						}
					}else if(plot.isAllowed(name))
					{
						String helpers = "";
						for(int i = 0 ; i < plot.allowedcount(); i++)
						{
							if(p.getName().equalsIgnoreCase((String) plot.allowed().toArray()[i]))
								if(name.equalsIgnoreCase(p.getName()))
									helpers += "" + BLUE + ITALIC + "You" + RESET + ", ";
								else
									helpers += "" + BLUE + ITALIC + name + RESET + ", ";
							else
								helpers += "" + BLUE + plot.allowed().toArray()[i] + RESET + ", ";
						}
						if(helpers.length() > 2)
							helpers = helpers.substring(0, helpers.length() - 2);
						
						if(plot.owner.equalsIgnoreCase(p.getName()))
							p.sendMessage("  " + plot.id + " -> " + BLUE + "Yours" + RESET + expiration + ", Helpers: " + helpers);
						else
							p.sendMessage("  " + plot.id + " -> " + BLUE + plot.owner + "'s" + RESET + expiration + ", Helpers: " + helpers);
					}
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}

	private boolean weanywhere(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.weanywhere", false))
		{
			if(PlotMe.isIgnoringWELimit(p))
			{
				PlotMe.removeIgnoreWELimit(p);
				p.sendMessage(PREFIX + RESET + " You can now only WorldEdit in your plots");
			}else{
				PlotMe.addIgnoreWELimit(p);
				p.sendMessage(PREFIX + RESET + " You can now WorldEdit anywhere");
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	
	private boolean showhelp(Player p, int page)
	{
		int max = 4;
		int maxpage = 0;
		
		List<String> allowed_commands = new ArrayList<String>();
		
		allowed_commands.add("limit");
		if(PlotMe.cPerms(p, "PlotMe.use.claim", true)) allowed_commands.add("claim");
		if(PlotMe.cPerms(p, "PlotMe.use.claim.other", false)) allowed_commands.add("claim.other");
		if(PlotMe.cPerms(p, "PlotMe.use.auto", true)) allowed_commands.add("auto");
		if(PlotMe.cPerms(p, "PlotMe.use.home", true)) allowed_commands.add("home");
		if(PlotMe.cPerms(p, "PlotMe.use.home.other", false)) allowed_commands.add("home.other");
		if(PlotMe.cPerms(p, "PlotMe.use.info", true))
		{
			allowed_commands.add("info");
			allowed_commands.add("biomeinfo");
		}
		if(PlotMe.cPerms(p, "PlotMe.use.comment", true)) allowed_commands.add("comment");
		if(PlotMe.cPerms(p, "PlotMe.use.comments", true)) allowed_commands.add("comments");
		if(PlotMe.cPerms(p, "PlotMe.use.list", true)) allowed_commands.add("list");
		if(PlotMe.cPerms(p, "PlotMe.use.biome", true))
		{
			allowed_commands.add("biome");
			allowed_commands.add("biomelist");
		}
		if(PlotMe.cPerms(p, "PlotMe.admin.tp", false)) allowed_commands.add("tp");
		if(PlotMe.cPerms(p, "PlotMe.admin.id", false)) allowed_commands.add("id");
		if(PlotMe.cPerms(p, "PlotMe.use.clear", true) || PlotMe.cPerms(p, "PlotMe.admin.clear", true)) allowed_commands.add("clear");
		if(PlotMe.cPerms(p, "PlotMe.admin.reset", false)) allowed_commands.add("reset");
		if(PlotMe.cPerms(p, "PlotMe.use.add", false) || PlotMe.cPerms(p, "PlotMe.admin.add", true)) allowed_commands.add("add");
		if(PlotMe.cPerms(p, "PlotMe.use.remove", false) || PlotMe.cPerms(p, "PlotMe.admin.remove", true)) allowed_commands.add("remove");
		if(PlotMe.cPerms(p, "PlotMe.admin.setowner", false)) allowed_commands.add("setowner");
		if(PlotMe.cPerms(p, "PlotMe.admin.move", false)) allowed_commands.add("move");
		if(PlotMe.cPerms(p, "PlotMe.admin.weanywhere", false)) allowed_commands.add("weanywhere");
		if(PlotMe.cPerms(p, "PlotMe.admin.reload", false)) allowed_commands.add("reload");
		if(PlotMe.cPerms(p, "PlotMe.admin.list", false)) allowed_commands.add("listother");
		if(PlotMe.cPerms(p, "PlotMe.admin.expired", false)) allowed_commands.add("expired");
		if(PlotMe.cPerms(p, "PlotMe.admin.addtime", false)) allowed_commands.add("addtime");
		
		maxpage = (int) Math.ceil((double) allowed_commands.size() / max);
		
		if (page > maxpage)
			page = 1;
		
		p.sendMessage(RED + " ---==" + BLUE + "PlotMe Help Page " + page + "/" + maxpage + RED + "==--- ");
		
		for(int ctr = (page - 1) * max; ctr < (page * max) && ctr < allowed_commands.size(); ctr++)
		{
			if(allowed_commands.get(ctr).equalsIgnoreCase("limit")){
				if(PlotManager.isPlotWorld(p))
				{
				
					int maxplots = PlotMe.getPlotLimit(p);
					int ownedplots = PlotManager.getNbOwnedPlot(p);
					
					if(maxplots == -1)
						p.sendMessage(GREEN + "Your plot limit in this world : " + AQUA + ownedplots + GREEN + " used of " + AQUA + "Infinite");
					else
						p.sendMessage(GREEN + "Your plot limit in this world : " + AQUA + ownedplots + GREEN + " used of " + AQUA + maxplots);
				}else{
					p.sendMessage(GREEN + "Your plot limit in this world : " + AQUA + "Not a plot world");
				}
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("claim")){
				p.sendMessage(GREEN + " /plotme claim");
				p.sendMessage(AQUA + " Claim the current plot you are standing on");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("claim.other")){
				p.sendMessage(GREEN + " /plotme claim <player>");
				p.sendMessage(AQUA + " Claim the current plot you are standing on for another player");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("auto")){
				p.sendMessage(GREEN + " /plotme auto");
				p.sendMessage(AQUA + " Claim the next available free plot");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("home")){
				p.sendMessage(GREEN + " /plotme home[:#]");
				p.sendMessage(AQUA + " Teleports you to your own plot, :# if you own multiple plots");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("home.other")){
				p.sendMessage(GREEN + " /plotme home[:#] <player>");
				p.sendMessage(AQUA + " Teleports you to other plots, :# if other people own multiple plots");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("info")){
				p.sendMessage(GREEN + " /plotme info");
				p.sendMessage(AQUA + " Displays information about the plot you're standing on");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("comment")){
				p.sendMessage(GREEN + " /plotme comment <comment>");
				p.sendMessage(AQUA + " Leave comment on the current plot");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("comments")){
				p.sendMessage(GREEN + " /plotme comments");
				p.sendMessage(AQUA + " Lists all comments users have said about your plot");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("list")){
				p.sendMessage(GREEN + " /plotme list");
				p.sendMessage(AQUA + " Lists every plot you can build on");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("listother")){
				p.sendMessage(GREEN + " /plotme list <player>");
				p.sendMessage(AQUA + " Lists every plot <player> can build on");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("biomeinfo")){
				p.sendMessage(GREEN + " /plotme biome");
				p.sendMessage(AQUA + " Shows the current biome in the plot");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("biome")){
				p.sendMessage(GREEN + " /plotme biome <biome>");
				p.sendMessage(AQUA + " Changes the plots biome to the one specified");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("biomelist")){
				p.sendMessage(GREEN + " /plotme biomelist");
				p.sendMessage(AQUA + " List all possible biomes");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("tp")){
				p.sendMessage(GREEN + " /plotme tp <id>");
				p.sendMessage(AQUA + " Teleports to a plot in the current world");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("id")){
				p.sendMessage(GREEN + " /plotme id");
				p.sendMessage(AQUA + " Gets plot id and coordinates of the current plot your standing on");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("clear")){
				p.sendMessage(GREEN + " /plotme clear");
				p.sendMessage(AQUA + " Clear the plot to its original flat state");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("reset")){
				p.sendMessage(GREEN + " /plotme reset");
				p.sendMessage(AQUA + " Reset the plot to its original flat state AND remove its owner");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("add")){
				p.sendMessage(GREEN + " /plotme add <player>");
				p.sendMessage(AQUA + " Allows a player to have full access to the plot(This is your responsibility!)");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("remove")){
				p.sendMessage(GREEN + " /plotme remove <player>");
				p.sendMessage(AQUA + " Revoke a players access to the plot");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("setowner")){
				p.sendMessage(GREEN + " /plotme setowner <player>");
				p.sendMessage(AQUA + " Sets the player provided as the owner of the plot your currently on");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("move")){
				p.sendMessage(GREEN + " /plotme move <id-from> <id-to>");
				p.sendMessage(AQUA + " Swaps the plots blocks(highly experimental for now, use at your own risk)");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("weanywhere")){
				p.sendMessage(GREEN + " /plotme weanywhere");
				p.sendMessage(AQUA + " Toggles using worldedit anywhere");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("expired")){
				p.sendMessage(GREEN + " /plotme expired [page]");
				p.sendMessage(AQUA + " Lists expired plots");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("addtime")){
				p.sendMessage(GREEN + " /plotme addtime");
				int days = PlotManager.getMap(p).DaysToExpiration;
				if(days == 0)
					p.sendMessage(AQUA + " Resets the expiration date to " + RESET + "Never");
				else
					p.sendMessage(AQUA + " Resets the expiration date to " + RESET + days + AQUA + " days from now");
			}else if(allowed_commands.get(ctr).equalsIgnoreCase("reload")){
				p.sendMessage(GREEN + " /plotme reload");
				p.sendMessage(AQUA + " Reloads the plugin and its configuration files");
			}
		}
		
		return true;
	}
	
	private boolean tp(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.tp", false))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}else{
				if(args.length == 2)
				{
					String id = args[1];
					
					Location bottom = PlotManager.getPlotBottomLoc(p.getWorld(), id);
					Location top = PlotManager.getPlotTopLoc(p.getWorld(), id);
					
					p.teleport(new Location(p.getWorld(), bottom.getX() + (top.getBlockX() - bottom.getBlockX())/2, PlotManager.getMap(p).RoadHeight + 1, bottom.getZ() - 2));
				}else{
					p.sendMessage(PREFIX + RESET + " Usage: " + RED + "/plotme tp <id> " + RESET + "Example: " + RED + "/plotme tp 5;-1 ");
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}

	private boolean auto(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.auto", true))
		{			
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}else{				
				if(PlotManager.getNbOwnedPlot(p) >= PlotMe.getPlotLimit(p) && !PlotMe.checkPerms(p, "PlotMe.admin"))
					p.sendMessage(PREFIX + RED + " You have already reached your maximum amount of plots (" + 
							PlotManager.getNbOwnedPlot(p) + "/" + PlotMe.getPlotLimit(p) + "). Use " + RED + "/plotme home" + RESET + " to get to them.");
				else
				{
					int limit = PlotManager.getMap(p).PlotAutoLimit;
					
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
									Plot plot = PlotManager.createPlot(w, id, p.getName());
									
									p.teleport(new Location(p.getWorld(), PlotManager.bottomX(plot.id, w) + (PlotManager.topX(plot.id, w) - 
											PlotManager.bottomX(plot.id, w))/2, PlotManager.getMap(w).RoadHeight + 1, PlotManager.bottomZ(plot.id, w) - 2));
		
									p.sendMessage(PREFIX + RESET + " This plot is now yours. Use " + RED + "/plotme home" + RESET + " to get back to them.");
									return true;
								}
							}
						}
					}
				
					p.sendMessage(PREFIX + RED + " No plot found within " + (limit^2) + " plots. Contact an admin.");
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}

	private boolean claim(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.claim", true) || PlotMe.cPerms(p, "PlotMe.admin.claim.other", false))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}
			else
			{		
				String id = PlotManager.getPlotId(p.getLocation());
				
				if(id == "")
				{
					p.sendMessage(PREFIX + RED + " You cannot claim the road");
				}
				else if(!PlotManager.isPlotAvailable(id, p))
				{
					p.sendMessage(PREFIX + RED + " This plot is already owned");
				}else
				{								
					if(PlotManager.getNbOwnedPlot(p) >= PlotMe.getPlotLimit(p) && !PlotMe.cPerms(p, "PlotMe.admin.claim.other", false))
						p.sendMessage(PREFIX + RESET + " You have already reached your maximum amount of plots (" + 
								PlotManager.getNbOwnedPlot(p) + "/" + PlotMe.getPlotLimit(p) + "). Use " + RED + "/plotme home" + RESET + " to get to it");
					else
					{
						Plot plot = PlotManager.createPlot(p.getWorld(), id, p.getName());
		
						if(plot == null)
							p.sendMessage(PREFIX + RED + " An error occured while creating the plot at " + id);
						else
							p.sendMessage(PREFIX + RESET + " This plot is now yours. Use " + RED + "/plotme home" + RESET + " to get back to it.");
					}
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean home(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.home", true) || PlotMe.cPerms(p, "PlotMe.admin.home.other", false))
		{
			if(!PlotManager.isPlotWorld(p))
			{
				p.sendMessage(PREFIX + RED + " This is not a plot world.");
			}else{
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
					if(PlotMe.cPerms(p, "PlotMe.admin.home.other", false))
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
							p.teleport(new Location(w, PlotManager.bottomX(plot.id, w) + (PlotManager.topX(plot.id, w) - 
									PlotManager.bottomX(plot.id, w))/2, PlotManager.getMap(p).RoadHeight + 1, PlotManager.bottomZ(plot.id, w) - 2));
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
						if(!playername.equalsIgnoreCase(p.getName())){
							p.sendMessage(PREFIX + RED + " " + playername + " does not have a plot #" + nb);
						}else{
							p.sendMessage(PREFIX + RED + " Could not find plot #" + nb);
						}
					}else if(!playername.equalsIgnoreCase(p.getName())){
						p.sendMessage(PREFIX + RED + " " + playername + " does not have a plot");
					}else{
						p.sendMessage(PREFIX + RED + " You don't have a plot");
					}
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean info(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.info", true))
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
				}else{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						Plot plot = PlotManager.getPlotById(p,id);
						
						p.sendMessage(BLUE +"ID: " + ITALIC + RESET + id);
						p.sendMessage(BLUE +"Owner: " + RESET + plot.owner);
						p.sendMessage(BLUE +"Biome: " + RESET + plot.biome);
						if(plot.expireddate == null)
							p.sendMessage(BLUE +"Expire date: " + RESET + "Never");
						else
							p.sendMessage(BLUE +"Expire date: " + RESET + plot.expireddate.toString());
						if(plot.finished)
							p.sendMessage(BLUE +"Finished: " + RESET + "Yes");
						else
							p.sendMessage(BLUE +"Finished: " + RESET + "No");
						if(plot.allowedcount() > 0)
						{
							p.sendMessage(BLUE +"Helpers: " + RESET + plot.getAllowed());
						}
						
					}else{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean comment(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.comment", true))
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
					}else{
						if(!PlotManager.isPlotAvailable(id, p))
						{
							Plot plot = PlotManager.getPlotById(p, id);
							
							String text = StringUtils.join(args," ");
							text = text.substring(text.indexOf(" "));
							
							String[] comment = new String[2];
							comment[0] = p.getName();
							comment[1] = text;
							
							plot.comments.add(comment);
							SqlManager.addPlotComment(comment, plot.comments.size(), PlotManager.getIdX(id), PlotManager.getIdZ(id), plot.world);
							
							p.sendMessage(PREFIX + RESET + " Comment added");
						}else{
							p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
						}
					}
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean comments(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.comments", true))
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
					}else{
						if(!PlotManager.isPlotAvailable(id, p))
						{
							Plot plot = PlotManager.getPlotById(p,id);
							
							if(plot.owner.equalsIgnoreCase(p.getName()) || plot.isAllowed(p.getName()) || PlotMe.checkPerms(p, "PlotMe.admin"))
							{
								if(plot.comments.size() == 0)
								{
									p.sendMessage(PREFIX + RESET + " No comments");
								}else{
									p.sendMessage(PREFIX + RESET + " You have " + BLUE + plot.comments.size() + RESET + " comments.");
									
									for(String[] comment : plot.comments)
									{
										p.sendMessage(ChatColor.BLUE + "From : " + RED + comment[0]);
										p.sendMessage("" + RESET + ChatColor.ITALIC + comment[1]);
									}
									
								}
							}else{
								p.sendMessage(PREFIX + RED + " This plot(" + id + ") is not yours. You are not allowed to view the comments.");
							}
						}else{
							p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
						}
					}
				}
			}
		}else{
			p.sendMessage(BLUE + PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean biome(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.biome", true))
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
				}else{
					if(!PlotManager.isPlotAvailable(id, p))
					{
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
							}else{
								Plot plot = PlotManager.getPlotById(p,id);
								
								if(plot.owner.equalsIgnoreCase(p.getName()) || PlotMe.checkPerms(p, "PlotMe.admin"))
								{
									
									PlotManager.setBiome(p.getWorld(), id, plot, biome);
								
									p.sendMessage(PREFIX + RESET + " Biome set to " + ChatColor.BLUE + biome.name());
								}else{
									p.sendMessage(PREFIX + RED + " This plot(" + id + ") is not yours. You are not allowed to change it's biome.");
								}
							}
						}else{
							Plot plot = PlotMe.plotmaps.get(p.getWorld().getName().toLowerCase()).plots.get(id);
							
							p.sendMessage(PREFIX + RESET + " This plot is using the biome " + ChatColor.BLUE + plot.biome.name());
						}
					}else{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean biomelist(CommandSender s, String[] args)
	{
		if (!(s instanceof Player) || PlotMe.cPerms((Player) s, "PlotMe.use.biome", true))
		{
			s.sendMessage(PREFIX + RESET + " Biomes : ");
					
			int i = 0;
			String line = "";
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
					line = line + b;
					s.sendMessage(BLUE + line);
					i = 0;
					line = "";
				}else{
					line = line + b + whitespace(363 - nameLength);
				}
			}
		}else{
			s.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean reset(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.reset", false))
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
				}else{
					Location bottom = PlotManager.getPlotBottomLoc(p.getWorld(), id);
					Location top = PlotManager.getPlotTopLoc(p.getWorld(), id);
					
					PlotManager.clear(bottom, top);
					
					SqlManager.deletePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), p.getWorld().getName().toLowerCase());
					
					if(!PlotManager.isPlotAvailable(id, p))
					{
						PlotManager.getPlots(p).remove(id);
					}
					
					PlotManager.removeSign(p.getWorld(), id);
					p.sendMessage(PREFIX + RESET + " Plot has been reset.");
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean clear(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.clear", false) || PlotMe.cPerms(p, "PlotMe.use.clear", true))
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
				}else{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						Plot plot = PlotManager.getPlotById(p,id);
						
						if(plot.owner.equalsIgnoreCase(p.getName()) || PlotMe.cPerms(p, "PlotMe.admin.clear", false))
						{
							PlotManager.clear(p.getWorld(), plot);
							
							p.sendMessage(PREFIX + RESET + " Plot cleared");
						}else{
							p.sendMessage(PREFIX + RED + " This plot(" + id + ") is not yours. You are not allowed to clear it.");
						}
					}else{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean add(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.add", false) || PlotMe.cPerms(p, "PlotMe.use.add", false))
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
				}else{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						if(args.length < 2 || args[1].equalsIgnoreCase(""))
						{
							p.sendMessage(PREFIX + RESET + " Usage " + RED + "/plotme allow <player>");
						}else{
						
							Plot plot = PlotManager.getPlotById(p,id);
							
							if(plot.owner.equalsIgnoreCase(p.getName()) || PlotMe.cPerms(p, "PlotMe.admin.add", false))
							{
								if(plot.isAllowed(args[1]))
								{
									p.sendMessage(PREFIX + RESET + " Player " + RED + args[1] + RESET + " was already allowed");
								}else{
									plot.addAllowed(args[1]);
									
									p.sendMessage(PREFIX + RESET + " Player " + RED + args[1] + RESET + " now allowed");
								}
							}else{
								p.sendMessage(PREFIX + RED + " This plot(" + id + ") is not yours. You are not allowed to add someone to it.");
							}
						}
					}else{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean remove(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.remove", false) || PlotMe.cPerms(p, "PlotMe.use.remove", false))
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
				}else{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						if(args.length < 2 || args[1].equalsIgnoreCase(""))
						{
							p.sendMessage(PREFIX + RESET + " Usage " + RED + "/plotme remove <player>");
						}else{
						
							Plot plot = PlotManager.getPlotById(p,id);
							
							if(plot.owner.equalsIgnoreCase(p.getName()) || PlotMe.cPerms(p, "PlotMe.admin.remove", false))
							{
								if(plot.isAllowed(args[1]))
								{
									plot.removeAllowed(args[1]);
																	
									p.sendMessage(PREFIX + RESET + "Player " + RED + args[1] + RESET + " removed");
								}else{
									p.sendMessage(PREFIX + RESET + "Player " + RED + args[1] + RESET + " wasn't allowed");
								}
							}else{
								p.sendMessage(PREFIX + RED + " This plot(" + id + ") is not yours. You are not allowed to remove someone from it.");
							}
						}
					}else{
						p.sendMessage(PREFIX + RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean setowner(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.setowner", false))
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
				}else{
					if(!PlotManager.isPlotAvailable(id, p))
					{
						if(args.length < 2 || args[1].equalsIgnoreCase(""))
						{
							p.sendMessage(PREFIX + RESET + " Usage " + RED + "/plotme owner <player>");
						}else{
						
							Plot plot = PlotManager.getPlotById(p,id);
					
							plot.owner = args[1];
							
							PlotManager.setSign(p.getWorld(), plot);
							
							SqlManager.updatePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), plot.world, "owner", args[1]);
						}
					}else{
						PlotManager.createPlot(p.getWorld(), id, args[1]);
					}
					
					p.sendMessage(PREFIX + RESET + " Plot Owner has been set to " + RED + args[1]);
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean id(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.id", false))
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
				}else{
					p.sendMessage(BLUE + "Plot Id: " + RESET + plotid);
					
					Location bottom = PlotManager.getPlotBottomLoc(p.getWorld(), plotid);
					Location top = PlotManager.getPlotTopLoc(p.getWorld(), plotid);
					
					p.sendMessage(BLUE + "Bottom: " + RESET + bottom.getBlockX() + ChatColor.BLUE + "," + RESET + bottom.getBlockZ());
					p.sendMessage(BLUE + "Top: " + RESET + top.getBlockX() + ChatColor.BLUE + "," + RESET + top.getBlockZ());
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean move(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.move", false))
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
				}else{
					if(PlotManager.movePlot(p.getWorld(), args[1], args[2]))
						p.sendMessage(PREFIX + RESET + " Plot moved successfully");
					else
						p.sendMessage(PREFIX + RED + " Error moving plot");
				}
			}
		}else{
			p.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	private boolean reload(CommandSender s, String[] args)
	{
		if (!(s instanceof Player) || PlotMe.cPerms((Player) s, "PlotMe.admin.reload", false))
		{
			plugin.initialize();
			s.sendMessage(PREFIX + RESET + " reloaded successfully");
		}else{
			s.sendMessage(PREFIX + RED + " Permission denied");
		}
		return true;
	}
	
	
	private String whitespace(int length) {
		int spaceWidth = MinecraftFontWidthCalculator.getStringWidth(" ");
		
		StringBuilder ret = new StringBuilder();
		
		for(int i = 0; i < length; i+=spaceWidth) {
			ret.append(" ");
		}
		
		return ret.toString();
	}
}
