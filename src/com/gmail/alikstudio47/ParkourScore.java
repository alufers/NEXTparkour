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
}