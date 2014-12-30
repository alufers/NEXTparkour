package com.gmail.alikstudio47;

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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NEXTparkour extends JavaPlugin implements Listener {
	private List< ParkourArena > arenas = new ArrayList< ParkourArena >( );

	public void onEnable ( ) {
		getServer( ).getPluginManager( ).registerEvents( this, this );
		loadConfiguration( );
		getLogger( ).info( "Loading arenas!" );
		loadArenas( );
	}

	public void onDisable ( ) {
		// getLogger().info("onDisable uruchomiony!");
	}

	public boolean validateCommands ( Player player, String current,
			String available ) {
		String[] splitted = available.split( "\\|" );

		for ( int i = 0; i < splitted.length; i++ ) {

			if ( splitted[ i ].equalsIgnoreCase( current ) ) {
				return true;
			}

		}
		player.sendMessage( ChatColor.RED + "Nie znaleziono takiej komendy." );
		player.sendMessage( ChatColor.GREEN + "Czy chodzilo ci o: "
				+ ChatColor.BOLD + "/np "
				+ StringUtils.findClosestString( current, splitted ) );
		player.sendMessage( ChatColor.GREEN + "Wpisz " + ChatColor.AQUA
				+ "/np help" + ChatColor.GREEN + ", aby uzyskac pomoc." );
		return false;

	}

	public String findClosestArenaName ( String invalid ) {
		String[] names = new String[ arenas.size( ) ];
		for ( int i = 0; i < arenas.size( ); i++ ) {
			names[ i ] = arenas.get( i ).getName( );
		}

		return StringUtils.findClosestString( "invalid", names );
	}

	@EventHandler
	public void onPlayerInteract ( PlayerInteractEvent event ) {
		if ( ( event.getAction( ) == Action.RIGHT_CLICK_BLOCK || event
				.getAction( ) == Action.RIGHT_CLICK_AIR )
				&& event.getPlayer( ).getItemInHand( ).getType( ) == Material.EMERALD ) {
			if ( event.getPlayer( ).getLocation( ).getBlock( )
					.getRelative( BlockFace.DOWN ).getType( ) == Material.EMERALD_BLOCK ) {
				event.getPlayer( ).sendMessage(
						"Kliknales prawym na emeralada." );
			} else {
				event.getPlayer( )
						.sendMessage(
								ChatColor.RED
										+ "Aby zapisac checkpoint musisz stac na emeraldzie." );

			}
		}

	}

	public void createArena ( Player player, String name ) {

		if ( findArena( name ) != null ) {
			player.sendMessage( ChatColor.RED
					+ "Arena o tej nazwie juz istnieje" );
		} else {
			//PrintWriter writer = null;

			ParkourArena arenaToCreate = new ParkourArena( this );
			arenaToCreate.setSpawnLocation( player.getLocation( ) );
			arenaToCreate.setName( name );
			arenas.add( arenaToCreate );
			saveArenas( );
			player.sendMessage( ChatColor.GREEN
					+ "Utworzono aren�. Aby j� aktywowa� nale�y wpisa�: "
					+ ChatColor.DARK_GREEN + "/npa setactive " + name + " true" );

		}

	}

	public void listArenas ( Player player ) {
		player.sendMessage( ChatColor.DARK_AQUA + "===" + ChatColor.DARK_RED
				+ "LISTA AREN " + ChatColor.AQUA + "NEXTparkour"
				+ ChatColor.GRAY + " by " + ChatColor.AQUA + "alufers "
				+ ChatColor.GOLD + "v1.0" + ChatColor.DARK_AQUA + "===" );

		for ( int i = 0; i < arenas.size( ); i++ ) {
			if ( arenas.get( i ).isActive( ) ) {

				player.sendMessage( ChatColor.YELLOW
						+ arenas.get( i ).getName( ) );
			} else {
				player.sendMessage( ChatColor.RED + "[Nieaktywna]"
						+ ChatColor.DARK_RED + arenas.get( i ).getName( ) );

			}

		}
	}

	public void joinArena ( Player player, String name ) {
		ParkourArena foundArena = findArena( name );
		ParkourArena foundArena1 = findPlayer( player );
		if ( foundArena != null ) {
			if ( foundArena1 == null ) {
				foundArena.addPlayer( player );
			} else {
				player.sendMessage( ChatColor.RED + "Jestes na innej arenie." );

			}
		} else {
			player.sendMessage( ChatColor.RED + "Arena nie istnieje." );
			player.sendMessage( ChatColor.GREEN + "Czy chodzilo ci o: "
					+ ChatColor.BOLD + findClosestArenaName( "name" )
					+ ChatColor.GREEN + "?" );
		}
	}

	public void leaveArena ( Player player ) {
		ParkourArena foundArena = findPlayer( player );
		if ( foundArena != null ) {
			foundArena.removePlayer( player );
		} else {
			player.sendMessage( ChatColor.RED + "Nie jestes na zadnej arenie." );
		}

	}

	public void deleteArena ( Player player, String name ) {
		if ( findArena( name ) != null ) {
			arenas.remove( findArena( name ) );
			saveArenas( );
			player.sendMessage( ChatColor.GREEN + "Usunieto." );

		} else {
			player.sendMessage( ChatColor.RED + "Arena nie istnieje." );
			player.sendMessage( ChatColor.GREEN + "Czy chodzilo ci o: "
					+ ChatColor.BOLD + findClosestArenaName( "name" )
					+ ChatColor.GREEN + "?" );
		}

	}

	public void setLobby ( Player player ) {
		getConfig( ).set( "config.lobbySpawnLocation.world",
				player.getLocation( ).getWorld( ).getName( ) );
		getConfig( ).set( "config.lobbySpawnLocation.x",
				player.getLocation( ).getX( ) );
		getConfig( ).set( "config.lobbySpawnLocation.y",
				player.getLocation( ).getY( ) );
		getConfig( ).set( "config.lobbySpawnLocation.z",
				player.getLocation( ).getZ( ) );
		saveConfig( );
		loadConfiguration( );
		player.sendMessage( ChatColor.GREEN + "Lo0kalizacje lobby ustawiono." );

	}

	public boolean onCommand ( CommandSender sender, Command cmd, String label,
			String[] args ) {
		if ( cmd.getName( ).equalsIgnoreCase( "nextparkour" )
				|| cmd.getName( ).equalsIgnoreCase( "nparkour" )
				|| cmd.getName( ).equalsIgnoreCase( "np" ) ) {
			if ( !( sender instanceof Player ) ) {
				sender.sendMessage( ChatColor.RED
						+ "Ta komenda musi byc wykonana przez gracza." );
			} else {
				Player player = (Player) sender;
				if ( !player.hasPermission( "np.use" ) ) {
					sender.sendMessage( ChatColor.RED
							+ "Nie masz uprawnien do wykonania tej czynnosci." );
					return true;
				}
				if ( args.length > 0 ) {
					if ( !validateCommands( player, args[ 0 ],
							"help|join|leave|list" ) ) {

						return true;

					}
					if ( args[ 0 ].equalsIgnoreCase( "help" ) ) {
						if ( !player.hasPermission( "np.help" ) )

							showHelp( sender );
					} else if ( args[ 0 ].equalsIgnoreCase( "join" ) ) {
						if ( !player.hasPermission( "np.join" ) ) {
							sender.sendMessage( ChatColor.RED
									+ "Nie masz uprawnien do wykonania tej czynnosci." );
							return true;
						}
						if ( args.length == 2 ) {
							joinArena( player, args[ 1 ] );
						} else {
							sender.sendMessage( ChatColor.RED
									+ "Za ma�o lub zbyt du�o argument�w." );

						}

					} else if ( args[ 0 ].equalsIgnoreCase( "leave" ) ) {
						if ( !player.hasPermission( "np.join" ) ) {
							sender.sendMessage( ChatColor.RED
									+ "Nie masz uprawnien do wykonania tej czynnosci." );
							return true;
						}
						if ( args.length == 1 ) {

							leaveArena( player );
						} else {
							sender.sendMessage( ChatColor.RED
									+ "Za ma�o lub zbyt du�o argument�w." );

						}

					} else if ( args[ 0 ].equalsIgnoreCase( "list" ) ) {
						if ( !player.hasPermission( "np.list" ) ) {
							sender.sendMessage( ChatColor.RED
									+ "Nie masz uprawnien do wykonania tej czynnosci." );
							return true;
						}
						if ( args.length == 1 ) {

							listArenas( player );
						} else {
							sender.sendMessage( ChatColor.RED
									+ "Za ma�o lub zbyt du�o argument�w." );

						}
					}

				} else {
					// help
					showHelp( sender );
				}
			}
			return true;
		}

		// START ADMIN
		if ( cmd.getName( ).equalsIgnoreCase( "nextparkouradmin" )
				|| cmd.getName( ).equalsIgnoreCase( "nparkouradmin" )
				|| cmd.getName( ).equalsIgnoreCase( "npa" ) ) {

			if ( !( sender instanceof Player ) ) {
				sender.sendMessage( ChatColor.RED
						+ "Ta komenda musi byc wykonana przez gracza." );
			} else {

				Player player = (Player) sender;
				if ( !player.hasPermission( "np.admin" ) ) {
					sender.sendMessage( ChatColor.RED
							+ "Nie masz uprawnien do wykonania tej czynnosci." );
					return true;
				}
				if ( args.length > 0 ) {
					// subcommands
					if ( args[ 0 ].equalsIgnoreCase( "help" ) ) {
						showAdminHelp( sender );
					} else if ( args[ 0 ].equalsIgnoreCase( "create" ) ) {
						if ( !player.hasPermission( "np.admin.create" ) ) {
							sender.sendMessage( ChatColor.RED
									+ "Nie masz uprawnien do wykonania tej czynnosci." );
							return true;
						}
						if ( args.length == 2 ) {

							createArena( player, args[ 1 ] );

						} else {
							sender.sendMessage( ChatColor.RED
									+ "Za ma�o lub zbyt du�o argument�w." );

						}

					} else if ( args[ 0 ].equalsIgnoreCase( "delete" ) ) {
						if ( !player.hasPermission( "np.admin.delete" ) ) {
							sender.sendMessage( ChatColor.RED
									+ "Nie masz uprawnien do wykonania tej czynnosci." );
							return true;
						}
						if ( args.length == 2 ) {

							deleteArena( player, args[ 1 ] );

						} else {
							sender.sendMessage( ChatColor.RED
									+ "Za ma�o lub zbyt du�o argument�w." );

						}

					} else if ( args[ 0 ].equalsIgnoreCase( "setlobby" ) ) {
						if ( !player.hasPermission( "np.admin.setlobby" ) ) {
							sender.sendMessage( ChatColor.RED
									+ "Nie masz uprawnien do wykonania tej czynnosci." );
							return true;
						}
						if ( args.length == 1 ) {
							setLobby( player );
						} else {
							sender.sendMessage( ChatColor.RED
									+ "Za ma�o lub zbyt du�o argument�w." );

						}

					} else if ( args[ 0 ].equalsIgnoreCase( "clear" ) ) {
						if ( !player.hasPermission( "np.admin.clear" ) ) {
							sender.sendMessage( ChatColor.RED
									+ "Nie masz uprawnien do wykonania tej czynnosci." );
							return true;
						}
						if ( args.length == 1 ) {
							File file = new File( "arenas.dat" );
							file.delete( );
						} else {
							sender.sendMessage( ChatColor.RED
									+ "Za ma�o lub zbyt du�o argument�w." );

						}

					} else if ( args[ 0 ].equalsIgnoreCase( "setactive" ) ) {
						if ( !player.hasPermission( "np.admin.setactive" ) ) {
							sender.sendMessage( ChatColor.RED
									+ "Nie masz uprawnien do wykonania tej czynnosci." );
							return true;
						}
						if ( args.length == 3 ) {

							if ( args[ 2 ].equals( "true" )
									|| args[ 2 ].equals( "false" ) ) {
								ParkourArena arena = findArena( args[ 1 ] );
								if ( arena != null ) {
									arena.setActive( Boolean
											.parseBoolean( args[ 2 ] ) );
									saveArenas( );
									sender.sendMessage( ChatColor.GREEN
											+ "Gotowe." );
								} else {
									sender.sendMessage( ChatColor.RED
											+ "Arena nie istnieje." );
									player.sendMessage( ChatColor.GREEN
											+ "Czy chodzilo ci o: "
											+ ChatColor.BOLD
											+ findClosestArenaName( "name" )
											+ ChatColor.GREEN + "?" );
								}
							} else {
								sender.sendMessage( ChatColor.RED
										+ "Wartosc musi byc " + ChatColor.GREEN
										+ "true" + ChatColor.RED + " albo "
										+ ChatColor.GREEN + "false" );

							}
						} else {
							sender.sendMessage( ChatColor.RED
									+ "Za ma�o lub zbyt du�o argument�w." );

						}

					}

				} else {
					// help
					showAdminHelp( sender );
				}
			}
			return true;
		}
		return false;
	}

	public ParkourArena findArena ( String arenaName ) {
		for ( int i = 0; i < arenas.size( ); i++ ) {
			if ( arenas.get( i ).getName( ).equalsIgnoreCase( arenaName ) ) {
				return arenas.get( i );
			}
		}
		return null;
	}

	public ParkourArena findPlayer ( Player player ) {
		for ( int i = 0; i < arenas.size( ); i++ ) {

			if ( arenas.get( i ).containsPlayer( player ) ) {

				return arenas.get( i );
			}

		}
		return null;
	}

	public void loadConfiguration ( ) {
		getConfig( ).addDefault( "config.lobbySpawnLocation.world", "world" );
		getConfig( ).addDefault( "config.lobbySpawnLocation.x", "0" );
		getConfig( ).addDefault( "config.lobbySpawnLocation.y", "0" );
		getConfig( ).addDefault( "config.lobbySpawnLocation.z", "0" );

		getConfig( ).options( ).copyDefaults( true );
		saveConfig( );

	}

	@EventHandler ( priority = EventPriority.NORMAL )
	public void onPlayerMoveEvent ( PlayerMoveEvent event ) { // YAY
																// ////////////////////////////////

		Player player = event.getPlayer( );

		if ( player.getLocation( ).getBlock( ).getRelative( BlockFace.DOWN )
				.getType( ) == Material.DIAMOND_BLOCK ) {

			ParkourArena tmp = findPlayer( player );
			if ( tmp != null ) {
				tmp.endReached( player );
			}
		} else if ( player.getLocation( ).getBlock( )
				.getRelative( BlockFace.DOWN ).getType( ) == Material.GOLD_BLOCK ) {

			ParkourArena tmp = findPlayer( player );
			if ( tmp != null ) {
				tmp.registerCheckpoint( player );
			}
		}
	}

	public Location getLobbySpawnLocation ( ) {
		World w = Bukkit.getServer( ).getWorld(
				getConfig( ).getString( "config.lobbySpawnLocation.world" ) );
		Location loc = new Location( w, getConfig( ).getInt(
				"config.lobbySpawnLocation.x" ), getConfig( ).getInt(
				"config.lobbySpawnLocation.y" ), getConfig( ).getInt(
				"config.lobbySpawnLocation.z" ) );

		return loc;
	}

	public void showHelp ( CommandSender player ) {
		player.sendMessage( ChatColor.DARK_AQUA + "===" + ChatColor.AQUA
				+ "NEXTparkour" + ChatColor.GRAY + " by " + ChatColor.AQUA
				+ "alufers " + ChatColor.GOLD + getDescription( ).getVersion( )
				+ ChatColor.DARK_AQUA + "===" );
		player.sendMessage( ChatColor.DARK_GREEN + "/np join [nazwaAreny] "
				+ ChatColor.GREEN + "Wchodzisz na aren�" );
		player.sendMessage( ChatColor.DARK_GREEN + "/np leave             "
				+ ChatColor.GREEN + "Opuszczasz aren�" );
		player.sendMessage( ChatColor.DARK_GREEN + "/np list              "
				+ ChatColor.GREEN + "Pokazujesz dost�pne areny" );

	}

	public void showAdminHelp ( CommandSender player ) {
		player.sendMessage( ChatColor.DARK_AQUA + "===" + ChatColor.AQUA
				+ "NEXTparkour" + ChatColor.GRAY + " by " + ChatColor.AQUA
				+ "alufers " + ChatColor.GOLD + getDescription( ).getVersion( )
				+ ChatColor.YELLOW + "[MODU� ADMINISTRACYJNY]"
				+ ChatColor.DARK_AQUA + "===" );
		player.sendMessage( ChatColor.DARK_GREEN + "/npa create [nazwaAreny] "
				+ ChatColor.GREEN + "Tworzysz aren�" );
		player.sendMessage( ChatColor.DARK_GREEN + "/npa delete [nazwaAreny] "
				+ ChatColor.GREEN + "Usuwasz aren�" );
		player.sendMessage( ChatColor.DARK_GREEN
				+ "/npa kick <nazwaAreny>                                   "
				+ ChatColor.GREEN
				+ "Wyrzucasz wszystkich graczy. Niepodanie argumentu powoduje wyrzucenie z wszystkich aren." );
		player.sendMessage( ChatColor.DARK_GREEN
				+ "/npa setlobby                                            "
				+ ChatColor.GREEN
				+ "Ustawia lobby w miejscu, w kt�rym stoisz." );
		player.sendMessage( ChatColor.DARK_GREEN
				+ "/npa setactive <nazwaAreny> <true|false>                                            "
				+ ChatColor.GREEN
				+ "Ustawia lobby w miejscu, w kt�rym stoisz." );

	}

	public void loadArenas ( ) {

		File f = new File( "arenas.dat" );
		if ( !f.exists( ) )
			saveArenas( );

		FileInputStream fin = null;
		try {
			fin = new FileInputStream( "arenas.dat" );
		} catch ( FileNotFoundException e ) {
			e.printStackTrace( );
		}
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream( fin );
		} catch ( IOException e ) {
			e.printStackTrace( );
		}
		try {
			@SuppressWarnings ( "unchecked" )
			HashMap< Integer, ParkourArena > hm = (HashMap< Integer, ParkourArena >) ois
					.readObject( );
			arenas = new ArrayList< ParkourArena >( );
			Iterator< Entry< Integer, ParkourArena >> it = hm.entrySet( )
					.iterator( );
			while ( it.hasNext( ) ) {
				Map.Entry< Integer, ParkourArena > pairs = (Map.Entry< Integer, ParkourArena >) it
						.next( );
				pairs.getValue( ).resetPlayers( );
				pairs.getValue( ).setPlugin( this );
				arenas.add( (ParkourArena) pairs.getValue( ) );

				it.remove( );
			}
		} catch ( ClassNotFoundException e ) {
			e.printStackTrace( );
		} catch ( IOException e ) {
			e.printStackTrace( );
		}
		try {
			fin.close( );
		} catch ( IOException e ) {
			e.printStackTrace( );
		}
	}

	public void saveArenas ( ) {
		getLogger( ).info( "Saving arenas! " + arenas.size( ) );
		HashMap< Integer, ParkourArena > hm = new HashMap< Integer, ParkourArena >( );
		for ( int i = 0; i < arenas.size( ); i++ ) {
			hm.put( i, arenas.get( i ) );
		}
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream( "arenas.dat" );
		} catch ( FileNotFoundException e1 ) {
			e1.printStackTrace( );
		}
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream( fout );
		} catch ( IOException e ) {
			e.printStackTrace( );
		}
		try {
			oos.writeObject( hm );
		} catch ( IOException e ) {
			e.printStackTrace( );
		}
		try {
			fout.close( );
		} catch ( IOException e ) {
			e.printStackTrace( );
		}

	}

}
