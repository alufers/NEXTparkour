package com.gmail.alikstudio47;

import java.io.Serializable;

public class ParkourScore implements Serializable {
	private static final long serialVersionUID = 1L;
	private float time;

	private String playerName;

	private int timesPlayed = 0;

	public ParkourScore( String playerName, float time ) {
		this.playerName = playerName;
		this.time = time;

	}

	public String getPlayerName ( ) {
		return playerName;
	}

	public void setPlayerName ( String playerName ) {
		this.playerName = playerName;
	}

	public float getTime ( ) {
		return time;
	}

	public void setTime ( float time ) {
		this.time = time;
	}

	public int getTimesPlayed ( ) {
		return timesPlayed;
	}

	public void setTimesPlayed ( int timesPlayed ) {
		this.timesPlayed = timesPlayed;
	}
}
