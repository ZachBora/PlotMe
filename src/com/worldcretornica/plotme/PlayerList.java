package com.worldcretornica.plotme;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;

public class PlayerList {

    private HashMap<String, UUID> playerlist;
    
    public PlayerList() {
        playerlist = new HashMap<>();
    }
    
    public PlayerList(HashMap<String, UUID> players) {
        playerlist = players;
    }
    
    public void put(String name) {
        put(name, null);
    }
    
    public void put(String name, UUID uuid) {
        playerlist.put(name, uuid);
        
        if(uuid == null) {
            //TODO add async get and feed it
        }
    }
    
    public String put(UUID uuid) {
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        playerlist.put(name, uuid);
        return name;
    }
        
    public void remove(String name) {
        playerlist.remove(name);
    }
    
    public String remove(UUID uuid) {
        for(String name : playerlist.keySet()) {
            if(playerlist.get(name).equals(uuid)) {
                playerlist.remove(name);
                return name;
            }
        }
        return "";
    }
    
    public Set<String> getPlayers() {
        return playerlist.keySet();
    }
    
    public String getPlayerList() {
        StringBuilder list = new StringBuilder();

        for (String s : playerlist.keySet()) {
                list = list.append(s + ", ");
        }
        if (list.length() > 1) {
            list = list.delete(list.length() - 2, 2);
        }
        return list.toString();
    }
    
    public boolean contains(String name) {
        return playerlist.containsKey(name);
    }
    
    public boolean contains(UUID uuid) {
        return playerlist.values().contains(uuid);
    }
    
    public HashMap<String, UUID> getAllPlayers() {
        return playerlist;
    }
    
    public void clear() {
        playerlist.clear();
    }
    
    public int size() {
        return playerlist.size();
    }
}
