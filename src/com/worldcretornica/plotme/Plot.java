package com.worldcretornica.plotme;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

public class Plot implements Comparable<Plot> {

    public String owner;
    public UUID ownerId;
    public String world;
    private HashSet<String> allowed;
    private HashSet<String> denied;
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
        owner = "";
        ownerId = null;
        world = "";
        id = "";
        allowed = new HashSet<>();
        denied = new HashSet<>();
        biome = Biome.PLAINS;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 7);
        java.util.Date utlDate = cal.getTime();
        expireddate = new java.sql.Date(utlDate.getTime());

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

    public Plot(String o, Location t, Location b, String tid, int days) {
        ownerId = getUUID(o);
        owner = o;
        world = t.getWorld().getName();
        allowed = new HashSet<>();
        denied = new HashSet<>();
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

    public Plot(UUID o, Location t, Location b, String tid, int days) {
        ownerId = o;
        owner = Bukkit.getPlayer(o).getName();
        world = t.getWorld().getName();
        allowed = new HashSet<>();
        denied = new HashSet<>();
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

    @Deprecated
    public Plot(String o, String w, int tX, int bX, int tZ, int bZ, String bio, Date exp, boolean fini, HashSet<String> al, List<String[]> comm, String tid, double custprice, boolean sale, String finishdt, boolean prot, String bidder, Double bid, boolean isauctionned, HashSet<String> den) {
        ownerId = getUUID(o);
        owner = o;
        world = w;
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
        currentbidderId = getUUID(bidder);
    }

    public Plot(String o, UUID uuid, String w, int tX, int bX, int tZ, int bZ, String bio, Date exp, boolean fini, HashSet<String> al, List<String[]> comm, String tid, double custprice, boolean sale, String finishdt, boolean prot, String bidder, UUID bidderId, Double bid, boolean isauctionned, HashSet<String> den) {
        ownerId = uuid;
        if(uuid == null)
            owner = o;
        else
            owner = Bukkit.getPlayer(uuid).getName();
        world = w;
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
        if(bidderId == null)
            currentbidderId = null;
        else
            currentbidderId = bidderId;
    }

    public void setExpire(Date date) {
        if (!expireddate.equals(date)) {
            expireddate = date;
            updateField("expireddate", expireddate);
        }
    }

    public void resetExpire(int days) {
        if (days == 0) {
            if (expireddate != null) {
                expireddate = null;
                updateField("expireddate", expireddate);
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

    public String getExpire() {
        return DateFormat.getDateInstance().format(expireddate);
    }

    public void setFinished() {
        finisheddate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
        finished = true;

        updateFinished(finisheddate, finished);
    }

    public void setUnfinished() {
        finisheddate = "";
        finished = false;

        updateFinished(finisheddate, finished);
    }

    public Biome getBiome() {
        return biome;
    }

    public String getOwner() {
        return owner;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getAllowed() {
        return getAllowedOrDenied(allowed);
    }

    public String getDenied() {
        return getAllowedOrDenied(denied);
    }

    private String getAllowedOrDenied(HashSet<String> listAllowedOrDenied) {
        String list = "";

        for (String s : listAllowedOrDenied) {
            UUID uuid = getUUID(s);
            if (uuid != null) {
                list = list + Bukkit.getPlayer(UUID.fromString(s)) + ", ";
            } else {
                list = list + s + ", ";
            }
        }
        if (list.length() > 1) {
            list = list.substring(0, list.length() - 2);
        }
        return list;
    }

    public int getCommentsCount() {
        return comments.size();
    }

    public String[] getComments(int i) {
        return comments.get(i);
    }

    @Deprecated
    public void addAllowed(String name) {
        if (!isAllowed(name)) {
            UUID uuid = getUUID(name);
            if(uuid == null) {
                allowed.add(name);
            } else {
                allowed.add(uuid.toString());
            }
            SqlManager.addPlotAllowed(name, uuid, PlotManager.getIdX(id), PlotManager.getIdZ(id), world);
        }
    }
    
    public void addAllowedGroup(String name) {
        if (!isGroupAllowed(name)) {
            allowed.add(name);
            SqlManager.addPlotAllowed(name, null, PlotManager.getIdX(id), PlotManager.getIdZ(id), world);
        }
    }

    @SuppressWarnings("deprecation")
    public void addAllowed(UUID uuid) {
        if (!isAllowed(uuid)) {
            allowed.add(uuid.toString());
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            SqlManager.addPlotAllowed(name, uuid, PlotManager.getIdX(id), PlotManager.getIdZ(id), world);
        }
    }

    @Deprecated
    public void addDenied(String name) {
        if (!isDenied(name)) {
            UUID uuid = getUUID(name);
            if(uuid == null) {
                denied.add(name);
            } else {
                denied.add(uuid.toString());
            }
            SqlManager.addPlotDenied(name, uuid, PlotManager.getIdX(id), PlotManager.getIdZ(id), world);
        }
    }
    
    public void addDeniedGroup(String name) {
        if (!isGroupDenied(name)) {
            denied.add(name);
            SqlManager.addPlotDenied(name, null, PlotManager.getIdX(id), PlotManager.getIdZ(id), world);
        }
    }

    @SuppressWarnings("deprecation")
    public void addDenied(UUID uuid) {
        if (!isDenied(uuid)) {
            denied.add(uuid.toString());
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            SqlManager.addPlotDenied(name, uuid, PlotManager.getIdX(id), PlotManager.getIdZ(id), world);
        }
    }

    @Deprecated
    public void removeAllowed(String name) {
        if (allowed.contains(name)) {
            allowed.remove(name);
            UUID uuid = getUUID(name);
            SqlManager.deletePlotAllowed(PlotManager.getIdX(id), PlotManager.getIdZ(id), name, uuid, world);
        }
    }
    
    public void removeAllowedGroup(String name) {
        if (allowed.contains(name)) {
            allowed.remove(name);
            SqlManager.deletePlotAllowed(PlotManager.getIdX(id), PlotManager.getIdZ(id), name, null, world);
        }
    }

    @SuppressWarnings("deprecation")
    public void removeAllowed(UUID uuid) {
        if (allowed.contains(uuid.toString())) {
            allowed.remove(uuid.toString());
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            SqlManager.deletePlotAllowed(PlotManager.getIdX(id), PlotManager.getIdZ(id), name, uuid, world);
        }
    }

    @Deprecated
    public void removeDenied(String name) {
        if (denied.contains(name)) {
            denied.remove(name);
            SqlManager.deletePlotDenied(PlotManager.getIdX(id), PlotManager.getIdZ(id), name, world);
        }
    }
    
    public void removeDeniedGroup(String name) {
        if (denied.contains(name)) {
            denied.remove(name);
            SqlManager.deletePlotDenied(PlotManager.getIdX(id), PlotManager.getIdZ(id), name, null, world);
        }
    }

    @SuppressWarnings("deprecation")
    public void removeDenied(UUID uuid) {
        if (denied.contains(uuid.toString())) {
            denied.remove(uuid.toString());
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            SqlManager.deletePlotDenied(PlotManager.getIdX(id), PlotManager.getIdZ(id), name, uuid, world);
        }
    }

    public void removeAllAllowed() {
        for (String n : allowed) {
            UUID uuid = getUUID(n);
            SqlManager.deletePlotAllowed(PlotManager.getIdX(id), PlotManager.getIdZ(id), n, uuid, world);
        }
        allowed = new HashSet<>();
    }

    public void removeAllDenied() {
        for (String n : denied) {
            UUID uuid = getUUID(n);
            SqlManager.deletePlotDenied(PlotManager.getIdX(id), PlotManager.getIdZ(id), n, uuid, world);
        }
        denied = new HashSet<>();
    }

    @Deprecated
    public boolean isAllowed(String name) {
        return isAllowedInternal(name, true, true);
    }
    
    public boolean isGroupAllowed(String name) {
        return isAllowedInternal(name, true, true);
    }

    public boolean isAllowed(UUID id) {
        return isAllowedInternal(id.toString(), true, true);
    }

    @Deprecated
    public boolean isAllowed(String name, boolean IncludeStar, boolean IncludeGroup) {
        return isAllowedInternal(name, IncludeStar, IncludeGroup);
    }
    
    private boolean isAllowedInternal(String name, boolean IncludeStar, boolean IncludeGroup) {
        UUID uuid = getUUID(name);
        Player p = null;

        if (uuid != null) {
            p = Bukkit.getServer().getPlayer(uuid);
        }

        if (uuid != null && ownerId.equals(uuid) || (IncludeStar && owner.equals("*")))
            return true;

        if (IncludeGroup && owner.toLowerCase().startsWith("group:") && p != null)
            if (p.hasPermission("plotme.group." + owner.replace("Group:", "")))
                return true;

        for (String str : allowed) {
            if (uuid != null && str.equals(uuid.toString()) || (IncludeStar && str.equals("*")))
                return true;

            if (IncludeGroup && str.toLowerCase().startsWith("group:") && p != null)
                if (p.hasPermission("plotme.group." + str.replace("Group:", "")))
                    return true;
        }

        return false;
    }

    @Deprecated
    public boolean isDenied(String name) {
        return isDeniedInternal(name, true, true);
    }
    
    public boolean isGroupDenied(String name) {
        return isDeniedInternal(name, true, true);
    }

    public boolean isDenied(UUID id) {
        return isDeniedInternal(id.toString(), true, true);
    }
    
    private boolean isDeniedInternal(String name, boolean IncludeStar, boolean IncludeGroup) {
        UUID uuid = getUUID(name);
        Player p = null;

        if (uuid != null) {
            p = Bukkit.getServer().getPlayer(uuid);
        }

        if (isAllowedInternal(name, false, false))
            return false;

        for (String str : denied) {
            if (uuid != null && str.equals(uuid.toString()) || str.equals("*"))
                return true;

            if (str.toLowerCase().startsWith("group:") && p != null)
                if (p.hasPermission("plotme.group." + str.replace("Group:", "")))
                    return true;
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    public HashSet<String> allowed() {
        HashSet<String> newlist = new HashSet<>();
        
        for(String s : allowed) {
            UUID uuid = getUUID(s);
            if(uuid == null) {
                newlist.add(s);
            } else {
                newlist.add(Bukkit.getOfflinePlayer(uuid).getName());
            }
        }
        
        return newlist;
    }

    @SuppressWarnings("deprecation")
    public HashSet<String> denied() {
        HashSet<String> newlist = new HashSet<>();
        
        for(String s : denied) {
            UUID uuid = getUUID(s);
            if(uuid == null) {
                newlist.add(s);
            } else {
                newlist.add(Bukkit.getOfflinePlayer(uuid).getName());
            }
        }
        
        return newlist;
    }

    public int allowedcount() {
        return allowed.size();
    }

    public int deniedcount() {
        return denied.size();
    }

    public int compareTo(Plot plot) {
        if (expireddate.compareTo(plot.expireddate) == 0)
            return owner.compareTo(plot.owner);
        else
            return expireddate.compareTo(plot.expireddate);
    }

    private void updateFinished(String finishtime, boolean isfinished) {
        updateField("finisheddate", finishtime);
        updateField("finished", isfinished);
    }

    public void updateField(String field, Object value) {
        SqlManager.updatePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), world, field, value);
    }

    private UUID getUUID(String name) {
        try {
            UUID uuid = UUID.fromString(name);
            return uuid;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
