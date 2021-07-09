package com.worldcretornica.plotme;

import java.util.Set;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.sql.Date;
import org.bukkit.block.Biome;
import java.util.UUID;

public class Plot implements Comparable<Plot> {
    public String owner;
    public UUID ownerId;
    public String world;
    PlayerList allowed;
    PlayerList denied;
    public Biome biome;
    public Date expireddate;
    public boolean finished;
    public List<String[]> comments;
    public String id;
    public double customprice;
    public boolean forsale;
    public String finisheddate;
    public boolean protect;
    public boolean auctionned;
    public String currentbidder;
    public UUID currentbidderId;
    public double currentbid;
    
    public Plot() {
        this.owner = "";
        this.ownerId = null;
        this.world = "";
        this.id = "";
        this.allowed = new PlayerList();
        this.denied = new PlayerList();
        this.biome = Biome.PLAINS;
        final Calendar cal = Calendar.getInstance();
        cal.add(6, 7);
        final java.util.Date utlDate = cal.getTime();
        this.expireddate = new Date(utlDate.getTime());
        this.comments = new ArrayList<String[]>();
        this.customprice = 0.0;
        this.forsale = false;
        this.finisheddate = "";
        this.protect = false;
        this.auctionned = false;
        this.currentbidder = "";
        this.currentbid = 0.0;
        this.currentbidderId = null;
    }
    
    public Plot(final String o, final Location t, final Location b, final String tid, final int days) {
        this.owner = o;
        this.ownerId = null;
        this.world = t.getWorld().getName();
        this.allowed = new PlayerList();
        this.denied = new PlayerList();
        this.biome = Biome.PLAINS;
        this.id = tid;
        if (days == 0) {
            this.expireddate = null;
        }
        else {
            final Calendar cal = Calendar.getInstance();
            cal.add(6, days);
            final java.util.Date utlDate = cal.getTime();
            this.expireddate = new Date(utlDate.getTime());
        }
        this.comments = new ArrayList<String[]>();
        this.customprice = 0.0;
        this.forsale = false;
        this.finisheddate = "";
        this.protect = false;
        this.auctionned = false;
        this.currentbidder = "";
        this.currentbid = 0.0;
        this.currentbidderId = null;
    }
    
    public Plot(final String o, final UUID uuid, final Location t, final Location b, final String tid, final int days) {
        this.owner = o;
        this.ownerId = uuid;
        this.world = t.getWorld().getName();
        this.allowed = new PlayerList();
        this.denied = new PlayerList();
        this.biome = Biome.PLAINS;
        this.id = tid;
        if (days == 0) {
            this.expireddate = null;
        }
        else {
            final Calendar cal = Calendar.getInstance();
            cal.add(6, days);
            final java.util.Date utlDate = cal.getTime();
            this.expireddate = new Date(utlDate.getTime());
        }
        this.comments = new ArrayList<String[]>();
        this.customprice = 0.0;
        this.forsale = false;
        this.finisheddate = "";
        this.protect = false;
        this.auctionned = false;
        this.currentbidder = "";
        this.currentbid = 0.0;
        this.currentbidderId = null;
    }
    
    public Plot(final UUID o, final Location t, final Location b, final String tid, final int days) {
        this.ownerId = o;
        final Player p = Bukkit.getPlayer(o);
        if (p != null) {
            this.owner = p.getName();
        }
        else {
            this.owner = "";
        }
        this.world = t.getWorld().getName();
        this.allowed = new PlayerList();
        this.denied = new PlayerList();
        this.biome = Biome.PLAINS;
        this.id = tid;
        if (days == 0) {
            this.expireddate = null;
        }
        else {
            final Calendar cal = Calendar.getInstance();
            cal.add(6, days);
            final java.util.Date utlDate = cal.getTime();
            this.expireddate = new Date(utlDate.getTime());
        }
        this.comments = new ArrayList<String[]>();
        this.customprice = 0.0;
        this.forsale = false;
        this.finisheddate = "";
        this.protect = false;
        this.auctionned = false;
        this.currentbidder = "";
        this.currentbid = 0.0;
        this.currentbidderId = null;
    }
    
    public Plot(final String o, final String w, final int tX, final int bX, final int tZ, final int bZ, final String bio, final Date exp, final boolean fini, final PlayerList al, final List<String[]> comm, final String tid, final double custprice, final boolean sale, final String finishdt, final boolean prot, final String bidder, final Double bid, final boolean isauctionned, final PlayerList den) {
        this.owner = o;
        this.ownerId = null;
        this.world = w;
        this.biome = Biome.valueOf(bio);
        this.expireddate = exp;
        this.finished = fini;
        this.allowed = al;
        this.comments = comm;
        this.id = tid;
        this.customprice = custprice;
        this.forsale = sale;
        this.finisheddate = finishdt;
        this.protect = prot;
        this.auctionned = isauctionned;
        this.currentbidder = bidder;
        this.currentbid = bid;
        this.denied = den;
    }
    
    Plot(final String o, final UUID uuid, final String w, final int tX, final int bX, final int tZ, final int bZ, final String bio, final Date exp, final boolean fini, final PlayerList al, final List<String[]> comm, final String tid, final double custprice, final boolean sale, final String finishdt, final boolean prot, final String bidder, final UUID bidderId, final Double bid, final boolean isauctionned, final PlayerList den) {
        this.ownerId = uuid;
        this.owner = o;
        this.world = w;
        this.biome = Biome.valueOf(bio);
        this.expireddate = exp;
        this.finished = fini;
        this.allowed = al;
        this.comments = comm;
        this.id = tid;
        this.customprice = custprice;
        this.forsale = sale;
        this.finisheddate = finishdt;
        this.protect = prot;
        this.auctionned = isauctionned;
        this.currentbidder = bidder;
        this.currentbid = bid;
        this.denied = den;
        if (bidderId == null) {
            this.currentbidderId = null;
        }
        else {
            this.currentbidderId = bidderId;
        }
    }
    
    public void setExpire(final Date date) {
        if (!this.expireddate.equals(date)) {
            this.updateField("expireddate", (this.expireddate = date));
        }
    }
    
    public void resetExpire(final int days) {
        if (days == 0) {
            if (this.expireddate != null) {
                this.updateField("expireddate", (this.expireddate = null));
            }
        }
        else {
            final Calendar cal = Calendar.getInstance();
            cal.add(6, days);
            final java.util.Date utlDate = cal.getTime();
            final Date temp = new Date(utlDate.getTime());
            if (this.expireddate == null || !temp.toString().equalsIgnoreCase(this.expireddate.toString())) {
                this.updateField("expireddate", (this.expireddate = temp));
            }
        }
    }
    
    public String getExpire() {
        return DateFormat.getDateInstance().format((java.util.Date)this.expireddate);
    }
    
    public void setFinished() {
        this.finisheddate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
        this.finished = true;
        this.updateFinished(this.finisheddate, this.finished);
    }
    
    public void setUnfinished() {
        this.finisheddate = "";
        this.finished = false;
        this.updateFinished(this.finisheddate, this.finished);
    }
    
    public Biome getBiome() {
        return this.biome;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public UUID getOwnerId() {
        return this.ownerId;
    }
    
    public String getAllowed() {
        return this.allowed.getPlayerList();
    }
    
    public String getDenied() {
        return this.denied.getPlayerList();
    }
    
    public int getCommentsCount() {
        return this.comments.size();
    }
    
    public String[] getComments(final int i) {
        return (String[])this.comments.get(i);
    }
    
    public void addAllowed(final String name) {
        if (!this.isAllowedConsulting(name)) {
            this.allowed.put(name);
            SqlManager.addPlotAllowed(name, null, PlotManager.getIdX(this.id), PlotManager.getIdZ(this.id), this.world);
        }
    }
    
    public void addAllowed(final UUID uuid) {
        if (!this.isAllowed(uuid)) {
            final String name = this.allowed.put(uuid);
            SqlManager.addPlotAllowed(name, uuid, PlotManager.getIdX(this.id), PlotManager.getIdZ(this.id), this.world);
        }
    }
    
    public void addDenied(final String name) {
        if (!this.isDeniedConsulting(name)) {
            this.denied.put(name);
            SqlManager.addPlotDenied(name, null, PlotManager.getIdX(this.id), PlotManager.getIdZ(this.id), this.world);
        }
    }
    
    public void addDenied(final UUID uuid) {
        if (!this.isDenied(uuid)) {
            final String name = this.denied.put(uuid);
            SqlManager.addPlotDenied(name, uuid, PlotManager.getIdX(this.id), PlotManager.getIdZ(this.id), this.world);
        }
    }
    
    public void removeAllowed(final String name) {
        if (this.allowed.contains(name)) {
            final UUID uuid = this.allowed.remove(name);
            SqlManager.deletePlotAllowed(PlotManager.getIdX(this.id), PlotManager.getIdZ(this.id), name, uuid, this.world);
            if (PlotMe.worldeditplugin != null) {
                final Player p = Bukkit.getPlayer(uuid);
                if (p != null && PlotManager.isPlotWorld(p.getWorld())) {
                    if (!PlotMe.isIgnoringWELimit(p)) {
                        PlotMe.plotworldedit.setMask(p);
                    }
                    else {
                        PlotMe.plotworldedit.removeMask(p);
                    }
                }
            }
        }
    }
    
    public void removeAllowedGroup(final String name) {
        if (this.allowed.contains(name)) {
            this.allowed.remove(name);
            SqlManager.deletePlotAllowed(PlotManager.getIdX(this.id), PlotManager.getIdZ(this.id), name, null, this.world);
        }
    }
    
    public void removeAllowed(final UUID uuid) {
        if (this.allowed.contains(uuid)) {
            final String name = this.allowed.remove(uuid);
            SqlManager.deletePlotAllowed(PlotManager.getIdX(this.id), PlotManager.getIdZ(this.id), name, uuid, this.world);
            if (PlotMe.worldeditplugin != null) {
                final Player p = Bukkit.getPlayer(uuid);
                if (p != null && PlotManager.isPlotWorld(p.getWorld())) {
                    if (!PlotMe.isIgnoringWELimit(p)) {
                        PlotMe.plotworldedit.setMask(p);
                    }
                    else {
                        PlotMe.plotworldedit.removeMask(p);
                    }
                }
            }
        }
    }
    
    public void removeDenied(final String name) {
        if (this.denied.contains(name)) {
            final UUID uuid = this.denied.remove(name);
            SqlManager.deletePlotDenied(PlotManager.getIdX(this.id), PlotManager.getIdZ(this.id), name, uuid, this.world);
        }
    }
    
    public void removeDeniedGroup(final String name) {
        if (this.denied.contains(name)) {
            this.denied.remove(name);
            SqlManager.deletePlotDenied(PlotManager.getIdX(this.id), PlotManager.getIdZ(this.id), name, null, this.world);
        }
    }
    
    public void removeDenied(final UUID uuid) {
        if (this.denied.contains(uuid)) {
            final String name = this.denied.remove(uuid);
            SqlManager.deletePlotDenied(PlotManager.getIdX(this.id), PlotManager.getIdZ(this.id), name, uuid, this.world);
        }
    }
    
    public void removeAllAllowed() {
        final HashMap<String, UUID> list = this.allowed.getAllPlayers();
        for (final String n : list.keySet()) {
            final UUID uuid = (UUID)list.get(n);
            SqlManager.deletePlotAllowed(PlotManager.getIdX(this.id), PlotManager.getIdZ(this.id), n, uuid, this.world);
        }
        this.allowed.clear();
    }
    
    public void removeAllDenied() {
        final HashMap<String, UUID> list = this.denied.getAllPlayers();
        for (final String n : list.keySet()) {
            final UUID uuid = (UUID)list.get(n);
            SqlManager.deletePlotDenied(PlotManager.getIdX(this.id), PlotManager.getIdZ(this.id), n, uuid, this.world);
        }
        this.denied.clear();
    }
    
    public boolean isAllowed(final String name) {
        final Player p = Bukkit.getServer().getPlayerExact(name);
        return p != null && this.isAllowedInternal(p.getName(), p.getUniqueId(), true, true);
    }
    
    public boolean isAllowedConsulting(final String name) {
        final Player p = Bukkit.getServer().getPlayerExact(name);
        if (p != null) {
            return this.isAllowedInternal(name, p.getUniqueId(), true, true);
        }
        return this.isAllowedInternal(name, null, true, true);
    }
    
    public boolean isGroupAllowed(final String name) {
        return this.isAllowedInternal(name, null, true, true);
    }
    
    public boolean isAllowed(final String name, final UUID uuid) {
        return this.isAllowedInternal(name, uuid, true, true);
    }
    
    public boolean isAllowed(final UUID uuid) {
        return this.isAllowedInternal("", uuid, true, true);
    }
    
    @Deprecated
    public boolean isAllowed(final String name, final boolean IncludeStar, final boolean IncludeGroup) {
        final Player p = Bukkit.getServer().getPlayerExact(name);
        return p != null && this.isAllowedInternal(p.getName(), p.getUniqueId(), IncludeStar, IncludeGroup);
    }
    
    private boolean isAllowedInternal(final String name, final UUID uuid, final boolean IncludeStar, final boolean IncludeGroup) {
        if (IncludeStar && this.owner.equals("*")) {
            return true;
        }
        Player p = null;
        if (uuid != null) {
            p = Bukkit.getServer().getPlayer(uuid);
        }
        if (uuid != null && this.ownerId != null && this.ownerId.equals(uuid)) {
            return true;
        }
        if (uuid == null && this.owner.equalsIgnoreCase(name)) {
            return true;
        }
        if (IncludeGroup && this.owner.toLowerCase().startsWith("group:") && p != null && p.hasPermission("plotme.group." + this.owner.replace("Group:", ""))) {
            return true;
        }
        final HashMap<String, UUID> list = this.allowed.getAllPlayers();
        for (final String str : list.keySet()) {
            if (IncludeStar && str.equals("*")) {
                return true;
            }
            final UUID u = (UUID)list.get(str);
            if (u != null && uuid != null && u.equals(uuid)) {
                return true;
            }
            if (uuid == null && str.equalsIgnoreCase(name)) {
                return true;
            }
            if (IncludeGroup && str.toLowerCase().startsWith("group:") && p != null && p.hasPermission("plotme.group." + str.replace("Group:", ""))) {
                return true;
            }
        }
        return false;
    }
    
    @Deprecated
    public boolean isDenied(final String name) {
        final Player p = Bukkit.getServer().getPlayerExact(name);
        return p != null && this.isDeniedInternal(name, null, true, true);
    }
    
    public boolean isDeniedConsulting(final String name) {
        final Player p = Bukkit.getServer().getPlayerExact(name);
        if (p != null) {
            return this.isDeniedInternal(name, p.getUniqueId(), true, true);
        }
        return this.isDeniedInternal(name, null, true, true);
    }
    
    public boolean isGroupDenied(final String name) {
        return this.isDeniedInternal(name, null, true, true);
    }
    
    public boolean isDenied(final UUID uuid) {
        return this.isDeniedInternal("", uuid, true, true);
    }
    
    private boolean isDeniedInternal(final String name, final UUID uuid, final boolean IncludeStar, final boolean IncludeGroup) {
        Player p = null;
        if (this.isAllowedInternal(name, uuid, false, false)) {
            return false;
        }
        if (uuid != null) {
            p = Bukkit.getServer().getPlayer(uuid);
        }
        final HashMap<String, UUID> list = this.denied.getAllPlayers();
        for (final String str : list.keySet()) {
            if (str.equals("*")) {
                return true;
            }
            final UUID u = (UUID)list.get(str);
            if (u != null && uuid != null && u.equals(uuid)) {
                return true;
            }
            if (uuid == null && str.equalsIgnoreCase(name)) {
                return true;
            }
            if (IncludeGroup && str.toLowerCase().startsWith("group:") && p != null && p.hasPermission("plotme.group." + str.replace("Group:", ""))) {
                return true;
            }
        }
        return false;
    }
    
    public Set<String> allowed() {
        return this.allowed.getPlayers();
    }
    
    public Set<String> denied() {
        return this.denied.getPlayers();
    }
    
    public int allowedcount() {
        return this.allowed.size();
    }
    
    public int deniedcount() {
        return this.denied.size();
    }
    
    public int compareTo(final Plot plot) {
        if (this.expireddate.compareTo((java.util.Date)plot.expireddate) == 0) {
            return this.owner.compareTo(plot.owner);
        }
        return this.expireddate.compareTo((java.util.Date)plot.expireddate);
    }
    
    private void updateFinished(final String finishtime, final boolean isfinished) {
        this.updateField("finisheddate", finishtime);
        this.updateField("finished", isfinished);
    }
    
    public void updateField(final String field, final Object value) {
        SqlManager.updatePlot(PlotManager.getIdX(this.id), PlotManager.getIdZ(this.id), this.world, field, value);
    }
}
