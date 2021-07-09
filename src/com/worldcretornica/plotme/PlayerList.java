package com.worldcretornica.plotme;

import java.util.Set;
import org.bukkit.Bukkit;
import java.util.UUID;
import java.util.HashMap;

public class PlayerList {
    private HashMap<String, UUID> playerlist;
    
    public PlayerList() {
        this.playerlist = new HashMap<String, UUID>();
    }
    
    public PlayerList(final HashMap<String, UUID> players) {
        this.playerlist = players;
    }
    
    public void put(final String name) {
        this.put(name, null);
    }
    
    public void put(final String name, final UUID uuid) {
        this.playerlist.put(name, uuid);
    }
    
    public String put(final UUID uuid) {
        final String name = Bukkit.getOfflinePlayer(uuid).getName();
        this.playerlist.put(name, uuid);
        return name;
    }
    
    public UUID remove(final String name) {
        String found = "";
        UUID uuid = null;
        for (final String key : this.playerlist.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                found = key;
            }
        }
        if (!found.equals("")) {
            uuid = (UUID)this.playerlist.get(found);
            this.playerlist.remove(found);
        }
        return uuid;
    }
    
    public String remove(final UUID uuid) {
        for (final String name : this.playerlist.keySet()) {
            if (((UUID)this.playerlist.get(name)).equals(uuid)) {
                this.playerlist.remove(name);
                return name;
            }
        }
        return "";
    }
    
    public Set<String> getPlayers() {
        return (Set<String>)this.playerlist.keySet();
    }
    
    public String getPlayerList() {
        StringBuilder list = new StringBuilder();
        for (final String s : this.playerlist.keySet()) {
            list = list.append(s + ", ");
        }
        if (list.length() > 1) {
            list = list.delete(list.length() - 2, list.length());
        }
        if (list.toString() == null) {
            return "";
        }
        return list.toString();
    }
    
    public boolean contains(final String name) {
        for (final String key : this.playerlist.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(final UUID uuid) {
        return this.playerlist.values().contains(uuid);
    }
    
    public HashMap<String, UUID> getAllPlayers() {
        return this.playerlist;
    }
    
    public void clear() {
        this.playerlist.clear();
    }
    
    public int size() {
        return this.playerlist.size();
    }
    
    public void replace(final UUID uuid, final String newname) {
        if (uuid != null && this.playerlist != null && this.contains(uuid)) {
            for (final String name : this.playerlist.keySet()) {
                if (this.playerlist.get(name) != null && ((UUID)this.playerlist.get(name)).equals(uuid)) {
                    this.playerlist.remove(name);
                    this.playerlist.put(newname, uuid);
                }
            }
        }
    }
    
    public void replace(final String name, final UUID newuuid) {
        if (newuuid != null && this.playerlist != null && this.contains(name)) {
            for (final String key : this.playerlist.keySet()) {
                if (key.equalsIgnoreCase(name)) {
                    this.playerlist.remove(key);
                    this.playerlist.put(name, newuuid);
                }
            }
        }
    }
}
