package com.gmail.alikstudio47;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class NEXTparkour extends JavaPlugin implements Listener {
	private List<ParkourArena> arenas = new ArrayList<ParkourArena>();
	private PlayerEventListeners playerListener;
	private CommandHandler cmdHandle;

	public void onEnable() {
		cmdHandle = new CommandHandler(this);
		playerListener = new PlayerEventListeners(this);
		getServer().getPluginManager().registerEvents(playerListener, this);
		loadConfiguration();
		getLogger().info("Loading arenas ...");
		loadArenas();
	}

	public void onDisable() {
		// getLogger().info("onDisable uruchomiony!");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		return cmdHandle.onCommand(sender, cmd, label, args);
	}

	public boolean validateCommands(Player player, String current,
			String available) {
		String[] splitted = available.split("\\|");

		for (int i = 0; i < splitted.length; i++) {
			if (splitted[i].equalsIgnoreCase(current))
				return true;
		}

		player.sendMessage(RED + "Nie znaleziono takiej komendy.");
		player.sendMessage(GREEN + "Czy chodzilo ci o: " + BOLD + "/np "
				+ StringUtils.findClosestString(current, splitted));
		player.sendMessage(GREEN + "Wpisz " + AQUA + "/np help" + GREEN
				+ ", aby uzyskac pomoc.");

		return false;
	}

	public String findClosestArenaName(String invalid) {
		String[] names = new String[arenas.size()];

		for (int i = 0; i < arenas.size(); i++) {
			names[i] = arenas.get(i).getName();
		}

		return StringUtils.findClosestString(invalid, names);
	}

	public void createArena(Player player, String name) {

		if (findArena(name) != null)
			player.sendMessage(RED + "Arena o tej nazwie juz istnieje");
		else {
			ParkourArena arenaToCreate = new ParkourArena(this);
			arenaToCreate.setSpawnLocation(player.getLocation());
			arenaToCreate.setName(name);
			arenas.add(arenaToCreate);
			saveArenas();
			player.sendMessage(GREEN
					+ "Utworzono arene. Aby ja aktywowac nalezy wpisac: "
					+ DARK_GREEN + "/npa setactive " + name + " true");
		}

	}

	public void listArenas(Player player) {
		player.sendMessage(DARK_AQUA + "===" + DARK_RED + "LISTA AREN " + AQUA
				+ "NEXTparkour" + GRAY + " by " + AQUA + "alufers i mibac138 "
				+ GOLD + "v1.0" + DARK_AQUA + "===");

		for (ParkourArena arena : arenas)
			if (arena.isActive())
				player.sendMessage(YELLOW + arena.getName());
			else
				player.sendMessage(RED + "[Nieaktywna]" + DARK_RED
						+ arena.getName());
	}

	public void joinArena(Player player, String name) {
		ParkourArena foundArena = findArena(name);
		ParkourArena foundArena1 = findPlayer(player);

		if (foundArena != null)
			if (foundArena1 == null)
				foundArena.addPlayer(player);
			else
				player.sendMessage(RED + "Jestes na innej arenie.");
		else
			player.sendMessage(RED
					+ "Ta arena nie istnieje. Czy chodzilo Ci o: " + BOLD
					+ findClosestArenaName(name) + GREEN + " ?");
	}

	public void leaveArena(Player player) {
		ParkourArena foundArena = findPlayer(player);

		if (foundArena != null)
			foundArena.removePlayer(player);
		else
			player.sendMessage(RED + "Nie jestes na zadnej arenie.");
	}

	public void deleteArena(Player player, String name) {
		if (findArena(name) != null) {
			arenas.remove(findArena(name));
			saveArenas();
			player.sendMessage(GREEN + "Usunieto.");
		} else
			player.sendMessage(RED
					+ "Ta arena nie istnieje. Czy chodzilo Ci o: " + BOLD
					+ findClosestArenaName(name) + GREEN + " ?");
	}

	public void setLobby(Player player) {
		getConfig().set("config.lobbySpawnLocation.world",
				player.getLocation().getWorld().getName());
		getConfig().set("config.lobbySpawnLocation.x",
				player.getLocation().getX());
		getConfig().set("config.lobbySpawnLocation.y",
				player.getLocation().getY());
		getConfig().set("config.lobbySpawnLocation.z",
				player.getLocation().getZ());
		saveConfig();
		loadConfiguration();

		player.sendMessage(GREEN + "Ustawiono lokalizacje lobby.");
	}

	public ParkourArena findArena(String arenaName) {
		for (int i = 0; i < arenas.size(); i++) {
			if (arenas.get(i).getName().equalsIgnoreCase(arenaName)) {
				return arenas.get(i);
			}
		}
		return null;
	}

	public ParkourArena findPlayer(Player player) {
		for (int i = 0; i < arenas.size(); i++) {
			if (arenas.get(i).containsPlayer(player))
				return arenas.get(i);
		}
		return null;
	}

	public void loadConfiguration() {
		getConfig().addDefault("config.lobbySpawnLocation.world", "world");
		getConfig().addDefault("config.lobbySpawnLocation.x", "0");
		getConfig().addDefault("config.lobbySpawnLocation.y", "0");
		getConfig().addDefault("config.lobbySpawnLocation.z", "0");

		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public Location getLobbySpawnLocation() {
		World w = Bukkit.getServer().getWorld(
				getConfig().getString("config.lobbySpawnLocation.world"));
		Location loc = new Location(w, getConfig().getDouble(
				"config.lobbySpawnLocation.x"), getConfig().getDouble(
				"config.lobbySpawnLocation.y"), getConfig().getDouble(
				"config.lobbySpawnLocation.z"));

		return loc;
	}

	public void showHelp(CommandSender player) {
		player.sendMessage(DARK_AQUA + "===" + AQUA + "NEXTparkour" + GRAY
				+ " by " + AQUA + "alufers " + GOLD
				+ getDescription().getVersion() + DARK_AQUA + "===");
		player.sendMessage(DARK_GREEN + "/np join [nazwaAreny] " + GREEN
				+ "Wchodzisz na arene");
		player.sendMessage(DARK_GREEN + "/np leave             " + GREEN
				+ "Opuszczasz arene");
		player.sendMessage(DARK_GREEN + "/np list              " + GREEN
				+ "Pokazujesz dostepne areny");

	}

	public void showAdminHelp(CommandSender player) {
		player.sendMessage(DARK_AQUA + "===" + AQUA + "NEXTparkour" + GRAY
				+ " by " + AQUA + "alufers " + GOLD
				+ getDescription().getVersion() + YELLOW
				+ "[MODUL ADMINISTRACYJNY]" + DARK_AQUA + "===");
		player.sendMessage(DARK_GREEN + "/npa create <nazwaAreny> " + GREEN
				+ "Tworzysz arene");
		player.sendMessage(DARK_GREEN + "/npa delete <nazwaAreny> " + GREEN
				+ "Usuwasz arene");
		player.sendMessage(DARK_GREEN
				+ "/npa kick <nazwaAreny>                                   "
				+ GREEN
				+ "Wyrzucasz wszystkich graczy. Niepodanie argumentu powoduje wyrzucenie z wszystkich aren.");
		player.sendMessage(DARK_GREEN
				+ "/npa setlobby                                            "
				+ GREEN + "Ustawia lobby w miejscu, w ktorym stoisz.");
		player.sendMessage(DARK_GREEN
				+ "/npa setactive <nazwaAreny> <true|false>                                            "
				+ GREEN + "Aktywuje lub deaktywuje arene.");
		player.sendMessage(DARK_GREEN
				+ "/npa clearscores <nazwaAreny>                                            "
				+ GREEN + "Czysci wyniki w danej arenie.");
		player.sendMessage(DARK_GREEN
				+ "/npa setarenaspawn <nazwaAreny>                                            "
				+ GREEN + "Ustawia spawn areny w miejscu, w ktorym stoisz.");

	}

	public void loadArenas() {
		File f = new File("arenas.dat");
		if (!f.exists())
			saveArenas();

		FileInputStream fin = null;

		try {
			fin = new FileInputStream("arenas.dat");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ObjectInputStream ois = null;

		try {
			ois = new ObjectInputStream(fin);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			@SuppressWarnings("unchecked")
			HashMap<Integer, ParkourArena> hm = (HashMap<Integer, ParkourArena>) ois
					.readObject();

			arenas = new ArrayList<ParkourArena>();

			Iterator<Entry<Integer, ParkourArena>> it = hm.entrySet()
					.iterator();

			while (it.hasNext()) {
				Map.Entry<Integer, ParkourArena> pairs = (Map.Entry<Integer, ParkourArena>) it
						.next();

				pairs.getValue().resetPlayers();
				pairs.getValue().setPlugin(this);
				arenas.add((ParkourArena) pairs.getValue());

				it.remove();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveArenas() {
		getLogger().info("Saving arenas! " + arenas.size());
		HashMap<Integer, ParkourArena> hm = new HashMap<Integer, ParkourArena>();

		for (int i = 0; i < arenas.size(); i++) {
			hm.put(i, arenas.get(i));
		}
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream("arenas.dat");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(fout);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			oos.writeObject(hm);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
