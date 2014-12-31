package com.gmail.alikstudio47;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.WHITE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ParkourArena implements Serializable {
	private static final long serialVersionUID = 1L;
	private transient List<ParkourPlayer> players = new ArrayList<ParkourPlayer>();
	private transient NEXTparkour plugin;
	private boolean isActive;
	private double x, y, z;
	private float pitch, yaw;
	private String world;
	private List<ParkourScore> scores = new ArrayList<ParkourScore>();

	public void setSpawnLocation(Location loc) {
		x = loc.getX();
		y = loc.getY();
		z = loc.getZ();
		world = loc.getWorld().getName();
		pitch = loc.getPitch();
		yaw = loc.getYaw();
	}

	public void resetPlayers() {
		players = new ArrayList<ParkourPlayer>();
	}

	public Location getSpawnLocation() {
		World w = Bukkit.getWorld(world);
		if (w == null)
			return null;
		Location toRet = new Location(w, x, y, z, yaw, pitch);
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
			tmp.player = player;
			tmp.startTime = System.currentTimeMillis();
			players.add(tmp);
			player.sendMessage(BLUE + "Witaj na mapie " + BOLD + getName()
					+ RESET + BLUE + "!");

			if (getScoreOf(player) != null) {
				player.sendMessage(GREEN + "Grasz juz "
						+ getScoreOf(player).timesPlayed + " raz "
						+ ", a twoj najlepszy czas to "
						+ getScoreOf(player).time + ".");
			} else {
				player.sendMessage(GOLD
						+ "Jeszcze nigdy nie doszedles do konca tej mapy. Powodzenia.");
			}
			if (getArenaBest() == null) {
				player.sendMessage(AQUA
						+ "Jeszcze nikt nie doszedl do konca tej mapy. Musisz byc pierwszy!");

			} else {
				player.sendMessage(AQUA
						+ "Najlepszy czas na tej mapie ma gracz " + BOLD
						+ getArenaBest().playerName + RESET + AQUA
						+ ", a jego czas to " + BOLD + getArenaBest().time
						+ RESET + AQUA + ".");
			}
			player.teleport(getSpawnLocation());
		}

	}

	public void removePlayer(Player player) {
		players.remove(player);

		player.sendMessage(BLUE + "Dzieki za granie na " + getName());
		player.teleport(plugin.getLobbySpawnLocation());
	}

	public Boolean containsPlayer(Player player) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).player == player) {
				return true;

			}
		}
		return false;
	}

	public ParkourScore getScoreOf(Player player) {
		return getScoreOf(player.getPlayerListName());
	}

	public ParkourScore getScoreOf(String player) {
		for (ParkourScore s : scores) {
			if (s.playerName.equals(player))
				return s;
		}

		return null;
	}

	public ParkourPlayer getPkPlayerByName(String name) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).player.getPlayerListName() == name) {
				return players.get(i);
			}
		}
		return null;
	}

	public void endReached(Player player) {
		endReached(player.getPlayerListName());
	}

	public ParkourScore getArenaBest() {
		ParkourScore currentBest = null;
		for (int i = 0; i < scores.size(); i++) {
			if (currentBest == null || scores.get(i).time < currentBest.time) {

				currentBest = scores.get(i);
			}

		}
		return currentBest;

	}

	public void endReached(String _player) {
		ParkourPlayer player = getPkPlayerByName(_player);

		if (player != null) {
			player.player
					.sendMessage(GOLD + "Dzieki za granie na " + getName());

			float time = System.currentTimeMillis() - player.startTime; // JUZ
																		// NIC
																		// NIE
																		// RUSZAC
																		// Z
																		// CZASEM
			time = time / 1000;
			float lastArenaBest;
			if (getArenaBest() != null) {
				lastArenaBest = getArenaBest().time; // to musi byc tutaj,
														// dlatego ze jak on
														// pobije rekord to
														// arenaBest sie zmieni
			} else {
				lastArenaBest = -1;

			}
			if (getScoreOf(_player) != null) {
				if (time < getScoreOf(_player).time) {
					getScoreOf(_player).time = time;
					player.player.sendMessage(BOLD
							+ "Pobiles swoj osobisty rekord!");
				}
				getScoreOf(_player).timesPlayed += 1;
			} else {
				scores.add(new ParkourScore(_player, time));
			}
			if (lastArenaBest == -1 || time < lastArenaBest) {
				
				if (lastArenaBest == -1) {

					plugin.getServer().broadcastMessage(
							GREEN + "[Parkour]" + GRAY + "Gracz " + GOLD + BOLD
									+ _player + RESET + GRAY
									+ " ukonczyl jako pierwszy parkour " + GOLD
									+ BOLD + getName() + RESET + GRAY + ".");
				} else {
					
					plugin.getServer().broadcastMessage(
							GREEN + "[Parkour]" + GRAY + "Gracz " + GOLD + BOLD
									+ _player + RESET + GRAY
									+ " pobil czas parkourze " + GOLD + BOLD
									+ getName() + RESET + GRAY
									+ ". Jego wynik to " + BOLD + RED + time
									+ "s");

				}
			}
			player.player.sendMessage(niceEndMsg(getScoreOf(_player), time));

			players.remove(player); // gracz musi byc usuniety bo bugi :D

			player.player.teleport(plugin.getLobbySpawnLocation());
			plugin.saveArenas();
		}

	}

	public void registerCheckpoint(Player player) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).player.equals(player)) {
				player.sendMessage(AQUA + "Zdobyles checkpoint!");
				// players.get(i).setLastCheckpoint(
				// new ParkourCheckpoint(player.getLocation()));
			}

		}

	}

	private String niceEndMsg(ParkourScore score, float currentTime) {

		return GOLD + "»   " + GRAY + "Parkour " + DARK_GREEN + BOLD + name
				+ GRAY + " ukonczony z czasem: " + WHITE + currentTime + GRAY
				+ "s.";
	}

	public void restart(Player player) {
		this.getPkPlayerByName(player.getPlayerListName()).startTime = System.currentTimeMillis();
		player.teleport(getSpawnLocation());
		
	}
}