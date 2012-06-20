package com.worldcretornica.plotme;

import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Biome;

public class Plot implements Comparable<Plot>, Serializable {

	private static final long serialVersionUID = 1129643448136021025L;
	public String owner;
	public String world;
	private HashSet<String> allowed;
	public Biome biome;
	public Date expireddate;
	public boolean finished;
	public List<String[]> comments;
	public String id;

	public Plot()
	{
		owner = "";
		world = "";
		id = "";
		allowed = new HashSet<String>();
		biome = Biome.PLAINS;
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 7);
		java.util.Date utlDate = cal.getTime();
		expireddate = new java.sql.Date(utlDate.getTime());
		
		comments = new ArrayList<String[]>();
	}
	
	public Plot(String o, Location t, Location b, String tid, int days)
	{
		owner = o;
		world = t.getWorld().getName();
		allowed = new HashSet<String>();
		biome = Biome.PLAINS;
		id = tid;
		
		if(days == 0)
		{
			expireddate = null;
		}else{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, days);
			java.util.Date utlDate = cal.getTime();
			expireddate = new java.sql.Date(utlDate.getTime());
		}
		
		comments = new ArrayList<String[]>();
	}
	
	public Plot(String o, String w, int tX, int bX, int tZ, int bZ, String bio, Date exp, boolean fini, HashSet<String> al, List<String[]> comm, String tid)
	{
		owner = o;
		world = w;
		biome = Biome.valueOf(bio);
		expireddate = exp;
		finished = fini;
		allowed = al;
		comments = comm;
		id = tid;
	}
			
	public void setExpire(Date date)
	{
		if(!expireddate.equals(date))
		{
			expireddate = date;
			SqlManager.updatePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), world, "expireddate", expireddate);
		}
	}
	
	public void resetExpire(int days)
	{
		if(days == 0)
		{
			if(expireddate != null)
			{
				expireddate = null;
				SqlManager.updatePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), world, "expireddate", expireddate);
			}
		}else{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, days);
			java.util.Date utlDate = cal.getTime();
			java.sql.Date temp = new java.sql.Date(utlDate.getTime());
			if(!temp.equals(expireddate))
			{
				expireddate = temp;
				SqlManager.updatePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), world, "expireddate", expireddate);
			}
		}
	}
	
	public String getExpire()
	{
		return DateFormat.getDateInstance().format(expireddate);
	}
	
	public Biome getBiome()
	{
		return biome;
	}
	
	public String getOwner()
	{
		return owner;
	}
	
	public String getAllowed()
	{
		String list = "";
		
		for(String s : allowed)
		{
			list = list + s + ", ";
		}
		if(list.length() > 1)
		{
			list = list.substring(0, list.length()-2);
		}
		return list;
	}
	
	public int getCommentsCount()
	{
		return comments.size();
	}
	
	public String[] getComments(int i)
	{
		return comments.get(i);
	}
	
	public void addAllowed(String name)
	{
		if(!isAllowed(name))
		{
			allowed.add(name);
			SqlManager.addPlotAllowed(name, PlotManager.getIdX(id), PlotManager.getIdZ(id), world);
		}
	}
	
	public void removeAllowed(String name)
	{
		if(isAllowed(name))
		{
			allowed.remove(name);
			SqlManager.deletePlotAllowed(PlotManager.getIdX(id), PlotManager.getIdZ(id), name, world);
		}
	}
	
	public boolean isAllowed(String name)
	{
		if(owner.equalsIgnoreCase(name)) return true;
		
		for(String str : allowed)
		{
			if(str.equalsIgnoreCase(name))
				return true;
		}
		
		return false;
	}
	
	public HashSet<String> allowed()
	{
		return allowed;
	}
	
	public int allowedcount()
	{
		return allowed.size();
	}

	@Override
	public int compareTo(Plot plot)
	{
		if(expireddate.compareTo(plot.expireddate) == 0)
			return owner.compareTo(plot.owner);
		else
			return expireddate.compareTo(plot.expireddate);
	}	
}
