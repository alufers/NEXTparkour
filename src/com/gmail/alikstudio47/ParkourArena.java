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
	private transient List< ParkourPlayer > players = new ArrayList< ParkourPlayer >( );
	private transient NEXTparkour plugin;
	private boolean isActive;
	private double x, y, z;
	private String world;
	private List< ParkourScore > scores = new ArrayList< ParkourScore >( );

	public void setSpawnLocation ( Location loc ) {
		x = loc.getX( );
		y = loc.getY( );
		z = loc.getZ( );
		world = loc.getWorld( ).getName( );
	}

	public void resetPlayers ( ) {
		players = new ArrayList< ParkourPlayer >( );
	}

	public Location getSpawnLocation ( ) {
		World w = Bukkit.getWorld( world );
		if ( w == null )
			return null;
		Location toRet = new Location( w, x, y, z );
		return toRet;
	}

	public boolean isActive ( ) {
		return isActive;
	}

	public void setActive ( boolean isActive ) {
		this.isActive = isActive;
	}

	public ParkourArena( NEXTparkour plugin ) {
		this.plugin = plugin;
	}

	public void setPlugin ( NEXTparkour plugin ) {
		this.plugin = plugin;
	}

	public String getName ( ) {
		return name;
	}

	public void setName ( String name ) {
		this.name = name;
	}

	private String name;

	public void addPlayer ( Player player ) {
		if ( !isActive( ) ) {
			player.sendMessage( RED + "Mapa nieaktywna lub w budowie!" );
			return;
		}
		if ( players.contains( player ) )
			player.sendMessage( RED + "Juz grasz na tej mapie!" );
		else {
			ParkourPlayer tmp = new ParkourPlayer( );
			tmp.player = player;
			tmp.startTime = System.currentTimeMillis( );
			players.add( tmp );
			player.sendMessage( BLUE + "Witaj na mapie " + getName( ) );

			if ( getScoreOf( player ) != null ) {
				player.sendMessage( GREEN + "Grasz juz "
						+ getScoreOf( player ).timesPlayed + " raz "
						+ ", a twoj najlepszy czas to "
						+ getScoreOf( player ).time + "." );
			} else {
				player.sendMessage( BLUE
						+ "Jeszcze nigdy nie doszedles do konca tej mapy. Powodzenia." );
			}

			player.teleport( getSpawnLocation( ) );
		}

	}

	public void removePlayer ( Player player ) {
		players.remove( player );

		player.sendMessage( BLUE + "Dzieki za granie na " + getName( ) );
		player.teleport( plugin.getLobbySpawnLocation( ) );
	}

	public Boolean containsPlayer ( Player player ) {
		return getScoreOf( player ) == null ? false : true;
	}

	public ParkourScore getScoreOf ( Player player ) {
		return getScoreOf( player.getPlayerListName( ) );
	}

	public ParkourScore getScoreOf ( String player ) {
		for ( ParkourScore s : scores ) {
			if ( s.playerName.equalsIgnoreCase( player ) )
				return s;
		}

		return null;
	}

	public void endReached ( Player player ) {
		endReached( player.getPlayerListName( ) );
	}

	public void endReached ( String _player ) {
		ParkourPlayer player = players.get( players.indexOf( _player ) );

		if ( player != null ) {
			player.player.sendMessage( GOLD + "Dzieki za granie na "
					+ getName( ) );

			float time = (float) ( ( System.currentTimeMillis( ) - player.startTime ) / 1000 );

			if ( getScoreOf( _player ) != null ) {
				getScoreOf( _player ).time = time;
				getScoreOf( _player ).timesPlayed += 1;
			} else {
				scores.add( new ParkourScore( _player, time ) );
			}

			player.player.sendMessage( YELLOW + "Twoj czas: " + time );

			players.remove( player ); // gracz musi byc usuniety bo bugi :D

			player.player.teleport( plugin.getLobbySpawnLocation( ) );
		}

	}

	public void registerCheckpoint ( Player player ) {
		for ( int i = 0; i < players.size( ); i++ ) {
			if ( players.get( i ).player.equals( player ) ) {
				player.sendMessage( AQUA + "Zdobyles checkpoint!" );
				// players.get(i).setLastCheckpoint(
				// new ParkourCheckpoint(player.getLocation()));
			}

		}

	}

}
