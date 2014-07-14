package com.worldcretornica.plotme;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

public class Plot implements Comparable<Plot> {

	public String owner;
	public UUID ownerId;
	public final String world;
	public Biome biome;
	public Date expireddate;
	public boolean finished;
	public final List<String[]> comments;
	public final String id;
	public double customprice;
	public boolean forsale;
	public String finisheddate;
	public boolean protect;
	public boolean auctionned;
	public String currentbidder;
	public UUID currentbidderId;
	public double currentbid;
	final PlayerList allowed;
	final PlayerList denied;

	public Plot(String o, UUID uuid, Location t, Location b, String tid, int days) {
		owner = o;
		ownerId = uuid;
		world = t.getWorld().getName();
		allowed = new PlayerList();
		denied = new PlayerList();
		biome = Biome.PLAINS;
		id = tid;

		if (days == 0) {
			expireddate = null;
		} else {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, days);
			java.util.Date utlDate = cal.getTime();
			expireddate = new java.sql.Date(utlDate.getTime());
		}

		comments = new ArrayList<>();
		customprice = 0;
		forsale = false;
		finisheddate = "";
		protect = false;
		auctionned = false;
		currentbidder = "";
		currentbid = 0;
		currentbidderId = null;
	}

	Plot(String o, UUID uuid, int tX, int bX, int tZ, int bZ, String bio, Date exp, boolean fini, PlayerList al, List<String[]> comm, String tid, double custprice, boolean sale, String finishdt, boolean prot, String bidder, UUID bidderId, Double bid, boolean isauctionned, PlayerList den) {
		ownerId = uuid;
		owner = o;
		world = "plotworld";
		biome = Biome.valueOf(bio);
		expireddate = exp;
		finished = fini;
		allowed = al;
		comments = comm;
		id = tid;
		customprice = custprice;
		forsale = sale;
		finisheddate = finishdt;
		protect = prot;
		auctionned = isauctionned;
		currentbidder = bidder;
		currentbid = bid;
		denied = den;
		if (bidderId == null) {
			currentbidderId = null;
		} else {
			currentbidderId = bidderId;
		}
	}

	public void resetExpire(int days) {
		if (days == 0) {
			if (expireddate != null) {
				expireddate = null;
				updateField("expireddate", null);
			}
		} else {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, days);
			java.util.Date utlDate = cal.getTime();
			java.sql.Date temp = new java.sql.Date(utlDate.getTime());
			if (expireddate == null || !temp.toString().equalsIgnoreCase(expireddate.toString())) {
				expireddate = temp;
				updateField("expireddate", expireddate);
			}
		}
	}

	public void setFinished() {
		finisheddate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
		finished = true;

		updateFinished(finisheddate, true);
	}

	public void setUnfinished() {
		finisheddate = "";
		finished = false;

		updateFinished(finisheddate, false);
	}

	public String getAllowed() {
		return allowed.getPlayerList();
	}

	public String getDenied() {
		return denied.getPlayerList();
	}

	public void addAllowed(String name) {
		if (!isAllowedConsulting(name)) {
			allowed.put(name);
			SqlManager.addPlotAllowed(name, null, PlotManager.getIdX(id), PlotManager.getIdZ(id), world);
		}
	}

	public void addAllowed(UUID uuid) {
		if (!isAllowed(uuid)) {
			String name = allowed.put(uuid);
			SqlManager.addPlotAllowed(name, uuid, PlotManager.getIdX(id), PlotManager.getIdZ(id), world);
		}
	}

	public void addDenied(String name) {
		if (!isDeniedConsulting(name)) {
			denied.put(name);
			SqlManager.addPlotDenied(name, null, PlotManager.getIdX(id), PlotManager.getIdZ(id), world);
		}
	}

	public void addDenied(UUID uuid) {
		if (!isDenied(uuid)) {
			String name = denied.put(uuid);
			SqlManager.addPlotDenied(name, uuid, PlotManager.getIdX(id), PlotManager.getIdZ(id), world);
		}
	}

	public void removeAllowed(String name) {
		if (allowed.contains(name)) {
			UUID uuid = allowed.remove(name);
			SqlManager.deletePlotAllowed(PlotManager.getIdX(id), PlotManager.getIdZ(id), name, uuid, world);
		}
	}

	public void removeAllowedGroup(String name) {
		if (allowed.contains(name)) {
			allowed.remove(name);
			SqlManager.deletePlotAllowed(PlotManager.getIdX(id), PlotManager.getIdZ(id), name, null, world);
		}
	}

	public void removeAllowed(UUID uuid) {
		if (allowed.contains(uuid)) {
			String name = allowed.remove(uuid);
			SqlManager.deletePlotAllowed(PlotManager.getIdX(id), PlotManager.getIdZ(id), name, uuid, world);
		}
	}

	public void removeDenied(String name) {
		if (denied.contains(name)) {
			UUID uuid = denied.remove(name);
			SqlManager.deletePlotDenied(PlotManager.getIdX(id), PlotManager.getIdZ(id), name, uuid, world);
		}
	}

	public void removeDeniedGroup(String name) {
		if (denied.contains(name)) {
			denied.remove(name);
			SqlManager.deletePlotDenied(PlotManager.getIdX(id), PlotManager.getIdZ(id), name, null, world);
		}
	}

	public void removeDenied(UUID uuid) {
		if (denied.contains(uuid)) {
			String name = denied.remove(uuid);
			SqlManager.deletePlotDenied(PlotManager.getIdX(id), PlotManager.getIdZ(id), name, uuid, world);
		}
	}

	public void removeAllAllowed() {
		HashMap<String, UUID> list = allowed.getAllPlayers();
		for (String n : list.keySet()) {
			UUID uuid = list.get(n);
			SqlManager.deletePlotAllowed(PlotManager.getIdX(id), PlotManager.getIdZ(id), n, uuid, world);
		}
		allowed.clear();
	}

	public void removeAllDenied() {
		HashMap<String, UUID> list = denied.getAllPlayers();
		for (String n : list.keySet()) {
			UUID uuid = list.get(n);
			SqlManager.deletePlotDenied(PlotManager.getIdX(id), PlotManager.getIdZ(id), n, uuid, world);
		}
		denied.clear();
	}

	@Deprecated
	public boolean isAllowed(String name) {
		Player p = Bukkit.getServer().getPlayerExact(name);
		return p != null && isAllowedInternal(p.getName(), p.getUniqueId(), true, true);
	}

	public boolean isAllowedConsulting(String name) {
		@SuppressWarnings("deprecation")
		Player p = Bukkit.getServer().getPlayerExact(name);
		if (p != null) {
			return isAllowedInternal(name, p.getUniqueId(), true, true);
		}
		return isAllowedInternal(name, null, true, true);
	}

	public boolean isGroupAllowed(String name) {
		return isAllowedInternal(name, null, true, true);
	}

	public boolean isAllowed(String name, UUID uuid) {
		return isAllowedInternal(name, uuid, true, true);
	}

	public boolean isAllowed(UUID uuid) {
		return isAllowedInternal("", uuid, true, true);
	}

	@Deprecated
	public boolean isAllowed(String name, boolean IncludeStar, boolean IncludeGroup) {
		Player p = Bukkit.getServer().getPlayerExact(name);
		return p != null && isAllowedInternal(p.getName(), p.getUniqueId(), IncludeStar, IncludeGroup);
	}

	private boolean isAllowedInternal(String name, UUID uuid, boolean IncludeStar, boolean IncludeGroup) {

		if (IncludeStar && owner.equals("*")) {
			return true;
		}

		Player p = null;

		if (uuid != null) {
			p = Bukkit.getServer().getPlayer(uuid);
		}

		if (uuid != null && ownerId != null && ownerId.equals(uuid)) {
			return true;
		}
		if (uuid == null && owner.equalsIgnoreCase(name)) {
			return true;
		}

		if (IncludeGroup && owner.toLowerCase().startsWith("group:") && p != null) {
			if (p.hasPermission("plotme.group." + owner.replace("Group:", ""))) {
				return true;
			}
		}

		HashMap<String, UUID> list = allowed.getAllPlayers();
		for (String str : list.keySet()) {
			if (IncludeStar && str.equals("*")) {
				return true;
			}

			UUID u = list.get(str);
			if (u != null && uuid != null && u.equals(uuid)) {
				return true;
			}
			if (uuid == null && str.equalsIgnoreCase(name)) {
				return true;
			}

			if (IncludeGroup && str.toLowerCase().startsWith("group:") && p != null) {
				if (p.hasPermission("plotme.group." + str.replace("Group:", ""))) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isDeniedConsulting(String name) {
		@SuppressWarnings("deprecation")
		Player p = Bukkit.getServer().getPlayerExact(name);
		if (p != null) {
			return isDeniedInternal(name, p.getUniqueId());
		}
		return isDeniedInternal(name, null);
	}

	public boolean isGroupDenied(String name) {
		return isDeniedInternal(name, null);
	}

	public boolean isDenied(UUID uuid) {
		return isDeniedInternal("", uuid);
	}

	private boolean isDeniedInternal(String name, UUID uuid) {
		Player p = null;

		if (isAllowedInternal(name, uuid, false, false)) {
			return false;
		}

		if (uuid != null) {
			p = Bukkit.getServer().getPlayer(uuid);
		}

		HashMap<String, UUID> list = denied.getAllPlayers();
		for (String str : list.keySet()) {
			if (str.equals("*")) {
				return true;
			}

			UUID u = list.get(str);
			if (u != null && uuid != null && u.equals(uuid)) {
				return true;
			}
			if (uuid == null && str.equalsIgnoreCase(name)) {
				return true;
			}

			if (str.toLowerCase().startsWith("group:") && p != null) {
				if (p.hasPermission("plotme.group." + str.replace("Group:", ""))) {
					return true;
				}
			}
		}

		return false;
	}

	public Collection<String> allowed() {
		return allowed.getPlayers();
	}

	public int allowedcount() {
		return allowed.size();
	}

	public int deniedcount() {
		return denied.size();
	}

	@Override
	public int compareTo(Plot plot) {
		if (expireddate.compareTo(plot.expireddate) == 0) {
			return owner.compareTo(plot.owner);
		}
		return expireddate.compareTo(plot.expireddate);
	}

	private void updateFinished(String finishtime, boolean isfinished) {
		updateField("finisheddate", finishtime);
		updateField("finished", isfinished);
	}

	public void updateField(String field, Object value) {
		SqlManager.updatePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), world, field, value);
	}
}
