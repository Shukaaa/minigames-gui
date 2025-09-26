package rip.shuka.minigamesGui.game

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit.createInventory
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import rip.shuka.minigamesGui.utils.ItemStacksUtil

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

		this.inventory = createInventory(this, this.rows * 9, this.title)
		this.initialize()
	}

	override fun getInventory(): Inventory {
		return inventory
	}

	/**
	* Helper function to send a standardized quit event to the game instance
	*/
	protected fun sendQuitEvent(player: Player) {
		game.receiveQuitEvent(player)
	}

	/**
	* Helper function to create a basic quit item stack to keep consistency across games
	*/
	protected fun setQuitItem(index: Int) {
		inventory.setItem(index, ItemStacksUtil.createBasicItemStack(Material.CHERRY_SIGN, "Quit", NamedTextColor.RED))
	}

	/**
	* Initialize the inventory with the game instance here
	*/
	abstract fun initialize()

	/**
	* Handle click events in the inventory here
	*/
	abstract fun onClick(originalEvent: InventoryClickEvent)
}