package rip.shuka.minigamesGui.game

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import rip.shuka.minigamesGui.message.MessageSender
import rip.shuka.minigamesGui.message.MessageStatus
import rip.shuka.minigamesGui.utils.SoundUtil
import kotlin.collections.forEach
import kotlin.collections.toTypedArray

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

	/** Initialize game-specific settings or states here */
	abstract fun initialize()

	/**
	 * Standardized event receiver for the game.
	 * This method gets called from the GameInventory when a player interacts with the inventory.
	 */
	abstract fun receiveEvent(eventId: String, vararg data: Any)

	/**
	* Helper function to receive a standardized quit event from the inventory.
	* This gets automatically called from the GameInventory when a player clicks the quit button (when implemented)
	* Override this method to handle quit events in your game logic
	*/
	fun receiveQuitEvent(quittingPlayer: Player) {
		SoundUtil.playQuitSound(*this.player.toTypedArray())
		this.player.forEach { p ->
			p.player.closeInventory()
			if (p.player != quittingPlayer) {
				MessageSender.send("${quittingPlayer.name} has quit the game.", p.player, MessageStatus.FAILURE)
			} else {
				MessageSender.send("You have quit the game.", p.player, MessageStatus.NEUTRAL)
			}
		}
	}
}