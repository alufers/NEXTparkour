package com.gmail.alikstudio47;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ParkourArena implements Serializable {
	private static final long serialVersionUID = 1L;
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
			player.sendMessage(RED + "Mapa nieaktywna lub w budowie!");
			return;
		}
		if (players.contains(player))
			player.sendMessage(RED + "Juz grasz na tej mapie!");
		else {
			ParkourPlayer tmp = new ParkourPlayer();
			tmp.setPlayer(player);
			tmp.setStartTime(System.currentTimeMillis());
			players.add(tmp);
			player.sendMessage(BLUE + "Witaj na mapie " + getName());
			if (getScoreOf(player) != null) {
				player.sendMessage(GREEN + "Grasz juz "
						+ getScoreOf(player).timesPlayed + " raz "
						+ ", a twoj najlepszy czas to "
						+ getScoreOf(player).time + ".");
			} else {
				player.sendMessage(BLUE
						+ "Jeszcze nigdy nie doszedles do konca tej mapy. Powodzenia.");
			}

			player.teleport(getSpawnLocation());
		}

	}

	public void removePlayer(Player player) {
		players.remove(player);

		player.sendMessage(BLUE + "Dzi�ki za granie na " + getName());
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

	/*
	 * @Deprecated public ParkourScore getPlayersScore ( String playerName ) {
	 * for ( int i = 0; i < scores.size( ); i++ ) { if ( scores.get( i
	 * ).getPlayerName( ).equals( playerName ) ) { return scores.get( i ); } }
	 * return null; }
	 */

	public ParkourScore getScoreOf(Player player) {
		for (int i = 0; i < scores.size(); i++) {
			if (scores.get(i).playerName.equals(player.getPlayerListName())) {
				return scores.get(i);
			}
		}
		return null;
	}

	public void endReached(Player _player) {
		ParkourPlayer player = players.get(players.indexOf(_player));

		if (player != null) {
			_player.sendMessage(GOLD + "Dzieki za granie na " + getName());

			float time = (float) ((System.currentTimeMillis() - player
					.getStartTime()) / 1000);

			if (getScoreOf(_player) != null) {
				getScoreOf(_player).time = time;
				getScoreOf(_player).timesPlayed += 1;
			} else {
				scores.add(new ParkourScore(_player.getPlayerListName(), time));
			}

			_player.sendMessage(YELLOW + "Twoj czas: " + time);

			players.remove(player); // gracz musi byc usuniety bo bugi :D

			_player.teleport(plugin.getLobbySpawnLocation());
		}

	}

	public void registerCheckpoint(Player player) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getPlayer().equals(player)) {
				player.sendMessage(AQUA + "Zdobyles checkpoint!");
				// players.get(i).setLastCheckpoint(
				// new ParkourCheckpoint(player.getLocation()));

			}

		}

	}

}
