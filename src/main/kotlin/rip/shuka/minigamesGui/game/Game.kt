package rip.shuka.minigamesGui.game

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

abstract class Game {
	/* Unique identifier for the game */
	abstract val key: String
	abstract val name: String
	abstract val title: Component
	abstract val description: String

	var player: List<GamePlayer> = ArrayList()
	var inventoryFactory: (() -> GameInventory)

	constructor(inventoryFactory: (() -> GameInventory)) {
		this.inventoryFactory = inventoryFactory
	}

	fun init(player: List<Player>) {
		val gamePlayers = ArrayList<GamePlayer>()
		for (p in player) {
			val inventory = this.inventoryFactory()
			inventory.init(this, this.title)
			gamePlayers.add(GamePlayer(p, inventory))
		}
		this.player = gamePlayers

		this.initialize()
	}

	/* Initialize game-specific settings or states here */
	abstract fun initialize()
	abstract fun receiveEvent(eventId: String, vararg data: Any)
}