package com.gmail.alikstudio47;

import static org.bukkit.Material.EMERALD;
import static org.bukkit.Material.EMERALD_BLOCK;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerEventListeners implements Listener {
	private NEXTparkour plugin;

	public PlayerEventListeners(NEXTparkour plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		Material mat = player.getLocation().getBlock()
				.getRelative(BlockFace.DOWN).getType();
		Material item = player.getItemInHand().getType();

		if ((action == RIGHT_CLICK_BLOCK || action == RIGHT_CLICK_AIR)
				&& item == EMERALD) {
			if (mat == EMERALD_BLOCK) {
				player.sendMessage("Kliknales prawym na emeralada.");
			} else {
				player.sendMessage(ChatColor.RED
						+ "Aby zapisac checkpoint musisz stac na emeraldzie.");
			}
		}

		if ((action == RIGHT_CLICK_BLOCK || action == RIGHT_CLICK_AIR)
				&& item == Material.REDSTONE) {
			player.sendMessage(ChatColor.RED + "Restartuje arene!");
			plugin.findPlayer(player).restart(player);
		}
	}

	

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Material matDown = event.getPlayer().getLocation().getBlock()
				.getRelative(BlockFace.DOWN).getType();
		/*Material mat = event.getPlayer().getLocation().getBlock().getType();*/
		if (matDown == Material.DIAMOND_BLOCK || matDown == Material.GOLD_BLOCK) {
			ParkourArena tmp = plugin.findPlayer(player);

			if (tmp != null) {

				if (matDown == Material.DIAMOND_BLOCK) {
					tmp.endReached(player.getPlayerListName());
				} 
				
				if (matDown == Material.NETHERRACK) {
					player.sendMessage(ChatColor.RED + "Game over!");
					tmp.restart(player);
				}

			}
		}
	}
}