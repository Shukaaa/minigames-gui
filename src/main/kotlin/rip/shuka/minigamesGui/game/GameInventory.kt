package rip.shuka.minigamesGui.game

import net.kyori.adventure.text.Component
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

abstract class GameInventory : InventoryHolder {
	private lateinit var inventory: Inventory
	lateinit var game: Game
	var title: Component = Component.text("Untitled")

	fun init(game: Game, title: Component) {
		this.game = game
		this.title = title

		this.initialize()
	}

	override fun getInventory(): Inventory {
		return inventory
	}

	protected fun setInventory(inv: Inventory) {
		this.inventory = inv
	}

	/*
	* Initialize the inventory with the game instance here
	*/
	abstract fun initialize()

	/*
	* Handle click events in the inventory here
	*/
	abstract fun onClick(originalEvent: InventoryClickEvent)
}