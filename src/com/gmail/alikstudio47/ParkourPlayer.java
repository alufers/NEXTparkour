package com.gmail.alikstudio47;

import org.bukkit.entity.Player;

public class ParkourPlayer {
	private Player player;
	// private ParkourCheckpoint lastCheckpoint = null;
	private int Deaths = 0;
	private long startTime;

	// private int checkpointsFound = 0;

	public long getStartTime ( ) {
		return startTime;
	}

	public void setStartTime ( long startTime ) {
		this.startTime = startTime;
	}

	public Player getPlayer ( ) {
		return player;
	}

	public void setPlayer ( Player player ) {
		this.player = player;
	}

	// public ParkourCheckpoint getLastCheckpoint() {
	// return lastCheckpoint;
	// }

	// public void setLastCheckpoint(ParkourCheckpoint lastCheckpoint) {
	// this.lastCheckpoint = lastCheckpoint;
	// }

	public int getDeaths ( ) {
		return Deaths;
	}

	public void setDeaths ( int deaths ) {
		Deaths = deaths;
	}

	/*
	 * public int getCheckpointsFound() { return checkpointsFound; }
	 * public void setCheckpointsFound(int checkpointsFound) {
	 * this.checkpointsFound = checkpointsFound; }
	 */
}
