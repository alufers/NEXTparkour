package com.gmail.alikstudio47;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.layout.PatternLayout;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class ParkourArena implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8900730653450176545L;
	private transient List<ParkourPlayer> players = new ArrayList<ParkourPlayer>();
	private transient NEXTparkour plugin;
	private boolean isActive;
	private double x, y, z;
	private String world;
	private List<ParkourScore> scores = new ArrayList<ParkourScore>();

	public void setSpawnLocation(Location loc) {
		x = loc.getX();
		y = loc.getY();
		z = loc.getZ();
		world = loc.getWorld().getName();
	}

	public void resetPlayers() {
		players = new ArrayList<ParkourPlayer>();
	}

	public Location getSpawnLocation() {
		World w = Bukkit.getWorld(world);
		if (w == null)
			return null;
		Location toRet = new Location(w, x, y, z);
		return toRet;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public ParkourArena(NEXTparkour plugin) {

		this.plugin = plugin;
	}

	public void setPlugin(NEXTparkour plugin) {

		this.plugin = plugin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String name;

	public void addPlayer(Player player) {
		if (!isActive()) {
			player.sendMessage(ChatColor.RED + "Mapa nieaktywna lub w budowie!");
			return;
		}
		if (players.contains(player)) {
			player.sendMessage(ChatColor.RED + "Ju� grasz na tej mapie!");
		} else {
			ParkourPlayer tmp = new ParkourPlayer();
			tmp.setPlayer(player);
			tmp.setStartTime(System.currentTimeMillis());
			players.add(tmp);
			player.sendMessage(ChatColor.BLUE + "Witaj na mapie " + getName());
			if (getPlayersScore(player.getPlayerListName()) != null) {
				player.sendMessage(ChatColor.GREEN + "Grasz juz tutaj po raz " + getPlayersScore(player.getPlayerListName()).getTimesPlayed() + ",a twoj najlepszy czas to " +getPlayersScore(player.getPlayerListName()).getTime() + "." );
			}
			else
			{
				
				player.sendMessage(ChatColor.BLUE + "Jeszcze nigdy nie doszedles do konca tej mapy. Powodzenia.");
			}
			player.teleport(getSpawnLocation());
		}

	}

	public void removePlayer(Player player) {

		for (int i = 0; i < players.size(); i++) {

			if (players.get(i).getPlayer().equals(player)) {

				players.remove(i);
			}

		}
		player.sendMessage(ChatColor.BLUE + "Dzi�ki za granie na " + getName());
		player.teleport(plugin.getLobbySpawnLocation());

	}

	public Boolean containsPlayer(Player player) {

		for (int i = 0; i < players.size(); i++) {

			if (players.get(i).getPlayer().equals(player)) {

				return true;
			}

		}
		return false;
	}

	public ParkourScore getPlayersScore(String playerName) {
		for (int i = 0; i < scores.size(); i++) {

			if (scores.get(i).getPlayerName().equals(playerName)) {
				return scores.get(i);
			}
		}
		return null;

	}

	public void endReached(Player player) {

		for (int i = 0; i < players.size(); i++) {

			if (players.get(i).getPlayer().equals(player)) {
				player.sendMessage(ChatColor.GOLD + "Dzieki za granie na "
						+ getName());
				float time = (float) (System.currentTimeMillis() - players.get(
						i).getStartTime()) / 1000;
				if (getPlayersScore(player.getPlayerListName()) != null) {
					getPlayersScore(player.getPlayerListName()).setTime(time);
					getPlayersScore(player.getPlayerListName()).setTimesPlayed(getPlayersScore(player.getPlayerListName()).getTimesPlayed() + 1);
				} else {
					scores.add(new ParkourScore(player.getPlayerListName(), time));
				}
				player.sendMessage(ChatColor.YELLOW + "Twoj czas: " + time);
				players.remove(i);

				player.teleport(plugin.getLobbySpawnLocation());
			}

		}
	}

	public void registerCheckpoint(Player player) {
		for (int i = 0; i < players.size(); i++) {

			if (players.get(i).getPlayer().equals(player)) {

				player.sendMessage(ChatColor.AQUA + "Zdobyles checkpoint!");
				// players.get(i).setLastCheckpoint(
				// new ParkourCheckpoint(player.getLocation()));

			}

		}

	}

}
