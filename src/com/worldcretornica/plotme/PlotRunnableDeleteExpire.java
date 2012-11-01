package com.worldcretornica.plotme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;

public class PlotRunnableDeleteExpire implements Runnable {

	public void run()
	{
		if(PlotMe.worldcurrentlyprocessingexpired != null)
		{
			World w = PlotMe.worldcurrentlyprocessingexpired;
			List<Plot> expiredplots = new ArrayList<Plot>();
			HashMap<String, Plot> plots = PlotManager.getPlots(w);
			String date = PlotMe.getDate();
			Plot expiredplot;
			
			for(String id : plots.keySet())
			{
				Plot plot = plots.get(id);
				
				if(!plot.protect && !plot.finished && plot.expireddate != null && PlotMe.getDate(plot.expireddate).compareTo(date.toString()) < 0)
				{
					expiredplots.add(plot);
				}
			}
			
			if(expiredplots.size() == 0)
			{
				PlotMe.counterexpired = 0;
			}
			else
			{
				plots = null;
				
				Collections.sort(expiredplots);
				
				String ids = "";
				
				for(int ictr = 0; ictr < PlotMe.nbperdeletionprocessingexpired; ictr++)
				{
					expiredplot = expiredplots.get(0);
					
					expiredplots = null;
					
					PlotManager.clear(w, expiredplot);
					
					String id = expiredplot.id;
					ids += ids + ", ";
					
					PlotManager.getPlots(w).remove(id);
						
					PlotManager.removeOwnerSign(w, id);
					PlotManager.removeSellSign(w, id);
					
					SqlManager.deletePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), w.getName().toLowerCase());
					
					PlotMe.counterexpired--;
				}
				
				if(ids.substring(ids.length() -1) == ",")
				{
					ids = ids.substring(0, ids.length() - 1);
				}
				
				PlotMe.cscurrentlyprocessingexpired.sendMessage("" + PlotMe.PREFIX + ChatColor.RESET + " Deleted expired plot " + ids);
			}
			
			if(PlotMe.counterexpired == 0)
			{
				PlotMe.cscurrentlyprocessingexpired.sendMessage("" + PlotMe.PREFIX + ChatColor.RESET + " Deletion session finished, rerun to reset more plots");
				PlotMe.worldcurrentlyprocessingexpired = null;
				PlotMe.cscurrentlyprocessingexpired = null;
			}
		}
	}
}
