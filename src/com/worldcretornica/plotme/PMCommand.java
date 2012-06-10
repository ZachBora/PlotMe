package com.worldcretornica.plotme;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.angelsl.minecraft.randomshit.fontwidth.MinecraftFontWidthCalculator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PMCommand implements CommandExecutor {

	private PlotMe plugin;
	
	public PMCommand(PlotMe instance)
	{
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender s, Command c, String l, String[] args){
		if(l.equalsIgnoreCase("plotme") || l.equalsIgnoreCase("plot") || l.equalsIgnoreCase("p")){
			if(!(s instanceof Player)){
				if(args.length == 0 || args[0].equalsIgnoreCase("1")){
					s.sendMessage(PlotMe.PREFIX + " v" + PlotMe.VERSION);
					s.sendMessage(" ---==PlotMe Console Help Page==--- "); //+ ChatColor.WHITE + " [] means optional, <> means obligated"
					s.sendMessage(" - /plotme reload");
					s.sendMessage(" - Reloads the plugin and its configuration files");
				}else{
					String a0 = args[0].toString();
					if(!(s instanceof Player)){
						if (a0.equalsIgnoreCase("reload")) { reload(s, args); return true;}
					}
				}
			}else{
				if(args.length == 0 || args[0].equalsIgnoreCase("1")){
					s.sendMessage(ChatColor.RED + " ---==" + ChatColor.BLUE + "PlotMe Help Page #1" + ChatColor.RED + "==--- "); //+ ChatColor.WHITE + " [] means optional, <> means obligated"
					if(PlotMe.cPerms((Player) s, "PlotMe.use.claim", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme claim");
						s.sendMessage(ChatColor.AQUA + " - Claim the current plot you are standing on");
					if(PlotMe.cPerms((Player) s, "PlotMe.use.claim.other", false))
						s.sendMessage(ChatColor.GREEN + " - /plotme claim <player>");
						s.sendMessage(ChatColor.AQUA + " - Claim the current plot you are standing on for another player");
					if(PlotMe.cPerms((Player) s, "PlotMe.use.auto", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme auto");
						s.sendMessage(ChatColor.AQUA + " - Claim the next available free plot");
					if(PlotMe.cPerms((Player) s, "PlotMe.use.home", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme home[:#]");
						s.sendMessage(ChatColor.AQUA + " - Teleports you to your own plot, :# if you own multiple plots");
					if(PlotMe.cPerms((Player) s, "PlotMe.use.home.other", false))
						s.sendMessage(ChatColor.GREEN + " - /plotme home[:#] <player>");
						s.sendMessage(ChatColor.AQUA + " - Teleports you to other plots, :# if other people own multiple plots");
				}else{
					String a0 = args[0].toString();
					if(s instanceof Player){
						Player p = (Player)s;
						if (a0.equalsIgnoreCase("claim")) { claim(p, args); return true;}
						if (a0.equalsIgnoreCase("auto")) { auto(p, args); return true;	}
						if (a0.startsWith("home") || a0.startsWith("h")) { home(p, args); return true;}
					}
				}
				if(args.length == 1 && args[0].equalsIgnoreCase("2")){
					s.sendMessage(ChatColor.RED + " ---==" + ChatColor.BLUE + "PlotMe Help Page #2" + ChatColor.RED + "==--- ");
					if(PlotMe.cPerms((Player) s, "PlotMe.use.info", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme info");
						s.sendMessage(ChatColor.AQUA + " - Displays information about the plot your currently standing on");
					if(PlotMe.cPerms((Player) s, "PlotMe.use.comment", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme comment <comment>");
						s.sendMessage(ChatColor.AQUA + " - Leave comment on the current plot");
					if(PlotMe.cPerms((Player) s, "PlotMe.use.comments", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme comments");
						s.sendMessage(ChatColor.AQUA + " - Lists all comments users have said about your plot");
					if(PlotMe.cPerms((Player) s, "PlotMe.use.info", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme biomeauto");
						s.sendMessage(ChatColor.AQUA + " - Shows the current biome in the plot");
					if(PlotMe.cPerms((Player) s, "PlotMe.use.biome", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme biome <biome>");
						s.sendMessage(ChatColor.AQUA + " - Changes the plots biome to the one specified");
				}else{
					String a0 = args[0].toString();
					if(s instanceof Player){
						Player p = (Player)s;
						if (a0.equalsIgnoreCase("info") || a0.equalsIgnoreCase("i")) { info(p, args); return true;}
						if (a0.equalsIgnoreCase("comment")) { comment(p, args); return true;}
						if (a0.equalsIgnoreCase("comments") || a0.equalsIgnoreCase("c")) { comments(p, args); return true;}
						if (a0.equalsIgnoreCase("biome") || a0.equalsIgnoreCase("b")) { biome(p, args); return true;}
					}
				}
				if(args.length == 1 && args[0].equalsIgnoreCase("3")){
					s.sendMessage(ChatColor.RED + " ---==" + ChatColor.BLUE + "PlotMe Help Page #3" + ChatColor.RED + "==--- ");
					if(PlotMe.cPerms((Player) s, "PlotMe.use.biome", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme biomelist");
						s.sendMessage(ChatColor.AQUA + " - List all possible biomes");
					if(PlotMe.cPerms((Player) s, "PlotMe.admin.tp", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme tp <id>");
						s.sendMessage(ChatColor.AQUA + " - Teleports to a plot in the current world");
					if(PlotMe.cPerms((Player) s, "PlotMe.admin.id", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme id");
						s.sendMessage(ChatColor.AQUA + " - Gets plot id and coordinates of the current plot your standing on");
					if(PlotMe.cPerms((Player) s, "PlotMe.admin.clear", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme clear");
						s.sendMessage(ChatColor.AQUA + " - Clear the plot to its original flat state");
					if(PlotMe.cPerms((Player) s, "PlotMe.admin.reset", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme reset");
						s.sendMessage(ChatColor.AQUA + " - Reset the plot to its original flat state AND remove its owner");
				}else{
					String a0 = args[0].toString();
					if(s instanceof Player){
						Player p = (Player)s;
						if (a0.equalsIgnoreCase("biomelist")) { biomelist(p, args); return true;}
						if (a0.equalsIgnoreCase("id")) { id(p, args); return true;}
						if (a0.equalsIgnoreCase("tp")) { tp(p, args); return true;}
						if (a0.equalsIgnoreCase("clear")) { clear(p, args); return true;}
						if (a0.equalsIgnoreCase("reset")) { reset(p, args); return true;}
					}
				}
				if(args.length == 1 && args[0].equalsIgnoreCase("4")){
					s.sendMessage(ChatColor.RED + " ---==" + ChatColor.BLUE + "PlotMe Help Page #4" + ChatColor.RED + "==--- ");
					if(PlotMe.cPerms((Player) s, "PlotMe.admin.add", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme add <player>");
						s.sendMessage(ChatColor.AQUA + " - Allows a player to have full access to the plot(This is your responsibility!)");
					if(PlotMe.cPerms((Player) s, "PlotMe.admin.remove", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme remove <player>");
						s.sendMessage(ChatColor.AQUA + " - Revoke a players access to the plot");
					if(PlotMe.cPerms((Player) s, "PlotMe.admin.setowner", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme setowner <player>");
						s.sendMessage(ChatColor.AQUA + " - Sets the player provided as the owner of the plot your currently on");
					if(PlotMe.cPerms((Player) s, "PlotMe.admin.move", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme move <id-from> <id-to>");
						s.sendMessage(ChatColor.AQUA + " - Swaps the plots blocks(highly experimental for now, use at your own risk)");
					if(PlotMe.cPerms((Player) s, "PlotMe.admin.reload", true))
						s.sendMessage(ChatColor.GREEN + " - /plotme reload");
						s.sendMessage(ChatColor.AQUA + " - Reloads the plugin and its configuration files");
				}else{
					String a0 = args[0].toString();
					if(s instanceof Player){
						Player p = (Player)s;
						if (a0.equalsIgnoreCase("add") || a0.equalsIgnoreCase("+")) { add(p, args); return true;}
						if (a0.equalsIgnoreCase("remove") || a0.equalsIgnoreCase("-")) { remove(p, args); return true;}
						if (a0.equalsIgnoreCase("setowner") || a0.equalsIgnoreCase("o")) { setowner(p, args); return true;}
						if (a0.equalsIgnoreCase("move") || a0.equalsIgnoreCase("m")) { move(p, args); return true;}
						if (a0.equalsIgnoreCase("reload")) { reload(s, args); return true;}
					}
				}
			}
		}
		return false;
	}
	
	private void tp(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.tp", false))
		{
			if(args.length == 2)
			{
				String id = args[1];
				
				Location bottom = PlotManager.getPlotBottomLoc(p.getWorld(), id);
				Location top = PlotManager.getPlotTopLoc(p.getWorld(), id);
				
				p.teleport(new Location(p.getWorld(), bottom.getX() + (top.getBlockX() - bottom.getBlockX())/2, 65, bottom.getZ() - 2));
			}else{
				p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Usage: " + ChatColor.RED + "/plotme tp <id> " + ChatColor.WHITE + "Example: " + ChatColor.RED + "/plotme tp 5;-1 ");
			}
		}
	}

	private void auto(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.auto", true))
		{
			Boolean found = false;
			
			if(!PlotMe.plotmaps.containsKey(p.getWorld().getName()))
			{
				p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " This is not a plot world.");
			}else{
				if(!PlotMe.checkPerms(p, "PlotMe.admin"))
				{
					for(Plot plot : PlotMe.plotmaps.get(p.getWorld().getName()).plots.values())
					{
						if(plot.owner.equalsIgnoreCase(p.getName()))
						{
							found = true;
							break;
						}
					}
				}
				
				if(found && !PlotMe.checkPerms(p, "PlotMe.admin"))
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " You already own a plot. Use " + ChatColor.RED + "/plotme home" + ChatColor.WHITE + " to get to it");
				else
				{
					int limit = PlotMe.plotmaps.get(p.getWorld().getName()).PlotAutoLimit;
					
					for(int i = 0; i < limit; i++)
					{
						for(int x = -i; x <= i; x++)
						{
							for(int z = -i; z <= i; z++)
							{
								String id = "" + x + ";" + z;
								
								if(PlotManager.isPlotAvailable(id, p.getWorld().getName()))
								{
									Plot plot = PlotManager.createPlot(p.getWorld(), id, p.getName());
									
									p.teleport(new Location(p.getWorld(), plot.bottomX + (plot.topX - plot.bottomX)/2, 65, plot.bottomZ - 2));
		
									p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " This plot is now yours. Use " + ChatColor.RED + "/plotme home" + ChatColor.WHITE + " to get back to it");
									return;
								}
							}
						}
					}
				
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " No plot found within " + (limit^2) + " plots. Contact an admin.");
				}
			}
		}
	}

	private void claim(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.claim", true) || PlotMe.cPerms(p, "PlotMe.admin.claim.other", false))
		{
			String id = PlotManager.getPlotId(p.getLocation());
			
			if(!PlotManager.isPlotAvailable(id, p.getWorld().getName()))
			{
				p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " This plot is already owned");
			}else{
				
				Boolean found = false;
				
				if(!PlotMe.cPerms(p, "PlotMe.admin.claim.other", false))
				{
					for(Plot plot : PlotMe.plotmaps.get(p.getWorld().getName()).plots.values())
					{
						if(plot.owner.equalsIgnoreCase(p.getName()))
						{
							found = true;
							break;
						}
					}
				}
				
				if(found && !PlotMe.checkPerms(p, "PlotMe.admin"))
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " You already own a plot. Use " + ChatColor.RED + "/plotme home" + ChatColor.WHITE + " to get to it");
				else
				{
					PlotManager.createPlot(p.getWorld(), id, p.getName());
	
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " This plot is now yours. Use " + ChatColor.RED + "/plotme home" + ChatColor.WHITE + " to get back to it.");
				}
			}
		}
	}
	
	private void home(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.home", true) || PlotMe.cPerms(p, "PlotMe.admin.home.other", false))
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
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Format is: " + ChatColor.RED + "/plot home:# " + ChatColor.WHITE + "As in : " + ChatColor.RED + "/plot home:1");
					return;
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
					
			for(Plot plot : PlotMe.plotmaps.get(p.getWorld().getName()).plots.values())
			{
				if(plot.owner.equalsIgnoreCase(playername))
				{
					if(i == 0)
					{
						p.teleport(new Location(p.getWorld(), plot.bottomX + (plot.topX - plot.bottomX)/2, 65, plot.bottomZ - 2));
						found = true;
						return;
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
						p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " " + playername + " does not have a plot #" + nb);
					}else{
						p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " Could not find plot #" + nb);
					}
				}else if(!playername.equalsIgnoreCase(p.getName())){
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " " + playername + " does not have a plot");
				}else{
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " You don't have a plot");
				}
			}
		}
	}
	
	private void info(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.info", true))
		{
			String id = PlotManager.getPlotId(p.getLocation());
			
			if(id.equals(""))
			{
				p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " No plot found");
			}else{
				if(!PlotManager.isPlotAvailable(id, p.getWorld().getName()))
				{
					Plot plot = PlotMe.plotmaps.get(p.getWorld().getName()).plots.get(id);
					
					p.sendMessage(ChatColor.BLUE +"ID: " + ChatColor.ITALIC + ChatColor.WHITE + id);
					p.sendMessage(ChatColor.BLUE +"Owner: " + ChatColor.WHITE + plot.owner);
					p.sendMessage(ChatColor.BLUE +"Biome: " + ChatColor.WHITE + plot.biome);
					if(plot.expireddate == null)
						p.sendMessage(ChatColor.BLUE +"Expire date: " + ChatColor.WHITE + "Never");
					else
						p.sendMessage(ChatColor.BLUE +"Expire date: " + ChatColor.WHITE + DateFormat.getDateInstance().format(plot.expireddate));
					if(plot.finished)
						p.sendMessage(ChatColor.BLUE +"Finished: " + ChatColor.WHITE + "Yes");
					else
						p.sendMessage(ChatColor.BLUE +"Finished: " + ChatColor.WHITE + "No");
					if(plot.allowed.size() > 0)
					{
						p.sendMessage(ChatColor.BLUE +"Helpers: " + ChatColor.WHITE + plot.getAllowed());
					}
					
				}else{
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " This plot(" + id + ") has no owners.");
				}
			}
		}
	}
	
	private void comment(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.comment", true))
		{
			if(args.length < 2)
			{
				p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Usage: " + ChatColor.RED + "/plotme comment <text>");
			}else{
			
				String id = PlotManager.getPlotId(p.getLocation());
				
				if(id.equals(""))
				{
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " No plot found");
				}else{
					if(!PlotManager.isPlotAvailable(id, p.getWorld().getName()))
					{
						Plot plot = PlotMe.plotmaps.get(p.getWorld().getName()).plots.get(id);
						
						String text = StringUtils.join(args," ");
						text = text.substring(text.indexOf(" "));
						
						String[] comment = new String[2];
						comment[0] = p.getName();
						comment[1] = text;
						
						plot.comments.add(comment);
						SqlManager.addPlotComment(comment, plot.comments.size(), PlotManager.getIdX(id), PlotManager.getIdZ(id), plot.world);
						
						p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Comment added");
					}else{
						p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}
	}
	
	private void comments(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.comments", true))
		{
			if(args.length < 2)
			{
				String id = PlotManager.getPlotId(p.getLocation());
				
				if(id.equals(""))
				{
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " No plot found");
				}else{
					if(!PlotManager.isPlotAvailable(id, p.getWorld().getName()))
					{
						Plot plot = PlotMe.plotmaps.get(p.getWorld().getName()).plots.get(id);
						
						if(plot.owner.equalsIgnoreCase(p.getName()) || plot.isAllowed(p.getName()) || PlotMe.checkPerms(p, "PlotMe.admin"))
						{
							if(plot.comments.size() == 0)
							{
								p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " No comments");
							}else{
								p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " You have " + ChatColor.BLUE + plot.comments.size() + ChatColor.WHITE + " comments.");
								
								for(String[] comment : plot.comments)
								{
									p.sendMessage(ChatColor.BLUE + "From : " + ChatColor.RED + comment[0]);
									p.sendMessage("" + ChatColor.WHITE + ChatColor.ITALIC + comment[1]);
								}
								
							}
						}else{
							p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " This plot(" + id + ") is not yours. You are not allowed to view the comments.");
						}
					}else{
						p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " This plot(" + id + ") has no owners.");
					}
				}
			}
		}
	}
	
	private void biome(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.use.biome", true))
		{
			String id = PlotManager.getPlotId(p.getLocation());
			if(id.equals(""))
			{
				p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " No plot found");
			}else{
				if(!PlotManager.isPlotAvailable(id, p.getWorld().getName()))
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
							p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " " + args[1] + ChatColor.WHITE + " is not a valid biome.");
						}else{
							Plot plot = PlotMe.plotmaps.get(p.getWorld().getName()).plots.get(id);
							
							if(plot.owner.equalsIgnoreCase(p.getName()) || PlotMe.checkPerms(p, "PlotMe.admin"))
							{
								
								PlotManager.setBiome(p.getWorld(), id, plot, biome);
							
								p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Biome set to " + ChatColor.BLUE + biome.name());
							}else{
								p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " This plot(" + id + ") is not yours. You are not allowed to change it's biome.");
							}
						}
					}else{
						Plot plot = PlotMe.plotmaps.get(p.getWorld().getName()).plots.get(id);
						
						p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " This plot is using the biome " + ChatColor.BLUE + plot.biome.name());
					}
				}else{
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " This plot(" + id + ") has no owners.");
				}
			}
		}
	}
	
	private void biomelist(CommandSender s, String[] args)
	{
		if (!(s instanceof Player) || PlotMe.cPerms((Player) s, "PlotMe.use.biome", true))
		{
			s.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Biomes : ");
					
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
					s.sendMessage(ChatColor.BLUE + line);
					i = 0;
					line = "";
				}else{
					line = line + b + whitespace(363 - nameLength);
				}
			}
		}
	}
	
	private void reset(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.reset", false))
		{
			if(PlotMe.checkPerms(p, "PlotMe.admin"))
			{
				String id = PlotManager.getPlotId(p.getLocation());
				if(id.equals(""))
				{
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " No plot found");
				}else{
					Location bottom = PlotManager.getPlotBottomLoc(p.getWorld(), id);
					Location top = PlotManager.getPlotTopLoc(p.getWorld(), id);
					
					PlotManager.clear(bottom, top);
					
					SqlManager.deletePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), p.getWorld().getName());
					
					if(!PlotManager.isPlotAvailable(id, p.getWorld().getName()))
					{
						PlotMe.plotmaps.get(p.getWorld().getName()).plots.remove(id);
					}
					
					PlotManager.removeSign(p.getWorld(), id);
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Plot has been reset.");
				}
			}
		}
	}
	
	private void clear(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.clear", false))
		{
			String id = PlotManager.getPlotId(p.getLocation());
			if(id.equals(""))
			{
				p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " No plot found");
			}else{
				if(!PlotManager.isPlotAvailable(id, p.getWorld().getName()))
				{
					Plot plot = PlotMe.plotmaps.get(p.getWorld().getName()).plots.get(id);
					
					if(plot.owner.equalsIgnoreCase(p.getName()) || PlotMe.checkPerms(p, "PlotMe.admin"))
					{
						PlotManager.clear(p.getWorld(), plot);
						
						p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Plot cleared");
					}else{
						p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " This plot(" + id + ") is not yours. You are not allowed to clear it.");
					}
				}else{
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " This plot(" + id + ") has no owners.");
				}
			}
		}
	}
	
	private void add(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.add", false))
		{
			String id = PlotManager.getPlotId(p.getLocation());
			if(id.equals(""))
			{
				p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " No plot found");
			}else{
				if(!PlotManager.isPlotAvailable(id, p.getWorld().getName()))
				{
					if(args.length < 2 || args[1].equalsIgnoreCase(""))
					{
						p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Usage " + ChatColor.RED + "/plotme allow <player>");
					}else{
					
						Plot plot = PlotMe.plotmaps.get(p.getWorld().getName()).plots.get(id);
						
						if(plot.isAllowed(args[1]))
						{
							p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Player " + ChatColor.RED + args[1] + ChatColor.WHITE + " was already allowed");
						}else{
							plot.allowed.add(args[1]);
							
							SqlManager.addPlotAllowed(args[1], PlotManager.getIdX(id), PlotManager.getIdZ(id), plot.world);
							p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Player " + ChatColor.RED + args[1] + ChatColor.WHITE + " now allowed");
						}
					}
				}else{
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " This plot(" + id + ") has no owners.");
				}
			}
		}
	}
	
	private void remove(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.remove", false))
		{
			String id = PlotManager.getPlotId(p.getLocation());
			if(id.equals(""))
			{
				p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " No plot found");
			}else{
				if(!PlotManager.isPlotAvailable(id, p.getWorld().getName()))
				{
					if(args.length < 2 || args[1].equalsIgnoreCase(""))
					{
						p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Usage " + ChatColor.RED + "/plotme remove <player>");
					}else{
					
						Plot plot = PlotMe.plotmaps.get(p.getWorld().getName()).plots.get(id);
						
						if(plot.isAllowed(args[1]))
						{
							plot.allowed.remove(args[1]);
							
							SqlManager.deletePlotAllowed(PlotManager.getIdX(id), PlotManager.getIdZ(id), args[1], plot.world);
							
							p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + "Player " + ChatColor.RED + args[1] + ChatColor.WHITE + " removed");
						}else{
							p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + "Player " + ChatColor.RED + args[1] + ChatColor.WHITE + " wasn't allowed");
						}
					}
				}else{
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " This plot(" + id + ") has no owners.");
				}
			}
		}
	}
	
	private void setowner(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.setowner", false))
		{
			String id = PlotManager.getPlotId(p.getLocation());
			if(id.equals(""))
			{
				p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " No plot found");
			}else{
				if(!PlotManager.isPlotAvailable(id, p.getWorld().getName()))
				{
					if(args.length < 2 || args[1].equalsIgnoreCase(""))
					{
						p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Usage " + ChatColor.RED + "/plotme owner <player>");
					}else{
					
						Plot plot = PlotMe.plotmaps.get(p.getWorld().getName()).plots.get(id);
				
						plot.owner = args[1];
						
						PlotManager.setSign(p.getWorld(), plot);
						
						SqlManager.updatePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), "owner", args[1], plot.world);
					}
				}else{
					PlotManager.createPlot(p.getWorld(), id, args[1]);
				}
				
				p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Plot Owner has been set to " + ChatColor.RED + args[1]);
			}
		}
	}
	
	private void id(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.id", false))
		{
			String plotid = PlotManager.getPlotId(p.getLocation());
			
			if(plotid.equals(""))
			{
				p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " No plot found");
			}else{
				p.sendMessage(ChatColor.BLUE + "Plot Id: " + ChatColor.WHITE + plotid);
				
				Location bottom = PlotManager.getPlotBottomLoc(p.getWorld(), plotid);
				Location top = PlotManager.getPlotTopLoc(p.getWorld(), plotid);
				
				p.sendMessage(ChatColor.BLUE + "Bottom: " + ChatColor.WHITE + bottom.getBlockX() + ChatColor.BLUE + "," + ChatColor.WHITE + bottom.getBlockZ());
				p.sendMessage(ChatColor.BLUE + "Top: " + ChatColor.WHITE + top.getBlockX() + ChatColor.BLUE + "," + ChatColor.WHITE + top.getBlockZ());
			}
		}
	}
	
	private void move(Player p, String[] args)
	{
		if (PlotMe.cPerms(p, "PlotMe.admin.move", false))
		{
			if(args.length < 3 || args[1].equalsIgnoreCase("") || args[2].equalsIgnoreCase(""))
			{
				p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Usage " + ChatColor.RED + "/plotme move <idFrom> <idTo>");
			}else{
				if(PlotManager.movePlot(p.getWorld(), args[1], args[2]))
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " Plot moved successfully");
				else
					p.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.RED + " Error moving plot");
			}
		}
	}
	
	private void reload(CommandSender s, String[] args)
	{
		if (!(s instanceof Player) || PlotMe.cPerms((Player) s, "PlotMe.admin.reload", false))
		{
			plugin.initialize();
			s.sendMessage(ChatColor.BLUE + PlotMe.PREFIX + ChatColor.WHITE + " reloaded successfully");
		}
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
