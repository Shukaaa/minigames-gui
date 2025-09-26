package rip.shuka.minigamesGui.game

import org.bukkit.entity.Player

data class GamePlayer(
	val player: Player,
	val inventoryHolder: GameInventory
)
