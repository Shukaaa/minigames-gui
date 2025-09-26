package rip.shuka.minigamesGui.game

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit.createInventory
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

abstract class GameInventory : InventoryHolder {
	private lateinit var inventory: Inventory
	protected lateinit var game: Game
	var title: Component = Component.text("Untitled")

	var rows: Int = 0

	constructor(rows: Int) {
		this.rows = rows
	}

	fun init(game: Game, title: Component) {
		this.game = game
		this.title = title

		this.inventory = createInventory(this, this.rows, this.title)
		this.initialize()
	}

	override fun getInventory(): Inventory {
		return inventory
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