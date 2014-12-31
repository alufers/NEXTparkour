package com.gmail.alikstudio47;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler {
	private NEXTparkour plugin;

	public CommandHandler(NEXTparkour plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("nextparkour")
				|| cmd.getName().equalsIgnoreCase("nparkour")
				|| cmd.getName().equalsIgnoreCase("np")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(RED
						+ "Ta komenda musi byc wykonana przez gracza.");
				return true;
			}

			Player player = (Player) sender;

			if (args.length <= 0) {
				plugin.showHelp(sender);
				return true;
			}

			if (!player.hasPermission("np.use")
					|| !player.hasPermission("np." + args[0].toLowerCase())) {
				sender.sendMessage(RED
						+ "Nie masz uprawnien do wykonania tej czynnosci.");
				return true;
			}

			if (args.length > 0) {
				if (!plugin.validateCommands(player, args[0],
						"help|join|leave|list"))
					return true;

				if (args[0].equalsIgnoreCase("help"))
					plugin.showHelp(sender);
				else if (args[0].equalsIgnoreCase("join"))
					if (args.length == 2)
						plugin.joinArena(player, args[1]);
					else
						sender.sendMessage(RED
								+ "Niewlasciwa ilosc parametrow.");
				else if (args[0].equalsIgnoreCase("leave"))
					if (args.length == 1)
						plugin.leaveArena(player);
					else
						sender.sendMessage(RED
								+ "Niewlasciwa ilosc parametrow.");
				else if (args[0].equalsIgnoreCase("list"))
					if (args.length == 1)
						plugin.listArenas(player);
					else
						sender.sendMessage(RED
								+ "Niewlasciwa ilosc parametrow.");

			} else
				plugin.showHelp(sender);

			return true;
		}

		// START ADMIN
		if (cmd.getName().equalsIgnoreCase("nextparkouradmin")
				|| cmd.getName().equalsIgnoreCase("nparkouradmin")
				|| cmd.getName().equalsIgnoreCase("npa")) {

			if (!(sender instanceof Player)) {
				sender.sendMessage(RED
						+ "Ta komenda musi byc wykonana przez gracza.");
				return true;
			}

			if (args.length <= 0) {
				plugin.showAdminHelp(sender);
				return true;
			}

			else {
				Player player = (Player) sender;

				if (!player.hasPermission("np.admin")
						|| !player.hasPermission("np.admin."
								+ args[0].toLowerCase())) {
					sender.sendMessage(RED
							+ "Nie masz uprawnien do wykonania tej czynnosci.");
					return true;
				}
				if (args.length > 0) {
					// subcommands
					ParkourArena tmpArena;
					if (args[0].equalsIgnoreCase("help"))
						plugin.showAdminHelp(sender);
					else if (args[0].equalsIgnoreCase("create"))
						if (args.length == 2)
							plugin.createArena(player, args[1]);
						else
							sender.sendMessage(RED
									+ "Niewlasciwa ilosc parametrow.");
					else if (args[0].equalsIgnoreCase("delete"))
						if (args.length == 2)
							plugin.deleteArena(player, args[1]);
						else
							sender.sendMessage(RED
									+ "Niewlasciwa ilosc parametrow.");
					else if (args[0].equalsIgnoreCase("setlobby"))
						if (args.length == 1)
							plugin.setLobby(player);
						else
							sender.sendMessage(RED
									+ "Niewlasciwa ilosc parametrow.");
					else if (args[0].equalsIgnoreCase("setarenaspawn"))
						if (args.length == 2) {
							if (plugin.findArena(args[1]) == null) {

								sender.sendMessage(RED + "Arena nie istnieje.");
								return true;
							}
							tmpArena = plugin.findArena(args[1]);
							tmpArena.setSpawnLocation(player.getLocation());
							plugin.saveArenas();
							sender.sendMessage(GREEN
									+ "Ustawiono nowy spawn n arenie.");
						} else
							sender.sendMessage(RED
									+ "Niewlasciwa ilosc parametrow.");
					else if (args[0].equalsIgnoreCase("setarenadeathblock"))
						if (args.length == 3) {
							if (plugin.findArena(args[1]) == null) {

								sender.sendMessage(RED + "Arena nie istnieje.");
								return true;
							}
							if (Material.getMaterial(args[2]) == null) {
								sender.sendMessage(RED + "Nie ma takiego bloku.");
								return true;
							}
							tmpArena = plugin.findArena(args[1]);
							tmpArena.setDeathBlock(Material
									.getMaterial(args[2]));
							plugin.saveArenas();
							sender.sendMessage(GREEN
									+ "Ustawiono nowy blok smierci.");
						} else
							sender.sendMessage(RED
									+ "Niewlasciwa ilosc parametrow.");
					else if (args[0].equalsIgnoreCase("clearscores"))
						if (args.length == 2) {
							if (plugin.findArena(args[1]) == null) {

								sender.sendMessage(RED + "Arena nie istnieje.");
								return true;
							}
							tmpArena = plugin.findArena(args[1]);
							tmpArena.clearScores();
							plugin.saveArenas();
							sender.sendMessage(GREEN + "Wyczyszczono wyniki.");
							plugin.saveArenas();
						} else
							sender.sendMessage(RED
									+ "Niewlasciwa ilosc parametrow.");
					else if (args[0].equalsIgnoreCase("clear"))
						if (args.length == 1) {
							File file = new File("arenas.dat");
							file.delete();
							plugin.loadArenas();
							sender.sendMessage(GREEN
									+ "Usunieto wszystkie areny i wyniki.");
						} else
							sender.sendMessage(RED
									+ "Niewlasciwa ilosc parametrow.");
					else if (args[0].equalsIgnoreCase("setactive")) {
						if (args.length == 3) {
							if (args[2].equals("true")
									|| args[2].equals("false")) {
								ParkourArena arena = plugin.findArena(args[1]);
								if (arena != null) {
									arena.setActive(Boolean
											.parseBoolean(args[2]));
									plugin.saveArenas();
									sender.sendMessage(GREEN + "Gotowe.");
								} else
									sender.sendMessage(RED
											+ "Arena nie istnieje. "
											+ GREEN
											+ "Czy chodzilo ci o: "
											+ BOLD
											+ plugin.findClosestArenaName("name")
											+ GREEN + "?");
							} else
								sender.sendMessage(RED + "Wartosc musi byc "
										+ GREEN + "true" + RED + " lub "
										+ GREEN + "false");
						} else
							sender.sendMessage(RED
									+ "Niewlasciwa ilosc parametrow.");
					} else {
						plugin.showAdminHelp(sender);
						return true;
					}
				}
			}
		}
		return true;
	}
}
