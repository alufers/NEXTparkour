package com.gmail.alikstudio47;

import java.io.Serializable;

import org.bukkit.Location;

public class ParkourCheckpoint implements Serializable {
	private static final long serialVersionUID = 1L;
	private Location location;

	public ParkourCheckpoint( Location location ) {
		this.location = location;

	}

	public Location getLocation ( ) {
		return location;
	}

	public void setLocation ( Location location ) {
		this.location = location;
	}

}
