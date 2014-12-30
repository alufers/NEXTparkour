package com.gmail.alikstudio47;

import java.io.Serializable;

public class ParkourScore implements Serializable {
	private static final long serialVersionUID = 1L;
	public float time;
	public String playerName;
	public int timesPlayed = 0;

	public ParkourScore( String playerName, float time ) {
		this.playerName = playerName;
		this.time = time;

	}

	// Po co robiles gettery i settery skoro nie maja zadnych mechanizmow ? (np. System.out.println(playerName);)

	/*
	 * public String getPlayerName ( ) { return playerName; } public
	 * void setPlayerName ( String playerName ) { this.playerName =
	 * playerName; }
	 */

	/*
	 * public float getTime ( ) { return time; } public void setTime (
	 * float time ) { this.time = time; }
	 */

	/*
	 * public int getTimesPlayed ( ) { return timesPlayed; } public
	 * void setTimesPlayed ( int timesPlayed ) { this.timesPlayed =
	 * timesPlayed; }
	 */
}
